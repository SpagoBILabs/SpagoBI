/*
*
* @file Format.java
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
* @version $Id: Format.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.formats;

import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;

/**
 * The format interface describes all visual formats that can be set in a cell
 * or a combination of cells.
 * The format describes the way, text is displayed (which font, which color,
 * which background) and how it is aligned (number formats with or without
 * decimal point, prefixes, ...). It also defines a background color for a
 * cell and can set different values depending on the value of the cell it
 * is applied to.
 * 
 * The format is a mere description. It does not say, on which cells it is
 * actually applied. To see, which format is used where, the FormatRangeInfo
 * interface can be used.
 * 
 * @author PhilippBouillon
 * @version $Id: Format.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public interface Format {
	/**
	 * Returns the id of this format.
	 * @return the id of this format.
	 */
	String getId();
	
	/**
	 * Returns the way, numbers are formatted if this format is applied to a
	 * cell that contains numbers. The syntax is equal to the
	 * java.text.NumberFormat syntax.
	 * @return the number format of this format definition.
	 */
	String getNumberFormat();
	
	/**
	 * Sets a new number format. The syntax is equal to the
	 * java.text.NumberFormat syntax.
	 * @param numberFormat the new number format.
	 */
	void setNumberFormat(String numberFormat);
	
	/**
	 * Returns the text color.
	 * @return the text color.
	 */
	ColorDescriptor getForegroundColor();
	
	/**
	 * Sets a new text color.
	 * @param foregroundColor the new text color.
 	 */
	void setForegroundColor(ColorDescriptor foregroundColor);
	
	/**
	 * Returns the background color.
	 * @return the background color. 
	 */
	ColorDescriptor getBackgroundColor();
	
	/**
	 * Sets a new background color.
	 * @param backgroundColor the new background color.
	 */
	void setBackgroundColor(ColorDescriptor backgroundColor);
	
	/**
	 * Returns the font data for this format description. It contains
	 * information about the font family, size, and styles.
	 * @return the font data.
	 */
	FontDescriptor getFontData();
	
	/**
	 * Sets a new font data for this format.
	 * @param fontData the new font data.
	 */
	void setFontData(FontDescriptor fontData);
	
	/**
	 * Returns an array of border data information for this format definition.
	 * It can contain up to six different border data information objects, so
	 * that each line of a cell (4) can be described and also the "inner lines"
	 * if the format is applied to a range of cells.
	 * 
	 * @return the active border data for this format or an empty array if no
	 * specific data has been set.
	 */
	BorderData [] getBorderData();
	
	/**
	 * Sets the given border data.
	 * @param borders the new border data array.
	 */
	void setBorderData(BorderData [] borders);
	
	/**
	 * Adds the given border data to the existing array of border data.
	 * @param border the new border data.
	 */
	void addBorderData(BorderData border);
	
	/**
	 * Returns any active TrafficLightData.
	 * @return the active TrafficLightData or null if no traffic light has been
	 * set.
	 */
	TrafficLightData getTrafficLightData();
	
	/**
	 * Sets the specified traffic light data.
	 * @param traffic the new traffic light data.
	 */
	void setTrafficLightData(TrafficLightData traffic);
	
	/**
	 * Adds a new "slice" to the existing traffic light data. No plausibility
	 * check is performed, so if you add an overlapping min/max region, the
	 * first one found will be applied.
	 * 
	 * @param min minimum value for this traffic light format spec.
	 * @param max maximum value for this traffic light format spec.
	 * @param background background color for this traffic light.
	 * @param foreground text color for this traffic light.
	 * @param font font for this traffic light.
	 */
	void addTrafficLightData(double min, double max,
							 ColorDescriptor background,
							 ColorDescriptor foreground,
							 FontDescriptor font);
	
	/**
	 * Returns all cell ranges where this format is active.
	 * @return the active cell ranges for this format.
	 */
	FormatRangeInfo [] getRanges();
	
	/**
	 * Returns the number of cell blocks for which this format is active.
	 * @return the number of cell blocks for which this format is active.
	 */
	int getRangeCount();
	
	/**
	 * Returns the range information at the given index.
	 * @param index the index to the range information array.
	 * @return the range information at the given index.
	 */
	FormatRangeInfo getRangeAt(int index);
	
	/**
	 * Adds a new cell range to this format.
	 * @param info the new cell range information object.
	 */
	void addRange(FormatRangeInfo info);
	
	/**
	 * Removes the specified cell range from the array of active ranges.
	 * @param info the range info, which is to be removed.
	 */
	void removeRange(FormatRangeInfo info);
	
	/**
	 * Removes all ranges from the current format.
	 */
	void removeAllRanges();
	
	/**
	 * Creates a copy of this format.
	 * @return a new format that is an exact copy of this format.
	 */
	Format copy();		
}
