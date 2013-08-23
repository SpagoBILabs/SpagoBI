/*
*
* @file ViewMapper.java
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
* @version $Id: ViewMapper.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
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

import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.viewapi.Account;
import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IAccountManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IUserManagement;
import org.palo.viewapi.internal.IViewManagement;
import org.palo.viewapi.internal.IViewRoleManagement;
import org.palo.viewapi.internal.ViewImpl;
import org.palo.viewapi.internal.ViewImpl.Builder;

/**
 * <code>ViewMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewMapper.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class ViewMapper extends AbstractMapper implements IViewManagement {

	private static final String TABLE = DbService.getQuery("Views.tableName");
	private static final String COLUMNS = DbService.getQuery("Views.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Views.createTable", TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Views.findById", COLUMNS, TABLE);
	private static final String FIND_BY_OWNER_STMT = DbService.getQuery("Views.findByOwner", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_STMT = DbService.getQuery("Views.findByName", COLUMNS, TABLE);
	private static final String FIND_BY_ACCOUNT_STMT = DbService.getQuery("Views.findByAccount", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_CUBE_ACCOUNT_STMT = DbService.getQuery("Views.findByNameCubeAccount", COLUMNS, TABLE);
	private static final String FIND_ALL_STMT = DbService.getQuery("Views.findAll", COLUMNS, TABLE);
	
	private static final String INSERT_STMT = DbService.getQuery("Views.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Views.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Views.delete", TABLE);

	
	public final View findByName(String name, Cube cube, Account account)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection connection = DbService.getConnection();
		try {
			View view = null;
			Database db = cube.getDatabase();
			stmt = connection
					.prepareStatement(FIND_BY_NAME_CUBE_ACCOUNT_STMT);
			stmt.setString(1, name);
			stmt.setString(2, db.getId());
			stmt.setString(3, cube.getId());
			stmt.setString(4, account.getId());
			results = stmt.executeQuery();
			if (results.next()) {
				view = (View) load(results);
			}
			return view;
		} finally {
			cleanUp(stmt, results);
		}
	}
	
	public final List<View> findViews(Role role) throws SQLException {
		IViewRoleManagement vr = 
			MapperRegistry.getInstance().getViewRoleAssociation();
		IViewManagement viewMgmt = 
			MapperRegistry.getInstance().getViewManagement();
		List<String> viewIDs = vr.getViews(role);
		List<View> views = new ArrayList<View>();
		for(String id : viewIDs) {
			View view = (View)viewMgmt.find(id);
			if(view != null && !views.contains(view))
				views.add(view);
		}
		return views;
	}

	public final List<View> findViews(User owner) throws SQLException {
		//TODO first run through cache?
		List<View> views = new ArrayList<View>();
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_OWNER_STMT);
			stmt.setString(1, owner.getId());
			results = stmt.executeQuery();
			while (results.next()) {
				View view = (View) load(results);
				if (!views.contains(view))
					views.add(view);
			}
			return views;
		} finally {
			cleanUp(stmt, results);
		}
	}

	public final List<View> listViews() throws SQLException {
		List<View> views = new ArrayList<View>();
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_ALL_STMT);
			results = stmt.executeQuery();
			while (results.next()) {
				View view = (View) load(results);
				if (!views.contains(view))
					views.add(view);
			}
			return views;
		} finally {
			cleanUp(stmt, results);
		}
	}

	public final List<View> findViews(Account account) throws SQLException {
		List<View> views = new ArrayList<View>();
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection _conn = DbService.getConnection();
		try {
			stmt = _conn.prepareStatement(FIND_BY_ACCOUNT_STMT);
			stmt.setString(1, account.getId());
			results = stmt.executeQuery();
			while (results.next()) {
				View view = (View) load(results);
				if (!views.contains(view))
					views.add(view);
			}
			return views;
		} finally {
			cleanUp(stmt, results);
		}
	}
	
	public final boolean hasViews(Account account) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection _conn = DbService.getConnection();
		try {
			stmt = _conn.prepareStatement(FIND_BY_ACCOUNT_STMT);
			stmt.setString(1, account.getId());
			results = stmt.executeQuery();
			return results.next();
		} finally {
			cleanUp(stmt, results);
		}
	}

	public final void update(DomainObject obj) throws SQLException {
		View view = (View) obj;
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, view.getName());
			stmt.setString(2, view.getOwner().getId());
			stmt.setString(3, view.getDefinition());
			stmt.setString(4, view.getDatabaseId());
			stmt.setString(5, view.getCubeId());
			stmt.setString(6, view.getAccount().getId());
			stmt.setString(7, view.getId());
			stmt.execute();			
			handleAssociations(view);
		} finally {
			cleanUp(stmt);
		}
	}


	
	protected void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		View view = (View) obj;
		stmt.setString(1, view.getName());
		stmt.setString(2, view.getOwner().getId());
		stmt.setString(3, view.getDefinition());
		stmt.setString(4, view.getDatabaseId());
		stmt.setString(5, view.getCubeId());
		stmt.setString(6, view.getAccount().getId());
		
		handleAssociations(view);
	}

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		Builder viewBuilder = new ViewImpl.Builder(id);
		viewBuilder.name(result.getString(2));
		//OWNER
		IUserManagement usrMgmt = MapperRegistry.getInstance().getUserManagement();
		viewBuilder.owner((User)usrMgmt.find(result.getString(3)));
		viewBuilder.definition(result.getString(4));
		viewBuilder.database(result.getString(5));
		viewBuilder.cube(result.getString(6));
		//ACCOUNT
		IAccountManagement accMgmt = 
			MapperRegistry.getInstance().getAccountManagement();
		viewBuilder.account((Account)accMgmt.find(result.getString(7)));
		//ROLES
		IRoleManagement roleMgmt = 
			MapperRegistry.getInstance().getRoleManagement();
		IViewRoleManagement vrAssoc = 
			MapperRegistry.getInstance().getViewRoleAssociation();
		//saved roles:
		List<String> roles = vrAssoc.getRoles(id);
		for(String roleId : roles) {
			Role role = (Role) roleMgmt.find(roleId);
			if(role != null)
				viewBuilder.add(role);
		}

		return viewBuilder.build();
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
		View view = (View) obj;		
		//remove associated roles
		IViewRoleManagement vrAssoc = 
			MapperRegistry.getInstance().getViewRoleAssociation();
		vrAssoc.delete(view);
	}
	
	protected final String deleteStatement() {
		return DELETE_STMT;
	}

	protected final String findStatement() {
		return FIND_BY_ID_STMT;
	}
	protected final String findByNameStatement() {
		return FIND_BY_NAME_STMT;
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

	private final void handleAssociations(View view) throws SQLException {
		//ASSOCIATED ROLES
		IRoleManagement roleMgmt = 
			MapperRegistry.getInstance().getRoleManagement();
		IViewRoleManagement vrAssoc = 
			MapperRegistry.getInstance().getViewRoleAssociation();
		//already saved roles:
		List<String> savedRoles = vrAssoc.getRoles(view);
		List<String> roles = ((ViewImpl)view).getRoleIDs();
		for(String id : roles) {
			if(!savedRoles.contains(id))
				vrAssoc.insert(view, roleMgmt.find(id));
		}
		//remove any deleted ones:
		savedRoles.removeAll(roles);
		for(String id : savedRoles)
			vrAssoc.delete(view, roleMgmt.find(id));
	}
}
