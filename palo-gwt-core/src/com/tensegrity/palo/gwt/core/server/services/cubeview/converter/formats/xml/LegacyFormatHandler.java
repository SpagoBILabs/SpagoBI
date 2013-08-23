/*
*
* @file LegacyFormatHandler.java
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
* @version $Id: LegacyFormatHandler.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.xml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.palo.viewapi.CubeView;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code>LegacyFormatHandler</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: LegacyFormatHandler.java,v 1.3 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class LegacyFormatHandler {

	public static void parseFormat(String xml, CubeView newView) {
		if (isValid(xml))
			parse(xml, new FormatXMLHandler(newView));
	}
	
	public static void parseRange(String xml, CubeView newView) {
		if (isValid(xml))
			parse(xml, new FormatRangeXMLHandler(newView));
	}
	
	private static boolean isValid(String xml) {
		return xml != null && xml.trim().length() > 0;
	}
	private static final void parse(String xml, DefaultHandler xmlHandler) {
		try {
			SAXParserFactory sF = SAXParserFactory.newInstance();
			SAXParser parser = sF.newSAXParser();
			parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")),
					xmlHandler);
		} catch (Exception e) {
			System.out.println("Can't parse this:");
			System.out.println(xml);
			// TODO add meaningful error handling
			e.printStackTrace();
		}
	}
}
