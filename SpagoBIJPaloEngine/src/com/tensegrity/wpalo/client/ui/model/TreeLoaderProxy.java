/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
