/*
*
* @file HttpPaloServer.java
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
* @version $Id: HttpPaloServer.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloServer;
import com.tensegrity.palojava.ServerInfo;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @version $Id: HttpPaloServer.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class HttpPaloServer implements PaloServer {

	private final HttpConnection connection;
	
	public HttpPaloServer(String host, String port, int timeout) {
		connection = new HttpConnection(host,port,timeout);
	}
	
	public final synchronized DbConnection connect() {
		return connection;
	}
	
	public final ServerInfo getInfo() {
		if(!isConnected())
			throw new PaloException("not connected!!");
		return connection.getServerInfo();
	}
	
	public final void disconnect() {
		connection.disconnect();
	}

	public final void ping() {
		throw new PaloException("HttpPaloServer#disconnect(): NOT IMPLEMENTED!!");
		// TODO Auto-generated method stub
		
	}

	public final boolean login(String username, String password) {
		return connection.login(username, password);
	}

	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final synchronized boolean isConnected() {
		return (connection != null && connection.isConnected());
	}

}
