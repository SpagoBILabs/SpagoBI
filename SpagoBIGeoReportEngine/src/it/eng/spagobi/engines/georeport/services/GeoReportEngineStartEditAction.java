/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.georeport.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.eng.spagobi.engines.georeport.GeoReportEngine;
import it.eng.spagobi.engines.georeport.GeoReportEngineInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import javax.servlet.RequestDispatcher;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @authors Andrea Gioia (andrea.gioia@eng.it)
 */
public class GeoReportEngineStartEditAction extends AbstractEngineStartServlet {

	private static final long serialVersionUID = 1L;

	private static final String ENGINE_NAME = "GeoReportEngine";
	
	private static final String REQUEST_DISPATCHER_URL = "/WEB-INF/jsp/geoReport.jsp";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GeoReportEngineStartEditAction.class);
    
	
	public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
		
		GeoReportEngineInstance engineInstance;
		IDataSet dataSet;
		RequestDispatcher requestDispatcher;
		
         
        logger.debug("IN");
        
        try {
        	dataSet = servletIOManager.getDataSet();
        	String datasetLabel = (String)servletIOManager.getParameter("dataset_label");
        	dataSet = servletIOManager.getDataSetServiceProxy().getDataSetByLabel(datasetLabel);
        	
        	JSONObject template = buildTemplate(dataSet);
        	
        	// create a new engine instance
        	engineInstance = GeoReportEngine.createInstance(
        			template.toString(), // servletIOManager.getTemplateAsString(), 
        			servletIOManager.getEnv()
        	);
        	
        	engineInstance.getEnv().put(EngineConstants.ENV_DATASET, dataSet);
        	
        	servletIOManager.getHttpSession().setAttribute(EngineConstants.ENGINE_INSTANCE, engineInstance);
        	
        	// dispatch the request to the presentation layer
        	requestDispatcher = getServletContext().getRequestDispatcher( REQUEST_DISPATCHER_URL );
            try {
            	requestDispatcher.forward(servletIOManager.getRequest(), servletIOManager.getResponse());
    		} catch (Throwable t) {
    			throw new SpagoBIServiceException(ENGINE_NAME, "An error occurred while dispatching request to [" + REQUEST_DISPATCHER_URL + "]", t);
    		} 
        } catch(Throwable t) {
        	logger.error("Impossible to execute document", t);
        	t.printStackTrace();
        	throw new SpagoBIServiceException(ENGINE_NAME, t);
        } finally {
        	logger.debug("OUT");        	 
        }        

	}

	private JSONObject buildTemplate(IDataSet dataSet) {
		JSONObject template;
		
		logger.debug("IN");
		
		template = new JSONObject();
		try {
			template.put("mapName", "Sud Tirol");
			template.put("analysisType", "choropleth");
			template.put("analysisConf", buildAnalysisConf(dataSet));
			template.put("feautreInfo", buildFeatureInfo(dataSet));
			template.put("indicators", buildIndicators(dataSet));
			template.put("businessId", "comune_ita");
			template.put("geoId", "NAME_3");
			template.put("selectedBaseLayer", "GoogleMap");
			template.put("targetLayerConf", buildTargetLayerConf(dataSet));
			template.put("controlPanelConf", buildControlPanelConf(dataSet));
			template.put("toolbarConf", buildToolbarConf(dataSet));
			template.put("role", "spagobi/admin");
			template.put("lon", "11.400");
			template.put("lat", "46.650");
			template.put("zoomLevel", "9");
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while executing building template",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return template;
	}
	
	/**
	 * @param dataSet
	 * @return
	 */
	private JSONObject buildToolbarConf(IDataSet dataSet) {
		JSONObject toolbarConf;
		
		logger.debug("IN");
		
		toolbarConf = new JSONObject();
		try {
			toolbarConf.put("enabled", true);
			toolbarConf.put("zoomToMaxButtonEnabled", true);
			
			toolbarConf.put("mouseButtonGroupEnabled", true);
			toolbarConf.put("measureButtonGroupEnabled", false);
			toolbarConf.put("wmsGroupEnabled", true);
			toolbarConf.put("drawButtonGroupEnabled", false);
			toolbarConf.put("historyButtonGroupEnabled", false);
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while building toolbar conf block",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return toolbarConf;
	}
	
	/**
	 * @param dataSet
	 * @return
	 */
	private JSONObject buildControlPanelConf(IDataSet dataSet) {
		JSONObject controlPanelConf;
		
		logger.debug("IN");
		
		controlPanelConf = new JSONObject();
		try {
			controlPanelConf.put("layerPanelEnabled", true);
			
			JSONObject layerPanelConf = new JSONObject();
			layerPanelConf.put("collapsed", true);
			controlPanelConf.put("layerPanelConf", layerPanelConf);
			controlPanelConf.put("analysisPanelEnabled", true);
			controlPanelConf.put("measurePanelEnabled", false);
			controlPanelConf.put("legendPanelEnabled", true);
			controlPanelConf.put("logoPanelEnabled", false);
			controlPanelConf.put("earthPanelEnabled", false);
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while building control panel conf block",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return controlPanelConf;
	}	

	/**
	 * @param dataSet
	 * @return
	 */
	private JSONObject buildTargetLayerConf(IDataSet dataSet) {
		JSONObject targetLayerConf;
		
		logger.debug("IN");
		
		targetLayerConf = new JSONObject();
		try {
			targetLayerConf.put("text", "Comuni");
			targetLayerConf.put("name", "gadm_ita_comuni");
			targetLayerConf.put("data", "comuni_sudtirol.json");
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while building target layer conf block",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return targetLayerConf;
	}


	/**
	 * @param dataSet
	 * @return
	 */
	private JSONArray buildIndicators(IDataSet dataSet) {
		JSONArray indicators;
		
		logger.debug("IN");
		
		indicators = new JSONArray();
		try {
			
			List<IFieldMetaData> fields;
			fields = new ArrayList<IFieldMetaData>();
			for(int i = 0; i < dataSet.getMetadata().getFieldCount(); i++) {
				IFieldMetaData fieldMeta = dataSet.getMetadata().getFieldMeta(i);
				if(fieldMeta.getFieldType() ==  FieldType.MEASURE) {
					fields.add(fieldMeta);
				}
			}
			
			JSONArray info;
			for(IFieldMetaData field : fields) {
				info = new JSONArray();
				info.put(field.getName());
				info.put( field.getAlias() != null? field.getAlias(): field.getName() );
				indicators.put(info);
			}
			
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while building indicators block",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return indicators;
	}

	/**
	 * @param dataSet
	 * @return
	 */
	private JSONArray buildFeatureInfo(IDataSet dataSet) {
		JSONArray featureInfo;
		
		logger.debug("IN");
				
		featureInfo = new JSONArray();
		try {
			List<IFieldMetaData> fields;
			fields = new ArrayList<IFieldMetaData>();
			for(int i = 0; i < dataSet.getMetadata().getFieldCount(); i++) {
				IFieldMetaData fieldMeta = dataSet.getMetadata().getFieldMeta(i);
				if(fieldMeta.getFieldType() ==  FieldType.ATTRIBUTE) {
					fields.add(fieldMeta);
				}
			}
			
			JSONArray info;
			for(IFieldMetaData field : fields) {
				info = new JSONArray();
			
				info.put( field.getAlias() != null? field.getAlias(): field.getName() );
				info.put(field.getName().toUpperCase());
				featureInfo.put(info);
			}
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while building feature info block",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return featureInfo;
	}

	/**
	 * @param dataSet
	 * @return
	 */
	private JSONObject buildAnalysisConf(IDataSet dataSet) {
		JSONObject analysisConf;
		
		logger.debug("IN");
		
		analysisConf = new JSONObject();
		try {
			analysisConf.put("type", "choropleth");
			analysisConf.put("indicator", "arrivi_totale_2012");
			analysisConf.put("method", "CLASSIFY_BY_EQUAL_INTERVALS"); // "CLASSIFY_BY_QUANTILS"
			analysisConf.put("classes", "7");
			analysisConf.put("fromColor", "#FFFF00");
			analysisConf.put("toColor", "#008000");
		} catch (Throwable t) {
			throw new RuntimeException(
					"An unexpected error occured while building analysis conf block",
					t);
		} finally {
			logger.debug("OUT");
		}
		
		return analysisConf;
	}
}
