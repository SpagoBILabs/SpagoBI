/*
*
* @file TrafficLightData.java
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
* @author PhilippBouillon
*
* @version $Id: TrafficLightData.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.formats;

import java.util.ArrayList;
import java.util.Arrays;

import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;

/**
 * The <code>TrafficLightData</code> describes a traffic light information for
 * a cell (or a range of cells).
 * A "traffic light" can be understood as follows:
 * The data of the cell on which a traffic light is active, is inspected. If
 * the value lies inside certain parameters, a special format is assigned to
 * that cell. Thus, you are able to say something like: "If the value inside
 * this cell is below 0 make its background red.", or "If the value inside this
 * cell is above 1000, make its background green.".
 * 
 * @author PhilippBouillon
 * @version $Id: TrafficLightData.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public class TrafficLightData {
	/**
	 * List of all minimal values. If a cell value is between minValue[n] and
	 * maxValue[n], the specified format is applied.
	 */
	private final ArrayList <Double> minValues;

	/**
	 * List of all maximal values. If a cell value is between minValue[n] and
	 * maxValue[n], the specified format is applied.
	 */	
	private final ArrayList <Double> maxValues;
	
	/**
	 * List of all background colors. bacgroundColors[n] is applied, if the
	 * value of the cell is between minValue[n] and maxValue[n].
	 */
	private final ArrayList <ColorDescriptor> backgroundColors;

	/**
	 * List of all foreground colors. foregroundColors[n] is applied, if the
	 * value of the cell is between minValue[n] and maxValue[n].
	 */	
	private final ArrayList <ColorDescriptor> foregroundColors;

	/**
	 * List of all fonts. fontDescriptor[n] is applied, if the
	 * value of the cell is between minValue[n] and maxValue[n].
	 */
	private final ArrayList <FontDescriptor> fonts;
	
	/**
	 * Creates a new TrafficLightData object. Note that all passed arrays must
	 * be of the same length.
	 * 
	 * @param minVals minimum values for all intervals.
	 * @param maxVals maximum values for all intervals.
	 * @param backgroundColors background colors for all intervals.
	 * @param foregroundColors foreground colors for all intervals.
	 * @param fontDatas font data for all intervals.
	 */
	public TrafficLightData(double [] minVals, double [] maxVals,
			                      ColorDescriptor [] backgroundColors,
			                      ColorDescriptor [] foregroundColors,
			                      FontDescriptor [] fontDatas) {
		this.minValues = new ArrayList <Double> ();
		this.maxValues = new ArrayList <Double> ();
		this.backgroundColors = new ArrayList <ColorDescriptor> ();
		this.foregroundColors = new ArrayList <ColorDescriptor> ();
		this.fonts = new ArrayList <FontDescriptor> ();
			
		for (double d: minVals) { minValues.add(d); }
		for (double d: maxVals) { maxValues.add(d); }
		this.backgroundColors.addAll(Arrays.asList(backgroundColors));
		this.foregroundColors.addAll(Arrays.asList(foregroundColors));
		this.fonts.addAll(Arrays.asList(fontDatas));
	}
	
	/**
	 * Returns the number of intervals in this TrafficLightData.
	 * @return the number of intervals in this TrafficLightData.
	 */
	public int getSize() {
		return minValues.size();
	}
	
	/**
	 * Returns all minimum values of intervals.
	 * @return all minimum values of intervals.
	 */
	public double [] getMinValues() {
		double [] result = new double[minValues.size()];
		int counter = 0;
		for (Double d: minValues) {result[counter++] = d;}
		return result;
	}
	
	/**
	 * Returns all maximum values of intervals.
	 * @return all maximum values of intervals.
	 */
	public double [] getMaxValues() {
		double [] result = new double[maxValues.size()];
		int counter = 0;
		for (Double d: maxValues) {result[counter++] = d;}
		return result;
	}
	
	/**
	 * Returns text colors of all intervals.
	 * @return text colors of all intervals.
	 */
	public ColorDescriptor [] getForegroundColors() {
		return foregroundColors.toArray(new ColorDescriptor[0]);
	}
	
	/**
	 * Returns background colors of all intervals.
	 * @return background colors of all intervals.
	 */
	public ColorDescriptor [] getBackgroundColors() {
		return backgroundColors.toArray(new ColorDescriptor[0]);
	}
	
	/**
	 * Returns fonts of all intervals.
	 * @return fonts of all intervals.
	 */
	public FontDescriptor [] getFonts() {
		return fonts.toArray(new FontDescriptor[0]);
	}
	
	/**
	 * Returns the minimum value at the specified index.
	 * @param index the index of which the minimum value is to be returned.
	 * @return the minimum value at the specified index.
	 */
	public double getMinValueAt(int index) {
		return minValues.get(index);
	}
	
	/**
	 * Returns the maximum value at the specified index.
	 * @param index the index of which the maximum value is to be returned.
	 * @return the maximum value at the specified index.
	 */
	public double getMaxValueAt(int index) {
		return maxValues.get(index);
	}

	/**
	 * Returns the foreground color at the specified index.
	 * @param index the index of which the foreground color is to be returned.
	 * @return the foreground color at the specified index.
	 */
	public ColorDescriptor getForegroundColorAt(int index) {
		ColorDescriptor rgb = foregroundColors.get(index);
		if (rgb == null) {
			rgb = new ColorDescriptor(0, 0, 0);
		}
		return rgb;
	}
	
	/**
	 * Returns the background color at the specified index.
	 * @param index the index of which the background color is to be returned.
	 * @return the background color at the specified index.
	 */
	public ColorDescriptor getBackgroundColorAt(int index) {
		ColorDescriptor rgb = backgroundColors.get(index);
		if (rgb == null) {
			rgb = new ColorDescriptor(128, 128, 128);
		}
		return rgb;
	}

	/**
	 * Returns the font at the specified index.
	 * @param index the index of which the font is to be returned.
	 * @return the font at the specified index.
	 */
	public FontDescriptor getFontAt(int index) {
		FontDescriptor fd = fonts.get(index);
		if (fd == null) {
			fd = new FontDescriptor("Segoe UI,9, , , ");
		}
		return fd;
	}
	
	public int getIndexForValue(double value) {
		for (int i = 0, n = minValues.size(); i < n; i++) {
			if (minValues.get(i) <= value && maxValues.get(i) >= value) {
				return i;
			}
		}
		return -1;
	}
}
