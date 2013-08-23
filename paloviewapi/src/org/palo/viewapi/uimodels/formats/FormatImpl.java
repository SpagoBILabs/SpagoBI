/*
*
* @file FormatImpl.java
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
* @version $Id: FormatImpl.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.uimodels.formats;

import java.util.ArrayList;

import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;

/**
 * TODO DOCUMENT ME
 * 
 * @author PhilippBouillon
 * @version $Id: FormatImpl.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public class FormatImpl implements Format {
	private final String id;
	private ColorDescriptor backgroundColor;
	private String numberFormat;
	private FontDescriptor fontData;
	private ColorDescriptor foregroundColor;
	private final ArrayList <BorderData> borderData = new ArrayList<BorderData>();
	private final ArrayList <Double> minValues = new ArrayList<Double>();
	private final ArrayList <Double> maxValues = new ArrayList<Double>();
	private final ArrayList <ColorDescriptor> backgroundValues = new ArrayList<ColorDescriptor>();
	private final ArrayList <ColorDescriptor> foregroundValues = new ArrayList<ColorDescriptor>();
	private final ArrayList <FontDescriptor> fontValues = new ArrayList<FontDescriptor>();
	private final ArrayList <FormatRangeInfo> ranges = new ArrayList<FormatRangeInfo>();

	public FormatImpl(String id,
			ColorDescriptor backgroundColor, String numberFormat,
			FontDescriptor font, 
			ColorDescriptor fontColor, BorderData [] borders, 
			TrafficLightData traffic) {
		this.backgroundColor = backgroundColor;
		this.numberFormat = numberFormat;
		this.fontData = font;
		this.foregroundColor = fontColor;
		setBorderData(borders);
		setTrafficLightData(traffic);
		this.id = id;
	}

	private FormatImpl(FormatImpl format) {
		//TODO since format will change we implement it correctly later...
		this.backgroundColor = format.backgroundColor;
		this.numberFormat = format.numberFormat;
		this.fontData = format.fontData;
		this.foregroundColor = format.foregroundColor;
		setBorderData(format.getBorderData());
		setTrafficLightData(format.getTrafficLightData());
		this.id = format.id;
		this.ranges.clear();
		this.ranges.addAll(format.ranges);
	}
	
	public FormatImpl(String id) {	
		this.id = id;
	}
		
	public String getId() {
		return id;
	}
		
	public final void setBackgroundColor(ColorDescriptor backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public final ColorDescriptor getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public final void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}
	
	public final String getNumberFormat() {
		return this.numberFormat;
	}
	
	public final void setFontData(FontDescriptor fontData) {
		this.fontData = fontData;
	}
	
	public final FontDescriptor getFontData() {
		return this.fontData;
	}
	
	public final void setForegroundColor(ColorDescriptor fontColor) {
		this.foregroundColor = fontColor;
	}
	
	public final ColorDescriptor getForegroundColor() {
		return this.foregroundColor;
	}
	
	public final void setBorderData(BorderData [] borders) {
		if (borders != null) {
			for (BorderData f: borders) {
				borderData.add(f);
			}
		}		
	}
	
	public final void addBorderData(BorderData border) {
		this.borderData.add(border);
	}
	
	public final BorderData [] getBorderData() {
		return borderData.toArray(new BorderData[0]);
	}
	
	public final void addTrafficLightData(double min, double max, ColorDescriptor background,
			ColorDescriptor foreground, FontDescriptor font) {
		minValues.add(min);
		maxValues.add(max);
		backgroundValues.add(background);
		foregroundValues.add(foreground);
		fontValues.add(font);
	}
	
	public final void setTrafficLightData(TrafficLightData traffic) {
		minValues.clear();
		maxValues.clear();
		backgroundValues.clear();
		foregroundValues.clear();
		fontValues.clear();
		if (traffic != null) {
			for (int i = 0, n = traffic.getSize(); i < n; i++) {
				addTrafficLightData(
						traffic.getMinValueAt(i),
						traffic.getMaxValueAt(i),
						traffic.getBackgroundColorAt(i),
						traffic.getForegroundColorAt(i),
						traffic.getFontAt(i));
			}
		}		
	}
	
	public final TrafficLightData getTrafficLightData() {
		if (backgroundValues.size() == 0 && foregroundValues.size() == 0) {
			if (minValues.size() == 0 && maxValues.size() == 0 &&
				fontValues.size() == 0) {
				return null;
			}
		}
		double [] minVals = new double[minValues.size()];
		double [] maxVals = new double[maxValues.size()];
		for (int i = 0; i < minVals.length; i++) {
			minVals[i] = minValues.get(i);
			maxVals[i] = maxValues.get(i);
		}
		return new TrafficLightData(
				minVals, maxVals, backgroundValues.toArray(new ColorDescriptor[0]),
				foregroundValues.toArray(new ColorDescriptor[0]),
				fontValues.toArray(new FontDescriptor[0]));
	}
	
	public final Format copy() {
		return new FormatImpl(this);
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer("Format Id = " + id + "\n  Font: ");
		
		if (fontData == null) {
	       	result.append("<null>\n");
	    } else {
	       	result.append("\n");
       		result.append("    " + fontData.getName() + "-" + fontData.getSize() + "-" + fontData.isBold() + "-" + fontData.isItalic() + "-" + fontData.isUnderlined() + "\n");
	    }	                
		
		if (borderData.size() > 0) {
	        result.append("  Border: <");
	        boolean first = true;
	        for (BorderData border: borderData) {
	        	if (!first) {
	        		result.append(", ");
	        	}
	        	first = false;
	        	result.append(border);
	        }
	        result.append(">\n");
	    } else {
	      	result.append("  Border: <null>\n");
	    }
	        
		if (numberFormat == null) {
			result.append("  NumberFormat: <null>\n");
		} else {
			result.append("  NumberFormat: " + numberFormat + "\n");
		}
			
	    if (foregroundColor == null) {
	      	result.append("  Foreground: <null>\n");
	    } else {
	      	result.append("  Foreground: " + foregroundColor + "\n");
	    }
	    
	    if (backgroundColor == null) {
	      	result.append("  Background: <null>\n");
	    } else {
	      	result.append("  Background: " + backgroundColor + "\n");
	    }		
	    
	    if (minValues.size() == 0) {
	    	result.append("  Trafficlight: <none>\n");
	    } else {
	    	result.append("  Trafficlight:\n");
	    	TrafficLightData traffic = getTrafficLightData();
	    	for (int i = 0, n = traffic.getSize(); i < n; i++) {
	    		result.append("    Min: " + traffic.getMinValueAt(i) + ", ");
	    		result.append("    Max: " + traffic.getMaxValueAt(i) + ", ");
	    		result.append("    Foreground: " + traffic.getForegroundColorAt(i) + ", ");
	    		result.append("    Background: " + traffic.getBackgroundColorAt(i) + ", ");
	    		FontDescriptor font = traffic.getFontAt(i);
	    		result.append("    Font: ");
	    		if (font != null) {
    				result.append(font.getName() + "-" + font.getSize() + "-" + font.isBold() + "-" + font.isItalic() + "-" + font.isUnderlined() + "\n");
	    		} else {
	    			result.append("<null>\n");
	    		}
	    	}
	    }
	    
	    return result.toString();
	}

	public FormatRangeInfo[] getRanges() {
		return ranges.toArray(new FormatRangeInfo[0]);
	}

	public void addRange(FormatRangeInfo info) {
		ranges.add(info);
	}

	public FormatRangeInfo getRangeAt(int index) {
		return ranges.get(index);
	}

	public int getRangeCount() {
		return ranges.size();
	}

	public void removeAllRanges() {
		ranges.clear();
	}

	public void removeRange(FormatRangeInfo info) {
		ranges.remove(info);
	}
}
