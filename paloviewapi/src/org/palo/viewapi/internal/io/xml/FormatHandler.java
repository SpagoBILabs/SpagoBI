/*
*
* @file FormatHandler.java
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
* @version $Id: FormatHandler.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import org.palo.api.PaloAPIException;
import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.internal.util.XMLUtil;
import org.palo.viewapi.uimodels.formats.BorderData;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatImpl;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;
import org.palo.viewapi.uimodels.formats.TrafficLightData;
import org.xml.sax.Attributes;

/**
 * <code>FormatHandler</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: FormatHandler.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class FormatHandler implements IXMLHandler {

	public static final String XPATH = "/view/format";
	
	private static final String FORMAT_RANGE = "/view/format/range";
	private static final String FORMAT_TRAFFICLIGHT = "/view/format/trafficlight";
	private static final String FORMAT_BORDER = "/view/format/border";
	
	private Format format;
	private final CubeView view;
	
	public FormatHandler(CubeView view) {
		this.view = view;
	}
	
	public void enter(String path, Attributes attributes) {
		if (format == null && path.equals(XPATH)) {
			// required format attributes:
			String id = attributes.getValue("id");
			if (id == null || id.equals("")) {
				throw new PaloAPIException("FormatHandler: no format id defined!");
			}
			
			// optional format attributes:
			String backgroundColor = attributes.getValue("backgroundColor");
			String fontColor = attributes.getValue("fontColor");
			String fontData = attributes.getValue("fontData");
			String numberFormat = attributes.getValue("numberFormat");
			
			format = new FormatImpl(id,
					parseRGBString(backgroundColor), numberFormat,
					parseFontString(fontData), parseRGBString(fontColor),
					null, null);

			// add format to view...
			view.addFormat(format);
		} else if (path.equals(FORMAT_TRAFFICLIGHT)) {
			if (format == null) {
				throw new PaloAPIException("FormatHandler: no format created!");
			}
			// required traffic light attributes:
			String min = attributes.getValue("min");
			String max = attributes.getValue("max");
			String backgroundColor = attributes.getValue("backgroundColor");
			String fontColor = attributes.getValue("fontColor");
			String fontData = attributes.getValue("fontData");
			try {
				format.addTrafficLightData(
						Double.parseDouble(min),
						Double.parseDouble(max),
						parseRGBString(backgroundColor),
						parseRGBString(fontColor),
						parseFontString(fontData));
			} catch (Exception e) {
				throw new PaloAPIException("FormatHandler: invalid traffic light parameters!");
			}
		} else if (path.equals(FORMAT_BORDER)) {
			if (format == null) {
				throw new PaloAPIException("FormatHandler: no format created!");
			}
			// required border attributes:
			String position = attributes.getValue("position");
			String width = attributes.getValue("width");
			String style = attributes.getValue("style");
			String color = attributes.getValue("color");
			try {
				format.addBorderData(new BorderData(
						Integer.parseInt(width),
						Integer.parseInt(style),
						parseRGBString(color),
						Integer.parseInt(position)));
			} catch (Exception e) {
				throw new PaloAPIException("FormatHandler: invalid border parameters!");
			}
		} else if (path.equals(FORMAT_RANGE)) {
			if (format == null) {
				throw new PaloAPIException("FormatHandler: no format created!");
			}
			// required range attributes:
			String coords = attributes.getValue("coords");
			String levels = attributes.getValue("levels");
			if (levels != null) {
				format.addRange(new FormatRangeInfo(
						view.getCube().getDimensions(), levels, false));
			} 
			if (coords != null) {
				format.addRange(new FormatRangeInfo(
						view.getCube().getDimensions(), coords, true));				
			}
		}
	}

	public String getXPath() {
		return XPATH;
	}

	public void leave(String path, String value) {
		if (format == null) {
			throw new PaloAPIException("FormatHandler: no format created!");
		}
		// Nothing to do...
	}
	
    private final ColorDescriptor parseRGBString(String color) {
    	if (color == null || color.length() == 0) {
    		return null;
    	}
    	String [] res = color.split(",");
    	if (res.length != 3) {
    		return null;
    	}
    	try {
    		return new ColorDescriptor(Integer.parseInt(res[0]),
    					   Integer.parseInt(res[1]),
    					   Integer.parseInt(res[2]));
    	} catch (NumberFormatException e) {
    		throw new PaloAPIException("FormatHandler: Invalid color specified.");
    	}
    }
	
    private final FontDescriptor parseFontString(String font) {
    	if (font == null || font.length() == 0) {
    		return null;
    	}
    	FontDescriptor desc = new FontDescriptor(font);
    	return desc;
    }
    
	public static final String getPersistenceString(Format format) {
		StringBuffer xml = new StringBuffer();
		xml.append("<format"); //$NON-NLS-1$
		xml.append(" id=\"" + XMLUtil.printQuoted(format.getId()) + "\"\r\n"); //$NON-NLS-1$
		if (format.getBackgroundColor() != null) {
			xml.append(getColorXML("backgroundColor", format.getBackgroundColor())); //$NON-NLS-1$
		}
		if (format.getForegroundColor() != null) {
			xml.append(getColorXML("fontColor", format.getForegroundColor())); //$NON-NLS-1$
		}
		if (format.getFontData() != null) {
			xml.append("  fontData=\"" + XMLUtil.printQuoted(format.getFontData().toString()) + "\"\r\n");
		}
		if (format.getNumberFormat() != null && format.getNumberFormat().length() != 0) {
			xml.append("  numberFormat=\"" + XMLUtil.printQuoted(format.getNumberFormat()) + "\"\r\n");
		}
		xml.append(">\r\n");
		String borderData = getBorderXML(format);
		if (borderData.length() > 0) {
			xml.append(borderData);
		}
		TrafficLightData traffic = format.getTrafficLightData();
		if (traffic != null && traffic.getSize() > 0) {
			for (int i = 0, n = traffic.getSize(); i < n; i++) {
				xml.append(getTrafficXML(traffic, i));
			}
		}
		
		for (FormatRangeInfo range: format.getRanges()) {
			xml.append(getPersistenceString(range));
		}
		xml.append("</format>\r\n");

		return xml.toString();
	}
	
	private static final String getPersistenceString(FormatRangeInfo range) {
		StringBuffer xml = new StringBuffer();

		xml.append("    <range\r\n"); //$NON-NLS-1$
		if (range.getCells() != null) {
			xml.append("      coords=\"" + XMLUtil.printQuoted(range.toString()) + "\"\r\n"); //$NON-NLS-1$
		} else if (range.getDimensions().length != 0) {
			xml.append("      levels=\"" + XMLUtil.printQuoted(range.toString()) + "\"\r\n"); //$NON-NLS-1$
		}
		xml.append("    />\r\n");

		return xml.toString();
	}
	
	private static final String getColorXML(String tagName, ColorDescriptor color) {
		String colorString;
		if (color == null) {
			colorString = "0,0,0";
		} else {
			colorString = color.getRed() + "," + //$NON-NLS-1$
        				  color.getGreen() + "," + //$NON-NLS-1$
        				  color.getBlue();
		}
		return "  " + tagName + "=\"" + XMLUtil.printQuoted(colorString) + "\"\r\n";
	}
	
	private static final String getBorderXML(Format desc) {
		StringBuffer borderXML = new StringBuffer();
		BorderData [] borders = desc.getBorderData();
		if (borders == null || borders.length == 0) {
			return "";
		}
		for (int i = 0, n = borders.length; i < n; i++) {			
			BorderData data = borders[i];
			borderXML.append("  <border position=\"" + 
					XMLUtil.printQuoted(data.getLinePosition()) + "\"\r\n");
			borderXML.append("         width=\"" +
					XMLUtil.printQuoted(data.getLineWidth()) + "\"\r\n");
			borderXML.append("         style=\"" +
					XMLUtil.printQuoted(data.getLineStyle()) + "\"\r\n");
			borderXML.append(getColorXML("       color", data.getLineColor()));
			borderXML.append("  />\r\n");
		}
		return borderXML.toString();
	}
	
	private static final String getTrafficXML(TrafficLightData data, int i) {
		StringBuffer trafficXML = new StringBuffer("  <trafficlight ");
		trafficXML.append("min=\"" + XMLUtil.printQuoted("" + data.getMinValueAt(i)) + "\"\r\n");
		trafficXML.append("                max=\"" + XMLUtil.printQuoted("" + data.getMaxValueAt(i)) + "\"\r\n");
		trafficXML.append(getColorXML("              backgroundColor", data.getBackgroundColorAt(i)));
		trafficXML.append(getColorXML("              fontColor", data.getForegroundColorAt(i)));
		trafficXML.append("                fontData=\"");
		trafficXML.append(XMLUtil.printQuoted(data.getFontAt(i).toString()));
		trafficXML.append("\"\r\n");
		trafficXML.append("  />\r\n");
		return trafficXML.toString();
	}	
}
