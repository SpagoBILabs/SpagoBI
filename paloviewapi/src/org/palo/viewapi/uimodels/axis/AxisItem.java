/*
*
* @file AxisItem.java
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
* @version $Id: AxisItem.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

//import org.palo.api.Element;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Property;


/**
 * <code>AxisItem</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisItem.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class AxisItem {
	
	//some default property ids:
	/** value for column (or row) width in 1/10 mm */
	public static final String PROPERTY_COLUMN_WIDTH = "com.tensegrity.palo.axisitem.columnwidth";
	
	//STATES:
	public static final int EXPANDED = 2;
	public static final int CACHED = 4;
	
	/** optional creation index */ 
	public int index = -1;
	/** optional leaf index */
	public int leafIndex = -1;
	
//	private final Element element;
	private final ElementNode node;
	//the internal item state:
	private int state = 0;
	private int level = 0;
	//connections:
	private AxisItem parent;
	private AxisItem parentInPrevHier;
	private final Set<AxisItem> children = new LinkedHashSet<AxisItem>();
	private final Set<AxisItem> rootsInNextHier = new LinkedHashSet<AxisItem>();
	private final HashMap<String, Property<?>> properties;
	
	public AxisItem(ElementNode node, int level) {
		this.node = node;
		this.level = level;
//		this.element = node.getElement();
		this.properties = new HashMap<String, Property<?>>();
		addProperty(new Property<Double>(PROPERTY_COLUMN_WIDTH, new Double(100)));
	}
	
	public final Element getElement() {
		return node.getElement();
	}
	public final ElementNode getElementNode() {
		return node;
	}
	
	public final String getName() {
		return node.getName();
	}
	
	public final Hierarchy getHierarchy() {
		return getElement().getHierarchy();
	}
	
	public final int getLevel() {
		return level;
	}
	
	/**
	 * Returns the path for this <code>AxisItem</code> based on its element ids.
	 * The returned path is suitable to create an {@link ElementPath} object 
	 * from.
	 * @return constructs the path for this <code>AxisItem</code>.
	 */
	public final String getPath() {
		StringBuffer path = new StringBuffer();
		if(parentInPrevHier != null && parent == null) {
			path.append(parentInPrevHier.getPath());
			path.append(ElementPath.DIMENSION_DELIM);
		}
		if(parent != null) {
			path.append(parent.getPath());
			path.append(ElementPath.ELEMENT_DELIM);
		}
		path.append(getElement().getId());
		return path.toString();
	}
	
	//parent...
	public final void setParent(AxisItem parent) {
		this.parent = parent;
	}
	public final AxisItem getParent() {
		return parent;
	}

	public final void setParentInPreviousHierarchy(AxisItem parentInPrevHier) {
		this.parentInPrevHier = parentInPrevHier;
	}
	public final AxisItem getParentInPrevHierarchy() {
		return parentInPrevHier;
	}

	//children...
	public final boolean hasChildren() {
		return getEstimatedChildrenCount() > 0;
	}
	public final void addChild(AxisItem item) {
		children.add(item);
	}
	public final void removeChild(AxisItem item) {
		if(children != null)
			children.remove(item);
	}
	public final AxisItem[] getChildren() {
		return children.toArray(new AxisItem[0]);
	}
	public final void removeChildren() {
		children.clear();
		deleteState(CACHED);
	}
	public final int getEstimatedChildrenCount() {
		return node.getChildren().length;
//		return getElement().getChildCount();
	}

	
	public final AxisItem[] getRootsInNextHierarchy() {
		return rootsInNextHier.toArray(new AxisItem[0]);
	}
	public final void addRootInNextHierarchy(AxisItem item) {
		rootsInNextHier.add(item);
	}
	public final void replaceRootInNextHierarchy(AxisItem item,
			AxisItem[] replacement) {
		ArrayList<AxisItem> tmp = new ArrayList<AxisItem>(rootsInNextHier);
		int index = tmp.indexOf(item);
		for(int i=0;i<replacement.length;++i)
			tmp.add(index+i, replacement[i]);
		tmp.remove(item);
		rootsInNextHier.clear();
		rootsInNextHier.addAll(tmp);
	}
	public final void setRootsInNextHierarchy(AxisItem [] roots) {
		rootsInNextHier.clear();
		for (AxisItem r: roots) {
			rootsInNextHier.add(r);
//			r.setParentInPreviousHierarchy(this);
		}
	}
	public final void removeRootInNextHierarchy(AxisItem item) {
		rootsInNextHier.remove(item);
	}
	public final void removeAllRootsInNextHierarchy() {
		rootsInNextHier.clear();
	}
	public final boolean hasRootsInNextHierarchy() {
		return !rootsInNextHier.isEmpty();
	}
	
	/** 
	 * returns <code>true</code> if an update was done, <code>false</code>
	 * to signal that item is cached and therefore was updated already...
	 * @return
	 */
	public final boolean update(){
		//loads this item and set its state to cached:
		if(hasState(CACHED))
			return false;

		setState(CACHED);
		//load all children:
		ElementNode[] children = node.getChildren();
		int childIndex = index;
		for(ElementNode child : children) {
			AxisItem _child = new AxisItem(child, level+1);
			_child.index = ++childIndex;
			_child.setParent(this);
			addChild(_child);				
		}
		return true;
//		Element[] children = element.getChildren();
//		for(Element child : children) {
//			AxisItem _child = new AxisItem(child, level+1);
//			_child.setParent(this);
//			addChild(_child);				
//		}
	}
	
	public final String toString() {
		StringBuffer str = new StringBuffer();
		str.append("AxisItem(\"");
		str.append(getName());
		str.append("\")[");
		str.append(hashCode()); //getId());
		str.append("]");
		return str.toString();
	}

	//state handling...
	public final void setState(int stateBit) {
		state |= stateBit;
	}
	public final void deleteState(int stateBit) {
		state &= ~stateBit;
	}	
	public final boolean hasState(int stateBit) {
		return (state & stateBit) >= stateBit;
	}

	public final AxisItem copy() {
		AxisItem copy = new AxisItem(node, level);
		copy.index = index;
		copy.state = state;
		copy.parent = parent != null ? parent.copy() : null;
		return copy;
	}
	
	public final void addProperty(Property<?> property) {
		properties.put(property.getId(), property);
	}
	public final void removeProperty(Property<?> property) {
		properties.remove(property.getId());
	}
	public final Property<?>[] getProperties() {
		return properties.values().toArray(new Property[0]);
	}
	public final String[] getPropertyIDs() {
		return properties.keySet().toArray(new String[0]);
	}
	public final Property<?> getProperty(String id) {
		return properties.get(id);
	}
	

	public final boolean equals(Object obj) {
		if(obj instanceof AxisItem) {
			AxisItem other = (AxisItem) obj;
			return getPath().equals(other.getPath()) && index == other.index;
		}
		return false;
	}
}
