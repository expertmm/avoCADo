package backend.model.CSG;

import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;


//
//Copyright (C) 2007 avoCADo (Adam Kumpf creator)
//This code is distributed under the terms of the 
//GNU General Public License (GPL).
//
//This file is part of avoADo.
//
//AvoCADo is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//AvoCADo is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with AvoCADo; if not, write to the Free Software
//Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//

/*
* @author  Adam Kumpf
* @created Mar. 2007
*/

/**
 * Constructive Solid Geometry :: Segment<br/><br/>
 * 
 * A line segment in 3D space.<br/><br/> 
 * 
 * The CSG_Segment also stores descriptor information 
 * about the starting, middle, and ending points. 
 * Additionally, neighboring vertices and distance 
 * information along a plane intersection line is also 
 * kept. <br/><br/>
 * 
 * Algorithms and structures from:<br/>
 * - Laidlaw, Trumbore, and Hughes <br/>
 * - "Constructive Solid Geometry for Polyhedral Objects"<br/>
 * - SIGGRAPH 1986, Volume 20, Number 4, pp.161-170 * 
 */
public class CSG_Segment {

	// Data structure from Fig. 5.1
    private double distStartFromP;
    private double distEndFromP;	
	private CSG_Ray ray;
	private CSG_Vertex startVert;
	private CSG_Vertex endVert;
	private enum VERTEX_DESC { VERTEX, EDGE, FACE, UNKNOWN };
	private VERTEX_DESC descStart = VERTEX_DESC.UNKNOWN;
	private VERTEX_DESC descMid   = VERTEX_DESC.UNKNOWN;
	private VERTEX_DESC descEnd   = VERTEX_DESC.UNKNOWN;
	private int vertNearStartPt = 0;
	private int vertNearEndPt   = 0;
	
	
	public CSG_Segment(CSG_Face face, List<Double> zDists, CSG_Ray ray){
		this.ray = ray;
		if(face.getNumberVertices() != zDists.size()){
			System.out.println("** CSG_Segment(constructor): OMG, d00d the number of zDists doesn't match your face.");
			distStartFromP = 0.0;
			distEndFromP = 1.0;
			startVert = ray.basePoint;
			endVert = startVert.addToVertex(ray.direction);
			return;			
		}
		Iterator<CSG_Vertex> fIter = face.getVertexIterator();
		Iterator<Double> dIter = zDists.iterator();
		double dist = 0.0;
		boolean lastDistWasPos = false;
		boolean lastDistWasNeg = false;
		CSG_Vertex lastVert = null;
		double lastDist = 0.0;
		//
		// check each vertex and pairs of vertices to find segment ends.
		//
		int vertIndex = 0;
		while(fIter.hasNext() && endVert == null){
			dist = dIter.next();
			CSG_Vertex vert = fIter.next();
			
			if(dist == 0.0){
				// vertex intersects, add it
				includeVertex(vert, VERTEX_DESC.VERTEX, vertIndex);
			}else{
				if(dist > 0.0){
					if(lastDistWasNeg && lastVert != null){
						// do line intersection (ratio of zDists)
						double alpha = Math.abs(lastDist)/(Math.abs(lastDist)+Math.abs(dist));
						CSG_Vertex tempV = vert.getScaledCopy(alpha);
						tempV = tempV.addToVertex(lastVert.getScaledCopy(1.0-alpha));
						includeVertex(tempV, VERTEX_DESC.EDGE, vertIndex);
					}
					lastDistWasNeg = false;
					lastDistWasPos = true;
				}else{
					if(lastDistWasPos && lastVert != null){
						// do line intersection (ratio of zDists)
						double alpha = Math.abs(lastDist)/(Math.abs(lastDist)+Math.abs(dist));
						CSG_Vertex tempV = vert.getScaledCopy(alpha);
						tempV = tempV.addToVertex(lastVert.getScaledCopy(1.0-alpha));
						includeVertex(tempV, VERTEX_DESC.EDGE, vertIndex);
					}
					lastDistWasPos = false;
					lastDistWasNeg = true;
				}
			}
			lastVert = vert;
			lastDist = dist;
			vertIndex++;
		}
		
		//
		// check last (wrap-around) case (last to first vertices)
		//
		fIter = face.getVertexIterator();
		dIter = zDists.iterator();
		vertIndex--;
		if(fIter.hasNext() && endVert == null){
			dist = dIter.next();
			CSG_Vertex vert = fIter.next();
			if(dist == 0.0){
				// vertex intersects, add it
				includeVertex(vert, VERTEX_DESC.VERTEX, vertIndex);
			}else{
				if(dist > 0.0){
					if(lastDistWasNeg && lastVert != null){
						// do line intersection (ratio of zDists)
						double alpha = Math.abs(lastDist)/(Math.abs(lastDist)+Math.abs(dist));
						CSG_Vertex tempV = vert.getScaledCopy(alpha);
						tempV = tempV.addToVertex(lastVert.getScaledCopy(1.0-alpha));
						includeVertex(tempV, VERTEX_DESC.EDGE, vertIndex);
					}
					lastDistWasNeg = false;
					lastDistWasPos = true;
				}else{
					if(lastDistWasPos && lastVert != null){
						// do line intersection (ratio of zDists)
						double alpha = Math.abs(lastDist)/(Math.abs(lastDist)+Math.abs(dist));
						CSG_Vertex tempV = vert.getScaledCopy(alpha);
						tempV = tempV.addToVertex(lastVert.getScaledCopy(1.0-alpha));
						includeVertex(tempV, VERTEX_DESC.EDGE, vertIndex);
					}
					lastDistWasPos = false;
					lastDistWasNeg = true;
				}
			}			
		}
		
		if(startVert != null && endVert == null){
			// Fig 5.2, for vertex,vertex,vertex case
			includeVertex(endVert, VERTEX_DESC.VERTEX, vertNearStartPt);			
		}
		
		// TODO make sure both vertA and vertB are not null!
		if(startVert == null || endVert == null){
			System.out.println("*** null verts: start=" + startVert + " and end=" + endVert);
			startVert = new CSG_Vertex(0,0,0);
			endVert = new CSG_Vertex(0,0,0);
		}else{
			//System.out.println("got a good pair of verts! :)");
		}
		// startVert and endVert now contain the endpoints!
		this.distStartFromP = this.ray.basePoint.getDistBetweenVertices(startVert);
		this.distEndFromP   = this.ray.basePoint.getDistBetweenVertices(endVert);
		
		// update the middle vertex desciption
		if(vertNearStartPt == vertNearEndPt){
			descMid = VERTEX_DESC.VERTEX;
		}else{
			if(descStart == VERTEX_DESC.VERTEX && descStart == VERTEX_DESC.VERTEX && 
					(vertNearStartPt-vertNearEndPt == 1 ||	vertNearStartPt-vertNearEndPt == -1)){
				descMid = VERTEX_DESC.EDGE;
			}else{
				descMid = VERTEX_DESC.FACE;
			}
		}
		
	}
	

	
	/**
	 * if vertA == NULL, place vert in vertA; <br/>
	 * else vertB = vert;
	 * @param vert
	 * @param vertA
	 * @param vertB
	 */
	private void includeVertex(CSG_Vertex vert, VERTEX_DESC vertDesc, int neighborIndex){
		//System.out.println("adding vert...");
		if(startVert == null){
			startVert = vert;
			descStart = vertDesc;
			vertNearStartPt = neighborIndex;
		}else{
			endVert = vert;
			descEnd = vertDesc;
			vertNearEndPt = neighborIndex;
		}
	}
	
	public CSG_Segment(CSG_Vertex startVert, CSG_Vertex endVert){
		this.startVert = startVert;
		this.endVert   = endVert;
		this.ray = new CSG_Ray(startVert,endVert.subFromVertex(startVert));
		this.distStartFromP = 0.0;
		this.distEndFromP = endVert.getDistBetweenVertices(startVert);
	}
	
	
	public void drawSegmentForDebug(GL gl){
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3d(startVert.getX(), startVert.getY(), startVert.getZ());
			gl.glVertex3d(endVert.getX(), endVert.getY(), endVert.getZ());			
		gl.glEnd();
	}
	
	/**
	 * @return minimum distance along ray that segment ends.
	 */
	public double getMinRayDist(){
		return Math.min(distStartFromP, distEndFromP);
	}
	
	/**
	 * @return maximum distance along ray that segment ends.
	 */
	public double getMaxRayDist(){
		return Math.max(distStartFromP, distEndFromP);
	}
	
}