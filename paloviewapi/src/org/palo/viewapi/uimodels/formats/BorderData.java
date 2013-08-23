/*
*
* @file BorderData.java
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
* @version $Id: BorderData.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.formats;

import org.palo.api.ext.ui.ColorDescriptor;

/**
 * Description class for the border style in a format description. This class
 * describes color, width and line style of all possible lines in a cell.
 * 
 * @author PhilippBouillon
 * @version $Id: BorderData.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
public class BorderData {
	/**
	 * Constant indicating the left vertical line of a cell.
	 */
	public static final int VERTICAL_LEFT = 0;

	/**
	 * Constant indicating the vertical line between two neighboring cells.
	 */	
	public static final int VERTICAL_MIDDLE = 1;
	
	/**
	 * Constant indicating the right vertical line of a cell.
	 */	
	public static final int VERTICAL_RIGHT = 2;
	
	/**
	 * Constant indicating the upper horizontal line of a cell.
	 */	
	public static final int HORIZONTAL_TOP = 3;

	/**
	 * Constant indicating the horizontal line between two neighboring cells.
	 */		
	public static final int HORIZONTAL_MIDDLE = 4;

	/**
	 * Constant indicating the lower horizontal line of a cell.
	 */		
	public static final int HORIZONTAL_BOTTOM = 5;

	/**
	 * Line drawing style for solid lines  (value is 1).
	 */
	public static final int LINE_SOLID = 1;
		
	/**
	 * Line drawing style for dashed lines (value is 2).
	 */
	public static final int LINE_DASH = 2;
		
	/**
	 * Line drawing style for dotted lines (value is 3).
	 */
	public static final int LINE_DOT = 3;
		
	/**
	 * Line drawing style for alternating dash-dot lines (value is 4).
	 */
	public static final int LINE_DASHDOT = 4;
		
	/**
	 * Line drawing style for dash-dot-dot lines (value is 5).
	 */
	public static final int LINE_DASHDOTDOT = 5;

	/**
	 * The width of the line in pixels.
	 */
	private final int lineWidth;
	
	/**
	 * The style of the line, use one of the constants defined above.
	 */
	private final int lineStyle;
	
	/**
	 * The color of the line.
	 */
	private final ColorDescriptor lineColor;
	
	/**
	 * Position of the line in its cell; use one of the constants defined above.
	 */
	private final int linePosition;
	
	/**
	 * Creates a new border description for a cell. Note that a cell can
	 * contain several border descriptions so that each line can be described
	 * individually.
	 * 
	 * @param width the width of the line.
	 * @param style the paint style of the line.
	 * @param color the color of the line.
	 * @param position the position of the line in its cell.
	 */
	public BorderData(int width, int style, ColorDescriptor color, int position) {
		lineWidth = width;
		lineStyle = style;
		lineColor = color;
		linePosition = position;
	}
	
	/**
	 * Returns the width of the line in pixels.
	 * @return the width of the line in pixels.
	 */
	public int getLineWidth() {
		return lineWidth;
	}
	
	/**
	 * Returns the style of the line (one of the LINE_xx constants from above).
	 * @return the style of the line.
	 */
	public int getLineStyle() {
		return lineStyle;
	}
	
	/**
	 * Returns the color of the line.
	 * @return the color of the line.
	 */
	public ColorDescriptor getLineColor() {
		return lineColor;
	}
	
	/**
	 * Returns the position of the line (one of the HORIZONTAL_xx or VERTICAL_xx
	 * constants defined above).
	 * @return the position of the line.
	 */
	public int getLinePosition() {
		return linePosition;
	}
	
	/**
	 * Returns a String representation of this border data.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		switch (lineStyle) {
			case LINE_SOLID: buf.append("Solid ("); break;
			case LINE_DOT: buf.append("Dotted ("); break;
			case LINE_DASH: buf.append("Dashed ("); break;
			case LINE_DASHDOT: buf.append("Dash-dot ("); break;
			case LINE_DASHDOTDOT: buf.append("Dash-dot-dot ("); break;
			default: buf.append("No line ("); break;
		}
		buf.append(lineWidth + ") [");
		buf.append(lineColor + "] @ ");
		switch (linePosition) {
			case HORIZONTAL_TOP: buf.append("top"); break;
			case HORIZONTAL_MIDDLE: buf.append("h-middle"); break;
			case HORIZONTAL_BOTTOM: buf.append("bottom"); break;
			case VERTICAL_LEFT: buf.append("left"); break;
			case VERTICAL_MIDDLE: buf.append("v-middle"); break;
			case VERTICAL_RIGHT: buf.append("right"); break;
		}
		return buf.toString();
	}
}
