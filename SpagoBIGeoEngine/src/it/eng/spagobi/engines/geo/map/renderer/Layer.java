/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
