/*
*
* @file ServerInfo.java
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
* @version $Id: ServerInfo.java,v 1.4 2009/11/23 08:25:26 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * <code>ServerInfo</code> provides information about the used palo server  
 * 
 * @author ArndHouben
 * @version $Id: ServerInfo.java,v 1.4 2009/11/23 08:25:26 PhilippBouillon Exp $
 */
public interface ServerInfo extends PaloInfo {
    int ENCRYPTION_NONE = 0;
    int ENCRYPTION_OPTIONAL = 1;
    int ENCRYPTION_REQUIRED = 2;
	
	/**
	 * Returns the major number of the server
	 * @return major number
	 */
	int getMajor();
	/**
	 * Returns the minor number of the server
	 * @return minor number
	 */
	int getMinor();
	/**
	 * Returns the bugfix level of current server version
	 * @return bugfix level
	 */
	int getBugfixVersion();
	/**
	 * Returns the build number of the server
	 * @return build number
	 */
	int getBuildNumber();
	/**
	 * Returns <code>true</code> if the used palo server is the legacy server,
	 * i.e. prior to version 1.5, <code>false</code> otherwise.
	 * @return <code>true</code> is used server is legacy, <code>false</code>
	 * otherwise
	 */
	boolean isLegacy();
	
	/**
	 * Returns the https port of this server or 0 if https is not supported.
	 * 
	 * @return the https port of this server or 0 if https is not supported.
	 */
	int getHttpsPort();
	
	/**
	 * Returns an integer value corresponding to the encryption state of this
	 * server.
	 * 
	 * @return an integer matching one of the constants defined in ServerInfo,
	 * describing the encryption mechanism of this server.
	 */
	int getEncryption();
	
	String getName();
	String getServerType();
	String getVersion();
	String [] getProperties();
}
