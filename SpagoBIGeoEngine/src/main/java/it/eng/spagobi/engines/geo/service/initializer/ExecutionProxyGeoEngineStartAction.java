/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.service.initializer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.geo.GeoEngine;
import it.eng.spagobi.engines.geo.GeoEngineAnalysisState;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.GeoEngineInstance;
import it.eng.spagobi.engines.geo.commons.presentation.DynamicPublisher;
import it.eng.spagobi.engines.geo.map.utils.SVGMapConverter;
import it.eng.spagobi.engines.geo.service.DrawMapAction;
import it.eng.spagobi.engines.geo.service.DrawMapServiceException;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.callbacks.mapcatalogue.MapCatalogueAccessUtils;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;
import it.eng.spagobi.utilities.service.IStreamEncoder;


/**
 * Geo entry point action.
 */
public class ExecutionProxyGeoEngineStartAction extends AbstractEngineStartAction {
	
	private MapCatalogueAccessUtils mapCatalogueServiceProxy;
	private String standardHierarchy;
	
	
	
	// request
	/** The Constant EXECUTION_CONTEXT. */
	public static final String EXECUTION_CONTEXT = "EXECUTION_CONTEXT";
	public static final String EXECUTION_ID = "EXECUTION_ID";
	public static final String DOCUMENT_LABEL = "DOCUMENT_LABEL";
	public static final String OUTPUT_TYPE = "outputType";
	
	//response 
	/** The Constant IS_DOC_COMPOSITION_MODE_ACTIVE. */
	public static final String IS_DOC_COMPOSITION_MODE_ACTIVE =  "isDocumentCompositionModeActive";
	
	// session
	/** The Constant GEO_ENGINE_INSTANCE. */
	public static final String GEO_ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ExecutionProxyGeoEngineStartAction.class);
    
    public static final String ENGINE_NAME = "SpagoBIGeoEngine";
	
    
	// REQUEST PARAMETERS	
	public static final String OUTPUT_FORMAT = "outputFormat";
	public static final String INLINE_RESPONSE = "inline";

	// RESPONSE PARAMETERS
	// ...
	
	// DEFAULT VALUES
	public static final String DEFAULT_OUTPUT_TYPE = GeoEngineConstants.DSVG;
	
	// Default serial version number (just to keep eclipse happy).
	private static final long serialVersionUID = 1L;
    

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws GeoEngineException {
		
		GeoEngineInstance geoEngineInstance;
		Map env;
		byte[] analysisStateRowData;
		GeoEngineAnalysisState analysisState = null;
		String executionContext;
		String executionId;
		String documentLabel;
		String outputType;
		
		Monitor hitsPrimary = null;
        Monitor hitsByDate = null;
        Monitor hitsByUserId = null;
        Monitor hitsByDocumentId = null;
        Monitor hitsByExecutionContext = null;
		
		
		logger.debug("IN");		
		
		try {
			setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);
			
			//if(true) throw new SpagoBIEngineStartupException(getEngineName(), "Test exception");
						
			logger.debug("User Id: " + getUserId());
			logger.debug("Audit Id: " + getAuditId());
			logger.debug("Document Id: " + getDocumentId());
			logger.debug("Template: " + getTemplateAsSourceBean());	
			
			hitsPrimary = MonitorFactory.startPrimary("GeoEngine.requestHits");
	        hitsByDate = MonitorFactory.start("GeoEngine.requestHits." + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));
	        hitsByUserId = MonitorFactory.start("GeoEngine.requestHits." + getUserId());
	        hitsByDocumentId = MonitorFactory.start("GeoEngine.requestHits." + getDocumentId());
			
			
			executionContext = getAttributeAsString( EXECUTION_CONTEXT ); 
			logger.debug("Parameter [" + EXECUTION_CONTEXT + "] is equal to [" + executionContext + "]");
			
			executionId = getAttributeAsString( EXECUTION_ID );
			logger.debug("Parameter [" + EXECUTION_ID + "] is equal to [" + executionId + "]");
			
			documentLabel = getAttributeAsString( DOCUMENT_LABEL );
			logger.debug("Parameter [" + DOCUMENT_LABEL + "] is equal to [" + documentLabel + "]");
			
			outputType = getAttributeAsString(OUTPUT_TYPE);
			logger.debug("Parameter [" + OUTPUT_TYPE + "] is equal to [" + outputType + "]");
			
			logger.debug("Execution context: " + executionContext);
			String isDocumentCompositionModeActive = (executionContext != null && executionContext.equalsIgnoreCase("DOCUMENT_COMPOSITION") )? "TRUE": "FALSE";
			logger.debug("Document composition mode active: " + isDocumentCompositionModeActive);
			
			hitsByExecutionContext = MonitorFactory.start("GeoEngine.requestHits." + (isDocumentCompositionModeActive.equalsIgnoreCase("TRUE")?"compositeDocument": "singleDocument"));
			
			
			env = getEnv("TRUE".equalsIgnoreCase(isDocumentCompositionModeActive), documentLabel, executionId);
			if( outputType != null ) {
				env.put(GeoEngineConstants.ENV_OUTPUT_TYPE, outputType);
			}			
			
			geoEngineInstance = GeoEngine.createInstance(getTemplateAsSourceBean(), env);
			geoEngineInstance.setAnalysisMetadata( getAnalysisMetadata() );
			
			analysisStateRowData = getAnalysisStateRowData();
			if(analysisStateRowData != null) {
				logger.debug("AnalysisStateRowData: " + new String(analysisStateRowData));
				analysisState = new GeoEngineAnalysisState( );
				analysisState.load( analysisStateRowData );
				logger.debug("AnalysisState: " + analysisState.toString());
			} else {
				logger.debug("AnalysisStateRowData: NULL");
			}
			if(analysisState != null) {
				geoEngineInstance.setAnalysisState( analysisState );
			}
			
			String selectedMeasureName  = getAttributeAsString("default_kpi");
			logger.debug("Parameter [" + "default_kpi" + "] is equal to [" + selectedMeasureName + "]");
			
			if(!StringUtilities.isEmpty(selectedMeasureName)) {
				geoEngineInstance.getMapRenderer().setSelectedMeasureName(selectedMeasureName);
			}
			
			
			if("TRUE".equalsIgnoreCase(isDocumentCompositionModeActive)){
				setAttribute(DynamicPublisher.PUBLISHER_NAME, "SIMPLE_UI_PUBLISHER");
			} else {
				setAttribute(DynamicPublisher.PUBLISHER_NAME, "AJAX_UI_PUBLISHER");
			}
			
			
			String id = getAttributeAsString("SBI_EXECUTION_ID");
			setAttributeInSession(GEO_ENGINE_INSTANCE, geoEngineInstance);					
		} catch (Exception e) {
			SpagoBIEngineStartupException serviceException = null;
						
			if(e instanceof SpagoBIEngineStartupException) {
				serviceException = (SpagoBIEngineStartupException)e;
			} else {
				Throwable rootException = e;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				String message = "An unpredicted error occurred while executing " + getEngineName() + " service."
								 + "\nThe root cause of the error is: " + str;
				
				serviceException = new SpagoBIEngineStartupException(getEngineName(), message, e);
			}
			
			throw serviceException;
		} finally {
			if(hitsByExecutionContext != null) hitsByExecutionContext.stop();
			if(hitsByDocumentId != null) hitsByDocumentId.stop();
			if(hitsByUserId != null) hitsByUserId.stop();
			if(hitsByDate != null) hitsByDate.stop();
			if(hitsPrimary != null) hitsPrimary.stop();
			
		}
		

		// Put draw Map Action
		
		String outputFormat = null;
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
			
			//executionId = getAttributeAsString( "SBI_EXECUTION_ID" );
			
			outputFormat = getAttributeAsString( OUTPUT_FORMAT );		
			logger.debug("Parameter [" + OUTPUT_FORMAT + "] is equal to [" + outputFormat + "]");
			
			
			inlineResponse = getAttributeAsBoolean( INLINE_RESPONSE, true );		
			logger.debug("Parameter [" + INLINE_RESPONSE + "] is equal to [" + inlineResponse + "]");
			
			if(getAuditServiceProxy() != null) getAuditServiceProxy().notifyServiceStartEvent();

			IEngineInstance iEngInst=(IEngineInstance)getAttributeFromSession( EngineConstants.ENGINE_INSTANCE );
			GeoEngineInstance geoInstance=(GeoEngineInstance)iEngInst;

			if(outputFormat == null) {
				logger.info("Parameter [" + outputFormat + "] not specified into request");
				
				//outputFormat = (String)((GeoEngineInstance)).getEnv().get(GeoEngineConstants.ENV_OUTPUT_TYPE);
				outputFormat = (String)geoInstance.getEnv().get(GeoEngineConstants.ENV_OUTPUT_TYPE);
				logger.debug("Env Parameter [" + GeoEngineConstants.ENV_OUTPUT_TYPE + "] is equal to [" + outputFormat + "]");
			}
			
			if(outputFormat == null) {
				logger.info("Parameter [" + GeoEngineConstants.ENV_OUTPUT_TYPE + "] not specified into environment");
				outputFormat = DEFAULT_OUTPUT_TYPE;
			}
			
			totalTimePerFormatMonitor = MonitorFactory.start("GeoEngine.drawMapAction." + outputFormat + "totalTime");
			
			
			try {
				if(outputFormat.equalsIgnoreCase(GeoEngineConstants.PDF)){
					maptmpfile = geoInstance.renderMap( GeoEngineConstants.JPEG );
					
				}else{
					maptmpfile = geoInstance.renderMap( outputFormat );
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

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		logger.debug("OUT");
	}
	
	private MapCatalogueAccessUtils getMapCatalogueProxy() {
		if(mapCatalogueServiceProxy == null) {
			mapCatalogueServiceProxy = new MapCatalogueAccessUtils( getHttpSession(), getUserIdentifier() );
		}
		
		return mapCatalogueServiceProxy;
	}
	
	private String getStandardHierarchy() {
		if(standardHierarchy == null) {
			try {
				standardHierarchy = getMapCatalogueProxy().getStandardHierarchy( );
				logger.debug("Standard hierarchy: " + standardHierarchy);
			} catch (Exception e) {
				logger.warn("Impossible to get standard Hierarchy configuration settings from map catalogue");
			}	
		}
		
		return standardHierarchy;
	}
	
	private String getContextUrl() {
		String contextUrl = null;
		
		contextUrl = getHttpRequest().getContextPath();	
		logger.debug("Context path: " + contextUrl);
		
		return contextUrl;
	}
	
	private String getAbsoluteContextUrl() {
		String contextUrl = null;
		
		contextUrl = getHttpRequest().getScheme() + "://" 
					+ getHttpRequest().getServerName() + ":" 
					+ getHttpRequest().getServerPort() + "/" 
					+ getContextUrl();
		logger.debug("Context path: " + contextUrl);
		
		return contextUrl;
	}
	
	
	
	
	public Map getEnv(boolean isDocumentCompositionModeActive, String documentLabel, String executionId) {
		Map env = null;
		
		env = super.getEnv();
		
		IDataSource dataSource = getDataSource();
		IDataSet dataset = getDataSet();
		if( dataset != null ) {
			dataset.setUserProfileAttributes(UserProfileUtils.getProfileAttributes(  getUserProfile() ));
			
			dataset.setParamsMap( env );
		}
		
		env.put(EngineConstants.ENV_DATASOURCE, dataSource);
		env.put(EngineConstants.ENV_DATASET, dataset);
		
		if (dataSource!=null) logger.debug("DataSource: " + dataSource.toString());
		else logger.debug("DataSource is NULL ");
		
		env.put(GeoEngineConstants.ENV_CONTEXT_URL, getContextUrl());
		env.put(GeoEngineConstants.ENV_ABSOLUTE_CONTEXT_URL, getAbsoluteContextUrl());
		
		env.put(GeoEngineConstants.ENV_MAPCATALOGUE_SERVICE_PROXY, getMapCatalogueProxy());
		
		if(isDocumentCompositionModeActive) {
			env.put(GeoEngineConstants.ENV_IS_DAFAULT_DRILL_NAV, "FALSE");
			env.put(GeoEngineConstants.ENV_IS_WINDOWS_ACTIVE, "FALSE");
			env.put(GeoEngineConstants.ENV_EXEC_IFRAME_ID, "iframe_" + documentLabel);
		} else {
			env.put(GeoEngineConstants.ENV_IS_WINDOWS_ACTIVE, "TRUE");
			env.put(GeoEngineConstants.ENV_EXEC_IFRAME_ID, "iframeexec" + executionId);
		}
		
		if(getStandardHierarchy() != null) {
			env.put(GeoEngineConstants.ENV_STD_HIERARCHY, getStandardHierarchy());
		}		
		
		return env;
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