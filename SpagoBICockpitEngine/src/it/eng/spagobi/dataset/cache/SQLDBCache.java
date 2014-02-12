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
package it.eng.spagobi.dataset.cache;

import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.persist.PersistedTableManager;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.HashMap;
import java.util.List;


import org.apache.log4j.Logger;



/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCache implements ICache {
	
	static private Logger logger = Logger.getLogger(SQLDBCache.class);


	// Key is resultsetSignature, Entry is Table Name
	HashMap<String,String> cacheRegistry;	
	
	private IDataSource dataSource;
	
	public SQLDBCache(IDataSource dataSource){
		this.dataSource = dataSource;
		cacheRegistry = new HashMap<String,String>();

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
			
			// TODO: collegarsi al db, fare una select, ricavare il risultato e restituirlo come DataStore
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
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#delete(java.lang.String)
	 */
	@Override
	public boolean delete(String resultsetSignature) {
		// TODO Auto-generated method stub
		return false;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#deleteAll()
	 */
	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#getCacheMetadata()
	 */
	@Override
	public ICacheMetadata getCacheMetadata() {
		// TODO Auto-generated method stub
		return null;
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
		
		
		//1- Ricava connessione alla sorgente dati dal DataSource per la scrittura
		//2- Ricava la struttura della tabella da creare dal resultset (SQL CREATE) - attenzione ai dialetti DBMS
		//3- Ricava i dati dal resultset da inserire nella tabella appena creata (SQL INSERT) - attenzione ai dialetti DBMS
		PersistedTableManager persistedTableManager = new PersistedTableManager();
		
		try {
			String tableName = persistedTableManager.generateRandomTableName();
			persistedTableManager.persistDataset(resultset, getDataSource(), tableName);
			//4- Aggiorna il cacheRegistry con la nuova coppia <resultsetSignature,nometabellaCreata>
			cacheRegistry.put(resultsetSignature, tableName);
		} catch (Exception e) {
			logger.debug("[SQLDBCACHE]Cannot perform persistence of result set on database");
		}
		
		
		logger.debug("OUT");

	}

}
