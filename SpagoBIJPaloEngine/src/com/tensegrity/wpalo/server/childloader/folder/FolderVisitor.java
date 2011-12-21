/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader.folder;

import org.palo.viewapi.internal.ExplorerTreeNode;

public interface FolderVisitor {

	public boolean visit(ExplorerTreeNode folder);
}
