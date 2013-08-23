/*
*
* @file FormatHandler1_0.java
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
* @author Philipp Bouillon
*
* @version $Id: FormatHandler1_0.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
*
* @file FormatHandler1_0.java
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
* @author Philipp Bouillon
*
* @version $Id: FormatHandler1_0.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.xml;

import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.StartHandler;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.uimodels.formats.BorderData;
import org.palo.viewapi.uimodels.formats.Format;
import org.xml.sax.Attributes;


/**
 * <code>FormatHandler1_0</code>
 * Defines <code>{@link StartHandler}</code>s and 
 * <code>{@link EndHandler}</code>s to read format definitions in cube views
 * which are stored using version 1.0
 *
 * @author Philipp Bouillon
 * @version $Id: FormatHandler1_0.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
class FormatHandler1_0 extends FormatHandler {
    
	private CubeView view;
	private Format format;
	
    FormatHandler1_0(CubeView view) {
    	super();
    	this.view = view;
    }
    
    protected void registerEndHandlers() {
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "formats/format";
			}

			public void endElement(String uri, String localName, String qName) {
				format = null;
			}
		});
	}
    
    protected void registerStartHandlers() {
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "formats/format";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String id = attributes.getValue("id");
				if (id != null) {
					format = view.addFormat(id);
					format.setBackgroundColor(colorDescriptorFromString(
							attributes.getValue("backgroundColor")));
					format.setFontData(fontDescriptorFromString(
							attributes.getValue("fontData")));
					format.setForegroundColor(colorDescriptorFromString(
							attributes.getValue("fontColor")));
					format.setNumberFormat(attributes.getValue("numberFormat"));
				}
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "formats/format/frame";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String position = attributes.getValue("position");
				String width = attributes.getValue("width");
				String style = attributes.getValue("style");
				String color = attributes.getValue("color");
				BorderData border = 
					createBorderData(position, width, style, color);
				format.addBorderData(border);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "formats/format/trafficlight";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String min = attributes.getValue("min");
				String max = attributes.getValue("max");
				String bgColor = attributes.getValue("backgroundColor");
				String fgColor = attributes.getValue("fontColor");
				String font = attributes.getValue("fontData");
				addTrafficLightData(min, max, bgColor, fgColor, font);
			}
		});
    }
    
    private ColorDescriptor colorDescriptorFromString(String color) {
		if (color == null || color.length() == 0) {
			return null;
		}
		String[] rgb = color.split(",");
		try {
			return new ColorDescriptor(Integer.parseInt(rgb[0]), Integer
					.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
		} catch (Exception e) {
			/* ignore */
		}
		return null;
	}
    private FontDescriptor fontDescriptorFromString(String font) {
		if (font != null) {
			try {
				String[] fontData = font.split("|");
				FontDescriptor fd = new FontDescriptor();
				fd.setName(fontData[1]);
				if(fontData.length > 2)
					fd.setSize(Integer.parseInt(fontData[2]));
				if(fontData.length > 3) {
					int style = Integer.parseInt(fontData[3]);
					fd.setBold(style == 1 || style == 3);
					fd.setItalic(style == 2 || style == 3);
				}
				//underlined is platform dependend...
//				if(fontData.length > 5)
//					fd.setUnderlined(b);
				return fd;
			} catch (Exception e) {
				/* ignore */
			}
		}
		return null;
	}
    private BorderData createBorderData(String position, String width, String style, String color) {
		try {
			int _width = Integer.parseInt(width);
			int _style = Integer.parseInt(style);
			int _position = Integer.parseInt(position);
			return new BorderData(_width, _style, colorDescriptorFromString(color), _position);
		}catch(Exception e) {
			
		}
		return null;
    }
    
    private void addTrafficLightData(String min, String max, String bgColor, String fgColor, String font) {
		try {
			format.addTrafficLightData(
					toDouble(min, Double.MIN_VALUE),
					toDouble(max, Double.MAX_VALUE),
					colorDescriptorFromString(bgColor),
					colorDescriptorFromString(fgColor),
					fontDescriptorFromString(font));
		}catch (Exception e) {
			/* ignore */
		}
    }
    
    private double toDouble(String dbl, double defaultDouble) {
    	double _dbl = Double.parseDouble(dbl);
    	if(Double.isNaN(_dbl))
    		_dbl = defaultDouble;
    	return _dbl;
    }
}
