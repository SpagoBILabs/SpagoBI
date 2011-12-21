/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.model;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;

public class SimpleTreeLoaderProxy extends RpcProxy <SimpleTreeNode, List <SimpleTreeNode>>{
	private final XAxisHierarchy xAxisHierarchy;
	private final XViewModel xViewModel;
	
	public SimpleTreeLoaderProxy(XAxisHierarchy hierarchy, XViewModel xViewModel) {
		this.xAxisHierarchy = hierarchy;
		this.xViewModel = xViewModel;
	}
	
	protected void load(final SimpleTreeNode loadConfig, final AsyncCallback<List <SimpleTreeNode>> callback) {
		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
	}
}
