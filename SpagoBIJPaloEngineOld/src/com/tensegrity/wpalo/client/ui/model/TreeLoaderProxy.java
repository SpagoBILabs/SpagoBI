/*
*
* @file TreeLoaderProxy.java
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
* @version $Id: TreeLoaderProxy.java,v 1.12 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolder;
import com.tensegrity.wpalo.client.WPaloServiceProvider;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public class TreeLoaderProxy extends RpcProxy <TreeNode, List <TreeNode>>{
	
	protected void load(TreeNode loadConfig, AsyncCallback<List <TreeNode>> callback) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
		if (loadConfig.getXObject() instanceof XFolder) {
			List <TreeNode> kids = loadConfig.getChildren();
			List <TreeNode> nodeKids = new ArrayList<TreeNode>();
			for (TreeModel m: kids) {
				if (m instanceof TreeNode) {
					nodeKids.add((TreeNode) m);
				}
			}
			callback.onSuccess(nodeKids);
			return;
		}
		WPaloServiceProvider.getInstance().loadChildren(sessionId, loadConfig, callback);
	}
//
//	@Override
//	protected void load(TreeNode loadConfig,
//			AsyncCallback<List<TreeNode>> callback) {
//		// TODO Auto-generated method stub
//		
//	}
}
