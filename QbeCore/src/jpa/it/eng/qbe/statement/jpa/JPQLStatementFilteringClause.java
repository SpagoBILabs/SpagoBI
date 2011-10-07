/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.HavingField;
import it.eng.qbe.query.Operand;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.jpa.JPQLStatementConditionalOperators.IConditionalOperator;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.objects.Couple;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class JPQLStatementFilteringClause  extends JPQLStatementClause {
	

	public static transient Logger logger = Logger.getLogger(JPQLStatementFilteringClause.class);
	
	/**
	 * Builds the sql statement (for the having or the where clause) for the calculate fields.  
	 * @param operator the operator of the clause 
	 * @param leftOperand the left operand
	 * @param isPromptable
	 * @param rightOperand right operand
	 * @param query the sql query
	 * @param entityAliasesMaps the map of the entity involved in the query
	 * @return
	 */
	protected String buildInLineCalculatedFieldClause(String operator, Operand leftOperand, boolean isPromptable, Operand rightOperand, Query query, Map entityAliasesMaps, IConditionalOperator conditionalOperator){
		String[] rightOperandElements;
				
		String expr = leftOperand.values[0].substring(leftOperand.values[0].indexOf("\"expression\":\"")+14);//.replace("\'", "");
		expr = expr.substring(0, expr.indexOf("\""));
		
		logger.debug("Left operand (of a inline calculated field) for the filter clause of the query: "+leftOperand.values[0]);
		logger.debug("Expression of a inline calculated field for the filter clause of the query: "+expr);

		
		//String expr = leftOperand.value.substring(15,leftOperand.value.indexOf("\",\"alias"));//.replace("\'", "");

		expr = parseInLinecalculatedField(expr, query, entityAliasesMaps);
				
		logger.debug("IN");
					
		if (parentStatement.OPERAND_TYPE_STATIC.equalsIgnoreCase(rightOperand.type) && isPromptable) {
			// get last value first (the last value edited by the user)
			rightOperandElements = rightOperand.lastValues;
		} else {
			rightOperandElements = buildOperand(rightOperand, query, entityAliasesMaps);
		}
		
		if (parentStatement.OPERAND_TYPE_STATIC.equalsIgnoreCase(rightOperand.type) )  {
			rightOperandElements = getTypeBoundedStaticOperand(leftOperand, operator, rightOperandElements);
		}
		
		return conditionalOperator.apply("("+expr+")", rightOperandElements);
	}
	
	String[] buildOperand(Operand operand, Query query, Map entityAliasesMaps) {
		String[] operandElement;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(operand, "Input parameter [operand] cannot be null in order to execute method [buildUserProvidedWhereField]");
			operandElement = new String[] {""};
			
			if(parentStatement.OPERAND_TYPE_STATIC.equalsIgnoreCase(operand.type)) {
				operandElement = buildStaticOperand(operand);
			} else if (parentStatement.OPERAND_TYPE_SUBQUERY.equalsIgnoreCase(operand.type)) {
				operandElement = new String[] {buildQueryOperand(operand)};
			} else if (parentStatement.OPERAND_TYPE_FIELD.equalsIgnoreCase(operand.type)) {
				operandElement = new String[] {buildFieldOperand(operand, query, entityAliasesMaps)};
			} else if (parentStatement.OPERAND_TYPE_PARENT_FIELD.equalsIgnoreCase(operand.type)) {
				operandElement = new String[] {buildParentFieldOperand(operand, query, entityAliasesMaps)};
			} else {
				Assert.assertUnreachable("Invalid operand type [" + operand.type+ "]");
			}
		} finally {
			logger.debug("OUT");
		}		
		return operandElement;
	}
	
	String[] buildStaticOperand(Operand operand) {
		String[] operandElement;
		
		logger.debug("IN");
		
		try {
			operandElement = operand.values;
		} finally {
			logger.debug("OUT");
		}		
		
		return operandElement;
	}
	
	String buildQueryOperand(Operand operand) {
		String operandElement;
		
		logger.debug("IN");
		
		try {
			String subqueryId;
			
			logger.debug("where element right-hand field type [" + parentStatement.OPERAND_TYPE_SUBQUERY + "]");
			
			subqueryId = operand.values[0];
			logger.debug("Referenced subquery [" + subqueryId + "]");
			
			operandElement = "Q{" + subqueryId + "}";
			operandElement = "( " + operandElement + ")";
			logger.debug("where element right-hand field value [" + operandElement + "]");	
		} finally {
			logger.debug("OUT");
		}		
		
		return operandElement;
	}
	
	String buildFieldOperand(Operand operand, Query query, Map entityAliasesMaps) {
		String operandElement;
		IModelField datamartField;
		IModelEntity rootEntity;
		String queryName;
		String rootEntityAlias;
		Map targetQueryEntityAliasesMap;
		
		logger.debug("IN");
		
		try {
			
			targetQueryEntityAliasesMap = (Map)entityAliasesMaps.get(query.getId());
			Assert.assertNotNull(targetQueryEntityAliasesMap, "Entity aliases map for query [" + query.getId() + "] cannot be null in order to execute method [buildUserProvidedWhereField]");
			
			
			datamartField = parentStatement.getDataSource().getModelStructure().getField( operand.values[0] );
			Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + operand.values[0] + "]");
			Couple queryNameAndRoot = datamartField.getQueryName();
			
			queryName = (String) queryNameAndRoot.getFirst();
			logger.debug("select field query name [" + queryName + "]");
			
			if(queryNameAndRoot.getSecond()!=null){
				rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
			}else{
				rootEntity = datamartField.getParent().getRoot(); 	
			}
			logger.debug("where field query name [" + queryName + "]");
			
			logger.debug("where field root entity unique name [" + rootEntity.getUniqueName() + "]");
			
			if(!targetQueryEntityAliasesMap.containsKey(rootEntity.getUniqueName())) {
				logger.debug("Entity [" + rootEntity.getUniqueName() + "] require a new alias");
				rootEntityAlias = parentStatement.getNextAlias(entityAliasesMaps);
				logger.debug("A new alias has been generated [" + rootEntityAlias + "]");				
				targetQueryEntityAliasesMap.put(rootEntity.getUniqueName(), rootEntityAlias);
			}
			rootEntityAlias = (String)targetQueryEntityAliasesMap.get( rootEntity.getUniqueName() );
			logger.debug("where field root entity alias [" + rootEntityAlias + "]");
			
			if (operand instanceof HavingField.Operand) {
				HavingField.Operand havingFieldOperand = (HavingField.Operand) operand;
				IAggregationFunction function = havingFieldOperand.function;
				operandElement = function.apply(rootEntityAlias + "." + queryName);
			} else {
				operandElement = rootEntityAlias + "." + queryName;
			}
			logger.debug("where element operand value [" + operandElement + "]");
		} finally {
			logger.debug("OUT");
		}		
		
		return operandElement;
	}
	
	String buildParentFieldOperand(Operand operand, Query query, Map entityAliasesMaps) {
		String operandElement;
		
		String[] chunks;
		String parentQueryId;
		String fieldName;
		IModelField datamartField;
		IModelEntity rootEntity;
		String queryName;
		String rootEntityAlias;
		
		
		logger.debug("IN");
		
		try {
			
			// it comes directly from the client side GUI. It is a composition of the parent query id and filed name, 
			// separated by a space
			logger.debug("operand  is equals to [" + operand.values[0] + "]");
			
			chunks = operand.values[0].split(" ");
			Assert.assertTrue(chunks.length >= 2, "Operand [" + chunks.toString() + "] does not contains enougth informations in order to resolve the reference to parent field");
			
			parentQueryId = chunks[0];
			logger.debug("where right-hand field belonging query [" + parentQueryId + "]");
			fieldName = chunks[1];
			logger.debug("where right-hand field unique name [" + fieldName + "]");

			datamartField = parentStatement.getDataSource().getModelStructure().getField( fieldName );
			Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + fieldName + "]");
			
			Couple queryNameAndRoot = datamartField.getQueryName();
			
			queryName = (String) queryNameAndRoot.getFirst();
			logger.debug("select field query name [" + queryName + "]");
			
			if(queryNameAndRoot.getSecond()!=null){
				rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
			}else{
				rootEntity = datamartField.getParent().getRoot(); 	
			}
			logger.debug("where right-hand field query name [" + queryName + "]");
			logger.debug("where right-hand field root entity unique name [" + rootEntity.getUniqueName() + "]");
			
			Map parentEntityAliases = (Map)entityAliasesMaps.get(parentQueryId);
			if(parentEntityAliases != null) {
				if(!parentEntityAliases.containsKey(rootEntity.getUniqueName())) {
					Assert.assertUnreachable("Filter of subquery [" + query.getId() + "] refers to a non " +
							"existing parent query [" + parentQueryId + "] entity [" + rootEntity.getUniqueName() + "]");
				}
				rootEntityAlias = (String)parentEntityAliases.get( rootEntity.getUniqueName() );
			} else {
				rootEntityAlias = "unresoved_alias";
				logger.warn("Impossible to get aliases map for parent query [" + parentQueryId +"]. Probably the parent query ha not been compiled yet");					
				logger.warn("Query [" + query.getId() +"] refers entities of its parent query [" + parentQueryId +"] so the generated statement wont be executable until the parent query will be compiled");					
			}
			logger.debug("where right-hand field root entity alias [" + rootEntityAlias + "]");
			
			operandElement = rootEntityAlias + "." + queryName;
			logger.debug("where element right-hand field value [" + operandElement + "]");
		} finally {
			logger.debug("OUT");
		}		
		
		return operandElement;
	}
	
	String[] getTypeBoundedStaticOperand(Operand leadOperand, String operator, String[] operandValuesToBound) {
		String[] boundedValues = new String[operandValuesToBound.length];

		for (int i = 0; i < operandValuesToBound.length; i++) {
		
			String operandValueToBound = operandValuesToBound[i];
			String boundedValue = operandValueToBound;
			
			
			// calculated field
			// TODO check!!!! why not a OPERAND_TYPE_CALCUALTED_FIELD????
			if (leadOperand.values[0].contains("expression")) {
				int startType = leadOperand.values[0].indexOf("type\":")+7;
				int endType = leadOperand.values[0].indexOf( "\"", startType);
				String type = leadOperand.values[0].substring(startType, endType);
				boundedValue = getValueBounded(operandValueToBound, type);
			}else if (parentStatement.OPERAND_TYPE_FIELD.equalsIgnoreCase(leadOperand.type) 
							|| parentStatement.OPERAND_TYPE_PARENT_FIELD.equalsIgnoreCase(leadOperand.type)) {
				
				IModelField datamartField = parentStatement.getDataSource().getModelStructure().getField(leadOperand.values[0]);
				boundedValue = getValueBounded(operandValueToBound, datamartField.getType());
			}

			boundedValues[i] = boundedValue;
		
		}
		
		return boundedValues;
	}
	
	String getValueBounded(String operandValueToBound, String operandType) {
		
		String boundedValue = operandValueToBound;
		if (operandType.equalsIgnoreCase("STRING") || operandType.equalsIgnoreCase("CHARACTER") || operandType.equalsIgnoreCase("java.lang.String") || operandType.equalsIgnoreCase("java.lang.Character")) {
			
			// if the value is already surrounded by quotes, does not neither add quotes nor escape quotes 
			if ( StringUtils.isBounded(operandValueToBound, "'") ) {
				boundedValue = operandValueToBound ;
			} else {
				operandValueToBound = StringUtils.escapeQuotes(operandValueToBound);
				return StringUtils.bound(operandValueToBound, "'");
			}
		} else if(operandType.equalsIgnoreCase("TIMESTAMP") || operandType.equalsIgnoreCase("DATE") || operandType.equalsIgnoreCase("java.sql.TIMESTAMP") || operandType.equalsIgnoreCase("java.sql.date") || operandType.equalsIgnoreCase("java.util.date")){
			boundedValue = parseDate(operandValueToBound);
		}
		
		return boundedValue;
	}
	
	/**
	 * Parse the date: get the user locale and format the date in the db format
	 * @param date the localized date
	 * @return the date in the db format
	 */
	String parseDate(String date){
		String toReturn = "'" +date+ "'";
		String userDfString = (String)parentStatement.getParameters().get("userDateFormatPattern");
		String dbDfString = (String)parentStatement.getParameters().get("databaseDateFormatPattern");
		DateFormat df = new SimpleDateFormat(userDfString);
		try {			
			Date operandValueToBoundDate = df.parse(date);
			df = new SimpleDateFormat(dbDfString);		
			toReturn =  "'"+df.format(operandValueToBoundDate)+"'";
		} catch (ParseException e) {
			logger.error("Error parsing the date "+date);
			throw new SpagoBIRuntimeException("Error parsing the date "+date+". Check the format, it should be "+userDfString);
		}
		return toReturn;
	}
	
	
}
