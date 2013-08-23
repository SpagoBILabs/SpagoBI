/*
*
* @file ConnectionInfoImpl.java
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
* @author ArndHouben
*
* @version $Id: ConnectionInfoImpl.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 * All rights reserved
 */
package com.tensegrity.palojava.impl;

import java.util.HashMap;

import com.tensegrity.palojava.ConnectionInfo;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: ConnectionInfoImpl.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class ConnectionInfoImpl implements ConnectionInfo {

	private final String host;
	private final String port;
	private String username;
	private String password;
	
	private final HashMap<String, Object> dataMap = new HashMap<String, Object>();
	
	public ConnectionInfoImpl(String host, String port) {
		this(host,port,null,null);
	}
	
	public ConnectionInfoImpl(String host, String port, String username,
			String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public final String getHost() {
		return host;
	}

	public final synchronized String getPassword() {
		return password;
	}

	public final String getPort() {
		return port;
	}

	public final synchronized String getUsername() {
		return username;	
	}
	
	public final synchronized void setPassword(String password) {
		this.password = password;
	}
	
	public final synchronized void setUser(String username) {
		this.username = username;
	}
	
	public final void setData(String id, Object data) {
		dataMap.put(id, data);
	}
	
	public final Object getData(String id) {
		return dataMap.get(id);
	}
	
	public final String toString() {
		StringBuffer str = new StringBuffer();
		str.append(host);
		str.append(":");
		str.append(port);
		str.append(" user:");
		str.append(username);
//		str.append(" password:");
//		str.append(password);
		return str.toString();
	}
	
	public final boolean equals(Object obj) {
		if (obj instanceof ConnectionInfo) {
			ConnectionInfo other = (ConnectionInfo) obj;
			boolean equals = host.equals(other.getHost())
					&& port.equals(other.getPort());

			// check user name if any:
			if (username != null)
				equals = equals && username.equals(other.getUsername());
			else
				equals = equals && (other.getUsername() == null);

			// check password if any:
			if (password != null)
				equals = equals && password.equals(other.getPassword());
			else
				equals = equals && (other.getPassword() == null);

			return equals;
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 23;
		hc += 37 * host.hashCode();
		hc += 37 * port.hashCode();
		if(username != null)
			hc += 37 * username.hashCode();
		if(password != null)
			hc += 37 * password.hashCode();
		return hc;
	}
}
