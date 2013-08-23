/*
 *
 * @file PaloPDFExporterConfiguration.java
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
 * @author AndreasEbbert
 *
 * @version $Id: PaloPDFExporterConfiguration.java,v 1.3 2010/04/12 11:15:09 PhilippBouillon Exp $
 *
 */

/* (c) 2008 Tensegrity Software GmbH */
package org.palo.viewapi.exporters;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

/**
 * Stores the PDF export configuration data.
 * 
 * @author AndreasEbbert
 * @version $Id: PaloPDFExporterConfiguration.java,v 1.2 2009/12/17 16:14:08
 *          PhilippBouillon Exp $
 */
public class PaloPDFExporterConfiguration {
	public final static String[] PAGE_FORMATS = new String[] { "A0", "A1",
			"A2", "A3", "A4", "A5", "A6", "B0", "B1", "B2", "B3", "B4", "B5",
			"B6", "Executive", "Legal", "Letter" };

	private boolean portrait = true;
	private int pageFormat;
	private String title;
	private boolean showTitle;
	private boolean showPOV;
	private boolean showExpansionStates;
	private boolean indent;
	private boolean showPageNumbers;
	private String path;
	private String maxColString;
	private String maxRowsHeaderString;
	private String cellReplaceString;
	
	public PaloPDFExporterConfiguration() {
	}

	public final static PaloPDFExporterConfiguration createDefault() {
		PaloPDFExporterConfiguration conf = new PaloPDFExporterConfiguration();
		conf.portrait = true;
		conf.pageFormat = 0;
		return conf;
	}

	public void setPath(String s) {
		path = s;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setTitle(String t) {
		title = t;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setShowTitle(boolean b) {
		showTitle = b;
	}
	
	public boolean isShowTitle() {
		return showTitle;
	}
	
	public void setShowPOV(boolean b) {
		showPOV = b;
	}
	
	public boolean isShowPOV() {
		return showPOV;
	}
	
	public void setShowExpansionStates(boolean b) {
		showExpansionStates = b;
	}
	
	public boolean isShowExpansionStates() {
		return showExpansionStates;
	}
	
	public void setIndent(boolean b) {
		indent = b;
	}
	
	public boolean isIndent() {
		return indent;
	}
	
	public void setShowPageNumbers(boolean b) {
		showPageNumbers = b;
	}
	
	public boolean isShowPageNumbers() {
		return showPageNumbers;
	}
	
	public void setPortrait(boolean b) {
		portrait = b;
	}

	public boolean isPortrait() {
		return portrait;
	}

	public void setPageFormat(int i) {
		if (i < 0 || i >= PAGE_FORMATS.length)
			throw new IllegalArgumentException("Illegal Page Format");
		this.pageFormat = i;
	}

	Rectangle getPageSize() {
		Rectangle ps = null;
		switch (pageFormat) {
		case 0:
			ps = PageSize.A0;
			break;
		case 1:
			ps = PageSize.A1;
			break;
		case 2:
			ps = PageSize.A2;
			break;
		case 3:
			ps = PageSize.A3;
			break;
		case 4:
			ps = PageSize.A4;
			break;
		case 5:
			ps = PageSize.A5;
			break;
		case 6:
			ps = PageSize.A6;
			break;
		case 7:
			ps = PageSize.B0;
			break;
		case 8:
			ps = PageSize.B1;
			break;
		case 9:
			ps = PageSize.B2;
			break;
		case 10:
			ps = PageSize.B3;
			break;
		case 11:
			ps = PageSize.B4;
			break;
		case 12:
			ps = PageSize.B5;
			break;
		case 13:
			ps = PageSize.B6;
			break;
		case 14:
			ps = PageSize.EXECUTIVE;
			break;
		case 15:
			ps = PageSize.LEGAL;
			break;
		case 16:
			ps = PageSize.LETTER;
			break;
		default:
			throw new IllegalStateException("invalid page format");
		}
		if (!portrait)
			ps = ps.rotate();
		return ps;
	}
	
	public void setMaxWidths(String maxColString, String maxRowsHeaderString, String cellReplaceString) {
		this.maxColString = maxColString;
		this.maxRowsHeaderString = maxRowsHeaderString;
		this.cellReplaceString = cellReplaceString;
	}
	
	public String getMaxColString() {
		return maxColString;
	}
	
	public String getMaxRowsHeaderString() {
		return maxRowsHeaderString;
	}
	
	public String getCellReplaceString() {
		return cellReplaceString;
	}	
}
