/*
*
* @file ConnectionPoolManager.java
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
* @version $Id: ConnectionPoolManager.java,v 1.10 2010/02/12 13:51:05 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.internal;

import java.util.HashMap;
import java.util.Map;

import org.palo.api.Connection;
import org.palo.viewapi.Account;
import org.palo.viewapi.PaloAccount;

public class ConnectionPoolManager {
	private final Map <String, ServerConnectionPool> pools =
		new HashMap <String, ServerConnectionPool>();
	
	private final static ConnectionPoolManager instance =
		new ConnectionPoolManager();
	
	private final PaloConnectionFactory factory = new
		PaloConnectionFactory();

	private ConnectionPoolManager() {		
	}

	public static ConnectionPoolManager getInstance() {
		return instance;
	}
	
	public ServerConnectionPool getPool(Account account, String sessionId) {
		ServerConnectionPool pool;
		synchronized (pools) {				
			String serverId = account.getLoginName() + ":" + account.getConnection().getHost() + ":" + account.getConnection().getService();// + ":" + sessionId;
			pool = (ServerConnectionPool)pools.get(serverId);
			if (pool == null){
				String sProvider = "palo";
				switch (account.getConnection().getType()) {
					case Connection.TYPE_XMLA : sProvider = "xmla"; break;
				}
				pool = new ServerConnectionPool(factory,
						account.getConnection().getHost(),
						account.getConnection().getService(),
						account.getLoginName(), 
						account.getPassword(),
						1, sProvider);
				pools.put(serverId, pool);
			}
		}
		return pool;
	}
	
	public ServerConnectionPool onlyGetPool(Account account, String sessionId) {
		ServerConnectionPool pool;
		synchronized (pools) {		
			if (account.getConnection() == null) {
				return null;
			}
			String serverId = account.getLoginName() + ":" + account.getConnection().getHost() + ":" + account.getConnection().getService();// + ":" + sessionId;
			pool = (ServerConnectionPool)pools.get(serverId);
		}
		return pool;		
	}
	
	public synchronized void disconnect(Account account, String sessionId, String from) {
//		Thread.dumpStack();
		if (account instanceof PaloAccount) {
			ServerConnectionPool pool;
			synchronized (pools) {				
				String serverId = account.getLoginName() + ":" + account.getConnection().getHost() + ":" + account.getConnection().getService();// + ":" + sessionId;
				pool = (ServerConnectionPool)pools.get(serverId);
				if(pool == null){
					String sProvider = "palo";
					switch (account.getConnection().getType()) {
						case Connection.TYPE_XMLA : sProvider = "xmla"; break;
					}
					pool = new ServerConnectionPool(factory,
							account.getConnection().getHost(),
							account.getConnection().getService(),
							account.getLoginName(), 
							account.getPassword(),
							1, sProvider);
					pools.put(serverId, pool);
				}
			}
			pool.shutdown();
		}		
	}	
}
 