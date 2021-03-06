package org.poikilos.librecsg.backend.global;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


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

/**
 * avoCADo's main colors for use by all graphical elements
 */
public class AvoColors {

	/**
	 * Background color of Menuet in various modes.
	 */
	public static final Color COLOR_MENUET_PROJECT = new Color(Display.getCurrent(),  220,  220,  220); //
	public static final Color COLOR_MENUET_GROUP   = new Color(Display.getCurrent(),  239,  220,  232); // pink/purple
	public static final Color COLOR_MENUET_SHARE   = new Color(Display.getCurrent(),  244,  242,  205); // yellow
	public static final Color COLOR_MENUET_PART    = new Color(Display.getCurrent(),  220,  210,  240); // blue
	public static final Color COLOR_MENUET_SKETCH  = new Color(Display.getCurrent(),  215,  245,  210); // green
	public static final Color COLOR_MENUET_BUILD   = new Color(Display.getCurrent(),  210,  220,  242); // purple/blue
	public static final Color COLOR_MENUET_MODIFY  = new Color(Display.getCurrent(),  245,  225,  210); // orange

	/**
	 * colors for special menuet buttons
	 */
	public static final Color COLOR_MENUET_DONE_US = new Color(Display.getCurrent(),  200,  255,  200);
	public static final Color COLOR_MENUET_DONE_MO = new Color(Display.getCurrent(),  230,  255,  230);
	public static final Color COLOR_MENUET_CNCL_US = new Color(Display.getCurrent(),  255,  200,  200);
	public static final Color COLOR_MENUET_CNCL_MO = new Color(Display.getCurrent(),  255,  230,  230);
	public static final Color COLOR_MENUET_TLBX_US = new Color(Display.getCurrent(),  180,  180,  255);
	public static final Color COLOR_MENUET_TLBX_MO = new Color(Display.getCurrent(),  200,  200,  255);

	/**
	 * colors for parameter dialog
	 */
	public static final Color COLOR_PARAM_BG       = new Color(Display.getCurrent(),  220,  210,  240);
	public static final Color COLOR_PARAM_DERIVED  = new Color(Display.getCurrent(),  220,  220,  240);
	public static final Color COLOR_PARAM_SEL_SAT  = new Color(Display.getCurrent(),  160,  240,  160);
	public static final Color COLOR_PARAM_SEL_UNSAT= new Color(Display.getCurrent(),  240,  160,  160);

	/**
	 * colors for MenuetToolboxDialog
	 */
	public static final Color COLOR_TOOLBOX_BG     = new Color(Display.getCurrent(),  220,  220,  255);

	/**
	 * colors for QuickSettings bar
	 */
	public static final Color COLOR_QSET_BG = new Color(Display.getCurrent(),  220,  220,  240);

	/**
	 * colors for GLView
	 */
	public static float[] GL_COLOR4_BACKGND    = new float[] {0.95f, 0.95f, 1.0f, 1.0f};
	public static float[] GL_COLOR4_GRID_DARK  = new float[] {0.6f, 0.6f, 0.6f, 1.0f};
	public static float[] GL_COLOR4_GRID_LIGHT = new float[] {0.8f, 0.8f, 0.8f, 1.0f};
	public static float[] GL_COLOR4_2D_NONACT  = new float[] {0.23f, 0.18f, 0.90f, 1.0f}; // non-active 2D primitives
	public static float[] GL_COLOR4_2D_ACTIVE  = new float[] {0.94f, 0.60f, 0.10f, 1.0f}; // active 2D primitives
	public static float[] GL_COLOR4_2D_X_AXIS  = new float[] {1.0f, 0.2f, 0.2f, 1.0f}; // red
	public static float[] GL_COLOR4_2D_Y_AXIS  = new float[] {0.2f, 1.0f, 0.2f, 1.0f}; // green
	public static float[] GL_COLOR4_2D_Z_AXIS  = new float[] {0.2f, 0.2f, 1.0f, 1.0f}; // blue
	public static float[] GL_COLOR4_2D_ORIGIN  = new float[] {0.6f, 0.6f, 0.6f, 0.6f}; // grey
	public static float[] GL_COLOR4_2D_REG_UNSEL   = new float[] {0.55f, 0.91f, 0.42f, 0.5f}; // region unselected
	public static float[] GL_COLOR4_2D_REG_SEL     = new float[] {0.55f, 0.53f, 0.90f, 1.0f}; // region selected
	public static float[] GL_COLOR4_2D_REG_SELMO   = new float[] {0.68f, 0.49f, 0.90f, 1.0f}; // region selected mouseover
	public static float[] GL_COLOR4_2D_REG_UNSELMO = new float[] {0.55f, 0.91f, 0.74f, 0.5f}; // region unselected mouseover

	public static float[] GL_COLOR4_3D_NONACT  = new float[] {0.23f, 0.18f, 0.90f, 1.0f}; // non-active 2D primitives
	public static float[] GL_COLOR4_3D_ACTIVE  = new float[] {0.94f, 0.60f, 0.10f, 1.0f}; // active 2D primitives
}


