/*
*
* @file ElementInfoImpl.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: ElementInfoImpl.java,v 1.5 2010/02/26 10:10:01 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.impl;

import java.util.ArrayList;

import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;


public class ElementInfoImpl implements ElementInfo {
	
	private final String id;	
	private final DimensionInfo dimension;
	
	private int type;
	private String name;
	private int position;
	private int level;
	private int indent;
	private int depth;
	private String[] parentIds;
	private String[] childrenIds;
	private double[] weights;
	
	public ElementInfoImpl(DimensionInfo dimension, String id) {
		this.id = id;
		this.dimension = dimension;
		this.parentIds = new String[0];
	}

	public final synchronized String[] getChildren() {
		return childrenIds;
	}

	public final synchronized int getChildrenCount() {
		return childrenIds.length;
	}

	public final synchronized int getDepth() {
		return depth;
	}

	public final DimensionInfo getDimension() {
		return dimension;
	}

	public final synchronized int getIndent() {
		return indent;
	}
	
	public final synchronized int getLevel() {
		return level;
	}

	public final synchronized String getName() {
		return name;
	}

	public final synchronized int getParentCount() {
		return parentIds.length;
	}

	public final synchronized String[] getParents() {
		return parentIds;
	}

	public final synchronized int getPosition() {
		return position;
	}

	public final synchronized double[] getWeights() {
		return weights;
	}

	public final synchronized void setChildren(String[] children,
			double[] weights) {
		childrenIds = children;
		this.weights = weights;
	}
		
	public final synchronized void setDepth(int depth) {
		this.depth = depth;
	}

	public final synchronized void setIndent(int indent) {
		this.indent = indent;
	}
	
	public final synchronized void setLevel(int newLevel) {
		level = newLevel;
	}

	public final synchronized void setName(String name) {
		this.name = name;
	}

	public final synchronized void setParents(String[] parents) {
		parentIds = parents;
	}

//	public final synchronized void addParent(String parentId) {
//		parentIds.add(parentId);
//	}
	
//	public final synchronized void removeParent(String parentId) {
//		parentIds.remove(parentId);
//	}
	
	public final synchronized void setPosition(int newPosition) {
		this.position = newPosition;
	}

	public final synchronized void setType(int type) {
		this.type = type;
	}

	public final String getId() {
		return id;
	}

	public final synchronized int getType() {
		return type;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}

	public void update(String[] children) {
		ArrayList <String> cIds = new ArrayList<String>();
		ArrayList <Double> wght = new ArrayList<Double>();
		
		if (children != null) {
			int counter = 0;
			int counter2 = 0;
			for (String s: childrenIds) {
				if (counter >= children.length) {
					break;
				}
				if (s.equals(children[counter])) {
					cIds.add(s);
					wght.add(weights[counter2]);
					counter++;
				}
				counter2++;
			}
		}
		
		int counter = 0;
		weights = new double[wght.size()];
		for (Double d: wght) {
			weights[counter++] = d;
		}
		
		childrenIds = cIds.toArray(new String[0]);
		
	}
}
