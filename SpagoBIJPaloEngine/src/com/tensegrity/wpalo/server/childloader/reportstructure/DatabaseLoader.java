/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader.reportstructure;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.palo.XServer;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.server.childloader.ChildLoader;

public class DatabaseLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent instanceof XAccount &&
			parent.getType().equals(XServer.TYPE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XAccount node = (XAccount) parent;
//		XUser xUser = node.getUser();
//		AuthUser user = (AuthUser) XObjectMatcher.getNativeObject(xUser);
//		if (user == null) {
//			return null;
//		}
//		Account acc = (Account) XObjectMatcher.getNativeObject(node);
//		if (acc == null || !(acc instanceof PaloAccount)) {
//			return null;
//		}
//		
//		List <XDatabase> allDatabases = new ArrayList<XDatabase>();
//		Connection con = ((PaloAccount) acc).login();
//		for (Database db: con.getDatabases()) {
//			if (db.isSystem()) {
//				continue;
//			}
//			XDatabase xdb = (XDatabase)XConverter.createX(db);
////			new XDatabase(db.getId(), db.getName(), 
////					db.getDimensionCount() > 0, xUser);
////			xdb.setType(XConstants.TYPE_DATABASE_NO_CUBES);
//			XObjectMatcher.put(xdb, db);
//			allDatabases.add(xdb);
//		}
//		return allDatabases.toArray(new XDatabase[0]);
		return new XObject[0];
	}
}
