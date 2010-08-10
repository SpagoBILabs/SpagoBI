/*
*
* @file AccountViewLoader.java
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
* @version $Id: AccountViewLoader.java,v 1.10 2009/12/17 16:14:20 PhilippBouillon Exp $
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
