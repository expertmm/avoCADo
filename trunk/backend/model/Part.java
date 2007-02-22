package backend.model;

import java.util.LinkedList;
import java.util.List;

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
public class Part {

	protected List<SubPart> subPartList = new LinkedList<SubPart>();
	
	protected int activeSubPart = -1;
	
	protected Group group;
	
	public Part(Group group){
		this.group = group;
	}
	
	public Group getParentGroup(){
		return this.group;
	}
	
	/**
	 * add a new SubPart to the end of the list of SubParts
	 * @param subPart non-null SubPart to be added
	 * @return the index of hte newly added SubPart, or -1 if SubPart was null.
	 */
	public int add(SubPart subPart){
		if(subPart != null){
			subPartList.add(subPart);
			AvoGlobal.modelEventHandler.notifyElementAdded();
			return subPartList.size()-1;
		}
		return -1;
	}
	
	/**
	 * get the SubPart at a give index
	 * @param i index
	 * @return the SubPart, or null if index was invalid.
	 */
	public SubPart getAtIndex(int i){
		if(i < 0 || i >= subPartList.size()){
			// index is not valid!
			return null;
		}
		return subPartList.get(i);
	}
	
	/**
	 * @return the size() of the list of SubParts.
	 */
	public int getSubPartListSize(){
		return subPartList.size();
	}
	
	
	/**
	 * set the index of the SubPart that should be set to Active.
	 * @param i index
	 */
	public void setActiveSubPart(int i){
		if(i < 0 || i >= subPartList.size()){
			// index is not valid!
			return;
		}
		activeSubPart = i;
		AvoGlobal.modelEventHandler.notifyActiveElementChanged();
	}
	
	/**
	 * set the active SubPart to none
	 */
	public void setActiveToNone(){
		activeSubPart = -1;
		AvoGlobal.modelEventHandler.notifyActiveElementChanged();
	}
	
	/**
	 * get the currently active SubPart
	 * @return the active SubPart, or null if no SubPart is active
	 */
	public SubPart getActiveSubPart(){
		return this.getAtIndex(activeSubPart);
	}
	
	/**
	 * Remove the SubPart at the index if present.
	 * @param i index
	 */
	public void removeSubPartAtIndex(int i){
		if(i < 0 || i >= subPartList.size()){
			// index is not valid!
			return;
		}
		subPartList.remove(i);
		AvoGlobal.modelEventHandler.notifyElementRemoved();
	}
	
	/**
	 * remove the active SubPart from the list.
	 */
	public void removeActiveSubPart(){
		removeSubPartAtIndex(activeSubPart);
	}
	
	
}
