/*
*
* @file CellImpl.java
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
* @version $Id: CellImpl.java,v 1.14 2010/02/26 10:10:01 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl;

import org.palo.api.Cell;
import org.palo.api.Cube;
import org.palo.api.Element;

import com.tensegrity.palojava.CellInfo;

/**
 * {@<describe>}
 * <p>
 * <code>Cell</code> interface implementation
 * </p>
 * {@</describe>}
 *
 * @author ArndHouben
 * @version $Id: CellImpl.java,v 1.14 2010/02/26 10:10:01 PhilippBouillon Exp $
 */
class CellImpl implements Cell {

	private final Cube cube;	
	private final CellInfo cInfo;
	private CompoundKey coordKey;
	private final Element[] coordinate;	
	
	CellImpl(Cube cube, CellInfo cInfo) {
		this(cube, cInfo, null);
	}
	
	CellImpl(Cube cube, CellInfo cInfo, Element[] coordinate) {
		this.cube = cube;
		this.cInfo = cInfo;
		this.coordinate = coordinate != null ? coordinate.clone() : getCoordinate(cInfo);
		String[] ids = new String[this.coordinate.length];
		for(int i=0; i<this.coordinate.length; ++i)
			ids[i] = this.coordinate[i].getId();
		coordKey = new CompoundKey(ids);
	}
	
	public final Cube getCube() {
		return cube;
	}
	
	public final Element[] getCoordinate() {
		return coordinate;
	}
	
	public final Element[] getPath() {
		return coordinate;
	}

	public String getRuleId() {
		return cInfo.getRule();
	}

	public boolean hasRule() {
		return cInfo.getRule()!=null;
	}

	public final int getType() {
		return cInfo.getType();
	}

	public final Object getValue() {
		return cInfo.getValue();
	}

//FOLLOWING METHODS WILL MAKE CELL REPRESENTATION STALE SINCE ITS VALUE AND	
//SERVER VALUE DOESN'T MATCH AFTERWARDS....
//	public final void setValue(Object value) {
//		cube.setData(coordinate, value);
//	}
//	
//	public final void setValue(Object value, int splashMode) {
//		cube.setDataSplashed(coordinate, value, splashMode);
//	}
//	
//	public final void clear() {
//		Element[][] area = new Element[coordinate.length][];
//		for (int i = 0; i < area.length; ++i) 
//			area[i] = new Element[] { coordinate[i] };
//		cube.clear(area);
//	}
	
	public final boolean isConsolidated() {
		boolean consolidated = false;
		for (Element element : coordinate) {
			switch (element.getType()) {
			case Element.ELEMENTTYPE_CONSOLIDATED:
				consolidated = true;
				break;
			case Element.ELEMENTTYPE_RULE:
			case Element.ELEMENTTYPE_STRING:
				return false;
			}
		}
		return consolidated;
	}
	
	public final boolean isEmpty() {
		Object val = getValue();
		return val == null || val.toString().equals("");
	}
	
	public final boolean equals(Object obj) {
		if(obj instanceof Cell) {
			CellImpl other = (CellImpl)obj;
			return cube.equals(other.cube) && coordKey.equals(other.coordKey);
		}
		return false;
	}
	public final int hashCode() {
		int hc = 23;
		hc += 37 * cube.hashCode();
		hc += 37 + coordKey.hashCode();
		return hc;
	}

	// --------------------------------------------------------------------------
	// PACKAGE INTERNAL
	//
	final CellInfo getInfo() {
		return cInfo;
	}

	/** 
	 * tries to get the cell coordinate from the cell info object. if no 
	 * coordinate was specified an empty array is returned.
	 */
	private final Element[] getCoordinate(CellInfo cInfo) {
		String[] ids = cInfo.getCoordinate();
		// set coordinate:
		if (ids != null) {
			Element[] coordinate = new Element[ids.length];
			// TODO figure out a way to get the active hierarchy...
			for (int i = 0; i < ids.length; ++i) {
				coordinate[i] = cube.getDimensionAt(i).getDefaultHierarchy()
						.getElementById(ids[i]);
			}
			return coordinate;
		}
		return new Element[0];
	}
}
