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
import it.eng.spagobi.tools.dataset.cache.CacheConfiguration;
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.ICacheActivity;
import it.eng.spagobi.tools.dataset.cache.ICacheEvent;
import it.eng.spagobi.tools.dataset.cache.ICacheListener;
import it.eng.spagobi.tools.dataset.cache.ICacheMetadata;
import it.eng.spagobi.tools.dataset.cache.ICacheTrigger;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.persist.PersistedTableManager;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;



/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCache implements ICache {
	
	static private Logger logger = Logger.getLogger(SQLDBCache.class);
	
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";

	private ICacheMetadata cacheMetadata;
	private IDataSource dataSource;
	private List<Properties> objectsTypeDimension = new ArrayList<Properties>();
	private CacheConfiguration cacheConfiguration;
	
	private String tableNamePrefix;
	
	public SQLDBCache(IDataSource dataSource, CacheConfiguration cacheConfiguration){
		this.dataSource = dataSource;
		this.cacheConfiguration = cacheConfiguration;

		if (this.cacheConfiguration != null){
			tableNamePrefix = this.cacheConfiguration.getTableNamePrefix();
			if (tableNamePrefix != null){
				eraseExistingTables(tableNamePrefix.toUpperCase());
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


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String)
	 */
	public IDataStore get(String resultsetSignature) {
		logger.debug("IN");

		if (getCacheMetadata().containsCacheItemByResultsetSignature(resultsetSignature)){
			String tableName = getCacheMetadata().getCacheItemByResultsetSignature(resultsetSignature).getTable();
			logger.debug("Found resultSet with signature ["+resultsetSignature+"] inside the Cache, table used ["+tableName+"]");
			
			IDataStore dataStore = dataSource.executeStatement("SELECT * FROM "+tableName, 0, 0);
			DataStore toReturn = (DataStore) dataStore;
			
			return toReturn;
		} 	
		logger.debug("Not found resultSet with signature ["+resultsetSignature+"] inside the Cache");
		logger.debug("OUT");
		return null;

	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String, java.util.List, java.util.List, java.util.List)
	 */
	public IDataStore get(String resultsetSignature,
			List<GroupCriteria> groups, List<FilterCriteria> filters,
			List<ProjectionCriteria> projections) {
		logger.debug("IN");
		
		if (getCacheMetadata().containsCacheItemByResultsetSignature(resultsetSignature)){
			String tableName = getCacheMetadata().getCacheItemByResultsetSignature(resultsetSignature).getTable();
			logger.debug("Found resultSet with signature ["+resultsetSignature+"] inside the Cache, table used ["+tableName+"]");
			
			SelectBuilder sqlBuilder = new SelectBuilder();
			sqlBuilder.from(tableName);
			
			//Columns to SELECT
			for (ProjectionCriteria projection : projections ){
				String aggregateFunction = projection.getAggregateFunction();
				String columnName = projection.getColumnName();
				columnName = AbstractJDBCDataset.encapsulateColumnName(columnName, dataSource);
				if ((aggregateFunction != null) && (!aggregateFunction.isEmpty()) && (columnName != "*")){
					columnName = aggregateFunction + "("+columnName+")";
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
	 * @see it.eng.spagobi.dataset.cache.ICache#delete(java.lang.String)
	 */
	public boolean delete(String signature) {
		if (getCacheMetadata().containsCacheItemByResultsetSignature(signature)){
			PersistedTableManager persistedTableManager = new PersistedTableManager();
			String tableName = getCacheMetadata().getCacheItemByResultsetSignature(signature).getTable();
			persistedTableManager.dropTableIfExists(getDataSource(), tableName);
			getCacheMetadata().removeCacheItem(tableName);
			logger.debug("Removed table "+tableName+" from [SQLDBCache] corresponding to the result Set: "+signature);
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#deleteAll()
	 */
	public void deleteAll() {
		logger.debug("Removing all tables from [SQLDBCache]");
		Iterator it = getCacheMetadata().getCacheRegistry().entrySet().iterator();
	    List<String> signatureToDelete = new ArrayList<String>();

	    while (it.hasNext()) {
	        Map.Entry<String,CacheItem> entry = (Map.Entry<String,CacheItem>)it.next();
	        CacheItem item =  entry.getValue();
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
	public ICacheMetadata getCacheMetadata() {
		if (cacheMetadata == null){		
			cacheMetadata = new SQLDBCacheMetadata(getDataSource(), cacheConfiguration);
			if (cacheMetadata instanceof SQLDBCacheMetadata){
				((SQLDBCacheMetadata)cacheMetadata).setObjectsTypeDimension(getObjectsTypeDimension());
			}
		}  
		return cacheMetadata;
	}


	

	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#put(java.lang.String, it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	public void put(IDataSet dataset,String resultsetSignature, IDataStore resultset) {
		logger.debug("IN");
		
		//0- Checks if there is enough space free in the cache for the new resultset 
		//   (ONLY if all cache parameters are correctly defined)		
		if (getCacheMetadata().isActiveCleanAction()){
			if (!getCacheMetadata().hasEnoughMemoryForResultSet(resultset)){		
				//clean of the cache
				Iterator it = getCacheMetadata().getCacheRegistry().entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String,CacheItem> entry = (Map.Entry<String,CacheItem>)it.next();
			        CacheItem item = entry.getValue();
			        String signature = item.getSignature();
			        logger.debug("Delete object ["+signature+"] for cleaning cache event.");
			        delete(signature); 
			        if (getCacheMetadata().getAvailableMemoryAsPercentage() > getCacheMetadata().getAvailableMemoryBaseline())
			        	break;
			    }				
			}
		}
		//check again if there is enough space for the resultset
		if ( ((getCacheMetadata().isActiveCleanAction()) && (getCacheMetadata().hasEnoughMemoryForResultSet(resultset))) || 
		(!getCacheMetadata().isActiveCleanAction()) ){
			//1- Gets the connection for writing (from the datasource) 
			//2- Derive the structure of the table to be created from the resultset (SQL CREATE) - attention to DBMS dialects
			//3- Draws the data from the resultset to be included in the newly created table (SQL INSERT) - attention to DBMS dialects
			PersistedTableManager persistedTableManager = new PersistedTableManager();
			String tablePrefix = null;
			
			try {
				if (this.cacheConfiguration != null){
					tableNamePrefix = this.cacheConfiguration.getTableNamePrefix();
					if (tableNamePrefix != null){
						tablePrefix = tableNamePrefix.toUpperCase();
					}
				}
				
				String tableName = persistedTableManager.generateRandomTableName(tablePrefix);
				persistedTableManager.persistDataset(dataset, resultset, getDataSource(), tableName);
				//4- Update cacheRegistry with the new couple <resultsetSignature,nometabellaCreata>
				getCacheMetadata().addCacheItem(resultsetSignature, tableName, resultset);
			} catch (Exception e) {
				logger.error("[SQLDBCACHE]Cannot perform persistence of result set on database");
			}
		} else {
			logger.warn("The resultset ["+resultsetSignature+"] cannot be cached because there is not enough space avaiable");
		}
		

		
		
		logger.debug("OUT");

	}


	/**
	 * @return the objectsTypeDimension
	 */
	public List<Properties> getObjectsTypeDimension() {
		return objectsTypeDimension;
	}


	/**
	 * @param objectsTypeDimension the objectsTypeDimension to set
	 */
	public void setObjectsTypeDimension(List<Properties> objectsTypeDimension) {
		this.objectsTypeDimension = objectsTypeDimension;
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
}
