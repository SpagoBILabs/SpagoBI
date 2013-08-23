/*
*
* @file ViewImpl.java
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
* @version $Id: ViewImpl.java,v 1.26 2010/02/12 13:51:05 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.util.HashSet;
import java.util.Set;

import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.io.CubeViewIO;
import org.palo.viewapi.services.AdministrationService;


/**
 * <code>View</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewImpl.java,v 1.26 2010/02/12 13:51:05 PhilippBouillon Exp $
 **/
public final class ViewImpl extends GuardedObjectImpl implements View {

	private String xml;
	private String name;
	private String cube;
	private String database;
	private Account account;
	private CubeView cubeView;
	private boolean isBusy = false;
	
	ViewImpl(String id) {
		super(id);
	}
	private ViewImpl(Builder builder) {
		super(builder.id);
		xml = builder.xml;
		name = builder.name;
		owner = builder.owner;
		cube = builder.cube;
		database = builder.database;
		account = builder.account;
		for(Role role : builder.roles)
			add(role);
	}
	
	public final synchronized CubeView createCubeView(AuthUser user, String sessionId) throws PaloIOException {
		if(cubeView == null) {
			while (isBusy) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			isBusy = true;
			try {				
				Cube cube = getPaloCube(sessionId);
				if (cube == null) {
					return null;
				}
				cubeView = CubeViewIO.fromXML(user, this, cube, xml);
			} catch (PaloIOException e) {
				throw e;
			} finally {
				isBusy = false;
//				ConnectionPoolManager.getInstance().disconnect(account);
			}
		}
		return cubeView;
	}
	
	public final CubeView getCubeView() {
		return cubeView;
	}
	
	public final void setCubeView(CubeView cubeView) {
		this.cubeView = cubeView;
	}
	
	public final Account getAccount() {
		return account;
	}

	public final String getCubeId() {
		return cube;
	}

	public final String getDatabaseId() {
		return database;
	}

	public final String getDefinition() {
		return xml != null ? xml : "";
	}
	
	public final String getName() {
		return name != null ? name : "";
	}
	
	public final void setName(String name) {
		this.name = name;
	}

	// Parameters --------------------------------------------------------------
	
	public Object getDefaultValue(String parameterName) {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {
//				return cv.getDefaultValue(parameterName);
//			}
//			return null;
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
		return null;
	}
	
	public String[] getParameterNames() {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {
//				return cv.getParameterNames();
//			}
//			return new String[0];
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
		return new String[0];
	}
	
	public Object getParameterValue(String parameterName) {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {
//				return cv.getParameterValue(parameterName);
//			}
//			return null;
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
		return null;
	}
	
	public boolean isParameterized() {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {
//				return cv.isParameterized();
//			}
//			return false;
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
		return false;
	}
	
	public void setParameter(String parameterName, Object parameterValue) {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {			
//				cv.setParameter(parameterName, parameterValue);
//			}
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
	}
	
	public void addParameterValue(String parameterName, Object parameterValue) {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {
//				cv.addParameterValue(parameterName, parameterValue);
//			}
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
	}
	
	public void setParameterNames(String[] parameterNames) {
//		try {
//			CubeView cv = createCubeView();
//			if (cv != null) {
//				cv.setParameterNames(parameterNames);
//			}
//		} finally {
//			ConnectionPoolManager.getInstance().disconnect(account);
//		}
	}	
	
	public final boolean equals(Object obj) {
		//currently we define two views as equal if they refer to the same
		//connection, same database, same cube and have the same name...
		if (obj instanceof ViewImpl) {
			ViewImpl other = (ViewImpl) obj;
			boolean equal = getId().equals(other.getId()) 
					&& getName().equals(other.name)
					&& account.equals(other.account);
			if(cube != null)
				equal = equal && cube.equals(other.cube);
			else
				equal = equal && other.cube == null;
			if(database != null)
				equal = equal && database.equals(other.database);
			else
				equal = equal && other.database == null;
			return equal;
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 17;
		hc += getId().hashCode();
		hc += getName().hashCode();
		if(cube != null)
			hc += cube.hashCode();
		if(database != null)
			hc += database.hashCode();
		hc += account.hashCode();
		return hc;
	}

	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	public final synchronized void setAccount(AuthUser user, Account account, String sessionId) throws PaloIOException {
		Account oldAccount = this.account;
		this.account = account;	
//		setOwner(account.getUser());
		if (account != null && (oldAccount == null || !account.getId().equals(oldAccount.getId()))) {
			if (cubeView != null) {
				cubeView = null;
				try {
					cubeView = createCubeView(user, sessionId);
				} finally {
					ConnectionPoolManager.getInstance().disconnect(account, sessionId, "ViewImpl.setAccount");
				}
			}
		}
	}
	public final synchronized void setAccount(Account account) {
		this.account = account;			
	}
	
	final void setCube(String id) {
		this.cube = id;
	}
	final void setDatabase(String id) {
		this.database = id;
	}
	final void setDefinition(String xml) {
		setDefinition(xml, true);
	}
	final void setDefinition(String xml, boolean invalidateCubeView) {
		if(invalidateCubeView)
			cubeView = null;
		this.xml = xml;
	}

	private final synchronized Cube getPaloCube(String sessionId) {
		//login to palo! TODO should we cache this login until user logs out??		
		if (!(account instanceof PaloAccount)) {
			return null;
		}
		ServerConnectionPool pool = 
			ConnectionPoolManager.getInstance().getPool(account, sessionId);
		
		org.palo.api.Connection paloConnection = 
			//((PaloAccount) account).login();
			pool.getConnection("ViewImpl.getPaloCube");
		Database db = paloConnection.getDatabaseById(database);
		if (db == null) {
			return null;
		}
		return db.getCubeById(cube);
	}
	
	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private String xml;
		private String name;
		private User owner;
		private String cube;
		private String database;
		private Account account;
		private Set<Role> roles = new HashSet<Role>();
		
		public Builder(String id) {
			AccessController.checkAccess(View.class);
			this.id = id;
		}

		public Builder definition(String xml) {
			this.xml = xml;
			return this;
		}
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		public Builder owner(User owner) {
			this.owner = owner;
			return this;
		}
		public Builder cube(String cube) {
			this.cube = cube;
			return this;
		}
		public Builder database(String database) {
			this.database =database;
			return this;
		}
		public Builder account(Account account) {
			this.account = account;
			return this;
		}
		public Builder add(Role role) {
			roles.add(role);
			return this;
		}
		public View build() {
			return new ViewImpl(this);
		}
	}
}
