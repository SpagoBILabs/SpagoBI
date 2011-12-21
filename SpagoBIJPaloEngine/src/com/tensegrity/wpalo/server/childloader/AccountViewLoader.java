/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;

public class AccountViewLoader implements ChildLoader {
	public boolean accepts(XObject parent) {
		if (Boolean.getBoolean("test.cube.editor"))
			return parent instanceof XAccount;
		return parent instanceof XAccount
				&& parent.getType().equals(XConstants.TYPE_ACCOUNTS_NODE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XAccount account = (XAccount) parent;
//		
//		AuthUser user = 
//			(AuthUser) XObjectMatcher.getNativeObject(account.getUser());
//		if(user == null)
//			return new XObject[0];
//		
//		Account acc = (Account) XObjectMatcher.getNativeObject(parent);
//		if (acc == null) {
//			return new XObject[0];
//		}
//
//		List <XView> allViews = new ArrayList<XView>();
//		for (View v: ServiceProvider.getViewService(user).getViews(acc)) {
//			XView xv = new XView(v.getId(), v.getName());
//			xv.setAccount(account);
//			xv.setCubeId(v.getCubeId());
//			xv.setDatabaseId(v.getDatabaseId());
//			xv.setDefinition(v.getDefinition());
//			allViews.add(xv);
//			XObjectMatcher.put(xv, v);
//		}
//		return allViews.toArray(new XView[0]);
		return new XObject[0];
	}
}
