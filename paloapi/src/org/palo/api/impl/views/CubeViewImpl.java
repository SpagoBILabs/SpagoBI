/*
*
* @file CubeViewImpl.java
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
* @version $Id: CubeViewImpl.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.palo.api.Axis;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.PaloAPIException;
import org.palo.api.Property;

/**
 * Default implementation of the {@link CubeView} interface
 */
class CubeViewImpl implements CubeView {
	/**
	 * The id of this <code>CubeView</code>.
	 */
	private final String id;
	
	/**
	 * The source cube for which this view is defined.
	 */
	private final Cube srcCube;
	
	/**
	 * The axes for this <code>CubeView</code>. Usually "rows", "cols" and
	 * "selected".
	 */
	private final Set axes = new HashSet();
	
	/**
	 * The name of this <code>CubeView</code>.
	 */
	private String name;
	
	/**
	 * The description of this <code>CubeView</code>.
	 */
	private String description;
	
	/**
	 * The <code>properties</code> hashmap stores all properties for this
	 * <code>CubeView</code>. Its keys are Strings representing the id of the
	 * specified property, its values are Strings denoting the value of the
	 * respective property.
	 */
	private HashMap properties;
	
	/**
	 * Creates a new empty <code>CubeView</code>.
	 * 
	 * @param id the id of the new CubeView.
	 * @param name the name of the new CubeView.
	 * @param srcCube the source cube for this CubeView.
	 */
	CubeViewImpl(String id, String name, Cube srcCube) {
		this.id = id;
		this.name = name;
		this.srcCube = srcCube;
		
		this.properties = new HashMap();
		description = "";
	}
	
	/**
	 * Clears all defines axes of this CubeView and restores it thus to an
	 * empty state.
	 */
	final void reset() {
		axes.clear();
	}
	
	/**
	 * Returns the source Cube of this view.
	 */
	public final Cube getCube() {
		return srcCube;
	}

	/**
	 * Returns the description of this view.
	 */
	public final synchronized String getDescription() {
		return description;
	}

	/**
	 * Sets a new description for this view.
	 */
	public final synchronized void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the id of this view.
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Returns the name of this view.
	 */
	public final synchronized String getName() {
		return name;
	}

	/**
	 * Sets a new name for this view.
	 */
	public final synchronized void setName(String name) {
		this.name = name != null ? name : "";
	}
	
	/**
	 * Adds an axis to this view if it doesn't exist, yet. If the axis already
	 * exists in the view, a PaloAPIExceptin is thrown.
	 */
	public final Axis addAxis(String id, String name) {
		AxisImpl axis = new AxisImpl(id,name,this);
		if(axes.contains(axis))
			throw new PaloAPIException("Axis already exist!");
		axes.add(axis);
		return axis;
	}

	/**
	 * Returns all defined axes for this view.
	 */
	public final Axis[] getAxes() {
		return (Axis[])axes.toArray(new Axis[axes.size()]);
	}

	/**
	 * Returns the axis with the specified id or null, if no such axis exists
	 * in the view.
	 */
	public final Axis getAxis(String id) {
		for(Iterator it = axes.iterator();it.hasNext(); ) {
			Axis axis = (Axis)it.next();
			if(axis.getId().equals(id))
				return axis;
		}
		return null;
	}
	
	/**
	 * Deletes the specified axis from the view.
	 */
	public final void removeAxis(Axis axis) {
		axes.remove(axis);
	}
		
	/**
	 * Saves this view.
	 */
	public final void save() {
		CubeViewManager.getInstance().save(this);
	}
	
    /**
     * Returns the internal used representation for the given cube view.
     * Currently the internal format is xml.
     */
	public final String getRawDefinition() {
		return CubeViewManager.getInstance().getRawDefinition(this);
	}

	/**
	 * Adds a property to this CubeView; if the specified id already exists,
	 * the old value is overwritten with the new value.
	 */
	public void addProperty(String id, String value) {
		properties.put(id, value);
	}
	
	/**
	 * Adds a property to this CubeView.
	 */
	public void addProperty(Property property) {
		if (property == null) {
			return;
		}
		properties.put(property.getId(), property.getValue());
	}

	/**
	 * Returns all defined property ids for this CubeView.
	 */
	public String [] getProperties() {
		return (String []) properties.keySet().toArray(
				new String[properties.size()]);
	}

	/**
	 * Returns the value for the specified property id or null if no such
	 * property exists.
	 */
	public String getPropertyValue(String id) {
		return (String) properties.get(id);
	}

	/**
	 * Removes the specified property from this view. 
	 */
	public void removeProperty(String id) {
		properties.remove(id);
	}
	
	/**
	 * Removes the specified property from this view.
	 */
	public void removeProperty(Property property) {
		if (property == null) {
			return;
		}
		removeProperty(property.getId());
	}

	/**
	 * @deprecated Do not use anymore. See {@link CubeView#isHideEmpty()}
	 */
	public boolean isHideEmpty() {
		return "true".equals(getPropertyValue(PROPERTY_ID_HIDE_EMPTY));
	}

	/**
	 * @deprecated Do not use anymore. See {@link CubeView#setHideEmpty()}
	 */
	public void setHideEmpty(boolean hideEmpty) {
		addProperty(PROPERTY_ID_HIDE_EMPTY, Boolean.toString(hideEmpty));
	}	
}
