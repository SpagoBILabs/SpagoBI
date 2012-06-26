/*
*
* @file TestFastTreeDialog.java
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
* @version $Id: TestFastTreeDialog.java,v 1.2 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.viewbrowser;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.ui.model.SimpleTreeNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTree;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.HasFastMSTreeItems;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>TestFastTreeDialog</code> TODO DOCUMENT ME
 * 
 * @version $Id: TestFastTreeDialog.java,v 1.2 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class TestFastTreeDialog extends Window {
	public static final String BUTTON_OK = "apply";
	
	private Button okButton;
	private final XAxisHierarchy hierarchy;
	private final XViewModel xViewModel;
	private SimpleTreeNode root;
	
	public TestFastTreeDialog(XViewModel model, XAxisHierarchy hierarchy) {
		this.hierarchy = hierarchy;
		this.xViewModel = model;
		setClosable(false);
		setCloseAction(CloseAction.CLOSE);
		setHeading("Test Fast Trees");
		setPixelSize(400, 400);
		setModal(true);
		add(createForm());
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
	}


	private FormPanel createForm() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		panel.setCollapsible(false);
		panel.setHeaderVisible(false);
		panel.setSize(386, -1);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setLayout(new FlowLayout());
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setLayout(new FlowLayout());
		cp.setSize(370, 300);
		cp.setScrollMode(Scroll.AUTOY);
		cp.add(createLazyTree());
		panel.add(cp);
		
		// finally the apply/cancel button:
		SelectionListener<ComponentEvent> buttonListener = new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				if (ce.component instanceof Button) {
					Button pressedButton = (Button) ce.component;
					// we close dialog on button press:
					if (closeAction == CloseAction.CLOSE)
						close(pressedButton);
					else
						hide(pressedButton);
				}
			}
		};
		okButton = new Button("Close");
		okButton.setItemId(BUTTON_OK);
		okButton.addSelectionListener(buttonListener);
		panel.addButton(okButton);
		
		return panel;
	}
	
	private Widget createLazyTree() {
		FastMSTree.addDefaultCSS();
		
		FastMSTree t = new FastMSTree(true);
		root = new SimpleTreeNode(null, hierarchy.getType(), hierarchy.getId(), hierarchy.getName(), 1, hierarchy);
		loadChildren(t, root);
		return t;
	}
	
	private void loadChildren(final HasFastMSTreeItems parent, final SimpleTreeNode node) {
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();
		
//		WPaloServiceProvider.getInstance().loadChildren(sessionId, hierarchy.getId(), node, xViewModel, new AsyncCallback <String []>() {
//			public void onSuccess(final String [] nodes) {
//				DeferredCommand.addCommand(new IncrementalCommand() {					
//					private int index = 0;
//					
//					public boolean execute() {						
//						final String dataType = nodes[index];
//						final String dataId   = nodes[index + 1];
//						final String dataName = nodes[index + 2];
//						final int    dataCC   = Integer.parseInt(nodes[index + 3]);
//						
//						FastMSTreeItem item = new FastMSTreeItem(dataName) {
//							public void ensureChildren() {
//								loadChildren(this, new SimpleTreeNode(node, dataType, dataId, dataName, dataCC,
//										new XElementNode(new XElement(dataId, dataName, dataCC > 0 ? XElementType.CONSOLIDATED : XElementType.NUMERIC), hierarchy)));
//							}
//						};
//						if (dataCC > 0) {
//							item.becomeInteriorNode();
//						}
//						
//						parent.addItem(item);
//						index += 4;
//						
//						return index < nodes.length;
//					}
//				});
//			}
//			
//			public void onFailure(Throwable arg0) {
//			}
//		});
	}
}
