package org.poikilos.librecsg.ui.paramdialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.poikilos.librecsg.ui.event.ParamListener;
import org.poikilos.librecsg.ui.utilities.NumUtils;
import org.poikilos.librecsg.backend.adt.ParamType;
import org.poikilos.librecsg.backend.adt.Param;
import org.poikilos.librecsg.backend.adt.ParamNotCorrectTypeException;
import org.poikilos.librecsg.backend.adt.ParamSet;
import org.poikilos.librecsg.backend.global.AvoColors;
import org.poikilos.librecsg.backend.global.AvoGlobal;


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
* @created Feb. 2007
*/
public class PCompDouble extends ParamComp{

	private Text tD;

	public PCompDouble(Composite parent, int style, Param p, ParamSet paramSet){
		super(parent,style, paramSet);
		param = p;

		//
		// Setup the component's layout
		//
		RowLayout rl = new RowLayout();
		rl.wrap = false;
		this.setLayout(rl);

		//
		// check to make sure param is of correct type
		//
		if(p.getType() != ParamType.Double){
			System.out.println("trying to display a non-Double in a PCompDouble (paramDialog)");
			return;
		}

		Double pD = getParamData();

		//
		// Param label display
		//
		Label l = new Label(this, SWT.NONE);
		l.setText(p.getLabel() + ": ");

		//
		// Create font to use for text boxes
		//
		FontData fd = new FontData();
		fd.setHeight(10);
		fd.setName("courier");
		Font textF = new Font(this.getDisplay(), fd);

		//
		// Value:
		//
		tD = new Text(this, SWT.BORDER);
		tD.setText(NumUtils.doubleToFixedString(pD,8));
		tD.setFont(textF);

		//
		// Value: handle key presses
		//
		tD.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				if(e.character == '\r'){
					// check to see if string is a valid number
					if(NumUtils.stringIsNumber(tD.getText())){
						Double pD = getParamData();
						pD = Double.parseDouble(tD.getText());
						param.change(pD);
						updateParamViaToolInterface();
						AvoGlobal.glView.updateGLView = true;
					}else{
						tD.setText(NumUtils.doubleToFixedString(getParamData(),8));
					}
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});

		//
		// X Coor: perform field validation
		//
		tD.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				// check to see if string is a valid number
				if(NumUtils.stringIsNumber(tD.getText())){
					Double pD = getParamData();
					pD = Double.parseDouble(tD.getText());
					param.change(pD);
					updateParamViaToolInterface();
					AvoGlobal.glView.updateGLView = true;
				}else{
					tD.setText(NumUtils.doubleToFixedString(getParamData(),8));
				}
			}
		});

		//
		// Disable user alteration if the parameter is derived
		//
		if(p.isDerivedParam()){
			tD.setEditable(false);
			tD.setBackground(AvoColors.COLOR_PARAM_DERIVED);
		}

		//
		// Add param listener!
		//
		paramListener = new ParamListener(){
			public void paramModified() {
				tD.setText(NumUtils.doubleToFixedString(getParamData(),8));
			}
			public void paramSwitched() {
			}
		};
		AvoGlobal.paramEventHandler.addParamListener(paramListener);

	}

	Double getParamData(){
		try{
			Double data = param.getDataDouble();
			return data;
		}catch(ParamNotCorrectTypeException e){
			System.out.println(" *** WARNING *** PCompDouble :: param was not of type Double!");
			return 0.0;
		}
	}

}
