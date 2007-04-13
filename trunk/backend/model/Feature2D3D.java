package backend.model;

import ui.tools.ToolModelBuild;
import backend.adt.ParamSet;
import backend.model.CSG.CSG_Face;
import backend.model.sketch.SketchPlane;


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
* @created Feb. 2007
*/

/**
 * model componet that represents "Build" tools
 */
public class Feature2D3D implements SubPart{
	
	public    ParamSet          paramSet    = null;
	
	protected int primarySketchID;
	
	protected Part part;
	public final int ID;
	
	public Feature2D3D(Part part, int primarySketchID, int ID){
		this.part = part;
		this.primarySketchID = primarySketchID;
		this.ID = ID;
	}	
	
	public Part getParentPart(){
		return this.part;
	}
	
	public Sketch getPrimarySketch(){
		return part.getSketchByID(primarySketchID);
	}
	
	//TODO: handle how sketches are linked to the Feature2D3D (sketch by number?)
	
	public Feature2D3D getFeature2D3D() {
		return this;
	}

	public Feature3D3D getFeature3D3D() {
		return null;
	}

	public Sketch getSketch() {
		return null;
	}
	
	public SketchPlane getPlaneByFaceID(int uniqueFaceID){
		if(paramSet == null){
			// paramSet was null, can't find a plane
			System.out.println("Feature2D3D(getPlaneByFaceID): paramSet was NULL, can't return a sketch!");
			return null;
		}
		ToolModelBuild toolModelBuild = paramSet.getToolModel2D3D();
		if(toolModelBuild != null){
			// get sketchPlane by constructing one from the specified CSG_face
			CSG_Face face = toolModelBuild.getFaceByID(this, uniqueFaceID);
			return new SketchPlane(face.getPlane());
		}else{
			// toolModel was null, can't find a plane
			System.out.println("Feature2D3D(getPlaneByFaceID): Tool Model was NULL, can't return a sketch!");
			return null;
		}
	}
	
}
