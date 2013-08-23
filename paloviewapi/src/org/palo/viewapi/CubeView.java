/*
*
* @file CubeView.java
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
* @version $Id: CubeView.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.viewapi;

import org.palo.api.Cube;
import org.palo.api.PaloAPIException;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.uimodels.formats.Format;



/**
 * A <code>CubeView</code> reflects a certain state of its corresponding palo 
 * cube. Therefore each cube view consists of one or more so called {@link Axis} 
 * which allows the sorting of the cube dimensions. E.g. the palo client defines 
 * axes for a cube table, like column and row, and a third axis for remaining 
 * dimensions.
 *
 * @version $Id: CubeView.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $ 
 */
public interface CubeView extends DomainObject, CubeViewProperties, 
								  ParameterReceiver {

	//some predefined axis ids which can be used...
	public static final String SELECTION_AXIS = "selected";
	public static final String ROW_AXIS = "rows";
	public static final String COLUMN_AXIS = "cols";
	
	
	/**
	 * Element parameter can be used to identify the selected element in a
	 * variable hierarchy.
	 */
	public final static String PARAMETER_ELEMENT = "Element";
	
	
//	/**
//	 * Returns the unique id of this cube view
//	 * @return unique id
//	 */
//	public String getId();
	
	/**
	 * Returns the corresponding source {@link Cube} of this view
	 * @return the source <code>Cube</code> of this view
	 */
	public Cube getCube();
	
	//optional
	/**
	 * Returns the name of this cube view or <code>null</code> if none was set.
	 * @return the name of this cube view or <code>null</code>
	 */
	public String getName();
	
	/**
	 * Sets the name of this cube view. Please note that the new name gets only
	 * stored with the next save of this view!
	 * @param name (new) name of the cube view
	 */
	public void setName(String name);
	
	/**
	 * Returns an optional description or <code>null</code> if none has been set
	 * @return a description of this view
	 */
	public String getDescription();
	/**
	 * Sets an optional description 
	 * @param description
	 */
	public void setDescription(String description);

//	/**
//	 * Returns the internal used raw definition of this cube view.
//	 * @return internal representation of this view 
//	 */
//	String getRawDefinition();
	
	/**
	 * Adds a new {@link Axis} to this cube view.
	 * @param id the unique id of the new axis
	 * @param name the name of the new axis
	 * @return the newly created axis
	 * @throws PaloAPIException if an axis with the given id exists already for this view
	 * @throws NoPermissionException if user has not enough rights to change this view
	 */
	Axis addAxis(String id,String name);	
	/**
	 * Removes the given axis from this cube view
	 * @param axis the axis to remove
	 */
	void removeAxis(Axis axis);
	/**
	 * Returns all axes registered to this cube view
	 * @return all registered axes
	 */
	Axis[] getAxes();
	/**
	 * Returns the axis which is registered to this cube view under the given
	 * axis id or <code>null</code> if no such axis exists.
	 * @param id 
	 * @return the registered axis or <code>null</code>
	 */
	Axis getAxis(String id);
	
	/**
	 * Creates a deep copy of this cube view.
	 * @return a copy of this cube view.
	 */
	CubeView copy();
	
	
	//for general usage:
	/**
	 * Adds a general purpose property to this CubeView with the given id and
	 * given value.
	 * 
	 * @param id the id for the new property. If a property with this id already
	 * exists for this CubeView, the old property will be overwritten.
	 * @param value the value for the new property.
	 * @return a new Property object with the given id and value.
	 */	
	Property <Object> addProperty(String id, Object value);
	
	/**
	 * Returns the property with the given id or null if no such property
	 * exists.
	 * @param id the id of the property.
	 * @return the property with the given id or null.
	 */
	Property <Object> getProperty(String id);
	
	/**
	 * Removes the property with the given id.
	 * @param id the id of the property that is to be removed.
	 */
	void removeProperty(String id);
	
	/**
	 * Returns all properties defined in this CubeView.
	 * @return all properties defined in this CubeView.
	 */
	Property <Object> [] getProperties();
	
	/**
	 * Returns the value of the property with the specified id or null if no
	 * such property exists.
	 * @param id the id of the specified property.
	 * @return the value of the property with the specified id or null.
	 */
	Object getPropertyValue(String id);
	
	// Format handling:
	/**
	 * Returns all formats defined in this view.
	 * @return  all formats defined in this view.
	 */
	Format[] getFormats();
	
	/**
	 * Creates a new format with the specified id and returns it. If a
	 * format with that id already exists in this view, it will be overwritten.
	 * 
	 * @param id the id for the new format.
	 * @return a new format.
	 */
	Format addFormat(String id);
	
	/**
	 * Adds the given format to this CubeView.
	 * @param format the format to add to this CubeView. If this CubeView
	 * already exists, nothing happens.
	 */
	void addFormat(Format format);
	
	/**
	 * Removed the format with the specified id from this CubeView.
	 * @param formatId the id of the format that is to be removed.
	 */
	void removeFormat(String formatId);
	
	/**
	 * Removes all formats from this view.
	 */
	void removeAllFormats();
	
	/**
	 * Returns true if any format is defined for this view, false otherwise.
	 * @return true if any format is defined for this view, false otherwise.
	 */
	boolean hasFormats();
	
	/**
	 * Returns the format with the specified id or null if no such format is
	 * defined in this view.
	 * @param formatId the id of the format.
	 * @return the format with the specified id or null.
	 */
	Format getFormat(String formatId);	
}
