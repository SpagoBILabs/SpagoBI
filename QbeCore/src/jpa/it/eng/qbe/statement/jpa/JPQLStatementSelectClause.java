/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesPunctualDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesRangeDescriptor;
import it.eng.qbe.query.AbstractSelectField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.SimpleSelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JPQLStatementSelectClause extends AbstractJPQLStatementClause {
	
	String[] statementFields;
	int index;
	Map entityAliases;;
	
	
	public static final String SELECT = "SELECT";
	public static final String DISTINCT = "DISTINCT";
	 
	public static transient Logger logger = Logger.getLogger(JPQLStatementSelectClause.class);
	
	public static String build(JPQLStatement parentStatement, Query query, Map<String, Map<String, String>> entityAliasesMaps){
		JPQLStatementSelectClause clause = new JPQLStatementSelectClause(parentStatement);
		return clause.buildClause(query, entityAliasesMaps);
	}
	
	protected JPQLStatementSelectClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	public String buildClause(Query query, Map<String, Map<String, String>> entityAliasesMaps) {
		StringBuffer buffer;
		List<ISelectField> selectFields;
		List<InLineCalculatedSelectField> selectInLineCalculatedFields = new ArrayList<InLineCalculatedSelectField>();
	
		logger.debug("IN");
		
		buffer = new StringBuffer();
		
		try {
			
			Assert.assertNotNull(query, "Input parameter [query] cannot be null");
			Assert.assertNotNull(query, "Input parameter [entityAliasesMaps] cannot be null");
			
			logger.debug("Building select clause for query [" + query.getId() + "]");
			
			entityAliases = (Map)entityAliasesMaps.get(query.getId());	
			Assert.assertNotNull(entityAliases, "The entity map for the query [" + query.getId() + "] canot be null");
			
			selectFields = query.getSelectFields(true);
				
			buffer.append(SELECT);		
			if (query.isDistinctClauseEnabled()) {
				buffer.append(" " + DISTINCT);
			}
			
			int calculatedFieldNumber = query.getCalculatedSelectFields(true).size();
			logger.debug("In select clause of query [" + query.getId() + "] there are [" + calculatedFieldNumber + "] calculated fields out of [" + selectFields.size() + "]");
			
			int statementFiledsNo = selectFields.size() - calculatedFieldNumber; // = simpleFields + inlineCalculatedFields
			if(statementFiledsNo == 0) {
				throw new RuntimeException("Impossible to execute a query that contains in the select statemet only (expert) calculated fields");
			}
			statementFields = new String[selectFields.size() - calculatedFieldNumber]; 
			index = 0;
			
			for(ISelectField selectAbstractField : selectFields){										
				if(selectAbstractField.isSimpleField()){
					addSimpleSelectField((SimpleSelectField)selectAbstractField, entityAliasesMaps); 
				} else if(selectAbstractField.isInLineCalculatedField()){
					// calculated field will be added in the second step when all the simple fields will be already in place
					selectInLineCalculatedFields.add((InLineCalculatedSelectField)selectAbstractField);
					// we keep the space to add this field later in the second process step
					index++;
				}
			}
				
			for(InLineCalculatedSelectField selectInLineField :  selectInLineCalculatedFields){
					
					String expression = selectInLineField.getExpression();
					String slots = selectInLineField.getSlots();
					
					expression = parseInLinecalculatedField(selectInLineField, slots, query, entityAliasesMaps);
					//expr = addSlots(expr, selectInLineField);
					expression = selectInLineField.getFunction().apply(expression);
					
					for(int y = 0; y < statementFields.length; y++){
						if(statementFields[y] == null){
							statementFields[y]= " " + expression;
							index = y;
							break;
						}
					}

					logger.debug("select clause element succesfully added to select clause");
			}
				
			String separator = "";
			for(int y = 0; y < statementFields.length; y++){
				buffer.append(separator + statementFields[y]);
				separator = ",";
			}		
		} finally {
			logger.debug("OUT");
		}
		
		return buffer.toString().trim();
	}
	
	private String addSlots(String expr, InLineCalculatedSelectField selectInLineField) {
		String newExpr;
		
		newExpr = null;
		
		try {
			String s = selectInLineField.getSlots();
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

	private void addSimpleSelectField(SimpleSelectField selectField, Map entityAliasesMaps) {
		
		IModelField datamartField;
		String queryName;
		IModelEntity rootEntity;
		String rootEntityAlias;
		String selectClauseElement; 
	
		
		logger.debug("select field unique name [" + selectField.getUniqueName() + "]");
		
		datamartField = parentStatement.getDataSource().getModelStructure().getField(selectField.getUniqueName());
		
		Couple queryNameAndRoot = datamartField.getQueryName();
		
		queryName = (String) queryNameAndRoot.getFirst();
		logger.debug("select field query name [" + queryName + "]");
		
		if(queryNameAndRoot.getSecond()!=null){
			rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
		}else{
			rootEntity = datamartField.getParent().getRoot(); 	
		}
		
			
		logger.debug("select field root entity unique name [" + rootEntity.getUniqueName() + "]");
		
		rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
		if(rootEntityAlias == null) {
			rootEntityAlias = parentStatement.getNextAlias(entityAliasesMaps);
			entityAliases.put(rootEntity.getUniqueName(), rootEntityAlias);
		}
		logger.debug("select field root entity alias [" + rootEntityAlias + "]");
		
		selectClauseElement = rootEntityAlias + "." + queryName;
		logger.debug("select clause element before aggregation [" + selectClauseElement + "]");
		
		selectClauseElement = selectField.getFunction().apply(selectClauseElement);
		logger.debug("select clause element after aggregation [" + selectClauseElement + "]");
		
		
		statementFields[index] = " " + selectClauseElement;
		index++;
		
		logger.debug("select clause element succesfully added to select clause");
	}
	
}
