/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.chart.services;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.chart.Utilities.ExportCharts;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.mime.MimeUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import sun.misc.BASE64Decoder;



/**
 * The Class ExportHighChartsAction.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ExportExtChartsAction extends AbstractEngineAction {	
	
	// INPUT PARAMETERS
	public static String SVG = "svg";
	public static String TITLE = "title";
	public static String SUBTITLE = "subtitle";
	public static String OUTPUT_FORMAT = "type";
 

	public static String OUTPUT_FORMAT_PNG = "PNG";
	public static String OUTPUT_FORMAT_JPEG = "JPG";
	public static String OUTPUT_FORMAT_PDF = "PDF";
	public static String OUTPUT_FORMAT_SVG = "SVG+XML";

	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(ExportExtChartsAction.class);
    private static final BASE64Decoder DECODER = new BASE64Decoder();
    
    public static final String ENGINE_NAME = "SpagoBIChartEngine";
		
    public void service(SourceBean request, SourceBean response) {
    	
    	logger.debug("IN");
    	InputStream inputStream = null;
    	OutputStream outputStream = null;
    	
    	try {
			super.service(request, response);
			
			freezeHttpResponse();
			
			String svg = this.getAttributeAsString(SVG);
			if (!svg.startsWith("<svg")){
				svg = svg.substring(svg.indexOf("<svg"));
			}
			//System.out.println("svg: " + svg);
			
			inputStream = new ByteArrayInputStream(svg.getBytes("UTF-8"));
			String outputType = this.getAttributeAsString(OUTPUT_FORMAT);
			if (outputType == null || outputType.trim().equals("")) {
				logger.debug("Output format not specified, default is " + OUTPUT_FORMAT_JPEG);
				outputType = OUTPUT_FORMAT_JPEG;
			}
			
			File exportFile = null;
			String ext = null;
			if (outputType.equalsIgnoreCase(OUTPUT_FORMAT_PNG)) {
				ext = ".png";
				exportFile = File.createTempFile("chart", ext);
				outputStream = new FileOutputStream(exportFile);
				ExportCharts.transformSVGIntoPNG(inputStream, outputStream);
			} else if (outputType.equalsIgnoreCase(OUTPUT_FORMAT_JPEG)) {
				ext = ".jpg";
				exportFile = File.createTempFile("chart", ext);
				outputStream = new FileOutputStream(exportFile);
				ExportCharts.transformSVGIntoJPEG(inputStream, outputStream);
			} else if (outputType.equalsIgnoreCase(OUTPUT_FORMAT_PDF)) {
				ext = ".pdf";
				exportFile = File.createTempFile("chart", ext);
				outputStream = new FileOutputStream(exportFile);
				ExportCharts.transformSVGIntoPDF(inputStream, outputStream);
			} else if (outputType.equalsIgnoreCase(OUTPUT_FORMAT_SVG)) {
				ext = ".svg";
				exportFile = File.createTempFile("chart", ext);
				outputStream = new FileOutputStream(exportFile);
				ExportCharts.writeSVG(inputStream, outputStream);
			} else {
				throw new SpagoBIEngineRuntimeException("Output format [" + outputType + "] not supperted");
			}
			
			String mimetype = MimeUtils.getMimeType(exportFile);
			
			try {
				writeBackToClient(exportFile, null, false, exportFile.getName(), mimetype);
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error(e);
				}
			} 
			logger.debug("OUT");
		}		

	}
    
    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String line  = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }
        return stringBuilder.toString();
     }

}
