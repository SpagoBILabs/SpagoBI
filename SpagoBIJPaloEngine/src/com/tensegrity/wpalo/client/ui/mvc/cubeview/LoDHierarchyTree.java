/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.HasFastMSTreeItems;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public class LoDHierarchyTree extends HierarchyTree {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	public LoDHierarchyTree(boolean multiSelect) {
		super(multiSelect);
	}

	protected void loadChildren(final HasFastMSTreeItems parentItem, final TreeNode parentNode) {
		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();
		
		final String hierarchyId;
		final String viewId;
		final String dataObjectType;
		final String dataObjectId;
		if (parentNode.getXObject() instanceof XElementNode) {
			hierarchyId = ((XElementNode) parentNode.getXObject()).getAxisHierarchyId();
			viewId = ((XElementNode) parentNode.getXObject()).getViewId();
			dataObjectType = ((XElementNode) parentNode.getXObject()).getType();
		} else if (parentNode.getXObject() instanceof XAxisHierarchy) {
			hierarchyId = ((XAxisHierarchy) parentNode.getXObject()).getId();
			viewId = ((XAxisHierarchy) parentNode.getXObject()).getViewId();
			dataObjectType = ((XAxisHierarchy) parentNode.getXObject()).getType();
		} else {
			return;
		}
		dataObjectId = parentNode.getXObject().getId();
		if (parentNode.getXObject() instanceof XElementNode) {
			if (((XElementNode) parentNode.getXObject()).getChildCount() >= 100) {
				((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingChildren());
			}
		}
		WPaloServiceProvider.getInstance().loadChildren(sessionId, dataObjectType, xViewModel.getId(), hierarchy.getAxisId(), dataObjectId, parentNode.getPath(), new AsyncCallback <List <TreeNode>>() {
			public void onSuccess(final List <TreeNode> kids) {
				DeferredCommand.addCommand(new IncrementalCommand() {					
					private int index = 0;
					public boolean execute() {		
						if (index >= kids.size()) {
							LoadEvent le = new LoadEvent(null, parentNode);
							tree.loaded(le);
							((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
							return false;
						}

						XObject xObj = kids.get(index).getXObject(); 
						String name;
						String image = "";
						if (xObj instanceof XElementNode) {
							image = "<img paddingTop=\"2px\" width=\"16\" height=\"14\" src=\"icons/element_";
							XElementType xElemType =      
								((XElementNode) xObj).getElement().getElementType();
							if (XElementType.CONSOLIDATED.equals(xElemType)) {
								image += "con2.png\">&nbsp;";
							} else if (XElementType.NUMERIC.equals(xElemType)) {
								image += "num2.png\">&nbsp;";
							} else if (XElementType.STRING.equals(xElemType)) {
								image += "str2.png\">&nbsp;";
							} else {
								image = "";
							}
							int count = ((XElementNode) xObj).getChildCount();
							name = count == 0 ? xObj.getName() : xObj.getName() + " <i><font color=\"gray\">(" + count + ")</i></font>";
							((XElementNode) xObj).setAxisHierarchyId(hierarchyId, viewId);
						} else {
							name = xObj.getName();
						}

						FastMSTreeItem item = new FastMSTreeItem(image + name) {
							public void ensureChildren() {
								loadChildren(this, getModel());
							}
						};
						if (kids.get(index).hasChildren()) {
							item.becomeInteriorNode();
						}
						item.setModel(kids.get(index));
						parentItem.addItem(item);
						if (item.getParentItem() != null &&
								item.getParentItem().getModel() != null) {
							item.getParentItem().getModel().addChild(kids.get(index));
						}
						index++;
						if (item.getParentItem() != null) {
							if (item.getParentItem().getFinishHandler() != null && index >= kids.size()) {
								item.getParentItem().getFinishHandler().onSuccess(null);
							}							
						}
						if (index >= kids.size()) {
							LoadEvent le = new LoadEvent(null, parentNode);
							tree.loaded(le);
							((Workbench)Registry.get(Workbench.ID)).hideWaitCursor();
						}
						return index < kids.size();
					}
				});
			}
			
			public void onFailure(Throwable arg0) {
			}
		});
	}
}
