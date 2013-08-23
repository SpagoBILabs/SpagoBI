/*
*
* @file FormatRangeInfo.java
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
* @author PhilippBouillon
*
* @version $Id: FormatRangeInfo.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.formats;

import java.util.LinkedHashMap;

import org.palo.api.Dimension;
import org.palo.api.Element;

/**
 * The FormatRangeInfo class describes a cell or a range of cells for which the
 * referenced Format is active.
 * 
 * @author PhilippBouillon
 * @version $Id: FormatRangeInfo.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public class FormatRangeInfo {
	/**
	 * Specifies the exact cells of this range. The format is like that passed
	 * to Cube.getDataBulk(...);
	 */
	private Element [][] cells;
	
	/**
	 * Specifies the level(s) of the dimension(s) to which this range applies. 
	 */
	private final LinkedHashMap <Dimension, Integer> levelDims;
		
	/**
	 * Default constructor. Creates an empty FormatRangeInfo.
	 */
	public FormatRangeInfo() {
		cells = null;
		levelDims = null;
	}

	/**
	 * Copy constructor. Creates a FormatRangeInfo from another FormatRangeInfo.
	 * 
	 * @param info the original FormatRangeInfo.
	 */
	private FormatRangeInfo(FormatRangeInfo info) {
		cells = info.cells.clone();
		levelDims = (LinkedHashMap<Dimension, Integer>) info.levelDims.clone();
	}

	/**
	 * Creates a new FormatRangeInfo that applies to the specified cells.
	 * 
	 * @param cells the cells that are contained in this FormatRangeInfo.
	 */
	public FormatRangeInfo(Element [][] cells) {
		this.cells = cells.clone();
		levelDims = null;
	}	
	
	/**
	 * Creates a new FormatRangeInfo that applies to the given level(s) of the
	 * given dimension(s). Note that you must give a level for each dimension
	 * that you specify.
	 * 
	 * @param dims dimensions for which a level is to be selected.
	 * @param levels levels to select in their respective dimension.
	 */
	public FormatRangeInfo(Dimension [] dims, int [] levels) {
		this.cells = null;
		levelDims = new LinkedHashMap <Dimension, Integer> ();
		for (int i = 0; i < dims.length; i++) {
			levelDims.put(dims[i], levels[i]);
		}
	}
		
	/**
	 * Internal constructor that creates a FormatRangeInfo from a specification
	 * in an xml document.
	 * 
	 * @param dims the dimensions for which the FormatRangeInfo is valid.
	 * @param description a description string which contains the
	 * FormatRangeInfo.
	 * @param hasCoordinates true if cells are individually represented, false
	 * if levels of dimensions are given.
	 */
	public FormatRangeInfo(Dimension [] dims, String description, boolean hasCoordinates) {
		if (hasCoordinates) {
			String [] coords = description.split(";");
			int length = coords.length;
			cells = new Element[length][];
			for (int i = 0; i < length; i++) {
				cells[i] = restoreFrom(dims, coords[i]);
			}
			levelDims = null;
		} else {
			cells = null;
			levelDims = new LinkedHashMap <Dimension, Integer> ();
			String [] levels = description.split(";");
			int length = levels.length;
			for (int i = 0; i < length; i++) {
				levelDims.put(dims[i], Integer.parseInt(levels[i]));
			}
		}
	}	
	
	/**
	 * Returns the elements specified by their ids in the description string.
	 * 
	 * @param dims the dimensions from which the elements are restored.
	 * @param desc the description string.
	 * @return the elements specified by their ids in the description string.
	 */
	private Element [] restoreFrom(Dimension [] dims, String desc) {
		String [] ids = desc.split(",");
		Element [] r = new Element[ids.length];
		for (int i = 0, n = ids.length; i < n; i++) {
			r[i] = dims[i].getElementById(ids[i]);
		}
		return r;
	}
			
	/**
	 * Copies this FormatRangeInfo.
	 * @return a copy of this FormatRangeInfo.
	 */
	public final FormatRangeInfo copy() {
		return new FormatRangeInfo(this);
	}
	
	/**
	 * Returns a hash code for this FormatRangeInfo object.
	 */
	public int hashCode() {
		int sum = 0;
		if (cells == null) {
			if (levelDims != null) {
				for (Dimension d: levelDims.keySet()) {
					sum += d.hashCode();
					sum += levelDims.get(d) * 17;
				}
			}
		} else {
			sum += 17;
			for (Element [] coord: cells) {
				sum += coord.hashCode();
			}
		}
		return sum;
	}
	
	/**
	 * Checks, if two coordinates are the same.
	 * 
	 * @param p1 first coordinate.
	 * @param p2 second coordinate.
	 * @return true if p1 references the same coordinate as p2, false otherwise.
	 */
	private final boolean coordsEquals(Element [] p1, Element [] p2) {
		if (p1 == null) {
			return p2 == null;
		}
		if (p1.length != p2.length) {
			return false;
		}
		for (int i = 0, n = p1.length; i < n; i++) {
			if (!p1[i].equals(p2[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if two FormatRangeInfo objects are equal.
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof FormatRangeInfo)) {
			return false;
		}
		FormatRangeInfo f = (FormatRangeInfo) o;
	
		if (cells == null && f.cells != null) {
			return false;
		}
		if (cells == null) {
			if (levelDims == null && f.levelDims != null) {
				return false;
			} else if (levelDims == null) {
				return true;
			}
			for (Dimension d: levelDims.keySet()) {
				if (!f.levelDims.containsKey(d)) {
					return false;
				}
				if (!levelDims.get(d).equals(f.levelDims.get(d))) {
					return false;
				}
			}
			return true;
		}
		if (cells.length != f.cells.length) {
			return false;
		}
		for (int i = 0; i < cells.length; i++) {
			if (!coordsEquals(cells[i], f.cells[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a description string for the given coordinate.
	 * @param e the coordinate.
	 * @return description string for this coordinate.
	 */
	private String getCoordinateString(Element [] e) {
		StringBuffer buffer = new StringBuffer();		
		for (int i = 0, n = e.length; i < n; i++) {
			buffer.append(e[i].getId());
			if (i < (n - 1)) {
				buffer.append(",");
			}
		}	
		return buffer.toString();
	}
	
	/**
	 * Returns the cells directly specified in this FormatRangeInfo.
	 * May be null.
	 * 
	 * @return the cells directly specified in this FormatRangeInfo or null if
	 * only levels of dimensions have been specified.
	 */
	public Element [][] getCells() {
		return cells;
	}
	
	/**
	 * Returns the level for which this format range info applies or -1 if no
	 * level has been assigned to the given dimension.
	 * 
	 * @param d the dimension for which the level is requested.
	 * @return the level of the dimension for which this range info should be
	 * active or -1 if no such level has been set.
	 */
	public int getLevel(Dimension d) {
		if (levelDims == null || !levelDims.containsKey(d)) {
			return -1;
		}
		return levelDims.get(d);
	}
	
	/**
	 * Returns all dimensions that contain level information for which this
	 * FormatRangeInfo should be active.
	 * 
	 * @return all dimensions for this RangeInfo.
	 */
	public Dimension [] getDimensions() {
		if (levelDims == null) {
			return new Dimension[0];
		}
		return levelDims.keySet().toArray(new Dimension[0]);
	}
		
	/**
	 * Returns a string representation of this FormatRangeInfo.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();		
		if (cells != null) {
			for (Element [] coord: cells) {
				buffer.append(getCoordinateString(coord));
				buffer.append(";");
			}
		} else {
			if (levelDims != null) {
				for (Dimension d: levelDims.keySet()) {
					buffer.append(levelDims.get(d));
					buffer.append(";");
				}
			}
		}
		if (buffer.length() > 0) {
			return buffer.substring(0, buffer.length() - 1).toString();
		}
		return "";
	}
}
