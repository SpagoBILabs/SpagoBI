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
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
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
			
			String sheetName = this.getAttributeAsString( SHEET );
			logger.debug("Parameter [" + SHEET + "] is equals to [" + sheetName + "]");
			String fieldName = getAttributeAsString( FIELD_NAME );
			logger.debug("Parameter [" + FIELD_NAME + "] is equals to [" + fieldName + "]");
			
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
			Map<String, List<String>> sheetFilters = getSheetFiltersOnDomainValues(sheetName);
			List<WhereField> temp = transformIntoWhereClauses(sheetFilters);
			whereFields.addAll(temp);
			
			String worksheetQuery = this.buildSqlStatement(fieldName, descriptor, whereFields);
			// execute SQL query against temporary table
			logger.debug("Executing query on temporary table : " + worksheetQuery);
			dataStore = this.executeWorksheetQuery(worksheetQuery, null, null);
			logger.debug("Query on temporary table executed successfully; datastore obtained:");
			logger.debug(dataStore);
			DataStore clone = this.clone(dataStore);
			logger.debug("Decoding dataset ...");
			dataStore = dataset.decode(dataStore);
			logger.debug("Dataset decoded:");
			logger.debug(dataStore);
			
			IMetaData metadata = dataStore.getMetaData();
			IFieldMetaData fieldMetadata = metadata.getFieldMeta(0);
			IMetaData newMetadata = new MetaData();
			newMetadata.addFiedMeta(fieldMetadata);
			newMetadata.addFiedMeta(new FieldMetadata(fieldMetadata.getName() + "_description", fieldMetadata.getType()));
			clone.setMetaData(newMetadata);
			long count = clone.getRecordsCount();
			for (long i = 0; i < count; i++) {
				IRecord record = clone.getRecordAt((int) i);
				Object value = dataStore.getRecordAt((int) i).getFieldAt(0);
				record.appendField(new Field(value.toString() + " description"));
			}
			
			JSONDataWriter dataSetWriter = new JSONDataWriter();
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

	private DataStore clone(IDataStore dataStore) {
		DataStore toReturn = new DataStore();
		IMetaData metadata = dataStore.getMetaData();
		toReturn.setMetaData(metadata);
		long count = dataStore.getRecordsCount();
		for (long i = 0; i < count; i++) {
			IRecord record = dataStore.getRecordAt((int) i);
			Object value = record.getFieldAt(0);
			IRecord newRecord = new Record();
			newRecord.appendField(new Field(value));
			toReturn.appendRecord(newRecord);
		}
		return toReturn;
	}

//	private IDataStore createDataStoreFromValues(Attribute sheetFilter) throws Exception {
//		String values = sheetFilter.getValues();
//		DataStore datastore = new DataStore();
//		IMetaData metadata = new MetaData();
//		IFieldMetaData fieldMetadata = new FieldMetadata(sheetFilter.getEntityId(), String.class); // TODO String.class????
//		metadata.addFiedMeta(fieldMetadata);
//		datastore.setMetaData(metadata);
//		JSONArray array = new JSONArray(values);
//		for (int i = 0; i < array.length(); i++) {
//			Object aValue = array.get(i);
//			IRecord record = new Record();
//			record.appendField(new Field(aValue));
//			datastore.appendRecord(record);
//		}
//		IDataSet dataset = this.getEngineInstance().getDataSet();
//		IDataStore datastoreDecoded = dataset.decode(datastore);
//		
//		DataStore toReturn = new DataStore();
//		IMetaData toReturnMetadata = new MetaData();
//		IFieldMetaData descriptionMetadata = new FieldMetadata(fieldMetadata.getName() + "_description", fieldMetadata.getType());
//		toReturnMetadata.addFiedMeta(fieldMetadata);
//		toReturnMetadata.addFiedMeta(descriptionMetadata);
//		toReturn.setMetaData(toReturnMetadata);
//		for (int i = 0; i < array.length(); i++) {
//			Object aValue = array.get(i);
//			IRecord record = new Record();
//			record.appendField(new Field(aValue));
//			Object description = datastoreDecoded.getRecordAt(i).getFieldAt(0);
//			record.appendField(new Field(description));
//			toReturn.appendRecord(record);
//		}
//		return toReturn;
//	}

//	private Attribute getSheetFilterOnDomainValues(String fieldName, String sheetName) {
//		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) getEngineInstance().getAnalysisState();
//		Sheet aSheet = workSheetDefinition.getSheet(sheetName);
//		List<Attribute> sheetFilters = aSheet.getFiltersOnDomainValues();
//		Attribute toReturn = null;
//		Iterator<Attribute> it = sheetFilters.iterator();
//		while (it.hasNext()) {
//			Attribute attribute = it.next();
//			if (attribute.getEntityId().equals(fieldName)) {
//				toReturn = attribute;
//				break;
//			}
//		}
//		return toReturn;
//	}

//	private IDataStore addDescriptionColumn(IDataStore dataStore) {
//		IMetaData metadata = dataStore.getMetaData();
//		IFieldMetaData field = metadata.getFieldMeta(0);
//		IFieldMetaData newFieldMetadata = new FieldMetadata(field.getName() + "_description", field.getType());
//		newFieldMetadata.setAlias(field.getAlias() + "_description");
//		metadata.addFiedMeta(newFieldMetadata);
//		Iterator records = dataStore.iterator();
//		while (records.hasNext()) {
//			IRecord record = (IRecord) records.next();
//			IField newField = new Field();
//			IField existingField = record.getFieldAt(0);
//			newField.setValue(existingField.getValue());
//			record.appendField(newField);
//		}
//		return dataStore;
//	}

	protected String buildSqlStatement(String fieldName, IDataSetTableDescriptor descriptor, List<WhereField> filters) {
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
		return CrosstabQueryCreator.getTableQuery(fieldNames, true, descriptor, filters);
	}
	
}
