/*
*
* @file FormatBuilder.java
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
* @version $Id: FormatBuilder.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
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
 * <code>FormatBuilder</code>
 * A builder for <code>Format</code> instances which hides their creation
 *
 * @author ArndHouben
 * @version $Id: FormatBuilder.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class FormatBuilder {

	private ColorDescriptor bgColor, fontColor;
	private int prio;
	private String nrFmt;
	private String name;
	private int size;
	private boolean bold, italic, underlined;
	
	
	//the various set methods...
	final void setPriority(String prio) {
		this.prio = (prio != null && prio.length() > 0) ? Integer
				.parseInt(prio) : 1;
	}
	final void setNumberFormatTemplate(String fmt) {
			nrFmt = fmt;
	}
	final void setBackGroundColor(String r, String g, String b) {
		bgColor = getRGB(r, g, b);
	}
	
	final void setFontColor(String r, String g, String b) {
		fontColor = getRGB(r, g, b);
	}
	
	final void setFontName(String name) {
		this.name = name;
	}

	final void setFontSize(String size) {
		this.size = (size != null && size.length()>0) ? 
				Integer.parseInt(size) : 8;
	}
	
	final void setBold(String bold) {
		this.bold = bold != null ? "bold".equalsIgnoreCase(bold) : false;
	}
	
	final void setItalic(String italic) {
		this.italic = italic != null ? 
				"italic".equalsIgnoreCase(italic) : false;
	}
	
	final void setUnderlined(String underlined) {
		this.underlined = underlined != null ? 
				"underlined".equalsIgnoreCase(underlined) : false;
	}
	
	
	Format create() {
		//check values and create format:
		FontDescriptor fd = new FontDescriptor();
		fd.setName(name);
		fd.setSize(size);
		fd.setBold(bold);
		fd.setItalic(italic);
		fd.setUnderlined(underlined);
		if(name==null)
			fd=null;
		
		DefaultFormat fmt = new DefaultFormat();
		fmt.setBgColor(bgColor);
		fmt.setFont(fd);
		fmt.setFontColor(fontColor);
		fmt.setNumberFormatPattern(nrFmt);
		fmt.setPriority(prio);
		return fmt;
	}
	
	private final ColorDescriptor getRGB(String r, String g, String b) {
		int _r = (r!=null && r.length()>0) ? Integer.parseInt(r) : 255;
		int _g = (g!=null && g.length()>0) ? Integer.parseInt(g) : 255;
		int _b = (b!=null && b.length()>0) ? Integer.parseInt(b) : 255;
		return new ColorDescriptor(_r,_g,_b);
	}
}
