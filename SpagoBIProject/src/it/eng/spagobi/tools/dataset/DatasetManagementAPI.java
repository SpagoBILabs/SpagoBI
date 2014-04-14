/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset;

/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/



import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.commons.deserializer.DeserializerFactory;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.tools.dataset.bo.AbstractJDBCDataset;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.cache.CacheManager;
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.FilterCriteria;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.GroupCriteria;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.ProjectionCriteria;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.work.SQLDBCacheWriteWork;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.tools.dataset.crosstab.CrosstabDefinition;
import it.eng.spagobi.tools.dataset.crosstab.Measure;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.exceptions.ParameterDsException;
import it.eng.spagobi.tools.dataset.utils.DataSetUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.json.JSONUtils;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commonj.work.Work;


/** 
 * DataLayer facade class. It manage the access to SpagoBI's datasets. It is built on top of the dao. 
 * It manages all complex operations that involve more than a simple CRUD operations over the dataset. It
 * also manages user's profilation and autorization. Other class must access dataset through this class and
 * not calling directly the DAO.
 * 
 * @author gavardi, gioia
 *
 */

public class DatasetManagementAPI {

	private UserProfile userProfile;
	private IDataSetDAO dataSetDao;

	// XML tags	
	public static final String PARAMETERSLIST = "PARAMETERSLIST"; 
	public static final String ROWS = "ROWS"; 
	public static final String ROW = "ROW"; 
	public static final String NAME = "NAME"; 
	public static final String TYPE = "TYPE"; 
	
	static private Logger logger = Logger.getLogger(DatasetManagementAPI.class);
	
	// ==============================================================================
	// COSTRUCTOR METHODS
	// ==============================================================================
	public DatasetManagementAPI() {
		setUserProfile(null);
	}
	
	public DatasetManagementAPI(UserProfile userProfile) {
		setUserProfile(userProfile);
	}
	
	// ==============================================================================
	// ACCESSOR METHODS
	// ==============================================================================
	public UserProfile getUserProfile() {
		return userProfile;
	}
	
	public String getUserId() {
		return getUserProfile().getUserUniqueIdentifier().toString();
	}


	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
		if(dataSetDao != null) {
			dataSetDao.setUserProfile(userProfile);
		}
	}
	
	private IDataSetDAO getDataSetDAO() {
		if(dataSetDao == null) {
			try {
				dataSetDao = DAOFactory.getDataSetDAO();
				if(getUserProfile() != null) {
					dataSetDao.setUserProfile(userProfile);
				}
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An unexpected error occured while instatiating the DAO", t);
			} 
		}
		return dataSetDao;
	}
	
	// ==============================================================================
	// API METHODS
	// ==============================================================================
	public List<IDataSet> getDataSets() {
		try {
			List<IDataSet> dataSets = null;
			if(UserUtilities.isTechnicalUser(getUserProfile())) {
				dataSets = getDataSetDAO().loadDataSets();
			} else {
				dataSets = getMyDataDataSet();
			}
			return dataSets;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}
	}
	
	public IDataSet getDataSet(String label) {
		
		logger.debug("IN");
		
		try {
			if(StringUtilities.isEmpty(label)) {
				throw new RuntimeException("Invalid value [" + label + "] for input parameter [label]");
			}
			
			IDataSet dataSet = null;
			try {
				dataSet = getDataSetDAO().loadDataSetByLabel(label);
			} catch(Throwable t) {
				throw new RuntimeException("An unexpected error occured while loading dataset [" + label + "]");
			}
			
			if(dataSet == null) {
				throw new RuntimeException("Dataset [" + label + "] does not exist");
			}
			
			if( DataSetUtilities.isExecutableByUser(dataSet, getUserProfile()) == false ) {
				throw new RuntimeException("User [" + getUserId() + "] cannot access to dataset [" + label + "]");
			}
			return dataSet;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method [getDataSet]", t);
		} finally {			
			logger.debug("OUT");
		}
	}
	
	public List<IFieldMetaData> getDataSetFieldsMetadata(String label) {
		try {
			IDataSet dataSet = getDataSetDAO().loadDataSetByLabel(label);
			
			if(dataSet == null) {
				throw new RuntimeException("Impossible to get dataset [" + label + "] from SpagoBI Server");
			}
			
			IMetaData metadata = dataSet.getMetadata();
			if(metadata == null) {
				throw new RuntimeException("Impossible to retrive metadata of dataset [" + metadata + "]");
			}
			
			List<IFieldMetaData> fieldsMetaData = new ArrayList<IFieldMetaData>();
			int fieldCount = metadata.getFieldCount();
			for(int i = 0; i < fieldCount; i++) {
				IFieldMetaData fieldMetaData = metadata.getFieldMeta(i);
				fieldsMetaData.add(fieldMetaData);
			}
			
			return fieldsMetaData;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}
	}
	
	public List<JSONObject> getDataSetParameters(String label) {
		logger.debug("IN");
		try {
			List<JSONObject> parametersList = new ArrayList<JSONObject>();
			IDataSet dataSet = getDataSetDAO().loadDataSetByLabel(label);
			
			if(dataSet == null) {
				throw new RuntimeException("Impossible to get dataset [" + label + "] from SpagoBI Server");
			}
			
			String strParams = dataSet.getParameters();		
			if (strParams==null) return parametersList;

			try {
				SourceBean xmlParams = SourceBean.fromXMLString(strParams);
				SourceBean sbRows = (SourceBean) xmlParams.getAttribute(ROWS);
				List lst = sbRows.getAttributeAsList(ROW);
				for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
					SourceBean sbRow = (SourceBean)iterator.next();
					String namePar=sbRow.getAttribute(NAME)!= null ? sbRow.getAttribute(NAME).toString() : null;
					String typePar=sbRow.getAttribute(TYPE)!= null ? sbRow.getAttribute(TYPE).toString() : null;
				
					if(typePar.startsWith("class")){
						typePar=typePar.substring(6);						
					}
					JSONObject paramMetaDataJSON = new JSONObject();
					String filterId =  "ds__"+ dataSet.getLabel() + "__" + namePar;
					paramMetaDataJSON.put("id", filterId);
					paramMetaDataJSON.put("labelObj", dataSet.getLabel());
					paramMetaDataJSON.put("nameObj", dataSet.getName());
					paramMetaDataJSON.put("typeObj", "Dataset");
					paramMetaDataJSON.put("namePar", namePar);						
					paramMetaDataJSON.put("typePar", typePar);
					parametersList.add(paramMetaDataJSON);
				}				
			} catch(Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to parse parameters [" + strParams + "]", t);
			} finally {
				logger.debug("OUT");
			}	
			
			return parametersList;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}
	}
	
	
	public IDataStore getDataStore(String label, int offset, int fetchSize, int maxResults, String filters) {
		try {
		
			IDataSet dataSet = this.getDataSetDAO().loadDataSetByLabel(label);
			Map reqParams = getParametersMap(filters);
			List dsParams = getDataSetParameters(label);
			if (dsParams.size() >  reqParams.size()){
				String emptyFields = getEmptyFields(reqParams, dsParams);
				throw new ParameterDsException("The following parameters have no value [" + emptyFields + "]");				
			}
			
			ICache cache = CacheManager.getCache();
			IDataStore cachedResultSet = cache.get(dataSet);			
			
			IDataStore dataStore = null;
			if (cachedResultSet == null){
				
				dataSet.setParamsMap(reqParams);
				dataSet.loadData(offset, fetchSize, maxResults);
				dataStore = dataSet.getDataStore();
				
				WorkManager workManager = new WorkManager(getSpagoBIConfigurationProperty("JNDI_THREAD_MANAGER"));
				Work cacheWriteWork = new SQLDBCacheWriteWork(cache, dataStore, dataSet);
				
				workManager.run(cacheWriteWork, null);
			} else {
				dataStore = cachedResultSet;
			}
			
			//dataSet.loadData(offset, fetchSize, maxResults);
			//IDataStore dataStore = dataSet.getDataStore();
			
			return dataStore;
		}catch(ParameterDsException p){
			throw new ParameterDsException(p.getMessage());
		}catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	public IDataStore getAggregatedDataStore(String label, int offset, int fetchSize, int maxResults, CrosstabDefinition crosstabDefinition) {
		try {
			
			IDataSet dataSet = this.getDataSetDAO().loadDataSetByLabel(label);
			
			ICache cache = CacheManager.getCache();
			IDataStore cachedResultSet = cache.get(dataSet);
			IDataStore dataStore = null;
			
			if (cachedResultSet == null){
				//Dataset not yet cached
				dataSet.loadData(offset, fetchSize, maxResults);
				dataStore = dataSet.getDataStore();
				
				cache.put(dataSet, dataStore);

			} 

			//TODO: must be refactorized
			List<ProjectionCriteria> projectionCriteria = this.getProjectionCriteria(crosstabDefinition);
			List<FilterCriteria> filterCriteria = new ArrayList<FilterCriteria>(); // TODO: empty for this time
			List<GroupCriteria> groupCriteria = this.getGroupCriteria(crosstabDefinition);
			dataStore = cache.get(dataSet, groupCriteria, filterCriteria, projectionCriteria);
			
			
			/* since the datastore, at this point, is a JDBC datastore, 
			* it does not contain information about measures/attributes, fields' name and alias...
			* therefore we adjust its metadata
			*/
			this.adjustMetadata((DataStore) dataStore, dataSet,null );
			
			logger.debug("Decoding dataset ...");
			dataSet.decode(dataStore);
			LogMF.debug(logger, "Dataset decoded: {0}", dataStore);
			

			return dataStore;


		}catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	private static Map getParametersMap(String filters){
		Map toReturn = new HashMap();
		filters = JSONUtils.escapeJsonString(filters);
		JSONObject jsonFilters  = ObjectUtils.toJSONObject(filters);	
		Iterator keys = jsonFilters.keys();
		try{
	        while( keys.hasNext() ){
	            String key = (String)keys.next();            
	            String value = jsonFilters.getString(key);
	            toReturn.put(key, value);
	        }
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading spagobi filters [" + filters + "]", t);
		}	
		return toReturn;
	}
	
	private static String getEmptyFields(Map reqParams, List dsParams){
		String toReturn = "";

		for (Iterator iterator = dsParams.iterator(); iterator.hasNext();) {
			JSONObject obj = (JSONObject) iterator.next();
			try{
				String key = obj.getString("namePar");
				if (reqParams.get(key) == null) {			
					toReturn += key;
					if(iterator.hasNext()){
						toReturn += ", ";
					}
				}
			} catch(Throwable t) {
				throw new SpagoBIRuntimeException("An unexpected exception occured while checking spagobi filters ", t);
			}
		}
		return toReturn;
	}
	
	private static String getSpagoBIConfigurationProperty(String propertyName) {
		try {
			String propertyValue = null;
			IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
			Config cacheSpaceCleanableConfig = configDao.loadConfigParametersByLabel(propertyName);
			if ((cacheSpaceCleanableConfig != null) && (cacheSpaceCleanableConfig.isActive())){
				propertyValue = cacheSpaceCleanableConfig.getValueCheck();
			}	
			return propertyValue;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading spagobi property [" + propertyName + "]", t);
		}
	}
	
	public List<IDataSet> getEnterpriseDataSet() {
		try {
			List<IDataSet> dataSets = getDataSetDAO().loadEnterpriseDataSets();
			return dataSets;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	public List<IDataSet> getOwnedDataSet() {
		return getOwnedDataSet(null);
	}
	
	public List<IDataSet> getOwnedDataSet(String userId) {
		try {
			if(userId == null) userId = this.getUserId();
			List<IDataSet> dataSets = getDataSetDAO().loadDataSetsOwnedByUser( userId );
			return dataSets;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
		
	}
	
	public List<IDataSet> getSharedDataSet() {
		return getSharedDataSet(null);
	}
	public List<IDataSet> getSharedDataSet(String userId) {
		try {
			if(userId == null) userId = this.getUserId();
			List<IDataSet> dataSets = getDataSetDAO().loadDatasetsSharedWithUser( userId );
			return dataSets;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	public List<IDataSet> getUncertifiedDataSet() {
		return getUncertifiedDataSet(null);
	}
	public List<IDataSet> getUncertifiedDataSet(String userId) {
		try {
			if(userId == null) userId = this.getUserId();
			List<IDataSet> dataSets = getDataSetDAO().loadDatasetOwnedAndShared( userId );
			return dataSets;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}
	}
	
	public List<IDataSet> getMyDataDataSet() {
		return getMyDataDataSet(null);
	}
	public List<IDataSet> getMyDataDataSet(String userId) {
		try {
			if(userId == null) userId = this.getUserId();
			List<IDataSet> dataSets = getDataSetDAO().loadMyDataDataSets( userId );
			return dataSets;
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
		

	
	
	public  Integer creatDataSet(IDataSet dataSet) {
		logger.debug("IN");
		Integer toReturn = null;
		if (dataSet == null) {
			logger.error("Dataset is null");
			return null;
		}

		try{

			validateDataSet(dataSet);

			// validate
			logger.debug("Getting the data set dao..");
			IDataSetDAO dataSetDao = DAOFactory.getDataSetDAO();
			logger.debug("DatasetDAo loaded");
			logger.debug("Inserting the data set wit the dao...");
			toReturn = dataSetDao.insertDataSet(dataSet);
			logger.debug("Data Set inserted");
			if (toReturn != null) {
				logger.info("DataSet "+dataSet.getLabel()+" saved with id = " + toReturn);
			} else {
				logger.error("DataSet not saved: check error log");
			}

		}
		catch (ValidationException e) {
			logger.error("Failed validation of dataset "+dataSet.getLabel()+" with cause: "+e.getValidationMessage());
			throw new RuntimeException(e.getValidationMessage(), e);
		}
		catch (EMFUserError e) {
			logger.error("EmfUserError ",e);
			throw new RuntimeException("EmfUserError ",e);
		}


		logger.debug("OUT");
		return toReturn;
	}

	  
	private boolean validateDataSet(IDataSet dataSet) throws ValidationException, EMFUserError{
		logger.debug("IN");

		logger.debug("check the dataset not alreaduy present with same label");

		IDataSet datasetLab = DAOFactory.getDataSetDAO().loadDataSetByLabel(dataSet.getLabel());

		if(datasetLab != null){
			throw new ValidationException("Dataset with label "+dataSet.getLabel()+" already found");
		}

		logger.debug("OUT");
		return true;

	}

	public class ValidationException extends Exception{
		private String validationMessage;
		ValidationException(String _validationMessage) {
			super();
			this.validationMessage = _validationMessage;
		}
		
		ValidationException(String _validationMessage, Throwable e) {
			super(e);
			this.validationMessage = _validationMessage;
		}
		public String getValidationMessage(){
			return this.validationMessage;
		}

	}
	
	//------------------------------------------------------------------------------
	// Methods for extracting information from CrosstabDefinition and related
	//------------------------------------------------------------------------------
	
	private List<ProjectionCriteria> getProjectionCriteria(CrosstabDefinition crosstabDefinition){
		logger.debug("IN");	
		List<ProjectionCriteria> projectionCriterias = new ArrayList<ProjectionCriteria>();
		
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
		List<Measure> measures = crosstabDefinition.getMeasures(); 
			
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String columnName = aColumn.getEntityId();
			ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(columnName,null,columnName);
			projectionCriterias.add(aProjectionCriteria);
		}
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String columnName = aRow.getEntityId();
			ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(columnName,null,columnName);
			projectionCriterias.add(aProjectionCriteria);
		}
		
		// appends measures
		Iterator<Measure> measuresIt = measures.iterator();
		while (measuresIt.hasNext()) {
			Measure aMeasure = measuresIt.next();
			IAggregationFunction function = aMeasure.getAggregationFunction();
			String columnName = aMeasure.getEntityId();
			if (columnName == null) {
				// when defining a crosstab inside the SmartFilter document, an additional COUNT field with id QBE_SMARTFILTER_COUNT
				// is automatically added inside query fields, therefore the entity id is not found on base query selected fields
				
				/*
				columnName = "Count";
				if (aMeasure.getEntityId().equals(QBE_SMARTFILTER_COUNT)) {
					toReturn.append(AggregationFunctions.COUNT_FUNCTION.apply("*"));
				} else {
					logger.error("Entity id " + aMeasure.getEntityId() + " not found on the base query!!!!");
					throw new RuntimeException("Entity id " + aMeasure.getEntityId() + " not found on the base query!!!!");
				}
				*/
			} else {
				if (function != AggregationFunctions.NONE_FUNCTION) {
					ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(columnName,function.getName(),columnName);
					projectionCriterias.add(aProjectionCriteria);
				} else {
					ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(columnName,null,columnName);
					projectionCriterias.add(aProjectionCriteria);
				}
			}

		}

		
		logger.debug("OUT");
		return projectionCriterias;
	}
	
	private List<GroupCriteria> getGroupCriteria(CrosstabDefinition crosstabDefinition){
		logger.debug("IN");
		List<GroupCriteria> groupCriterias = new ArrayList<GroupCriteria>();

		
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
			
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String columnName = aColumn.getEntityId();
			GroupCriteria groupCriteria = new GroupCriteria(columnName,null);
			groupCriterias.add(groupCriteria);
		}
		
		
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String columnName = aRow.getEntityId();
			GroupCriteria groupCriteria = new GroupCriteria(columnName,null);
			groupCriterias.add(groupCriteria);
		}
		logger.debug("OUT");
		return groupCriterias;
	}
	
	protected void adjustMetadata(DataStore dataStore,
			IDataSet dataset,
			JSONArray fieldOptions) {

		IMetaData dataStoreMetadata = dataStore.getMetaData();
		IMetaData dataSetMetadata = dataset.getMetadata();
		MetaData newdataStoreMetadata = new MetaData();
		int fieldCount = dataStoreMetadata.getFieldCount();
		for (int i = 0; i < fieldCount; i++) {
			IFieldMetaData dataStoreFieldMetadata = dataStoreMetadata.getFieldMeta(i);
			String columnName = dataStoreFieldMetadata.getName();
			logger.debug("Column name : " + columnName);
			String fieldName = columnName;
			logger.debug("Field name : " + fieldName);
			int index = dataSetMetadata.getFieldIndex(fieldName);
			logger.debug("Field index : " + index);
			IFieldMetaData dataSetFieldMetadata = dataSetMetadata.getFieldMeta(index);
			logger.debug("Field metadata : " + dataSetFieldMetadata);
			FieldMetadata newFieldMetadata = new FieldMetadata();
			String decimalPrecision = (String) dataSetFieldMetadata.getProperty(IFieldMetaData.DECIMALPRECISION);
			if(decimalPrecision!=null){
				newFieldMetadata.setProperty(IFieldMetaData.DECIMALPRECISION,decimalPrecision);
			}
			if(fieldOptions!=null){
				addMeasuresScaleFactor(fieldOptions, dataSetFieldMetadata.getName(), newFieldMetadata);
			}
			newFieldMetadata.setAlias(dataSetFieldMetadata.getAlias());
			newFieldMetadata.setFieldType(dataSetFieldMetadata.getFieldType());
			newFieldMetadata.setName(dataSetFieldMetadata.getName());
			newFieldMetadata.setType(dataStoreFieldMetadata.getType());
			newdataStoreMetadata.addFiedMeta(newFieldMetadata);
		}
		newdataStoreMetadata.setProperties(dataStoreMetadata.getProperties());
		dataStore.setMetaData(newdataStoreMetadata);
	}
	
	public static final String ADDITIONAL_DATA_FIELDS_OPTIONS_OPTIONS = "options";
	public static final String ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR = "measureScaleFactor";
	
	private void addMeasuresScaleFactor(JSONArray fieldOptions, String fieldId,
			FieldMetadata newFieldMetadata) {
		if (fieldOptions != null) {
			for (int i = 0; i < fieldOptions.length(); i++) {
				try {
					JSONObject afield = fieldOptions.getJSONObject(i);
					JSONObject aFieldOptions = afield.getJSONObject(ADDITIONAL_DATA_FIELDS_OPTIONS_OPTIONS);
					String afieldId = afield.getString("id");
					String scaleFactor = aFieldOptions.optString(ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR);
					if (afieldId.equals(fieldId) && scaleFactor != null) {
						newFieldMetadata.setProperty(
								ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR,
								scaleFactor);
						return;
					}
				} catch (Exception e) {
					throw new RuntimeException(
							"An unpredicted error occurred while adding measures scale factor",
							e);
				}
			}
		}
	}

	
	
}
