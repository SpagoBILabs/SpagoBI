/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.persist.temporarytable;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class NativeTypeTranslatorFactory {
	
	public static final String DRIVER_ORACLE = "Oracle";
	public static final String DRIVER_SQLSERVER = "mssqlserver4";
	public static final String DRIVER_SQLSERVERMICROSOFT_WITH_SPACE = "SQL Server";
	public static final String DRIVER_SQLSERVERMICROSOFT = "SQLServer";
	public static final String DRIVER_SQLSERVERSPRINTA = "TdsDriver";
	public static final String DRIVER_SQLSERVERJTURBO = "jturbo";
	public static final String DRIVER_DB2 = "DB2";
	public static final String DRIVER_MYSQL = "MySQL";
	public static final String TEMPORARY_TABLE_NATIVE_TYPES_CLASS = "TEMPORARY_TABLE_NATIVE_TYPES_CLASS";
	
	public static INativeDBTypeable getInstance(String driverName){
//		String className = getProperty( TEMPORARY_TABLE_NATIVE_TYPES_CLASS );
//		INativeDBTypeable toReturn;
//		try {
//			toReturn = (INativeDBTypeable)Class.forName(className).newInstance();
//		} catch (Exception e) {
//			throw new SpagoBIEngineRuntimeException("The db type mapping class "+ className +" is not supported yet for the temporary table creation..");
//		}
//		return toReturn;
		if(driverName.contains(DRIVER_DB2)){
			return new DB2TypeTranslator();
		}else if(driverName.contains(DRIVER_ORACLE)){
			return new OracleTypeTranslator();
		}else if(driverName.contains(DRIVER_SQLSERVER) || driverName.contains(DRIVER_SQLSERVERMICROSOFT) || driverName.contains(DRIVER_SQLSERVERMICROSOFT_WITH_SPACE) || driverName.contains(DRIVER_SQLSERVERSPRINTA) || driverName.contains(DRIVER_SQLSERVERJTURBO)){
			return new SqlServerTypeTranslator();
		}else if(driverName.contains(DRIVER_MYSQL) ){
			return new MySqlTypeTranslator();
		}
		throw new SpagoBIEngineRuntimeException("The db with dialect "+driverName+" is not supported yet for the temporary table creation..");
	}
	
	
	private static String getProperty(String propertName) {
		String propertyValue = null;		
		SourceBean sourceBeanConf;
		EnginConf engineConf = EnginConf.getInstance();
		Assert.assertNotNull(engineConf, "Impossible to parse engine-config.xml file");
		
		sourceBeanConf = (SourceBean) engineConf.getConfig().getAttribute( propertName);
		if(sourceBeanConf != null) {
			propertyValue  = (String) sourceBeanConf.getCharacters();
		}
		
		return propertyValue;		
	}

}
