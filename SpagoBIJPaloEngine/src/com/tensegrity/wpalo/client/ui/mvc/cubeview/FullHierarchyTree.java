package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.HasFastMSTreeItems;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public class FullHierarchyTree extends HierarchyTree {

	public FullHierarchyTree(boolean multiSelect) {
		super(multiSelect);
	}

	protected void loadChildren(HasFastMSTreeItems parentItem, final TreeNode parentNode) {
		XUser user = ((Workbench) Registry.get(Workbench.ID)).getUser();
		final String sessionId = user.getSessionId();

		WPaloServiceProvider.getInstance().loadHierarchyTree(sessionId,
				hierarchy.getId(), hierarchy.getViewId(), hierarchy.getAxisId(), -1, new AsyncCallback<List<TreeNode>>() {
					public void onFailure(Throwable arg0) {
						((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
					}

					public void onSuccess(final List<TreeNode> kids) {
						final HashMap<TreeNode, FastMSTreeItem> parents = new HashMap<TreeNode, FastMSTreeItem>();
						DeferredCommand.addCommand(new IncrementalCommand() {
							private int index = 0;
							private int size = kids.size();

							public boolean execute() {
								if (index >= size) {
									LoadEvent le = new LoadEvent(null, parentNode);
//									((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
									tree.loaded(le);
									return false;
								}
								TreeNode tn = kids.get(index);
								XObject xObj = tn.getXObject();

								String name;
								String image = "";
								if (xObj instanceof XElementNode) {
									int count = ((XElementNode) xObj)
											.getChildCount();
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
									name = count == 0 ? xObj.getName() : xObj
											.getName()
											+ " <i><font color=\"gray\">("
											+ count + ")</i></font>";
								} else {
									name = xObj.getName();
								}

								SelectingFastMSTreeItem item = new SelectingFastMSTreeItem(image + name, tree);
								
								if (tn.hasChildren()) {
									item.becomeInteriorNode();
									parents.put(tn, item);
								}
								item.setModel(tn);
								HasFastMSTreeItems parentItem = tree;
								if (tn.getParent() != null) {
									FastMSTreeItem pItem = parents.get(tn.getParent());
									if (pItem != null) {
										parentItem = pItem;
									}
								}
								parentItem.fastAddItem(item);
								index++;
								if (index >= size) {
									LoadEvent le = new LoadEvent(null, parentNode);
//									((Workbench) Registry.get(Workbench.ID)).hideWaitCursor();
									tree.loaded(le);
								}
								return index < size;
							}
						});
					}
				});
	}

}
