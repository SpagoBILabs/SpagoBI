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
* @author Arnd Houben
*
* @version $Id: ServerInfoImpl.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.impl;

import com.tensegrity.palojava.ServerInfo;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @version $Id: ServerInfoImpl.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class ServerInfoImpl implements ServerInfo {

	private final boolean isLegacy;
	private final int bugfixVersion;
	private final int buildNumber;
	private final int majorNumber;
	private final int minorNumber;
	private final int httpsPort;
	private final int encryption;
	private final String name;
	private final String serverType;
//	private final String version;
	
	public ServerInfoImpl(int buildNumber, int bugfixNumber, int majorNumber,
			int minorNumber, int httpsPort, int encryption, boolean isLegacy) {
		this.isLegacy = isLegacy;
		this.buildNumber = buildNumber;
		this.bugfixVersion = bugfixNumber;
		this.majorNumber = majorNumber;
		this.minorNumber = minorNumber;
		this.httpsPort = httpsPort;
		this.encryption = encryption;
		this.name = "PaloServer";
		this.serverType = "Palo";
//		this.version = majorNumber + "." + minorNumber;
	}
	
	public int getBugfixVersion() {
		return bugfixVersion;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public int getMajor() {
		return majorNumber;
	}

	public int getMinor() {
		return minorNumber;
	}

	public boolean isLegacy() {
		return isLegacy;
	}

	public String getId() {
		return Integer.toString(buildNumber);
	}

	public int getType() {
		//we are palo server...
		return 2;	//TODO Connection.TYPE_HTTP should be defined in this package...
	}

	public boolean canBeModified() {
		return false;
	}

	public boolean canCreateChildren() {
		return false;
	}

	public int getEncryption() {
		return encryption;
	}

	public int getHttpsPort() {
		return httpsPort;
	}

	public String getName() {
		return name;
	}

	public String getServerType() {
		return serverType;
	}

	public String getVersion() {
		StringBuffer vStr = new StringBuffer(majorNumber);
		vStr.append(".");
		vStr.append(minorNumber);
		return vStr.toString(); //version;
	}

	public String[] getProperties() {
		return new String[0];
	}
}
