/*
*
* @file AbstractTopLevelView.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: AbstractTopLevelView.java,v 1.19 2010/03/02 08:59:12 PhilippBouillon Exp $
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

