package org.poikilos.librecsg.ui.treeviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import org.poikilos.librecsg.ui.event.ModelListener;
import org.poikilos.librecsg.ui.menuet.Menuet;
import org.poikilos.librecsg.backend.global.AvoGlobal;
import org.poikilos.librecsg.backend.model.Build;
import org.poikilos.librecsg.backend.model.Feature2D;
import org.poikilos.librecsg.backend.model.Group;
import org.poikilos.librecsg.backend.model.Modify;
import org.poikilos.librecsg.backend.model.Part;
import org.poikilos.librecsg.backend.model.Project;
import org.poikilos.librecsg.backend.model.Sketch;
import org.poikilos.librecsg.backend.model.SubPart;
import org.poikilos.librecsg.backend.model.material.PartMaterial;


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
public class TreeViewer  {

	public static Composite treeComp;
	private static Tree tree;

	public TreeViewer(Composite parent, int style){
		//super(parent, style);
		treeComp = new Composite(parent, style);
		treeComp.setBackground(new Color(Display.getCurrent(), 200, 200, 240));
		treeComp.setLayout(new FillLayout());

		tree = new Tree(treeComp, SWT.SINGLE);
		buildTreeFromAssembly();

		AvoGlobal.modelEventHandler.addModelListener(new ModelListener(){
			public void activeElementChanged() {
				// TODO highlight the new active element (or none, if null)
			}
			public void elementAdded() {
				buildTreeFromAssembly();

			}
			public void elementRemoved() {
				buildTreeFromAssembly();
			}
		});

		tree.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
				// Set active elements in model based on treeItem selected.
				if(tree.getSelection().length > 0){
					TreeItem ti = tree.getSelection()[0];
					int[] indxs = (int[])ti.getData();
					if(indxs.length == 1){
						// group selected
						AvoGlobal.project.setActiveGroup(indxs[0]);
						AvoGlobal.project.getAtIndex(indxs[0]).setActiveToNone();
						AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_GROUP);
						AvoGlobal.glView.updateGLView = true;
					}
					if(indxs.length == 2){
						// part selected
						AvoGlobal.project.setActiveGroup(indxs[0]);
						AvoGlobal.project.getActiveGroup().setActivePart(indxs[1]);
						AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_PART);
						AvoGlobal.glView.updateGLView = true;
					}
					if(indxs.length == 3){
						// part selected
						AvoGlobal.project.setActiveGroup(indxs[0]);
						AvoGlobal.project.getActiveGroup().setActivePart(indxs[1]);
						// handle special cases on subpart for properties and root planes
						if(indxs[2] == 0){
							// properties for part
							ColorDialog cd = new ColorDialog(treeComp.getShell());
							cd.setText("Select Material Color");
							PartMaterial m = AvoGlobal.project.getActivePart().getPartMaterial();
							cd.setRGB(new RGB((int)(m.diffuse[0]*255),  (int)(m.diffuse[1]*255),  (int)(m.diffuse[2]*255)));
							RGB newColor = cd.open();
							if(newColor != null){
								m.diffuse[0]=(newColor.red/255.0f);
								m.diffuse[1]=(newColor.green/255.0f);
								m.diffuse[2]=(newColor.blue/255.0f);
								AvoGlobal.glView.updateGLView = true;
							}
							return;
						}
						if(indxs[2] == 1 || indxs[2] == 2 || indxs[2] == 3){
							// root plane selected
							Part part = AvoGlobal.project.getActivePart();
							if(part == null){
								System.out.println("TreeViewer(mouseDoubleClick): the active part was null?! this makes no CENTS!!11");
								return;
							}
							switch(indxs[2]){
							case 1: {
									part.setSelectedPlane(part.planeXY);
									break;
									}
							case 2: {
									part.setSelectedPlane(part.planeYZ);
									break;
									}
							case 3: {
									part.setSelectedPlane(part.planeZX);
									break;
									}
							}
							return;
						}else{
							// sub-part selected.
							SubPart subpart = AvoGlobal.project.getActivePart().getAtIndex(indxs[2]-4);
							Sketch sketch =  subpart.getSketch();
							if(sketch != null){
								// subpart was a "sketch" tool
								AvoGlobal.project.getActivePart().setActiveSubPart(indxs[2]-4);
								AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_SKETCH);
								AvoGlobal.glView.updateGLView = true;
							}
							Build feat2D3D = subpart.getBuild();
							if(feat2D3D != null){
								// subpart was a "build" tool
								AvoGlobal.project.getActivePart().setActiveSubPart(indxs[2]-4);
								AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_BUILD);
								AvoGlobal.glView.updateGLView = true;
							}
							Modify feat3D3D = subpart.getModify();
							if(feat3D3D != null){
								// subpart was a "modify" tool.
								AvoGlobal.project.getActivePart().setActiveSubPart(indxs[2]-4);
								AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_MODIFY);
								AvoGlobal.glView.updateGLView = true;
							}
						}
					}

					System.out.print("---> ");
					for(int i : indxs){
						System.out.print(i + ",");
					}
					System.out.print("\n");
				}
			}
			public void mouseDown(MouseEvent e) {
			}
			public void mouseUp(MouseEvent e) {
				if(tree.getSelection().length > 0){
					TreeItem ti = tree.getSelection()[0];
					int[] indxs = (int[])ti.getData();
					if(indxs.length == 1){
						// group selected
						AvoGlobal.project.setActiveGroup(indxs[0]);
						AvoGlobal.project.getAtIndex(indxs[0]).setActiveToNone();
						AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_GROUP);
						AvoGlobal.glView.updateGLView = true;
					}
					if(indxs.length == 2){
						// part selected
						AvoGlobal.project.setActiveGroup(indxs[0]);
						AvoGlobal.project.getActiveGroup().setActivePart(indxs[1]);
						AvoGlobal.menuet.setCurrentToolMode(Menuet.MENUET_MODE_PART);
						AvoGlobal.glView.updateGLView = true;
					}
				}
			}
		});
	}

	// triggered via event listener
	void buildTreeFromAssembly(){
		Project project = AvoGlobal.project;

		if(project == null){
			return;
		}
		for(int i=project.getGroupListSize(); i<tree.getItemCount(); i++){
			// remove groups that no longer exist
			tree.getItem(i).dispose();
		}
		// only add things that have changed.
		for(int iGroup=0; iGroup < project.getGroupListSize(); iGroup++){
			Group group = project.getAtIndex(iGroup);
			TreeItem tiGroup;
			if(tree.getItemCount() > iGroup){
				tiGroup = tree.getItem(iGroup);
			}else{
				tiGroup = new TreeItem(tree, SWT.NONE, iGroup);
			}
			tiGroup.setText("Group " + group.ID);
			tiGroup.setData(new int[] {iGroup});
			for(int i=group.getPartListSize(); i<tiGroup.getItemCount(); i++){
				// remove parts that no longer exist
				tiGroup.getItem(i).dispose();
			}
			for(int iPart=0; iPart<group.getPartListSize(); iPart++){
				Part part = group.getAtIndex(iPart);
				TreeItem tiPart;
				if(tiGroup.getItemCount() > iPart){
					tiPart = tiGroup.getItem(iPart);
				}else{
					tiPart = new TreeItem(tiGroup, SWT.NONE, iPart);
					tiGroup.setExpanded(true);
				}
				if (part.PartName!=null){
					tiPart.setText("Part " + part.PartName + " [" + part.ID +"]");
				}else{
					tiPart.setText("Part [" + part.ID +"]");
				}

				tiPart.setData(new int[] {iGroup, iPart});

				TreeItem tiPartProp;
				if(tiPart.getItemCount() > 0){
					tiPartProp = tiPart.getItem(0);
				}else{
					tiPartProp = new TreeItem(tiPart, SWT.NONE, 0);
					tiPart.setExpanded(true);
				}
				tiPartProp.setData(new int[] {iGroup, iPart, 0});
				tiPartProp.setText("Material");

				TreeItem tiPartXY;
				if(tiPart.getItemCount() > 1){
					tiPartXY = tiPart.getItem(1);
				}else{
					tiPartXY = new TreeItem(tiPart, SWT.NONE, 1);
				}
				tiPartXY.setData(new int[] {iGroup, iPart, 1});
				tiPartXY.setText("XY Plane");

				TreeItem tiPartYZ;
				if(tiPart.getItemCount() > 2){
					tiPartYZ = tiPart.getItem(2);
				}else{
					tiPartYZ = new TreeItem(tiPart, SWT.NONE, 2);
				}
				tiPartYZ.setData(new int[] {iGroup, iPart, 2});
				tiPartYZ.setText("YZ Plane");

				TreeItem tiPartZX;
				if(tiPart.getItemCount() > 3){
					tiPartZX = tiPart.getItem(3);
				}else{
					tiPartZX = new TreeItem(tiPart, SWT.NONE, 3);
				}
				tiPartZX.setData(new int[] {iGroup, iPart, 3});
				tiPartZX.setText("ZX Plane");

				for(int i=part.getSubPartListSize()+4; i<tiPart.getItemCount(); i++){
					// remove subParts that no longer exist
					tiPart.getItem(i).dispose();
				}
				for(int iSubPart=0; iSubPart < part.getSubPartListSize(); iSubPart++){
					SubPart subPart = part.getAtIndex(iSubPart);
					TreeItem tiSubPart;
					if(tiPart.getItemCount() > iSubPart+4){
						tiSubPart = tiPart.getItem(iSubPart+4);
					}else{
						tiSubPart = new TreeItem(tiPart, SWT.NONE, iSubPart+4);
						tiPart.setExpanded(true);
					}
					tiSubPart.setData(new int[] {iGroup, iPart, iSubPart+4});
					Sketch sketch = subPart.getSketch();
					if(sketch != null){
						tiSubPart.setText("Sketch " + sketch.getUniqueID());
						if(sketch.isConsumed){
							tiSubPart.setText("Sketch(c) " + sketch.getUniqueID());
							tiSubPart.setBackground(new Color(Display.getCurrent(), 240, 200, 200));
						}

						for(int i=sketch.getFeat2DListSize(); i<tiSubPart.getItemCount(); i++){
							// remove Feature2D that no longer exist
							tiSubPart.getItem(i).dispose();
						}
						for(int iSketch=0; iSketch < sketch.getFeat2DListSize(); iSketch++){
							Feature2D feat2D = sketch.getAtIndex(iSketch);
							TreeItem tiFeat2D;
							if(tiSubPart.getItemCount() > iSketch){
								tiFeat2D = tiSubPart.getItem(iSketch);
							}else{
								tiFeat2D = new TreeItem(tiSubPart, SWT.NONE, iSketch);
							}
							tiFeat2D.setData(new int[] {iGroup, iPart, iSubPart, iSketch});
							tiFeat2D.setText(feat2D.ID);
						}
					}
					Build feat2D3D = subPart.getBuild();
					if(feat2D3D != null){
						if(feat2D3D.paramSet != null){
							tiSubPart.setText(feat2D3D.paramSet.label);
						}else{
							tiSubPart.setText("2Dto3D " + feat2D3D.ID);
						}
					}
					Modify feat3D3D = subPart.getModify();
					if(feat3D3D != null){
						tiSubPart.setText("Feature 3D " + feat3D3D.ID);
					}

				}
			}
		}


	}

}
