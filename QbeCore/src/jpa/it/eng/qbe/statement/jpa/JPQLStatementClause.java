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
public class JPQLStatementClause {
	
	JPQLStatement parentStatement;
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementSelectClause.class);
	
	public String parseInLinecalculatedField(String expression, String slots, Query query, Map entityAliasesMaps){
		String newExpression;
		
		newExpression = expression;
		newExpression = replaceFields(newExpression, query, entityAliasesMaps);
		newExpression = replaceInLineFunctions(newExpression, query, entityAliasesMaps);
		newExpression = replaceSlotDefinitions(newExpression, slots, query, entityAliasesMaps);
		
		return newExpression;
	}
	
	private String replaceFields(String expression, Query query, Map entityAliasesMaps) {
		List allSelectFields;
		IModelEntity rootEntity;
		IModelField datamartField;
		String queryName;
		String rootEntityAlias;
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		List<String> aliasEntityMapping = new  ArrayList<String>();
		List<String> aliases = new  ArrayList<String>();
		
		StringTokenizer stk = new StringTokenizer(expression, "+-|*/(),");
		while(stk.hasMoreTokens()){
			String alias = stk.nextToken().trim();
			String uniqueName;
			allSelectFields = query.getSelectFields(false);
			for(int i=0; i<allSelectFields.size(); i++){
				if(allSelectFields.get(i).getClass().equals(SimpleSelectField.class) && ((SimpleSelectField)allSelectFields.get(i)).getAlias().equals(alias)){
					uniqueName=((SimpleSelectField)allSelectFields.get(i)).getUniqueName();
					datamartField = parentStatement.getDataSource().getModelStructure().getField(uniqueName);	
					Couple queryNameAndRoot = datamartField.getQueryName();
					queryName = (String) queryNameAndRoot.getFirst();
					logger.debug("select field query name [" + queryName + "]");
					
					if(queryNameAndRoot.getSecond()!=null){
						rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
					}else{
						rootEntity = datamartField.getParent().getRoot(); 	
					}
					rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
					queryName = ((SimpleSelectField)allSelectFields.get(i)).getFunction().apply(rootEntityAlias+"."+queryName);
					aliasEntityMapping.add(queryName);
					aliases.add(alias);
					break;
				}
			}
		}
		
		String freshExpr = expression;
		int ind =0;
		int pos =0;
		stk = new StringTokenizer(expression.replace("\'", ""), "+-|*/(),");
		while(stk.hasMoreTokens()){
			String alias = stk.nextToken().trim();
			pos = freshExpr.indexOf(alias, pos);
			if(ind<aliases.size() && aliases.get(ind).equals(alias)){
				freshExpr = freshExpr.substring(0, pos)+ aliasEntityMapping.get(ind)+freshExpr.substring(pos+alias.length());
				pos = pos+ aliasEntityMapping.get(ind).length();
				ind++;
			}else{
				//freshExpr = freshExpr.substring(0, pos)+ alias+freshExpr.substring(pos+alias.length());
				pos = pos+ alias.length();
			}
		}
		return freshExpr;
	}
	
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
				newExpr += " ELSE (" + expr + ")";
			}
			newExpr += " END ";
		} catch (Throwable t) {
			logger.error("Impossible to add slots", t);
			return expr;
		}
		
		return newExpr;
	}
	
}
