/*
*
* @file ServerInfoImpl.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: ServerInfoImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import java.util.HashMap;

import org.palo.api.Connection;
import org.palo.api.Property2;
import org.palo.api.ServerInfo;

//import com.tensegrity.palojava.ServerInfo;

class ServerInfoImpl implements ServerInfo {
	private final ConnectionImpl connection;
	private final HashMap <String, Property2> properties;
	private final com.tensegrity.palojava.ServerInfo serverInfo;
	
	ServerInfoImpl(Connection connection,
			com.tensegrity.palojava.ServerInfo serverInfo) {
		this.connection = (ConnectionImpl) connection;
		this.properties = new HashMap<String, Property2>();
		this.serverInfo = serverInfo;
		
		addProperty(ServerInfo.MAJOR_VERSION_PROPERTY, 
				Integer.toString(serverInfo.getMajor()));
		addProperty(ServerInfo.MINOR_VERSION_PROPERTY, 
				Integer.toString(serverInfo.getMinor()));
		addProperty(ServerInfo.BUILD_NUMBER_PROPERTY, 
				Integer.toString(serverInfo.getBuildNumber()));
		
		String[] props = serverInfo.getProperties();
		for (int i = 0; i < props.length; i += 2) {
			addProperty(props[i], props[i + 1]);
		}
	}
		
	private final void addProperty(String id, String value) {
		properties.put(id, Property2Impl.create(connection, id, value, null, 
									Property2.TYPE_STRING, true));		
	}
	
	public String getName() {
		return serverInfo.getName();
	}

	public String getProperty(String id) {
		if (properties.containsKey(id)) {
			return properties.get(id).getValue();
		}
		return "";
	}

	public String getType() {
		return serverInfo.getServerType();
	}

	public String getVersion() {
		return serverInfo.getVersion();
	}

	public String[] getPropertyIds() {
		return properties.keySet().toArray(new String[0]);
	}
}
