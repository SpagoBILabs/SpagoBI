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
package it.eng.spagobi.engines.georeport.services;

import javax.servlet.RequestDispatcher;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.georeport.GeoReportEngine;
import it.eng.spagobi.engines.georeport.GeoReportEngineInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;


/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class GeoReportEngineStartAction extends AbstractEngineStartServlet {
	
	private static final String ENGINE_NAME = "GeoReportEngine";
	
	private static final String REQUEST_DISPATCHER_URL = "/WEB-INF/jsp/geoReport.jsp";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GeoReportEngineStartAction.class);
    
	
	public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
		
		GeoReportEngineInstance engineInstance;
		IDataSource dataSource;
        IDataSet dataSet;
		RequestDispatcher requestDispatcher;
		
         
        logger.debug("IN");
        
        try {
        	// log some contextual infos
        	logger.debug("User: [" + servletIOManager.getUserId() + "]");
        	logger.debug("Document: [" + servletIOManager.getDocumentId() + "]");
        	
        	dataSource = servletIOManager.getDataSource();
        	logger.debug("Datasource: [" + (dataSource == null? dataSource: dataSource.getLabel()) + "]");
        	 
        	dataSet = servletIOManager.getDataSet();
        	logger.debug("Dataset: [" + (dataSet == null? dataSource: dataSet.getName()) + "]");
        	
        	// create a new engine instance
        	engineInstance = GeoReportEngine.createInstance(
        			servletIOManager.getTemplateAsString(), 
        			servletIOManager.getEnv()
        	);
        	
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

}
