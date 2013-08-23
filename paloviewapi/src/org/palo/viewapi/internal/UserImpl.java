/*
*
* @file UserImpl.java
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
* @version $Id: UserImpl.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.palo.viewapi.Account;
import org.palo.viewapi.Group;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;

import sun.misc.BASE64Encoder;

/**
 * <code>UserImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: UserImpl.java,v 1.11 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class UserImpl extends DomainObjectImpl implements User {

	private String firstname;
	private String lastname;
	private String login; //login name...
	private String password;
//	protected Authentication authentication;
	protected final Set<String> roles = new LinkedHashSet<String>();
	protected final Set<String> groups = new LinkedHashSet<String>();
	protected final Set<Account> accounts = new LinkedHashSet<Account>();

	
	UserImpl(String id) {
		super(id);
	}
	UserImpl(UserImpl user) {
		super(user.getId());
		firstname = user.firstname;
		lastname = user.lastname;
		login = user.login;
		password = user.password; 
//		authentication = user.authentication;
		roles.addAll(user.roles);
		groups.addAll(user.groups);
		accounts.addAll(user.accounts);
	}
	private UserImpl(Builder builder) {
		super(builder.id);
		firstname = builder.firstname;
		lastname = builder.lastname;
		login = builder.login;
		password = builder.password; 
//		authentication = builder.authentication;
		roles.addAll(builder.roles);
		groups.addAll(builder.groups);
		accounts.addAll(builder.accounts);
	}
	
	public final String getFirstname() {
		return firstname;
	}
	public final String getLastname() {
		return lastname;
	}
	public final String getLoginName() {
		return login != null ? login : "";
	}
	public final String getPassword() {
		return password;
	}

	public final boolean isMemberOf(Group group) {
		return groups.contains(group.getId());
	}

	private final List<Group> getGroupList() {
		IGroupManagement groupMgmt = 
			MapperRegistry.getInstance().getGroupManagement();
		List<Group> groups = new ArrayList<Group>();
		for(String id : this.groups) {
			try {
				Group group = (Group) groupMgmt.find(id);
				if (group != null && !groups.contains(group))
					groups.add(group);
			} catch (SQLException e) { /* ignore */
			}
		}
		return groups;
	}

	public final boolean hasRole(Role role) {
		boolean result = roles.contains(role.getId());
		if (!result) {
			for (Group g: getGroupList()) {
				for (Role r: g.getRoles()) {
					if (r.getId().equals(role.getId())) {
						return true;
					}
				}
			}
		}
		return result;
	}
	
	public final boolean equals(Object obj) {
		if (obj instanceof User) {
			UserImpl other = (UserImpl) obj;
			// two users are equal if and only if their IDs and login names
			// are equal
			return getId().equals(other.getId())
					&& getLoginName().equals(other.getLoginName());
		}
		return false;
	}

	public final int hashCode() {
		int hc = 17;
		hc += 31 * getId().hashCode();
		hc += 31 * getLoginName().hashCode();
		return hc;
	}
	
	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	public final List<String> getGroupIDs() {
		AccessController.checkAccess(User.class);
		return new ArrayList<String>(groups);
	}
	public final List<String> getRoleIDs() {
		AccessController.checkAccess(User.class);
		return new ArrayList<String>(roles);
	}
	public List<Account> getAccounts() {
		AccessController.checkAccess(User.class);
		return new ArrayList<Account>(accounts);
	}
	
	final void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	final void setLastname(String lastname) {
		this.lastname = lastname;
	}
	final void setLoginName(String login) {
		this.login = login;
	}
	final void setPassword(String password) {
		this.password = password;
	}
	final void add(Role role) {
		roles.add(role.getId());
	}
	final void add(Group group) {
		groups.add(group.getId());
	}
	final void add(Account account) {
		if(account != null)
			accounts.add(account);
	}
	final void remove(Account account) {
		accounts.remove(account);
	}
	final void remove(Role role) {
		roles.remove(role.getId());
	}
	final void remove(Group group) {
		groups.remove(group.getId());
	}
	final void setRoles(List<Role> roles) {
		this.roles.clear();
		if (roles != null) {
			for (Role role : roles)
				this.roles.add(role.getId());
		}
	}

	//	final void setRoles(List<String> roles) {
//		this.roles.clear();
//		if(roles != null)
//			this.roles.addAll(roles);
//	}
	final void setGroups(List<Group> groups) {
		this.groups.clear();
		if(groups != null) {
			for(Group group : groups)
				this.groups.add(group.getId());
		}
	}
	final void setAccounts(List<Account> accounts) {
		this.accounts.clear();
		if(accounts != null)
			this.accounts.addAll(accounts);
	}

	
	//md5 encrypt given string...
	public static final String encrypt(String pass) {
		if(pass == null || pass.equals(""))
			return "";
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(pass.getBytes("UTF-8"));
			byte raw[] = md.digest();
			String hash = (new BASE64Encoder()).encode(raw);
			return hash;
			// byte[] bytes = md.digest(pass.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pass;
	}
		
	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private String firstname;
		private String lastname;
		private String login;
		private String password; 	
//		private Authentication authentication;
		private final Set<String> roles = new LinkedHashSet<String>();
		private final Set<String> groups = new LinkedHashSet<String>();
		private final Set<Account> accounts = new LinkedHashSet<Account>();
		
		
		public Builder(String id) {
			AccessController.checkAccess(User.class);
			this.id = id;
		}

		public Builder firstname(String firstname) {
			this.firstname = firstname;
			return this;
		}
		public Builder lastname(String lastname) {
			this.lastname = lastname;
			return this;
		}
		public Builder login(String login) {
			this.login = login;
			return this;
		}
		public Builder password(String password) {
			this.password = password;
			return this;
		}
//		public Builder authentication(Authentication authentication) {
//			this.authentication = authentication;
//			return this;
//		}
		public Builder groups(List<String> groups) {
			this.groups.clear();
			if(groups != null)
				this.groups.addAll(groups);
			return this;
		}
		public Builder roles(List<String> roles) {
			this.roles.clear();
			if(roles != null)
				this.roles.addAll(roles);
			return this;
		}
		public Builder accounts(List<Account> accounts) {
			this.accounts.clear();
			if(accounts != null)
				this.accounts.addAll(accounts);
			return this;
		}

		public Builder add(Account account) {
			accounts.add(account);
			return this;
		}
		public User build() {
			return new UserImpl(this);
		}
	}

}
