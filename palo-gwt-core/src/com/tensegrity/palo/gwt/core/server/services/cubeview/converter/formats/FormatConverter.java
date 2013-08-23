/*
*
* @file FormatConverter.java
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
* @version $Id: FormatConverter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats;

import org.palo.viewapi.CubeView;
import org.palo.viewapi.internal.util.XMLUtil;

import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.xml.LegacyFormatHandler;

/**
 * <code>FormatConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: FormatConverter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class FormatConverter {

	public static final String PROPERTY_FORMAT_RANGES = "PropertyFormatRanges";
	private static final String FORMAT_DESCRIPTION_PROPERTY = "formatDescription";
	private static final String RANGE_DESCRIPTION_PROPERTY = "formatRanges";

	public static final int[] getFromToIndices(String from_to) {
		if(from_to == null)
			return new int[0];
    	String[] indices = from_to.split(",");
    	int[] fromTo = new int[indices.length];
    	for(int i=0; i<indices.length; i++)
    		fromTo[i] = Integer.parseInt(indices[i]);
    	return fromTo;
	}

	public static final String getFromToString(String from, String to) {
		return from + "," + to;
	}

	public static final void convert(org.palo.api.CubeView legacyView,
			CubeView newView) {
		parseFormats(legacyView, newView);

	}
	
	private static final void parseFormats(org.palo.api.CubeView legacyView, CubeView view) {
		String formatXML = legacyView.getPropertyValue(FORMAT_DESCRIPTION_PROPERTY);
		if (formatXML != null) {
			LegacyFormatHandler.parseFormat(XMLUtil.dequoteString(formatXML), view);

			String rangesXML = legacyView.getPropertyValue(RANGE_DESCRIPTION_PROPERTY);
			if (rangesXML != null) {
				LegacyFormatHandler.parseRange(XMLUtil.dequoteString(rangesXML), view);
			}
		}
	}
}
