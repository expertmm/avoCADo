package ui.menuet;

import ui.tools.build.ToolBuildDoneView;
import ui.tools.build.ToolBuildExtrudeView;
import ui.tools.build.ToolBuildRevolveView;
import ui.tools.part.ToolMain2D3DView;
import ui.tools.part.ToolMain2DView;
import ui.tools.sketch.Tool2DCancelView;
import ui.tools.sketch.Tool2DCircleView;
import ui.tools.sketch.Tool2DDoneView;
import ui.tools.sketch.Tool2DExampleView;
import ui.tools.sketch.Tool2DLineView;
import ui.tools.sketch.Tool2DRectView;
import ui.tools.sketch.Tool2DSelectView;
import backend.global.AvoColors;
import backend.global.AvoGlobal;


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
public class MenuetBuilder {

	/**
	 * Build all of the tools into the menuet.
	 * Nothing fancy, just add them all along 
	 * with appropriate labels.
	 * @param menuet
	 */
	public static void buildMenuet(Menuet menuet){
		
		//
		//  TOOL MODE:  2D
		//
		new Tool2DDoneView(menuet);
		new Tool2DCancelView(menuet);
		
		MELabel label2D = new MELabel(menuet,Menuet.MENUET_MODE_SKETCH, null);
		label2D.meColorBackground = AvoColors.COLOR_MENUET_SKETCH;
		label2D.meLabel = "2D";
		label2D.textIsBold = true;
		
		new Tool2DLineView(menuet);
		new Tool2DCircleView(menuet);
		new Tool2DRectView(menuet);
		new Tool2DExampleView(menuet);
		
		
		new METoolbox(menuet,Menuet.MENUET_MODE_SKETCH);
		
		new Tool2DSelectView(menuet);
		
		
		//
		//  TOOL MODE:  2Dto3D
		//
		new ToolBuildDoneView(menuet);
		
		MELabel label2D3D = new MELabel(menuet,Menuet.MENUET_MODE_BUILD, null);
		label2D3D.meColorBackground = AvoColors.COLOR_MENUET_BUILD;
		label2D3D.meLabel = "2Dto3D";
		label2D3D.textIsBold = true;
		
		new ToolBuildExtrudeView(menuet);
		new ToolBuildRevolveView(menuet);
		
		new METoolbox(menuet,Menuet.MENUET_MODE_BUILD);
		
		//
		//  TOOL MODE:  Main
		//
		MELabel labelMAIN = new MELabel(menuet,Menuet.MENUET_MODE_PART, null);
		labelMAIN.meColorBackground = AvoColors.COLOR_MENUET_PART;
		labelMAIN.meLabel = "Tools";
		labelMAIN.textIsBold = true;
		
		new ToolMain2DView(menuet);
		new ToolMain2D3DView(menuet);
		AvoGlobal.menuet.currentToolMode = Menuet.MENUET_MODE_PART;		
		
	}
	
}
