/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader.folder;

import org.palo.viewapi.internal.ExplorerTreeNode;

public class FolderTraverser {

	public static final void traverse(ExplorerTreeNode folder, FolderVisitor visitor) {
		visitor.visit(folder);
		ExplorerTreeNode[] children = folder.getChildren();
		for(ExplorerTreeNode child : children)
			traverse(child, visitor);		
	}
}
