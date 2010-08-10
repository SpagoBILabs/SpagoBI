/**
 * 
 * LICENSE: see COPYING file. 
 * 
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
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	public boolean executeUpdateQuery(Map<String , Object> params, JSONObject metaParams) throws Throwable, Exception{
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
					//for (int j=0; j<queryPars.length(); j++){
					for (int j=0; j<numPars; j++){
						JSONObject obj = (JSONObject)queryPars.get(j);
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
						inputParameter.add(dataConnection.createDataField(paramName,getParamType(paramType), paramValue));							
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
	
	
}
