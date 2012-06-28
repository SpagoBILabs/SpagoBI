/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport.services;

import java.io.File;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.jasperreport.JasperReportEngine;
import it.eng.spagobi.engines.jasperreport.JasperReportEngineInstance;
import it.eng.spagobi.engines.jasperreport.JasperReportEngineTemplate;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

/**
 * @authors
 * Andrea Gioia (andrea.gioia@eng.it)
 * Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class JasperReportEngineStartAction extends AbstractEngineStartServlet {
	
	private static String CONNECTION_NAME="connectionName";
	private static String OUTPUT_TYPE = "outputType";
	
	    
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(JasperReportEngineStartAction.class);
    
	
	public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
		
		IDataSource dataSource;
        IDataSet dataSet;
        String connectionName;
        String outputType;
        
        JasperReportEngineTemplate template;
        JasperReportEngineInstance engineInstance;
	
        logger.debug("IN");
        
        try {
        	// log some contextual infos
        	logger.debug("User: [" + servletIOManager.getUserId() + "]");
        	logger.debug("Document: [" + servletIOManager.getDocumentId() + "]");
        	
        	dataSource = servletIOManager.getDataSource();
        	logger.debug("Datasource: [" + (dataSource == null? dataSource: dataSource.getLabel()) + "]");
        	if (dataSource==null){
        		logger.warn("This document doesn't have the Data Source");
        	}
        	 
        	dataSet = servletIOManager.getDataSet();
        	logger.debug("Dataset: [" + (dataSet == null? dataSource: dataSet.getName()) + "]");
        	
        	// read and log builtin parameters
        	connectionName = servletIOManager.getParameterAsString(CONNECTION_NAME);
        	logger.debug("Parameter [" + CONNECTION_NAME + "] is equal to [" + connectionName + "]");
        	
        	outputType = servletIOManager.getParameterAsString(OUTPUT_TYPE);
        	logger.debug("Parameter [" + OUTPUT_TYPE + "] is equal to [" + outputType + "]");
        	if(outputType == null) {
        		outputType = JasperReportEngine.getConfig().getDefaultOutputType();
        		servletIOManager.getEnv().put(OUTPUT_TYPE, outputType);
        		logger.debug("Parameter [" + OUTPUT_TYPE + "] has been set to the default value [" + servletIOManager.getEnv().get(OUTPUT_TYPE) + "]");
        	}
        	
        	// this proxy is used by ScriptletChart to execute and embed external chart into report
        	servletIOManager.getEnv().put(EngineConstants.ENV_DOCUMENT_EXECUTE_SERVICE_PROXY, servletIOManager.getDocumentExecuteServiceProxy());
        	
        	servletIOManager.auditServiceStartEvent();
        	
        	
        	template = new JasperReportEngineTemplate(servletIOManager.getTemplateName(), servletIOManager.getTemplate());
        	
        	
        	File reportOutputDir = JasperReportEngine.getConfig().getReportOutputDir();
        	File reportFile = File.createTempFile("report", "." + outputType, reportOutputDir);
        	DataSetServiceProxy proxyDataset = servletIOManager.getDataSetServiceProxy();
        	
        	engineInstance = JasperReportEngine.createInstance( template, servletIOManager.getEnv() , proxyDataset);
        	engineInstance.setId(servletIOManager.getParameterAsString("SBI_EXECUTION_ID"));
        	servletIOManager.getHttpSession().setAttribute(engineInstance.getId(), engineInstance);
        	
        	
        	
        	engineInstance.runReport(reportFile, servletIOManager.getRequest());
        	
        	   
        	servletIOManager.writeBackToClient(200, reportFile, true, "report." + outputType, JasperReportEngine.getConfig().getMIMEType(outputType));
        	
        	// instant cleaning
        	reportFile.delete();

        	servletIOManager.auditServiceEndEvent();
        } catch(Throwable t) {
        	throw new SpagoBIEngineException("An error occurred while executing report. Check log file for more information", t);
        } finally {
        	logger.debug("OUT");        	 
        }        

	}

}
