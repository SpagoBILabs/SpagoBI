/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.google.gwt.user.client.Window;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTree;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;

public class SelectingFastMSTreeItem extends FastMSTreeItem {
	private final FastMSTree tree;
	
	public SelectingFastMSTreeItem(String caption, FastMSTree tree) {
		super(caption);
		this.tree = tree;
	}
	
	protected void afterOpen() {
		if (tree.isListenToStateChange() && isSelected()) {			
			LinkedHashSet <FastMSTreeItem> sels = new LinkedHashSet<FastMSTreeItem>();
			for (FastMSTreeItem it: tree.getSelectedItems()) {
				sels.add(it);
			}
			sels.addAll(getChildren());						
			tree.fastSetSelectedItems(sels);
		}										
	}
	
	private final void collectAll(ArrayList <FastMSTreeItem> roots, LinkedHashSet<FastMSTreeItem> rem) {
		if (roots == null || roots.isEmpty()) {
			return;
		}
		for (FastMSTreeItem i: roots) {
			rem.add(i);
			collectAll(i.getChildren(), rem);
		}
	}
	
	protected void afterClose() {
		if (!tree.isListenToStateChange()) {
			return;
		}
		LinkedHashSet <FastMSTreeItem> kidsToBeRemoved = new LinkedHashSet<FastMSTreeItem>();
		collectAll(getChildren(), kidsToBeRemoved);
		LinkedHashSet <FastMSTreeItem> sels = new LinkedHashSet<FastMSTreeItem>();
		for (FastMSTreeItem it: tree.getSelectedItems()) {
			sels.add(it);
		}
		sels.removeAll(kidsToBeRemoved);
		tree.fastSetSelectedItems(sels);
	}
}
