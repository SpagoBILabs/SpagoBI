/*
*
* @file MapperRegistry.java
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
* @version $Id: MapperRegistry.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Report;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.AdminServiceImpl;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.IAccountManagement;
import org.palo.viewapi.internal.IConnectionManagement;
import org.palo.viewapi.internal.IFolderManagement;
import org.palo.viewapi.internal.IFolderRoleManagement;
import org.palo.viewapi.internal.IGroupManagement;
import org.palo.viewapi.internal.IGroupRoleManagement;
import org.palo.viewapi.internal.IReportManagement;
import org.palo.viewapi.internal.IReportRoleManagement;
import org.palo.viewapi.internal.IReportViewManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IUserGroupManagement;
import org.palo.viewapi.internal.IUserManagement;
import org.palo.viewapi.internal.IUserRoleManagement;
import org.palo.viewapi.internal.IViewManagement;
import org.palo.viewapi.internal.IViewRoleManagement;
import org.palo.viewapi.internal.UserImpl;


/**
 * <code>MapperRegistry</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: MapperRegistry.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public final class MapperRegistry {

	//--------------------------------------------------------------------------
	// ACCESS CONTROLL:
	//
	private static final class AccessChecker extends SecurityManager {
		public Class[] getClassContext() {
			return super.getClassContext();
		}
	}

	public static final void checkAccess(Class<? extends DomainObject> forObj) {
		//get validation class:
		Class<? extends Mapper> mapper = getMapper(forObj);
		Class[] elements = new AccessChecker().getClassContext();
		//we check the call hierarchy:
		int lastCaller = Math.min(10, elements.length);
		boolean accessOk = false;
		for (int i = 0; i < lastCaller; ++i) {
			if (elements[i].equals(mapper)
					|| elements[i].equals(UserImpl.class)
					|| elements[i].equals(DbService.class)
					|| elements[i].equals(AdminServiceImpl.class)) {
				accessOk = true;
				break;
			}
		}
		if (!accessOk)
			throw new IllegalStateException("Illegal access!");
		//TODO better exception message...
		//		=> or an AccessViolationException
	}

	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final MapperRegistry instance = new MapperRegistry();
	public static final MapperRegistry getInstance() {
//		MapperRegistry instance = instances.get(connection);
//		if (instance == null) {
//			instance = new MapperRegistry(connection);
//			instances.put(connection, instance);
////			// first time...
////			try {
////				initialize(connection, instance);
////			} catch (SQLException e) {
////				throw new RuntimeException(
////						"Failed to initialize mapper registry!", e);
////			}
//		}
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCES
	//
	private final UserMapper usrMapper;
	private final RoleMapper roleMapper;
	private final GroupMapper groupMapper;
	private final ViewMapper viewMapper;
	private final FolderMapper folderMapper;
	private final ReportMapper reportMapper;
	private final AccountMapper accountMapper;
	private final ConnectionMapper connectionMapper;
	//associations:
	private final UserRoleAssociation usrRoleAssoc;
	private final UserGroupAssociation usrGroupAssoc;
	private final GroupRoleAssociation grpRoleAssoc;
	private final ViewRoleAssociation viewRoleAssoc;
	private final FolderRoleAssociation folderRoleAssoc;
	private final ReportRoleAssociation repRoleAssoc;
	private final ReportViewAssociation repViewAssoc;
	
	private MapperRegistry() {
		//note the sequence of creation is important since tables are created
		//if necessary!		
		usrMapper = new UserMapper();
		roleMapper = new RoleMapper();
		groupMapper = new GroupMapper();
		connectionMapper = new ConnectionMapper();
		accountMapper = new AccountMapper();
		viewMapper = new ViewMapper();
		folderMapper = new FolderMapper();
		reportMapper = new ReportMapper();
		
		
		//associations:
		grpRoleAssoc = new GroupRoleAssociation();
		usrRoleAssoc = new UserRoleAssociation();
		usrGroupAssoc = new UserGroupAssociation();
		viewRoleAssoc = new ViewRoleAssociation();
		folderRoleAssoc = new FolderRoleAssociation();
		repRoleAssoc = new ReportRoleAssociation();
		repViewAssoc = new ReportViewAssociation();		
	}
	
	public final IAccountManagement getAccountManagement() {
		return accountMapper;
	}
	
	public final IUserManagement getUserManagement() {
		return usrMapper;
	}
	
	public final IRoleManagement getRoleManagement() {
		return roleMapper;
	}
	
	public final IGroupManagement getGroupManagement() {
		return groupMapper;
	}
	
	public final IViewManagement getViewManagement() {
		return viewMapper;
	}
	
	public final IFolderManagement getFolderManagement() {
		return folderMapper;
	}
	
	public final IReportManagement getReportManagement() {
		return reportMapper;
	}

	public final IConnectionManagement getConnectionManagement() {
		return connectionMapper;
	}

	public final IGroupRoleManagement getGroupRoleAssociation() {
		return grpRoleAssoc;
	}
	
	public final IUserRoleManagement getUserRoleAssociation() {
		return usrRoleAssoc;
	}
	public final IUserGroupManagement getUserGroupAssociation() {
		return usrGroupAssoc;
	}
	public final IViewRoleManagement getViewRoleAssociation() {
		return viewRoleAssoc;
	}
	
	public final IFolderRoleManagement getFolderRoleAssociation() {
		return folderRoleAssoc;	
	}
	
	public final IReportRoleManagement getReportRoleAssociation() {
		return repRoleAssoc;
	}
	
	public final IReportViewManagement getReportViewAssociation() {
		return repViewAssoc;		
	}
	
	
	public final void clearCaches() {
		usrMapper.reset();
		roleMapper.reset();
		groupMapper.reset();
		connectionMapper.reset();
		accountMapper.reset();
		viewMapper.reset();
		folderMapper.reset();
		reportMapper.reset();
//		//associations currently have no cache:
//		grpRoleAssoc.reset();
//		usrRoleAssoc.reset();
//		usrGroupAssoc.reset();
//		viewRoleAssoc.reset();
//		repRoleAssoc.reset();
//		repViewAssoc.reset();		
	}
	
	private static final Class<? extends Mapper> getMapper(Class<? extends DomainObject> forObj) {
		if (Account.class.isAssignableFrom(forObj))
			return AccountMapper.class;
		else if (PaloConnection.class.isAssignableFrom(forObj))
			return ConnectionMapper.class;
		else if (Group.class.isAssignableFrom(forObj))
			return GroupMapper.class;
		else if (Report.class.isAssignableFrom(forObj))
			return ReportMapper.class;
		else if (Role.class.isAssignableFrom(forObj))
			return RoleMapper.class;
		else if (User.class.isAssignableFrom(forObj))
			return UserMapper.class;
		else if (AuthUser.class.isAssignableFrom(forObj))
			return UserMapper.class;
		else if (View.class.isAssignableFrom(forObj))
			return ViewMapper.class;
		else if (ExplorerTreeNode.class.isAssignableFrom(forObj)) 
			return FolderMapper.class;
//		else if (IPerson.class.isAssignableFrom(forObj))
//			return PersonMapper.class;

		throw new IllegalStateException("Unkown domain object!");
	}
}
