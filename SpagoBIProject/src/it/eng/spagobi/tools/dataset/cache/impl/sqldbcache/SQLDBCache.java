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
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.ICacheActivity;
import it.eng.spagobi.tools.dataset.cache.ICacheConfiguration;
import it.eng.spagobi.tools.dataset.cache.ICacheEvent;
import it.eng.spagobi.tools.dataset.cache.ICacheListener;
import it.eng.spagobi.tools.dataset.cache.ICacheMetadata;
import it.eng.spagobi.tools.dataset.cache.ICacheTrigger;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.exceptions.ParameterDsException;
import it.eng.spagobi.tools.dataset.persist.PersistedTableManager;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;



/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCache implements ICache {
	
	// we really need it?
	private SQLDBCacheConfiguration cacheConfiguration;
	
	private boolean enabled;
	private IDataSource dataSource;
	private ICacheMetadata cacheMetadata;
	
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";
	
	static private Logger logger = Logger.getLogger(SQLDBCache.class);
	
	
	public SQLDBCache(ICacheConfiguration cacheConfiguration){
		this.enabled = true;
		this.dataSource = cacheConfiguration.getCacheDataSource();
		this.cacheConfiguration = (SQLDBCacheConfiguration)cacheConfiguration;

		if (this.cacheConfiguration != null){
			String tableNamePrefix = this.cacheConfiguration.getTableNamePrefix();
			if (tableNamePrefix != null){
				if (!tableNamePrefix.isEmpty()){
					eraseExistingTables(tableNamePrefix.toUpperCase());
				} else {
					throw new CacheException("An unexpected error occured while initializing cache: SPAGOBI.CACHE.NAMEPREFIX cannot be empty");
				}
			} else {
				throw new CacheException("An unexpected error occured while initializing cache: SPAGOBI.CACHE.NAMEPREFIX not found");
			}
			
			String databaseSchema = this.cacheConfiguration.getSchema();
			if (databaseSchema != null){
				//test schema
				testDatabaseSchema(databaseSchema, dataSource);
			} 
		}
	}
	

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
	 * Erase existing tables that begins with the prefix
	 * @param prefix table name prefix
	 * 
	 */
	private void eraseExistingTables(String prefix){
		PersistedTableManager persistedTableManager = new PersistedTableManager();
		persistedTableManager.dropTablesWithPrefix(getDataSource(), prefix);
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
				}catch(ParameterDsException p){
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
				logger.debug("The table associated to dataset ["+resultsetSignature+"] is [tableName]");
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
			String tableName = getMetadata().getCacheItem(resultsetSignature).getTable();
			logger.debug("Found resultSet with signature ["+resultsetSignature+"] inside the Cache, table used ["+tableName+"]");
			
			SelectBuilder sqlBuilder = new SelectBuilder();
			sqlBuilder.from(tableName);
			
			//Columns to SELECT
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
			
			//WHERE conditions
			for (FilterCriteria filter : filters ){
				String leftOperand = filter.getLeftOperand().getOperandText();
				if (!filter.getLeftOperand().isCostant()){
					leftOperand = AbstractJDBCDataset.encapsulateColumnName(leftOperand, dataSource);
				}
				String operator = filter.getOperator();
				String rightOperand = filter.getRightOperand().getOperandText();
				if (!filter.getRightOperand().isCostant()){
					rightOperand = AbstractJDBCDataset.encapsulateColumnName(rightOperand, dataSource);
				}
				
				sqlBuilder.where(leftOperand+" "+operator+" "+rightOperand);
			}
			
			//GROUP BY conditions 
			for (GroupCriteria group : groups ){
				String aggregateFunction = group.getAggregateFunction();
				String columnName = group.getColumnName();
				columnName = AbstractJDBCDataset.encapsulateColumnName(columnName, dataSource);
				if ((aggregateFunction != null) && (!aggregateFunction.isEmpty()) && (columnName != "*")){
					columnName = aggregateFunction + "("+columnName+")";
				}
				sqlBuilder.groupBy(columnName);
			}
			
			String queryText = sqlBuilder.toString();
			IDataStore dataStore = dataSource.executeStatement(queryText, 0, 0);
			DataStore toReturn = (DataStore) dataStore;
			
			return toReturn;
		} else {
			logger.debug("Not found resultSet with signature ["+resultsetSignature+"] inside the Cache");
		}
		
		
		logger.debug("OUT");
		return null;

	}

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


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#getCacheMetadata()
	 */
	public ICacheMetadata getMetadata() {
		if (cacheMetadata == null){		
			cacheMetadata = new SQLDBCacheMetadata(cacheConfiguration);
		}  
		return cacheMetadata;
	}


	

	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#put(java.lang.String, it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	public void put(IDataSet dataSet, IDataStore dataStore) {
		logger.trace("IN");
		try {
			if (getMetadata().isCleaningEnabled() 
					&& !getMetadata().hasEnoughMemoryForStore(dataStore)) {
				deleteToQuota();
			}
			
			//check again if there is enough space for the resultset
			if ( getMetadata().hasEnoughMemoryForStore(dataStore) ){
				String signature = dataSet.getSignature();
				String tableName = persistStoreInCache(dataSet, signature, dataStore);
				getMetadata().addCacheItem(signature, tableName, dataStore);
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
			String tablePrefix = null;
			String tableNamePrefix = this.cacheConfiguration.getTableNamePrefix();
			if (tableNamePrefix != null){
				tablePrefix = tableNamePrefix.toUpperCase();
			}
			
			String tableName = persistedTableManager.generateRandomTableName(tablePrefix);
			persistedTableManager.persistDataset(dataset, resultset, getDataSource(), tableName);
			return tableName;
		} catch(Throwable t) {
			if(t instanceof CacheException) throw (CacheException)t;
			else throw new CacheException("An unexpected error occured while persisting store in cache", t);
		} finally {
			logger.trace("OUT");
		}	
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
}
