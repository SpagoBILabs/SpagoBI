/*
*
* @file AccountImpl.java
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
* @version $Id: AccountImpl.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;

import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;


/**
 * <code>ConnectionAccount</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AccountImpl.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public abstract class AccountImpl extends DomainObjectImpl implements Account {

	private String name;
	private String pass;	
	private String accConnection;	//connection id
	protected Object paloConnection;
	private String user; //user id
	
	AccountImpl(String id, String user) {
		super(id);
		this.user = user;
	}
	protected AccountImpl(Builder builder) {
		super(builder.id);
		user = builder.user;
		name = builder.username;
		pass = builder.pass;
		accConnection = builder.connId;
	}

	private final static Account createAccountImpl(Builder builder) {
		PaloConnection conn = null;
		try {
			conn = (PaloConnection) MapperRegistry.getInstance()
					.getConnectionManagement().find(builder.connId);
		} catch (SQLException e) { /* ignore */
		}
		if (conn == null) {
			return null;
		}
		if (conn.getType() == PaloConnection.TYPE_WSS) {
			return new WSSAccountImpl(builder);
		} else {
			return new PaloAccountImpl(builder);
		}
	}
	
	public final PaloConnection getConnection() {
		// return connection;
		PaloConnection conn = null;
		try {
			conn = (PaloConnection) MapperRegistry.getInstance()
					.getConnectionManagement().find(accConnection);
		} catch (SQLException e) { /* ignore */
		}
		return conn;
	}
	
	public final String getLoginName() {
		return name;
	}
	
	public final String getPassword() {
		return pass;
	}

	public final AuthUser getUser() {
		// return user;
		try {
			User usr = (User) MapperRegistry.getInstance()
					.getUserManagement().find(user);
			return new AuthUserImpl(usr);
		} catch (SQLException e) { /* ignore */
		}
		return null; //TODO is null o.k. here? 
	}

//	public Object login() {
//		if (!isLoggedIn()) {
//			PaloConnection paloConnection = getConnection();
//			if (paloConnection.getType() == PaloConnection.TYPE_WSS) {
//				WSSConnection con = 
//					WSSConnectionFactory.getInstance().newConnection(
//							paloConnection.getHost(),
//							paloConnection.getService());
//				con.login(name, pass);
//				this.paloConnection = con;
//			} else {
//				// configure connection:
//				ConnectionConfiguration cfg = ConnectionFactory.getInstance()
//					.getConfiguration(paloConnection.getHost(),
//							paloConnection.getService());
//				cfg.setUser(name);
//				cfg.setPassword(pass);
//				cfg.setType(paloConnection.getType());
//				// login:
//				this.paloConnection = ConnectionFactory.getInstance()
//					.newConnection(cfg);
//			}
//		}
//		return this.paloConnection;
//	}
//	public final void logout() {
//		if (paloConnection instanceof WSSConnection) {
//			((WSSConnection) paloConnection).logout();
//		} else {
//			((org.palo.api.Connection) paloConnection).disconnect();			
//		}
//		paloConnection = null;
//	}
	
	public boolean isLoggedIn() {
		return paloConnection != null;
	}
	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	final void setConnection(PaloConnection connection) {
		this.accConnection = connection.getId();
	}
	final void setConnection(String id) {
		this.accConnection = id;
	}
	final void setLoginName(String name) {
		this.name = name;
	}
	final void setPassword(String pass) {
		this.pass = pass;
	}
	final void setUser(User user) {
		this.user = user.getId();
		((UserImpl)user).add(this);
	}
	
	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private String username;
		private String pass;	
		private String connId;	//connection id
		private final String user; //user id
		
		public Builder(String id, String user) {
			AccessController.checkAccess(Account.class);
			this.id = id;
			this.user = user;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}
		public Builder password(String pass) {
			this.pass = pass;
			return this;
		}
		public Builder connection(String connId) {
			this.connId = connId;
			return this;
		}
		public Account build() {
			return createAccountImpl(this);
		}
	}

}
