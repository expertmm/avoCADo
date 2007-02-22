package ui.tools.DD;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import ui.menuet.MEButton;
import ui.menuet.Menuet;
import ui.menuet.MenuetElement;
import ui.tools.Tool2D;
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
public class Tool2DCancel extends Tool2D{

	public Tool2DCancel(Menuet menuet){	
		
		// initialize GUI elements
		mElement = new MEButton(menuet, this.getToolMode());
		mElement.mePreferredHeight = 25;
		mElement.meColorMouseOver  = AvoGlobal.COLOR_MENUET_CNCL_MO;
		mElement.meColorUnselected = AvoGlobal.COLOR_MENUET_CNCL_US; 
		mElement.meLabel = "Cancel";
		mElement.setToolTipText("Cancel ALL changes made \nin the 2D drawing mode.");
		mElement.mePriority = 0; 	// 0 = always show element, >5 = never show element
		mElement.meDispOptions = MenuetElement.ME_TEXT_ONLY;
		
		this.applyToolGroupSettings();	// APPLY 2D GROUP SETTINGS
		
		toolInterface = new Tool2DCancelInt();
	}

	@Override
	public void toolSelected() {
		AvoGlobal.paramDialog.finalizeCurrentParams();
		MessageBox m = new MessageBox(AvoGlobal.menuet.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		m.setMessage("Are you sure you want to discard ALL changes\nand exit the 2D drawing mode?");
		m.setText("Discard ALL Changes?");
		if(m.open() == SWT.YES){		
			AvoGlobal.menuet.disableAllTools();
			AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_MAIN);
			
			// TODO: remove sketch when Cancel is pushed.
			//       this will also force the TreeViewer to rebuild itself.

			AvoGlobal.paramDialog.setParamSet(null);
			
			AvoGlobal.menuet.currentTool = null;			
			
			AvoGlobal.menuet.updateToolModeDisplayed();
			AvoGlobal.glView.updateGLView = true;
		}
	}
}