/*
*
* @file AccountMapper.java
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
* @version $Id: AccountMapper.java,v 1.10 2010/02/12 13:51:05 PhilippBouillon Exp $
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

import org.palo.viewapi.Account;
import org.palo.viewapi.DomainObject;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.AccountImpl;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IAccountManagement;
import org.palo.viewapi.internal.AccountImpl.Builder;


/**
 * <code>ConnectionAccountMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountMapper.java,v 1.10 2010/02/12 13:51:05 PhilippBouillon Exp $
 **/
final class AccountMapper extends AbstractMapper implements IAccountManagement {

	private static final String TABLE = DbService.getQuery("Accounts.tableName");
	private static final String COLUMNS = DbService.getQuery("Accounts.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Accounts.createTable", TABLE);
	private static final String FIND_ALL_STMT = DbService.getQuery("Accounts.findAll", COLUMNS, TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Accounts.findById", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_STMT = DbService.getQuery("Accounts.findByName", COLUMNS, TABLE);	
	private static final String FIND_BY_USER_STMT = DbService.getQuery("Accounts.findByUser", COLUMNS, TABLE);
	private static final String FIND_BY_CONNECTION_STMT = DbService.getQuery("Accounts.findByConnection", COLUMNS, TABLE);
	private static final String FIND_BY_USER_CONNECTION_STMT = DbService.getQuery("Accounts.findByUserConnection", COLUMNS, TABLE);
	private static final String FIND_BY_LOGIN_CONNECTION_STMT = DbService.getQuery("Accounts.findByLoginConnection", COLUMNS, TABLE);
	private static final String INSERT_STMT = DbService.getQuery("Accounts.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Accounts.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Accounts.delete", TABLE);
	private static final String DELETE_CONNECTION_STMT = DbService.getQuery("Accounts.deleteConnection", TABLE);

	
	public final Account findBy(String login, PaloConnection connection) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		Account account = null;
		Connection _conn = DbService.getConnection();
		try {
			stmt = _conn.prepareStatement(FIND_BY_LOGIN_CONNECTION_STMT);
			stmt.setString(1, login);
			stmt.setString(2, connection.getId());
			results = stmt.executeQuery();
			if (results.next())
				account = (Account) load(results);
		} finally {
			cleanUp(stmt, results);
		}
		return account;
	}

	
	public final Account findBy(User user, PaloConnection connection) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		Account account = null;
		Connection _conn = DbService.getConnection();
		try {
			stmt = _conn.prepareStatement(FIND_BY_USER_CONNECTION_STMT);
			stmt.setString(1, user.getId());
			stmt.setString(2, connection.getId());
			results = stmt.executeQuery();
			if (results.next())
				account = (Account) load(results);
		} finally {
			cleanUp(stmt, results);
		}
		return account;
	}
	
	public final List<Account> findAll() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<Account> accounts = new ArrayList<Account>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_ALL_STMT);
			results = stmt.executeQuery();
			while(results.next()) { 
				Account account = (Account)load(results);
				if(account != null)
					accounts.add(account);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return accounts;
	}

	public List<Account> getAccounts(User user) throws SQLException {
		if (user == null) {
			return new ArrayList<Account>();
		}
		return getAccounts(user.getId());
	}
	public List<Account> getAccounts(String userId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<Account> accounts = new ArrayList<Account>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_USER_STMT);
			stmt.setString(1,userId);
			results = stmt.executeQuery();
			while(results.next()) { 
				Account account = (Account)load(results);
				if(account != null)
					accounts.add(account);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return accounts;
	}
	
	public List<Account> getAccountsBy(String connectionId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet results = null;
		List<Account> accounts = new ArrayList<Account>();
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_CONNECTION_STMT);
			stmt.setString(1,connectionId);
			results = stmt.executeQuery();
			while(results.next()) { 
				Account account = (Account)load(results);
				if(account != null)
					accounts.add(account);
			}
		} finally {
			cleanUp(stmt, results);
		}
		return accounts;
	}

	public final void update(DomainObject obj) throws SQLException {
		PreparedStatement stmt = null;
		Account acc = (Account) obj;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, acc.getLoginName());
			stmt.setString(2, acc.getPassword());
			stmt.setString(3, acc.getConnection().getId());			
			stmt.setString(4, acc.getUser().getId());
			stmt.setString(5, acc.getId());
			stmt.execute();
		} finally {
			cleanUp(stmt);
		}
	}

	public final void delete(PaloConnection conn) throws SQLException {
		PreparedStatement stmt = null;
		Connection connection = DbService.getConnection();
		try {
			//now the object itself...
			stmt = connection.prepareStatement(DELETE_CONNECTION_STMT);
			stmt.setString(1, conn.getId());
			stmt.execute();
//			results = stmt.executeQuery();
//			while(results.next()) {
//				//TODO have to remove accounts from cache!!!
////				loaded.remove(???);
//			}
		} finally {
			cleanUp(stmt);
		}
	}

	protected final void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		Account acc = (Account) obj;
		stmt.setString(1, acc.getId());
		stmt.setString(2, acc.getLoginName());
		stmt.setString(3, acc.getPassword());
		stmt.setString(4, acc.getConnection().getId());
		stmt.setString(5, acc.getUser().getId());
	}

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		String userId = result.getString(5);
		Builder accBuilder = new AccountImpl.Builder(id, userId);
		accBuilder.username(result.getString(2));
		accBuilder.password(result.getString(3));
		accBuilder.connection(result.getString(4));
		return accBuilder.build();		
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
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
}
