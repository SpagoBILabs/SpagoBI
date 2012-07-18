package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.LinkedHashSet;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.tensegrity.wpalo.client.i18n.ILocalConstants;
import com.tensegrity.wpalo.client.i18n.Resources;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

class LocalFilterSelectionFilter implements Filter {
	protected transient final ILocalConstants constants = Resources.getInstance().getConstants();
	
	final LinkedHashSet <String> allPaths = new LinkedHashSet<String>();
	final LinkedHashSet <String> allPrefixes = new LinkedHashSet<String>();
	final TreeNodeSelector selector;
	int size;
	private boolean reachedFirstSelectedElement = false;
	
	LocalFilterSelectionFilter(String paths, TreeNodeSelector selector) {
		this.selector = selector;
		if (paths != null) {
			String currentPath = "";
			for (int i = 0; i < paths.length(); i++) {
				char c = paths.charAt(i);
				if (c == ',') {
					if (currentPath.length() != 0) {
						allPaths.add(currentPath);
						addAllPrefixes(currentPath);
						currentPath = "";
					}
				} else {
					currentPath += c;
				}
			}
		}
		size = allPaths.size();
		((Workbench)Registry.get(Workbench.ID)).showWaitCursor(constants.loadingSelectingChildren());
	}
		
	private final void addAllPrefixes(String p) {
		int l = p.length();
		if (l < 2) {
			return;
		}
		int lastCol = p.lastIndexOf(":", l - 2);
		if (lastCol == -1) {
			return;
		}
		String prefix = p.substring(0, lastCol + 1);
		allPrefixes.add(prefix);
		addAllPrefixes(prefix);
	}
	
	public boolean filter(final FastMSTreeItem item) {
		final TreeNode tNode = item.getModel();
		if (tNode != null && size > 0) {
			String path = tNode.getPath();
			if (allPaths.contains(path)) {
				selector.select(item, true);
				if (!reachedFirstSelectedElement) {
					reachedFirstSelectedElement = true;
					DeferredCommand.addCommand(new Command(){
						public void execute() {
							selector.getTree().getTree().ensureItemVisible(item);
						}
					});																					
				}
				allPaths.remove(path);
				size--;
			}
		}
			
		return true;
	}

	public boolean shouldExpand(FastMSTreeItem item) {
		if (item.isLeafNode()) {
			return false;
		}
		TreeNode tNode = item.getModel();
		if (tNode != null && size > 0) {
			String path = tNode.getPath();
			if (allPrefixes.contains(path)) {
				return true;
			}
//			for (String p : allPaths) {
//				if (p.indexOf(path) != -1 && !p.equals(path)) {
//					return true;
//				}
//			}
		}
		return false;			
	}

	public boolean traverseChild(TreeNode child) {
		if (size > 0) {
			String path = child.getPath();
			boolean result = allPaths.contains(path);
			if (!result && child.hasChildren() && size > 0) {
				if (allPrefixes.contains(path)) {
					return true;
				}
//				for (String p : allPaths) {
//					if (p.indexOf(path) != -1) {
//						return true;
//					}
//				}
			}
			return result;
		}	
		return false;
	}	
}