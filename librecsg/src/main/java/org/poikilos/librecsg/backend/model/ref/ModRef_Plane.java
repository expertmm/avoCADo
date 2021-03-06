package org.poikilos.librecsg.backend.model.ref;

import java.util.Iterator;

import org.poikilos.librecsg.backend.model.Part;
import org.poikilos.librecsg.backend.model.CSG.CSG_Face;
import org.poikilos.librecsg.backend.model.sketch.SketchPlane;


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

public class ModRef_Plane extends ModelReference{

	private final int uniqueSubPartID;
	private final int uniqueFaceID;

	// planes are built with reference to a particular part, feat2D3D, and faceID within that feature.

	/**
	 * a plane reference is build upon an existing SubPart
	 * that has a unique face associated with it.
	 * @param uniqueSubPartID the unique ID of the SubPart to use
	 * @param uniqueFaceID the unique ID of the face created by the SubPart.
	 */
	public ModRef_Plane(int uniqueSubPartID, int uniqueFaceID){
		super(ModRefType.Plane);
		this.uniqueSubPartID = uniqueSubPartID;
		this.uniqueFaceID    = uniqueFaceID;
	}

	public int getUniqueFaceID(){
		return uniqueFaceID;
	}

	@Override
	public String getStringReferenceInfo() {
		return "SubPartID:" + uniqueSubPartID + ", FaceID:" + uniqueFaceID;
	}

	public SketchPlane getSketchPlane(Part part){
		Iterator<CSG_Face> fIter = part.getSolid().getFacesIter();
		while(fIter.hasNext()){
			CSG_Face face = fIter.next();
			if(face.isSelectable()){
				if(face.getModRefPlane().uniqueSubPartID == this.uniqueSubPartID &&
						face.getModRefPlane().uniqueFaceID == this.uniqueFaceID){
					return face.getSketchPlane();
				}
			}
		}
		return null;
	}

}
