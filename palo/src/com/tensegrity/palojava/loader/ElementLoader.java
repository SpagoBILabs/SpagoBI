/*
*
* @file ElementLoader.java
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
* @version $Id: ElementLoader.java,v 1.9 2010/01/12 14:37:03 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PaloInfo;

/**
 * <p><code>ElementInfoLoader</code></p>
 * This abstract base class manages the loading of {@link ElementInfo} objects.
 *
 * @author ArndHouben
 * @version $Id: ElementLoader.java,v 1.9 2010/01/12 14:37:03 PhilippBouillon Exp $
 **/
public abstract class ElementLoader extends PaloInfoLoader {

	protected final HierarchyInfo hierarchy;
	
	/**
	 * Creates a new loader instance.
	 * @param paloConnection
	 * @param dimension
	 */
	public ElementLoader(DbConnection paloConnection, HierarchyInfo hierarchy) {
		super(paloConnection);
		this.hierarchy = hierarchy;
	}

	/**
	 * Returns the identifiers of all elements currently known to the palo 
	 * server.
	 * @return ids of all known palo elements
	 */
	public abstract String[] getAllElementIds();
	/**
	 * Loads the <code>ElementInfo</code> object by its name
	 * @param name the name of the <code>ElementInfo</code> to load
	 * @return the loaded <code>ElementInfo</code> instance
	 */
	public abstract ElementInfo loadByName(String name);

	/**
	 * Loads the <code>ElementInfo</code> object at the specified index
	 * @param index the index of the <code>ElementInfo</code> object to load
	 * @return the loaded <code>ElementInfo</code> object
	 */	
	public abstract ElementInfo load(int index);

	public abstract ElementInfo[] getElementsAtDepth(int depth);
	public abstract ElementInfo[] getChildren(ElementInfo parent);
	
	/**
	 * Creates a new {@link ElementInfo} instance with the given name, type,
	 * children and weights
	 * @param name the element name
	 * @param type the element type
	 * @param children the element children
	 * @param weights the children weights  
	 * @return a new <code>ElementInfo</code> object
	 */
	public final ElementInfo create(String name, int type,
			ElementInfo[] children, double[] weights) {
		ElementInfo elInfo = 
			paloConnection.addElement(hierarchy, name,type,children,weights);
		loaded(elInfo);
		return elInfo;
	}

	public final ElementInfo[] createBulk(String[] names, int type,
			ElementInfo[][] children, double[][] weights) {
		List<ElementInfo> newElements = new ArrayList<ElementInfo>(names.length);
		if (paloConnection.addElements(hierarchy.getDimension(), names, type,
				children, weights)) {
			ElementInfo[] allElements = 
				paloConnection.getElements(hierarchy.getDimension());
			Set<String> reqNames = new HashSet<String>(Arrays.asList(names));
			for(ElementInfo el : allElements) {
				if(reqNames.contains(el.getName())) {
					newElements.add(el);
					loaded(el);
				}
			}
		}
		return newElements.toArray(new ElementInfo[0]);
	}

	public final ElementInfo[] createBulk(String[] names, int [] types,
			ElementInfo[][] children, double[][] weights) {
		List<ElementInfo> newElements = new ArrayList<ElementInfo>(names.length);
		if (paloConnection.addElements(hierarchy.getDimension(), names, types,
				children, weights)) {
			ElementInfo[] allElements = 
				paloConnection.getElements(hierarchy.getDimension());
			Set<String> reqNames = new HashSet<String>(Arrays.asList(names));
			for(ElementInfo el : allElements) {
				if(reqNames.contains(el.getName())) {
					newElements.add(el);
					loaded(el);
				}
			}
		}
		return newElements.toArray(new ElementInfo[0]);
	}

	public final ElementInfo [] replaceBulk(ElementInfo [] elements, int type,
			ElementInfo [][] children, Double [][] weights) {
		if (paloConnection.replaceBulk(hierarchy.getDimension(), elements, type, children, weights)) {
			return paloConnection.getElements(hierarchy.getDimension());
		}
		return new ElementInfo[0];
	}
	
	/**
	 * Deletes the given <code>ElementInfo</code> instance from the palo 
	 * server as well as from the internal used cache.
	 * @param elInfo the <code>ElementInfo</code> instance to delete
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public final boolean delete(ElementInfo elInfo) {
		if(paloConnection.delete(elInfo)) {
			removed(elInfo);
			return true;
		} 
		return false;
	}

	public final boolean delete(ElementInfo[] elInfos) {
		if(paloConnection.delete(elInfos)) {
			for(ElementInfo elInfo : elInfos)
				removed(elInfo);
			return true;
		}
		return false;
	}
	
	/**
	 * Loads the <code>ElementInfo</code> object which corresponds to the given
	 * id
	 * @param id the identifier of the <code>ElementInfo</code> object to load
	 * @return the loaded <code>ElementInfo</code> object
	 */	
	public final ElementInfo load(String id) {
		PaloInfo el = loadedInfo.get(id);
		if (el == null) {
			el = paloConnection.getElement(hierarchy, id);
			loaded(el);
		}
		return (ElementInfo)el;
	}
		
}
