/*
*
* @file DefaultFormat.java
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
* @author ArndHouben
*
* @version $Id: DefaultFormat.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.ui.table.impl;

import org.palo.api.ext.ui.FontDescriptor;
import org.palo.api.ext.ui.Format;
import org.palo.api.ext.ui.ColorDescriptor;

/**
 * <code>DefaultFormat</code>
 * A default implementation of the <code>{@link Format}</code> interface
 *
 * @author ArndHouben
 * @version $Id: DefaultFormat.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
class DefaultFormat implements Format {

	private int priority;
	private ColorDescriptor bgColor;
	private ColorDescriptor fontColor;
	private FontDescriptor font;
	private String nrFmtPattern;
	
	
	public final ColorDescriptor getBackGroundColor() {
		return bgColor;
	}

	public final FontDescriptor getFont() {
		return font;
	}

	public final ColorDescriptor getFontColor() {
		return fontColor;
	}
	
	public final String getNumberFormatPattern() {
		return nrFmtPattern;
	}

	public final int getPriority() {
		return priority;
	}


	final void setBgColor(ColorDescriptor bgColor) {
		this.bgColor = bgColor;
	}

	final void setFont(FontDescriptor font) {
		this.font = font;
	}
	
	final void setFontColor(ColorDescriptor fontColor) {
		this.fontColor = fontColor;
	}

	final void setNumberFormatPattern(String nrFmtPattern) {
		this.nrFmtPattern = nrFmtPattern;
	}

	final void setPriority(int priority) {
		this.priority = priority;
	}
}
