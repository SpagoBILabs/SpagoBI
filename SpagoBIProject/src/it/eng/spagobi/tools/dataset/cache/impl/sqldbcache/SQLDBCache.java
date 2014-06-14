/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.cache.impl.sqldbcache;


import it.eng.spagobi.tools.dataset.bo.AbstractJDBCDataset;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.cache.CacheException;
import it.eng.spagobi.tools.dataset.cache.CacheItem;
import it.eng.spagobi.tools.dataset.cache.SpagoBICacheManager;
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.ICacheActivity;
import it.eng.spagobi.tools.dataset.cache.ICacheConfiguration;
import it.eng.spagobi.tools.dataset.cache.ICacheEvent;
import it.eng.spagobi.tools.dataset.cache.ICacheListener;
import it.eng.spagobi.tools.dataset.cache.ICacheMetadata;
import it.eng.spagobi.tools.dataset.cache.ICacheTrigger;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.work.SQLDBCacheWriteWork;
import it.eng.spagobi.tools.dataset.common.association.Association;
import it.eng.spagobi.tools.dataset.common.association.AssociationGroup;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.exceptions.ParametersNotValorizedException;
import it.eng.spagobi.tools.dataset.persist.PersistedTableManager;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commonj.work.Work;
import commonj.work.WorkItem;



/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCache implements ICache {
	
		
	private boolean enabled;
	private IDataSource dataSource;
	
	private SQLDBCacheMetadata cacheMetadata;

	private WorkManager spagoBIWorkManager;

	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";
	
	static private Logger logger = Logger.getLogger(SQLDBCache.class);
	
	public SQLDBCache(SQLDBCacheConfiguration cacheConfiguration){
		
		if (cacheConfiguration == null){
			throw new CacheException("Impossible to initialize cache. The cache configuration object cannot be null");
		}
		
		this.enabled = true;
		this.dataSource = cacheConfiguration.getCacheDataSource();
		this.cacheMetadata = new SQLDBCacheMetadata(cacheConfiguration);
	
		this.spagoBIWorkManager = cacheConfiguration.getWorkManager();
		
		
		eraseExistingTables(cacheMetadata.getTableNamePrefix().toUpperCase());
			
		String databaseSchema = cacheConfiguration.getSchema();
		if (databaseSchema != null){
			//test schema
			testDatabaseSchema(databaseSchema, dataSource);
		} 
	}
	
	
	// ===================================================================================
	// CONTAINS METHODS
	// ===================================================================================
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#contains(it.eng.spagobi.tools.dataset.bo.IDataSet)
	 */
	public boolean contains(IDataSet dataSet) {
		return contains(dataSet.getSignature());
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#contains(java.lang.String)
	 */
	public boolean contains(String resultsetSignature) {
		return getMetadata().containsCacheItem(resultsetSignature);
	}
	

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#contains(java.util.List)
	 */
	public boolean contains(List<IDataSet> dataSets) {
		return getNotContained(dataSets).size() > 0;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#getNotContained(java.util.List)
	 */
	public List<IDataSet> getNotContained(List<IDataSet> dataSets) {
		List<IDataSet> notContainedDataSets = new ArrayList<IDataSet>();
		for(IDataSet dataSet : dataSets) {
			if(contains(dataSet) == false) {
				notContainedDataSets.add(dataSet);
			}
		}
		return notContainedDataSets;
	}
	
	// ===================================================================================
	// GET METHODS
	// ===================================================================================
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(it.eng.spagobi.tools.dataset.bo.IDataSet)
	 */
	public IDataStore get(IDataSet dataSet) {
		IDataStore dataStore = null;
		
		logger.debug("IN");
		try {
			if(dataSet != null) {
				String dataSetSignature = null;
			
				try{
					dataSetSignature = dataSet.getSignature();
				}catch(ParametersNotValorizedException p){
					logger.warn("Error on getting signature for dataset [ "+ dataSet.getLabel() +" ]. Error: " + 
							p.getMessage());
					return null; //doesn't cache data
				}
				dataStore = get(dataSetSignature);
			} else {
				logger.warn("Input parameter [dataSet] is null");
			}
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occure while getting dataset from cache", t);
		} finally {
			logger.debug("OUT");
		}
		
		return dataStore;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String)
	 */
	public IDataStore get(String resultsetSignature) {
		IDataStore dataStore = null;
		
		logger.debug("IN");
		
		try {
			if (getMetadata().containsCacheItem(resultsetSignature)){
				logger.debug("Resultset with signature ["+resultsetSignature+"] found");
				CacheItem cacheItem = getMetadata().getCacheItem(resultsetSignature);
				String tableName = cacheItem.getTable();	
				logger.debug("The table associated to dataset ["+resultsetSignature+"] is [" + tableName + "]");
				dataStore = dataSource.executeStatement("SELECT * FROM " + tableName, 0, 0);		
			} else {
				logger.debug("Resultset with signature ["+resultsetSignature+"] not found");
			}
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occure while getting dataset from cache", t);
		} finally {
			logger.debug("OUT");
		}
		
		return dataStore;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(it.eng.spagobi.tools.dataset.bo.IDataSet, java.util.List, java.util.List, java.util.List)
	 */
	public IDataStore get(IDataSet dataSet, List<GroupCriteria> groups, List<FilterCriteria> filters, List<ProjectionCriteria> projections) { 
		IDataStore dataStore = null;
		
		logger.debug("IN");
		try {
			if(dataSet != null) {
				String dataSetSignature = dataSet.getSignature();
				dataStore = get(dataSetSignature, groups, filters, projections);
			} else {
				logger.warn("Input parameter [dataSet] is null");
			}
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occure while getting dataset from cache", t);
		} finally {
			logger.debug("OUT");
		}
		
		return dataStore;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String, java.util.List, java.util.List, java.util.List)
	 */
	public IDataStore get(String resultsetSignature,
			List<GroupCriteria> groups, List<FilterCriteria> filters,
			List<ProjectionCriteria> projections) {
		logger.debug("IN");
		
		if (getMetadata().containsCacheItem(resultsetSignature)){
			CacheItem cacheItem = getMetadata().getCacheItem(resultsetSignature); 
			String tableName = cacheItem.getTable();
			logger.debug("Found resultSet with signature ["+resultsetSignature+"] inside the Cache, table used ["+tableName+"]");
			
			SelectBuilder sqlBuilder = new SelectBuilder();
			sqlBuilder.from(tableName);
			
			//Columns to SELECT
			if(projections != null) {
				for (ProjectionCriteria projection : projections ){
					String aggregateFunction = projection.getAggregateFunction();
					String columnName = projection.getColumnName();
					String aliasName = projection.getAliasName();
					columnName = AbstractJDBCDataset.encapsulateColumnName(columnName, dataSource);
					aliasName = AbstractJDBCDataset.encapsulateColumnName(aliasName, dataSource);
					if ((aggregateFunction != null) && (!aggregateFunction.isEmpty()) && (columnName != "*") && (aliasName != null) && (!aliasName.isEmpty())){
						columnName = aggregateFunction + "("+columnName+") AS "+aliasName;
					}
					sqlBuilder.column(columnName);
					
				}
			}
			
			
			//WHERE conditions
			if(filters != null) {
				for (FilterCriteria filter : filters ){
					String leftOperand = null;
					if (filter.getLeftOperand().isCostant()){
						// why? warning!
						leftOperand = filter.getLeftOperand().getOperandValueAsString();
					} else { // it's a column
						Map<String, String> datasetAlias = (Map<String, String>)cacheItem.getProperty("DATASET_ALIAS");
						String datasetLabel = filter.getLeftOperand().getOperandDataSet();
						leftOperand = datasetAlias.get(datasetLabel) +  " - " + filter.getLeftOperand().getOperandValueAsString();
						leftOperand = AbstractJDBCDataset.encapsulateColumnName(leftOperand, dataSource);
					}
					
					String operator = filter.getOperator();
					
					String rightOperand = null;
					if (filter.getRightOperand().isCostant()){
						if(filter.getRightOperand().isMultivalue()) {
							rightOperand = "(";
							String separator = "";
							String stringDelimiter = "'";
							List<String> values =  filter.getRightOperand().getOperandValueAsList();
							for(String value : values) {
								rightOperand += separator + stringDelimiter + value + stringDelimiter;
								separator = ",";
							}
							rightOperand += ")";
						} else {
							rightOperand = filter.getRightOperand().getOperandValueAsString();
						}
					} else { // it's a column
						rightOperand = filter.getRightOperand().getOperandValueAsString();
						rightOperand = AbstractJDBCDataset.encapsulateColumnName(rightOperand, dataSource);
					}
					
					sqlBuilder.where(leftOperand+" "+operator+" "+rightOperand);
				}
			}
			
			//GROUP BY conditions 
			if(groups != null) {
				for (GroupCriteria group : groups ){
					String aggregateFunction = group.getAggregateFunction();
					String columnName = group.getColumnName();
					columnName = AbstractJDBCDataset.encapsulateColumnName(columnName, dataSource);
					if ((aggregateFunction != null) && (!aggregateFunction.isEmpty()) && (columnName != "*")){
						columnName = aggregateFunction + "("+columnName+")";
					}
					sqlBuilder.groupBy(columnName);
				}
			}
			
			String queryText = sqlBuilder.toString();
			IDataStore dataStore = dataSource.executeStatement(queryText, 0, 0);
			DataStore toReturn = (DataStore) dataStore;
			
			List<Integer> breakIndexes = (List<Integer>)cacheItem.getProperty("BREAK_INDEXES");
			if(breakIndexes != null) {
				dataStore.getMetaData().setProperty("BREAK_INDEXES", breakIndexes);
			}
			
			return toReturn;
		} else {
			logger.debug("Not found resultSet with signature ["+resultsetSignature+"] inside the Cache");
		}
		
		
		logger.debug("OUT");
		return null;

	}
	
	// ===================================================================================
	// LOAD METHODS
	// ===================================================================================
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#load(it.eng.spagobi.tools.dataset.bo.IDataSet, boolean)
	 */
	public IDataStore load(IDataSet dataSet, boolean wait) {
		List<IDataSet> dataSets = new ArrayList<IDataSet>();
		dataSets.add(dataSet);
		List<IDataStore> dataStores = load(dataSets, wait);
		return dataStores.get(0);
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#load(java.util.List, boolean)
	 */
	public List<IDataStore> load(List<IDataSet> dataSets, boolean wait) {
		List<IDataStore> dataStores = new ArrayList<IDataStore>();
		
		try {
			List<Work> works = new ArrayList<Work>();
			for(IDataSet dataSet: dataSets) {
				// first we set parameters because they change the signature
				// dataSet.setParamsMap(parametersValues);
				
				IDataStore dataStore = null;
				
				// then we verified if the store associated to the joined datatset is in cache
				if(contains(dataSet)) {
					dataStore = get(dataSet);
					dataStores.add(dataStore);
					continue;
				}
				
				// if not we create a work to store it and we add it to works list
				dataSet.loadData();
				dataStore = dataSet.getDataStore();
				dataStores.add(dataStore);
				
				Work cacheWriteWork = new SQLDBCacheWriteWork(this, dataStore, dataSet);
				works.add(cacheWriteWork);
			}
			
			
			if(works.size() > 0) {
				if(wait == true) {
					if(spagoBIWorkManager == null) {
						for(int i = 0; i < dataSets.size(); i++) {
							this.put(dataSets.get(i), dataStores.get(i));
						}
					} else {
						commonj.work.WorkManager workManager = spagoBIWorkManager.getInnerInstance();
						List<WorkItem> workItems = new ArrayList<WorkItem>();
						for(Work work : works) {
							WorkItem workItem = workManager.schedule(work);
							workItems.add(workItem);
						}
						
						workManager.waitForAll(workItems, workManager.INDEFINITE);
					}
				} else {
					if(spagoBIWorkManager == null) {
						throw new RuntimeException("Impossible to save the store in background because the work manager is not properly initialized");
					}
					
					commonj.work.WorkManager workManager = spagoBIWorkManager.getInnerInstance();
					for(Work workItem : works) {
						workManager.schedule(workItem);
					}
				}
				
			}
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		}
		
		return dataStores;
	}
	
	// ===================================================================================
	// REFRESH METHODS
	// ===================================================================================
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#load(it.eng.spagobi.tools.dataset.bo.IDataSet, boolean)
	 */
	public IDataStore refresh(IDataSet dataSet, boolean wait) {
		
		IDataStore dataStore = null;
		try {
			dataSet.loadData();
			dataStore = dataSet.getDataStore();
			
			if(wait == true) {
				this.put(dataSet, dataStore);
			} else {
				if(spagoBIWorkManager == null) {
					throw new RuntimeException("Impossible to save the store in background because the work manager is not properly initialized");
				}
				
				commonj.work.WorkManager workManager = spagoBIWorkManager.getInnerInstance();
				Work cacheWriteWork = new SQLDBCacheWriteWork(this, dataStore, dataSet);
				workManager.schedule(cacheWriteWork);
			}
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while executing method", t);
		} finally {			
			logger.debug("OUT");
		}	
		
		return dataStore;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#put(java.util.List, org.json.JSONArray, it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	public IDataStore refresh(List<IDataSet> dataSets, AssociationGroup associationGroup) {
		logger.trace("IN");
		try {
			SelectBuilder sqlBuilder = new SelectBuilder();
			
			Map<String, String> datasetAliases = new HashMap<String, String>();
			int aliasNo = 0;
			
			Map<String, List<String>> columnNames = new HashMap<String, List<String>>();
			List<Integer> columnBreakIndexes = new ArrayList<Integer>();
			int lastIndex = 0;
			columnBreakIndexes.add(lastIndex);
			for(IDataSet dataSet : dataSets) {
				List<String> names = new ArrayList<String>();
				if(contains(dataSet) == false) return null;
				String tableName = getMetadata().getCacheItem(dataSet.getSignature()).getTable();
				String tableAlias = "t" + ++aliasNo;
				datasetAliases.put(dataSet.getLabel(), tableAlias);
				sqlBuilder.from(tableName + " " + tableAlias);
				
				
				// TODO move this to dataset?
				String column = AbstractJDBCDataset.encapsulateColumnName("sbicache_row_id", dataSource);
				String alias = AbstractJDBCDataset.encapsulateColumnAlaias(tableAlias + " - sbicache_row_id", dataSource);
				names.add(alias);
				sqlBuilder.column(tableAlias + "." + column + " as " + alias);
				
				for(int i = 0; i < dataSet.getMetadata().getFieldCount(); i++) {
					IFieldMetaData fieldMeta = dataSet.getMetadata().getFieldMeta(i);
					
					column = AbstractJDBCDataset.encapsulateColumnName(fieldMeta.getName(), dataSource);
					alias = AbstractJDBCDataset.encapsulateColumnAlaias(tableAlias + " - " + fieldMeta.getAlias(), dataSource);
					names.add(alias);
					sqlBuilder.column(tableAlias + "." + column + " as " + alias);
				}
				lastIndex += dataSet.getMetadata().getFieldCount() + 1;
				columnBreakIndexes.add(lastIndex);
				columnNames.put(dataSet.getLabel(), names);
			}
			columnBreakIndexes.remove(0);
			columnBreakIndexes.remove(columnBreakIndexes.size()-1);
			
			Collection<Association> associaions = associationGroup.getAssociations();
			for(Association association: associaions) {
				String whereClause = "";
				String separator = "";
				for(Association.Field field: association.getFields()) {
					String dataset = field.getDataSetLabel();
					String column = field.getFieldName();
					column = AbstractJDBCDataset.encapsulateColumnName(column, dataSource);
					whereClause += separator + datasetAliases.get(dataset) + "." + column;
					separator = " = ";
				}
				sqlBuilder.where(whereClause);
			}
			
			
			String queryText = sqlBuilder.toString();
			IDataStore dataStore = dataSource.executeStatement(queryText, 0, 0);
						
			dataStore.getMetaData().setProperty("BREAK_INDEXES", columnBreakIndexes);
			dataStore.getMetaData().setProperty("COLUMN_NAMES", columnNames);
			dataStore.getMetaData().setProperty("DATASET_ALIAS", datasetAliases);
			
			
			DataStore toReturn = (DataStore) dataStore;
			
			return toReturn;
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occured while loading joined store from cache", t);
		} finally {
			logger.trace("OUT");
		}
	}
	
	
	// ===================================================================================
	// PUT METHODS
	// ===================================================================================
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#put(java.lang.String, it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	public void put(IDataSet dataSet, IDataStore dataStore) {
		logger.trace("IN");
		try {
			
			BigDecimal requiredMemory = getMetadata().getRequiredMemory(dataStore);
			
			if (getMetadata().isCleaningEnabled() 
					&& !getMetadata().isAvailableMemoryGreaterThen(requiredMemory)) {
				deleteToQuota();
			}
			
			//check again if there is enough space for the resultset
			if ( getMetadata().isAvailableMemoryGreaterThen(requiredMemory) ){
				String signature = dataSet.getSignature();
				String tableName = persistStoreInCache(dataSet, signature, dataStore);
				CacheItem item =  getMetadata().addCacheItem(signature, tableName, dataStore);
				List<Integer> breakIndexes =  (List<Integer>)dataStore.getMetaData().getProperty("BREAK_INDEXES");
				if(breakIndexes != null) {
					item.setProperty("BREAK_INDEXES", breakIndexes);
				}
				Map<String,List<String>> columnNames =  (Map<String,List<String>>)dataStore.getMetaData().getProperty("COLUMN_NAMES");
				if(columnNames != null) {
					item.setProperty("COLUMN_NAMES", columnNames);
				}
				
				Map<String, String> datasetAlias = (Map<String, String>)dataStore.getMetaData().getProperty("DATASET_ALIAS");
				if(datasetAlias != null) {
					item.setProperty("DATASET_ALIAS", datasetAlias);
				}
			} else {
				throw new CacheException("Store is to big to be persisted in cache." +
						" Store extimated dimenion is [" + getMetadata().getRequiredMemory(dataStore) + "]" +
						" while cache available space is [" + getMetadata().getAvailableMemory() + "]." +
						" Incrase cache size or execute the dataset disabling cache.");
			}
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occured while adding store into cache", t);
		} finally {
			logger.trace("OUT");
		}
		
		logger.debug("OUT");
	}

	private String persistStoreInCache(IDataSet dataset, String signature, IDataStore resultset) {
		logger.trace("IN");
		try {
			PersistedTableManager persistedTableManager = new PersistedTableManager();
			persistedTableManager.setRowCountColumIncluded(true);
			String tableName = persistedTableManager.generateRandomTableName( this.getMetadata().getTableNamePrefix() );
			persistedTableManager.persistDataset(dataset, resultset, getDataSource(), tableName);
			return tableName;
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occured while persisting store in cache", t);
		} finally {
			logger.trace("OUT");
		}	
	}
	
	// ===================================================================================
	// DELETE METHODS
	// ===================================================================================
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#delete(it.eng.spagobi.tools.dataset.bo.IDataSet)
	 */
	public boolean delete(IDataSet dataSet) {
		boolean result = false;
		
		logger.debug("IN");
		try {
			if(dataSet != null) {
				String dataSetSignature = dataSet.getSignature();
				result = delete(dataSetSignature);
			} else {
				logger.warn("Input parameter [dataSet] is null");
			}
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occure while deleting dataset from cache", t);
		} finally {
			logger.debug("OUT");
		}
		
		return result;
	}
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#delete(java.lang.String)
	 */
	public boolean delete(String signature) {
		if (getMetadata().containsCacheItem(signature)){
			PersistedTableManager persistedTableManager = new PersistedTableManager();
			String tableName = getMetadata().getCacheItem(signature).getTable();
			persistedTableManager.dropTableIfExists(getDataSource(), tableName);
			getMetadata().removeCacheItem(tableName);
			logger.debug("Removed table "+tableName+" from [SQLDBCache] corresponding to the result Set: "+signature);
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#deleteQuota()
	 */
	public void deleteToQuota() {
		logger.trace("IN");
		try {
			List<String> signatures = getMetadata().getSignatures();
			for (String signature: signatures) {
		        delete(signature); 
		        if (getMetadata().getAvailableMemoryAsPercentage() > getMetadata().getCleaningQuota()) {
		        	break;
		        }	
		    }
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occured while deleting cache to quota", t);
		} finally {
			logger.trace("OUT");
		}				
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#deleteAll()
	 */
	public void deleteAll() {
		logger.debug("Removing all tables from [SQLDBCache]");
		
		List<String> signatureToDelete = new ArrayList<String>();
		
		List<String> signatures = getMetadata().getSignatures();
		for(String signature : signatures) {
			CacheItem item =  getMetadata().getCacheItem(signature);
			signatureToDelete.add(item.getSignature());
		}
	    
	    for (String signature : signatureToDelete){
	    	delete(signature);
	    }
	    
	    
		logger.debug("[SQLDBCache] All tables removed, Cache cleaned ");
	}
	
	/**
	 * Erase existing tables that begins with the prefix
	 * @param prefix table name prefix
	 * 
	 */
	private void eraseExistingTables(String prefix){
		PersistedTableManager persistedTableManager = new PersistedTableManager();
		persistedTableManager.dropTablesWithPrefix(getDataSource(), prefix);
	}
	
	// ===================================================================================
	// ACCESSOR METHODS
	// ===================================================================================
	
	/**
	 * @return the dataSource
	 */
	public IDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Test if the passed schema name is correct.
	 * Create a table in the database via the dataSource then
	 * try to select the table using the schema.table syntax
	 * 
	 * @param schema the schema name
	 * @param dataSource the DataSource
	 */
	private void testDatabaseSchema(String schema, IDataSource dataSource){

		//Create a fake dataStore
		DataStore dataStore = new DataStore();
		IMetaData metadata = new MetaData();
		IFieldMetaData fieldMetaData = new FieldMetadata();
		fieldMetaData.setAlias("test_column");
		fieldMetaData.setName("test_column");
		fieldMetaData.setType(String.class);
		fieldMetaData.setFieldType(FieldType.ATTRIBUTE);
		metadata.addFiedMeta(fieldMetaData);
		dataStore.setMetaData(metadata);
		Record record = new Record();
		Field field = new Field();
		field.setValue("try");
		record.appendField(field);
		dataStore.appendRecord(record);
		
		//persist the datastore as a table on db
		PersistedTableManager persistedTableManager = new PersistedTableManager();
		Random ran = new Random();
		int x = ran.nextInt(100);
		String tableName = "SbiTest"+x;
		persistedTableManager.setTableName(tableName);
		
		try {
			persistedTableManager.persistDataset(dataStore, dataSource);
		} catch (Exception e) {
			logger.error("Error persisting dataset");
		}
		
		//try to query the table using the Schema.TableName syntax if schemaName is valorized
        
        try {
            if (schema.isEmpty()){
            	dataSource.executeStatement("SELECT * FROM "+tableName, 0, 0);

            } else {
            	dataSource.executeStatement("SELECT * FROM "+schema+"."+tableName, 0, 0);
            }
        }
        catch (Exception e){
			throw new CacheException("An unexpected error occured while testing database schema for cache");
        }
        finally {
            //Dropping table
            persistedTableManager.dropTableIfExists(dataSource,tableName);
    		
        }		
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#getCacheMetadata()
	 */
	public SQLDBCacheMetadata getMetadata() {
		return (SQLDBCacheMetadata)cacheMetadata;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#addListener(it.eng.spagobi.tools.dataset.cache.ICacheEvent, it.eng.spagobi.tools.dataset.cache.ICacheListener)
	 */
	public void addListener(ICacheEvent event, ICacheListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#scheduleActivity(it.eng.spagobi.tools.dataset.cache.ICacheActivity, it.eng.spagobi.tools.dataset.cache.ICacheTrigger)
	 */
	public void scheduleActivity(ICacheActivity activity, ICacheTrigger trigger) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#enable(boolean)
	 */
	public void enable(boolean enable) {
		this.enabled = enabled;
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	public WorkManager getSpagoBIWorkManager() {
		return spagoBIWorkManager;
	}

	public void setSpagoBIWorkManager(WorkManager spagoBIWorkManager) {
		this.spagoBIWorkManager = spagoBIWorkManager;
	}

	

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICache#refresh(java.util.List, boolean)
	 */
	public IDataStore refresh(List<IDataSet> dataSets, boolean wait) {
		// TODO Auto-generated method stub
		return null;
	}

}
