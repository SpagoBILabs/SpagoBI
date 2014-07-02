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



import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.commons.deserializer.DeserializerFactory;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.tools.dataset.bo.AbstractJDBCDataset;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.VersionedDataSet;
import it.eng.spagobi.tools.dataset.cache.SpagoBICacheManager;
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.JoinedDataSet;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.FilterCriteria;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.GroupCriteria;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.Operand;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.ProjectionCriteria;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.work.SQLDBCacheWriteWork;
import it.eng.spagobi.tools.dataset.common.association.Association;
import it.eng.spagobi.tools.dataset.common.association.AssociationGroup;
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
import it.eng.spagobi.tools.dataset.exceptions.ParametersNotValorizedException;
import it.eng.spagobi.tools.dataset.utils.DataSetUtilities;
import it.eng.spagobi.tools.dataset.utils.datamart.SpagoBICoreDatamartRetriever;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.json.JSONUtils;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

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
				for(IDataSet dataSet : dataSets) {
					checkQbeDataset(dataSet);
				}
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
	
	private void checkQbeDataset(IDataSet dataSet) {
		
		IDataSet ds = null;
		if (dataSet instanceof VersionedDataSet) {
			VersionedDataSet versionedDataSet = (VersionedDataSet)dataSet;
			ds = versionedDataSet.getWrappedDataset();
		} else {
			ds = dataSet;
		}
		
		
		if (ds instanceof QbeDataSet) {
			SpagoBICoreDatamartRetriever retriever = new SpagoBICoreDatamartRetriever();
			Map parameters = ds.getParamsMap();
			if (parameters == null) {
				parameters = new HashMap();
				ds.setParamsMap(parameters);
			}
			ds.getParamsMap().put(SpagoBIConstants.DATAMART_RETRIEVER, retriever);
		}
	}
	
	/**
	 * 
	 * @param label
	 * @param offset
	 * @param fetchSize
	 * @param maxResults
	 * @param parametersValues
	 * @return
	 */
	public IDataStore getDataStore(String label, 
			int offset, int fetchSize, int maxResults, Map<String, String> parametersValues) {
		try {
			IDataSet dataSet = this.getDataSetDAO().loadDataSetByLabel(label);
			checkQbeDataset( dataSet);
			
			List<JSONObject> parameters = getDataSetParameters(label);
			if (parameters.size() >  parametersValues.size()){
				String parameterNotValorizedStr = getParametersNotValorized(parameters, parametersValues);
				throw new ParametersNotValorizedException("The following parameters have no value [" + parameterNotValorizedStr + "]");				
			}
			
			ICache cache = SpagoBICacheManager.getCache();
			IDataStore cachedResultSet = cache.get(dataSet);			
			
			IDataStore dataStore = null;
			if (cachedResultSet == null){
				dataSet.setParamsMap(parametersValues);
				dataStore = cache.refresh(dataSet, false);
			} else {
				dataStore = cachedResultSet;
			}
			return dataStore;
		} catch(ParametersNotValorizedException p){
			throw new ParametersNotValorizedException(p.getMessage());
		}catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	public IDataStore getDataStore(String label, 
			int offset, int fetchSize, int maxResults, Map<String, String> parametersValues,
			List<GroupCriteria> groupCriteria, List<FilterCriteria> filterCriteria, List<ProjectionCriteria> projectionCriteria) {
		
		try {
			
			IDataSet dataSet = this.getDataSetDAO().loadDataSetByLabel(label);
			checkQbeDataset(dataSet);
			
			ICache cache = SpagoBICacheManager.getCache();
			IDataStore dataStore = null;
			
			if(cache.contains(dataSet) == false){
				dataSet.loadData();
				IDataStore baseDataStore = dataSet.getDataStore();
				cache.put(dataSet, baseDataStore);
			} 

			// just get without storing...ok?
			dataStore = cache.get(dataSet, groupCriteria, filterCriteria, projectionCriteria);
			
			
			/* since the datastore, at this point, is a JDBC datastore, 
			* it does not contain information about measures/attributes, fields' name and alias...
			* therefore we adjust its metadata
			*/
			this.adjustMetadata((DataStore) dataStore, dataSet, null);
			dataSet.decode(dataStore);
			
			return dataStore;

		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	/**
	 * @param associationGroupJSON
	 * @param selectionsJSON
	 * @return
	 */
	public IDataStore getJoinedDataStore(AssociationGroup associationGroup, JSONObject selections, Map<String, String> parametersValues) {
		
		IDataStore joinedDataStore = null;
		
		logger.debug("IN");
		
		try {
			JoinedDataSet joinedDataSet = new JoinedDataSet("theLabel", "theLabel", "theLabel", associationGroup);
			joinedDataSet.setParamsMap(parametersValues);
			
			ICache cache = SpagoBICacheManager.getCache();
			if (cache.contains(joinedDataSet) == false) {
				cache.refresh(joinedDataSet, true);
			} 
			
			List<FilterCriteria> filters = new ArrayList<FilterCriteria>();
			Iterator<String> it = selections.keys();
			while(it.hasNext()) {
				String associationName = it.next();
				JSONArray values = selections.getJSONArray(associationName);
				if(values.length() == 0) continue;
				List<String> valuesList = new ArrayList<String>(); 
				for(int i = 0; i < values.length(); i++) {
					valuesList.add(values.get(i).toString());
				}
				
				String datasetColumn = null;
				String datasetLabel = null;
				Association association = associationGroup.getAssociation(associationName);
				if(association != null) {
					datasetColumn = association.getFields().get(0).getFieldName();
					datasetLabel = association.getFields().get(0).getDataSetLabel();
				} else {
					datasetColumn = associationName.split("\\.")[1];
					datasetLabel =  associationName.split("\\.")[0];
				}
				
				
				Operand leftOperand = new Operand(datasetLabel, datasetColumn);
				Operand rightOperand = new Operand(valuesList);
				FilterCriteria filterCriteria = new FilterCriteria(leftOperand, "IN", rightOperand);
				filters.add(filterCriteria);
			}
			
			joinedDataStore = cache.get(joinedDataSet, null, filters, null);
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
		
		
		return joinedDataStore;
	}
	
	/**
	 * @param associationGroupObject
	 * @param selectionsJSON
	 * @param parametersMap
	 * @param groupCriterias
	 * @param object
	 * @param projectionCriterias
	 * @return
	 */
	public IDataStore getJoinedDataStore(AssociationGroup associationGroup, 
			JSONObject selections, 
			Map<String, String> parametersValues,
			List<GroupCriteria> groupCriteria, 
			List<FilterCriteria> filterCriteria,
			List<ProjectionCriteria> projectionCriteria) {

		IDataStore joinedDataStore = null;
		
		logger.debug("IN");
		
		try {
			JoinedDataSet joinedDataSet = new JoinedDataSet("theLabel", "theLabel", "theLabel", associationGroup);
			joinedDataSet.setParamsMap(parametersValues);
			
			ICache cache = SpagoBICacheManager.getCache();
			if (cache.contains(joinedDataSet) == false) {
				cache.refresh(joinedDataSet, true);
			} 
			
			List<FilterCriteria> filters = (filterCriteria != null)? filterCriteria: new ArrayList<FilterCriteria>();
			Iterator<String> it = selections.keys();
			while(it.hasNext()) {
				String associationName = it.next();
				JSONArray values = selections.getJSONArray(associationName);
				if(values.length() == 0) continue;
				List<String> valuesList = new ArrayList<String>(); 
				for(int i = 0; i < values.length(); i++) {
					valuesList.add(values.get(i).toString());
				}
				
				String datasetColumn = null;
				String datasetLabel = null;
				Association association = associationGroup.getAssociation(associationName);
				if(association != null) {
					datasetColumn = association.getFields().get(0).getFieldName();
					datasetLabel = association.getFields().get(0).getDataSetLabel();
				} else {
					datasetColumn = associationName.split("\\.")[1];
					datasetLabel =  associationName.split("\\.")[0];
				}
				
				Operand leftOperand = new Operand(datasetLabel, datasetColumn);
				Operand rightOperand = new Operand(valuesList);
				FilterCriteria filter = new FilterCriteria(leftOperand, "IN", rightOperand);
				filters.add(filter);
			}
			
			joinedDataStore = cache.get(joinedDataSet, groupCriteria, filters, projectionCriteria);
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
		
		
		return joinedDataStore;
		
	}
	
	private List<IDataStore> storeDataSetsInCache(List<IDataSet> joinedDataSets, Map<String, String> parametersValues, boolean wait) {
		
		List<IDataStore> dataStores = new ArrayList<IDataStore>();
		
		try {
			ICache cache = SpagoBICacheManager.getCache();
			
			WorkManager spagoBIWorkManager;
			try {
				spagoBIWorkManager = new WorkManager(getSpagoBIConfigurationProperty("JNDI_THREAD_MANAGER"));
			} catch (NamingException t) {
				throw new RuntimeException("Impossible to initialize work manager");
			}
			commonj.work.WorkManager workManager = spagoBIWorkManager.getInnerInstance();
			
			List<Work> workItemList = new ArrayList<Work>();
			for(IDataSet dataSet: joinedDataSets) {
				// first we set parameters because they change the signature
				dataSet.setParamsMap(parametersValues);
				
				// then we verified if the store associated to the joined dtatset is in cache
				if(cache.contains(dataSet)) continue;
				
				// if not we create a work to store it and we add it to works list
				
				dataSet.loadData();
				IDataStore dataStore = dataSet.getDataStore();
				
				Work cacheWriteWork = new SQLDBCacheWriteWork(cache, dataStore, dataSet);
				
				workItemList.add(cacheWriteWork);
			}
			
			if(workItemList.size() > 0) {
				if(wait == true) {
					workManager.waitForAll(workItemList, workManager.INDEFINITE);
				} else {
					for(Work work : workItemList) workManager.schedule(work);
				}
			}
		
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while storing datasets in cache", t);
		} finally {			
			logger.debug("OUT");
		}
		
		return dataStores;
	}
	
	private IDataStore storeDataSetInCache(IDataSet dataSet, Map<String, String> parametersValues, boolean wait) {
		try {
			ICache cache = SpagoBICacheManager.getCache();
			dataSet.setParamsMap(parametersValues);
			dataSet.loadData();
			IDataStore dataStore = dataSet.getDataStore();
			
			WorkManager spagoBIWorkManager;
			try {
				spagoBIWorkManager = new WorkManager(getSpagoBIConfigurationProperty("JNDI_THREAD_MANAGER"));
			} catch (NamingException t) {
				throw new RuntimeException("Impossible to initialize work manager");
			}
			commonj.work.WorkManager workManager = spagoBIWorkManager.getInnerInstance();
			
			List<Work> workItemList = new ArrayList<Work>();
			Work cacheWriteWork = new SQLDBCacheWriteWork(cache, dataStore, dataSet);
			workItemList.add(cacheWriteWork);
			
			if(workItemList.size() > 0) {
				if(wait == true) {
					workManager.waitForAll(workItemList, workManager.INDEFINITE);
				} else {
					for(Work work : workItemList) workManager.schedule(work);
				}
			}
			
			return dataStore;
		} catch(ParametersNotValorizedException p){
			throw new ParametersNotValorizedException(p.getMessage());
		}catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	
	
	public IDataStore getAggregatedDataStore(String label, int offset, int fetchSize, int maxResults, CrosstabDefinition crosstabDefinition) {
		try {
			
			IDataSet dataSet = this.getDataSetDAO().loadDataSetByLabel(label);
			checkQbeDataset(dataSet);
			
			ICache cache = SpagoBICacheManager.getCache();
			IDataStore cachedResultSet = cache.get(dataSet);
			IDataStore dataStore = null;
			
			if (cachedResultSet == null){
				//Dataset not yet cached
				dataSet.loadData(offset, fetchSize, maxResults);
				dataStore = dataSet.getDataStore();
				
				cache.put(dataSet, dataStore);

			} 

			List<ProjectionCriteria> projectionCriteria = this.getProjectionCriteria(label, crosstabDefinition);
			List<FilterCriteria> filterCriteria = new ArrayList<FilterCriteria>(); // empty in this case
			List<GroupCriteria> groupCriteria = this.getGroupCriteria(label, crosstabDefinition);
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
	
	private static String getParametersNotValorized(List<JSONObject> parameters, Map<String, String> parametersValues){
		String toReturn = "";

		for (Iterator<JSONObject> iterator = parameters.iterator(); iterator.hasNext();) {
			JSONObject parameter = iterator.next();
			try{
				String parameterName = parameter.getString("namePar");
				if (parametersValues.get(parameterName) == null) {			
					toReturn += parameterName;
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
			for(IDataSet dataSet : dataSets) {
				checkQbeDataset(dataSet);
			}
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
			for(IDataSet dataSet : dataSets) {
				checkQbeDataset(dataSet);
			}
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
			for(IDataSet dataSet : dataSets) {
				checkQbeDataset(dataSet);
			}
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
			for(IDataSet dataSet : dataSets) {
				checkQbeDataset(dataSet);
			}
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
			for(IDataSet dataSet : dataSets) {
				checkQbeDataset(dataSet);
			}
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
		checkQbeDataset(datasetLab);
		
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
	
	/**
	 * @deprecated
	 */
	private List<ProjectionCriteria> getProjectionCriteria(String dataset, CrosstabDefinition crosstabDefinition){
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
			ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(dataset, columnName,null,columnName);
			projectionCriterias.add(aProjectionCriteria);
		}
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String columnName = aRow.getEntityId();
			ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(dataset, columnName,null,columnName);
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
					ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(dataset, columnName,function.getName(),columnName);
					projectionCriterias.add(aProjectionCriteria);
				} else {
					ProjectionCriteria aProjectionCriteria = new ProjectionCriteria(dataset, columnName,null,columnName);
					projectionCriterias.add(aProjectionCriteria);
				}
			}

		}

		
		logger.debug("OUT");
		return projectionCriterias;
	}
	
	/**
	 * @deprecated
	 */
	private List<GroupCriteria> getGroupCriteria(String dataset, CrosstabDefinition crosstabDefinition){
		logger.debug("IN");
		List<GroupCriteria> groupCriterias = new ArrayList<GroupCriteria>();

		
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
			
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String columnName = aColumn.getEntityId();
			GroupCriteria groupCriteria = new GroupCriteria(dataset, columnName,null);
			groupCriterias.add(groupCriteria);
		}
		
		
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String columnName = aRow.getEntityId();
			GroupCriteria groupCriteria = new GroupCriteria(dataset, columnName,null);
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
		MetaData newDataStoreMetadata = new MetaData();
		int fieldCount = dataStoreMetadata.getFieldCount();
		for (int i = 0; i < fieldCount; i++) {
			IFieldMetaData dataStoreFieldMetadata = dataStoreMetadata.getFieldMeta(i);
			String fieldName = dataStoreFieldMetadata.getName();
			int index = dataSetMetadata.getFieldIndex(fieldName);
			IFieldMetaData newFieldMetadata = null;
			if(index >= 0) {
				newFieldMetadata = new FieldMetadata();
				IFieldMetaData dataSetFieldMetadata = dataSetMetadata.getFieldMeta(index);
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
			} else {
				newFieldMetadata = dataStoreFieldMetadata;
			}
			newDataStoreMetadata.addFiedMeta(newFieldMetadata);
		}
		newDataStoreMetadata.setProperties(dataStoreMetadata.getProperties());
		dataStore.setMetaData(newDataStoreMetadata);
	}
	
	public static final String ADDITIONAL_DATA_FIELDS_OPTIONS_OPTIONS = "options";
	public static final String ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR = "measureScaleFactor";
	
	private void addMeasuresScaleFactor(JSONArray fieldOptions, String fieldId, IFieldMetaData newFieldMetadata) {
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
