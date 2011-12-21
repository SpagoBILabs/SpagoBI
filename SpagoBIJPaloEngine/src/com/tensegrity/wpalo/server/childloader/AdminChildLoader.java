/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.server.childloader;

import java.util.List;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;

/**
 * <code>UserChildLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AdminChildLoader.java,v 1.9 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class AdminChildLoader implements ChildLoader {
	
	public boolean accepts(XObject parent) {
		String type = parent.getType();
		return type.equals(XConstants.TYPE_USERS_NODE)
				|| type.equals(XConstants.TYPE_GROUPS_NODE)
				|| type.equals(XConstants.TYPE_ROLES_NODE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		XNode node = (XNode) parent;
		String type = parent.getType();
		
		AuthUser admin = userSession.getUser();
		if(admin == null)
			return new XObject[0];
				
		AdministrationService adminService =
			ServiceProvider.getAdministrationService(admin);
		
		XObject[] ret;
		if(type.equals(XConstants.TYPE_USERS_NODE)) 
			ret = getUsers(adminService); //.getUsers());
		else if (type.equals(XConstants.TYPE_GROUPS_NODE))
			ret = getGroups(adminService.getGroups());
		else if (type.equals(XConstants.TYPE_ROLES_NODE))
			ret = getRoles(adminService.getRoles());
		else
			ret = new XObject[0];
		
		return ret;
	}


	private final XObject[] getUsers(AdministrationService adminSrv) {
		List<User> users = adminSrv.getUsers();
		XObject[] ret = new XObject[users.size()];
		for (int i = 0; i < ret.length; ++i) {
			User usr = users.get(i);			
			XUser xUser = (XUser) XConverter.createX(usr);
			ret[i] = xUser;
//			// accounts:
//			List<Account> accounts = adminSrv.getAccounts(usr);
//			xUser.setAccounts(WPaloAdminCache.getAccounts(accounts)); //XConverter.createAccounts(accounts, xUser));
//			// groups:
//			List<Group> groups = adminSrv.getGroups(usr);
//			xUser.setGroups(WPaloAdminCache.getGroups(groups)); //XConverter.createGroups(groups));
//			// roles:
//			List<Role> roles = adminSrv.getRoles(usr);
//			xUser.setRoles(WPaloAdminCache.getRoles(roles)); //XConverter.createRoles(roles));
//			ret[i] = xUser;
//			WPaloAdminCache.add(ret[i], usr);
		}
		return ret;
	}
	
	private final XObject[] getGroups(List<Group> groups) {
		XObject[] ret = new XObject[groups.size()];
		for(int i=0;i<ret.length;++i) {
			Group grp = groups.get(i);
			ret[i] = (XGroup) XConverter.createX(grp);
//			ret[i] = (XGroup) WPaloAdminCache.getXObject(grp); //XConverter.createGroup(grp);
//			WPaloAdminCache.add(ret[i], grp);
		}
		return ret;
	}
	
	private final XObject[] getRoles(List<Role> roles) {
		XObject[] ret = new XObject[roles.size()];
		for(int i=0;i<ret.length;++i) {
			Role role = roles.get(i);
			ret[i] = (XRole) XConverter.createX(role);
//			ret[i] = (XRole) WPaloAdminCache.getXObject(role); //XConverter.createRole(role);
//			WPaloAdminCache.add(ret[i], role);
		}
		return ret;
	}
}
