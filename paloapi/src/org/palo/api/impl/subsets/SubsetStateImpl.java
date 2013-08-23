/*
*
* @file SubsetStateImpl.java
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
* @version $Id: SubsetStateImpl.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2006. All rights reserved.
 */
package org.palo.api.impl.subsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.SubsetState;

/**
 * A default implementation for a {@link SubsetState}.
 * 
 * @author ArndHouben
 * @version $Id: SubsetStateImpl.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
class SubsetStateImpl implements SubsetState {

//	private static final String PATH_DELIM = ":";
	
	private final String id;
	
	private String name;
	private String expression;
	private Attribute searchAttribute;
//	private final List visibleElements = new ArrayList();
	private final Set visibleElements = new HashSet(); //promised to be faster...
	private final Map elPaths = new HashMap(); 
	private final Map positions = new HashMap();
	
	/**
	 * Creates a new {@link SubsetState} instance.
	 * @param id the unique id for this subset state
	 */
	SubsetStateImpl(String id) {
		this.id = id;
	}
	
	public final synchronized void setExpression(String expression) {
		this.expression = expression;
	}

	public final void addVisibleElment(Element element) {
		addVisibleElement(element,-1);
	}
	
	public final void addVisibleElement(Element element, int position) {
		addPosition(element, Integer.toString(position));
		visibleElements.add(element);
	}
	
	public final void removeVisibleElement(Element element) {
		visibleElements.remove(element);
		elPaths.remove(element);
		positions.remove(element);
	}
	
	public final void removeAllVisibleElements() {
		visibleElements.clear();
		elPaths.clear();
		positions.clear();
	}
	
	public final synchronized String getExpression() {
		return expression;
	}

	public final String getId() {
		return id;
	}

	public final synchronized  void setName(String name) {
		this.name = name;
	}
	
	public final synchronized String getName() {
		return name;
	}

	public final Element[] getVisibleElements() {
		return (Element[]) visibleElements.toArray(
				new Element[visibleElements.size()]);
	}

	public final synchronized Attribute getSearchAttribute() {
		return searchAttribute;
	}

	public final synchronized void setSearchAttribute(Attribute searchAttribute) {
		this.searchAttribute = searchAttribute;
	}

	public final String[] getPaths(Element element) {
		Set paths = (Set)elPaths.get(element);
		if(paths == null)
			return new String[0];
		return (String[])paths.toArray(new String[paths.size()]);
	}

	public final void addPath(Element element, String path) {
		if(!elPaths.containsKey(element))
			elPaths.put(element,new HashSet());
		Set paths = (Set)elPaths.get(element);
		paths.add(path);
	}

	public final void removePath(Element element, String path) {
		if(!elPaths.containsKey(element))
			return;
			
		Set paths = (Set)elPaths.get(element);
		paths.remove(path);
	}

	public final boolean containsPath(Element element, String path) {
		Set paths = (Set)elPaths.get(element);
		if(paths == null)
			return false;
		return paths.contains(path);
	}

	public final int[] getPositions(Element element) {
		String[] positions = getPositionsInternal(element);
		int[] _pos = new int[positions.length];
		for(int i=0;i<positions.length;++i)
			_pos[i] = Integer.parseInt(positions[i]);
		return _pos;
	}
	
	public final boolean isVisible(Element element) {
		return visibleElements.contains(element);
	}
	
	final void setPaths(Element element, String paths) {
		if(paths == null || paths.equals(""))
			return;
		String[] _paths = paths.split(SubsetPersistence.PATH_DELIM);
		for(int i=0;i<_paths.length;++i) {
			addPath(element,_paths[i]);
		}		
	}
	
	final String getPathsAsString(Element element) {
		if(!elPaths.containsKey(element))
			return null;
		StringBuffer paths = new StringBuffer();
		String[] allPaths = getPaths(element);
		int lastPath = allPaths.length-1;
		for(int i=0;i<allPaths.length;++i) {
			paths.append(allPaths[i]);
			if(i<lastPath)
				paths.append(SubsetPersistence.PATH_DELIM);			
		}
		return paths.toString();
	}

	final String getPositionsAsString(Element element) {
		String[] positions = getPositionsInternal(element);
		StringBuffer posStr = new StringBuffer();
		int lastPos = positions.length-1;
		for(int i=0;i<positions.length;++i) {
			posStr.append(positions[i]);
			if(i<lastPos)
				posStr.append(SubsetPersistence.ELEMENT_DELIM);
		}
		return posStr.toString();
	}
	
	final void setPosition(Element element, String posStr) {
		if(posStr == null || posStr.equals(""))
			return;
		String[] allPositions = posStr.split(SubsetPersistence.ELEMENT_DELIM);
		for(int i=0;i<allPositions.length;++i) {
			addPosition(element, allPositions[i]);
		}		
	}	
	
	final String[] getPositionsInternal(Element element) {
		List allPositions = (List)positions.get(element);
		if(allPositions != null) {
			return (String[])allPositions.toArray(new String[allPositions.size()]);
		}
		return new String[0];
	}

	private final void addPosition(Element element, String position) {
		if(position.equals("-1"))
			return;
		List allPositions = (List)positions.get(element);
		if(allPositions == null) {
			allPositions = new ArrayList();
			positions.put(element,allPositions);
		}
		if(!allPositions.contains(position))
			allPositions.add(position);

	}
}
