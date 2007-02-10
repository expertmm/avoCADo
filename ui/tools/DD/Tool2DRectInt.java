package ui.tools.DD;

import javax.media.opengl.GL;

import ui.opengl.GLDynPrim;
import ui.tools.ToolInterface;
import backend.adt.Param;
import backend.adt.ParamSet;
import backend.adt.Point2D;
import backend.global.AvoGlobal;
import backend.model.Feature;


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
public class Tool2DRectInt implements ToolInterface {

	/**
	 * All of the tool's main functionality
	 * mouse handling, glView drawing, 
	 * parameter storage, etc.
	 *
	 */
	public Tool2DRectInt(){		
	}
	
	public void glMouseDown(double x, double y, double z, int mouseX, int mouseY) {
		if(AvoGlobal.getWorkingFeature() != null){
			// store the last feature more permenently (paramSet and type)
			AvoGlobal.pushWorkFeatToSet();
		}
		
		//
		// Build parameter set for 2D Line
		//
		ParamSet pSet = new ParamSet();
		pSet.addParam("a", new Param("Pt.A", new Point2D(x,y)));
		pSet.addParam("b", new Param("Pt.B", new Point2D(x,y)));
		Param width = new Param("Width", 0.0);
		Param height = new Param("Height", 0.0);
		width.setParamIsDerived(true);
		height.setParamIsDerived(true);
		pSet.addParam("w", width);
		pSet.addParam("h", height);
		
		//
		// set the workingFeature to the 2D Line
		//
		AvoGlobal.setWorkingFeature(new Feature(this, pSet, "Rectangle"));
	}

	public void glMouseDrag(double x, double y, double z, int mouseX, int mouseY) {
		AvoGlobal.getWorkingFeature().paramSet.changeParam("b", new Point2D(x,y));
		Point2D ptA = (Point2D)AvoGlobal.getWorkingFeature().paramSet.getParam("a").getData();
		Point2D ptB = (Point2D)AvoGlobal.getWorkingFeature().paramSet.getParam("b").getData();		
		AvoGlobal.getWorkingFeature().paramSet.changeParam("w", Math.abs(ptA.getX() - ptB.getX()));
		AvoGlobal.getWorkingFeature().paramSet.changeParam("h", Math.abs(ptA.getY() - ptB.getY()));
	}

	public void glMouseUp(double x, double y, double z, int mouseX, int mouseY) {
		// * finalize line's formation
		AvoGlobal.getWorkingFeature().paramSet.changeParam("b", new Point2D(x,y));
		Point2D ptA = (Point2D)AvoGlobal.getWorkingFeature().paramSet.getParam("a").getData();
		Point2D ptB = (Point2D)AvoGlobal.getWorkingFeature().paramSet.getParam("b").getData();
		AvoGlobal.getWorkingFeature().paramSet.changeParam("w", Math.abs(ptA.getX() - ptB.getX()));
		AvoGlobal.getWorkingFeature().paramSet.changeParam("h", Math.abs(ptA.getY() - ptB.getY()));
		
		// * discard if start point is the same as the end point
		if(ptA.getX() == ptB.getX() || ptA.getY() == ptB.getY()){
			// end point are the same... discard
			System.out.println("Reactangle has zero area... discarding feature");
			AvoGlobal.setWorkingFeature(null);
		}
	}

	public void glDrawFeature(GL gl, ParamSet p) {
		Point2D ptA = (Point2D)p.getParam("a").getData();
		Point2D ptB = (Point2D)p.getParam("b").getData();
		GLDynPrim.line2D(gl, ptA, new Point2D(ptA.getX(),ptB.getY()), 0.0);
		GLDynPrim.line2D(gl, ptA, new Point2D(ptB.getX(),ptA.getY()), 0.0);
		GLDynPrim.line2D(gl, ptB, new Point2D(ptA.getX(),ptB.getY()), 0.0);
		GLDynPrim.line2D(gl, ptB, new Point2D(ptB.getX(),ptA.getY()), 0.0);
	}
	
}
