package backend.model.CSG;

import java.util.LinkedList;
import java.util.List;


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
* @created Mar. 2007
*/

/**
 * Constructive Solid Geometry :: Face<br/><br/>
 * 
 * A CSG_Face is a special type of polygon that must:<br/>
 * 1. Be planar and Convex<br/>
 * 2. Have no 3 vertices that are collinear<br/>
 * 3. Have every vertex be unique<br/>
 * 4. Always be viewed as Clockwise <br/><br/>
 * 
 * Algorithms and structures from:<br/>
 * - Laidlaw, Trumbore, and Hughes <br/>
 * - "Constructive Solid Geometry for Polyhedral Objects"<br/>
 * - SIGGRAPH 1986, Volume 20, Number 4, pp.161-170
 */
public class CSG_Face {

	List<CSG_Vertex> vertices = new LinkedList<CSG_Vertex>();
	CSG_Bounds extent;
	
	public CSG_Face(){
		
	}
	
	
	
	
}
