package backend.model;

import java.util.LinkedList;

import ui.tools.ToolInterface;
import ui.tools.ToolInterface2D;
import backend.adt.ParamSet;
import backend.primatives.Prim2D;


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
public class Feature2D{

	public ToolInterface2D    toolInterface; 
	public ParamSet           paramSet;
	public String		      label;
	public boolean		      isSelected = true;
	public LinkedList <Prim2D>prim2DList = new LinkedList<Prim2D>();
	
	public Feature2D(ToolInterface2D toolInt, ParamSet pSet, String labelName){
		toolInterface = toolInt;
		paramSet      = pSet;
		label         = labelName;
	}
	
	public void buildPrim2DList(){
		if(toolInterface != null && paramSet != null){
			prim2DList = this.toolInterface.buildPrimList(paramSet);
		}
	}
}