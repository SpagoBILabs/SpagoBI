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
package utilities;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.dbaccess.SQLStatements;
import it.eng.spago.dbaccess.Utils;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.SQLCommand;
import it.eng.spago.dbaccess.sql.mappers.SQLMapper;
import it.eng.spago.dbaccess.sql.result.DataResult;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.exporter.Field;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class DataSourceUtilities {

	public static String SCHEMA = "schema";
	public static String STMT = "stmt";
	public static String NUM_PARS = "numPars";
	
	private static transient Logger logger = Logger.getLogger(DataSourceUtilities.class);
	private IDataSource datasource = null;
	
	public DataSourceUtilities(){
		//default
	}

	public DataSourceUtilities(IDataSource ds){		
		datasource = ds;		
		Assert.assertNotNull(datasource, "IDatasource object cannot be null!");
	}
	
	/**  This method gets all request parameters and define an hashmap object
	 * 
	 * @param request the sourcebean with the request
	 * 
	 * @return the hashmap with all parameters
	 * 
	 */
	public HashMap  getAttributesAsMap(SourceBean request){
		logger.debug("IN");
		
		HashMap<String , Object> params = new HashMap <String , Object>();
		List reqParams = (List)request.getContainedAttributes();
		Iterator it = reqParams.iterator();
		while (it.hasNext()) {
			SourceBeanAttribute param = (SourceBeanAttribute)it.next();
			String paramName = param.getKey();
			String paramValue;
			try {
				paramValue = (String) param.getValue();
			} catch (Exception e) {
				logger.debug("OUT");
				logger.error("Impossible read value for the parameter [" + paramName + "] into request's content", e);
				throw new SpagoBIServiceException("", "Impossible read value for the parameter [" + paramName + "] into request's content", e);
			}
			params.put(paramName, paramValue);
		}
		
		logger.debug("OUT");
		return params;
	}
	
	/** This method execute an update query on a generic db with Spago instructions.
	 * 
	 * @param the hashmap with all parameters
	 * 
	 * @return a boolean value with the response of the operation.
	 * 
	 */
	public boolean executeUpdateQuery(LinkedHashMap<String , Object> params, JSONObject metaParams) throws Throwable, Exception{
		boolean toReturn = true;
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		
		logger.debug("IN");
		
		try {

			Connection jdbcConnection = datasource.getConnection();
			jdbcConnection.setAutoCommit(false);
			dataConnection = getDataConnection(jdbcConnection);

			String statement = SQLStatements.getStatement((String)params.get( STMT )); 
			logger.debug("Parameter [" + STMT + "] is equals to [" + statement + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( statement ), "Parameter [" + STMT + "] cannot be null or empty");	
			
			statement = substituteListValuesInStmt(statement, params, metaParams );
			logger.debug ("Statement after substitution of list of values (placeholder '$'):  " + statement);
			
			String numParsStr = (String) params.get( NUM_PARS );
			int numPars = (numParsStr != null)?Integer.parseInt(numParsStr):0;
			logger.debug("Parameter [ numPars ] is equals to [" + numPars + "]");
			
			sqlCommand = dataConnection.createUpdateCommand(statement);
			dataConnection.initTransaction();
			
			if (numPars > 0){
				List inputParameter = new ArrayList(numPars);

				if (metaParams == null){	
					Assert.assertTrue(metaParams == null, "Parameter [" + metaParams + "] cannot be null or empty.");					
				}else{
					JSONArray queryPars = (JSONArray)metaParams.get("queryParams");
					//for (int j=0; j<numPars; j++){
					for (int j=0; j<queryPars.length(); j++){
						JSONObject obj = (JSONObject)queryPars.get(j);
						if(!obj.isNull("name")){
							String paramType = (String)obj.get("type");
							String paramName = (String)obj.get("name");
							String paramValue = (String)params.get(paramName);
							//if value isn't valorized, checks the defualt (if it's defined into meta section)
							if (paramValue == null){ 
								try{
									paramValue =  (String)obj.get("default");
								}catch(JSONException je ){
									logger.error("param " + paramName + "in JSON template not found. Parameter value is null!");
									paramValue = null;
								}
							}
							logger.debug("Parameter " + paramName + "  is equals to [" + paramValue + "]");
							inputParameter.add(dataConnection.createDataField(paramName,getParamType(paramType), paramValue));
						}
					}	
				}				
				dataResult = sqlCommand.execute(inputParameter);
			}else{
				dataResult = sqlCommand.execute();
			}
			dataConnection.commitTransaction(); 
		} // try
		catch (Exception ex) {
			toReturn = false;
			logger.error("QueryExecutor::executeQuery:", ex);
			try{
				dataConnection.rollBackTransaction();
			} catch (Throwable t) {
				toReturn = false;
				throw new Throwable(t);
			}
			throw new Throwable(ex);
			
		} 
		finally {
			Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		} // finally try
		
		return toReturn;
	}


	
	
	private int getParamType(String parType){
		int toReturn = 0;
		if (parType.equalsIgnoreCase("num") || parType.equalsIgnoreCase("integer")) return java.sql.Types.INTEGER;	
		else if (parType.equalsIgnoreCase("decimal") ) return java.sql.Types.DECIMAL;	
		else if (parType.equalsIgnoreCase("double")) return java.sql.Types.DOUBLE;
		else if (parType.equalsIgnoreCase("string") || parType.equalsIgnoreCase("char")) return java.sql.Types.VARCHAR;
		else if (parType.equalsIgnoreCase("boolean") ) return java.sql.Types.BOOLEAN;
		else if (parType.equalsIgnoreCase("date") || parType.equalsIgnoreCase("datetime")) return java.sql.Types.DATE;
		return toReturn;
	}
	
	private int getParamType(String colName, JSONObject metaParams) throws Throwable, Exception{
		logger.debug("IN");
		int toReturn = 0;

		try{
			if (metaParams !=  null){
				JSONArray queryPars = (JSONArray)metaParams.get("queryParams");
				for (int i=0; i<queryPars.length(); i++){
					JSONObject obj = (JSONObject)queryPars.get(i);
					String type = (String)obj.get("name");
					if (type.equalsIgnoreCase(colName)){
						toReturn = getParamType(type);
					}
				}
			}
		} catch (Throwable t) {
			throw new Throwable(t);
		} finally {
			logger.debug("OUT");
		}
	
		return toReturn;
	}
	
	
	/**
	 * Creates a Spago DataConnection object starting from a sql connection.
	 * 
	 * @param con Connection to the export database
	 * 
	 * @return The Spago DataConnection Object
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public DataConnection getDataConnection(Connection con) throws EMFInternalError {
		DataConnection dataCon = null;
		try {
			Class mapperClass = Class.forName("it.eng.spago.dbaccess.sql.mappers.OracleSQLMapper");
			SQLMapper sqlMapper = (SQLMapper)mapperClass.newInstance();
			dataCon = new DataConnection(con, "2.1", sqlMapper);
		} catch(Exception e) {
			logger.error("Error while getting Data Source " + e);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "cannot build spago DataConnection object");
		}
		return dataCon;
	}
	
	public Vector readFields(String query) throws Exception {
		Vector queryFields = new Vector();
        
		PreparedStatement ps = null;
		
        try {
        	
        	Connection connection = datasource.getConnection();
        	String queryToUpperCase = query.toUpperCase();
        	
        	if ( queryToUpperCase.indexOf("GROUP BY") > 0 ){
        		String before = query.substring(0, queryToUpperCase.indexOf("GROUP BY"));
        		String after =query.substring(queryToUpperCase.indexOf("GROUP BY")); 
        		
        		if (( before.indexOf("where") > 0) || (before.indexOf("WHERE") > 0)){
        			before += " and 1 = 0 ";
        		}else{
        			before += " where 1 = 0 ";
        		}
        		
        		
        		query = before + after; 
        	
        	}else if ( queryToUpperCase.indexOf("ORDER BY") > 0 ){
        		
        		String before = query.substring(0, queryToUpperCase.indexOf("ORDER BY"));
        		String after =query.substring(queryToUpperCase.indexOf("ORDER BY")); 
        		
        		if (( before.indexOf("where") > 0) || (before.indexOf("WHERE") > 0)){
        			before += " and 1 = 0 ";
        		}else{
        			before += " where 1 = 0 ";
        		}	
        		query = before + after;
        	}else{
        		if (( query.indexOf("where") > 0) || (query.indexOf("WHERE") > 0)){
        			query += " and 1 = 0 ";
        		}else{
        			query += " where 1 = 0 ";
        		}        		
        	}
        	
        	ps = connection.prepareStatement( query );
             
            // Some JDBC drivers don't supports this method...
            try { ps.setFetchSize(0); } catch(Exception e ) {}
             
             
             ResultSet rs = ps.executeQuery();             
             ResultSetMetaData rsmd = rs.getMetaData();             
             
             List columns = new ArrayList();
             for (int i=1; i <= rsmd.getColumnCount(); ++i) {
            	 Field field = new Field(
                         rsmd.getColumnLabel(i), 
                         getJdbcTypeClass(rsmd, i),
                         rsmd.getColumnDisplaySize(i));
                 
            	 queryFields.add( field );
             }
         }
         catch(Exception e) {
        	 e.printStackTrace();
         }
		
		
		return queryFields;
	}
	public static String getJdbcTypeClass(ResultSetMetaData rsmd, int t ) {
        String cls = "java.lang.Object";

        try {
            cls = rsmd.getColumnClassName(t);
            cls =  Field.getFieldType(cls);

        } catch (Exception ex) {
            // if getColumnClassName is not supported...
            try {
                int type = rsmd.getColumnType(t);
                switch( type ) {
                        case java.sql.Types.TINYINT:
                        case java.sql.Types.BIT:
                                cls = "java.lang.Byte";
                                break;
                        case java.sql.Types.SMALLINT:
                                cls = "java.lang.Short";
                                break;
                        case java.sql.Types.INTEGER:
                                cls = "java.lang.Integer";
                                break;
                        case java.sql.Types.FLOAT:
                        case java.sql.Types.REAL:
                        case java.sql.Types.DOUBLE:
                        case java.sql.Types.NUMERIC:
                        case java.sql.Types.DECIMAL:
                                cls = "java.lang.Double";
                                break;
                        case java.sql.Types.CHAR:
                        case java.sql.Types.VARCHAR:
                                cls = "java.lang.String";
                                break;

                        case java.sql.Types.BIGINT:
                                cls = "java.lang.Long";
                                break;
                        case java.sql.Types.DATE:
                                cls = "java.util.Date";
                                break;
                        case java.sql.Types.TIME:
                                cls = "java.sql.Time";
                                break;
                        case java.sql.Types.TIMESTAMP:
                                cls = "java.sql.Timestamp";
                                break;
                }
            } catch (Exception ex2){
                ex2.printStackTrace();
            }
        }
        return cls;
	 }
	
	/**
	 * Substitute the placeholder $ into the statement with the list of values presents into params
	 * following the positional substitution type.
	 * 
	 * @param stmt the statement
	 * @param params the map of parameters (list too)
	 * @param metaParams the metaparams obj where the type of the parameter could be: string, char, num, date, list...
	 * 
	 * @return The statement replaced of the $ chars with the list of values
	 * 
	 * @throws Exception
	 */
	private String substituteListValuesInStmt(String stmt, LinkedHashMap<String , Object> params, JSONObject metaParams)
		throws Exception {
		JSONArray metaPars = null;
		
		if (stmt == null || stmt.indexOf("$") < 0) return stmt;
		if (metaParams == null) return stmt;
		
		metaPars = (JSONArray)metaParams.get("queryParams");
		
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(stmt);
		while(st.hasMoreTokens()){
			String tok = st.nextToken();
			if(tok.indexOf("$") != -1){
				String preTok = tok.substring(0,tok.indexOf("$"));
				String postTok = tok.substring(tok.indexOf("$")+1);
				String newValues = "";
				if (preTok != null) newValues += preTok;
				newValues += getParamListValue(metaPars, params);
				if (postTok != null) newValues += postTok;
				if (newValues != null ){
					sb.append(newValues);
					sb.append(" ");
				}								
			}else{
				sb.append(tok);
				sb.append(" ");
			}
		}
		return  sb.toString();
	}
	
	private String getParamListValue(JSONArray metaPars,  LinkedHashMap<String , Object> pars) throws Exception{
		String toReturn = "";
		try{
			for (int i=0, l=metaPars.length(); i<l; i++) {
				JSONObject obj = (JSONObject)metaPars.get(i);				
				if (obj == null || obj.isNull("name")) continue;
				logger.debug("Get param value for : "  + (String)obj.get("name") + " . Its type is defined as : " + obj.get("type"));
				String paramType = (String)obj.get("type");		
				if (paramType.toUpperCase().startsWith("LIST")){
					ArrayList values = new ArrayList();
					String paramName = (String)obj.get("name");
					Object objValues = pars.get(paramName);
					if (objValues instanceof ArrayList )
						values = (ArrayList)pars.get(paramName);
					else //monovalue:
						values.add((String)pars.get(paramName));
					
					if (values != null) {
						String tmpReturn = (values.toString()).substring(1,(values.toString()).indexOf("]"));
						//if the values must be string adds the ' char to each single value
						String realType = (paramType.indexOf("_") != -1)?paramType.substring(paramType.indexOf("_") +1 ):"";
						if (realType.equalsIgnoreCase("STRING")){
							String[] strValues = tmpReturn.split(",");
							for (int j=0, l2 = strValues.length; j < l2; j++){
								toReturn += "'" + strValues[j].trim() + "'" + ((j <  (l2-1))?",":"");
							}							
						}else {
							toReturn = tmpReturn;	
						}
						//forces the default values if it isn't present into the request
						if (toReturn == null || toReturn.equals("") || toReturn.equals("''") || 
							toReturn.equalsIgnoreCase("'null'")){
							if (realType.equalsIgnoreCase("STRING")){
								toReturn = "'" + (String)obj.get("default") +"'";
							}else{
								toReturn = (String)obj.get("default");
							}
						}
						
					}
					pars.remove(paramName);
					//clean the meta param just managed
					obj = new JSONObject();
					metaPars.put(i, obj);
					break;
				}
			}
		}catch (Exception e){
			logger.error("Error while replacing parameters in value: " + e.getMessage());
			throw e;
		}
		return toReturn;
	}

}
