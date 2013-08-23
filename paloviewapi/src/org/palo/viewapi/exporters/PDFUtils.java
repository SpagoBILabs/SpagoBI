/*
*
* @file PDFUtils.java
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
* @version $Id: PDFUtils.java,v 1.3 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/* (c) 2008 Tensegrity Software GmbH */
package org.palo.viewapi.exporters;

import java.awt.Color;

import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;
import org.palo.viewapi.uimodels.formats.BorderData;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfContentByte;

/**
 * Static helper methods to convert from/to <code>SWT</code> to/from <code>iText</code>.
 * @author AndreasEbbert
 * @version $Id: PDFUtils.java,v 1.3 2010/04/12 11:15:09 PhilippBouillon Exp $
 */
class PDFUtils {
	// no instance
	private PDFUtils() {
	}

	static final BaseColor convertColor(ColorDescriptor desc) {
		if (desc == null)
			return null;
		return new BaseColor(desc.getRed(), desc.getGreen(), desc.getBlue());
	}

	static final int convertFontStyle(FontDescriptor desc) {
		if (desc == null)
			return Font.NORMAL;
		int style = desc.isBold() ? Font.BOLD : 0;
		style |= desc.isItalic() ? Font.ITALIC : 0;
		style |= desc.isUnderlined() ? Font.UNDERLINE : 0;
		return style;
	}

	static final void applyBorderDataLineStyle(PdfContentByte pByte, BorderData bData) {
		if (pByte == null)
			return;
		if (bData == null) {
			pByte.setLineWidth(1f);
			pByte.setLineDash(1, 0);
			pByte.setColorStroke(BaseColor.BLACK);
			return;
		}
		switch (bData.getLineStyle()) {
		case BorderData.LINE_DASH:
			pByte.setLineDash(LINE_DASH_LEN, 0);
			break;
		case BorderData.LINE_DASHDOT:
			pByte.setLineDash(LINE_DASH_DOT, 0);
			break;
		case BorderData.LINE_DASHDOTDOT:
			pByte.setLineDash(LINE_DASH_DOT_DOT, 0);
			break;
		case BorderData.LINE_DOT:
			pByte.setLineDash(LINE_DOT_LEN, 0);
			break;
		}
		pByte.setLineWidth(bData.getLineWidth()/1.5f);
		if (bData.getLineColor() != null)
			pByte.setColorStroke(bData.getLineColor() != null ? PDFUtils.convertColor(bData
					.getLineColor()) : BaseColor.BLACK);
	}

	private final static float LINE_DASH_LEN = 2;
	private final static float LINE_DOT_LEN = 1;
	private final static float[] LINE_DASH_DOT = new float[] { LINE_DASH_LEN, LINE_DOT_LEN };
	private final static float[] LINE_DASH_DOT_DOT = new float[] { LINE_DASH_LEN, LINE_DOT_LEN, LINE_DOT_LEN };
}
