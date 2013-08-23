/*
*
* @file CubeViewReader.java
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
* @version $Id: CubeViewReader.java,v 1.8 2010/01/13 08:02:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.palo.api.Cube;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.io.xml.CubeViewXMLHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <code>CubeViewReader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewReader.java,v 1.8 2010/01/13 08:02:42 PhilippBouillon Exp $
 **/
public class CubeViewReader {
	public static boolean CHECK_RIGHTS = true;
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static CubeViewReader instance = new CubeViewReader();
	static final CubeViewReader getInstance() {
		return instance;
	}

	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private CubeViewReader() {
	}


	public CubeView fromXML(AuthUser user, View view, Cube cube, InputStream input) throws PaloIOException {
		CubeView cView = null;
		boolean oldRights = CHECK_RIGHTS;
		try {
//			if (!isValid(input)) {
////				String dbName = cube.getDatabase().getName();
//				//TODO throw a different exception here ??
//				throw new PaloAPIException("Not a valid cube view xml definition!");
////				+"\nDatabase: "+dbName+"\nCube view: "+name);
//			}
			CHECK_RIGHTS = false;
			StreamSource xml = new StreamSource(input);
			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema xsd = sf.newSchema(new StreamSource(CubeViewReader.class
					.getResourceAsStream("xml/cubeview.xsd")));
			CubeViewReaderErrorHandler errHandler = 
				new CubeViewReaderErrorHandler();
			ValidatorHandler vHandler = xsd.newValidatorHandler();
			vHandler.setErrorHandler(errHandler);
//			TypeInfoProvider typeProvider = vHandler.getTypeInfoProvider();
			CubeViewXMLHandler viewHandler = new CubeViewXMLHandler(user, view, cube);
			vHandler.setContentHandler(viewHandler);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(vHandler);
			parser.parse(new InputSource(xml.getInputStream()));
			cView = viewHandler.getView();
			// dump(subset);
		} catch (SAXException e) {
			throw new PaloIOException("XML Exception during loading of cube view!", e);
		} catch (IOException e) {
			throw new PaloIOException("IOException during loading of cube view!", e);
		} catch(Exception e) {
			throw new PaloIOException("Exception during loading of cube view!", e);
		} finally {
			CHECK_RIGHTS = oldRights;
		}
		return cView;
	}

	private final boolean isValid(InputStream input) throws SAXException,
			IOException {
		StreamSource xml = new StreamSource(input);
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema xsd = sf.newSchema(new StreamSource(CubeViewReader.class
				.getResourceAsStream("xml/cubeview.xsd")));

		CubeViewReaderErrorHandler errHandler = new CubeViewReaderErrorHandler();
		Validator validator = xsd.newValidator();
		validator.setErrorHandler(errHandler);
		validator.validate(xml);
		if(input.markSupported())
			input.reset();
		return !errHandler.hasErrors();
	}
}
