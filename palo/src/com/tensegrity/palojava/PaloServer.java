/*
*
* @file PaloServer.java
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
* @version $Id: PaloServer.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * The <code>PaloServer</code> interface defines general methods to connect with
 * and login into a certain palo server.
 * 
 * @author ArndHouben
 * @version $Id: PaloServer.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface PaloServer {

	/** 
	 * constant for server type legacy
	 * @deprecated legacy server is not supported anymore!
	 */
	public static final int TYPE_LEGACY = 1;
	/** constant for server type http */
	public static final int TYPE_HTTP = 2;
	/** constant for server type XMLA */
	public static final int TYPE_XMLA = 3;
	/** constant for server type WSS */
	public static final int TYPE_WSS = 4;
	
	/**
	 * Returns the {@link ServerInfo} object to gather further information about 
	 * this palo server
	 * @return
	 */
	public ServerInfo getInfo();
	
	/**
	 * Connect to this palo server.
	 * @return {@link DbConnection} if connection was successful
	 */
	public DbConnection connect();
	
    /**
     * Disconnects from the palo server
     * @throws PaloException if an communication exception occurs
     */
    public void disconnect();

    /**
     * Tests if the palo server is still reachable
     * @throws PaloException if palo server is not reachable anymore 
     */
    public void ping() throws PaloException;

//    /**
//     * Try to login to palo server with given name and password
//     * @param username login name
//     * @param password login password
//     * @return <code>true</code> if login was successful, <code>false</code>
//     * otherwise
//     */
//    public boolean login(String username, String password);
}
