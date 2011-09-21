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
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.services.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.worksheet.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 			Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */

public class ExecuteWorksheetQueryAction extends AbstractWorksheetEngineAction {
	
	private static final long serialVersionUID = -9134072368475124558L;
	
	// INPUT PARAMETERS
	public static final String LIMIT = "limit";
	public static final String START = "start";
	public static final String OPTIONAL_VISIBLE_COLUMNS = QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS;
	public static final String OPTIONAL_FILTERS = QbeEngineStaticVariables.OPTIONAL_FILTERS;
	public static final String SHEET = LoadWorksheetCrosstabAction.SHEET;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(LoadCrosstabAction.class);
	
	public void service(SourceBean request, SourceBean response)  {				
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
		
		JSONObject gridDataFeed = null;
		IDataStore dataStore = null;
		JSONArray jsonVisibleSelectFields = null;
		
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("WorksheetEngine.executeWorksheetQueryAction.totalTime");
			
			jsonVisibleSelectFields = getAttributeAsJSONArray( OPTIONAL_VISIBLE_COLUMNS );
			logger.debug("jsonVisibleSelectFields input: " + jsonVisibleSelectFields);
			Assert.assertTrue(jsonVisibleSelectFields != null && jsonVisibleSelectFields.length() > 0, "jsonVisibleSelectFields input not valid");
			
			dataStore = executeQuery(jsonVisibleSelectFields);
			
			gridDataFeed = serializeDataStore(dataStore);
			
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
	
	public JSONObject serializeDataStore(IDataStore dataStore) {
		JSONDataWriter dataSetWriter = new JSONDataWriter();
		JSONObject gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
		return gridDataFeed;
	}
	
//	protected JSONObject loadSmartFilterFormValues() throws JSONException{
//		FormState formState = getEngineInstance().getFormState();
//		if (formState == null) {
//			return null;
//		} else {
//			return formState.getFormStateValues();
//		}
//	}
	
	protected IDataStore executeQuery(JSONArray jsonVisibleSelectFields) throws Exception {
		
		IDataStore dataStore = null;
		JSONObject jsonFormState = null;
		
		Integer limit;
		Integer start;
		
		start = getAttributeAsInteger( START );	
		logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
		
		limit = getAttributeAsInteger( LIMIT );
		logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
		
		Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of WorksheetEngineInstance class");
		
//		jsonFormState = loadSmartFilterFormValues();
//		logger.debug("Form state retrieved as a string: " + jsonFormState);
//		//build the query filtered for the smart filter
//		if (jsonFormState != null) {
//			query = getFilteredQuery(query, jsonFormState);
//		}
		
		JSONObject optionalUserFilters = getAttributeAsJSONObject( OPTIONAL_FILTERS );
		List<String> aliases = new ArrayList<String>();
		List<Attribute> onTableAttributes = new ArrayList<Attribute>();
		for (int i = 0; i < jsonVisibleSelectFields.length(); i++) {
			JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(i);
			Attribute attribute = (Attribute) SerializationManager.deserialize(jsonVisibleSelectField, "application/json", Attribute.class);
			aliases.add(attribute.getAlias());
			onTableAttributes.add(attribute);
		}	
		List<WhereField> whereFields = new ArrayList<WhereField>();
		String sheetName = this.getAttributeAsString( SHEET );
		whereFields.addAll(LoadWorksheetCrosstabAction.transformIntoWhereClauses(onTableAttributes));
//		whereFields.addAll(LoadWorksheetCrosstabAction.getMandatoryFilters(this.getEngineInstance(), sheetName));
		whereFields.addAll(LoadWorksheetCrosstabAction.getOptionalFilters(optionalUserFilters));
		
		// get temporary table name
		String tableName = this.getTemporaryTableName();
		// TODO set into dataset the global filters and selected fields
		// persist dataset into temporary table	
		IDataSetTableDescriptor descriptor = this.persistDataSet(tableName);
		// build SQL query against temporary table
		// TODO get sheet filters (optional filters)
		String worksheetQuery = this.buildSqlStatement(aliases, descriptor, new ArrayList<WhereField>());
		// execute SQL query against temporary table
		dataStore = this.executeWorksheetQuery(worksheetQuery, start, limit);
		
		// at this moment, the store has "col_0_..." (or something like that) as column aliases: we must put the right aliases 
		IMetaData dataStoreMetadata = dataStore.getMetaData();
		for (int i = 0; i < jsonVisibleSelectFields.length(); i++) {
			JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(i);
			dataStoreMetadata.changeFieldAlias(i, jsonVisibleSelectField.getString("alias"));
		}
		
		return dataStore;
	}

	private String buildSqlStatement(List<String> aliases,
			IDataSetTableDescriptor descriptor, ArrayList<WhereField> filters) {
		return CrosstabQueryCreator.getTableQuery(aliases, descriptor, filters);	
	}
	
}
