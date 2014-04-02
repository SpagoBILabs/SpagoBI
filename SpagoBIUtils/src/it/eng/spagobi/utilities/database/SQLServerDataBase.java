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
package it.eng.spagobi.utilities.database;

import org.apache.log4j.Logger;

import it.eng.spagobi.tools.datasource.bo.IDataSource;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SQLServerDataBase extends AbstractDataBase {
	
	private static transient Logger logger = Logger.getLogger(SQLServerDataBase.class);
	
	public SQLServerDataBase(IDataSource dataSource) {
		super(dataSource);
	}
	
	public String getDataBaseType(Class javaType) {
		String toReturn = null;
		String javaTypeName = javaType.toString();
		if (javaTypeName.contains("java.lang.String")){
			toReturn = " VARCHAR (" + getVarcharLength() + ")";
		} else if (javaTypeName.contains("java.lang.Short")) {
			toReturn = " INTEGER ";
		} else if (javaTypeName.contains("java.lang.Integer")){
			toReturn = " INTEGER ";			
		} else if (javaTypeName.contains("java.lang.Long")){
			toReturn = " NUMERIC ";	
		} else if (javaTypeName.contains("java.lang.BigDecimal") || javaTypeName.contains("java.math.BigDecimal")){
			toReturn = " NUMERIC ";	
		} else if (javaTypeName.contains("java.lang.Double")){
			toReturn = " NUMERIC ";	
		} else if (javaTypeName.contains("java.lang.Float")){
			toReturn = " NUMERIC ";	
		} else if (javaTypeName.contains("java.lang.Boolean")){
			toReturn = " BIT ";	
		} else if (javaTypeName.contains("java.sql.Date")){
			toReturn = " DATETIME ";	
		} else if (javaTypeName.contains("java.sql.Timestamp")){
			toReturn = " DATETIME ";	
		} else if (javaTypeName.contains("[B")){
			toReturn = " TEXT ";
		} else if (javaTypeName.contains("[C")){
			toReturn = " TEXT ";
		} else {
			logger.debug("Cannot map java type [" + javaTypeName + "] to a valid database type ");
		}
		
		return toReturn;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.database.IDataBase#getAliasDelimiter()
	 */
	public String getAliasDelimiter() {
		return "\""; 
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.database.AbstractDataBase#getUsedMemorySizeQuery(java.lang.String, java.lang.String)
	 * Returning null the used memory size wil be calculated by the abstract call using an heuristic not dependent by
	 * the database.
	 * 
	 * TODO: write a custom implementation also for qblServer
	 */
	public String getUsedMemorySizeQuery(String schema, String tableNamePrefix) {
		return null;
	}
}
