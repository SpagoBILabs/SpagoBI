/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
