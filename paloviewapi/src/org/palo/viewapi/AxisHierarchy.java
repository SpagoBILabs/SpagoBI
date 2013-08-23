/*
*
* @file AxisHierarchy.java
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
* @version $Id: AxisHierarchy.java,v 1.11 2010/01/13 08:02:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.subsets.Subset2;

/**
 * <code>AxisHierarchy</code>
 * <p>
 * An <code>AxisHierarchy</code> is used to make per {@link Hierarchy} settings
 * for an {@link Axis}. Currently {@link Subset2}s, {@link LocalFilter} and 
 * selected {@link Element}s are defined per <code>Hierarchy</code> and not 
 * axis wide.
 * </p>
 *
 * @version $Id: AxisHierarchy.java,v 1.11 2010/01/13 08:02:42 PhilippBouillon Exp $
 **/
public interface AxisHierarchy {

	//ids of some predefined properties:
	public static final String USE_ALIAS = "com.tensegrity.palo.axis.use_alias";

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
	
	/**
	 * Adds the given {@link Element} to the list of all selected elements.
	 * @param element the <code>Element</code> to add
	 */
	public void addSelectedElement(Element element);
	/**
	 * Returns the corresponding {@link Hierarchy} instance.
	 * @return the corresponding {@link Hierarchy} instance.
	 */
	public Hierarchy getHierarchy();
	
	/**
	 * Returns all {@link Element}s which should be used as selected elements.
	 * @return all currently selected <code>Elements</code> 
	 */
	public Element[] getSelectedElements();
	/**
	 * Returns the {@link Subset2} to use for the corresponding 
	 * {@link Hierarchy} or <code>null</code> if no <code>Subset</code> was set.
	 * @return the <code>Subset</code> instance or <code>null</code> if none 
	 * should be used
	 */
	public Subset2 getSubset();
	/**
	 * Checks if this <code>AxisHierarchy</code> has any selected 
	 * {@link Element}s defined.
	 * @return <code>true</code> if selected <code>Element</code>s should be
	 * used, <code>false</code> otherwise
	 */
	public boolean hasSelectedElements();
	/**
	 * Removes the given {@link Element} from the list of all selected elements
	 * @param element the <code>Element</code> to remove.
	 */
	public void removeSelectedElement(Element element);
	/**
	 * Removes all currently selected {@link Element}s. 
	 */
	public void clearSelectedElements();
	/**
	 * Sets the {@link Subset2} to use for the corresponding {@link Hierarchy}.
	 * Specifying <code>null</code> will clear the subset setting.
	 * @param subset the <code>Subset2</code> to use. Specify <code>null</code>
	 * to clear any previously set subset.
	 */
	public void setSubset(Subset2 subset);
	public void setSubsetMissing(String id);
	public void setAliasMissing(String id);
	
	public String getSubsetMissing();
	public String getAliasMissing();
	
//	public Element[] getRootElements();
	
	public ElementNode[] getRootNodes();
	public String getElementNameFor(String elementID);
	
	public void setLocalFilter(LocalFilter filter);
	public LocalFilter getLocalFilter();
	
	public boolean contains(Element element);
	
	public Axis getAxis();
	public void setAxis(Axis axis);

}
