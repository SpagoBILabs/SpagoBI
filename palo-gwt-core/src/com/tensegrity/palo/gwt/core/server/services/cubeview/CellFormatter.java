/*
*
* @file CellFormatter.java
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
* @version $Id: CellFormatter.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.palo.api.Cell;
import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;
import org.palo.viewapi.uimodels.formats.BorderData;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.TrafficLightData;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellFormat;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;

/**
 * <code>CellFormatter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CellFormatter.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class CellFormatter {

	public static final void applyDefaultFormat(XCell xCell, Cell cell, NumberFormat format) {
		XCellFormat xFormat = new XCellFormat();
		xFormat.backgroundColor = getDefaultBackgroundColor(cell);
		xFormat.foregroundColor = getDefaultForegroundColor();
		xFormat.font = getDefaultFont();
		xFormat.textDecoration = "none";
		setDefaultBorders(xFormat);
		if (format instanceof DecimalFormat) {
			formatValue(xCell, cell, (DecimalFormat) format);	
		} else {
			formatValue(xCell, cell, (String) null);
		}
		
		xCell.format = xFormat;
	}
	public static final void applyFormat(Format format, XCell xCell, Cell cell, RangePosition positionInRange) {
		XCellFormat xFormat = new XCellFormat();
		applyBackgroundColor(format.getBackgroundColor(), xFormat, cell);
		applyForegroundColor(format.getForegroundColor(), xFormat, cell);
		applyBorder(format.getBorderData(), xFormat, positionInRange);
		applyFont(format.getFontData(), xFormat);
		applyTrafficLight(format.getTrafficLightData(), xFormat, cell);
		//and finally:
		formatValue(xCell, cell, format.getNumberFormat());
		xCell.format = xFormat;
	}
	
	private static final void applyBackgroundColor(ColorDescriptor cd,
			XCellFormat xFormat, Cell cell) {
		xFormat.backgroundColor = (cd == null) ? 
				getDefaultBackgroundColor(cell) : toCssColor(cd);
	}

	private static final void applyForegroundColor(ColorDescriptor cd,
			XCellFormat xFormat, Cell cell) {
		xFormat.foregroundColor = (cd == null) ? 
				getDefaultForegroundColor() : toCssColor(cd);
	}
	
	private static final String getDefaultBackgroundColor(Cell cell) {
		return cell.isConsolidated() ? "#e4f4fe" : "white";
	}
	private static final String getDefaultForegroundColor() {
		return "black";
	}
	
	private static final String toCssColor(ColorDescriptor cd) {
		StringBuilder hex = new StringBuilder();
		hex.append("#");
		hex.append(toHexString(cd.getRed()));
		hex.append(toHexString(cd.getGreen()));
		hex.append(toHexString(cd.getBlue()));
		return hex.toString();
	}
	
	private static final String toHexString(int val) {
		StringBuilder str = new StringBuilder();
		str.append(Integer.toHexString(val));
		if(str.length()<2)
			str.insert(0, "0");
		return str.toString();
	}
	private static final void applyBorder(BorderData[] borders,
			XCellFormat xFormat, RangePosition positionInRange) {
		if (borders == null || borders.length == 0)
			setDefaultBorders(xFormat);
		else {
			for (BorderData border : borders) {
				switch (border.getLinePosition()) {
				case BorderData.HORIZONTAL_TOP:
					if(isTop(positionInRange))
						xFormat.borderTop = toCssBorder(border);
					break;
				case BorderData.HORIZONTAL_BOTTOM:
					if(isBottom(positionInRange))
						xFormat.borderBottom = toCssBorder(border);
					break;
				case BorderData.VERTICAL_LEFT:
					if(isLeft(positionInRange))
						xFormat.borderLeft = toCssBorder(border);
					break;
				case BorderData.VERTICAL_RIGHT:
					if(isRight(positionInRange))
						xFormat.borderRight = toCssBorder(border);
					break;
				}
			}
		}
	}
	private static final boolean isTop(RangePosition position) {
		return position.equals(RangePosition.TOP)
				|| position.equals(RangePosition.TOP_LEFT)
				|| position.equals(RangePosition.TOP_RIGHT);
	}
	private static final boolean isLeft(RangePosition position) {
		return position.equals(RangePosition.LEFT)
				|| position.equals(RangePosition.TOP_LEFT)
				|| position.equals(RangePosition.BOTTOM_LEFT);
	}
	private static final boolean isRight(RangePosition position) {
		return position.equals(RangePosition.RIGHT)
				|| position.equals(RangePosition.TOP_RIGHT)
				|| position.equals(RangePosition.BOTTOM_RIGHT);
	}
	
	private static final boolean isBottom(RangePosition position) {
		return position.equals(RangePosition.BOTTOM)
				|| position.equals(RangePosition.BOTTOM_LEFT)
				|| position.equals(RangePosition.BOTTOM_RIGHT);
	}

	private static final void setDefaultBorders(XCellFormat xFormat) {
		xFormat.borderRight = "1px solid silver";
		xFormat.borderBottom = "1px solid silver";
	}
	private static final String toCssBorder(BorderData border) {
		StringBuilder cssBorder = new StringBuilder();
		cssBorder.append(border.getLineWidth());
		cssBorder.append("px ");
		cssBorder.append(toCssColor(border.getLineColor()));
		cssBorder.append(" ");
		switch (border.getLineStyle()) {
		case BorderData.LINE_DASH:
		case BorderData.LINE_DASHDOT:
			cssBorder.append("dashed");
			break;
		case BorderData.LINE_DOT:
		case BorderData.LINE_DASHDOTDOT:
			cssBorder.append("dotted");
			break;
		case BorderData.LINE_SOLID:
			cssBorder.append("solid");
			break;
		}
		return cssBorder.toString();		
	}
	private static final void applyFont(FontDescriptor font, XCellFormat xFormat) {
		if(font != null) {
			xFormat.font = toCssFont(font);
			if(font.isUnderlined())
				xFormat.textDecoration = "underline";
		} else
			xFormat.font = getDefaultFont();
	}
	private static final String getDefaultFont() {
		return "normal 9pt Tahoma";
	}	
	private static final String toCssFont(FontDescriptor font) {
		StringBuilder cssFont = new StringBuilder();
		if(font.isBold())
			cssFont.append("bold ");
		if(font.isItalic())
			cssFont.append("italic ");
		cssFont.append(font.getSize());
		cssFont.append("pt ");
		cssFont.append(font.getName());
		return cssFont.toString();
	}
	private static final void applyTrafficLight(TrafficLightData trafficLight,
			XCellFormat xFormat, Cell cell) {
		if(trafficLight == null)
			return;
		// traffic light only works for numeric cells:
		if (cell.getType() == Cell.NUMERIC) {
			int index = trafficLight.getIndexForValue(
					Double.parseDouble(cell.getValue().toString()));
			if (index > -1) {
				applyBackgroundColor(trafficLight.getBackgroundColorAt(index), xFormat, cell);
				applyForegroundColor(trafficLight.getForegroundColorAt(index), xFormat, cell);
				applyFont(trafficLight.getFontAt(index), xFormat);
			}
		}
	}
	
	private static final void formatValue(XCell xCell, Cell cell, String numberFormat) {
		Object value = cell.getValue();		
		if (value != null) {			
			if (cell.getType() == Cell.NUMERIC) {
				xCell.value = formatNumber(value, numberFormat);
			} else {
				xCell.value = value.toString();
			}
		} else {
			xCell.value = "";
		}		
	}
	private static final void formatValue(XCell xCell, Cell cell, DecimalFormat numberFormat) {
		Object value = cell.getValue();		
		if (value != null) {			
			if (cell.getType() == Cell.NUMERIC) {
				xCell.value = formatNumber(value, numberFormat);
			} else {
				xCell.value = value.toString();
			}
		} else {
			xCell.value = "";
		}		
	}	
	private static final String formatNumber(Object number, DecimalFormat format) {
		try {
			if (format == null) {
				return formatNumber(number, (String) null);
			}
			return format.format(number);
		} catch (IllegalArgumentException e) {
		}
		return number.toString();
	}
	
	private static final String formatNumber(Object number, String numberFormat) {
		try {
			if (numberFormat == null)
				numberFormat = getDefaultNumberFormat();
			DecimalFormat df = new DecimalFormat(numberFormat);
			return df.format(number);
		} catch (IllegalArgumentException e) {
			/* ignore */
		}
		return number.toString();
	}
	private static final String getDefaultNumberFormat() {
		return "#,##0.00";
	}
}
