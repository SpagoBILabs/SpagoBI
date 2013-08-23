/*
*
* @file FolderMapper.java
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
* @version $Id: FolderMapper.java,v 1.15 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.AbstractExplorerTreeNode;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.ExplorerTreeNode;
import org.palo.viewapi.internal.IFolderManagement;
import org.palo.viewapi.internal.IFolderRoleManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IUserManagement;
import org.palo.viewapi.internal.AbstractExplorerTreeNode.Builder;
import org.palo.viewapi.services.ServiceProvider;

/**
 * <code>ViewMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: FolderMapper.java,v 1.15 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class FolderMapper extends AbstractMapper implements IFolderManagement {

	private static final String TABLE = DbService.getQuery("Folders.tableName");
	private static final String COLUMNS = DbService.getQuery("Folders.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Folders.createTable", TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Folders.findById", COLUMNS, TABLE);
	private static final String FIND_BY_OWNER_STMT = DbService.getQuery("Folders.findByOwner", COLUMNS, TABLE);
	private static final String FIND_BY_CONNECTION_STMT = DbService.getQuery("Folders.findByConnection", COLUMNS, TABLE);
	private static final String INSERT_STMT = DbService.getQuery("Folders.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Folders.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Folders.delete", TABLE);

	private AuthUser authUser;
	
	public void setUser(AuthUser user) {
		this.authUser = user;
	}
	
	public final List <ExplorerTreeNode> getFolders(Role role) throws SQLException {
		IFolderRoleManagement fr =
			MapperRegistry.getInstance().getFolderRoleAssociation();
		IFolderManagement folderMgmt = 
			MapperRegistry.getInstance().getFolderManagement();		
		List<String> folderIDs = fr.getFolders(role);
		List<ExplorerTreeNode> folders = new ArrayList<ExplorerTreeNode>();
		for(String id : folderIDs) {
			ExplorerTreeNode node = (ExplorerTreeNode)folderMgmt.find(id);
			if(node != null && !folders.contains(node)) {
				folders.add(node);
			} 
		}
		return folders;
	}

	public final List <ExplorerTreeNode> getFolders(AuthUser user, User owner) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection connection = DbService.getConnection();
		List<ExplorerTreeNode> folders = new ArrayList<ExplorerTreeNode>();
		try {
			stmt = connection.prepareStatement(FIND_BY_OWNER_STMT);
			stmt.setString(1, owner.getId());
			results = stmt.executeQuery();			
			while (results.next()) {
				authUser = user;
				ExplorerTreeNode node = (ExplorerTreeNode) load(results);
				if (node != null && !folders.contains(node)) {
					folders.add(node);
				}
			}
			return folders;
		} finally {
			cleanUp(stmt, results);
		}
	}
	
	protected DomainObject load(ResultSet rs) throws SQLException {
		String id = rs.getString(1);
		if (cache.contains(id) && cache.get(id) != null)
			return cache.get(id);
		DomainObject obj = doLoad(id, rs);
		cache.add(obj); //loaded.put(id, obj);
		return obj;
	}
	
	public final List <ExplorerTreeNode> getFolders(AuthUser user) throws SQLException {
		// TODO first run through cache?
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection _conn = DbService.getConnection();
		List<ExplorerTreeNode> folders = new ArrayList<ExplorerTreeNode>();
		try {
			stmt = _conn.prepareStatement(FIND_BY_CONNECTION_STMT);
			//stmt.setString(1, connection.getId());
			results = stmt.executeQuery();
			while (results.next()) {
				// String id = results.getString(1);
				authUser = user;
				ExplorerTreeNode node = (ExplorerTreeNode) load(results);
				if (node != null && !folders.contains(node)) {
					folders.add(node);
				}
			}
			return folders;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		} finally {
			cleanUp(stmt, results);
		}
	}

	public final List <ExplorerTreeNode> reallyGetFolders(AuthUser user) throws SQLException {
		// TODO first run through cache?
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection _conn = DbService.getConnection();
		List<ExplorerTreeNode> folders = new ArrayList<ExplorerTreeNode>();
		try {
			stmt = _conn.prepareStatement(FIND_BY_OWNER_STMT);
			stmt.setString(1, user.getId());
			results = stmt.executeQuery();
			while (results.next()) {
				// String id = results.getString(1);
				authUser = user;
				ExplorerTreeNode node = (ExplorerTreeNode) load(results);
				if (node != null && !folders.contains(node)) {
					folders.add(node);
				}
			}
			return folders;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		} finally {
			cleanUp(stmt, results);
		}
	}

	public final void update(DomainObject obj) throws SQLException {
		ExplorerTreeNode node = (ExplorerTreeNode) obj;
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, node.getName());
			if (node.getOwner() == null) {
				ServiceProvider.getFolderService(authUser).setOwner(
						authUser, node);
			}
			stmt.setString(2, node.getOwner().getId());
			stmt.setString(3, "" + node.getType());
			stmt.setString(4, node.getId());
			stmt.execute();			
			handleAssociations(node);
		} finally {
			cleanUp(stmt);
		}
	}


	
	protected void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		ExplorerTreeNode node = (ExplorerTreeNode) obj;
		stmt.setString(1, node.getId());
		stmt.setString(2, node.getName());
		stmt.setString(3, node.getOwner().getId());
		stmt.setString(4, "" + node.getType());
		
		handleAssociations(node);
	}
	
	public synchronized void insert(DomainObject obj) throws SQLException {
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			String ins = insertStatement();
			stmt = connection.prepareStatement(ins);
			doInsert(obj, stmt);
			stmt.execute();
			cache.add(obj); //loaded.put(obj.getId(), obj);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			cleanUp(stmt);
		}				
	}	

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		Builder folderBuilder = new AbstractExplorerTreeNode.Builder(id);
		folderBuilder.name(result.getString(2));
		//OWNER
		IUserManagement usrMgmt = MapperRegistry.getInstance().getUserManagement();
		folderBuilder.owner((User)usrMgmt.find(result.getString(3)));
		folderBuilder.type(result.getString(4));
		//CONNECTION
		folderBuilder.connection(null);
		//ROLES
		IRoleManagement roleMgmt = 
			MapperRegistry.getInstance().getRoleManagement();
		IFolderRoleManagement frAssoc = 
			MapperRegistry.getInstance().getFolderRoleAssociation();
		//saved roles:
		List<String> roles = frAssoc.getRoles(id);
		for(String roleId : roles) {
			Role role = (Role) roleMgmt.find(roleId);
			if(role != null)
				folderBuilder.add(role);
		}

		ExplorerTreeNode obj = folderBuilder.build(authUser);
		return obj;
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
		ExplorerTreeNode node = (ExplorerTreeNode) obj;

		//remove associated roles
		IFolderRoleManagement frAssoc = 
			MapperRegistry.getInstance().getFolderRoleAssociation();
		
		frAssoc.delete(node);
	}
	
	protected final String deleteStatement() {
		return DELETE_STMT;
	}

	protected final String findStatement() {
		return FIND_BY_ID_STMT;
	}

	protected final String insertStatement() {
		return INSERT_STMT;
	}
	
	protected final String createTableStatement() {
		return CREATE_TABLE_STMT;
	}
	
	protected final String getTableName() {
		return TABLE;
	}

	private final void handleAssociations(ExplorerTreeNode node) throws SQLException {
		//ASSOCIATED ROLES
		IRoleManagement roleMgmt = 
			MapperRegistry.getInstance().getRoleManagement();
		IFolderRoleManagement frAssoc = 
			MapperRegistry.getInstance().getFolderRoleAssociation();
		//already saved roles:
		List<String> savedRoles = frAssoc.getRoles(node);
		List<String> roles = ((AbstractExplorerTreeNode)node).getRoleIDs();
		for(String id : roles) {
			if(!savedRoles.contains(id))
				frAssoc.insert(node, roleMgmt.find(id));
		}
		//remove any deleted ones:
		savedRoles.removeAll(roles);
		for(String id: savedRoles)
			frAssoc.delete(node, roleMgmt.find(id));
	}

	protected String findByNameStatement() {
		throw new IllegalArgumentException("This method is not supported for folders.");
	}
}
