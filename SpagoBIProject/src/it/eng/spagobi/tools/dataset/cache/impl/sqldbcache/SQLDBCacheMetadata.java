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

import it.eng.spagobi.tools.dataset.cache.CacheItem;
import it.eng.spagobi.tools.dataset.cache.ICacheMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.database.DataBase;
import it.eng.spagobi.utilities.database.IDataBase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public class SQLDBCacheMetadata implements ICacheMetadata {
	
	private LinkedHashMap<String, CacheItem> cacheRegistry = new LinkedHashMap<String, CacheItem>();	
	
	SQLDBCacheConfiguration cacheConfiguration;

	private BigDecimal totalMemory;
	private BigDecimal availableMemory ;
	private BigDecimal usedMemory ;
	
	private String  tableNamePrefix;
	
	private boolean isActiveCleanAction = false;
	private Integer cachePercentageToClean;
	
	private Map<String, Integer> columnSize =  new HashMap<String, Integer>();
	
	private enum FieldType {ATTRIBUTE, MEASURE}
	
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";
	public static final String CACHE_SPACE_AVAILABLE_CONFIG = "SPAGOBI.CACHE.SPACE_AVAILABLE";
	public static final String CACHE_LIMIT_FOR_CLEAN_CONFIG = "SPAGOBI.CACHE.LIMIT_FOR_CLEAN";
	public static final String DIALECT_MYSQL = "MySQL";
	public static final String DIALECT_POSTGRES = "PostgreSQL";
	public static final String DIALECT_ORACLE = "OracleDialect";
	public static final String DIALECT_HSQL = "HSQL";
	public static final String DIALECT_HSQL_PRED = "Predefined hibernate dialect";
	public static final String DIALECT_ORACLE9i10g = "Oracle9Dialect";
	public static final String DIALECT_SQLSERVER = "SQLServer";
	public static final String DIALECT_DB2 = "DB2";
	public static final String DIALECT_INGRES = "Ingres";
	public static final String DIALECT_TERADATA = "Teradata";
	
	static private Logger logger = Logger.getLogger(SQLDBCacheMetadata.class);

	public SQLDBCacheMetadata(SQLDBCacheConfiguration cacheConfiguration){
		this.cacheConfiguration = cacheConfiguration;
		if (this.cacheConfiguration != null){
			tableNamePrefix = this.cacheConfiguration.getTableNamePrefix();
			totalMemory = this.cacheConfiguration.getCacheSpaceAvailable();
			cachePercentageToClean = this.cacheConfiguration.getCachePercentageToClean();
		}
		
		
		if (tableNamePrefix != null && !"".equals(tableNamePrefix) &&
			totalMemory != null && cachePercentageToClean != null  ) {
			isActiveCleanAction = true;
		}
	}
	
	public BigDecimal getTotalMemory() {
		return totalMemory;
	}
	
	/**
	 * Returns the number of bytes used by the table already cached (approximate)
	 */
	public BigDecimal getAvailableMemory(){
		availableMemory = getTotalMemory();
//		String query = "SELECT ";	
//		
//		if (dataSource.getHibDialectClass().contains(DIALECT_POSTGRES)){
//			query += //" pg_size_pretty(sum(pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || \'\"\'))) AS size, " +
//					 " sum(pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || '\"')) as size " +
//					 " FROM information_schema.tables " +
//					 " where table_name like '"+ tableNamePrefix +"%'";
//		}else if (dataSource.getHibDialectClass().contains(DIALECT_MYSQL)){
//			query += " coalesce(sum(round(((data_length + index_length)),2)),0) as size " +
//					 " FROM information_schema.TABLES WHERE table_name like '"+ tableNamePrefix +"%'";
//		}else if (dataSource.getHibDialectClass().contains(DIALECT_ORACLE) ||
//				dataSource.getHibDialectClass().contains(DIALECT_ORACLE9i10g)){
//			query += " sum(num_rows*avg_row_len) as sizet " +
//					 " from all_tables " + 
//					 " where table_name like '"+ tableNamePrefix +"%'";
//		}else{
//			//get approximate dimension
//			Iterator it = cacheRegistry.entrySet().iterator();
//		    while (it.hasNext()) {
//		    	BigDecimal size = null;
//		        Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
//		        String signature = entry.getValue();
//		        query = " select * from " + signature;
//		        IDataStore dataStore  = dataSource.executeStatement(query, 0, 0);
//				DataStore ds = (DataStore) dataStore;				
//				BigDecimal rowWeight = getRowWeight(ds.getRecordAt(0), ds.getMetaData());
//				size = rowWeight.multiply(new BigDecimal(ds.getRecordsCount())) ;
//				logger.debug("Dimension stimated for cached object "+ signature +" [rowWeight*rows]: " + size + " ["+rowWeight+" * "+ds.getRecordsCount()+"]");
//				if (size != null) availableMemory = availableMemory.subtract(size);		        
//		    }
//		    logger.debug("Remaining cache free space: " + availableMemory);
//		    return availableMemory;
//		}
//		logger.debug("Defined query: " +query);
//		IDataStore dataStore  = dataSource.executeStatement(query, 0, 0);
//		DataStore ds = (DataStore) dataStore;
//		BigDecimal size = null;
//		if (ds.getRecordsCount() > 0){
//			IRecord rec = ds.getRecordAt(0);
//			for (int i=0, l=rec.getFields().size(); i<l; i++){		
//				IField field = rec.getFieldAt(i);
//				size = (BigDecimal)field.getValue();
//			}
//		}
		IDataBase dataBase = DataBase.getDataBase( cacheConfiguration.getCacheDataSource() );
		BigDecimal size = dataBase.getUsedMemorySize(cacheConfiguration.getSchema(), cacheConfiguration.getTableNamePrefix());
		logger.debug("Size of object cached: " + size);
		if (size != null) availableMemory = availableMemory.subtract(size);
		logger.debug("Remaining cache free space: " + availableMemory);
		return availableMemory;
	}	
	
	/**
	 * @return the number of bytes used by the resultSet (approximate)
	 */
	public BigDecimal getRequiredMemory(IDataStore store){
		return DataStoreStatistics.extimateMemorySize(store, cacheConfiguration.getObjectsTypeDimension());
	}
	
	public 	Integer getAvailableMemoryAsPercentage(){
		Integer toReturn = 0;
		BigDecimal spaceAvailable = getAvailableMemory();
		toReturn = Integer.valueOf(((spaceAvailable.multiply(new BigDecimal(100)).divide(getTotalMemory(),RoundingMode.HALF_UP)).intValue()));
		return toReturn;
	}

	public Integer getNumberOfObjects(){		
		return cacheRegistry.size();
	}

	public boolean isCleaningEnabled(){ 
		return isActiveCleanAction;
	} 
	
	public Integer getCleaningQuota(){
		return cachePercentageToClean;
	}
	
	public boolean hasEnoughMemoryForStore(IDataStore store) {
		BigDecimal availableMemory = getAvailableMemory();
		BigDecimal requiredMemory = getRequiredMemory(store);
		if (availableMemory.compareTo(requiredMemory) <= 0){
			return false;
		} else {
			return true;
		}
	}	
	
	private Map<String, Integer> getColumnSize() {
		return columnSize;
	}
	
	public LinkedHashMap<String, CacheItem> getCacheRegistry() {
		return cacheRegistry;
	}

	public void setCacheRegistry(LinkedHashMap<String, CacheItem> cacheRegistry) {
		this.cacheRegistry = cacheRegistry;
	}


	public void addCacheItem(String resultsetSignature, String tableName, IDataStore resultset) {
		CacheItem item = new CacheItem();
		item.setName(tableName);
		item.setTable(tableName);
		item.setSignature(resultsetSignature);				
		item.setDimension(getRequiredMemory(resultset));
		item.setCreationDate(new Date());
		getCacheRegistry().put(tableName,item);		
		
		logger.debug("Added cacheItem : [ Name: " + item.getName() + " \n Signature: " + item.getSignature() +
				" \n Dimension: "+ item.getDimension() +" bytes (approximately)  ]");
	}


	public void removeCacheItem(String tableName) {
		getCacheRegistry().remove(tableName);		
	}


	public void removeAllCacheItems() {
		Iterator it = getCacheRegistry().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,CacheItem> entry = (Map.Entry<String,CacheItem>)it.next();
	        String key = entry.getKey();
	        this.removeCacheItem(key);
	    }		
	}

	public CacheItem getCacheItemByResultSetTableName(String tableName) {
		CacheItem toReturn = null;
		Iterator it = getCacheRegistry().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,CacheItem> entry = (Map.Entry<String,CacheItem>)it.next();	
	        CacheItem item =  entry.getValue();
	        if (item.getTable().equalsIgnoreCase(tableName)){
	        	toReturn = item;
	        	break;
	        }
	    }
		return toReturn;
	}

	public CacheItem getCacheItem(String resultSetSignature){
		CacheItem toReturn = null;
		Iterator it = getCacheRegistry().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,CacheItem> entry = (Map.Entry<String,CacheItem>)it.next();	
	        CacheItem item =  entry.getValue();
	        if (item.getSignature().equalsIgnoreCase(resultSetSignature)){
	        	toReturn = item;
	        	break;
	        }
	    }
		return toReturn;
	}

	public boolean containsCacheItemByTableName(String tableName) {
		return getCacheItemByResultSetTableName(tableName) != null;
	}
	
	public boolean containsCacheItem(String resultSetSignature) {
		return getCacheItem(resultSetSignature) != null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.cache.ICacheMetadata#getSignatures()
	 */
	public List<String> getSignatures() {
		List<String> signatures = new ArrayList<String>();
		Iterator it = getCacheRegistry().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,CacheItem> entry = (Map.Entry<String,CacheItem>)it.next();
	        signatures.add( entry.getValue().getSignature() );
	    }
	    return signatures;
	}

}
