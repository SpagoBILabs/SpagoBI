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
package it.eng.spagobi.dataset.cache.impl.sqldbcache;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.dataset.cache.ICache;
import it.eng.spagobi.dataset.cache.ICacheActivity;
import it.eng.spagobi.dataset.cache.ICacheEvent;
import it.eng.spagobi.dataset.cache.ICacheListener;
import it.eng.spagobi.dataset.cache.ICacheMetadata;
import it.eng.spagobi.dataset.cache.ICacheTrigger;
import it.eng.spagobi.tools.dataset.bo.AbstractJDBCDataset;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.persist.PersistedTableManager;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;



/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCache implements ICache {
	
	static private Logger logger = Logger.getLogger(SQLDBCache.class);
	
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";


	// Key is resultsetSignature, Entry is Table Name
	private HashMap<String,String> cacheRegistry;	
	
	private IDataSource dataSource;
	
	private Config tableNamePrefixConfig;
	
	public SQLDBCache(IDataSource dataSource){
		this.dataSource = dataSource;
		cacheRegistry = new HashMap<String,String>();
		try {
			IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
			tableNamePrefixConfig = configDao.loadConfigParametersByLabel(CACHE_NAME_PREFIX_CONFIG);
			if (tableNamePrefixConfig.isActive()){
				String tablePrefix = tableNamePrefixConfig.getValueCheck();
				eraseExistingTables(tablePrefix.toUpperCase());
			}

		} catch (EMFUserError e) {
			logger.debug("Impossible to instantiate SbiConfigDAO in SQLDBCache");
		} catch (Exception e) {
			logger.debug("Impossible to instantiate SbiConfigDAO in SQLDBCache");
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
	 * @see it.eng.spagobi.dataset.cache.ICache#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String resultsetSignature) {
		return cacheRegistry.containsKey(resultsetSignature);
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String)
	 */
	@Override
	public IDataStore get(String resultsetSignature) {
		logger.debug("IN");

		if (cacheRegistry.containsKey(resultsetSignature)){
			String tableName = cacheRegistry.get(resultsetSignature);
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
	@Override
	public IDataStore get(String resultsetSignature,
			List<GroupCriteria> groups, List<FilterCriteria> filters,
			List<ProjectionCriteria> projections) {
		logger.debug("IN");
		
		if (cacheRegistry.containsKey(resultsetSignature)){
			String tableName = cacheRegistry.get(resultsetSignature);
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
	@Override
	public boolean delete(String resultsetSignature) {
		if (cacheRegistry.containsKey(resultsetSignature)){
			PersistedTableManager persistedTableManager = new PersistedTableManager();
			String tableName = cacheRegistry.get(resultsetSignature);
			persistedTableManager.dropTableIfExists(getDataSource(), tableName);
			cacheRegistry.remove(resultsetSignature);
			logger.debug("Removed table "+tableName+" from [SQLDBCache] corresponding to the result Set: "+resultsetSignature);
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#deleteAll()
	 */
	@Override
	public void deleteAll() {
		logger.debug("Removing all tables from [SQLDBCache]");
		Iterator it = cacheRegistry.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
	        String resultsetSignature = entry.getKey();
	        this.delete(resultsetSignature);
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		logger.debug("[SQLDBCache] All tables removed, Cache cleaned ");
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#getCacheMetadata()
	 */
	@Override
	public ICacheMetadata getCacheMetadata() {
		return new SQLDBCacheMetadata(getDataSource(), cacheRegistry);
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#addListener(it.eng.spagobi.dataset.cache.ICacheEvent, it.eng.spagobi.dataset.cache.ICacheListener)
	 */
	@Override
	public void addListener(ICacheEvent event, ICacheListener listener) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#scheduleActivity(it.eng.spagobi.dataset.cache.ICacheActivity, it.eng.spagobi.dataset.cache.ICacheTrigger)
	 */
	@Override
	public void scheduleActivity(ICacheActivity activity, ICacheTrigger trigger) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#put(java.lang.String, it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	@Override
	public void put(IDataSet dataset,String resultsetSignature, IDataStore resultset) {
		logger.debug("IN");
		
		//0- Controlla che ci sia lo spazio disponibile nella cache (se i parametri sono correttamente configurati)
		ICacheMetadata mdCache = this.getCacheMetadata();
		if (mdCache.isActiveCleanAction()){
			if (!mdCache.hasSpaceForResultSet(resultset)){
				//start clean action from the cache
			}
		}
		//1- Ricava connessione alla sorgente dati dal DataSource per la scrittura
		//2- Ricava la struttura della tabella da creare dal resultset (SQL CREATE) - attenzione ai dialetti DBMS
		//3- Ricava i dati dal resultset da inserire nella tabella appena creata (SQL INSERT) - attenzione ai dialetti DBMS
		PersistedTableManager persistedTableManager = new PersistedTableManager();
		String tablePrefix = null;
		
		try {
			if (tableNamePrefixConfig.isActive()){
				tablePrefix = tableNamePrefixConfig.getValueCheck();
				tablePrefix.toUpperCase();
			}
			String tableName = persistedTableManager.generateRandomTableName(tablePrefix);
			persistedTableManager.persistDataset(dataset, resultset, getDataSource(), tableName);
			//4- Aggiorna il cacheRegistry con la nuova coppia <resultsetSignature,nometabellaCreata>
			cacheRegistry.put(resultsetSignature, tableName);
		} catch (Exception e) {
			logger.debug("[SQLDBCACHE]Cannot perform persistence of result set on database");
		}
		
		
		logger.debug("OUT");

	}

}
