/*
*
* @file Axis.java
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
* @version $Id: Axis.java,v 1.18 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api;

import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;

/**
 * An <code>Axis</code> holds several cube {@link Dimension}s and manages their 
 * current state. A dimension state is described by its active {@link Subset}
 * and its currently expanded {@link Element}s. Furthermore you can set a
 * selected element for a fix dimension, i.e. this dimension defines a fix part
 * of a cube cell coordinate.
 *  
 * @author ArndHouben
 * @version $Id: Axis.java,v 1.18 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public interface Axis {
	/**
	 * Returns the unique id of the axis
	 * @return the axis identifier
	 */
	String getId();
	
	/**
	 * Sets an optional name for the axis
	 * @param name the axis name
	 */
	void setName(String name);

	/**
	 * Returns the axis name or <code>null</code> if none has been set
	 * @return the axis name or <code>null</code>
	 */
	String getName();
	
	/**
	 * Returns the hierarchy that has been set for the specified dimension.
	 * 
	 * @param dimension the dimension whose hierarchy is requested.
	 * @return the hierarchy that has been set for the specified dimension or
	 * <code>null</code> if the dimension is not present on this axis.
	 */
	Hierarchy getHierarchy(Dimension dimension);
	
	/**
	 * Adds the given dimension to the axis and selects its default hierarchy
	 * @param dimension the dimension to add
	 * @deprecated use {@link add(Hierarchy)} instead.
	 */
	void add(Dimension dimension);
	
	/**
	 * Adds the given hierarchy to the axis.
	 * @param hierarchy the hierarchy to add.
	 */
	void add(Hierarchy hierarchy);
	
	/**
	 * Removes the given dimension from the axis
	 * @param dimension the dimension to remove
	 * @deprecated use {@link remove(Hierarchy)} instead.
	 */
	void remove(Dimension dimension);
	
	/**
	 * Removes the given hierarchy from the axis
	 * @param hierarchy the hierarchy to remove
	 */
	void remove(Hierarchy hierarchy);
	
	/**
	 * Returns all added dimensions
	 * @return all registered <code>Dimension</code>s
	 * @deprecated use {@link getHierarchies()} instead.
	 */
	Dimension[] getDimensions();
	
	/**
	 * Returns all added hierarchies
	 * @return all registered <code>Hierarchy</code> objects
	 */
	Hierarchy[] getHierarchies();
	
	/**
	 * Returns the active subset for the given dimension or <code>null</code> if
	 * the dimension has no active subset, i.e. no subset is used 
	 * @param dimension
	 * @return the active <code>Subset</code> to use for the given dimension or null
	 */
	Subset getActiveSubset(Dimension dimension);
	
	/**
	 * Sets the active subset for the given dimension.
	 * <p><b>Note:</b> <code>null</code> is allowed and will deactivate the 
	 * current active subset.</p>
	 * @param dimension
	 * @param subset
	 */
	void setActiveSubset(Dimension dimension,Subset subset); //subset==null => deactivate
	
	/**
	 * Sets the active modern subset2 for the given dimension.
	 * <p><b>Note:</b> <code>null</code> is allowed and will deactivate the 
	 * current active subset2.</p>
	 * @param dimension
	 * @param subset
	 */
	void setActiveSubset2(Dimension dimension, Subset2 subset);
	
	/**
	 * Returns the active subset2 for the given dimension or <code>null</code> if
	 * the dimension has no active subset2, i.e. no subset is used 
	 * @param dimension
	 * @return the active <code>Subset</code> to use for the given dimension or null
	 */	
	Subset2 getActiveSubset2(Dimension dimension);

	/**
	 * Returns the currently selected element of the given dimension or 
	 * <code>null</code> if none has been set
	 * @param dimension the dimension for which the selected element is to be
	 * returned.
	 * @return the current selected <code>Element</code> for the given dimension
	 * or <code>null</code>.
 	 * @deprecated use {@link getSelectedElement(Hierarchy)} instead.
	 */
	Element getSelectedElement(Dimension dimension);
	
	/**
	 * Returns the currently selected element of the given hierarchy or 
	 * <code>null</code> if none has been set
	 * @param hierarchy the hierarchy for which the selected element is to be
	 * returned.
	 * @return the current selected <code>Element</code> for the given hierarchy
	 * or <code>null</code>.
	 */
	Element getSelectedElement(Hierarchy hierarchy);
	
	/**
	 * Sets the currently selected element of the given dimension.
	 * <p><b>Note:</b> providing <code>null</code> as element is allowed and 
	 * will deselect currently selected element 
	 * @param dimension the dimension for which the element is to be set.
	 * @param element the selected element for this dimension or null.
	 * @deprecated use {@link setSelectedElement(Hierarchy, Element)} instead.
	 */
	void setSelectedElement(Dimension dimension,Element element); //element==null => deactivate
	
	/**
	 * Sets the currently selected element of the given hierarchy.
	 * <p><b>Note:</b> providing <code>null</code> as element is allowed and 
	 * will deselect currently selected element 
	 * @param hierarchy the hierarchy for which the element is to be set.
	 * @param element the selected element for this hierarchy or null.
	 */	
	void setSelectedElement(Hierarchy hierarchy,Element element);
	
	/**
	 * Adds the given path to the expand list of the provided dimension. An 
	 * element path describes the path to the expanded element inside its 
	 * dimension. Since a path can occur several times within a multi dimension
	 * axis, the repetition parameter determines which repetition of the path
	 * is actually expanded
	 * @param dimension the dimension for which the expand path is to be set
	 * @param path the path of the expanded elements
	 * @param repetition determines which path repetition is actually expanded
	 * @deprecated please use {@link #addExpanded(ElementPath)} instead 
	 */
	void addExpanded(Dimension dimension,Element[] path, int repetition);
	
	/**
	 * Adds the given path to the expand list of the provided hierarchy. An 
	 * element path describes the path to the expanded element inside its 
	 * hierarchy. Since a path can occur several times within a multi hierarchy
	 * axis, the repetition parameter determines which repetition of the path
	 * is actually expanded
	 * @param hierarchy the hierarchy for which the expand path is to be set
	 * @param path the path of the expanded elements
	 * @param repetition determines which path repetition is actually expanded
	 * @deprecated please use {@link #addExpanded(ElementPath)} instead 
	 */
	void addExpanded(Hierarchy hierarchy,Element[] path, int repetition);
	
	/**
	 * Removes the given path from the expanded path list of the provided 
	 * dimension. 
	 * @param dimension
	 * @param path
	 * @param repetition the path repetition which should be removed
	 * @deprecated please use {@link #removeExpanded(ElementPath)} instead
	 */	
	void removeExpanded(Dimension dimension, Element[] path, int repetition);

	/**
	 * Removes the given path from the expanded path list of the provided 
	 * hierarchy. 
	 * @param hierarchy
	 * @param path
	 * @param repetition the path repetition which should be removed
	 * @deprecated please use {@link #removeExpanded(ElementPath)} instead
	 */	
	void removeExpanded(Hierarchy hierarchy, Element[] path, int repetition);
	
	/**
	 * Returns all expanded paths for the given dimension
	 * @param dimension
	 * @return all expanded paths 
	 * @deprecated please use {@link #getExpandedPaths()} instead
	 */
	Element[][] getExpanded(Dimension dimension);

	/**
	 * Returns all expanded paths for the given hierarchy
	 * @param hierarchy
	 * @return all expanded paths 
	 * @deprecated please use {@link #getExpandedPaths()} instead
	 */
	Element[][] getExpanded(Hierarchy hierarchy);
	
	/**
	 * Returns all expanded <code>{@link ElementPath}</code>s which are 
	 * registered with this axis.
	 * @return all expanded paths
	 */
	ElementPath[] getExpandedPaths();
	
	/**
	 * Adds the given <code>{@link ElementPath}</code> to the expand list of 
	 * this axis. 
	 * 
	 * @param path to the expanded element
	 */
	void addExpanded(ElementPath path);
	
	/**
	 * Convenience method to add several <code>{@link ElementPath}</code>s
	 * in one go
	 * @param paths to the expanded elements
	 */	
	void addExpanded(ElementPath[] paths);
	
	/**
	 * Removes the given <code>{@link ElementPath}</code> from the expand list
	 * of this axis
	 * @param path
	 */
	void removeExpanded(ElementPath path);

	/**
	 * Returns all repetition positions for the specified expanded path within
	 * the given dimension 
	 * @param dimension
	 * @param path
	 * @return all repetitions for the specified path within given dimension
	 * @deprecated please use {@link #getExpandedPaths()} instead
	 */
	int[] getRepetitionsForExpanded(Dimension dimension, Element[] path);

	/**
	 * Returns all repetition positions for the specified expanded path within
	 * the given hierarchy 
	 * @param hierarchy
	 * @param path
	 * @return all repetitions for the specified path within given hierarchy
	 * @deprecated please use {@link #getExpandedPaths()} instead
	 */
	int[] getRepetitionsForExpanded(Hierarchy hierarchy, Element[] path);
	
	/**
	 * Adds the given path to the hidden path list of the provided dimension. 
	 * The element path describes the path to the hidden element inside its 
	 * dimension. 
	 * @param dimension
	 * @param path
	 * @deprecated please use {@link #addVisible(ElementPath)} 
	 */
	void addHidden(Dimension dimension, Element[] path);

	/**
	 * Adds the given path to the hidden path list of the provided hierarchy. 
	 * The element path describes the path to the hidden element inside its 
	 * dimension. 
	 * @param hierarchy
	 * @param path
	 * @deprecated please use {@link #addVisible(ElementPath)} 
	 */
	void addHidden(Hierarchy hierarchy, Element[] path);

	/**
	 * Removes the given path from the hidden path list of the provided 
	 * dimension
	 * @param dimension
	 * @param path
	 * @deprecated please use {@link #removeVisible(ElementPath)}
	 */
	void removeHidden(Dimension dimension, Element[] path);

	/**
	 * Removes the given path from the hidden path list of the provided 
	 * hierarchy
	 * @param hierarchy
	 * @param path
	 * @deprecated please use {@link #removeVisible(ElementPath)}
	 */	
	void removeHidden(Hierarchy hierarchy, Element[] path);
	
	/**
	 * Returns all hidden paths for the given dimension
	 * @param dimension
	 * @return all hidden paths
	 * @deprecated please use {@link #getVisiblePaths(Dimension)} 
	 */
	Element[][] getHidden(Dimension dimension);

	/**
	 * Returns all hidden paths for the given hierarchy
	 * @param hierarchy
	 * @return all hidden paths
	 * @deprecated please use {@link #getVisiblePaths(Hierarchy)} 
	 */	
	Element[][] getHidden(Hierarchy hierarchy);

	/**
	 * Adds the given path to the visible path list of this axis. 
	 * The element path describes a path to the visible element inside its 
	 * dimension. 
	 * @param path
	 */
	void addVisible(ElementPath path);
	
	/**
	 * Removes the given path from the visible path list of this axis.
	 * @param path
	 */
	void removeVisible(ElementPath path);
	
	/**
	 * Returns all visible paths for this axis.
	 * @param dimension the dimension for which the visible element paths are
	 * to be returned.
	 * @return all visible paths for the specified dimension.
	 * @deprecated please use {@link #getVisiblePaths(Hierarchy)} instead.
	 */
	ElementPath[] getVisiblePaths(Dimension dimension);

	/**
	 * Returns all visible paths for this axis.
	 * @param hierarchy the hierarchy for which the visible element paths are
	 * to be returned.
	 * @return all visible paths for the specified hierarchy.
	 */	
	ElementPath[] getVisiblePaths(Hierarchy hierarchy);
		
	/**
	 * Checks if the element specified by the given path is visible or not
	 * @param path path to the element
	 * @return <code>true</code> if specified element is visible, otherwise 
	 * <code>false</code>
	 */
	boolean isVisible(ElementPath path);
		
//	//for general usage:
	/**
	 * Adds a property with the given id and given value to this axis
	 * <b>NOTE: RIGHT NOW FOR INTERNAL USAGE ONLY </b>
	 * @param id a unique identifier
	 * @param value the property value
	 * @deprecated please do not use. this method is subject for modifications
	 */
	void addProperty(String id, String value);
	
	/**
	 * Removes the property specified by the given id from this axis
	 * <b>NOTE: RIGHT NOW FOR INTERNAL USAGE ONLY </b>
	 * @param id the property identifier
	 * @deprecated please do not use. this method is subject for modifications
	 */
	void removeProperty(String id);
	
	/**
	 * Returns all property ids
	 * <b>NOTE: RIGHT NOW FOR INTERNAL USAGE ONLY </b>
	 * @return all property identifiers
	 * @deprecated please do not use. this method is subject for modifications
	 */	
	String[] getProperties();
	
	/**
	 * Returns the value of the property specified by the given id
	 * <b>NOTE: RIGHT NOW FOR INTERNAL USAGE ONLY </b>  
	 * @param id the property identifier
	 * @return the property value
	 * @deprecated please do not use. this method is subject for modifications
	 */
	String getPropertyValue(String id);
	
	/**
	 * <b>NOTE: RIGHT NOW FOR INTERNAL USAGE ONLY </b>  
	 * @deprecated please do not use. this method is subject for modifications
	 */
	String getData(String id);
}