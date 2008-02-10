package ui.opengl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ui.event.ParamListener;
import ui.menuet.Menuet;
import backend.global.AvoColors;
import backend.global.AvoGlobal;
import backend.model.Build;
import backend.model.Feature2D;
import backend.model.Part;
import backend.model.Sketch;
import backend.model.SubPart;
import backend.model.CSG.CSG_Solid;
import backend.model.sketch.Prim2D;
import backend.model.sketch.Region2D;


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
public class GLView {

	static GL  gl;
	static GLU glu;
	public final  GLCanvas  glCanvas;
	final  GLContext glContext;
	
	boolean mouseIsDown = false;
	
	public boolean updateGLView = true;
	
	static float aspect = 0.0f;
	static float rotation_x = 0.0f;
	static float rotation_y = 0.0f;
	static float rotation_z = 0.0f;
	static float translation_x = 0.0f;
	static float translation_y = 0.0f;
	static float dist_from_center = -30.0f;
	static float viewing_angle = 45.0f;
	
	//double lastMousePos3D[] = new double[] {0.0, 0.0, 0.0};
	
	/** stores the modelview used when backsolving for 3D cords from mouse location. */
	private static double[] mouseModelview = new double[16];
	
	static int mouse_down_button 	= -1;
	static int mouse_down_x 		= 0;
	static int mouse_down_y		 	= 0;
	static float rot_init_x = 0.0f;
	static float rot_init_y = 0.0f;
	static float rot_init_z = 0.0f;	
	static float trans_init_x = 0.0f;
	static float trans_init_y = 0.0f;
	
	static int MOUSE_LEFT   = 1;
	static int MOUSE_MIDDLE = 2;
	static int MOUSE_MIDDLE_SHIFT = 21;
	static int MOUSE_MIDDLE_CTRL  = 22; 
	static int MOUSE_RIGHT  = 3;
	
	private CSG_Solid buildSolidFeat2D3D = null;
	boolean buildSolidNeedsRebuilt = false;
	
	/**
	 * this shader is a GLSL shader used to apply post 
	 * processing rendering effects (procedural textures) on 
	 * modern graphics hardware (openGL 2.0 or greater).
	 */
	public final boolean OPENGL_FRAGMENT_SHADER_GLSL_ENABLED;
	
	/**
	 * this shader is a ARB program used to apply post 
	 * processing rendering effects (procedural textures) on 
	 * legacy graphics hardware (openGL 1.5).
	 */
	public final boolean OPENGL_FRAGMENT_PROGRAM_ARB_ENABLED;
	
	public GLView(Composite comp){
		GLData data = new GLData ();
		data.doubleBuffer = true;
		data.depthSize = 16;
		data.stencilSize = 1;
		glCanvas = new GLCanvas(comp, SWT.NONE, data);
		
		GLCapabilities glc = new GLCapabilities();
		glc.setStencilBits(1);
		System.out.println("JOGL -- OpenGL Capabilities -- " +
				"DepthBits:" + glc.getDepthBits() + 
				"; DoubleBuff:" + glc.getDoubleBuffered() + 
				"; HWAccel:" + glc.getHardwareAccelerated() + 
				"; StencilBits:" + glc.getStencilBits() +
				"; FloatPBuf:" + glc.getPbufferFloatingPointBuffers());
		glCanvas.setCurrent();
		glContext = GLDrawableFactory.getFactory().createExternalGLContext();		
		
		glCanvas.addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent e) {
				updateGLView = true;				
			}

			public void controlResized(ControlEvent e) {
				updateGLView = true;				
			}			
		});
		
		
		glCanvas.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e) {
				updateGLView = true;
			}			
		});
		
		//
		//  TODO: do rotation better! (Quaternions?)
		//
		glCanvas.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				boolean ctrlIsDown  = (e.stateMask & SWT.CTRL) != 0;
				boolean shiftIsDown = (e.stateMask & SWT.SHIFT) != 0;
				if(ctrlIsDown){
					float zoom_smoothness = 1.50f; // closer to 1.0 is smoother
					if(e.keyCode == SWT.ARROW_RIGHT){
						rotation_z += 5.0;
						updateGLView = true;
					}
					if(e.keyCode == SWT.ARROW_LEFT){
						rotation_z -= 5.0;
						updateGLView = true;					
					}
					if(e.keyCode == SWT.ARROW_DOWN){
						viewing_angle = Math.min(135.0f, viewing_angle * zoom_smoothness);
						updateGLView = true;
					}
					if(e.keyCode == SWT.ARROW_UP){
						viewing_angle = Math.max(0.025f, viewing_angle / zoom_smoothness);
						updateGLView = true;					
					}
				}else{
					if(shiftIsDown){
						if(e.keyCode == SWT.ARROW_RIGHT){
							translation_x += 1.0;
							updateGLView = true;
						}
						if(e.keyCode == SWT.ARROW_LEFT){
							translation_x -= 1.0;
							updateGLView = true;					
						}
						if(e.keyCode == SWT.ARROW_DOWN){
							translation_y -= 1.0;
							updateGLView = true;
						}
						if(e.keyCode == SWT.ARROW_UP){
							translation_y += 1.0;
							updateGLView = true;					
						}
					}else{
						if(e.keyCode == SWT.ARROW_RIGHT){
							rotation_y += 5.0;
							updateGLView = true;
						}
						if(e.keyCode == SWT.ARROW_LEFT){
							rotation_y -= 5.0;
							updateGLView = true;					
						}
						if(e.keyCode == SWT.ARROW_DOWN){
							rotation_x += 5.0;
							updateGLView = true;
						}
						if(e.keyCode == SWT.ARROW_UP){
							rotation_x -= 5.0;
							updateGLView = true;					
						}
					}
				}
				
				//
				// let current tool know that a key has been pressed
				//
				if(AvoGlobal.activeToolController != null){
					AvoGlobal.activeToolController.glKeyPressed(e, ctrlIsDown, shiftIsDown, AvoGlobal.paramDialog.getParamSet());
					updateGLView = true;
				}	
			}
			public void keyReleased(KeyEvent e) {			
			}			
		});
		
		//
		//  TODO: do rotation better! (Quaternions?)
		//
		glCanvas.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				mouse_down_button = e.button;
				if(((e.stateMask & SWT.SHIFT) != 0) && e.button == MOUSE_MIDDLE){
					System.out.println("Shift + Middle click");
					mouse_down_button = MOUSE_MIDDLE_SHIFT;
				}
				if(((e.stateMask & SWT.CTRL) != 0) && e.button == MOUSE_MIDDLE){
					System.out.println("Control + Middle click");
					mouse_down_button = MOUSE_MIDDLE_CTRL;
				}
				mouse_down_x = e.x;
				mouse_down_y = e.y;
				rot_init_x = rotation_x;
				rot_init_y = rotation_y;
				rot_init_z = rotation_z;
				trans_init_x = translation_x;
				trans_init_y = translation_y;
				//System.out.println("mouse button: " + e.button);
				
				//
				// let current tool know that the left mouse has been clicked
				//
				if(mouse_down_button == MOUSE_LEFT && 
						AvoGlobal.activeToolController != null){
					double[] coor = getWorldCoorFromMouse(e.x,e.y);
					AvoGlobal.activeToolController.glMouseDown(coor[0], coor[1], coor[2], e, AvoGlobal.paramDialog.getParamSet());
					updateGLView = true;
				}				
			}
			public void mouseUp(MouseEvent e) {
				if(mouse_down_button == MOUSE_LEFT && 
						glCanvas.getBounds().contains(e.x,e.y) && 
						(AvoGlobal.activeToolController != null)){
					double[] coor = getWorldCoorFromMouse(e.x,e.y);
					AvoGlobal.activeToolController.glMouseUp(coor[0], coor[1], coor[2], e, AvoGlobal.paramDialog.getParamSet());					
				}
				mouse_down_button = -1;
				updateGLView = true;
			}			
		});
		glCanvas.addMouseMoveListener(new MouseMoveListener(){
			public void mouseMove(MouseEvent e) {
				float sensitivity = viewing_angle / 22.5f * 0.25f;				
				if(mouse_down_button == MOUSE_LEFT){					
				}
				if(mouse_down_button == MOUSE_MIDDLE){
					//System.out.println("Rotating view");
					int xdif = e.x - mouse_down_x;
					int ydif = e.y - mouse_down_y;					
					//rotation_x = sensitivity*ydif/2 + rot_init_x;
					//rotation_y = sensitivity*xdif/2 + rot_init_y;
					rotation_x = ydif/2 + rot_init_x;
					rotation_y = xdif/2 + rot_init_y;
					rotation_z =   0.0f + rot_init_z;
				}
				if(mouse_down_button == MOUSE_MIDDLE_SHIFT){
					//System.out.println("Panning view");
					int xdif = e.x - mouse_down_x;
					int ydif = e.y - mouse_down_y;
					translation_x = sensitivity*xdif/13.60f + trans_init_x;
					translation_y = -sensitivity*ydif/13.60f + trans_init_y;
				}	
				if(mouse_down_button == MOUSE_MIDDLE_CTRL){
					//System.out.println("Rotating view");
					int xdif = e.x - mouse_down_x;
					int ydif = e.y - mouse_down_y;					
					rotation_x = rot_init_x;
					rotation_y = rot_init_y;
					rotation_z =  sensitivity*ydif + sensitivity*xdif + rot_init_z;
				}
				if(mouse_down_button == MOUSE_RIGHT){					
				}		
				
				//System.out.println("(X,Y),(rX,rY,rZ): (" + translation_x + "," + translation_y + "),(" + 
				//		rotation_x + "," + rotation_y + "," + rotation_z + ")");
				
				//
				// if mouse is inside glView, send the mouseMove event
				// to the currently active tool
				//
				if(glCanvas.getBounds().contains(e.x,e.y)){
					updateGLView = true;
					double[] coor = getWorldCoorFromMouse(e.x,e.y);
					if(AvoGlobal.activeToolController != null){
						if(mouse_down_button == MOUSE_LEFT){
							AvoGlobal.activeToolController.glMouseDrag(coor[0], coor[1], coor[2], e, AvoGlobal.paramDialog.getParamSet());
						}else{
							if(mouse_down_button == -1){
								// no mouse buttons are currently down.
								AvoGlobal.activeToolController.glMouseMovedUp(coor[0], coor[1], coor[2], e, AvoGlobal.paramDialog.getParamSet());
							}
						}
					}
				}
				
			}			
		});
		glCanvas.addListener(SWT.MouseWheel, new Listener(){
			public void handleEvent(Event event) {
				System.out.println("Got mouse wheel: " + event.count);
				float zoom_smoothness = 1.50f; // closer to 1.0 is smoother
				if(event.count > 0){
					viewing_angle = Math.max(0.025f, viewing_angle / zoom_smoothness);
				}else{
					viewing_angle = Math.min(135.0f, viewing_angle * zoom_smoothness);
				}
				
				System.out.println("Viewing angle now:" + viewing_angle);
				updateGLView = true;
			}			
		});
		
		glInit();
		turnGLLightsOff(gl);
		
		if (gl.isExtensionAvailable("GL_ARB_fragment_shader")){
            System.out.println("ARB_fragment_shader supported (openGL 2.0) --  Materials can look: Excellent!  :)");
            OPENGL_FRAGMENT_SHADER_GLSL_ENABLED = true;
        }else{
        	OPENGL_FRAGMENT_SHADER_GLSL_ENABLED = false;
        }
		if (gl.isExtensionAvailable("GL_ARB_fragment_program")){
            System.out.println("ARB_fragment_program supported (openGL 1.5) --  Materials can look: Okay.");
            OPENGL_FRAGMENT_PROGRAM_ARB_ENABLED = true;
        }else{
        	OPENGL_FRAGMENT_PROGRAM_ARB_ENABLED = false;
        }
		if(!OPENGL_FRAGMENT_SHADER_GLSL_ENABLED && !OPENGL_FRAGMENT_PROGRAM_ARB_ENABLED){
			System.out.println("NO Fragment Shader supported with your hardware (openGL < 1.5) --  Materials will look: Bad. :(");
		}
		
		
		// -----------------------------------------------------------
		// >>>>>>>>>>>>>>>> MAIN OPEN GL DRAWING LOOP <<<<<<<<<<<<<<<<
		// -----------------------------------------------------------
		new Runnable() {
	        public void run() {	  
				if (glCanvas.isDisposed()) return;
				else{
					if(updateGLView){
						Long startTime = System.nanoTime();
						glCanvas.setCurrent();
						glContext.makeCurrent();
						gl = glContext.getGL ();
						//-------------------------------						
						int width = glCanvas.getBounds().width;
						int height= glCanvas.getBounds().height;
					    gl.glViewport(0, 0, width, height);
					    aspect = (float) width / (float) height;
					    // -------------------------------							
					    gl.glMatrixMode(GL.GL_PROJECTION);
					    gl.glLoadIdentity();
					    glu.gluPerspective(viewing_angle, aspect, 1.0f, 1000.0f); // Perspective view

					    gl.glTranslatef(translation_x, translation_y, dist_from_center);	    
					    gl.glRotatef(rotation_x, 1.0f, 0.0f, 0.0f);
					    gl.glRotatef(rotation_y, 0.0f, 1.0f, 0.0f);
					    gl.glRotatef(rotation_z, 0.0f, 0.0f, 1.0f);
					    // -------------------------------						    
					    gl.glMatrixMode(GL.GL_MODELVIEW);						
						gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
						gl.glLoadIdentity();
						gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
						
						//
						//  TEST Constructive Solid Geometry!
						//
						//GLTests.testCSG(gl);
						//GLTests.testCSG_Flush(gl);
						//
						//  TEST Convexize! (make arbitrary face into convex polygons)
						//
						//GLTests.testConvexize(gl);
						//
						//  TEST Rotations!
						//
						//GLTests.testRotation(gl);
						//
						//  TEST Perimeter Formation!
						//
						//GLTests.testPerimeterFormation(gl);
						
						gl.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
						gl.glLineWidth(2.5f);

						gl.glColor3f(1.0f,0.0f,0.0f);					
						
						
						//
						// Main Drawing routine for the active part
						//
						if(AvoGlobal.project.getActivePart() != null){
							
							Part part = AvoGlobal.project.getActivePart();							
							gl.glLoadIdentity();	// ensure back at identity orientation.
							setMouseMatrixToModelview(); // mouse is at identity unless overridden later by sketch.
							part.glDrawSolid(gl); // draw entire part first...
							part.glDrawSelectedElements(gl); // overlay drawing of seleted elements.
							part.glDrawImportantEdges(gl); // now draw outlines to emphasize important areas. 
							
							gl.glLoadIdentity();	// ensure back at identity orientation.							
							SubPart activeSubPart = AvoGlobal.project.getActiveSubPart();
							// TODO: draw sketches if not consumed and not active
							Iterator<SubPart> subPartIter = part.getSubPartIterator();
							while(subPartIter.hasNext()){
								SubPart subPart = subPartIter.next();
								if(subPart != null && subPart != activeSubPart){
									Sketch sketch = subPart.getSketch();
									if(sketch != null && !sketch.isConsumed){
										// draw the sketch, it is not consumed.
										gl.glPushMatrix();
										sketch.getSketchPlane().glOrientToPlane(gl);
										gl.glLineWidth(2.0f);
										for(int i=0; i < sketch.getFeat2DListSize(); i++){
											Feature2D f2D = sketch.getAtIndex(i);
											if(f2D.isSelected){
									    		gl.glColor4f(	AvoColors.GL_COLOR4_2D_ACTIVE[0], AvoColors.GL_COLOR4_2D_ACTIVE[1],
									    						AvoColors.GL_COLOR4_2D_ACTIVE[2], AvoColors.GL_COLOR4_2D_ACTIVE[3]);
									    		// TODO: HACK, don't build primatives here.. build when created/modified!
									    		f2D.buildPrim2DList();
									    	}else{
									    		gl.glColor4f(	AvoColors.GL_COLOR4_2D_NONACT[0], AvoColors.GL_COLOR4_2D_NONACT[1],
									    						AvoColors.GL_COLOR4_2D_NONACT[2], AvoColors.GL_COLOR4_2D_NONACT[3]);
									    	}
											for(Prim2D prim : f2D.prim2DList){
									    		prim.glDraw(gl);
									    	}
										}									
										gl.glPopMatrix();
										
										
									}
								}
							}							
							
							
							
							// only call glDraw if feat2D3D or feat3D3D is active
							if(activeSubPart != null){
								Sketch sketch = activeSubPart.getSketch();
								if(sketch != null && !sketch.isConsumed){
									// draw active sketch if not consumed
									gl.glPushMatrix();
									sketch.getSketchPlane().glOrientToPlane(gl);
									drawSketchGrid();
									gl.glLineWidth(2.0f);
									for(int i=0; i < sketch.getFeat2DListSize(); i++){
										Feature2D f2D = sketch.getAtIndex(i);
										if(f2D.isSelected){
								    		gl.glColor4f(	AvoColors.GL_COLOR4_2D_ACTIVE[0], AvoColors.GL_COLOR4_2D_ACTIVE[1],
								    						AvoColors.GL_COLOR4_2D_ACTIVE[2], AvoColors.GL_COLOR4_2D_ACTIVE[3]);
								    		// TODO: HACK, don't build primatives here.. build when created/modified!
								    		f2D.buildPrim2DList();
								    	}else{
								    		gl.glColor4f(	AvoColors.GL_COLOR4_2D_NONACT[0], AvoColors.GL_COLOR4_2D_NONACT[1],
								    						AvoColors.GL_COLOR4_2D_NONACT[2], AvoColors.GL_COLOR4_2D_NONACT[3]);
								    	}
										for(Prim2D prim : f2D.prim2DList){
								    		prim.glDraw(gl);
								    	}
									}									
									if(AvoGlobal.menuet.getCurrentToolMode() == Menuet.MENUET_MODE_SKETCH){
										drawTransparentMouseLayer();
										setMouseMatrixToModelview();
									}
									if(mouse_down_button != MOUSE_MIDDLE && 
											mouse_down_button != MOUSE_MIDDLE_SHIFT && 
											mouse_down_button != MOUSE_MIDDLE_CTRL &&
											AvoGlobal.activeToolController != null){
										drawToolEndPos();
									}
									gl.glPopMatrix();
								}
								Build feat2D3D = activeSubPart.getBuild();
								if(feat2D3D != null && feat2D3D.paramSet != null && feat2D3D.paramSet.getToolModel2D3D() != null){
									// request draw of feat2D3D if active
									sketch = feat2D3D.getPrimarySketch();
									if(sketch != null && !sketch.isConsumed){
										// draw regions...
										sketch.getSketchPlane().glOrientToPlane(gl);
										Iterator<Region2D> regIter = sketch.getRegion2DIterator();
										while(regIter.hasNext()){
											Region2D region = regIter.next();
											region.glDrawUnselected(gl, true);				
										}
										// only rebuild if necessary.
										if(buildSolidNeedsRebuilt){
											buildSolidFeat2D3D = feat2D3D.paramSet.getToolModel2D3D().getBuiltSolid(feat2D3D);
											buildSolidNeedsRebuilt = false;
										}
										// draw solid constructed from build operation
										if(buildSolidFeat2D3D != null){
											//turnGLLightsOn(gl);
											buildSolidFeat2D3D.glDrawSolid(gl); //.draw3DFeature(gl, feat2D3D);
										}
										// TODO: HACK, selecting regions seems to break for non XY plane orientations.
										feat2D3D.getPrimarySketch().getSketchPlane().glOrientToPlane(gl);
										setMouseMatrixToModelview();
									}	
								}
							}
						}


						
						glCanvas.swapBuffers(); // double buffering excitement!
						glContext.release();	// go ahead, you can have it back.
						
						Long timeDiff = System.nanoTime() - startTime;
						if(timeDiff > 100e6){
							System.out.println(" *** Time-To-Render Warning!! Render > 100mSec. :: time=" + timeDiff/1e6 + "mSec");
						}
						// TODO: dynamically change RenderLevel based on time to render!
						//System.out.println("Time to render: " + timeDiff);
						
						updateGLView = false;
					}
					Display.getCurrent().timerExec(50, this); // run "this" again in 50mSec.
		        }				
			}
	    }.run();
	
	    AvoGlobal.paramEventHandler.addParamListener(new ParamListener(){
			@Override
			public void paramModified() {
				buildSolidNeedsRebuilt = true;
				updateGLView = true;
			}
			@Override
			public void paramSwitched() {
				buildSolidNeedsRebuilt = true;
				updateGLView = true;
			}	    	
	    });
	    
	}
	
	private void drawTransparentMouseLayer(){
		//
		// invisible plane to catch mouse events at depth ~0.0
		// this should be the last thing drawn in the 2D mode
		// TODO: HACK, this should be sized according to the grid, which should also be dynamic!
		//
		if(AvoGlobal.menuet.getCurrentToolMode() == Menuet.MENUET_MODE_SKETCH ||
				AvoGlobal.menuet.getCurrentToolMode() == Menuet.MENUET_MODE_BUILD){
			gl.glColor4f(1.0f,0.0f,0.0f, 0.0f);
			gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(-100.0f, 100.0f, 0.0f);
				gl.glVertex3f( 100.0f, 100.0f, 0.0f);
				gl.glVertex3f( 100.0f,-100.0f, 0.0f);
				gl.glVertex3f(-100.0f,-100.0f, 0.0f);
			gl.glEnd();
		}
	}
	
	private void drawSketchGrid(){
		// if there is an active sketch, show the gridlines.
		if(AvoGlobal.project.getActiveSketch() != null){
			// disable depth test so that overlapped items at
			// the same depth (in particular, 0.0) still get drawn.
			
			cad_3DXYZ(0.0f,0.0f,0.0f,0.25f);
			
			// gl.glDisable(GL.GL_DEPTH_TEST);
			// set grid color						
			gl.glColor4f(AvoColors.GL_COLOR4_GRID_DARK[0], AvoColors.GL_COLOR4_GRID_DARK[1],
					AvoColors.GL_COLOR4_GRID_DARK[2], AvoColors.GL_COLOR4_GRID_DARK[3]);
			gl.glColor4f(AvoColors.GL_COLOR4_GRID_LIGHT[0], AvoColors.GL_COLOR4_GRID_LIGHT[1],
					AvoColors.GL_COLOR4_GRID_LIGHT[2], AvoColors.GL_COLOR4_GRID_LIGHT[3]);
			// draw grid
			gl.glDisable(GL.GL_LINE_SMOOTH);
			GLDynPrim.mesh(gl, -10.0, 10.0, -10.0, 10.0, 20, 20);
			
			gl.glBegin(GL.GL_LINES);
				gl.glColor4f(	AvoColors.GL_COLOR4_2D_X_AXIS[0], AvoColors.GL_COLOR4_2D_X_AXIS[1], 
								AvoColors.GL_COLOR4_2D_X_AXIS[2], AvoColors.GL_COLOR4_2D_X_AXIS[3]);
				gl.glVertex3d(-10.0, 0.0, 0.0);
				gl.glVertex3d( 10.0, 0.0, 0.0);
				gl.glColor4f(	AvoColors.GL_COLOR4_2D_Y_AXIS[0], AvoColors.GL_COLOR4_2D_Y_AXIS[1], 
								AvoColors.GL_COLOR4_2D_Y_AXIS[2], AvoColors.GL_COLOR4_2D_Y_AXIS[3]);
				gl.glVertex3d(0.0, -10.0, 0.0);
				gl.glVertex3d(0.0,  10.0, 0.0);
			gl.glEnd();
			
			
			gl.glEnable(GL.GL_LINE_SMOOTH);
			gl.glEnable(GL.GL_DEPTH_TEST);
		}
	}
	
	private void drawToolEndPos(){
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		float size = (float)(AvoGlobal.gridSize / 4.0);
		cad_2DCross((float)AvoGlobal.glCursor3DPos[0], (float)AvoGlobal.glCursor3DPos[1], (float)AvoGlobal.glCursor3DPos[2], size);
	}
	
	
	public void cad_3DX(float x, float y, float z, float size){
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x+size, y+size, z+size);
			gl.glVertex3f(x-size, y-size, z-size);
			gl.glVertex3f(x-size, y+size, z+size);
			gl.glVertex3f(x+size, y-size, z-size);			
			gl.glVertex3f(x+size, y-size, z+size);
			gl.glVertex3f(x-size, y+size, z-size);
			gl.glVertex3f(x+size, y+size, z-size);
			gl.glVertex3f(x-size, y-size, z+size);			
		gl.glEnd();		
	}
	
	public void cad_3DXYZ(float x, float y, float z, float size){
		float a = 0.85f;
		float b = 1.0f-a;
		float c = b*0.5f;
		gl.glBegin(GL.GL_LINES);
			gl.glColor4f(	AvoColors.GL_COLOR4_2D_X_AXIS[0], AvoColors.GL_COLOR4_2D_X_AXIS[1], 
							AvoColors.GL_COLOR4_2D_X_AXIS[2], AvoColors.GL_COLOR4_2D_X_AXIS[3]);
			gl.glVertex3f(x+size, y, z);
			gl.glVertex3f(x, y, z);
			gl.glVertex3f(x+size, y, z);
			gl.glVertex3f(x+a*size, y+b*size, z);
			gl.glVertex3f(x+size, y, z);
			gl.glVertex3f(x+a*size, y-b*size, z);
			gl.glColor4f(	AvoColors.GL_COLOR4_2D_Y_AXIS[0], AvoColors.GL_COLOR4_2D_Y_AXIS[1], 
							AvoColors.GL_COLOR4_2D_Y_AXIS[2], AvoColors.GL_COLOR4_2D_Y_AXIS[3]);
			gl.glVertex3f(x, y+size, z);
			gl.glVertex3f(x, y, z);		
			gl.glVertex3f(x, y+size, z);
			gl.glVertex3f(x, y+a*size, z+b*size);
			gl.glVertex3f(x, y+size, z);
			gl.glVertex3f(x, y+a*size, z-b*size);
			gl.glColor4f(	AvoColors.GL_COLOR4_2D_Z_AXIS[0], AvoColors.GL_COLOR4_2D_Z_AXIS[1], 
							AvoColors.GL_COLOR4_2D_Z_AXIS[2], AvoColors.GL_COLOR4_2D_Z_AXIS[3]);			
			gl.glVertex3f(x, y, z+size);
			gl.glVertex3f(x, y, z);		
			gl.glVertex3f(x, y, z+size);
			gl.glVertex3f(x+b*size, y, z+a*size);	
			gl.glVertex3f(x, y, z+size);
			gl.glVertex3f(x-b*size, y, z+a*size);
			gl.glColor4f(	AvoColors.GL_COLOR4_2D_ORIGIN[0], AvoColors.GL_COLOR4_2D_ORIGIN[1], 
							AvoColors.GL_COLOR4_2D_ORIGIN[2], AvoColors.GL_COLOR4_2D_ORIGIN[3]);
			gl.glVertex3f(x+c*size, y+c*size, z+c*size);
			gl.glVertex3f(x-c*size, y+c*size, z+c*size);
			gl.glVertex3f(x-c*size, y+c*size, z+c*size);
			gl.glVertex3f(x-c*size, y-c*size, z+c*size);
			gl.glVertex3f(x-c*size, y-c*size, z+c*size);
			gl.glVertex3f(x+c*size, y-c*size, z+c*size);
			gl.glVertex3f(x+c*size, y-c*size, z+c*size);
			gl.glVertex3f(x+c*size, y+c*size, z+c*size);
			
			gl.glVertex3f(x+c*size, y+c*size, z-c*size);
			gl.glVertex3f(x-c*size, y+c*size, z-c*size);
			gl.glVertex3f(x-c*size, y+c*size, z-c*size);
			gl.glVertex3f(x-c*size, y-c*size, z-c*size);
			gl.glVertex3f(x-c*size, y-c*size, z-c*size);
			gl.glVertex3f(x+c*size, y-c*size, z-c*size);
			gl.glVertex3f(x+c*size, y-c*size, z-c*size);
			gl.glVertex3f(x+c*size, y+c*size, z-c*size);
			
			gl.glVertex3f(x+c*size, y+c*size, z+c*size);
			gl.glVertex3f(x+c*size, y+c*size, z-c*size);
			gl.glVertex3f(x-c*size, y+c*size, z+c*size);
			gl.glVertex3f(x-c*size, y+c*size, z-c*size);
			gl.glVertex3f(x-c*size, y-c*size, z+c*size);
			gl.glVertex3f(x-c*size, y-c*size, z-c*size);
			gl.glVertex3f(x+c*size, y-c*size, z+c*size);
			gl.glVertex3f(x+c*size, y-c*size, z-c*size);
		gl.glEnd();
	}
	
	public void cad_2DCross(float x, float y, float z, float size){
		float sizeB = size*0.7f;
		gl.glLineWidth(1.0f);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x+sizeB, y+sizeB, z);
			gl.glVertex3f(x-sizeB, y-sizeB, z);
			gl.glVertex3f(x-sizeB, y+sizeB, z);
			gl.glVertex3f(x+sizeB, y-sizeB, z);			
		gl.glEnd();	
		gl.glLineWidth(2.25f);
	}

	private void glInit(){
		glContext.makeCurrent();
		gl = glContext.getGL ();
		glu = new GLU();
		
		gl.glClearColor(AvoColors.GL_COLOR4_BACKGND[0],AvoColors.GL_COLOR4_BACKGND[1],
						AvoColors.GL_COLOR4_BACKGND[2],AvoColors.GL_COLOR4_BACKGND[3]);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);		
		gl.glEnable(GL.GL_BLEND);
		gl.glEnable(GL.GL_AUTO_NORMAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		gl.glEnable(GL.GL_COLOR_MATERIAL);	// override material properties, makes coloring easier & faster
		gl.glEnable(GL.GL_LINE_SMOOTH); // smooth rendering of lines
		gl.glClearDepth(1.0);
		gl.glClearStencil(0);
		gl.glLineWidth(2.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_SHADE_MODEL);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glDepthFunc(GL.GL_LEQUAL);
		
		doProceduralShading(gl);
		
		glContext.release();
	
		Device.DEBUG = true;
	}
	
	
	private void turnGLLightsOn(GL gl)
	{
		gl.glEnable(GL.GL_LIGHTING);
		
		float global_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, global_ambient, 0);
		
		gl.glEnable(GL.GL_LIGHT0);
		
		// Create light components
		float ambientLight[] = { 0.2f, 0.2f, 0.2f, 1.0f };
		float diffuseLight[] = { 0.8f, 0.8f, 0.8f, 1.0f };
		float specularLight[] = { 0.5f, 0.5f, 0.5f, 1.0f };
		float position[] = { -5.0f, 5.0f, 5.0f, 1.0f };

		// Assign created components to GL_LIGHT0
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambientLight, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuseLight, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, specularLight, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE); 
		// now all subsequent glColor commands will also effect the lighting colors.
		
		/*
		//light attenuation in function of the vertex distance
		float constantAttenuation = 1.0f;
		float linearAttenuation = 0.2f;
		float quadraticAttenuation = 0.08f;
	    gl.glLightf(GL.GL_LIGHT0, GL.GL_CONSTANT_ATTENUATION, constantAttenuation);
	    gl.glLightf(GL.GL_LIGHT0, GL.GL_LINEAR_ATTENUATION, linearAttenuation);
	    gl.glLightf(GL.GL_LIGHT0, GL.GL_QUADRATIC_ATTENUATION, quadraticAttenuation);
		*/
	}

	private void turnGLLightsOff(GL gl){
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_LIGHT0);
	}
	
	private void doProceduralShading(GL gl){

		// TODO: use FRAGMENT shading for pixel-by-pixel rendering of textures (procedural textures).
		
		String extensions = gl.glGetString(GL.GL_EXTENSIONS);
		/*
		if(extensions.indexOf("GL_ARB_fragment_program") != -1){
			System.out.println("Fragment Program Supported! :)");
		}else{
			System.out.println("Fragment Program NOT supported.. aborting. :(");
			return;
		}	
		
        if ( !gl.isExtensionAvailable("GL_ARB_vertex_shader") && !gl.isExtensionAvailable("GL_ARB_fragment_shader") ) {
            System.out.println("ARB_vertex_shader extension is not supported.");
            System.out.println("ARB_fragment_shader extension is not supported.");
            //System.exit(-1);
        } else if ( gl.isExtensionAvailable("GL_ARB_vertex_shader") && gl.isExtensionAvailable("GL_ARB_vertex_shader") ) {
            System.out.println("ARB_vertex_shader extension is supported continuing!");
            System.out.println("ARB_fragment_shader extension is supported continuing!");
        }
        //*/
        
		/*
		//
		// try to make the shader from Cg :)
		//
		System.out.println("from GLSL source? ");
		int fragGLSLProg = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
		try{
			BufferedReader brf = new BufferedReader(new FileReader("fragmentshader.glsl"));
			String fsrc = "";
			String line;
			while ((line=brf.readLine()) != null) {
				fsrc += line + "\n";
			}
			gl.glShaderSource(fragGLSLProg, 1, new String[] {fsrc}, (int[])null, 0);
			gl.glCompileShader(fragGLSLProg);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int shaderprogram = gl.glCreateProgram();
		gl.glAttachShader(shaderprogram, fragGLSLProg);
		gl.glLinkProgram(shaderprogram);
		gl.glValidateProgram(shaderprogram);

		gl.glUseProgram(shaderprogram);
		
		System.out.println("done with test.");
		// --------
		//*/
		/*
		gl.glEnable(GL.GL_FRAGMENT_PROGRAM_ARB);
		int[] shaderArray = {0};
		gl.glGenProgramsARB(1, shaderArray, 0);
		int fragProg = shaderArray[0];
		
		//int fragProg = gl.glCreateProgramObjectARB();
		gl.glBindProgramARB(GL.GL_FRAGMENT_PROGRAM_ARB, fragProg);
		
		//
		// super simple Fragment Shader.  
		// just place RGB based on XYZ of texture coordinate. :)
		//
		String fragmentAsmString = "!!ARBfp1.0\n\n" + 
			"OUTPUT out_color = result.color; \n" +
			"TEMP t0, t1, t2, t3; \n" +
			"ATTRIB texcoord = fragment.texcoord[0]; \n" +
			"MOV out_color, fragment.color; \n" + 
			"MOV out_color.r, texcoord.x; \n" +
			"MOV out_color.g, texcoord.y; \n" +
			"MOV out_color.b, texcoord.z; \n" +
			"END";		
		gl.glProgramStringARB(GL.GL_FRAGMENT_PROGRAM_ARB, GL.GL_PROGRAM_FORMAT_ASCII_ARB, fragmentAsmString.length(), fragmentAsmString);
		gl.glUseProgramObjectARB(fragProg);
		//gl.glDisable(GL.GL_FRAGMENT_PROGRAM_ARB);
		//*/
		
		/*
		//
		// ----------------- Sample Vertex Program ------------------
		// (don't use unless you handle all lighting/perspective/etc.)
		//
		if(extensions.indexOf("GL_ARB_vertex_program") != -1){
			System.out.println("Vertex Program Supported! :)");
		}else{
			System.out.println("Vertex Program NOT supported.. aborting. :(");
			return;
		}		
		gl.glEnable(GL.GL_VERTEX_PROGRAM_ARB);
		int[] vertShaderArray = {0};
		gl.glGenProgramsARB(1, vertShaderArray, 0);
		int vertProg = vertShaderArray[0];
		
		System.out.println("Vertex Program id: " + vertProg);
		
		//int fragProg = gl.glCreateProgramObjectARB();
		gl.glBindProgramARB(GL.GL_VERTEX_PROGRAM_ARB, vertProg);
		
		String vertString = "!!ARBvp1.0\n\n" + 
		
		//	"TEMP vertexClip, temp; \n" +
		//	"DP4 vertexClip.x, state.matrix.mvp.row[0], vertex.position; \n" +
		//	"DP4 vertexClip.y, state.matrix.mvp.row[1], vertex.position; \n" +
		//	"DP4 vertexClip.z, state.matrix.mvp.row[2], vertex.position; \n" +
		//	"DP4 vertexClip.w, state.matrix.mvp.row[3], vertex.position; \n" + 
		//	"MOV result.position, vertexClip; \n" +
		//	"MOV result.color, vertex.color; \n" + 
		//	"MOV result.texcoord[0], vertex.texcoord; \n" +
		//	"MUL result.color.x, vertex.color.x, vertex.position.x; \n" +
		//	"MUL temp, vertex.position.y, 1.0; \n" + 
		//	"MOV result.color.y, temp; \n" +
			"MOV result.color, vertex.color; \n" +
			"MOV result.texcoord[0], vertex.position; \n" +
		
		//	"ATTRIB col0 = vertex.color; \n" +
		//	"PARAM pink = { 1.0, 0.4, 0.8, 1.0}; \n" + 
		//	"OUTPUT out = result.color; \n" +
			//"MUL out, col0, pink; \n" +
		//	"MOV out, pink; \n" +
			//"TEMP t0, t1, t2; \n" + 
			//"MUL t0, fragment.position, {0.01}\n" + 
			//"SUB result.color.x, 0.5 fragment.position.x; \n" +
			//"SUB result.color.y, 0.5, fragment.position.y; \n" +
			//"SUB result.color.z, 0.5, fragment.position.z; \n" +
			"END";
		int vertLength = vertString.length();
		gl.glProgramStringARB(GL.GL_VERTEX_PROGRAM_ARB, GL.GL_PROGRAM_FORMAT_ASCII_ARB, vertLength, vertString);
		if ( GL.GL_INVALID_OPERATION == gl.glGetError() )
		{
		    // Find the error position
		    int err = gl.glGetError();
		    String s = gl.glGetString(err);
		    System.out.println("ERROR: " + s);
		}else{
			gl.glUseProgramObjectARB(vertProg);
		}
		//*/
		
	}
	
	private void setMouseMatrixToModelview(){
		gl.glGetDoublev( GL.GL_MODELVIEW_MATRIX, mouseModelview, 0 );
	}
	
	public double[] getWorldCoorFromMouse(int mouseX, int mouseY){
		// get mouse coordinates and project them onto the 3D space
		int[] viewport = new int[4];
		//double[] modelview = new double[16];
		double[] projection = new double[16];
		float winX, winY;
		FloatBuffer winZ = FloatBuffer.allocate(4);
		double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords

		//gl.glGetDoublev( GL.GL_MODELVIEW_MATRIX, modelview, 0 );
		gl.glGetDoublev( GL.GL_PROJECTION_MATRIX, projection, 0 );
		gl.glGetIntegerv( GL.GL_VIEWPORT, viewport, 0 );

		//TODO: more precision in XYZ lookup by using doubles instead of floats?
		
		winX = (float)mouseX;
		winY = (float)viewport[3] - (float)mouseY;		

		gl.glReadPixels( mouseX, (int)winY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, winZ );
		glu.gluUnProject((double)winX, (double)winY, (double)(winZ.get()), mouseModelview, 0, projection, 0, viewport, 0, wcoord, 0);
		
        //System.out.println("World coords: (" + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2] + ")");
		
		//
		// Map coord's to snap if snap is enabled
		//
		if(AvoGlobal.snapEnabled && AvoGlobal.menuet.getCurrentToolMode() == Menuet.MENUET_MODE_SKETCH){
			wcoord[0] = Math.floor((wcoord[0]+AvoGlobal.snapSize/2.0)/AvoGlobal.snapSize)*AvoGlobal.snapSize;
			wcoord[1] = Math.floor((wcoord[1]+AvoGlobal.snapSize/2.0)/AvoGlobal.snapSize)*AvoGlobal.snapSize;
			wcoord[2] = Math.floor((wcoord[2]+AvoGlobal.snapSize/2.0)/AvoGlobal.snapSize)*AvoGlobal.snapSize;
		}
		
		if(AvoGlobal.menuet.getCurrentToolMode() == Menuet.MENUET_MODE_SKETCH && wcoord[2] < 0.25 && wcoord[2] > -0.25){
			wcoord[2] = 0.0; // tie z-value to zero if close enough to located on the drawing plane.
		}
		
        AvoGlobal.glCursor3DPos = wcoord;
        AvoGlobal.glViewEventHandler.notifyCursorMoved();
		return wcoord;
	}
	
	
	public void setViewTop(){
		rotation_x = 0.0f;
		rotation_y = 0.0f;
		rotation_z = 0.0f;
		translation_x = 0.0f;
		translation_y = 0.0f;
		updateGLView = true;
	}
	
	public void setViewLeft(){
		rotation_x = -90.0f;
		rotation_y = 0.0f;
		rotation_z = 90.0f;
		translation_x = 0.0f;
		translation_y = 0.0f;
		updateGLView = true;
	}
	
	public void setViewFront(){
		rotation_x = -90.0f;
		rotation_y = 0.0f;
		rotation_z = 0.0f;
		translation_x = 0.0f;
		translation_y = 0.0f;
		updateGLView = true;
	}
	
	public void setViewIso(){
		rotation_x = -45.0f;
		rotation_y = 0.0f;
		rotation_z = 45.0f;
		translation_x = 0.0f;
		translation_y = 0.0f;
		updateGLView = true;
	}
	
	public void setViewSketch(){
		Sketch sketch = AvoGlobal.project.getActiveSketch();
		if(sketch != null){
			System.out.println("GLSketchRotation: (" + sketch.getSketchPlane().getRotationX() + "," +
					sketch.getSketchPlane().getRotationY() + "," +
					sketch.getSketchPlane().getRotationZ() + ")");
			// TODO: orient view to active sketch 
			rotation_x = (float)(sketch.getSketchPlane().getRotationX()*180.0/Math.PI);
			rotation_y = (float)(sketch.getSketchPlane().getRotationY()*180.0/Math.PI);
			rotation_z = (float)(sketch.getSketchPlane().getRotationZ()*180.0/Math.PI);
			translation_x = 0.0f;
			translation_y = 0.0f;
			updateGLView = true;
		}else{
			System.out.println("GLView(setViewSketch): no active sketch, view not being changed.");
		}
		
	}

}
