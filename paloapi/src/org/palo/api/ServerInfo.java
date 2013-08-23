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
* @version $Id: ServerInfo.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api;

/**
 * <code>ServerInfo</code>
 * This object provides information about the currently used server. Please
 * note that not all server provide all information. If this is the case 
 * <code>null</code> is returned.
 *
 * @author ArndHouben
 * @version $Id: ServerInfo.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public interface ServerInfo {
	
	//predefined properties:
	String SECURITY_INFO_PROPERTY = "SecurityInfoProperty";
	String BUILD_NUMBER_PROPERTY = "BuildNumberProperty";
	String MINOR_VERSION_PROPERTY = "MinorVersionProperty";
	String MAJOR_VERSION_PROPERTY = "MajorVersionProperty";
	String DESCRIPTION_PROPERTY = "DescriptionProperty";
	
	/**
	 * Returns the server name or <code>null</code> if server does
	 * not provide it
	 * @return server name or <code>null</code>
	 */
	String getName();
	/**
	 * Returns the server version name or <code>null</code> if server does
	 * not provide it
	 * @return server version or <code>null</code>
	 */
	String getVersion();
	/**
	 * Returns the server type or <code>null</code> if server does
	 * not provide it. 
	 * @return server type or <code>null</code>
	 */
	String getType();

	/**
	 * Returns all property ids known to the current used server.
	 * @return all property ids known to the current used server
	 */
	String [] getPropertyIds();	
	/**
	 * Returns the value for the property which is specified by the given id.
	 * If the server does not know this property <code>null</code> is returned.
	 * @param id the property identifier
	 * @return the property value or <code>null</code> if no such property was
	 * defined
	 */
	String getProperty(String id);
}
