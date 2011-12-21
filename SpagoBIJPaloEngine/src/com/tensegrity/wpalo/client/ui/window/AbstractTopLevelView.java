/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.window;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;

public abstract class AbstractTopLevelView extends View {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	protected ContentPanel viewpanel;
	protected ContentPanel editorpanel;
	protected ContentPanel viewpanelStatusLine;
	protected ContentPanel editorpanelStatusLine;
//	protected Viewport viewport;
	protected LayoutContainer viewport;
	
	protected AbstractTopLevelView(Controller controller) {
		super(controller);
	}
	
	public final void addToViewPanel(ContentPanel panel) {
		viewpanel.add(panel);
	}
	protected final void createViewPanel() {
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST);
		data.setSplit(true);
		data.setCollapsible(true);
		data.setFloatable(true);
		data.setMinSize(120);
		data.setMargins(new Margins(5, 0, 5, 5));
		data.setSize(200);
		
		viewpanel = new ContentPanel() {			
			public boolean layout() {
				if (viewpanel.isRendered() && viewpanel.isAttached() && !viewpanel.isExpanded()) {
					int size = viewpanel.getParent().getOffsetHeight();
					if (size > 25) {
						viewpanel.setPixelSize(viewpanel.getWidth(), size - 25);
						viewpanel.setHeight(size - 25);
						viewpanel.getLayoutTarget().setHeight(size - 25);
				 	} 
				} 
				return super.layout();
			}
		};
		viewpanel.getState().put("expanded", true);
		viewpanel.saveState();
		viewpanel.setHeading(constants.navigator());		
		viewpanel.setLayoutOnChange(true);
		viewpanel.setMonitorWindowResize(true);
		viewpanel.setLayout(new AccordionLayout());
		viewpanel.getLayout().setExtraStyle("wpalo-overview");
		viewpanel.setExpanded(true);
		viewport.add(viewpanel, data);
	}

	protected final void createEditorPanel(final boolean headerVisible) {
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		data.setMinSize(200);
		data.setMargins(new Margins(5));
		
		editorpanel = new ContentPanel(){
			public void onRender(Element parent, int index) {
				super.onRender(parent, index);
//				if (!headerVisible) {
//					getHeader().el().getParent().removeFromParent();
//				}
			}
		};
		editorpanel.setHeaderVisible(headerVisible);
		editorpanel.setMonitorWindowResize(true);
		editorpanel.setLayoutOnChange(true);
		editorpanel.setLayout(new RowLayout());

		viewport.add(editorpanel, data);		
	}

	protected final void createViewPanelStatusLine(int columns) {
		viewpanelStatusLine = new ContentPanel();
		//no header, just body:
		viewpanelStatusLine.setHeaderVisible(false);
		viewpanelStatusLine.setBorders(true);
		viewpanelStatusLine.setLayoutOnChange(true);		
		//viewpanelStatusLine.setBodyStyle("backgroundColor: #d0def0;");
		// for some reason "RowLayout(Orientation.HORIZONTAL)" doesn't work...
		viewpanelStatusLine.setLayout(new TableLayout(columns));
		viewpanelStatusLine.setBodyStyleName("link-background");				
		viewpanel.setBottomComponent(viewpanelStatusLine);
	}
	
	public ContentPanel getStatusLine() {
		return editorpanelStatusLine;
	}
	
	protected final void createEditorPanelStatusLine(boolean hide) {
		editorpanelStatusLine = new ContentPanel();
		//no header, just body:
		editorpanelStatusLine.setHeaderVisible(false);
		editorpanelStatusLine.setBorders(false); //!hide);
		editorpanelStatusLine.setBodyStyleName("link-background");
		//editorpanelStatusLine.setBodyStyle("backgroundColor: #d0def0;");
//		if (!hide) {
//			editorpanel.setBottomComponent(editorpanelStatusLine);
//		}
	}
}

