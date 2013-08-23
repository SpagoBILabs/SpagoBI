/*
*
* @file XDelta.java
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
* @version $Id: XDelta.java,v 1.7 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XDelta</code>
 * A simple delta model representation for GWTs RPC feature.
 * This model contains all required data which accumulates during loading of
 * a header item.
 *
 * @version $Id: XDelta.java,v 1.7 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class XDelta extends XObject {

	//the parent item which children should be loaded
	private XAxisItem parent;
	
	//loaded items:
	private List<XAxisItem> items = new ArrayList<XAxisItem>();
	private List<XAxisItem> collapsedItems = new ArrayList<XAxisItem>();
	
	//loaded cells:
	private XCellCollection cells;

	public XDelta() {		
	}
	public XDelta(XAxisItem parent) {
		this.parent = parent;
	}
	
	public final boolean isEmpty() {
		return cells == null || cells.isEmpty();
	}
	
	/**
	 * Returns the item which children where loaded
	 * @return the parent item
	 */
	public final XAxisItem getParent() {
		return parent;
	}
	
	/**
	 * Adds the given item model to this load delta.
	 * @param item the item model to add
	 */
	public final void add(XAxisItem item) {
		items.add(item);
	}

	public final void addCollapsed(XAxisItem item) {
		collapsedItems.add(item);
	}

	/**
	 * Returns a list of all item models which where added during loading
	 * @return a list of all loaded items
	 */
	public final List<XAxisItem> getItems() {
		return items;
	}
	public final List<XAxisItem> getCollapsedItems() {
		return collapsedItems;
	}
	
	public final XCellCollection getCells() {
		return cells;
	}
	public final void setCells(XCellCollection cells) {
		this.cells = cells;
	}
	public final void addCells(XCellCollection cells) {
		this.cells.add(cells);
	}
	public String getType() {
		return getClass().getName();
	}

}
