/*
*
* @file SubsetReader.java
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
* @version $Id: SubsetReader.java,v 1.8 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.impl.SubsetHandlerImpl;
import org.palo.api.subsets.io.xml.SubsetXMLHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * <code>Subset2Reader</code>
 * <p>
 * Singleton to for reading and creating <code>Subset2</code>s from xml
 * </p>
 *
 * @author ArndHouben
 * @version $Id: SubsetReader.java,v 1.8 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class SubsetReader {

	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static SubsetReader instance = new SubsetReader();
	static final SubsetReader getInstance() {
		return instance;
	}

	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private SubsetReader() {
	}

	/**
	 * Creates a new <code>Subset2</code> instance with the given name and type
	 * from the provided input stream.
	 * @param handler used for subset creation
	 * @param name the subset name
	 * @param input the input stream which contains the xml subset representation
	 * @param type the subset type
	 * @return the new created subset
	 */
	final Subset2 fromXML(SubsetHandlerImpl handler, String name,
			InputStream input, int type) throws PaloIOException {
		Subset2 subset = null;
		try {
			String path2xsd = getXSD(input);
			if(path2xsd == null) {
				String dbName = handler.getDimension().getDatabase().getName();
				throw new PaloAPIException("Unknown subset version!" +
						"\nDatabase: "+dbName+"\nSubset: "+name);
			}

			if (!isValid(input, path2xsd)) {
				String dbName = handler.getDimension().getDatabase().getName();
				throw new PaloAPIException("Not a valid subset xml definition!" +
						"\nDatabase: "+dbName+"\nSubset: "+name);
			}
			
			StreamSource xml = new StreamSource(input);
			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema xsd = sf.newSchema(new StreamSource(SubsetReader.class
					.getResourceAsStream(path2xsd)));
			SubsetReaderErrorHandler errHandler = 
				new SubsetReaderErrorHandler();
			ValidatorHandler vHandler = xsd.newValidatorHandler();
			vHandler.setErrorHandler(errHandler);
			TypeInfoProvider typeProvider = vHandler.getTypeInfoProvider();
			SubsetXMLHandler subset2Handler = 
				new SubsetXMLHandler(typeProvider, handler, name, type);
			vHandler.setContentHandler(subset2Handler);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(vHandler);
			parser.parse(new InputSource(xml.getInputStream()));
			subset = subset2Handler.getSubset();
			// dump(subset);
		} catch (SAXException e) {
			throw new PaloIOException("XML Exception during subset loading!", e);
		} catch (IOException e) {
			throw new PaloIOException("IOException during subset loading!", e);
		} catch(Exception e) {
			throw new PaloIOException("Exception during subset loading!", e);
		}
		return subset;
	}

	private final boolean isValid(InputStream input, String path2xsd) throws SAXException,
			IOException {
		StreamSource xml = new StreamSource(input);

		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema xsd = sf.newSchema(new StreamSource(SubsetReader.class
				.getResourceAsStream(path2xsd)));

		SubsetReaderErrorHandler errHandler = new SubsetReaderErrorHandler();
		Validator validator = xsd.newValidator();
		validator.setErrorHandler(errHandler);
		
		validator.validate(xml);
		
		if(input.markSupported())
			input.reset();
		return !errHandler.hasErrors();
	}
	
	private final String getSubsetVersion(InputStream input) throws IOException {
		final String[] version = new String[] { "" };
		// XSModel
		try {
			SAXParserFactory sF = SAXParserFactory.newInstance();
			SAXParser parser = null;
			DefaultHandler annotationHandler = new DefaultHandler() {
				public void processingInstruction(String target, String data)
						throws SAXException {
					if (target.equals("palosubset")) {
						version[0] = 
							data.substring(9, data.length() - 1).trim();
					}
					super.processingInstruction(target, data);
				}

			};
			parser = sF.newSAXParser();
			parser.parse(input, annotationHandler);
		} catch (Exception ex) {
			/* ignore */
		}

		if (input.markSupported())
			input.reset();
		return version[0];
	}
	
	private final String getXSD(InputStream input) throws IOException {		
		String version = getSubsetVersion(input);
		StringBuffer path = new StringBuffer();
		//naming convention:		
		path.append("xml/schemas/subset_");
		path.append(version);
		path.append(".xsd");
		String path2xsd = path.toString();
		//check if xsd exists:
		URL rsc = SubsetReader.class.getResource(path2xsd);
		if(rsc == null)
			return null;
		return path2xsd;
	}
}

