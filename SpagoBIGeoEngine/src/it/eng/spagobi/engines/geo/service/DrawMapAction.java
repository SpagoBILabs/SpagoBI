/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.geo.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.map.utils.SVGMapConverter;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.service.IStreamEncoder;


/**
 * The Class MapDrawAction.
 * 
 */
public class DrawMapAction extends AbstractGeoEngineAction {
	
	// REQUEST PARAMETERS	
	public static final String OUTPUT_FORMAT = "outputFormat";
	public static final String INLINE_RESPONSE = "inline";

	// RESPONSE PARAMETERS
	// ...
	
	// DEFAULT VALUES
	public static final String DEFAULT_OUTPUT_TYPE = GeoEngineConstants.DSVG;
	
	// Default serial version number (just to keep eclipse happy).
	private static final long serialVersionUID = 1L;
	
	// Logger component
    public static transient Logger logger = Logger.getLogger(DrawMapAction.class);
	
	
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		
		String outputFormat = null;
		String executionId = null;
		File maptmpfile = null;
		boolean inlineResponse;
		String responseFileName;
		Monitor totalTimeMonitor = null;
		Monitor totalTimePerFormatMonitor = null;
		Monitor flushingResponseTotalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
	
		logger.debug("IN");
		
		try {		
			super.service(serviceRequest, serviceResponse);
			
			totalTimeMonitor = MonitorFactory.start("GeoEngine.drawMapAction.totalTime");
			
			executionId = getAttributeAsString( "SBI_EXECUTION_ID" );
			
			outputFormat = getAttributeAsString( OUTPUT_FORMAT );		
			logger.debug("Parameter [" + OUTPUT_FORMAT + "] is equal to [" + outputFormat + "]");
			
			
			inlineResponse = getAttributeAsBoolean( INLINE_RESPONSE, true );		
			logger.debug("Parameter [" + INLINE_RESPONSE + "] is equal to [" + inlineResponse + "]");
			
			if(getAuditServiceProxy() != null) getAuditServiceProxy().notifyServiceStartEvent();
			
			if(outputFormat == null) {
				logger.info("Parameter [" + outputFormat + "] not specified into request");
				outputFormat = (String)getGeoEngineInstance().getEnv().get(GeoEngineConstants.ENV_OUTPUT_TYPE);
				logger.debug("Env Parameter [" + GeoEngineConstants.ENV_OUTPUT_TYPE + "] is equal to [" + outputFormat + "]");
			}
			
			if(outputFormat == null) {
				logger.info("Parameter [" + GeoEngineConstants.ENV_OUTPUT_TYPE + "] not specified into environment");
				outputFormat = DEFAULT_OUTPUT_TYPE;
			}
			
			totalTimePerFormatMonitor = MonitorFactory.start("GeoEngine.drawMapAction." + outputFormat + "totalTime");
			
			
			try {
				if(outputFormat.equalsIgnoreCase(GeoEngineConstants.PDF)){
					maptmpfile = getGeoEngineInstance().renderMap( GeoEngineConstants.JPEG );
					
				}else{
					maptmpfile = getGeoEngineInstance().renderMap( outputFormat );
				}
			} catch (Throwable t) {
				 throw new DrawMapServiceException(getActionName(), "Impossible to render map", t);
			}
			
			responseFileName = "map.svg";
			
			IStreamEncoder encoder = null;
			File tmpFile = null;
			if(outputFormat.equalsIgnoreCase(GeoEngineConstants.JPEG)) {
				encoder = new SVGMapConverter();
				responseFileName =  "map.jpeg";
			}else if(outputFormat.equalsIgnoreCase(GeoEngineConstants.PDF)){
				
				encoder = new SVGMapConverter();
				BufferedInputStream bis = null;
				
				String dirS = System.getProperty("java.io.tmpdir");
				File imageFile = null;
				bis = new BufferedInputStream( new FileInputStream(maptmpfile) );
				try {
					int contentLength = 0;
					int b = -1;
					String contentFileName = "tempJPEGExport";
					freezeHttpResponse();
										
					File dir = new File(dirS);
					imageFile = File.createTempFile("tempJPEGExport", ".jpeg" , dir);
					FileOutputStream stream = new FileOutputStream(imageFile);
					
					encoder.encode(bis,stream);
					
					stream.flush();
					stream.close();		
					
					File dirF = new File(dirS);
				    tmpFile = File.createTempFile("tempPDFExport", ".pdf", dirF);
				    Document pdfDocument = new Document();
				    PdfWriter docWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(tmpFile));
				    pdfDocument.open();
				    Image jpg = Image.getInstance(imageFile.getPath());
				    jpg.setRotation(new Double(Math.PI/2).floatValue());
				    jpg.scaleAbsolute(770, 520);
				    pdfDocument.add(jpg);
				    pdfDocument.close();
				    docWriter.close();
				    maptmpfile = tmpFile;
					
				} finally {
					bis.close();
					if(imageFile!=null)imageFile.delete();
				}		

				responseFileName =  "map.pdf";
				encoder = null;
				
			}
			
			try {
				flushingResponseTotalTimeMonitor = MonitorFactory.start("GeoEngine.drawMapAction.flushResponse.totalTime");
				writeBackToClient(maptmpfile, encoder, inlineResponse, responseFileName, getContentType(outputFormat));
				
			} catch(IOException e) {
				logger.error("error while flushing output", e);
				if(getAuditServiceProxy() != null) getAuditServiceProxy().notifyServiceErrorEvent( "Error while flushing output" );
				throw new DrawMapServiceException(getActionName(), "Error while flushing output", e);
			}
			
			if(getAuditServiceProxy() != null) getAuditServiceProxy().notifyServiceEndEvent( );
			
			maptmpfile.delete();	
			if(tmpFile!=null)tmpFile.delete();
			
		} catch (Throwable t) {
			errorHitsMonitor = MonitorFactory.start("GeoEngine.errorHits");
			errorHitsMonitor.stop();
			DrawMapServiceException wrappedException;
			if(t instanceof DrawMapServiceException) {
				wrappedException = (DrawMapServiceException)t;
			} else {
				wrappedException = new DrawMapServiceException(getActionName(), "An unpredicted error occurred while executing " + getActionName() + " service", t);
			}
			
			
			wrappedException.setDescription(wrappedException.getRootCause());
			Throwable rootException = wrappedException.getRootException();
			if(rootException instanceof SpagoBIEngineRuntimeException) {
				wrappedException.setHints( ((SpagoBIEngineRuntimeException)rootException).getHints() );
			}
			
			throw wrappedException;
		} finally {
			if(flushingResponseTotalTimeMonitor != null) flushingResponseTotalTimeMonitor.stop();
			if(totalTimePerFormatMonitor != null) totalTimePerFormatMonitor.stop();
			if(totalTimeMonitor != null) totalTimeMonitor.stop();
		
		}	
		
		logger.debug("OUT");
	}
	
	
	/**
	 * Returns the right content type for the output format.
	 * 
	 * @param outFormat the out format
	 * 
	 * @return the string code of the content type for the output format
	 */
	private String getContentType(String outFormat) {
		if (outFormat.equalsIgnoreCase(GeoEngineConstants.SVG)
				|| outFormat.equalsIgnoreCase(GeoEngineConstants.DSVG)
				|| outFormat.equalsIgnoreCase(GeoEngineConstants.XDSVG))
			return GeoEngineConstants.SVG_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.PDF))
			return GeoEngineConstants.PDF_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.GIF))
			return GeoEngineConstants.GIF_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.JPEG))
			return GeoEngineConstants.JPEG_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.BMP))
			return GeoEngineConstants.BMP_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.X_PNG))
			return GeoEngineConstants.X_PNG_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.HTML))
			return GeoEngineConstants.HTML_MIME_TYPE;
		else if (outFormat.equalsIgnoreCase(GeoEngineConstants.XML))
			return GeoEngineConstants.XML_MIME_TYPE;
		else return GeoEngineConstants.TEXT_MIME_TYPE;
	}
	
	
	
	
	
	

}