/*
*
* @file XAxisItem.java
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
* @version $Id: XAxisItem.java,v 1.8 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.Axis;
import org.palo.viewapi.uimodels.axis.AxisItem;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementType;

/**
 * <code>XAxisItem</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XAxisItem.java,v 1.8 2010/03/11 10:42:20 PhilippBouillon Exp $
 **/
public class XAxisItem extends XObject {

	//tree structure:	
	private XAxisItem parent;
	private String hierarchyId;	
	private List<XAxisItem> children = new ArrayList<XAxisItem>();
	private List<XAxisItem> rootsInNextHier = new ArrayList<XAxisItem>();

	//state:
	public int index;
	public int level;	//the hierarchy level to which this item belongs
	public int depth;	//the depth of this item within its hierarchy
	public int leafIndex; 
	public int columns;	//actual number of loaded columns
	public String name;
	public boolean isLoaded;
	public boolean isExpanded;
	public XElementType elementType;
	
	private String path;
	private static long ID;
	
	public XAxisItem() {
	}

	/**
	 * Creates a new {@link XViewItem} for the specified path with the given 
	 * name.
	 * @param path the items path within its axis
	 * @param name the items name
	 */
	public XAxisItem(String path, String name, String hierarchyId) { //, XAxisItem parentInPrevHierarchy) {
		setId(Long.toString(ID++));
		setName(name);
		this.path = path;
		this.hierarchyId = hierarchyId;
//		this.parentInPrevHierarchy = parentInPrevHierarchy;
	}

	public final XAxisItem copy() {
		XAxisItem copy = new XAxisItem(path, name, hierarchyId);
		copy.parent = parent != null ? parent.copy() : null;
		return copy;
	}
	
	public final String getType() {
		return 	getClass().getName();
	}
	
	public final String getHierarchyId() {
		return hierarchyId;
	}
	
	/**
	 * Returns the items path within its {@link Axis}
	 * @return the items path
	 */
	public final String getPath() {
		return path;
	}
	
	public final XAxisItem getParent() {
		return parent;
	}
	public final void setParent(XAxisItem parent) {
		this.parent = parent;
	}
	/**
	 * Adds the given item as a child of this item model. 
	 * @param item the child to add
	 */
	public final void addChild(XAxisItem item) {		
		if(item != null) {
			item.setParent(this);
			if(item.depth == 0)
				item.depth = this.depth + 1;
			children.add(item);
			this.setHasChildren(true);
		}		
	}
	
	/**
	 * Adds the given item as a root in next hierarchy of this item model. 
	 * @param item the next hierarchy root to add
	 */
	public final void addRootInNextHierarchy(XAxisItem item) {
		if(item != null) {
			rootsInNextHier.add(item);
		}
	}

	/**
	 * Returns a list of all added children
	 * @return the children of this item
	 */
	public final List<XAxisItem> getChildren() {
		return children;
	}
	
	/**
	 * Returns a list of all added next hierarchy root items
	 * @return the next hierarchy root items
	 */
	public final List<XAxisItem> getRootsInNextHier() {
		return rootsInNextHier;
	}
}
