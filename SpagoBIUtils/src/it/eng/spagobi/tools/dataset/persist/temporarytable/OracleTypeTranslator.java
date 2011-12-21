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

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class OracleTypeTranslator implements INativeDBTypeable{

	private static Logger logger = Logger.getLogger("OracleTypeTranslator");
	
	static final Integer MAX_CHAR_SIZE = 2000;
	
	private static Map<String, String> oracleTypeMapping;
	static{
		oracleTypeMapping = new HashMap<String, String>();
		oracleTypeMapping.put("java.lang.Integer", "NUMBER");
		oracleTypeMapping.put("java.lang.String", "CHAR");
		oracleTypeMapping.put("java.lang.String4001", "CLOB");
		oracleTypeMapping.put("java.lang.Boolean", "VARCHAR2(1)");
		oracleTypeMapping.put("java.lang.Float", "NUMBER");
		oracleTypeMapping.put("java.lang.Double", "NUMBER");
		oracleTypeMapping.put("java.util.Date", "DATE");
		oracleTypeMapping.put("java.sql.Date", "DATE");
		oracleTypeMapping.put("java.sql.Timestamp", "TIMESTAMP");
		oracleTypeMapping.put("oracle.sql.TIMESTAMP", "TIMESTAMP");
		oracleTypeMapping.put("java.math.BigDecimal", "NUMBER");
	}
	

	@SuppressWarnings("rawtypes")
	public String getNativeTypeString(String typeJavaName, Map properties) {
		logger.debug("Translating java type "+typeJavaName+" with properties "+properties);
		// convert java type in SQL type
		String queryType ="";
		String typeSQL ="";

		// proeprties
		Integer size = null;
		Integer precision = null;
		Integer scale = null;
		if(properties!=null){
			if(properties.get(SIZE) != null) 
				size = Integer.valueOf(properties.get(SIZE).toString());
			if(properties.get(PRECISION) != null) 
				precision = Integer.valueOf(properties.get(PRECISION).toString());
			if(properties.get(SCALE) != null) 
				scale = Integer.valueOf(properties.get(SCALE).toString());	
		}

		typeSQL = oracleTypeMapping.get(typeJavaName);


		// write Type
		queryType +=" "+typeSQL+""; 

		if(typeJavaName.equalsIgnoreCase(String.class.getName())){
			if(size>MAX_CHAR_SIZE){
				logger.error("For Oracle the max size of a char column must be < "+MAX_CHAR_SIZE+". The size you've specified is "+size);
				throw new SpagoBIRuntimeException("For Oracle the max size of a char column must be < "+MAX_CHAR_SIZE+". The size you've specified is "+size);
			}
			if( size != null && size!= 0){
				queryType +="("+size+")";
			}
		}else if(typeJavaName.equalsIgnoreCase(Integer.class.getName())     ||
				typeJavaName.equalsIgnoreCase(Double.class.getName()) ||
				typeJavaName.equalsIgnoreCase(Float.class.getName())){
			if(precision != null && scale != null){
				queryType+="("+precision+","+scale+")";
			}
		}
		logger.debug("The translated Oracle type is "+queryType);	
		queryType+=" ";
		return queryType;
	}

}
