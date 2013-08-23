/*
*
* @file ConnectionContextImpl.java
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
* @version $Id: ConnectionContextImpl.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.util.HashMap;

import org.palo.api.ConnectionContext;
import org.palo.api.Rights;
import org.palo.api.ServerInfo;


/**
 * <code>ConnectionContextImpl</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: ConnectionContextImpl.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
class ConnectionContextImpl implements ConnectionContext {

	private final ConnectionImpl connection;
	private final RightsImpl rights;
	private final ServerInfoImpl serverInfo;
	private boolean doSupportSubset2;
	
	private final HashMap<String, Object> dataMap = new HashMap<String, Object>();
	
	ConnectionContextImpl(ConnectionImpl connection) {
		this.connection = connection;
		this.rights = new RightsImpl(connection);
		this.serverInfo = new ServerInfoImpl(connection, 
				connection.getConnectionInternal().getServerInfo());
		setContext();
	}

	final void setDoSupportSubset2(boolean b) {
		this.doSupportSubset2 = b;
	}
	
	public final boolean doSupportSubset2() {
		return doSupportSubset2;
	}
	
	
	private final void setContext() {
		com.tensegrity.palojava.ServerInfo srvInfo = 
			connection.getConnectionInternal().getServerInfo();
		setDoSupportSubset2(
				(srvInfo.getMajor() >= 2 && srvInfo.getBuildNumber() > 2400));
	}
	
	public final Rights getRights() {
		return rights;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	
	public final void setData(String id, Object data) {
		dataMap.put(id, data);
	}
	
	public final Object getData(String id) {
		return dataMap.get(id);
	}

}
