/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;

interface FastMSTreeItemVisitor {
	/** return <code>true</code> to continue traversing, <code>false</code> otherwise */
	boolean visit(FastMSTreeItem item, FastMSTreeItem parent);
}
