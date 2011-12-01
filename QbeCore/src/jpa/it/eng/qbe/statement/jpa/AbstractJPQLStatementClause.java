/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.configuration.dao.fileimpl.InLineFunctionsDAOFileImpl.InLineFunction;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesPunctualDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesRangeDescriptor;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.SimpleSelectField;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.qbe.statement.IStatementClause;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractJPQLStatementClause implements IStatementClause {
	
	JPQLStatement parentStatement;
	
	public static final String EXPRESSION_TOKEN_DELIMITERS = "+-|*/(),";
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementSelectClause.class);
	
	public String parseInLinecalculatedField(String expression, String slots, Query query, Map entityAliasesMaps){
		String newExpression;
		
		logger.debug("IN");
		
		newExpression = expression;
		
		try {
			Assert.assertNotNull(parentStatement, "Class member [parentStatement] cannot be null in orser to properly parse inline calculated field expression [" + expression + "]");
			Assert.assertNotNull(expression, "Input parameter [espression] cannot be null");
			Assert.assertNotNull(query, "Input parameter [query] cannot be null");
			Assert.assertNotNull(entityAliasesMaps, "Input parameter [entityAliasesMaps] cannot be null");
			
			logger.debug("Parsing expression [" + expression + "] ...");
			newExpression = replaceFields(newExpression, false, query, entityAliasesMaps);
			newExpression = replaceInLineFunctions(newExpression, query, entityAliasesMaps);
			newExpression = replaceSlotDefinitions(newExpression, slots, query, entityAliasesMaps);
			logger.debug("Expression [" + expression + "] paresed succesfully into [" + newExpression + "]");
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while parsing expression [" + expression + "]");
		} finally {
			logger.debug("OUT");
		}
		
		return newExpression;
	}
	

	private String replaceFields(String expression, boolean isTransientExpression, Query query, Map entityAliasesMaps) {
		String newExpression;
		IModelEntity rootEntity;
		IModelField modelField;
		String queryName;
		String rootEntityAlias;
		Map entityAliases;
		
		List<String> fieldQueryNames;
		List<String> fieldExpressionNames;
		
		logger.debug("IN");
		
		newExpression = expression;
		
		entityAliases = (Map)entityAliasesMaps.get(query.getId());
		fieldQueryNames = new  ArrayList<String>();
		fieldExpressionNames = new  ArrayList<String>();
		
		try  {		
			StringTokenizer tokenizer = new StringTokenizer(expression, EXPRESSION_TOKEN_DELIMITERS);
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().trim();
				
				logger.debug("Processing expression token [" + token + "] ...");
					
				modelField = null;
				if(isTransientExpression) {
					SimpleSelectField fieldMatchingAlias = null;
					List<SimpleSelectField> fieldsMatchingAlias = query.getSelectSimpleFieldsByAlias(token);
					if(!fieldsMatchingAlias.isEmpty()) {
						fieldMatchingAlias = fieldsMatchingAlias.get(0);
						modelField = parentStatement.getDataSource().getModelStructure().getField(fieldMatchingAlias.getUniqueName());
					}
				} else {
					String decodedToken = token;
					decodedToken = decodedToken.replaceAll("\\[", "(");
					decodedToken = decodedToken.replaceAll("\\]", ")");
					modelField = parentStatement.getDataSource().getModelStructure().getField(decodedToken);
				}
				
				
				if(modelField != null) {
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
					//queryName = fieldMatchingAlias.getFunction().apply(rootEntityAlias + "." + queryName);
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
			tokenizer = new StringTokenizer(expression.replace("\'", ""), "+-|*/(),");
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken().trim();
				expressionCursorIndex = newExpression.indexOf(token, expressionCursorIndex);
				if(fieldIndex < fieldExpressionNames.size() && fieldExpressionNames.get(fieldIndex).equals(token)){
					newExpression = newExpression.substring(0, expressionCursorIndex)+ fieldQueryNames.get(fieldIndex)+newExpression.substring(expressionCursorIndex+token.length());
					expressionCursorIndex = expressionCursorIndex + fieldQueryNames.get(fieldIndex).length();
					fieldIndex++;
				}else {
					expressionCursorIndex = expressionCursorIndex + token.length();
				}
			}
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while parsing expression [" + expression + "]");
		} finally {
			logger.debug("OUT");
		}
		return newExpression;
	}
	
//	private String replaceFields(String expression, Query query, Map entityAliasesMaps) {
//		IModelEntity rootEntity;
//		IModelField modelField;
//		String queryName;
//		String rootEntityAlias;
//		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
//		
//		List<String> aliasEntityMapping = new  ArrayList<String>();
//		List<String> aliases = new  ArrayList<String>();
//		
//		StringTokenizer stk = new StringTokenizer(expression, "+-|*/(),");
//		while(stk.hasMoreTokens()) {
//			String alias = stk.nextToken().trim();
//			List<SimpleSelectField> fieldsMatchingAlias = query.getSelectSimpleFieldsByAlias(alias);
//			if(!fieldsMatchingAlias.isEmpty()) {
//				SimpleSelectField fieldMatchingAlias = fieldsMatchingAlias.get(0);
//				
//				String uniqueName = fieldMatchingAlias.getUniqueName();
//				modelField = parentStatement.getDataSource().getModelStructure().getField(uniqueName);	
//				Couple queryNameAndRoot = modelField.getQueryName();
//				queryName = (String) queryNameAndRoot.getFirst();
//				logger.debug("select field query name [" + queryName + "]");
//				
//				if(queryNameAndRoot.getSecond()!=null){
//					rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
//				}else{
//					rootEntity = modelField.getParent().getRoot(); 	
//				}
//				rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
//				queryName = fieldMatchingAlias.getFunction().apply(rootEntityAlias+"."+queryName);
//				
//				aliasEntityMapping.add(queryName);
//				aliases.add(alias);
//			}
//		}
//
//		String freshExpr = expression;
//		int ind =0;
//		int pos =0;
//		stk = new StringTokenizer(expression.replace("\'", ""), "+-|*/(),");
//		while(stk.hasMoreTokens()){
//			String alias = stk.nextToken().trim();
//			pos = freshExpr.indexOf(alias, pos);
//			if(ind<aliases.size() && aliases.get(ind).equals(alias)){
//				freshExpr = freshExpr.substring(0, pos)+ aliasEntityMapping.get(ind)+freshExpr.substring(pos+alias.length());
//				pos = pos+ aliasEntityMapping.get(ind).length();
//				ind++;
//			}else{
//				//freshExpr = freshExpr.substring(0, pos)+ alias+freshExpr.substring(pos+alias.length());
//				pos = pos+ alias.length();
//			}
//		}
//		return freshExpr;
//	}
	
	
	
	private String replaceInLineFunctions(String expression, Query query, Map entityAliasesMaps) {
		String newExpression;
 
		ConnectionDescriptor connection = (ConnectionDescriptor)this.parentStatement.getDataSource().getConfiguration().loadDataSourceProperties().get("connection");		
		String dbDialect = connection.getDialect(); 
		HashMap<String, InLineFunction>  mapFuncs = this.parentStatement.getDataSource().getConfiguration().loadInLineFunctions(dbDialect);

		if (expression.startsWith("(")) expression = (expression.substring(expression.indexOf("(")+1,expression.lastIndexOf(")"))).trim();
		//if is not a real function (ex. only a field) returns the expression in input
		if (expression.indexOf("(") < 0) return expression;

		String nameFunc = expression.substring(0, expression.indexOf("("));
		
		if (mapFuncs.get(nameFunc) == null) return expression;
		
		String codeFunc = ((InLineFunction)mapFuncs.get(nameFunc)).getCode();
		newExpression = codeFunc;
		//substitutes paramters in the new function code
		StringTokenizer stk = new StringTokenizer(expression, "+-|*/(),");
		int idx = 0;
		while(stk.hasMoreTokens()){
			String alias = stk.nextToken().trim();
			if (!alias.equalsIgnoreCase(nameFunc)) {
				newExpression = newExpression.replaceAll("\\$"+(idx+1), alias);
				idx++;
			}
		}
		
		return newExpression;
	}
	
	private String replaceSlotDefinitions(String expr, String s, Query query, Map entityAliasesMaps) {
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
							newExpr += valueSeparator + "'" + value + "'";
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
							minCondition += punctualDescriptor.getMinValue();
						}
						if(punctualDescriptor.getMaxValue() != null) {
							maxCondition = " (" + expr + ")";
							maxCondition += (punctualDescriptor.isIncludeMaxValue())? " <= " : "<";
							maxCondition += punctualDescriptor.getMaxValue();
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
				newExpr += " ELSE (CONCAT(" + expr + ",''))";
			}
			newExpr += " END ";
		} catch (Throwable t) {
			logger.error("Impossible to add slots", t);
			return expr;
		}
		
		return newExpr;
	}
	
}
