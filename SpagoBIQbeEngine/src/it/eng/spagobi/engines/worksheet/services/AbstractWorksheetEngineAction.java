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
package it.eng.spagobi.engines.worksheet.services;

import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.bo.Sheet;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public abstract class AbstractWorksheetEngineAction extends AbstractEngineAction {
	
	private static final long serialVersionUID = 6446776217192515816L;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(AbstractWorksheetEngineAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
    public IDataStore executeWorksheetQuery (String worksheetQuery, Integer start, Integer limit) {
    	
    	IDataStore dataStore = null;
    	
//		if (!TemporaryTableManager.isEnabled()) {
//			logger.warn("TEMPORARY TABLE STRATEGY IS DISABLED!!! " +
//				"Using inline view construct, therefore performance will be very low");			
//			dataStore = useInLineViewStrategy(worksheetQuery, baseQuery, start, limit);
//		} else {
//			logger.debug("Using temporary table strategy....");			
//			dataStore = useTemporaryTableStrategy(worksheetQuery, baseQuery,
//					start, limit);
//		}

		logger.debug("Using temporary table strategy....");			
		dataStore = useTemporaryTableStrategy(worksheetQuery, start, limit);
		
		Assert.assertNotNull(dataStore, "The dataStore cannot be null");
		logger.debug("Query executed succesfully");
		
		Integer resultNumber = (Integer) dataStore.getMetaData().getProperty("resultNumber");
		Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by queryTemporaryTable method of the class [" + TemporaryTableManager.class.getName()+ "] cannot be null");
		logger.debug("Total records: " + resultNumber);			
		
		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
		Integer maxSize = QbeEngineConfig.getInstance().getResultLimit();
		boolean overflow = maxSize != null && resultNumber >= maxSize;
		if (overflow) {
			logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
			auditlogger.info("[" + userProfile.getUserId() + "]:: max result limit [" + maxSize + "] exceeded with SQL: " + worksheetQuery);
		}
		
		return dataStore;
    }

	private IDataStore useTemporaryTableStrategy(String worksheetQuery,
			Integer start, Integer limit) {
		
		IDataStore dataStore = null;
		
		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
//		ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
//		DataSource dataSource = getDataSource(connection);
		IDataSource dataSource = getDataSource();
		
//		logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (SQL): [" + baseQuery + "]");
		logger.debug("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + worksheetQuery + "]");
		
//		auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: SQL: " + baseQuery);
		auditlogger.info("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + worksheetQuery + "]");
		
		try {
			dataStore = TemporaryTableManager.queryTemporaryTable(worksheetQuery, dataSource, start, limit);
		} catch (Exception e) {
			logger.debug("Query execution aborted because of an internal exception");
			String message = "An error occurred in " + getActionName() + " service while querying temporary table";				
			SpagoBIEngineServiceException exception = new SpagoBIEngineServiceException(getActionName(), message, e);
//			exception.addHint("Check if the base query is properly formed: [" + baseQuery + "]");
			exception.addHint("Check if the crosstab's query is properly formed: [" + worksheetQuery + "]");
			exception.addHint("Check connection configuration: connection's user must have DROP and CREATE privileges");
			throw exception;
		}
		return dataStore;
	}

//	private IDataStore useInLineViewStrategy(String worksheetQuery,
//			String baseQuery, Integer start, Integer limit) {
//
//		IDataStore dataStore = null;
//		
//		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
//		ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
//		DataSource dataSource = getDataSource(connection);
//		
//		int beginIndex = worksheetQuery.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
//		int endIndex = worksheetQuery.indexOf(" ", beginIndex);
//		String inlineSQLQuery = worksheetQuery.substring(0, beginIndex) + " ( " + baseQuery + " ) TEMP " + worksheetQuery.substring(endIndex);
//		logger.debug("Executable query for user [" + userProfile.getUserId() + "] (SQL): [" + inlineSQLQuery + "]");
//		auditlogger.info("[" + userProfile.getUserId() + "]:: SQL: " + inlineSQLQuery);
//		JDBCDataSet dataSet = new JDBCDataSet();
//		dataSet.setDataSource(dataSource);
//		dataSet.setQuery(inlineSQLQuery);
//		if (start != null && limit != null) {
//			dataSet.loadData(start, limit, -1);
//		} else {
//			dataSet.loadData();
//		}
//		dataStore = (DataStore) dataSet.getDataStore();
//		return dataStore;
//	}
	
    public WorksheetEngineInstance getEngineInstance() {
    	return (WorksheetEngineInstance) getAttributeFromSession( WorksheetEngineInstance.class.getName() );
    }
    
    public void setEngineInstance(WorksheetEngineInstance engineInstance) {
    	setAttributeInSession( WorksheetEngineInstance.class.getName() , engineInstance );
    }
    
	public IDataSource getDataSource() {
		WorksheetEngineInstance engineInstance  = getEngineInstance();
    	if (engineInstance == null) {
    		return null;
    	}
    	return engineInstance.getDataSource();
	}

	public void setDataSource(IDataSource dataSource) {
		WorksheetEngineInstance engineInstance  = getEngineInstance();
    	if (engineInstance == null) {
    		return;
    	}
    	engineInstance.setDataSource(dataSource);
	}
	
	public String getTemporaryTableName() {
		UserProfile userProfile = (UserProfile) getEnv().get(EngineConstants.ENV_USER_PROFILE);
		return TemporaryTableManager.getTableName(userProfile.getUserId().toString());
	}
	
	public IDataSetTableDescriptor persistDataSet(String tableName) {
		WorksheetEngineInstance engineInstance = getEngineInstance();
		IDataSet dataset = engineInstance.getDataSet();
		String signature = dataset.getSignature();
		if (signature.equals(TemporaryTableManager.getLastDataSetSignature(tableName))) {
			// signature matches: no need to create a TemporaryTable
			return TemporaryTableManager.getLastDataSetTableDescriptor(tableName);
		}
		Connection connection = getConnection();
		//drop the temporary table if one exists
		try {
			TemporaryTableManager.dropTableIfExists(tableName, getEngineInstance().getDataSource());
		} catch (Exception e) {
			logger.error("Impossible to drop the temporary table with name "+tableName, e);
			throw new SpagoBIEngineRuntimeException("Impossible to drop the temporary table with name "+tableName, e);
		}
		
		IDataSetTableDescriptor td = dataset.persist(tableName, connection);
		TemporaryTableManager.setLastDataSetTableDescriptor(tableName, td);
		return td;
	}

	public Connection getConnection() {
		try {
			return this.getDataSource().getConnection();
		} catch (Exception e) {
			throw new SpagoBIEngineRuntimeException("Cannot get connection to datasource", e);
		}
	}
	
	public Map<String, List<String>> getFiltersOnDomainValues() {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		Map<String, List<String>> toReturn = workSheetDefinition.getFiltersOnDomainValues();
		return toReturn;
	}
	
	public Map<String, List<String>> getSheetFiltersOnDomainValues(String sheetName) {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		Sheet sheet = workSheetDefinition.getSheet(sheetName);
		List<Attribute> sheetFilters = sheet.getFiltersOnDomainValues();
		Map<String, List<String>> toReturn = new HashMap<String, List<String>>();
		Iterator<Attribute> it = sheetFilters.iterator();
		while (it.hasNext()) {
			Attribute attribute = it.next();
			toReturn.put(attribute.getEntityId(), attribute.getValuesAsList());
		}
		return toReturn;
	}
	
	public Map<String, List<String>> getGlobalFiltersOnDomainValues() {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		Map<String, List<String>> toReturn = workSheetDefinition.getGlobalFiltersAsMap();
		return toReturn;
	}
    
}