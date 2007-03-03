package ui.tools.tool2D;

import ui.tools.ToolModel2D;
import backend.adt.PType;
import backend.adt.ParamSet;
import backend.adt.Point2D;
import backend.global.AvoGlobal;
import backend.model.Sketch;
import backend.model.sketch.Prim2DLine;
import backend.model.sketch.Prim2DList;


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
public class Tool2DExampleModel implements ToolModel2D{

	public Prim2DList buildPrim2DList(ParamSet paramSet) {
		try{
			Point2D ptC  = paramSet.getParam("c").getDataPoint2D();
			double size = paramSet.getParam("s").getDataDouble();
			int triangles = paramSet.getParam("t").getDataInteger();
			if(triangles >= 2){
				Prim2DList primList = new Prim2DList();
				Point2D arm = new Point2D(size,0.0);
				double degPerTri = 360.0 / (double)(2*triangles);
				for(int i=0; i< triangles; i++){
					primList.add(new Prim2DLine(ptC,arm.getNewRotatedPt(2*i*degPerTri).addPt(ptC)));
					primList.add(new Prim2DLine(arm.getNewRotatedPt((2*i+1)*degPerTri).addPt(ptC),arm.getNewRotatedPt(2*i*degPerTri).addPt(ptC)));
					primList.add(new Prim2DLine(ptC,arm.getNewRotatedPt((2*i+1)*degPerTri).addPt(ptC)));
				}
				return primList;
			}
		}catch(Exception ex){
			System.out.println(ex.getClass());
		}
		return null;
	}

	public boolean paramSetIsValid(ParamSet paramSet) {
		//		 ParamSet:  "Rectangle"
		// --------------------------------
		// # "c"  ->  "Center"    <Point2D>
		// # "s"  ->  "Size"      <Double>
		// # "t"  ->  "Triangles" <Integer>
		// --------------------------------		
		boolean isValid = (	paramSet != null &&
							paramSet.label == "Example" &&
							paramSet.hasParam("c", PType.Point2D) &&
							paramSet.hasParam("s", PType.Double) &&
							paramSet.hasParam("t", PType.Integer));
		return isValid;
	}

	public void updateDerivedParams(ParamSet paramSet) {		
	}

	public void finalize(ParamSet paramSet) {
		Sketch sketch = AvoGlobal.project.getActiveSketch();
		if(sketch != null){
			sketch.deselectAllFeat2D();
		}
	}
	
}
