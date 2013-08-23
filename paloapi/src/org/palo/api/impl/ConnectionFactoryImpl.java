/*
*
* @file ConnectionFactoryImpl.java
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
* @author Arnd Houben
*
* @version $Id: ConnectionFactoryImpl.java,v 1.21 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.util.HashSet;
import java.util.Set;

import org.palo.api.Connection;
import org.palo.api.ConnectionConfiguration;
import org.palo.api.ConnectionFactory;
import org.palo.api.PaloAPIException;

import com.tensegrity.palo.xmla.XMLAServer;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloFactory;
import com.tensegrity.palojava.PaloServer;

/**
 * <code>ConnectionFactoryImpl</code>
 * 
 * @author Arnd Houben
 * @author Stepan Rutz
 * @version $Id: ConnectionFactoryImpl.java,v 1.21 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class ConnectionFactoryImpl extends ConnectionFactory {
	/**
	 * Stores a list of all Palo connections that have been created with this
	 * factory, and which support rules.
	 */
	private final Set activePaloConnections = new HashSet();
	
	/**
	 * @deprecated please use @{link {@link #newConnection(ConnectionConfiguration)}
	 */
	public final Connection newConnection(String server, String service, String user,
			String pass) {
		return newConnection(server,service,user,pass, false, Connection.TYPE_HTTP);
	}

	/**
	 * @deprecated please use @{link {@link #newConnection(ConnectionConfiguration)}
	 */
	public final Connection newConnection(String server, String service, String user,
			String pass, boolean doLoadOnDemand, int type) {
		PaloServer paloSrv;  
		if (type == Connection.TYPE_HTTP || type == Connection.TYPE_LEGACY) {	
			paloSrv =
				PaloFactory.getInstance().createServerConnection(server, service, type,Connection.DEFAULT_TIMEOUT);
		} else if (type == Connection.TYPE_XMLA) {
			paloSrv = new XMLAServer(server, service, user, pass);
		} else {
			throw new PaloAPIException("Unknown connection type specified: " 
					+ type + "!!");
		}
		ConnectionImpl connection;
		try { 
			connection =  new ConnectionImpl(paloSrv);
			if (connection.login(user, pass)) {
				if(!doLoadOnDemand)
					connection.reload();
//					connection.reloadAlllDatabases(true);
//					connection.loadDatabaseInfos();
//				else
//					connection.initDatabases(true);
			} else {
				throw new PaloAPIException("Could not login to palo server '"
						+ server + "' as user '" + user + "'!!");
			}
		} catch (PaloException e) {
			throw new PaloAPIException(e.getMessage());
		}
		if (connection.supportsRules() && type != Connection.TYPE_XMLA) {
			activePaloConnections.add(connection);
		}
		return connection;
	}

	/**
	 * Returns a default {@link ConnectionConfiguration} instance. The type is
	 * {@link Connection#TYPE_HTTP}, load on demand is disabled and timeout
	 * is set to {@link #CONNECTION_TIMEOUT}.
	 */
	public final ConnectionConfiguration getConfiguration(String host,
			String service) {
		return getConfiguration(host, service, null, null);
	}

	public final ConnectionConfiguration getConfiguration(String host, String service, String user, String password) {
		ConnectionConfiguration cfg = new ConnectionConfiguration(host,service);
		//configure:
		cfg.setUser(user);
		cfg.setPassword(password);
		return cfg;
	}

	public final Connection newConnection(ConnectionConfiguration cfg) {
		PaloServer paloSrv;
		int type = cfg.getType();
		if (type == Connection.TYPE_HTTP || type == Connection.TYPE_LEGACY) {
			paloSrv = PaloFactory.getInstance().createServerConnection(
					cfg.getHost(), cfg.getPort(), type, cfg.getTimeout());
		} else if (type == Connection.TYPE_XMLA) {
			paloSrv = new XMLAServer(cfg.getHost(), cfg.getPort(), cfg
					.getUser(), cfg.getPassword());
		} else {
			throw new PaloAPIException("Unknown connection type specified: "
					+ type + "!!");
		}
		ConnectionImpl connection = new ConnectionImpl(paloSrv);
		if (connection.login(cfg.getUser(), cfg.getPassword())) {
			if (!cfg.doLoadOnDemand())
				connection.reload();
//				connection.reloadAlllDatabases(true);
//				connection.loadDatabaseInfos();
//			else
//				connection.initDatabases(true);
		} else
			throw new PaloAPIException("Could not login to palo server '"
					+ cfg.getHost() + "' as user '" + cfg.getUser()
					+ "'!!");
		if (connection.supportsRules() && type != Connection.TYPE_XMLA) {
			activePaloConnections.add(connection);
		}
		return connection;
	}
	
	final Connection [] getActiveConnections() {
		Connection [] cons = (Connection []) 
			activePaloConnections.toArray(new Connection[0]);
		for (int i = 0, n = cons.length; i < n; i++) {
			if (!cons[i].isConnected()) {
				activePaloConnections.remove(cons[i]);
			}
		}
		return (Connection []) activePaloConnections.toArray(new Connection[0]);
	}
	
	final void removePaloConnection(Connection con) {
		activePaloConnections.remove(con);
	}
}
