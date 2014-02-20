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
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.dataset.cache.ICacheMetadata;
import it.eng.spagobi.engine.cockpit.CockpitEngineConfig;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public class SQLDBCacheMetadata implements ICacheMetadata {
	
	static private Logger logger = Logger.getLogger(SQLDBCacheMetadata.class);
	
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

	
	private IDataSource dataSource;
	private BigDecimal dimensionSpaceFree ;
	private BigDecimal dimensionSpaceUsed ;
	private HashMap<String,String> cacheRegistry;	
	
	private String  tableNamePrefix;
	private BigDecimal cacheSpaceAvailable;
	private Integer cacheLimit4Clean;
	private boolean isActiveCleanAction = false;
	
	private List objectsDimension = new ArrayList();
	private Map<String, Integer> columnSize =  new HashMap<String, Integer>();
	private enum FieldType {ATTRIBUTE, MEASURE}
	
	public SQLDBCacheMetadata(IDataSource ds, HashMap<String,String> registry){
		try {
			dataSource = ds;
			cacheRegistry = registry;
			objectsDimension = CockpitEngineConfig.getDimensionTypes();
			
			
			IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
			Config tableNamePrefixConfig = configDao.loadConfigParametersByLabel(CACHE_NAME_PREFIX_CONFIG);
			if (tableNamePrefixConfig.isActive()){
				tableNamePrefix = tableNamePrefixConfig.getValueCheck();

			}
			Config cacheSpaceAvailableConfig = configDao.loadConfigParametersByLabel(CACHE_SPACE_AVAILABLE_CONFIG);
			if (cacheSpaceAvailableConfig.isActive()){
				cacheSpaceAvailable = BigDecimal.valueOf(Double.valueOf(cacheSpaceAvailableConfig.getValueCheck()));
			}			
			Config cacheSpaceCleanableConfig = configDao.loadConfigParametersByLabel(CACHE_LIMIT_FOR_CLEAN_CONFIG);
			if (cacheSpaceAvailableConfig.isActive()){
				cacheLimit4Clean = Integer.valueOf(cacheSpaceCleanableConfig.getValueCheck());
			}
			if (tableNamePrefix != null && !"".equals(tableNamePrefix) &&
				cacheSpaceAvailable != null && cacheLimit4Clean != null  ) 
					isActiveCleanAction = true;
		
		} catch (EMFUserError e) {
			logger.debug("Impossible to instantiate SbiConfigDAO in SQLDBCacheMetadata");
		} catch (Exception e) {
			logger.debug("Impossible to instantiate SbiConfigDAO in SQLDBCacheMetadata");
		}
	}
	
	/**
	 * Returns the number of bytes used by the table already cached (approximate)
	 */
	public BigDecimal getDimensionSpaceAvailable(){
		dimensionSpaceFree = cacheSpaceAvailable;
		String query = "SELECT ";	
		
		if (dataSource.getHibDialectClass().contains(DIALECT_POSTGRES)){
			query += " pg_size_pretty(sum(pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || \'\"\'))) AS size, " +
					 " sum(pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || '\"')) as size_num " +
					 " FROM information_schema.tables " +
					 " where table_name like '"+ tableNamePrefix +"%'";
		}else if (dataSource.getHibDialectClass().contains(DIALECT_MYSQL)){
			query += " coalesce(sum(round(((data_length + index_length)),2)),0) as size " +
					 " FROM information_schema.TABLES WHERE table_name like '"+ tableNamePrefix +"%'";
		}else if (dataSource.getHibDialectClass().contains(DIALECT_ORACLE) ||
				dataSource.getHibDialectClass().contains(DIALECT_ORACLE9i10g)){
			query += " select sum(num_rows*avg_row_len) as size " +
					 " from all_tables " + 
					 " where table_name like '"+ tableNamePrefix +"%'";
		}else{
			//get approximate dimension
//			for (int i=0, l=cacheRegistry.size(); i<l; i++){
//				//cfr delete all 
//				query = " select * from " + cacheRegistry.get(key);
//			}
		}
		logger.debug("Defined query: " +query);
		IDataStore dataStore  = dataSource.executeStatement(query, 0, 0);
		DataStore ds = (DataStore) dataStore;
		BigDecimal size = null;
		if (ds.getRecordsCount() > 0){
			IRecord rec = ds.getRecordAt(0);
			for (int i=0, l=rec.getFields().size(); i<l; i++){		
				IField field = rec.getFieldAt(i);
				size = (BigDecimal)field.getValue();
			}
		}
		logger.debug("Size of object cached: " + size);
		if (size != null) dimensionSpaceFree = dimensionSpaceFree.subtract(size);
		return dimensionSpaceFree;
	}	
	
	/**
	 * Returns the number of bytes used by the resultSet (approximate)
	 */
	public BigDecimal getDimensionSpaceUsed(IDataStore resultset){
		BigDecimal rowWeight = getRowWeight(resultset.getRecordAt(0), resultset.getMetaData());
		dimensionSpaceUsed = rowWeight.multiply(new BigDecimal(resultset.getRecordsCount())) ;
		logger.debug("Dimension stimated for the new resultset [rowWeight*rows]: " + dimensionSpaceUsed + " ["+rowWeight+" * "+resultset.getRecordsCount()+"]");
		return dimensionSpaceUsed;
	}
	

	public Integer getNumberOfObjects(){
		//TODO
		return null;
	}
	
	public boolean isActiveCleanAction(){ 
		return isActiveCleanAction;
	} 
	
	public boolean hasSpaceForResultSet(IDataStore resultset){
		if (getDimensionSpaceAvailable().compareTo(getDimensionSpaceUsed(resultset)) <= 0){
			return false;
		}else{
			return true;
		}
	}

	public List getObjectsByDimension(){
		//TODO
		return null;
	}
	

	public List getObjectsByTime(){
		//TODO
		return null;
	}

	private BigDecimal getBytesForType(String type){
		BigDecimal toReturn = new BigDecimal(8); //for default sets a generic Object size
		for (int i=0, l=objectsDimension.size(); i<l; i++){
			String typeName = ((Properties)objectsDimension.get(i)).getProperty("name");
			if (type.contains(typeName)){
				toReturn = new BigDecimal(((Properties)objectsDimension.get(i)).getProperty("bytes"));
				logger.debug("Used configurated type: " + type + " - weight: " + toReturn.toString());
				break;
			}
		}
		return toReturn;		
	}
	
	private Map<String, Integer> getColumnSize() {
		return columnSize;
	}
	
	private BigDecimal getRowWeight(IRecord record, IMetaData md){
		BigDecimal toReturn = new BigDecimal(0);
		
		for (int i=0, l=record.getFields().size(); i<l; i++){		
			IFieldMetaData fmd = md.getFieldMeta(i);
			// in case of a measure with String type, convert it into a Double
			if (fmd.getFieldType().equals(FieldType.MEASURE) && fmd.getType().toString().contains("String")) {
				fmd.setType(java.lang.Double.class);
				logger.debug("Column type is string but the field is measure: converting it into a double");	
			}else if(fmd.getType().toString().contains("[B")) {  //BLOB		
				//TODO something?
			}else if(fmd.getType().toString().contains("[C")) {	 //CLOB				
				//TODO something?
			}

			toReturn = toReturn.add(getBytesForType(fmd.getType().toString()));			
		}

		return toReturn;
	}
}
