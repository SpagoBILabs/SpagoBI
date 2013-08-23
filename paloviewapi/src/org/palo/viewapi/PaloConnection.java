/*
*
* @file PaloConnection.java
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
* @version $Id: PaloConnection.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;



/**
 * The <code>PaloConnection</code> interface describes a palo server and provides
 * the information required to connect to it. 
 *
 * @version $Id: PaloConnection.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface PaloConnection extends DomainObject {

	/** connection type legacy @deprecated NOT SUPPORTED ANYMORE */
	public static final int TYPE_LEGACY = 1;
	
	/** connection type http */
	public static final int TYPE_HTTP = 2;
	/** connection type xmla */
	public static final int TYPE_XMLA = 3;
	/** connection type wss */
	public static final int TYPE_WSS = 4;

	/**
	 * Returns the name for this connection or <code>null</code> if none was 
	 * assigned.
	 * @return the connection name or <code>null</code>
	 */
	public String getName();
	/**
	 * Returns the host string which is associated with this connection.
	 * An example of a host string can be a domain or ip address.
	 * @return the associated host
	 */
	public String getHost();
	/**
	 * Returns the service string which is associated with this connection.
	 * An example of a service string can be a port number  
	 * @return the associated service
	 */

	public String getService();
	/**
	 * Returns the description for this connection or <code>null</code> if none
	 * was added.
	 * @return an optional connection description 
	 */
	public String getDescription();
	
    /**
     * Returns the type for this connection. The returned value will be
     * one of the TYPE_xxx constants defined in this interface.
     * @return the connection type
     */
	public int getType();
}
