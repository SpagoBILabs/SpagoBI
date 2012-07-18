/*
*
* @file SelectElementDialog.java
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
* @version $Id: SelectElementDialog.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.DoubleClickListener;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

/**
 * <code>SelectElementMenu</code> TODO DOCUMENT ME
 * 
 * @version $Id: SelectElementDialog.java,v 1.13 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class SelectElementDialog extends Menu {	
	private final SelectElementMenuItem item;
	private final boolean showSelection;
	
	public SelectElementDialog(XViewModel xViewModel, XAxisHierarchy hierarchy, boolean showSelection) {
		this.showSelection = showSelection;
		item = new SelectElementMenuItem(this, xViewModel, hierarchy);
		add(item.getItem());
		setAutoHeight(true);
		DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
	}
	
	boolean isShowSelection() {
		return showSelection;
	}
	
	public void onComponentEvent(ComponentEvent ce) {
		// we have to filter out on click because this event is consumed in
		// private method Menu#onClick(ce) and hence we cannot receive double
		// click events in tree!!!
		if (ce.type == Event.ONCLICK)
			return;
		
		super.onComponentEvent(ce);
	}

	public final void addSelectionListener(Listener<SelectionEvent> listener) {
		item.addSelectionListener(listener);
	}
	public final void removeSelectionListener(Listener<SelectionEvent> listener) {
		item.removeSelectionListener(listener);
	}
}

class SelectElementMenuItem extends LoadListener {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();

	private final HierarchyTree hierarchyTree;
	private final ContentPanel treePanel;
	private final AdapterMenuItem item;
	private final ArrayList<Listener<SelectionEvent>> listeners = new ArrayList<Listener<SelectionEvent>>();
	private final SelectElementDialog dialog;
	private final LayoutContainer treeLayoutContainer;
	
	public SelectElementMenuItem(SelectElementDialog dialog, final XViewModel xViewModel, final XAxisHierarchy hierarchy) {
		this.dialog = dialog;
		treePanel = new ContentPanel();
		treePanel.setScrollMode(Scroll.AUTOY);
		treePanel.setCollapsible(false);
		treePanel.setHeading(constants.doubleClickToSelect());
		treePanel.setSize(210, 200);
		treePanel.setLayout(new FitLayout());
		hierarchyTree = new LoDHierarchyTree(false); //hierarchy);
		hierarchyTree.addLoadListener(this);		

		treeLayoutContainer = new LayoutContainer();
		treeLayoutContainer.setScrollMode(Scroll.AUTO);
		treeLayoutContainer.add(hierarchyTree.getTree());
		treePanel.add(treeLayoutContainer);
		item = new AdapterMenuItem(treePanel);
		item.setHideOnClick(true);
		item.setActiveStyle("select-element");
		initEventHandling();
		((LoDHierarchyTree) hierarchyTree).setInput(hierarchy, xViewModel);	
		hierarchyTree.addLoadListener(new LoadListener(){
			public void loaderLoad(LoadEvent le) {
				hierarchyTree.getTree().setSelectedItems(null);
				hierarchyTree.removeLoadListener(this);
				if (SelectElementMenuItem.this.dialog.isShowSelection()) {
					String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
					String elementId = hierarchy.getSelectedElement() == null ? "" : hierarchy.getSelectedElement().getId();
					WPaloCubeViewServiceProvider.getInstance().getElementPath(sessionId, hierarchy.getId(), hierarchy.getViewId(), hierarchy.getAxisId(), elementId, new AsyncCallback<String>(){
						public void onFailure(Throwable t) {
							t.printStackTrace();
						}

						public void onSuccess(String path) {
							if (path != null && !path.isEmpty()) {
								hierarchyTree.selectBy(path, true);								
							}
						}
					});
				} else {
					((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
				}
			}
		});
//		hierarchyTree.load();
	}

	Item getItem() {
		return item;
	}

	final void addSelectionListener(Listener<SelectionEvent> listener) {
		removeSelectionListener(listener);
		listeners.add(listener);
	}

	final void removeSelectionListener(Listener<SelectionEvent> listener) {
		listeners.remove(listener);
	}
	
	private final void initEventHandling() {
		hierarchyTree.getTree().addDoubleClickListener(new DoubleClickListener() {			
			public void doubleClicked(FastMSTreeItem it) {				
				((Workbench) Registry.get(Workbench.ID)).showWaitCursor(constants.changingSelectedElement(), true);
				if (it != null && it.getModel() != null) {
					TreeNode node = it.getModel();
					if (node.getXObject() instanceof XElement) {
						fireSelectionEvent((XElement) node.getXObject());
					} else if (node.getXObject() instanceof XElementNode) {
						fireSelectionEvent((XElementNode) node.getXObject());
					} 
					item.getParentMenu().hide(true);
				}
			}
		});
	}
	private final void fireSelectionEvent(XElement selection) {
		SelectionEvent event = new SelectionEvent(this, selection);		
		for(Listener<SelectionEvent> listener : listeners)
			listener.handleEvent(event);
	}
	private final void fireSelectionEvent(XElementNode selection) {
		SelectionEvent event = new SelectionEvent(this, selection);		
		for(Listener<SelectionEvent> listener : listeners)
			listener.handleEvent(event);
	}  
	
	  public void loaderLoadException(LoadEvent le) {
		  dialog.hide();
	  }	
}
