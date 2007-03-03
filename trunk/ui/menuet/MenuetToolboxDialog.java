package ui.menuet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ui.animator.Animator;
import ui.paramdialog.ParamComp;
import backend.data.utilities.ImageUtils;
import backend.global.AvoColors;
import backend.global.AvoGlobal;

public class MenuetToolboxDialog {

	public static Composite tboxCompMain;
	public static Composite tboxCompTab;
	public static Composite parentComp;
	
	private static Label tabLabel;
	
	private ScrolledComposite spComp;
	private Composite tboxComp;
	
	private static ToolboxAnim animator;
	
	public static final int TAB_HEIGHT     = 20;
	public static final int TAB_WIDTH      = 125;
	public static final int TAB_TEXT_WIDTH = 100;
	public static final int BORDER_WIDTH   = 3;

	private static int bodyHeight = 300;
	private static int bodyWidth  = 250;

	public MenuetToolboxDialog(Composite comp){
		parentComp = comp;		
		tboxCompMain = new Composite(comp,SWT.NONE);
		tboxCompMain.setBackground(AvoColors.COLOR_TOOLBOX_BG);
		tboxCompMain.setBounds(1,1,bodyWidth,bodyHeight);
		spComp = new ScrolledComposite(tboxCompMain, SWT.V_SCROLL);
		spComp.setBounds(BORDER_WIDTH,BORDER_WIDTH,bodyWidth-2*BORDER_WIDTH,bodyHeight-2*BORDER_WIDTH);
		tboxComp = new Composite(spComp, SWT.NONE);
		tboxComp.setLayout(new RowLayout(SWT.HORIZONTAL));
		spComp.setContent(tboxComp);
	    spComp.setExpandVertical(true);
	    spComp.setExpandHorizontal(true);
		
		
		tboxCompTab = new Composite(comp,SWT.NONE);
		tboxCompTab.setBackground(AvoColors.COLOR_TOOLBOX_BG);	
		tboxCompTab.setBounds(1,1,TAB_WIDTH,TAB_HEIGHT);
		tabLabel = new Label(tboxCompTab, SWT.NONE);
		tabLabel.setText("Toolbox");
		tabLabel.setBounds(BORDER_WIDTH,BORDER_WIDTH,TAB_TEXT_WIDTH-2*BORDER_WIDTH,TAB_HEIGHT-BORDER_WIDTH);
		tabLabel.setBackground(AvoColors.COLOR_TOOLBOX_BG);
		
		int bWidth = TAB_WIDTH-TAB_TEXT_WIDTH-BORDER_WIDTH;
		Button bClose = new Button(tboxCompTab, SWT.PUSH);
		bClose.setBounds(TAB_TEXT_WIDTH,BORDER_WIDTH,bWidth,TAB_HEIGHT-BORDER_WIDTH);
		bClose.setBackground(AvoColors.COLOR_TOOLBOX_BG);
		bClose.setToolTipText("Close");
		bClose.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.drawImage(ImageUtils.getIcon("./toolbox/Cancel.png", 12,12), 5, 3);
			}			
		});
		bClose.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {			
			}
			public void mouseDown(MouseEvent e) {
				// handle "OK" click in paramDialog
				animator.animateBackwards(0);
			}
			public void mouseUp(MouseEvent e) {				
			}			
		});
		bClose.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				// handle "OK" click in paramDialog
				animator.animateBackwards(0);
			}			
		});
		
		animator = new ToolboxAnim(); 
		//buildTBoxComposite();
	}
	
	
	private void buildTBoxComposite(){
		// remove all of the old children from the composite.
		Control[] cList = tboxComp.getChildren();
		for(int i=0; i<cList.length; i++){
			Control c = cList[i];
			if(c instanceof ParamComp){
				((ParamComp)c).removeParamListener();
			}
			c.dispose();
		}
		
		// setup the composite for rowLayout.
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.marginWidth = 2;		
		gl.marginHeight = 2;
		gl.verticalSpacing = 1;
		gl.horizontalSpacing = 0;
		tboxComp.setLayout(gl);
		
		//
		// TODO: add all of the tools in the current mode
		//
		for(int i=0; i<7; i++){
			MTToolComposite cnew = new MTToolComposite(tboxComp, SWT.BORDER, AvoGlobal.menuet.menuetElements[AvoGlobal.menuet.currentToolMode].get(i));
			GridData gd = new GridData();
			gd.grabExcessHorizontalSpace = true;
			gd.widthHint = 300;
			cnew.setLayoutData(gd);
		}
	
		tboxComp.pack();		
        spComp.setMinSize(tboxComp.computeSize(spComp.getClientArea().width-10, SWT.DEFAULT));
	}
	
	/**
	 * move the MenuetToolboxDialog to it's appropriate position
	 * on the screen.. this only should need to be done
	 * if the window is resized.
	 */
	public void positionToolboxDialog(){
		bodyHeight = parentComp.getSize().y-225;
		tboxCompMain.setBounds(1,1,bodyWidth,bodyHeight);
		spComp.setBounds(BORDER_WIDTH,BORDER_WIDTH,bodyWidth-2*BORDER_WIDTH,bodyHeight-2*BORDER_WIDTH);
		animator.setToLastValue();
	}
	
	public void loadToolBoxForCurrentMode(){
		buildTBoxComposite();
		animator.animateForwards(300);
	}
	
	class ToolboxAnim extends Animator{
		public void animatorTransition(float percentComplete) {				
			tboxCompMain.setLocation((int)(percentComplete*bodyWidth)-bodyWidth,parentComp.getBounds().height-bodyHeight-125);
			tboxCompTab.setLocation((int)(percentComplete*bodyWidth)-bodyWidth,parentComp.getBounds().height-bodyHeight-125-TAB_HEIGHT);
			//System.out.println("transistioned @ " +  percentComplete);			
		}		
	}
}
