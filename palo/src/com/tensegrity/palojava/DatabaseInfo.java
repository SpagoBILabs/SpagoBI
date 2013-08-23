/*
*
* @file DatabaseInfo.java
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
* @version $Id: DatabaseInfo.java,v 1.4 2009/11/23 08:25:26 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * The <code>DatabaseInfo</code> is a representation of the palo
 * <code>Database</code> object.
 *  
 * @author ArndHouben
 * @version $Id: DatabaseInfo.java,v 1.4 2009/11/23 08:25:26 PhilippBouillon Exp $
 */
public interface DatabaseInfo extends PaloInfo, PaloConstants {
	
	public static final int STATUS_UNLOADED = 0;
	public static final int STATUS_LOADED = 1;
	public static final int STATUS_CHANGED = 2;
	
	/**
	 * Returns the database name
	 * @return database name
	 */
	public String getName();
	/**
	 * Returns the number of <code>Cube</code>s this database has
	 * @return
	 */
	public int getCubeCount();
	/**
	 * Returns the number of <code>Dimension</code>s this database has
	 * @return
	 */
	public int getDimensionCount();
	/**
	 * Returns the database status which is one of the specified constants
	 * @return database status
	 */
	public int getStatus();
	/**
	 * Returns the database token. The database token is changed whenever 
	 * something has changed within the database, e.g. an <code>Element</code>
	 * was deleted
	 * @return database token
	 */
	public int getToken();

	/**
	 * Checks if this palo info object represents a palo system object 
	 * @return <code>true</code> if this palo info object represents a palo
	 * system object, <code>false</code> otherwise
	 * 
	 */
	public boolean isSystem();
	
	public boolean isUserInfo();
}
