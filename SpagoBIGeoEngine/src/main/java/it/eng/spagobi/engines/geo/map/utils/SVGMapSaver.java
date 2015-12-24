/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.svg.SVGDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class SVGMapSaver.
 * 
 * @author Andrea Gioia
 */
public class SVGMapSaver {
	
	/** The transformer factory. */
	private static TransformerFactory transformerFactory;
	
	static {
		transformerFactory = TransformerFactory.newInstance();
	}
	
	/**
	 * Save map.
	 * 
	 * @param doc the doc
	 * @param ouputFile the ouput file
	 * 
	 * @throws FileNotFoundException the file not found exception
	 * @throws TransformerException the transformer exception
	 */
	public static void saveMap(SVGDocument doc, File ouputFile) throws FileNotFoundException, TransformerException{
		saveMap(doc, new FileOutputStream(ouputFile));
	}
	
	/**
	 * Save map.
	 * 
	 * @param doc the doc
	 * @param outputStream the output stream
	 * 
	 * @throws TransformerException the transformer exception
	 */
	public static void saveMap(SVGDocument doc, OutputStream outputStream) throws TransformerException {
		Transformer transformer;
		DOMSource source;
		StreamResult streamResult;		
	    
	    transformer = transformerFactory.newTransformer();
	    source = new DOMSource(doc);	    
	    streamResult = new StreamResult(outputStream);
	    transformer.transform(source, streamResult); 
	}
}
