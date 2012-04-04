/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.dao.fileimpl.InLineFunctionsDAOFileImpl.InLineFunction;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesPunctualDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesRangeDescriptor;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.json.QuerySerializationConstants;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.qbe.statement.IStatementClause;
import it.eng.qbe.statement.StatementTockenizer;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.objects.Couple;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractJPQLStatementClause implements IStatementClause {
	
	JPQLStatement parentStatement;
	
	private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );
	public static transient Logger logger = Logger.getLogger(JPQLStatementSelectClause.class);
	
	public String parseInLinecalculatedField(InLineCalculatedSelectField cf, String slots, Query query, Map entityAliasesMaps){
		String newExpression;
		
		logger.debug("IN");
		
		newExpression = cf.getExpression();
		
		try {
			Assert.assertNotNull(parentStatement, "Class member [parentStatement] cannot be null in orser to properly parse inline calculated field expression [" + cf.getExpression() + "]");
			Assert.assertNotNull(cf.getExpression(), "Input parameter [espression] cannot be null");
			Assert.assertNotNull(query, "Input parameter [query] cannot be null");
			Assert.assertNotNull(entityAliasesMaps, "Input parameter [entityAliasesMaps] cannot be null");
			
			logger.debug("Parsing expression [" + cf.getExpression() + "] ...");
			newExpression = replaceFields(cf, false, query, entityAliasesMaps);
			newExpression = replaceInLineFunctions(newExpression, query, entityAliasesMaps);
			newExpression = replaceSlotDefinitions(newExpression, cf.getType(), slots, query, entityAliasesMaps);
			logger.debug("Expression [" + cf.getExpression() + "] paresed succesfully into [" + newExpression + "]");
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while parsing expression [" + cf.getExpression() + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return newExpression;
	}
	

	private String replaceFields(InLineCalculatedSelectField cf, boolean isTransientExpression, Query query, Map entityAliasesMaps) {
		String newExpression;
		IModelEntity rootEntity;
		IModelField modelField;
		String queryName;
		String rootEntityAlias;
		Map entityAliases;
		
		List<String> fieldQueryNames;
		List<String> fieldExpressionNames;
		
		logger.debug("IN");
		
		newExpression = cf.getExpression();
		
		entityAliases = (Map)entityAliasesMaps.get(query.getId());
		fieldQueryNames = new  ArrayList<String>();
		fieldExpressionNames = new  ArrayList<String>();
		
		try  {		
			StatementTockenizer tokenizer = new StatementTockenizer(cf.getExpression());
			while(tokenizer.hasMoreTokens()) {
				
				String token = tokenizer.nextTokenInStatement();
				logger.debug("Processing expression token [" + token + "] ...");
					
				modelField = null;
				String decodedToken = token;
				decodedToken = decodedToken.replaceAll("\\[", "(");
				decodedToken = decodedToken.replaceAll("\\]", ")");
				modelField = parentStatement.getDataSource().getModelStructure().getField(decodedToken);
			
				
				
				if(modelField != null) {
					if(cf.getType().equals("undefined")){
						if(modelField.getType().toLowerCase().contains("timestamp") || modelField.getType().toLowerCase().contains("date")){
							cf.setType("DATE");
						}
					}
					logger.debug("Expression token [" + token + "] references the model field whose unique name is [" + modelField.getUniqueName()+ "]");
					
					Couple queryNameAndRoot = modelField.getQueryName();
					queryName = (String) queryNameAndRoot.getFirst();
					logger.debug("select field query name [" + queryName + "]");
					
					if(queryNameAndRoot.getSecond()!=null){
						rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
					}else{
						rootEntity = modelField.getParent().getRoot(); 	
					}
					
					rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
					if(rootEntityAlias == null) {
						rootEntityAlias = parentStatement.getNextAlias(entityAliasesMaps);
						entityAliases.put(rootEntity.getUniqueName(), rootEntityAlias);
					}
					
					
					queryName = rootEntityAlias + "." + queryName;
					logger.debug("Expression token [" + token + "] query name is equal to [" + queryName + "]");
					
						
					fieldQueryNames.add(queryName);
					fieldExpressionNames.add(token);
				} else {
					logger.debug("Expression token [" + token + "] does not references any model field");
				}
				
				logger.debug("Expression token [" + token + "] succesfully processed");
			}
	
			int fieldIndex =0;
			int expressionCursorIndex = 0;
			tokenizer = new StatementTockenizer(cf.getExpression().replace("\'", ""));
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextTokenInStatement();
				expressionCursorIndex = newExpression.indexOf(token, expressionCursorIndex);
				if(fieldIndex < fieldExpressionNames.size() && fieldExpressionNames.get(fieldIndex).equals(token)){
					newExpression = newExpression.substring(0, expressionCursorIndex)+ fieldQueryNames.get(fieldIndex)+newExpression.substring(expressionCursorIndex+token.length());
					expressionCursorIndex = expressionCursorIndex + fieldQueryNames.get(fieldIndex).length();
					fieldIndex++;
				}else {
					expressionCursorIndex = expressionCursorIndex + token.length();
				}
			}
			
			if(cf.getType().equals("undefined")){
				cf.setType("STRING");
			}
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while parsing expression [" + cf.getExpression() + "]", t);
		} finally {
			logger.debug("OUT");
		}
		

		
		return newExpression;
	}
	

	
	
	
	private String replaceInLineFunctions(String expression, Query query, Map entityAliasesMaps) {
		String newExpression;
 
		HashMap<String, InLineFunction>  inlineFunctionsMap = getInlineFunctions();

		expression = expression.trim();
		
		if (expression.startsWith("(")) {
			expression = expression.substring(expression.indexOf("(")+1,expression.lastIndexOf(")"));
			expression = expression.trim();
		}
		
		//if is not a real function (ex. only a field) returns the expression in input
		if (expression.indexOf("(") < 0) return expression;

		String functionName = expression.substring(0, expression.indexOf("("));
		
		if (inlineFunctionsMap.get(functionName) == null) return expression;
		
		String functionCode = inlineFunctionsMap.get(functionName).getCode();
		newExpression = functionCode;
		
		//substitutes parameters in the new function code
		StatementTockenizer statementTockenizer = new StatementTockenizer(expression);
		int idx = 0;
		while(statementTockenizer.hasMoreTokens()){
			String alias = statementTockenizer.nextTokenInStatement();
			if (!alias.equalsIgnoreCase(functionName)) {
				newExpression = newExpression.replaceAll("\\$"+(idx+1), alias);
				idx++;
			}
		}
		
		return newExpression;
	}
	
	private HashMap<String, InLineFunction> getInlineFunctions() {
		HashMap<String, InLineFunction> inlineFunctionsMap;
		
		inlineFunctionsMap = null;
		try {
			Assert.assertNotNull(parentStatement, "[parentStatement] cannot be null");
			IDataSource dataSource = parentStatement.getDataSource();
			IDataSourceConfiguration dataSourceConfiguration = parentStatement.getDataSource().getConfiguration();
			
			ConnectionDescriptor connection = (ConnectionDescriptor)dataSourceConfiguration.loadDataSourceProperties().get("connection");	
			String dialect = connection.getDialect(); 
			
			inlineFunctionsMap = dataSourceConfiguration.loadInLineFunctions(dialect);
		} catch (Throwable t) {
			throw new RuntimeException("An unexpected error occured while getting dialect from datasource", t);
		}
		
		return inlineFunctionsMap;
	}
	
	
	
	
	
	
	private String replaceSlotDefinitions(String expr, String cfType, String s, Query query, Map entityAliasesMaps) {
		String newExpr;
		
		newExpr = null;
		
		try {
			if(s ==  null || s.trim().length() == 0) return expr;
			JSONArray slotsJSON = new JSONArray(s);
			List<Slot> slots = new ArrayList<Slot>();
			for(int i = 0; i < slotsJSON.length(); i++) {
				Slot slot = (Slot)SerializationManager.deserialize(slotsJSON.get(i), "application/json", Slot.class);
				slots.add(slot);
			}
			
			if(slots.isEmpty()) return expr;
			
			Slot defaultSlot = null;
			

			
			newExpr = "CASE";
			for(Slot slot : slots) {
				List<Slot.IMappedValuesDescriptor> descriptors =  slot.getMappedValuesDescriptors();
				if(descriptors == null || descriptors.isEmpty()) {
					defaultSlot = slot;
					continue;
				}
				for(Slot.IMappedValuesDescriptor descriptor : descriptors) {
					if(descriptor instanceof MappedValuesPunctualDescriptor) {
					
						MappedValuesPunctualDescriptor punctualDescriptor = (MappedValuesPunctualDescriptor)descriptor;
						newExpr += " WHEN (" + expr + ") IN (";
						String valueSeparator = "";
						Set<String> values = punctualDescriptor.getValues();
						for(String value : values) {
							if(cfType.equals("DATE")){
								newExpr += valueSeparator + parseDate(value);
							}else{
								newExpr += valueSeparator + "'" + value + "'";
							}
							
							valueSeparator = ", ";
						}
						newExpr += ") THEN '" + slot.getName() + "'";
						
					} else if(descriptor instanceof MappedValuesRangeDescriptor) {
						MappedValuesRangeDescriptor punctualDescriptor = (MappedValuesRangeDescriptor)descriptor;
						newExpr += " WHEN";
						String minCondition = null;
						String maxCondition = null;
						if(punctualDescriptor.getMinValue() != null) {

							minCondition = " (" + expr + ")";
							minCondition += (punctualDescriptor.isIncludeMinValue())? " >= " : ">";
							if(cfType.equals("DATE")){
								minCondition +=  parseDate(punctualDescriptor.getMinValue());
							}else{
								minCondition += punctualDescriptor.getMinValue();
							}
							
						}
						if(punctualDescriptor.getMaxValue() != null) {
							maxCondition = " (" + expr + ")";
							maxCondition += (punctualDescriptor.isIncludeMaxValue())? " <= " : "<";
							if(cfType.equals("DATE")){
								maxCondition +=  parseDate(punctualDescriptor.getMaxValue());
							}else{
								maxCondition += punctualDescriptor.getMaxValue();
							}
							
						}
						String completeCondition = "";
						if(minCondition != null) {
							completeCondition += "(" + minCondition + ")";
						}
						if(maxCondition != null) {
							completeCondition += (minCondition != null)? " AND " : "";
							completeCondition += "(" + maxCondition + ")";
						}
						newExpr += " " + completeCondition;
						newExpr += " THEN '" + slot.getName() + "'";
					} else {
						// ignore slot
					}
				
				}
			}
			if(defaultSlot != null) {
				newExpr += " ELSE '" + defaultSlot.getName() + "'";
			} else {
				newExpr += " ELSE (" + expr + ")";
			}
			newExpr += " END ";
		} catch (Throwable t) {
			logger.error("Impossible to add slots", t);
			return expr;
		}
		
		return newExpr;
	}
	
	/**
	 * Parse the date: get the user locale and format the date in the db format
	 * @param date the localized date
	 * @return the date in the db format
	 */
	protected String parseDate(String date){
		if (date==null || date.equals("")){
			return "";
		}
		
		String toReturn = "'" +date+ "'";
		
		Date operandValueToBoundDate = null;
		try {			
			operandValueToBoundDate = TIMESTAMP_FORMATTER.parse(date);
			DateFormat stagingDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS");	
			toReturn = stagingDataFormat.format(operandValueToBoundDate);
		} catch (ParseException e) {
			logger.error("Error parsing the date " + date, e);
			throw new SpagoBIRuntimeException("Error parsing the date "+date);
		}
		
		ConnectionDescriptor connection = (ConnectionDescriptor)parentStatement.getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
		String dialect = connection.getDialect();
		
		if(dialect!=null){
			
			if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_MYSQL)){
				if (toReturn.startsWith("'") && toReturn.endsWith("'")) {
					toReturn = " STR_TO_DATE("+toReturn+",'%d/%m/%Y %H:%i:%s') ";
				}else{
					toReturn = " STR_TO_DATE('"+toReturn+"','%d/%m/%Y %H:%i:%s') ";
				}
			}else if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_HSQL)){
				try {
					DateFormat daf;
					if ( StringUtils.isBounded(toReturn, "'") ) {
						daf = new SimpleDateFormat("'dd/MM/yyyy HH:mm:SS'");
					}else{
						daf = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS");
					}
					
					Date myDate = daf.parse(toReturn);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");		
					toReturn =  "'"+df.format(myDate)+"'";

				} catch (Exception e) {
					toReturn = "'" +toReturn+ "'";
				}
			}else if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_INGRES)){
				if (toReturn.startsWith("'") && toReturn.endsWith("'")) {
					toReturn = " STR_TO_DATE("+toReturn+",'%d/%m/%Y') ";
				}else{
					toReturn = " STR_TO_DATE('"+toReturn+"','%d/%m/%Y') ";
				}
			}else if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_ORACLE)){
				if (toReturn.startsWith("'") && toReturn.endsWith("'")) {
					toReturn = " TO_TIMESTAMP("+toReturn+",'DD/MM/YYYY HH24:MI:SS.FF') ";
				}else{
					toReturn = " TO_TIMESTAMP('"+toReturn+"','DD/MM/YYYY HH24:MI:SS.FF') ";
				}
			}else if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_ORACLE9i10g)){
				if (toReturn.startsWith("'") && toReturn.endsWith("'")) {
					toReturn = " TO_TIMESTAMP("+toReturn+",'DD/MM/YYYY HH24:MI:SS.FF') ";
				}else{
					toReturn = " TO_TIMESTAMP('"+toReturn+"','DD/MM/YYYY HH24:MI:SS.FF') ";
				}
			}else if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_POSTGRES)){
				if (toReturn.startsWith("'") && toReturn.endsWith("'")) {
					toReturn = " TO_TIMESTAMP("+toReturn+",'DD/MM/YYYY HH24:MI:SS.FF') ";
				}else{
					toReturn = " TO_TIMESTAMP('"+toReturn+"','DD/MM/YYYY HH24:MI:SS.FF') ";
				}
			}else if( dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_SQLSERVER)){
				if (toReturn.startsWith("'") && toReturn.endsWith("'")) {
					toReturn = toReturn;
				}else{
					toReturn = "'"+toReturn+"'";
				}
			} else if (dialect.equalsIgnoreCase(QuerySerializationConstants.DIALECT_TERADATA)) {
				/*
				 * Unfortunately we cannot use neither
				 * CAST(" + dateStr + " AS DATE FORMAT 'dd/mm/yyyy') 
				 * nor
				 * CAST((" + dateStr + " (Date,Format 'dd/mm/yyyy')) As Date)
				 * because Hibernate does not recognize (and validate) those SQL functions.
				 * Therefore we must use a predefined date format (yyyy-MM-dd).
				 */
				try {
					DateFormat dateFormat;
					if ( StringUtils.isBounded(toReturn, "'") ) {
						dateFormat = new SimpleDateFormat("'dd/MM/yyyy'");
					} else {
						dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					}
					Date myDate = dateFormat.parse(toReturn);
					dateFormat = new SimpleDateFormat("yyyy-MM-dd");		
					toReturn = "'" + dateFormat.format(myDate) + "'";
				} catch (Exception e) {
					logger.error("Error parsing the date " + toReturn, e);
					throw new SpagoBIRuntimeException("Error parsing the date " + toReturn + ".");
				}
			}
		}
		
		return toReturn;
	}
	
}
