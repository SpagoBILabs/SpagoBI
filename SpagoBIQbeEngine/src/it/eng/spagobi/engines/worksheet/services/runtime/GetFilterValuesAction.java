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
package it.eng.spagobi.engines.worksheet.services.runtime;

import it.eng.qbe.query.WhereField;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.services.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.worksheet.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
				
		IDataStore dataStore = null;
		JSONObject gridDataFeed = null;
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("WorksheetEngine.getFilterValuesAction.totalTime");

			WorksheetEngineInstance engineInstance = getEngineInstance();
			Assert.assertNotNull(engineInstance, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			// persist dataset into temporary table	
			IDataSetTableDescriptor descriptor = this.persistDataSet();
			
			IDataSet dataset = engineInstance.getDataSet();
			// build SQL query against temporary table
			List<WhereField> whereFields = new ArrayList<WhereField>();
			if (!dataset.hasBehaviour(FilteringBehaviour.ID)) {
				Map<String, List<String>> globalFilters = getGlobalFiltersOnDomainValues();
				List<WhereField> temp = transformIntoWhereClauses(globalFilters);
				whereFields.addAll(temp);
			}
			String sheetName = this.getAttributeAsString(SHEET);
			Map<String, List<String>> sheetFilters = getSheetFiltersOnDomainValues(sheetName);
			List<WhereField> temp = transformIntoWhereClauses(sheetFilters);
			whereFields.addAll(temp);
			
			String fieldName = getAttributeAsString(FIELD_NAME);
			logger.debug("Parameter [" + FIELD_NAME + "] is equals to [" + fieldName + "]");
			
			String worksheetQuery = this.buildSqlStatement(fieldName, descriptor, whereFields);
			// execute SQL query against temporary table
			logger.debug("Executing query on temporary table : " + worksheetQuery);
			dataStore = this.executeWorksheetQuery(worksheetQuery, null, null);
			logger.debug("Query on temporary table executed successfully; datastore obtained:");
			logger.debug(dataStore);
			logger.debug("Decoding dataset ...");
			dataStore = dataset.decode(dataStore);
			logger.debug("Dataset decoded:");
			logger.debug(dataStore);
			
			JSONDataWriter dataSetWriter = new JSONDataWriter();
			gridDataFeed = (JSONObject) dataSetWriter.write(dataStore);
			
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

	protected String buildSqlStatement(String fieldName, IDataSetTableDescriptor descriptor, List<WhereField> filters) {
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
		return CrosstabQueryCreator.getTableQuery(fieldNames, true, descriptor, filters);
	}
	
}
