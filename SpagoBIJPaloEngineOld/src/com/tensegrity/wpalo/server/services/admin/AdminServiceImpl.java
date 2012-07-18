/*
*
* @file AdminServiceImpl.java
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
* @version $Id: AdminServiceImpl.java,v 1.15 2010/04/12 11:13:36 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.services.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.AuthenticationFailedException;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.IAccountManagement;
import org.palo.viewapi.internal.IFolderManagement;
import org.palo.viewapi.internal.IViewManagement;
import org.palo.viewapi.internal.PaloAccountImpl;
import org.palo.viewapi.internal.ServerConnectionPool;
import org.palo.viewapi.internal.StaticFolder;
import org.palo.viewapi.internal.ViewImpl;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.client.exceptions.SessionExpiredException;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XView;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.AdminService;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.exceptions.DbOperationFailedException;
import com.tensegrity.wpalo.client.services.admin.WPaloAdminService;

/**
 * <code>AdminServiceImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AdminServiceImpl.java,v 1.15 2010/04/12 11:13:36 PhilippBouillon Exp $
 **/
public class AdminServiceImpl extends AdminService implements WPaloAdminService {

	/** generated default serial number */
	private static final long serialVersionUID = -6035121917190713696L;
	
	public XAccount[] getAccounts(String sessionId, XUser xUser) throws SessionExpiredException {
		AuthUser loggedInUser = getLoggedInUser(sessionId);
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(loggedInUser);
		List<XAccount> xAccounts = new ArrayList<XAccount>();
		User forUser = getNativeUser(sessionId, xUser);
		if (forUser != null) {
			for (Account account : adminService.getAccounts(forUser)) {
				XAccount xAccount = (XAccount) XConverter.createX(account);
				xAccounts.add(xAccount);
			}
		}
		return xAccounts.toArray(new XAccount[0]);
	}

	public synchronized XAccount[] listAccounts(String sessionId, XUser xUser) throws SessionExpiredException {
		List<XAccount> xAccounts = new ArrayList<XAccount>();
		IAccountManagement accMgmt = MapperRegistry.getInstance().getAccountManagement();
		try {
			for (Account account : accMgmt.getAccounts(xUser.getId())) {
				XAccount xAccount = (XAccount) XConverter.createX(account);
				xAccounts.add(xAccount);
			}
		} catch (SQLException e) {
		}
		return xAccounts.toArray(new XAccount[0]);
	}

	private final User getNativeUser(String sessionId, XUser xUser) throws SessionExpiredException {
		AuthUser loggedInUser = getLoggedInUser(sessionId);
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(loggedInUser);
		return adminService.getUser(xUser.getId());
	}
	
	public XConnection[] getConnections(String sessionId, XUser user) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		List<XConnection> xConnections = new ArrayList<XConnection>();
		for(PaloConnection connection : adminService.getConnections()) {
			XConnection xConnection = (XConnection)XConverter.createX(connection);
			xConnections.add(xConnection);
		}
		return xConnections.toArray(new XConnection[0]);
		// TODO throw exception or return empty array !?!?!?
	}

	public XGroup[] getGroups(String sessionId, XUser xUser) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		List<XGroup> xGroups = new ArrayList<XGroup>();
		User forUser = getNativeUser(sessionId, xUser);
		if (forUser != null) {
			for (Group group : adminService.getGroups(forUser)) {
				XGroup xGroup = (XGroup) XConverter.createX(group);
				xGroup.clearRoleNames();
				for (Role r: group.getRoles()) {
					xGroup.addRoleName(r.getName());
				}
				xGroups.add(xGroup);
			}
		}
		return xGroups.toArray(new XGroup[0]);
	}

	public XRole[] getRoles(String sessionId, XUser user) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		List<XRole> xRoles = new ArrayList<XRole>();
		for(Role role : adminService.getRoles()) {
			XRole xRole = (XRole)XConverter.createX(role);
			xRoles.add(xRole);
		}
		return xRoles.toArray(new XRole[0]);
	}

	public Boolean [] hasRoles(String sessionId, XUser xuser, String [] roles) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		Boolean [] result = new Boolean[roles.length];
		for (int i = 0; i < roles.length; i++) {
			result[i] = false;
		}
		User user = getNative(sessionId, xuser);
		for (Role r: adminService.getRoles(user)) {
			for (int i = 0; i < roles.length; i++) {
				if (r.getName().equalsIgnoreCase(roles[i])) {
					result[i] = true;
				}
			}
		}
		for (Group g: adminService.getGroups(user)) {
			for (Role r: g.getRoles()) {
				for (int i = 0; i < roles.length; i++) {
					if (r.getName().equalsIgnoreCase(roles[i])) {
						result[i] = true;
					}
				}
			}			
		}
		return result;
	}

	public XUser[] getUsers(String sessionId) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		List<XUser> xUsers= new ArrayList<XUser>();
		for(User usr : adminService.getUsers()) {
			XUser xUser = (XUser)XConverter.createX(usr);
			xUsers.add(xUser);
		}
		return xUsers.toArray(new XUser[0]);
		//TODO throw exception or return empty array !?!?!?
	}

	public Boolean hasAccount(String sessionId, XConnection con) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider.
			getAdministrationService(getLoggedInUser(sessionId));
		for (Account acc: adminService.getAccounts()) {
			if (acc.getConnection() != null && acc.getConnection().getId().equals(con.getId())) {
				return true;
			}
		}
		return false;
	}

	public XObject saveXObject(String sessionId, XObject obj) throws DbOperationFailedException, SessionExpiredException {
		try {
			String type = obj.getType();
			if (type.equals(XAccount.TYPE))
				return save(sessionId, (XAccount) obj);
			else if (type.equals(XConnection.TYPE))
				return save(sessionId, (XConnection) obj);
			else if (type.equals(XGroup.TYPE))
				return save(sessionId, (XGroup) obj);
			else if (type.equals(XRole.TYPE))
				return save(sessionId, (XRole) obj);
			else if (type.equals(XUser.TYPE))
				return save(sessionId, (XUser) obj);
		} catch (OperationFailedException e) {
			e.printStackTrace();
			UserSession usrSession = getUserSession(sessionId);
			
			throw new DbOperationFailedException(
					usrSession.translate("couldNotSave", obj.getName(), e.getLocalizedMessage()), e);
		}
		return null;
	}

	private final void deleteFolder(String sessionId, FolderService folderService, StaticFolder folder) throws OperationFailedException, SessionExpiredException {
		if (folder != null) {
			deleteContentof(sessionId, folder);
			folderService.delete(folder);
		}
	}
	
	private final void deleteContentof(String sessionId, ExplorerTreeNode folder)
			throws OperationFailedException, SessionExpiredException {
		FolderService folderService = ServiceProvider
				.getFolderService(getLoggedInUser(sessionId));
		for (ExplorerTreeNode child : folder.getChildren()) {
			if (child instanceof StaticFolder) {
				deleteFolder(sessionId, folderService, (StaticFolder) child);
			}
			folderService.delete(child);
		}
	}
	
	private final Account findReplaceAccount(String sessionId, User user, Account acc) throws SessionExpiredException {
		if (acc != null) {
			if (acc.getUser() != null) {
				if (acc.getUser().getId().equals(user.getId())) {
					// This account needs to be changed.
					AuthUser admin = getLoggedInUser(sessionId);
					AdministrationService admService = ServiceProvider.getAdministrationService(admin);					
					User admUser = admService.getUser(admin.getId());					
					if (!admUser.getId().equals(user.getId())) {
						for (Account a: admService.getAccounts(admUser)) {
							if (a.getConnection().getId().equals(acc.getConnection().getId())) {
								return a;
							}
						}
					}
					IAccountManagement accountManagement = MapperRegistry.getInstance().getAccountManagement();
					try {
						List <Account> accounts = accountManagement.getAccountsBy(acc.getConnection().getId());
						for (Account a: accounts) {
							if (a.getConnection().getId().equals(acc.getConnection().getId())) {
								if (!a.getUser().getId().equals(user.getId())) {
									return a;
								}
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					return acc;
				}
			}
		}
		return null;
	}
	
	public String [] mayDelete(String sessionId, XObject xObj) throws SessionExpiredException {
		String type = xObj.getType();
		if (type.equals(XUser.TYPE)) {
			User user = getNative(sessionId, (XUser) xObj);
			IViewManagement viewManagement = MapperRegistry.getInstance().getViewManagement();
			try {
				List <View> allViews = viewManagement.findViews(user);				
				List <String> viewNames = new ArrayList<String>();
				if (!allViews.isEmpty()) {										
					for (View v: allViews) {
						viewNames.add(v.getName());
					}
				}
				
				List <String> accountErrors = new ArrayList<String>();
				IAccountManagement accountManagement = MapperRegistry.getInstance().getAccountManagement();
				for (Account a: accountManagement.getAccounts(user.getId())) {
					allViews = viewManagement.findViews(a);
					for (View v: allViews) {
						Account acc = v.getAccount();
						Account replaceAccount = findReplaceAccount(sessionId, user, acc);
						if (replaceAccount == null) {
							accountErrors.add(v.getName());
						}												
					}
				}
				
				if (!accountErrors.isEmpty()) {
					accountErrors.add(0, "_NO_ACCOUNT_ERROR_");
					return accountErrors.toArray(new String[0]);
				}
				return viewNames.toArray(new String[0]);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		} else if (type.equals(XAccount.TYPE)) {
			List <String> accountErrors = new ArrayList<String>();
			Account a = getNative(sessionId, (XAccount) xObj);			
			IViewManagement viewManagement = MapperRegistry.getInstance().getViewManagement();				
			try {
				List <View> allViews = viewManagement.findViews(a);
				for (View v: allViews) {
					Account acc = v.getAccount();
					Account replaceAccount = findReplaceAccount(sessionId, a.getUser(), acc);
					if (replaceAccount == null) {
						accountErrors.add(v.getName());
					}												
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if (!accountErrors.isEmpty()) {
				accountErrors.add(0, "_NO_ACCOUNT_ERROR_");
				return accountErrors.toArray(new String[0]);
			}			
		}
		return new String[0];
	}
	
	public void delete(String sessionId, XObject xObj) throws DbOperationFailedException, SessionExpiredException {
		try {
			String type = xObj.getType();
			if (type.equals(XAccount.TYPE)) {
				Account a = getNative(sessionId, (XAccount) xObj);
				IViewManagement viewManagement = MapperRegistry.getInstance().getViewManagement();				
				try {
					List <View> allViews = viewManagement.findViews(a);
					ViewService viewService = ServiceProvider.getViewService(getLoggedInUser(sessionId));
					try {
						CubeViewReader.CHECK_RIGHTS = false;
						for (View v: allViews) {
							Account acc = v.getAccount();
							Account replaceAccount = findReplaceAccount(sessionId, a.getUser(), acc);
							if (replaceAccount != null && !v.getAccount().getId().equals(replaceAccount.getId())) {
								((ViewImpl) v).setAccount(replaceAccount);
								viewService.save(v);
							}
						}
					} finally {
						CubeViewReader.CHECK_RIGHTS = true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				delete(sessionId, getNative(sessionId, (XAccount) xObj));
			}
			else if (type.equals(XConnection.TYPE))
				delete(sessionId, getNative(sessionId, (XConnection) xObj));
			else if (type.equals(XGroup.TYPE))
				delete(sessionId, getNative(sessionId, (XGroup) xObj));
			else if (type.equals(XRole.TYPE))
				delete(sessionId, getNative(sessionId, (XRole) xObj));
			else if (type.equals(XUser.TYPE)) {
				User user = getNative(sessionId, (XUser) xObj);
				IViewManagement viewManagement = MapperRegistry.getInstance().getViewManagement();
				try {
					try {
						IFolderManagement folders = MapperRegistry.getInstance().
							getFolderManagement();					
						AuthUser aUser = ServiceProvider.getAuthenticationService().authenticateHash(user.getLoginName(),
								user.getPassword());
						List <ExplorerTreeNode> nodes = folders.reallyGetFolders(aUser);
						for (ExplorerTreeNode nd: nodes) {
							folders.delete(nd);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (AuthenticationFailedException e) {
						e.printStackTrace();
					}				
					List <View> allViews = viewManagement.findViews(user);
					ViewService viewService = ServiceProvider.getViewService(getLoggedInUser(sessionId));
					// At this point, the user has agreed to map all his views to admin...
					if (!viewManagement.findViews(user).isEmpty()) {						
						User adminUser = getLoggedInUser(sessionId);
						try {
							CubeViewReader.CHECK_RIGHTS = false;
							for (View v: allViews) {														
								viewService.setOwner(adminUser, v);
								viewService.save(v);
							}
						} finally {
							CubeViewReader.CHECK_RIGHTS = true;
						}
					}
					IAccountManagement accountManagement = MapperRegistry.getInstance().getAccountManagement();
					for (Account a: accountManagement.getAccounts(user.getId())) {
						allViews = viewManagement.findViews(a);
						try {									
							CubeViewReader.CHECK_RIGHTS = false;
							for (View v: allViews) {
								Account acc = v.getAccount();
								Account replaceAccount = findReplaceAccount(sessionId, user, acc);
								if (replaceAccount != null && !v.getAccount().getId().equals(replaceAccount.getId())) {
									((ViewImpl) v).setAccount(replaceAccount);
									viewService.save(v);
								}												
							}
						} finally {
							CubeViewReader.CHECK_RIGHTS = true;
						}
					}												
				} catch (SQLException e) {
					UserSession userSession = getUserSession(sessionId);					
					throw new OperationFailedException(userSession.translate("deletionFailed"));
				}
				delete(sessionId, getNative(sessionId, (XUser) xObj));
			}

		} catch (OperationFailedException e) {
			UserSession userSession = getUserSession(sessionId);
			String message = userSession.translate("unknownReason");
			String type = xObj.getType();
			if (type.equals(XAccount.TYPE)) {
				message = userSession.translate("viewsUsingThisAccount");
			} else if (type.equals(XConnection.TYPE)) {
				message = userSession.translate("accountsUsingThisConnection"); 
			} else if (type.equals(XGroup.TYPE)) {
				message = userSession.translate("usersUsingThisGroup"); 
			} else if (type.equals(XRole.TYPE)) {
				message = userSession.translate("usersOrGroupsUsingThisRole"); 
			} else if (type.equals(XUser.TYPE)) {
				message = userSession.translate("viewsCreatedByThisUser"); 
			}
			throw new DbOperationFailedException(userSession.translate("couldNotDelete", xObj.getName(), message), e);
		}
	}
	
	private final Account getNative(String sessionId, XAccount xAccount) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getAccount(xAccount.getId());
	}

	private final PaloConnection getNative(String sessionId, XConnection xConnection) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getConnection(xConnection.getId());
	}

	private final Group getNative(String sessionId, XGroup xGroup) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getGroup(xGroup.getId());
	}

	private final Role getNative(String sessionId, XRole xRole) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getRole(xRole.getId());
	}

	private final User getNative(String sessionId, XUser xUser) throws SessionExpiredException {
		AdministrationService adminService = ServiceProvider
				.getAdministrationService(getLoggedInUser(sessionId));
		return adminService.getUser(xUser.getId());
	}

	public XUser[] getUsersForConnection(String sessionId, String viewId)
			throws SessionExpiredException {				
		LinkedHashSet <User> users = new LinkedHashSet<User>();
		View v = null;
		try {
			v = (View) MapperRegistry.getInstance().getViewManagement().find(viewId);
		} catch (SQLException e1) {
		}
		if (v != null) {			
			try {
				Account acc = v.getAccount();
				if (acc != null) {
					String conId = acc.getConnection().getId();
					List <Account> allAccounts = MapperRegistry.getInstance().getAccountManagement().getAccountsBy(conId);
					for (Account a: allAccounts) {
						users.add(a.getUser());
					}
				}
			} catch (SQLException e) {
			}
			List <XUser> xUsers = new ArrayList<XUser>();
			for (User usr: users) {
				XUser xUser = (XUser)XConverter.createX(usr);
				xUsers.add(xUser);
			}
			return xUsers.toArray(new XUser[0]);
		}
		return new XUser[0];
	}
	
	public XConnection getConnection(String sessionId, XView view) throws SessionExpiredException {
		View v = null;
		try {
			v = (View) MapperRegistry.getInstance().getViewManagement().find(view.getId());
		} catch (SQLException e1) {
		}
		if (v != null && v.getAccount() != null && v.getAccount().getConnection() != null) {
			PaloConnection con = v.getAccount().getConnection();
			XConnection xCon = (XConnection) XConverter.createX(con);
			return xCon;
		}
		return null;
	}

	public synchronized XCube[] getCubes(String sessionId, XConnection xCon, XDatabase xDb)
			throws SessionExpiredException {
		AuthUser loggedInUser = getLoggedInUser(sessionId);		
		try {
			List <Account> accounts = MapperRegistry.getInstance().getAccountManagement().getAccounts(loggedInUser);
			if (accounts != null && !accounts.isEmpty()) {
				for (Account acc: accounts) {
					if (acc.getConnection().getId().equals(xCon.getId())) {
						if (acc instanceof PaloAccountImpl) {
							ServerConnectionPool pool = ConnectionPoolManager.getInstance().getPool(acc, sessionId);
							Connection con = pool.getConnection("getCubes");
							((PaloAccountImpl) acc).setConnection(con);
							for (Database db: con.getDatabases()) {
								if (db.getId().equals(xDb.getId())) {
									List <XCube> cubes = new ArrayList<XCube>();
									for (Cube c: db.getCubes()) {
										if (c.getType() != Cube.TYPE_ATTRIBUTE &&
											c.getType() != Cube.TYPE_SYSTEM &&
											c.getType() != Cube.TYPE_USER_INFO &&
											!c.getName().startsWith("#")) {
											cubes.add((XCube) XConverter.createX(c));
										}
									}
									ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "getCubes");
									return cubes.toArray(new XCube[0]);
								}
							}
							ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "getCubes");							
						}
					}
				}
			}
		} catch (SQLException e) {
		}
		return null;
	}

	public synchronized XDatabase[] getDatabases(String sessionId, XConnection xCon)
			throws SessionExpiredException, DbOperationFailedException {
		AuthUser loggedInUser = getLoggedInUser(sessionId);		
		try {
			List <Account> accounts = MapperRegistry.getInstance().getAccountManagement().getAccounts(loggedInUser);
			if (accounts != null && !accounts.isEmpty()) {
				for (Account acc: accounts) {
					if (acc.getConnection().getId().equals(xCon.getId())) {
						if (acc instanceof PaloAccountImpl) {
							Connection con = null;
							try {
								ServerConnectionPool pool = ConnectionPoolManager.getInstance().getPool(acc, sessionId);
								 con = pool.getConnection("getDatabases");
								((PaloAccountImpl) acc).setConnection(con);
							} catch (Throwable t) {
								ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "getDatabases");
								throw new DbOperationFailedException(t.getMessage());
							}
							List <XDatabase> databases = new ArrayList<XDatabase>();
							for (Database db: con.getDatabases()) {
								if (db.getType() != Database.TYPE_SYSTEM &&
									db.getType() != Database.TYPE_ATTRIBUTE &&
									db.getType() != Database.TYPE_USER_INFO &&
									!db.getName().startsWith("#")) {
									databases.add((XDatabase) XConverter.createX(db));
								}
							}
							ConnectionPoolManager.getInstance().disconnect(acc, sessionId, "getDatabases");
							return databases.toArray(new XDatabase[0]);
						}
					}
				}				
			}
		} catch (SQLException e) {
		}		
		return null;
	}
}
