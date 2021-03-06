package org.poikilos.librecsg.ui.tools.part;

import java.util.Iterator;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import org.poikilos.librecsg.ui.tools.ToolCtrlPart;
import org.poikilos.librecsg.backend.adt.ParamSet;
import org.poikilos.librecsg.backend.global.AvoGlobal;
import org.poikilos.librecsg.backend.model.Part;
import org.poikilos.librecsg.backend.model.CSG.CSG_Face;
import org.poikilos.librecsg.backend.model.CSG.CSG_Vertex;


//
//Copyright (C) 2007 avoCADo (Adam Kumpf creator)
//This code is distributed under the terms of the
//GNU General Public License (GPL).
//
//This file is part of avoCADo.
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
* @created Apr. 2007
*/

/**
 * default controller to use when in the "Part" menuet mode.
 * the basic functionality includes selecting of CSG_Faces
 * on which a new sketch can be started.
 */
public class ToolPartDefaultCtrl implements ToolCtrlPart{

	private final double MAX_DIST_FROM_FACE = 0.1;

	public void glMouseDown(double x, double y, double z, MouseEvent e, ParamSet paramSet) {
		//long startTime = System.nanoTime();
		Part part = AvoGlobal.project.getActivePart();
		if(part != null){
			CSG_Vertex clickedVert = new CSG_Vertex(x, y, z);
			Iterator<CSG_Face> faceIter = part.getSolid().getFacesIter();
			double closestDistSoFar = MAX_DIST_FROM_FACE;
			CSG_Face faceToSelect = null;
			while(faceIter.hasNext()){
				CSG_Face face = faceIter.next();
				face.setSelected(false);
				if(face.isSelectable() &&
						Math.abs(face.distFromVertexToFacePlane(clickedVert)) < closestDistSoFar && face.vertexIsInsideFace(clickedVert)){
						// a selectable face was clicked!
						faceToSelect = face;
						closestDistSoFar = Math.abs(face.distFromVertexToFacePlane(clickedVert));
				}else{
					face.setSelected(false);
				}
			}
			if(faceToSelect != null){
				System.out.println("You selected a selectable face! " + faceToSelect.getModRefPlane());
				faceToSelect.setSelected(true);
				part.setSelectedPlane(faceToSelect.getModRefPlane());
			}

			// TODO: this belong in constraint/mating code, not here...
			// -------- TEST
			faceIter = part.getSolid().getFacesIter();
			closestDistSoFar = MAX_DIST_FROM_FACE;
			CSG_Face faceWithArc = null;
			while(faceIter.hasNext()){
				CSG_Face face = faceIter.next();
				if(face.getModRefCylinder() != null &&
						Math.abs(face.distFromVertexToFacePlane(clickedVert)) < closestDistSoFar &&
						face.vertexIsInsideFace(clickedVert)){
					// a selectable face was clicked!
					System.out.println("*");
					faceWithArc = face;
					closestDistSoFar = Math.abs(face.distFromVertexToFacePlane(clickedVert));
				}
			}
			if(faceWithArc != null){
				System.out.println("You selected a selectable ARC face! " + faceWithArc.getModRefCylinder());
			}
			// -----

			AvoGlobal.glView.updateGLView = true;
		}
		//long endTime = System.nanoTime();
		//System.out.println("Time to search for clicked face: " + (endTime-startTime)/1e6 + "mSec");
	}

	public void glMouseDrag(double x, double y, double z, MouseEvent e, ParamSet paramSet) {
		// TODO Auto-generated method stub

	}

	public void glMouseMovedUp(double x, double y, double z, MouseEvent e, ParamSet paramSet) {
		// TODO Auto-generated method stub

	}

	public void glMouseUp(double x, double y, double z, MouseEvent e, ParamSet paramSet) {
		// TODO Auto-generated method stub

	}

	public void menuetElementDeselected() {
		// TODO Auto-generated method stub

	}

	public void menuetElementSelected() {
		// TODO Auto-generated method stub

	}

	public void glKeyPressed(KeyEvent e, boolean ctrlIsDown, boolean shiftIsDown, ParamSet paramSet) {
		// TODO Auto-generated method stub
	}

}
