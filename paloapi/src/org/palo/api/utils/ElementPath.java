/*
*
* @file ElementPath.java
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
* @version $Id: ElementPath.java,v 1.16 2009/09/28 15:03:41 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;

/**
 * <p>
 * An <code>ElementPath</code> is a distinct reference to an 
 * <code>{@link Element}</code> inside a structure of multiple
 * <code>{@link Dimension}</code>s. Therefore a path could be build up of 
 * several so called parts where each part describes a path inside a single 
 * <code>{@link Dimension}</code>.
 * </p>
 * <p> 
 * <b>NOTE:</b> the assemble of the <code>ElementPath</code> is determined by 
 * the order at which the single parts are added. It is therefore important
 * to add the path parts of each dimension according to the dimension hierarchy.
 * </p>
 * 
 * The {@link #toString()} and the {@link #restore(Dimension[], String)} can 
 * be used to save and restore an <code>ElementPath</code>.
 * 
 * @author ArndHouben
 * @version $Id: ElementPath.java,v 1.16 2009/09/28 15:03:41 PhilippBouillon Exp $
 **/
public class ElementPath {
	
	public static final String ELEMENT_DELIM = ",";
	public static final String DIMENSION_DELIM = ":";
	public static final String DIM_HIER_DELIM = "~~~";
	
	private final Map<Hierarchy, Element[]> parts = new LinkedHashMap<Hierarchy, Element[]>();
	
	/**
	 * Default constructor which creates an empty <code>ElementPath</code>
	 * instance.
	 */
	public ElementPath() {
		this((Element[])null);
	}
	
	/**
	 * Creates an <code>ElementPath</code> instance which consists of the
	 * given {@link Element}s.
	 * @param elements the path <code>Element</code>s
	 */
	public ElementPath(Element[] elements) {
		init(elements);
	}
	
	/** a copy constructor */
	private ElementPath(ElementPath other) {
		Element[] path = other.getComplete();
		init(path.clone());
	}
	
	/**
	 * Adds a new part to this element path with the given path for the 
	 * specified <code>{@link Dimension}</code>
	 * @param dimension
	 * @param pathPart
	 */
	public final void addPart(Dimension dimension, Element[] path) {
		parts.put(dimension.getDefaultHierarchy(),path);
	}

	public final void addPart(Hierarchy hierarchy, Element [] path) {
		parts.put(hierarchy, path);
	}
	
	public final Element[] getComplete() {
		ArrayList<Element> elements = new ArrayList<Element>();
		Set<Hierarchy> hierarchies = parts.keySet();
		for(Hierarchy hierarchy : hierarchies) {
			Element[] els = parts.get(hierarchy);
			elements.addAll(Arrays.asList(els));
		}
		return elements.toArray(new Element[0]);
	}

	/**
	 * Checks if the given <code>{@link Dimension}</code> is part of this
	 * element paths.
	 * @return <code>true</code> if <code>{@link Dimension}</code> is part of
	 * this element paths, <code>false</code> otherwise
	 */
	public final boolean contains(Dimension dimension) {
		return parts.containsKey(dimension);
	}
	
	public final boolean contains(Hierarchy hierarchy) {
		return parts.containsKey(hierarchy);
	}
	
	/**
	 * Returns all <code>{@link Dimension}</code>s which are part of this 
	 * element path.
	 * @return all <code>{@link Dimension}</code>s which build up this path
	 */
	public final Dimension[] getDimensions() {
		Hierarchy [] hiers = getHierarchies();
		Dimension [] result = new Dimension[hiers.length];
		int counter = 0;
		for (Hierarchy hier: hiers) {
			result[counter++] = hier.getDimension();
		}
		return result;
	}

	public final Hierarchy[] getHierarchies() {
		Set<Hierarchy> hierarchies = parts.keySet();
		return hierarchies.toArray(new Hierarchy[hierarchies.size()]);
	}

	/**
	 * Returns the path part which corresponds to the given 
	 * <code>{@link Dimension}</code>
	 * @param dimension the <code>{@link Dimension}</code> which contains the
	 * path part
	 * @return the path part as an array of <code>{@link Element}</code>s
	 * @throws PaloAPIException if no path part could be determined, i.e. the
	 * given <code>{@link Dimension}</code> is not part of this element path
	 */
	public final Element[] getPart(Dimension dimension) throws PaloAPIException {
		return getPart(dimension.getDefaultHierarchy());
	}

	public final Element[] getPart(Hierarchy hierarchy) throws PaloAPIException {
		if (!parts.containsKey(hierarchy))
			throw new PaloAPIException("Unknown hierarchy '" + hierarchy
					+ "' for this element path");
		return parts.get(hierarchy);
	}

	public final String toString() {
		StringBuffer path = new StringBuffer();
		Hierarchy [] hiers = getHierarchies();
		int lastHier = hiers.length-1;
		for(int i=0;i<hiers.length;++i) {
			appendPart(getPart(hiers[i]),path);
			if(i<lastHier)
				path.append(DIMENSION_DELIM);
		}
		return path.toString();
	}

	/**
	 * Returns a copy of this element path instance
	 * @return a copy of this element path
	 */
	public ElementPath copy() {
		return new ElementPath(this);
	}
	
	public final boolean equals(Object obj) {
		if(obj instanceof ElementPath) {
			ElementPath other = (ElementPath)obj;
			return getFingerPrint().equals(other.getFingerPrint());
		}
		return false;
	}
	
	public final int hashCode() {
		return getFingerPrint().hashCode();
	}
	
	/**
	 * Restores the element path specified by the given 
	 * <code>{@link Dimension}</code>s and element path string
	 * @param dimensions the code>{@link Dimension}</code>s which build up this 
	 * element path
	 * @param pathStr an element path definition string
	 * @return a new element path instance
	 */
	public static final ElementPath restore(Dimension[] dimensions,
			String pathStr) {
		Hierarchy [] hiers = new Hierarchy[dimensions.length];
		for (int i = 0, n = dimensions.length; i < n; i++) {
			hiers[i] = dimensions[i].getDefaultHierarchy();
		}
		return restore(hiers, pathStr);
	}

	public static final ElementPath restore(Hierarchy [] hierarchies,
			String pathStr) {
		ElementPath path = new ElementPath();
		String[] parts = pathStr.split(DIMENSION_DELIM);
		for (int i = 0; i < parts.length; ++i) {
			path.addPart(hierarchies[i], parts[i].split(ELEMENT_DELIM));
		}
		return path;
	}
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//--------------------------------------------------------------------------
	private final void init(Element[] elements) {
		if(elements != null && elements.length > 0) {
			Hierarchy lastHierarchy = elements[0].getHierarchy();
			ArrayList<Element> parts = new ArrayList<Element>();
			for(Element element : elements) {
				if (element == null) {
					continue;
				}
				if(!element.getHierarchy().equals(lastHierarchy)) {
					addPart(lastHierarchy, parts.toArray(new Element[0]));
					lastHierarchy = element.getHierarchy();
					parts.clear();
				}
				parts.add(element);					
			}
			if(!parts.isEmpty()) {
				addPart(lastHierarchy, parts.toArray(new Element[0]));
			}
		}
	}
	
	/**
	 * Adds the path part, specified by the given element ids to this element 
	 * path
	 * @param dimension
	 * @param elIDs
	 */
	private final void addPart(Hierarchy hierarchy, String[] elIDs)
			throws PaloAPIException {
		Element[] elements = new Element[elIDs.length];
		for (int i = 0; i < elIDs.length; ++i) {
			Element element = hierarchy.getElementById(elIDs[i]);
			Element [] allElements = hierarchy.getElements();
			if (element == null) {
				PaloAPIException exception = new PaloAPIException("Could not find element with id '"
						+ elIDs[i] + "' in hierarchy '" + hierarchy.getName()
						+ "'!!");
				exception.setData(new Object[]{hierarchy,elIDs[i]});
				throw exception;
			}
			elements[i] = element;
		}
		parts.put(hierarchy, elements);
	}
	

	private final void appendPart(Element[] elements, StringBuffer path) {
		int lastElement = elements.length-1;
		for(int i=0;i<elements.length;++i) {
			if (elements[i] != null) {
				path.append(elements[i].getId());
				if(i<lastElement)
					path.append(ELEMENT_DELIM);
			}
		}
	}
	
	private final String getFingerPrint() {
		StringBuffer str = new StringBuffer();
		Hierarchy[] hierarchies = getHierarchies();
		int lastHier = hierarchies.length-1;
		for(int i=0;i<hierarchies.length;++i) {
			str.append("[");
			str.append(hierarchies[i].getId());
			str.append("]");
			Element[] elements = getPart(hierarchies[i]);
			int lastElement = elements.length-1;
			for(int e=0;e<elements.length;++e) {
				str.append(elements[e].getId());
				if(e<lastElement)
					str.append(ELEMENT_DELIM);
			}
			if(i<lastHier)
				str.append(DIMENSION_DELIM);
		}
		return str.toString();
	}

	
}