/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.extjs.gxt.ui.client.widget.tree.TreeItem;

interface TreeItemVisitor {
	/** return <code>true</code> to continue traversing, <code>false</code> otherwise */
	boolean visit(TreeItem item, TreeItem parent);
}
