/*
*
* @file CubeInfo.java
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
* @version $Id: CubeInfo.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

import java.math.BigInteger;

/**
 * The <code>CubeInfo</code> interface is a representation of the palo
 * <code>Cube</code> object.
 * 
 * @author ArndHouben
 * @version $Id: CubeInfo.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface CubeInfo extends PaloInfo, PaloConstants {

	public static final int STATUS_UNLOADED = 0;
	public static final int STATUS_LOADED = 1;
	public static final int STATUS_CHANGED = 2;

//	public static final int CUBETYPE_NORMAL = 1;
//	public static final int CUBETYPE_SYSTEM = 2;
//	public static final int	CUBETYPE_ATTRIBUTE = 4;
//	public static final int CUBETYPE_USERINFO = 8;
	
	/**
	 * Returns the cube name
	 * @return cube name
	 */
	public String getName();
	/**
	 * Returns the <code>Database</code> representation which contains this
	 * cube
	 * @return <code>Database</code> representation
	 */
	public DatabaseInfo getDatabase();
	
	/**
	 * Returns the identifiers for the <code>Dimension</code>s which build up
	 * this cube
	 * @return the <code>Dimension</code> ids
	 */
	public String[] getDimensions();
	/**
	 * Returns the number of all cells this cube has
	 * @return number of all cube cells
	 */
	public BigInteger getCellCount();
	/**
	 * Returns the number of <code>Dimension</code>s this cube consists of
	 * @return number of <code>Dimension</code>s
	 */
	public int getDimensionCount();
	/**
	 * Returns the number of filled cells
	 * @return number of filled cells
	 */
	public BigInteger getFilledCellCount();
	/**
	 * Returns the cube status which is one of the defined constants
	 * @return cube status
	 */
	public int getStatus();
	/**
	 * Returns the cube token. The cube token is changed whenever 
	 * something has changed within the cube, e.g. an <code>Element</code>
	 * was deleted
	 * @return cube token
	 */
	public int getToken();

}
