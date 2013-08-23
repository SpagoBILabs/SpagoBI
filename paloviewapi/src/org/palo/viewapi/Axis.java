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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: Axis.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.viewapi;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;


/**
 * An <code>Axis</code> holds several cube {@link Dimension}s and manages their 
 * current state. A dimension state is described by its active {@link Subset2}
 * and its currently expanded {@link Element}s. Furthermore you can set a
 * selected element for a fix dimension, i.e. this dimension defines a fix part
 * of a cube cell coordinate.
 *
 * @version $Id: Axis.java,v 1.12 2009/12/17 16:14:08 PhilippBouillon Exp $  
 */
public interface Axis extends AxisProperties {
	
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
	
	/** convenience */
	
	/**
	 * Returns all {@link Hierarchy}s which were added to this axis.
	 * @return all added hierarchies
	 */
	Hierarchy[] getHierarchies();
	/**
	 * Adds the given {@link Hierarchy} to this axis and returns the 
	 * corresponding {@link AxisHierarchy} object. The returned 
	 * <code>AxisHierarchy</code> can be used to tweak some per hierarchy
	 * settings for this axis.
	 * @param hierarchy the hierarchy to add
	 * @return the corresponding <code>AxisHierarchy</code> object
	 */
	AxisHierarchy add(Hierarchy hierarchy);
	/**
	 * Adds the given {@link AxisHierarchy} and therewith its corresponding
	 * {@link Hierarchy}
	 * @param hierarchy the <code>AxisHierarchy</code> to add
	 */
	void add(AxisHierarchy hierarchy);
	/**
	 * Removes the given {@link Hierarchy} from this axis and returns the 
	 * corresponding {@link AxisHierarchy} object. If the given 
	 * <code>Hierarchy</code> was removed already <code>null</code> is returned.
	 * @param hierarchy the hierarchy to remove
	 * @return the corresponding <code>AxisHierarchy</code> object
	 */
	AxisHierarchy remove(Hierarchy hierarchy);
	
	/**
	 * Removes the given {@link AxisHierarchy} and therewith its corresponding
	 * {@link Hierarchy}
	 * @param hierarchy the <code>AxisHierarchy</code> to remove
	 */
	void remove(AxisHierarchy axisHierarchy);
	
	/**
	 * Removes all added hierarchies. This will remove all visible and expanded
	 * paths too.
	 */
	void removeAll();
	
	/**
	 * Returns all added {@link Hierarchy}s represented by their corresponding
	 * {@link AxisHierarchy} objects.
	 * @return all added <code>AxisHierarchy</code>
	 */
	AxisHierarchy[] getAxisHierarchies();
	/**
	 * Returns the {@link AxisHierarchy} which corresponds to the given 
	 * {@link Hierarchy} or <code>null</code> if the given 
	 * <code>Hierarchy</code> was not added before.
	 * @param hierarchy the hierarchy to look for
	 * @return the corresponding <code>AxisHierarchy</code> object or 
	 * <code>null</code>
	 */
	AxisHierarchy getAxisHierarchy(Hierarchy hierarchy);
	/**
	 * Returns the {@link AxisHierarchy} which corresponds to the given 
	 * <code>id</code> or <code>null</code> if no hierarchy with the given 
	 * <code>id</code> was added before.
	 * @param <code>id</code> the id of the hierarchy to look for
	 * @return the corresponding <code>AxisHierarchy</code> object or 
	 * <code>null</code>
	 */
	AxisHierarchy getAxisHierarchy(String id);

	
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

	void removeAllExpandedPaths();	
	
	/**
	 * Returns a deep copy of this axis
	 * @return a copy of this axis
	 */
	Axis copy();
		
	//for general usage:
	
	/**
	 * Returns all properties specified for this AxisHierarchy.
	 * @return all properties specified for this AxisHierarchy.
	 */	
	public Property<?>[] getProperties();

	/**
	 * Returns the property for the specified <code>id</code> or 
	 * <code>null</code> if the hierarchy has no property with this id. 
	 * @param id id of the property to look for 
	 * @return the matching property or <code>null</code>
	 */
	public Property<?> getProperty(String id);
	
	/**
	 * Adds a general purpose property to this AxisHierarchy.
	 * @param property the property to add to this AxisHierarchy.
	 */	
	public void addProperty(Property<?> property);	

	/**
	 * Removes a general purpose property from this AxisHierarchy.
	 * @param property the property to be removed from this AxisHierarchy.
	 */
	public void removeProperty(Property<?> property);
	
	public void removeAllProperties();
	
	View getView();
}
