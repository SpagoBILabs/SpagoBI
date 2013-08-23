/*
*
* @file CellInfo.java
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
* @version $Id: CellInfo.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * The <code>CellInfo</code> interface is a representation of a single cell
 * within a palo <code>Cube</code>. Each cell has a value of type 
 * {@link #TYPE_NUMERIC} or {@link #TYPE_STRING} and a path specified by its
 * coordinate.
 * 
 * @author ArndHouben
 * @version $Id: CellInfo.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface CellInfo extends PaloInfo {

	//new values for palo 1.5 => has to be adjusted for legacy...
	public static final int SPLASH_MODE_DISABLED = 0;
	public static final int SPLASH_MODE_DEFAULT = 1;
	public static final int SPLASH_MODE_ADD = 2;
	public static final int SPLASH_MODE_SET = 3;
	public static final int SPLASH_MODE_UNKNOWN = 4;
	
	/** type for numeric cell value */
	public static final int TYPE_NUMERIC = 1;
	/** type for textual cell value */
	public static final int TYPE_STRING = 2;
	/** signals an cell error */
	public static final int TYPE_ERROR = 99;

	/**
	 * Checks if a cell at current set coordinate exists.
	 * @return <code>true</code> if a cell exists at current coordinate,
	 * <code>false</code> otherwise
	 */
	public boolean exists();
	
	/**
	 * Returns the cell value as {@link Double} or {@link String} depending on
	 * the cell type
	 * @return cell value
	 */
	public Object getValue();
	
	/**
	 * Returns the ids of the <code>Element</code>s which build up the path to
	 * this cell. <b>NOTE:</b> this is optional and can return <code>null</code>
	 * if no path was set. 
	 * @return
	 */
	public String[] getCoordinate();
	
	/**
	 * Returns the type of this cell. The type is one of the predefined cell 
	 * type constants.
	 * @return the cell type.
	 */
	public int getType();
	
	/**
	 * Returns the identifier of the rule which is attached to this cell or
	 * <code>null</code> if this cell has no rule. 
	 * @return the rule id or <code>null</code>
	 */
	public String getRule();
	
	/**
	 * Sets the identifier of the rule to use for this cell
	 * @param id the identifier of the rule to use for this cell
	 */
	public void setRule(String id);
	
}
