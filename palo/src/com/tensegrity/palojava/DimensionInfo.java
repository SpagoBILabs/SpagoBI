/*
*
* @file DimensionInfo.java
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
* @version $Id: DimensionInfo.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * The <code>DimensionInfo</code> interface is a representation of the palo
 * <code>Dimension</code> object.
 * 
 * @author ArndHouben
 * @version $Id: DimensionInfo.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface DimensionInfo extends PaloInfo, PaloConstants {
//	public static final int DIMTYPE_NORMAL = 1;
//	public static final int DIMTYPE_SYSTEM = 2;
//	public static final int	DIMTYPE_ATTRIBUTE = 4;
//	public static final int DIMTYPE_USERINFO = 8;
	
	/**
	 * Returns the <code>Database</code> representation which contains this
	 * dimension
	 * @return <code>Database</code> representation
	 */
	public DatabaseInfo getDatabase();
	/**
	 * Returns the dimension name 
	 * @return dimension name
	 */
	public String getName();
	/**
	 * Sets the dimension name
	 * @param name new dimension name
	 */
	public void setName(String name);
	/**
	 * Returns the id of the corresponding attribute cube
	 * @return attribute cube id
	 */
	public String getAttributeCube();
	/**
	 * Returns the id of the corresponding attribute dimension
	 * @return attribute dimension id
	 */
	public String getAttributeDimension();
	/**
	 * Returns the number of <code>Element</code>s this dimension has
	 * @return number of <code>Element</code>s
	 */
	public int getElementCount();
	/**
	 * Returns the maximum depth of this dimension
	 * @return maximum depth
	 */
	public int getMaxDepth();
	/**
	 * Returns the maximum indent of this dimension
	 * @return maximum indent
	 */
	public int getMaxIndent();
	/**
	 * Returns the maximum level of this dimension
	 * @return maximum level
	 */
	public int getMaxLevel();
	/**
	 * Returns the id of the corresponding rights cube
	 * @return rights cube id
	 */
	public String getRightsCube();
	/**
	 * Returns the dimension token. The dimension token is changed whenever 
	 * something has changed within the dimension, e.g. an <code>Element</code>
	 * was deleted
	 * @return dimension token
	 */
	public int getToken();

	public int getHierarchyCount();
	public HierarchyInfo getDefaultHierarchy();
	public HierarchyInfo [] getHierarchies();	
}
