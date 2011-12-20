/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/

package it.eng.spagobi.engines.kpi.utils;

import java.awt.Color;

/** 
 *  * @author Giulio Gavardi
 *     giulio.gavardi@eng.it
 */

public class KpiInterval {

	private String label;
	private Double min;
	private Double max;
	private Color color;
	
	
	/**
	 * Instantiates a new kpi interval.
	 */
	public KpiInterval() {
		super();
	}

	/**
	 * Instantiates a new kpi interval.
	 * 
	 * @param min the min
	 * @param max the max
	 * @param color the color
	 */
	public KpiInterval(Double min, Double max, Color color) {
		super();
		this.min = min;
		this.max = max;
		this.color = color;
	}
	
	/**
	 * Gets the min.
	 * 
	 * @return the min
	 */
	public Double getMin() {
		return min;
	}
	
	/**
	 * Sets the min.
	 * 
	 * @param min the new min
	 */
	public void setMin(Double min) {
		this.min = min;
	}
	
	/**
	 * Gets the max.
	 * 
	 * @return the max
	 */
	public Double getMax() {
		return max;
	}
	
	/**
	 * Sets the max.
	 * 
	 * @param max the new max
	 */
	public void setMax(Double max) {
		this.max = max;
	}
	
	/**
	 * Gets the color.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the color.
	 * 
	 * @param color the new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
