/*
*
* @file FolderServiceImpl.java
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
* @version $Id: FolderServiceImpl.java,v 1.15 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.internal;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.NoAccountException;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ServiceProvider;

public class FolderServiceImpl extends InternalService implements FolderService {
	FolderServiceImpl(AuthUser user) {
		super(user);
		IFolderManagement folderMgmt = 
			MapperRegistry.getInstance().getFolderManagement();
		folderMgmt.setUser(user);
	}

	public void add(Role role, ExplorerTreeNode toNode)
			throws OperationFailedException {
		if (!toNode.hasRole(role)) {
//			AccessController.checkPermission(Right.WRITE, toNode, user);
			AbstractExplorerTreeNode node = (AbstractExplorerTreeNode) toNode;
			node.add(role);
			try {
				getFolderManagement().update(node);
			} catch (SQLException e) {
				// recover and throw exception
				node.remove(role);
				throw new OperationFailedException(
						"Failed to modify tree node", e);
			}
		}
	}

	public DynamicFolder createDynamicFolder(String name,
			ExplorerTreeNode parent, Hierarchy sourceHierarchy,
			Subset2 sourceSubset, PaloConnection con)
			throws OperationFailedException {
		AccessController.checkPermission(Right.CREATE, user);
		try {
			// do we have an account for it:
			DynamicFolder folder = new DynamicFolder(parent, sourceHierarchy,
					sourceSubset, name);
			folder.setOwner(user);
			// use account
			folder.setConnectionId("");
			// user should get owner role:
			Role ownerRole = (Role) getRoleManagement().findByName("owner");
			if (ownerRole != null) {
				user.add(ownerRole);
			}
			getFolderManagement().insert(folder);
			return folder;
		} catch (SQLException e) {
			throw new OperationFailedException(
					"Failed to create dynamic folder", e);
		}
	}

	public FolderElement createFolderElement(String name,
			ExplorerTreeNode parent, PaloConnection con)
			throws OperationFailedException {
//		AccessController.checkPermission(Right.CREATE, user);
		try {
			// do we have an account for it:
			FolderElement folder = new FolderElement(parent, name);
			folder.setOwner(user);
			// use account
			folder.setConnectionId("");
			// user should get owner role:
			Role ownerRole = (Role) getRoleManagement().findByName("owner");
			if (ownerRole != null) {
				user.add(ownerRole);
			}
			getFolderManagement().insert(folder);
			return folder;
		} catch (SQLException e) {
			throw new OperationFailedException(
					"Failed to create folder element", e);
		}
	}

	public StaticFolder createStaticFolder(String name,
			ExplorerTreeNode parent, PaloConnection con)
			throws OperationFailedException {
		AccessController.checkPermission(Right.CREATE, user);
		return createStaticFolderUnchecked(name, parent, con);
	}
	private StaticFolder createStaticFolderUnchecked(String name,
			ExplorerTreeNode parent, PaloConnection con)
			throws OperationFailedException {
		try {
			// do we have an account for it:
			StaticFolder folder = new StaticFolder(parent, name);
			folder.setOwner(user);
			// use account
			folder.setConnectionId("");
			// user should get owner role:
			Role ownerRole = (Role) getRoleManagement().findByName("owner");
			if (ownerRole != null) {
				user.add(ownerRole);
			}
			getFolderManagement().insert(folder);
			return folder;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create view", e);
		}
	}

	private StaticFolder createStaticFolderUnchecked(String name,
			ExplorerTreeNode parent, String id)
			throws OperationFailedException {
		try {
			// do we have an account for it:
			StaticFolder folder = StaticFolder.internalCreate(parent, id, name);
			folder.setOwner(user);
			// use account
			folder.setConnectionId("");
			// user should get owner role:
			Role ownerRole = (Role) getRoleManagement().findByName("owner");
			if (ownerRole != null) {
				user.add(ownerRole);
			}
			getFolderManagement().insert(folder);
			return folder;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create view", e);
		}
	}

	public void delete(ExplorerTreeNode node) throws OperationFailedException {
		if (!doesTreeNodeExist(node)) {
			return;
		}
		AccessController.checkPermission(Right.DELETE, node, user);
		// no exception, so we can remove the view:
		try {
			for (ExplorerTreeNode n : node.getChildren()) {
				delete(n);
			}
			if (node.getParent() != null) {
				node.getParent().removeChild(node);
			}
			getFolderManagement().delete(node);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete tree node", e);
		}
	}

	public boolean doesTreeNodeExist(ExplorerTreeNode node) {
		return node != null && getTreeNode(node.getId()) != null;
	}

	public ExplorerTreeNode getTreeNode(String id) {
		AccessController.checkPermission(Right.READ, user);
		try {
			return (ExplorerTreeNode) getFolderManagement().find(id);
		} catch (SQLException e) { /* ignore */
		}
		return null;
	}

	private final void getAllViews(ExplorerTreeNode root, Set <String> allViews) {
		if (root instanceof FolderElement) {
			ParameterReceiver pr = ((FolderElement) root).getSourceObject();
			if (pr instanceof CubeView) {
				allViews.add(((CubeView) pr).getId());
			} else if (pr instanceof View) {
				allViews.add(((View) pr).getId());
			}
		}
		for (ExplorerTreeNode nd: root.getChildren()) {
			getAllViews(nd, allViews);
		}
	}
	
	private final void addAllViews(ExplorerTreeNode rootNode) {
		Set <String> allViews = new LinkedHashSet<String>();
		getAllViews(rootNode, allViews);
		
		ExplorerTreeNode targetNode = null;
		
		List<View> existingViews = null;
		try {
			existingViews = getViewManagement().listViews();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		HashSet <String> userCons = new HashSet<String>();
		for (Account a: user.getAccounts()) {
			userCons.add(a.getConnection().getId());
		}
		
		if (existingViews != null) {
			for (View v: existingViews) {
				if (!allViews.contains(v.getId())) {
					String conId = v.getAccount().getConnection().getId();
					if (userCons.contains(conId)) {
						allViews.add(v.getId());
						FolderElement fe;
						try {
							if (targetNode == null) {
								ExplorerTreeNode node = findTreeNode(rootNode, user.getId() + "foreignViews");
								if (node == null) {
									try {
										node = createStaticFolderUnchecked("Recently created views", rootNode, user.getId() + "foreignViews");
									} catch (OperationFailedException e) {
									}
								}
								targetNode = node == null ? rootNode : node;								
							}
							fe = createFolderElement(v.getName(), targetNode, null);
							fe.setSourceObject(v);	
						} catch (OperationFailedException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		}
		
		try {
			save(rootNode);
		} catch (OperationFailedException e) {
			e.printStackTrace();
		}		
	}
	private final ExplorerTreeNode findTreeNode(ExplorerTreeNode root, String id) {
		if (root == null) {
			return null;
		}
		if (root.getId().equals(id)) {
			return root;
		}
		for (ExplorerTreeNode kid: root.getChildren()) {
			ExplorerTreeNode res = findTreeNode(kid, id);
			if (res != null) {
				return res;
			}
		}
		return null;
	}
	
	private final void addOwnerViews(ExplorerTreeNode rootNode) {
		Set <String> allViews = new LinkedHashSet<String>();
		getAllViews(rootNode, allViews);
		
		ExplorerTreeNode targetNode = null;
		HashSet <String> userCons = new HashSet<String>();
		for (Account a: user.getAccounts()) {
			userCons.add(a.getConnection().getId());
		}
		
		List<View> roleViews = null;
		try {
			roleViews = getViewManagement().findViews(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (roleViews != null) {
			for (View v: roleViews) {
				if (!allViews.contains(v.getId())) {
					String conId = v.getAccount().getConnection().getId();
					if (userCons.contains(conId)) {
						allViews.add(v.getId());
						FolderElement fe;
						try {
							if (targetNode == null) {
								ExplorerTreeNode node = findTreeNode(rootNode, user.getId() + "foreignViews");
								if (node == null) {
									try {
										node = createStaticFolderUnchecked("Recently created views", rootNode, user.getId() + "foreignViews");
									} catch (OperationFailedException e) {
									}
								}
								targetNode = node == null ? rootNode : node;
							}
							fe = createFolderElement(v.getName(), targetNode, null);
							fe.setSourceObject(v);	
						} catch (OperationFailedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
//		try {
//			save(rootNode);
//		} catch (OperationFailedException e) {
//			e.printStackTrace();
//		}		
	}
	
	private final void addRoleViews(ExplorerTreeNode rootNode) {
		Set <String> allViews = new LinkedHashSet<String>();
		getAllViews(rootNode, allViews);
		
		ExplorerTreeNode targetNode = null;
		HashSet <Role> allRoles = new HashSet<Role>();
		
		for (Role r: user.getRoles()) {
			allRoles.add(r);
		}
		for (Group g: user.getGroups()) {
			for (Role r: g.getRoles()) {
				allRoles.add(r);
			}
		}
		HashSet <String> userCons = new HashSet<String>();
		for (Account a: user.getAccounts()) {
			userCons.add(a.getConnection().getId());
		}
		
		for (Role r: allRoles) {
			List<View> roleViews = null;
			try {
				roleViews = getViewManagement().findViews(r);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (roleViews != null) {
				for (View v: roleViews) {
					if (!allViews.contains(v.getId())) {
						String conId = v.getAccount().getConnection().getId();
						if (userCons.contains(conId)) {
							allViews.add(v.getId());
							FolderElement fe;
							try {
								if (targetNode == null) {
									ExplorerTreeNode node = findTreeNode(rootNode, user.getId() + "foreignViews");
									if (node == null) {
										try {
											node = createStaticFolderUnchecked("Recently created views", rootNode, user.getId() + "foreignViews");
										} catch (OperationFailedException e) {
										}
									}
									targetNode = node == null ? rootNode : node;
								}
								fe = createFolderElement(v.getName(), targetNode, null);
								fe.setSourceObject(v);	
							} catch (OperationFailedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		try {
			save(rootNode);
		} catch (OperationFailedException e) {
			e.printStackTrace();
		}
	}
		
	public ExplorerTreeNode getTreeRoot() {
		AccessController.checkPermission(Right.READ, user);
		ExplorerTreeNode rootNode = null;
		
		try {
			// do we have an account for it:
			getFolderManagement().getFolders(user);			

			try {
				rootNode = FolderModel.getInstance().load(user);
			} catch (PaloIOException e) {
			}
		} catch (SQLException e) { /* ignore */
			e.printStackTrace();
		}
		if (rootNode == null) {
			String fn = user.getFirstname();
			String ln = user.getLastname();
			String name = ((fn == null ? "" : fn) + " " + (ln == null ? "" : ln))
					.trim();
			if (name.length() == 0) {
				name = user.getLoginName();
			}
			try {
				rootNode = createStaticFolderUnchecked(name, null, (PaloConnection) null);
			} catch (OperationFailedException e) {
			}
		}

		addOwnerViews(rootNode);
		if (ServiceProvider.isAdmin(user)) {
			addAllViews(rootNode);
		} else {
			addRoleViews(rootNode);
		}
		
//		if (roleNodes != null) {
//			for (ExplorerTreeNode nd: roleNodes) {				
//				if (!existsIn(rootNode, nd)) {
//					rootNode.addChild(nd);
//				}
//			}
//		}
		return rootNode;
	}

	public void remove(Role role, ExplorerTreeNode fromNode)
			throws OperationFailedException {
		if (fromNode.hasRole(role)) {
//			AccessController.checkPermission(Right.WRITE, fromNode, user);
			// remove it:
			AbstractExplorerTreeNode node = (AbstractExplorerTreeNode) fromNode;
			node.remove(role);
			try {
				getFolderManagement().update(node);
			} catch (SQLException e) {
				// recover and throw exception
				node.add(role);
				throw new OperationFailedException("Failed to modify node", e);
			}
		}

	}

	public void save(ExplorerTreeNode root) throws OperationFailedException {
//		AccessController.checkPermission(Right.WRITE, root, user);
		try {
			try {
				FolderModel.getInstance().save(user, root);
			} catch (PaloIOException e) {
				throw new OperationFailedException("Failed to save node", e);
			}
			getFolderManagement().update(root);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save node", e);
		}
	}

	public void setName(String name, ExplorerTreeNode ofNode) {
//		AccessController.checkPermission(Right.WRITE, ofNode, user);
		AbstractExplorerTreeNode node = (AbstractExplorerTreeNode) ofNode;
		node.setName(name);
	}

	public void setOwner(User owner, ExplorerTreeNode ofNode) {
//		AccessController.checkPermission(Right.WRITE, ofNode, user);
		((AbstractExplorerTreeNode) ofNode).setOwner(owner);
	}

	private final Account getAccount(PaloConnection forConnection)
			throws SQLException {
		Account account = getAccountManagement().findBy(user, forConnection);
		if (account == null)
			throw new NoAccountException(user, forConnection, "User '"
					+ user.getLastname() + "' has no account on '"
					+ forConnection.getHost() + "'");
		return account;
	}
}
