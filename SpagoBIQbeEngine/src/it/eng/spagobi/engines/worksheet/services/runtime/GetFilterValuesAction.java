/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.worksheet.services.runtime;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.worksheet.services.AbstractWorksheetEngineAction;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class GetFilterValuesAction extends AbstractWorksheetEngineAction {	
	
	private static final long serialVersionUID = 118095916184707515L;
	
	// INPUT PARAMETERS
	public static final String FIELD_NAME = "fieldName";
	public static final String SHEET = "sheetName";

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(GetFilterValuesAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		JSONObject gridDataFeed = null;
		Monitor errorHitsMonitor = null;
		Monitor totalTimeMonitor = null;
		
		it.eng.spagobi.tools.dataset.common.datastore.IDataStore clone;
		
		totalTimeMonitor = MonitorFactory.start("WorksheetEngine.getFilterValuesAction.totalTime");

		logger.debug("IN");
		
		try {
		
			super.service(request, response);	

			String sheetName = this.getAttributeAsString( SHEET );
			logger.debug("Parameter [" + SHEET + "] is equals to [" + sheetName + "]");
			String fieldName = getAttributeAsString( FIELD_NAME );
			logger.debug("Parameter [" + FIELD_NAME + "] is equals to [" + fieldName + "]");

			JSONDataWriter dataSetWriter = new JSONDataWriter();
			
			clone = getUserSheetFilterValues(sheetName, fieldName);
			gridDataFeed = (JSONObject) dataSetWriter.write(clone);
			
			try {
				writeBackToClient( new JSONSuccess(gridDataFeed) );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch(Throwable t) {
			errorHitsMonitor = MonitorFactory.start("WorksheetEngine.errorHits");
			errorHitsMonitor.stop();
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			if (totalTimeMonitor != null) totalTimeMonitor.stop();
			logger.debug("OUT");
		}	
	}
}
