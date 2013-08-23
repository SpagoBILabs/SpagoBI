/*
*
* @file Cell.java
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
* @version $Id: Cell.java,v 1.6 2009/05/15 08:53:56 ArndHouben Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api;

/**
 * {@<describe>}
 * <p>
 * This interface describes a <code>Cell</code> within a palo cube. Each cell 
 * consists of a coordinate and a cell value of certain type. Currently palo 
 * supports the types <code>NUMERIC</code> and <code>STRING</code>. Constants 
 * are provide for both types.<br/>
 * Note: <code>Cell</code>s are not cached by the API.
 * </p>
 * {@</describe>}
 *
 * @author ArndHouben
 * @version $Id: Cell.java,v 1.6 2009/05/15 08:53:56 ArndHouben Exp $
 */

public interface Cell {

	/** 
	 * Constant for NUMERIC type. Values of type NUMERIC should be handled as 
	 * <code>double</code>s. 
	 * */
	public static final int NUMERIC = 1;
	/** Constant for value type STRING */
	public static final int STRING = 2;
	
	
	/**
	 * Returns the cube of this cell
	 * @return
	 */
	Cube getCube();
	
	/**
	 * Returns the cell path
	 * @return cell path
//	 * @deprecated please use {@link #getCoordinate()} instead.
	 */
	Element[] getPath();
	
	/**
	 * Returns the cell coordinate
	 * @return cell coordinate
	 */
	Element[] getCoordinate();
	
	/**
	 * Returns the cell value which is either of type <code>NUMERIC</code> or 
	 * <code>STRING</code>
	 * @return cell value
	 */
	Object getValue();
	
	/**
	 * Returns the value type which is either <code>NUMERIC</code> or 
	 * <code>STRING</code>
	 * @return value type
	 */
	int getType();
	
	/**
	 * Checks if the cell value is based on a rule
	 * @return <code>true</code> if cell has a rule applied, <code>false</code>
	 * otherwise.
	 */
	boolean hasRule();
	
	/**
	 * Returns the identifier of the {@link Rule} which is applied to this cell 
	 * or <code>null</code> if this cell has no rule. 
	 * @return the id of the cell rule or <code>null</code>.
	 */
	String getRuleId();
	
	/**
	 * Checks if the cell is consolidated, i.e. at least one 
	 * <code>Element</code> must be of type <code>CONSOLIDATED</code> and all
	 * other must be of type <code>NUMERIC</code>.
	 * 
	 * @return <code>true</code> if this cell is consolidated, 
	 * <code>false</code> otherwise
	 */
	boolean isConsolidated();
	
	/**
	 * Checks if this cell is empty, i.e. no value is set. In case of type
	 * <code>STRING</code> this means an empty string.
	 * @return <code>true</code> if this cell has no value, otherwise <code>false</code>
	 */
	boolean isEmpty();
	//FOLLOWING METHODS WILL MAKE CELL REPRESENTATION STALE SINCE ITS VALUE AND	
	//SERVER VALUE DOESN'T MATCH AFTERWARDS....
//		public final void setValue(Object value);
//		public final void setValue(Object value, int splashMode);
//		public final void clear();

}
