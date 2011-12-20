/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.geo.map.renderer;

// TODO: Auto-generated Javadoc
/**
 * The Class Layer.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class Layer {
	
	/** The name. */
	String name;
	
	/** The description. */
	String description;
	
	/** The selected. */
	boolean selected;
	
	/** The default fill color. */
	String defaultFillColor;
	
	/**
	 * Gets the default fill color.
	 * 
	 * @return the default fill color
	 */
	public String getDefaultFillColor() {
		return defaultFillColor;
	}
	
	/**
	 * Sets the default fill color.
	 * 
	 * @param defaultFillColor the new default fill color
	 */
	public void setDefaultFillColor(String defaultFillColor) {
		this.defaultFillColor = defaultFillColor;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Sets the selected.
	 * 
	 * @param selected the new selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
