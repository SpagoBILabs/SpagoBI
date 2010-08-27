package it.eng.spagobi.engines.qbe.services.core;

import it.eng.qbe.model.LookupStoreJSONSerializer;
import it.eng.qbe.query.ExpressionNode;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.query.serializer.QuerySerializationConstants;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.QbeDataSet;
import it.eng.qbe.statment.hibernate.HQLStatement;
import it.eng.qbe.statment.hibernate.HQLStatement.IConditionalOperator;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class GetValuesForQbeFilterLookup  extends AbstractQbeEngineAction{
	
	public static final String SERVICE_NAME = "GET_VALUES_FOR_QBE_FILTER_LOOKUP_ACTION";
	
	// request parameters
	public static String ENTITY_ID = "ENTITY_ID";
	public static String FILTERS = "FILTERS";	
	public static String MODE = "MODE";
	public static String MODE_SIMPLE = "simple";
	public static String MODE_COMPLETE = "complete";
	public static String START = "start";
	public static String LIMIT = "limit";
	// logger component
	private static Logger logger = Logger.getLogger(GetValuesForQbeFilterLookup.class);
	
	public void service(SourceBean request, SourceBean response) {		
		String entityId = null;
		Integer limit = null;
		Integer start = null;
		Integer maxSize = null;
		boolean isMaxResultsLimitBlocking = false;
		IDataStore dataStore = null;
		QbeDataSet dataSet = null;
		JSONDataWriter serializer;
		LookupStoreJSONSerializer serializer2;
		JSONObject filtersJSON = null;
		Query query = null;
		IStatement statement = null;
		
		Integer resultNumber = null;
		JSONObject gridDataFeed = new JSONObject();
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
		
		logger.debug("IN");
		
		try {
			
			super.service(request, response);	
		
			totalTimeMonitor = MonitorFactory.start("QbeEngine.GetValuesForQbeFilterLookup.totalTime");
			
			entityId = getAttributeAsString( ENTITY_ID );
			if(this.requestContainsAttribute( FILTERS ) ) {
				filtersJSON = getAttributeAsJSONObject( FILTERS );
			}
			query = buildQuery(entityId, filtersJSON);
			statement = getDatamartModel().createStatement( query );
			
			statement.setParameters( getEnv() );
			
			String hqlQuery = statement.getQueryString();
			String sqlQuery = ((HQLStatement)statement).getSqlQueryString();
			logger.debug("Executable query (HQL): [" +  hqlQuery+ "]");
			logger.debug("Executable query (SQL): [" + sqlQuery + "]");
			
			start = getAttributeAsInteger( START );
			limit = getAttributeAsInteger( LIMIT );
			
			logger.debug("Parameter [" + ENTITY_ID + "] is equals to [" + entityId + "]");
			logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
			logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
			
			Assert.assertNotNull(entityId, "Parameter [" + ENTITY_ID + "] cannot be null" );
		
			try {
				logger.debug("Executing query ...");
				dataSet = new QbeDataSet(statement);
				dataSet.setAbortOnOverflow(true);
				
				Map userAttributes = new HashMap();
				UserProfile profile = (UserProfile)this.getEnv().get(EngineConstants.ENV_USER_PROFILE);
				Iterator it = profile.getUserAttributeNames().iterator();
				while(it.hasNext()) {
					String attributeName = (String)it.next();
					Object attributeValue = profile.getUserAttribute(attributeName);
					userAttributes.put(attributeName, attributeValue);
				}
				dataSet.addBinding("attributes", userAttributes);
				dataSet.addBinding("parameters", this.getEnv());
				dataSet.loadData(start, limit, (maxSize == null? -1: maxSize.intValue()));
				
				dataStore = dataSet.getDataStore();
				Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			} catch (Exception e) {
				logger.debug("Query execution aborted because of an internal exceptian");
				SpagoBIEngineServiceException exception;
				String message;
				
				message = "An error occurred in " + getActionName() + " service while executing query: [" +  statement.getQueryString() + "]";				
				exception = new SpagoBIEngineServiceException(getActionName(), message, e);
				exception.addHint("Check if the query is properly formed: [" + statement.getQueryString() + "]");
				exception.addHint("Check connection configuration");
				exception.addHint("Check the qbe jar file");
				
				throw exception;
			}
			logger.debug("Query executed succesfully");
			
			resultNumber = (Integer)dataStore.getMetaData().getProperty("resultNumber");
			Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			logger.debug("Total records: " + resultNumber);			
			
			//serializer = new DataStoreJSONSerializer();
			//gridDataFeed = (JSONObject)serializer.serialize(dataStore);
			
			serializer2 = new LookupStoreJSONSerializer();
			gridDataFeed = (JSONObject)serializer2.serialize(dataStore);
			
			try {
				writeBackToClient( new JSONSuccess(gridDataFeed) );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch(Throwable t) {
			errorHitsMonitor = MonitorFactory.start("QbeEngine.errorHits");
			errorHitsMonitor.stop();
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			if (totalTimeMonitor != null) totalTimeMonitor.stop();
			logger.debug("OUT");
		}		
	}
	
	private Query buildQuery(String fieldUniqueName, JSONObject filtersJSON) throws JSONException {
		logger.debug("IN: fieldUniqueName = " + fieldUniqueName);
		Query query = new Query();
		query.addSelectFiled(fieldUniqueName, "NONE", "Valori", true, true, false, "asc", null);
		query.setDistinctClauseEnabled(true);
		if (filtersJSON != null) {
			
			ExpressionNode whereClauseStructure = new ExpressionNode("NODE_CONST", "$F{Filter1}");
			query.setWhereClauseStructure(whereClauseStructure);
			String valuefilter = (String) filtersJSON.get(SpagoBIConstants.VALUE_FILTER);
			String typeFilter = (String) filtersJSON.get(SpagoBIConstants.TYPE_FILTER);
			String typeValueFilter = (String) filtersJSON.get(SpagoBIConstants.TYPE_VALUE_FILTER);
			WhereField.Operand leftOperand = new WhereField.Operand(new String[] {fieldUniqueName}, "", HQLStatement.OPERAND_TYPE_FIELD, null, null);
			valuefilter = typeValueFilter.equalsIgnoreCase("NUMBER") ? valuefilter : "" + valuefilter + "";
			WhereField.Operand rightOperand = new WhereField.Operand(new String[] {valuefilter}, 
					"", HQLStatement.OPERAND_TYPE_STATIC, null, null);
			query.addWhereField("Filter1", "", false, leftOperand, typeFilter, rightOperand, "AND");
			
		}
		logger.debug("OUT");
		return query;
	}
		

}
