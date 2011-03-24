/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.statement.jpa;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.jpa.IJpaDataSource;
import it.eng.qbe.model.accessmodality.IModelAccessModality;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.query.AbstractSelectField;
import it.eng.qbe.query.CriteriaConstants;
import it.eng.qbe.query.DataMartSelectField;
import it.eng.qbe.query.ExpressionNode;
import it.eng.qbe.query.Filter;
import it.eng.qbe.query.HavingField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Operand;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.utility.StringUtils;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.log4j.Logger;



/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPQLStatement extends AbstractStatement {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JPQLStatement.class);
    
    public static final String DISTINCT = "DISTINCT";
	public static final String SELECT = "SELECT";
	public static final String FROM = "FROM";
	
	protected IJpaDataSource dataSource;
	
	public static interface IConditionalOperator {	
		String apply(String leftHandValue, String[] rightHandValues);
	}
	public static Map conditionalOperators;
	
	private String whereClause;
	
	static {
		conditionalOperators = new HashMap();
		conditionalOperators.put(CriteriaConstants.EQUALS_TO, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.EQUALS_TO;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_EQUALS_TO, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.NOT_EQUALS_TO;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "!=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.GREATER_THAN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.GREATER_THAN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + ">" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.EQUALS_OR_GREATER_THAN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.EQUALS_OR_GREATER_THAN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + ">=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.LESS_THAN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.LESS_THAN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "<" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.EQUALS_OR_LESS_THAN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.EQUALS_OR_LESS_THAN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				return leftHandValue + "<=" + rightHandValues[0];
			}
		});
		conditionalOperators.put(CriteriaConstants.STARTS_WITH, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.STARTS_WITH;}
			public String apply(String leftHandValue, String[] rightHandValues) {	
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				rightHandValue = rightHandValue.substring(1, rightHandValue.length()-1);
				rightHandValue = rightHandValue + "%";
				return leftHandValue + " like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_STARTS_WITH, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.NOT_STARTS_WITH;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				rightHandValue = rightHandValue.substring(1, rightHandValue.length()-1);
				rightHandValue = rightHandValue + "%";
				return leftHandValue + " not like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.ENDS_WITH, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.ENDS_WITH;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				rightHandValue = rightHandValue.substring(1, rightHandValue.length()-1);
				rightHandValue = "%" + rightHandValue;
				return leftHandValue + " like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_ENDS_WITH, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.NOT_ENDS_WITH;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				rightHandValue = rightHandValue.substring(1, rightHandValue.length()-1);
				rightHandValue = "%" + rightHandValue;
				return leftHandValue + " not like '" + rightHandValue + "'";
			}
		});		
		conditionalOperators.put(CriteriaConstants.CONTAINS, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.CONTAINS;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues[0] != null, "Operand cannot be null when the operator is " + getName());
				String rightHandValue = rightHandValues[0].trim();
				rightHandValue = rightHandValue.substring(1, rightHandValue.length()-1);
				rightHandValue = "%" + rightHandValue + "%";
				return leftHandValue + " like '" + rightHandValue + "'";
			}
		});
		conditionalOperators.put(CriteriaConstants.IS_NULL, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.IS_NULL;}
			public String apply(String leftHandValue, String[] rightHandValue) {
				return leftHandValue + " IS NULL";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_NULL, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.NOT_NULL;}
			public String apply(String leftHandValue, String[] rightHandValue) {
				return leftHandValue + " IS NOT NULL";
			}
		});
		
		conditionalOperators.put(CriteriaConstants.BETWEEN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.BETWEEN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues.length == 2, "When BEETWEEN operator is used the operand must contain minValue and MaxValue");
				return leftHandValue + " BETWEEN " + rightHandValues[0] + " AND " + rightHandValues[1];
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_BETWEEN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.NOT_BETWEEN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				Assert.assertTrue(rightHandValues != null && rightHandValues.length == 2, "When BEETWEEN operator is used the operand must contain minValue and MaxValue");
				return leftHandValue + " NOT BETWEEN " + rightHandValues[0] + " AND " + rightHandValues[1];
			}
		});
		
		conditionalOperators.put(CriteriaConstants.IN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.IN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				String rightHandValue = StringUtils.join(rightHandValues, ",");
				return leftHandValue + " IN (" +  rightHandValue + ")";
			}
		});
		conditionalOperators.put(CriteriaConstants.NOT_IN, new IConditionalOperator() {
			public String getName() {return CriteriaConstants.NOT_IN;}
			public String apply(String leftHandValue, String[] rightHandValues) {
				String rightHandValue = StringUtils.join(rightHandValues, ",");
				return leftHandValue + " NOT IN (" +  rightHandValue + ")";
			}
		});
	}
	
	protected JPQLStatement(IDataSource dataSource) {
		super(dataSource);
	}
	
	
	public JPQLStatement(IDataSource dataSource, Query query) {
		super(dataSource, query);
	}
	
	private String getNextAlias(Map entityAliasesMaps) {
		int aliasesCount = 0;
		Iterator it = entityAliasesMaps.keySet().iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			Map entityAliases = (Map)entityAliasesMaps.get(key);
			aliasesCount += entityAliases.keySet().size();
		}
		
		return "t_" + aliasesCount;
	}
	
	private String buildSelectClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer;
		List selectFields;
		List allSelectFields;
		List<InLineCalculatedSelectField> selectInLineCalculatedFields = new ArrayList<InLineCalculatedSelectField>();
		AbstractSelectField selectAbstractField;
		DataMartSelectField selectField;
		InLineCalculatedSelectField selectInLineField;
		IModelEntity rootEntity;
		IModelField datamartField;
		String queryName;
		String rootEntityAlias;
		String selectClauseElement; // rootEntityAlias.queryName
		Map entityAliases;
		List<String> aliasEntityMapping;
		
		logger.debug("IN");
		buffer = new StringBuffer();
		try {
			selectFields = query.getSelectFields(true);
			
			if(selectFields == null ||selectFields.size() == 0) {
				return "";
			}
			
			entityAliases = (Map)entityAliasesMaps.get(query.getId());
						
			buffer.append(SELECT);		
			if (query.isDistinctClauseEnabled()) {
				buffer.append(" " + DISTINCT);
			}
			
			Iterator it = selectFields.iterator();
			if(it.hasNext()){
				selectAbstractField = (AbstractSelectField)it.next();
				String[] idsForQuery = new String[selectFields.size()-query.getCalculatedSelectFields(true).size()]; 
				int index=0;
				do{				
					if(selectAbstractField.isDataMartField()){
					
						selectField = (DataMartSelectField)selectAbstractField;
						
						logger.debug("select field unique name [" + selectField.getUniqueName() + "]");
						
						datamartField = getDataSource().getModelStructure().getField(selectField.getUniqueName());
						queryName = datamartField.getQueryName();
						logger.debug("select field query name [" + queryName + "]");
						
						rootEntity = datamartField.getParent().getRoot(); 		
						logger.debug("select field root entity unique name [" + rootEntity.getUniqueName() + "]");
						
						rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
						if(rootEntityAlias == null) {
							rootEntityAlias = getNextAlias(entityAliasesMaps);
							entityAliases.put(rootEntity.getUniqueName(), rootEntityAlias);
						}
						logger.debug("select field root entity alias [" + rootEntityAlias + "]");
						
						
						selectClauseElement = rootEntityAlias + "." + queryName.substring(0,1).toLowerCase()+queryName.substring(1);
						logger.debug("select clause element before aggregation [" + selectClauseElement + "]");
						
						selectClauseElement = selectField.getFunction().apply(selectClauseElement);
						logger.debug("select clause element after aggregation [" + selectClauseElement + "]");
						
						
						idsForQuery[index] = " " + selectClauseElement;
						index++;
						logger.debug("select clause element succesfully added to select clause");

					}else if(selectAbstractField.isInLineCalculatedField()){
						selectInLineCalculatedFields.add((InLineCalculatedSelectField)selectAbstractField);
						index++;
					}

					if(it.hasNext()){
						selectAbstractField = (AbstractSelectField)it.next();
					}else{
						break;
					}
					
				} while( true );
				

				
				aliasEntityMapping = new ArrayList<String>();
				for(int k=0; k< selectInLineCalculatedFields.size(); k++){
					selectInLineField = selectInLineCalculatedFields.get(k);
					
					String expr = selectInLineField.getExpression();//.replace("\'", "");			
					expr = parseInLinecalculatedField(expr, query, entityAliasesMaps);
					expr = selectInLineField.getFunction().apply(expr);
					
					for(int y= 0; y<idsForQuery.length; y++){
						if(idsForQuery[y]==null){
							idsForQuery[y]=" " +expr;
							index = y;
							break;
						}
					}
					
					
					
					logger.debug("select clause element succesfully added to select clause");
				}
				
				
				for(int y= 0; y<idsForQuery.length-1; y++){
					buffer.append(idsForQuery[y]+",");
				}
				buffer.append(idsForQuery[idsForQuery.length-1]);
				
			}
		
		}
		
		finally {
			logger.debug("OUT");
		}
		
		return buffer.toString().trim();
	}
	
	
	
	private String buildFromClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer;
		
		
		logger.debug("IN");
		buffer = new StringBuffer();
		try {
			Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
			
			
			if(entityAliases == null || entityAliases.keySet().size() == 0) {
				return "";
			}
			
			buffer.append(" " + FROM + " ");
			
			
			// outer join are not supported by jpa ?? check it! 
			// so this method is expected to return always an empty string
			//buffer.append( buildJoinClause(query, entityAliases) );
			
			Iterator it = entityAliases.keySet().iterator();
			while( it.hasNext() ) {
				String entityUniqueName = (String)it.next();
				logger.debug("entity [" + entityUniqueName +"]");
				
				String entityAlias = (String)entityAliases.get(entityUniqueName);
				logger.debug("entity alias [" + entityAlias +"]");
				
				IModelEntity datamartEntity =  getDataSource().getModelStructure().getEntity(entityUniqueName);
				
				addTableFakeCondition(datamartEntity.getName(), entityAlias);
				
				String whereClauseElement = datamartEntity.getName() + " " + entityAlias;
				logger.debug("where clause element [" + whereClauseElement +"]");
				
				buffer.append(" " + whereClauseElement);
				if( it.hasNext() ) {
					buffer.append(",");
				}
			}
		} finally {
			logger.debug("OUT");
		}
		
		return buffer.toString().trim();
	}
	
	/**
	 * Add to the where clause a fake condition..
	 * Id est, take the primary key (or an attribute of the primary key if it's a composed key) 
	 * of the entity and (for example keyField) and add to the whereClause the clause  
	 * entityAlias.keyField = entityAlias.keyField
	 * @param datamartEntityName the jpa object name
	 * @param entityAlias the alias of the table
	 */
	public void addTableFakeCondition(String datamartEntityName, String entityAlias){
		EntityManager entityManager = ((IJpaDataSource)getDataSource()).getEntityManager();
		Metamodel classMetadata =  entityManager.getMetamodel();
		//search the EntityType of the datamartEntityName
		for(Iterator it2 = classMetadata.getEntities().iterator(); it2.hasNext(); ) {
			EntityType et = (EntityType)it2.next();
			String entityName = et.getName();
			
			if(datamartEntityName.equals(entityName)){
			
				Type keyT = et.getIdType();
				
				if (keyT instanceof BasicType) {
					//the key has only one field
					String name = (et.getId(Object.class)).getName();
					if(whereClause==null || whereClause.equals("")){
						whereClause = "WHERE ";
					}else{
						whereClause = whereClause+" AND ";
					}
					whereClause = whereClause + " "+ entityAlias+"."+name+"="+entityAlias+"."+name;
				}else if (keyT instanceof EmbeddableType) {
					//the key is a composed key
					SingularAttribute keyAttr = (SingularAttribute)(((EmbeddableType) keyT).getDeclaredSingularAttributes().iterator().next());
					String name = keyAttr.getName();
					if(whereClause==null || whereClause.equals("")){
						whereClause = "WHERE ";
					}else{
						whereClause = whereClause+" AND ";
					}
					whereClause = whereClause + " "+ entityAlias+"."+name+"="+entityAlias+"."+name;
				}
				break;
			}
			
		}
	}
	

	
	
	private String[] buildStaticOperand(Operand operand) {
		String[] operandElement;
		
		logger.debug("IN");
		
		try {
			operandElement = operand.values;
		} finally {
			logger.debug("OUT");
		}		
		
		return operandElement;
	}
	
	private String buildFieldOperand(Operand operand, Query query, Map entityAliasesMaps) {
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
			
			
			datamartField = getDataSource().getModelStructure().getField( operand.values[0] );
			Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + operand.values[0] + "]");
			queryName = datamartField.getQueryName();
			logger.debug("where field query name [" + queryName + "]");
			
			rootEntity = datamartField.getParent().getRoot(); 
			logger.debug("where field root entity unique name [" + rootEntity.getUniqueName() + "]");
			
			if(!targetQueryEntityAliasesMap.containsKey(rootEntity.getUniqueName())) {
				logger.debug("Entity [" + rootEntity.getUniqueName() + "] require a new alias");
				rootEntityAlias = getNextAlias(entityAliasesMaps);
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
	
	private String buildParentFieldOperand(Operand operand, Query query, Map entityAliasesMaps) {
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

			datamartField = getDataSource().getModelStructure().getField( fieldName );
			Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + fieldName + "]");
			
			queryName = datamartField.getQueryName();
			logger.debug("where right-hand field query name [" + queryName + "]");
			
			rootEntity = datamartField.getParent().getRoot();
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
	
	private String buildQueryOperand(Operand operand) {
		String operandElement;
		
		logger.debug("IN");
		
		try {
			String subqueryId;
			
			logger.debug("where element right-hand field type [" + OPERAND_TYPE_SUBQUERY + "]");
			
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
	
	private String[] buildOperand(Operand operand, Query query, Map entityAliasesMaps) {
		String[] operandElement;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(operand, "Input parameter [operand] cannot be null in order to execute method [buildUserProvidedWhereField]");
			operandElement = new String[] {""};
			
			if(OPERAND_TYPE_STATIC.equalsIgnoreCase(operand.type)) {
				operandElement = buildStaticOperand(operand);
			} else if (OPERAND_TYPE_SUBQUERY.equalsIgnoreCase(operand.type)) {
				operandElement = new String[] {buildQueryOperand(operand)};
			} else if (OPERAND_TYPE_FIELD.equalsIgnoreCase(operand.type)) {
				operandElement = new String[] {buildFieldOperand(operand, query, entityAliasesMaps)};
			} else if (OPERAND_TYPE_PARENT_FIELD.equalsIgnoreCase(operand.type)) {
				operandElement = new String[] {buildParentFieldOperand(operand, query, entityAliasesMaps)};
			} else {
				Assert.assertUnreachable("Invalid operand type [" + operand.type+ "]");
			}
		} finally {
			logger.debug("OUT");
		}		
		return operandElement;
	}
	
	private String[] getTypeBoundedStaticOperand(Operand leadOperand, String operator, String[] operandValuesToBound) {
		String[] boundedValues = new String[operandValuesToBound.length];

		for (int i = 0; i < operandValuesToBound.length; i++) {
		
			String operandValueToBound = operandValuesToBound[i];
			String boundedValue = operandValueToBound;
			
			
			// calculated field
			// TODO check!!!! why not a OPERAND_TYPE_CALCUALTED_FIELD????
			if (leadOperand.values[0].contains("expression")) {
				String type = (leadOperand.values[0].substring(leadOperand.values[0].indexOf("type\":")+7, leadOperand.values[0].indexOf("\"}")));
				boundedValue = getValueBounded(operandValueToBound, type);
			}else if (OPERAND_TYPE_FIELD.equalsIgnoreCase(leadOperand.type) 
							|| OPERAND_TYPE_PARENT_FIELD.equalsIgnoreCase(leadOperand.type)) {
				
				IModelField datamartField = getDataSource().getModelStructure().getField(leadOperand.values[0]);
				boundedValue = getValueBounded(operandValueToBound, datamartField.getType());
			}

			boundedValues[i] = boundedValue;
		
		}
		
		return boundedValues;
	}
	
	
	
	
	private String getValueBounded(String operandValueToBound, String operandType) {
		
		String boundedValue = operandValueToBound;
		if (operandType.equalsIgnoreCase("STRING") || operandType.equalsIgnoreCase("CHARACTER")) {
			
			// if the value is already surrounded by quotes, does not neither add quotes nor escape quotes 
			if ( StringUtils.isBounded(operandValueToBound, "'") ) {
				boundedValue = operandValueToBound ;
			} else {
				operandValueToBound = StringUtils.escapeQuotes(operandValueToBound);
				StringUtils.bound(operandValueToBound, "'");
			}
		} else if(operandType.equalsIgnoreCase("TIMESTAMP") || operandType.equalsIgnoreCase("DATE")){
			boundedValue = parseDate(operandValueToBound);
		}
		
		return boundedValue;
	}
	
	/**
	 * Parse the date: get the user locale and format the date in the db format
	 * @param date the localized date
	 * @return the date in the db format
	 */
	private String parseDate(String date){
		String toReturn = "'" +date+ "'";
		String userDfString = (String)getParameters().get("userDateFormatPattern");
		String dbDfString = (String)getParameters().get("databaseDateFormatPattern");
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
	
	
	private String buildUserProvidedWhereField(WhereField whereField, Query query, Map entityAliasesMaps) {
		
		String whereClauseElement = "";
		String[] rightOperandElements;
		String[] leftOperandElements;
				
		logger.debug("IN");
		
		try {
			IConditionalOperator conditionalOperator = null;
			conditionalOperator = (IConditionalOperator)conditionalOperators.get( whereField.getOperator() );
			Assert.assertNotNull(conditionalOperator, "Unsopported operator " + whereField.getOperator() + " used in query definition");
			
			if(whereField.getLeftOperand().values[0].contains("expression")){
				whereClauseElement = buildInLineCalculatedFieldClause(whereField.getOperator(), whereField.getLeftOperand(), whereField.isPromptable(), whereField.getRightOperand(), query, entityAliasesMaps, conditionalOperator);
			}else{
				
				leftOperandElements = buildOperand(whereField.getLeftOperand(), query, entityAliasesMaps);
				
				if (OPERAND_TYPE_STATIC.equalsIgnoreCase(whereField.getRightOperand().type) 
						&& whereField.isPromptable()) {
					// get last value first (the last value edited by the user)
					rightOperandElements = whereField.getRightOperand().lastValues;
				} else {
					
					rightOperandElements = buildOperand(whereField.getRightOperand(), query, entityAliasesMaps);
				}
				
				if (OPERAND_TYPE_STATIC.equalsIgnoreCase(whereField.getLeftOperand().type) )  {
					leftOperandElements = getTypeBoundedStaticOperand(whereField.getRightOperand(), whereField.getOperator(), leftOperandElements);
				}
				
				if (OPERAND_TYPE_STATIC.equalsIgnoreCase(whereField.getRightOperand().type) )  {
					rightOperandElements = getTypeBoundedStaticOperand(whereField.getLeftOperand(), whereField.getOperator(), rightOperandElements);
				}
				
				whereClauseElement = conditionalOperator.apply(leftOperandElements[0], rightOperandElements);
			}
			
			logger.debug("where element value [" + whereClauseElement + "]");
		} finally {
			logger.debug("OUT");
		}
		
		
		return  whereClauseElement;
	}
	
	
	
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
	private String buildInLineCalculatedFieldClause(String operator, Operand leftOperand, boolean isPromptable, Operand rightOperand, Query query, Map entityAliasesMaps, IConditionalOperator conditionalOperator){
		String[] rightOperandElements;
				
		String expr = leftOperand.values[0].substring(leftOperand.values[0].indexOf("\"expression\":\"")+14);//.replace("\'", "");
		expr = expr.substring(0, expr.indexOf("\""));
		
		logger.debug("Left operand (of a inline calculated field) for the filter clause of the query: "+leftOperand.values[0]);
		logger.debug("Expression of a inline calculated field for the filter clause of the query: "+expr);

		
		//String expr = leftOperand.value.substring(15,leftOperand.value.indexOf("\",\"alias"));//.replace("\'", "");

		expr = parseInLinecalculatedField(expr, query, entityAliasesMaps);
				
		logger.debug("IN");
					
		if (OPERAND_TYPE_STATIC.equalsIgnoreCase(rightOperand.type) && isPromptable) {
			// get last value first (the last value edited by the user)
			rightOperandElements = rightOperand.lastValues;
		} else {
			rightOperandElements = buildOperand(rightOperand, query, entityAliasesMaps);
		}
		
		if (OPERAND_TYPE_STATIC.equalsIgnoreCase(rightOperand.type) )  {
			rightOperandElements = getTypeBoundedStaticOperand(leftOperand, operator, rightOperandElements);
		}
		
		return conditionalOperator.apply("("+expr+")", rightOperandElements);
	}
	
	
	public String parseInLinecalculatedField(String expr, Query query, Map entityAliasesMaps){
		List allSelectFields;
		IModelEntity rootEntity;
		IModelField datamartField;
		String queryName;
		String rootEntityAlias;
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		List<String> aliasEntityMapping = new  ArrayList<String>();
		List<String> aliases = new  ArrayList<String>();
		
		StringTokenizer stk = new StringTokenizer(expr, "+-|*/()");
		while(stk.hasMoreTokens()){
			String alias = stk.nextToken().trim();
			String uniqueName;
			allSelectFields = query.getSelectFields(false);
			for(int i=0; i<allSelectFields.size(); i++){
				if(allSelectFields.get(i).getClass().equals(DataMartSelectField.class) && ((DataMartSelectField)allSelectFields.get(i)).getAlias().equals(alias)){
					uniqueName=((DataMartSelectField)allSelectFields.get(i)).getUniqueName();
					datamartField = getDataSource().getModelStructure().getField(uniqueName);	
					queryName = datamartField.getQueryName();
					rootEntity = datamartField.getParent().getRoot(); 
					rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
					queryName = ((DataMartSelectField)allSelectFields.get(i)).getFunction().apply(rootEntityAlias+"."+queryName);
					aliasEntityMapping.add(queryName);
					aliases.add(alias);
					break;
				}
			}
		}
		
		String freshExpr = expr;
		int ind =0;
		int pos =0;
		stk = new StringTokenizer(expr.replace("\'", ""), "+-|*/()");
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
	
	/*
	private String buildUserProvidedWhereField(WhereField whereField, Query query, Map entityAliasesMaps) {
		
		String whereClauseElement;
		Map targetQueryEntityAliasesMap;
		String leftHandValue;
		String rightHandValue;
		DataMartField datamartField;
		DataMartEntity rootEntity;
		String queryName;
		String rootEntityAlias;
		
		
		logger.debug("IN");
		
		try {
			whereClauseElement = "";
			leftHandValue = "";
			rightHandValue = "";
		
			targetQueryEntityAliasesMap = (Map)entityAliasesMaps.get(query.getId());
			Assert.assertNotNull(targetQueryEntityAliasesMap, "Entity aliasses map for query [" + query.getId() + "] cannot be null in order to execute method [buildUserProvidedWhereField]");
			
			
			// build left-hand value
			logger.debug("processing where element left-hand field value ...");
			
			logger.debug("where left-hand field unique name [" + whereField.getUniqueName() + "]");
			
			datamartField = getDataMartModel().getDataMartModelStructure().getField(whereField.getUniqueName());
			Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + whereField.getOperand().toString() + "]");
			queryName = datamartField.getQueryName();
			logger.debug("where left-hand field query name [" + queryName + "]");
			
			rootEntity = datamartField.getParent().getRoot(); 
			logger.debug("where left-hand field root entity unique name [" + rootEntity.getUniqueName() + "]");
			
			if(!targetQueryEntityAliasesMap.containsKey(rootEntity.getUniqueName())) {
				logger.debug("Entity [" + rootEntity.getUniqueName() + "] require a new alias");
				rootEntityAlias = getNextAlias(entityAliasesMaps);
				logger.debug("A new alias has been generated [" + rootEntityAlias + "]");				
				targetQueryEntityAliasesMap.put(rootEntity.getUniqueName(), rootEntityAlias);
			}
			rootEntityAlias = (String)targetQueryEntityAliasesMap.get( rootEntity.getUniqueName() );		
			logger.debug("where left-hand field root entity alias [" + rootEntityAlias + "]");
			
			leftHandValue = rootEntityAlias + "." + queryName;
			logger.debug("where element left-hand field value [" + leftHandValue + "]");
			
			
			// build right-hand value
			logger.debug("processing where element right-hand field value ...");
			
			if(OPERAND_TYPE_FIELD.equalsIgnoreCase( whereField.getOperandType() ) ) {
				logger.debug("where element right-hand field type [" + OPERAND_TYPE_FIELD + "]");
				
				logger.debug("where right-hand field unique name [" + whereField.getOperand().toString() + "]");
				
				datamartField = getDataMartModel().getDataMartModelStructure().getField( whereField.getOperand().toString() );
				Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + whereField.getOperand().toString() + "]");
				queryName = datamartField.getQueryName();
				logger.debug("where right-hand field query name [" + queryName + "]");
				
				rootEntity = datamartField.getParent().getRoot(); 
				logger.debug("where right-hand field root entity unique name [" + rootEntity.getUniqueName() + "]");
				
				if(!targetQueryEntityAliasesMap.containsKey(rootEntity.getUniqueName())) {
					logger.debug("Entity [" + rootEntity.getUniqueName() + "] require a new alias");
					rootEntityAlias = getNextAlias(entityAliasesMaps);
					logger.debug("A new alias has been generated [" + rootEntityAlias + "]");				
					targetQueryEntityAliasesMap.put(rootEntity.getUniqueName(), rootEntityAlias);
				}
				rootEntityAlias = (String)targetQueryEntityAliasesMap.get( rootEntity.getUniqueName() );
				logger.debug("where right-hand field root entity alias [" + rootEntityAlias + "]");
				
				rightHandValue = rootEntityAlias + "." + queryName;
				logger.debug("where element right-hand field value [" + rightHandValue + "]");
				
			} else if(OPERAND_TYPE_PARENT_FIELD.equalsIgnoreCase( whereField.getOperandType() ) ) {
				String operand;
				String[] chunks;
				String parentQueryId;
				String fieldName;
				
				logger.debug("where element right-hand field type [" + OPERAND_TYPE_FIELD + "]");
				
				// it comes directly from the client side GUI. It is a composition of the parent query id and filed name, 
				// separated by a space
				operand = whereField.getOperand().toString();
				logger.debug("operand  is equals to [" + operand + "]");
				
				chunks = operand.split(" ");
				Assert.assertTrue(chunks.length >= 2, "Operand [" + chunks.toString() + "]does not contains enougth informations in order to resolve the reference to parent field");
				
				parentQueryId = chunks[0];
				logger.debug("where right-hand field belonging query [" + parentQueryId + "]");
				fieldName = chunks[1];
				logger.debug("where right-hand field unique name [" + fieldName + "]");
	
				datamartField = getDataMartModel().getDataMartModelStructure().getField( fieldName );
				Assert.assertNotNull(datamartField, "DataMart does not cantain a field named [" + fieldName + "]");
				
				queryName = datamartField.getQueryName();
				logger.debug("where right-hand field query name [" + queryName + "]");
				
				rootEntity = datamartField.getParent().getRoot();
				logger.debug("where right-hand field root entity unique name [" + rootEntity.getUniqueName() + "]");
				
				Map parentEntityAliases = (Map)entityAliasesMaps.get(parentQueryId);
				if(parentEntityAliases != null) {
					if(!parentEntityAliases.containsKey(rootEntity.getUniqueName())) {
						Assert.assertUnreachable("Filter [" + whereField.getUniqueName() + "] of subquery [" + query.getId() + "] refers to a non " +
								"existing parent query [" + parentQueryId+ "] entity [" + rootEntity.getUniqueName() + "]");
					}
					rootEntityAlias = (String)parentEntityAliases.get( rootEntity.getUniqueName() );
				} else {
					rootEntityAlias = "unresoved_alias";
					logger.warn("Impossible to get aliases map for parent query [" + parentQueryId +"]. Probably the parent query ha not been compiled yet");					
					logger.warn("Query [" + query.getId() +"] refers entities of its parent query [" + parentQueryId +"] so the generated statement wont be executable until the parent query will be compiled");					
				}
				logger.debug("where right-hand field root entity alias [" + rootEntityAlias + "]");
				
				rightHandValue = rootEntityAlias + "." + queryName;
				logger.debug("where element right-hand field value [" + rightHandValue + "]");
				
			} else if(OPERAND_TYPE_STATIC.equalsIgnoreCase( whereField.getOperandType() ) ) {
				
				logger.debug("where element right-hand field type [" + OPERAND_TYPE_STATIC + "]");
				
				if (whereField.isFree()) {
					// get last value first (the last value edited by the user)
					rightHandValue = whereField.getLastValue();
				} else {
					rightHandValue = whereField.getOperand().toString();
				}
				
				logger.debug("where right-hand field value [" + rightHandValue + "]");
				
				logger.debug("where right-hand field type [" + datamartField.getType() + "]");
				
				if(datamartField.getType().equalsIgnoreCase("String")) {
					if( !( whereField.IN.equalsIgnoreCase( whereField.getOperator() ) 
							|| whereField.IN.equalsIgnoreCase( whereField.getOperator() )
							|| whereField.NOT_IN.equalsIgnoreCase( whereField.getOperator() )
							|| whereField.BETWEEN.equalsIgnoreCase( whereField.getOperator() )
							|| whereField.NOT_BETWEEN.equalsIgnoreCase( whereField.getOperator() ) 
					)) {
						rightHandValue = "'" + rightHandValue + "'";
					} else {
						String[] items = rightHandValue.split(",");
						rightHandValue = "";
						for(int i = 0; i < items.length; i++) {
							rightHandValue += (i==0?"":",") + "'" + items[i] + "'";
						}					
					}
				}
				logger.debug("where element right-hand field value [" + rightHandValue + "]");
			} else if(OPERAND_TYPE_SUBQUERY.equalsIgnoreCase( whereField.getOperandType() ) ) {
				String subqueryId;
				
				logger.debug("where element right-hand field type [" + OPERAND_TYPE_SUBQUERY + "]");
				
				subqueryId = (String)whereField.getOperand();
				logger.debug("Referenced subquery [" + subqueryId + "]");
				
				rightHandValue = "Q{" + subqueryId + "}";
				rightHandValue = "( " + rightHandValue + ")";
				logger.debug("where element right-hand field value [" + rightHandValue + "]");
			} else {
				Assert.assertUnreachable("Unrecognized filter type: " + whereField.getOperandType());
			}		
			
			IConditionalOperator conditionalOperator = null;
			conditionalOperator = (IConditionalOperator)conditionalOperators.get( whereField.getOperator() );
			Assert.assertNotNull(conditionalOperator, "Unsopported operator " + whereField.getOperator() + " used in query definition");
			
			
			whereClauseElement = conditionalOperator.apply(leftHandValue, rightHandValue);
			logger.debug("where element value [" + whereClauseElement + "]");
		} finally {
			logger.debug("OUT");
		}
		
		
		return  whereClauseElement;
	}
	*/
	
	private String buildUserProvidedWhereClause(ExpressionNode filterExp, Query query, Map entityAliasesMaps) {
		String str = "";
		
		String type = filterExp.getType();
		if("NODE_OP".equalsIgnoreCase( type )) {
			for(int i = 0; i < filterExp.getChildNodes().size(); i++) {
				ExpressionNode child = (ExpressionNode)filterExp.getChildNodes().get(i);
				String childStr = buildUserProvidedWhereClause(child, query, entityAliasesMaps);
				if("NODE_OP".equalsIgnoreCase( child.getType() )) {
					childStr = "(" + childStr + ")";
				}
				str += (i==0?"": " " + filterExp.getValue());
				str += " " + childStr;
			}
		} else {
			WhereField whereField = query.getWhereFieldByName( filterExp.getValue() );
			str += buildUserProvidedWhereField(whereField, query, entityAliasesMaps);
		}
		
		return str;
	}
	
	private String buildHavingClause(Query query, Map entityAliasesMaps) {
		
		StringBuffer buffer = new StringBuffer();
		
		if( query.getHavingFields().size() > 0) {
			buffer.append("HAVING ");
			Iterator it = query.getHavingFields().iterator();
			while (it.hasNext()) {
				HavingField field = (HavingField) it.next();
								
				if(field.getLeftOperand().values[0].contains("expression")){
					IConditionalOperator conditionalOperator = null;
					conditionalOperator = (IConditionalOperator)conditionalOperators.get( field.getOperator() );
					Assert.assertNotNull(conditionalOperator, "Unsopported operator " + field.getOperator() + " used in query definition");

					String havingClauseElement =  buildInLineCalculatedFieldClause(field.getOperator(), field.getLeftOperand(), field.isPromptable(), field.getRightOperand(), query, entityAliasesMaps, conditionalOperator);
					buffer.append(havingClauseElement);
				}else{
						buffer.append( buildHavingClauseElement(field, query, entityAliasesMaps) );
				}
				
				if (it.hasNext()) {
					buffer.append(" " + field.getBooleanConnector() + " ");
				}
			}
		}
		
		return buffer.toString().trim();
	}
	
	private String buildHavingClauseElement(HavingField havingField, Query query, Map entityAliasesMaps) {
		
		String havingClauseElement;
		String[] leftOperandElements;
		String[] rightOperandElements;
				
		logger.debug("IN");
		
		try {
			IConditionalOperator conditionalOperator = null;
			conditionalOperator = (IConditionalOperator)conditionalOperators.get( havingField.getOperator() );
			Assert.assertNotNull(conditionalOperator, "Unsopported operator " + havingField.getOperator() + " used in query definition");
			
			leftOperandElements = buildOperand(havingField.getLeftOperand(), query, entityAliasesMaps);
			
			if (OPERAND_TYPE_STATIC.equalsIgnoreCase(havingField.getRightOperand().type) 
					&& havingField.isPromptable()) {
				// get last value first (the last value edited by the user)
				rightOperandElements = havingField.getRightOperand().lastValues;
			} else {
				rightOperandElements = buildOperand(havingField.getRightOperand(), query, entityAliasesMaps);
			}
			
			if (OPERAND_TYPE_STATIC.equalsIgnoreCase(havingField.getLeftOperand().type) )  {
				leftOperandElements = getTypeBoundedStaticOperand(havingField.getRightOperand(), havingField.getOperator(), leftOperandElements);
			}
			
			if (OPERAND_TYPE_STATIC.equalsIgnoreCase(havingField.getRightOperand().type) )  {
				rightOperandElements = getTypeBoundedStaticOperand(havingField.getLeftOperand(), havingField.getOperator(), rightOperandElements);
			}
			
			havingClauseElement = conditionalOperator.apply(leftOperandElements[0], rightOperandElements);
			logger.debug("Having clause element value [" + havingClauseElement + "]");
		} finally {
			logger.debug("OUT");
		}
		
		
		return  havingClauseElement;
	}
	
	
	private String buildWhereClause(Query query, Map entityAliasesMaps) {
	
		StringBuffer buffer = new StringBuffer();
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		
		if( query.getWhereClauseStructure() != null) {
			buffer.append("WHERE ");
			buffer.append( buildUserProvidedWhereClause(query.getWhereClauseStructure(), query, entityAliasesMaps) );
		}
		

		IModelStructure dataMartModelStructure = getDataSource().getModelStructure();
		IModelAccessModality dataMartModelAccessModality = getDataSource().getModelAccessModality();
		
		Iterator it = entityAliases.keySet().iterator();
		while(it.hasNext()){
			String entityUniqueName = (String)it.next();
			IModelEntity entity = dataMartModelStructure.getEntity( entityUniqueName );
			
			// check for condition filter on this entity
			List filters = dataMartModelAccessModality.getEntityFilterConditions(entity.getType());
			if(filters!=null){
				for(int i = 0; i < filters.size(); i++) {
					Filter filter = (Filter)filters.get(i);
					Set fields = filter.getFields();
					Properties props = new Properties();
					Iterator fieldIterator = fields.iterator();
					while(fieldIterator.hasNext()) {
						String fieldName = (String)fieldIterator.next();
						String entityAlias = (String)entityAliases.get(entityUniqueName);
						props.put(fieldName, entityAlias + "." + fieldName);
					}
					String filterCondition = null;
					try {
						filterCondition = StringUtils.replaceParameters(filter.getFilterCondition(), "F", props);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(filterCondition != null) {
						if(buffer.toString().length() > 0) {
							buffer.append(" and ");
						} else {
							buffer.append("where ");
						}
						buffer.append(filterCondition + " ");
					}
				}
				
				
				
				if(dataMartModelAccessModality.getRecursiveFiltering() == null 
						|| dataMartModelAccessModality.getRecursiveFiltering().booleanValue() == true) {
					//	check for condition filter on sub entities
					List subEntities = entity.getAllSubEntities();
					for(int i = 0; i < subEntities.size(); i++) {
						IModelEntity subEntity = (IModelEntity)subEntities.get(i);
						filters = dataMartModelAccessModality.getEntityFilterConditions(subEntity.getType());
						for(int j = 0; j < filters.size(); j++) {
							Filter filter = (Filter)filters.get(j);
							Set fields = filter.getFields();
							Properties props = new Properties();
							Iterator fieldIterator = fields.iterator();
							while(fieldIterator.hasNext()) {
								String fieldName = (String)fieldIterator.next();
								IModelField filed = null;
								Iterator subEntityFields = subEntity.getAllFields().iterator();
								while(subEntityFields.hasNext()) {
									filed = (IModelField)subEntityFields.next();
									if(filed.getQueryName().endsWith("." + fieldName)) break;
								}
								String entityAlias = (String)entityAliases.get(entityUniqueName);
								props.put(fieldName, entityAlias + "." + filed.getQueryName());
							}
							String filterCondition = null;
							try {
								filterCondition = StringUtils.replaceParameters(filter.getFilterCondition(), "F", props);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							if(filterCondition != null) {
								if(buffer.toString().length() > 0) {
									buffer.append(" and ");
								} else {
									buffer.append("where ");
								}
								buffer.append(filterCondition + " ");
							}
						}
					}
				}
			}
		}
		
		
		return buffer.toString().trim();
	}
	
	private String buildGroupByClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer = new StringBuffer();
		List groupByFields = query.getGroupByFields();
		String fieldName; 
		if(groupByFields == null ||groupByFields.size() == 0) {
			return "";
		}
		
		buffer.append("GROUP BY");
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		
		Iterator<AbstractSelectField> it = groupByFields.iterator();
		while( it.hasNext() ) {
			AbstractSelectField abstractSelectedField = it.next();
			
			if(abstractSelectedField.isInLineCalculatedField()){
				InLineCalculatedSelectField icf = (InLineCalculatedSelectField)abstractSelectedField;
				fieldName = parseInLinecalculatedField(icf.getExpression(), query, entityAliasesMaps);
			}else{
			
				DataMartSelectField groupByField = (DataMartSelectField)abstractSelectedField;
				IModelField datamartField = getDataSource().getModelStructure().getField(groupByField.getUniqueName());
				IModelEntity entity = datamartField.getParent().getRoot(); 
				String queryName = datamartField.getQueryName();
				if(!entityAliases.containsKey(entity.getUniqueName())) {
					entityAliases.put(entity.getUniqueName(), getNextAlias(entityAliasesMaps));
				}
				String entityAlias = (String)entityAliases.get( entity.getUniqueName() );
				fieldName = entityAlias + "." +queryName;
			}
			buffer.append(" " + fieldName);
			if( it.hasNext() ) {
				buffer.append(",");
			}
		}
		
		return buffer.toString().trim();
	}
	
	private List getOrderByFields(Query query) {
		List orderByFields = new ArrayList();
		Iterator it = query.getDataMartSelectFields(false).iterator();
		while( it.hasNext() ) {
			DataMartSelectField selectField = (DataMartSelectField)it.next();
			if(selectField.isOrderByField()) {
				orderByFields.add(selectField);
			}
		}
		return orderByFields;
	}
	
	private String buildOrderByClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer;
		Iterator it;
		DataMartSelectField selectField;
		
		it = getOrderByFields(query).iterator();		
		if(!it.hasNext()) {
			return "";
		}
		
		buffer = new StringBuffer();	
		buffer.append("ORDER BY");
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
					
		while( it.hasNext() ) {
			selectField = (DataMartSelectField)it.next();
			
			Assert.assertTrue(selectField.isOrderByField(), "Field [" + selectField.getUniqueName() +"] is not an orderBy filed");
			
			IModelField datamartField = getDataSource().getModelStructure().getField(selectField.getUniqueName());
			IModelEntity entity = datamartField.getParent().getRoot(); 
			String queryName = datamartField.getQueryName();
			if(!entityAliases.containsKey(entity.getUniqueName())) {
				entityAliases.put(entity.getUniqueName(), getNextAlias(entityAliasesMaps));
			}
			String entityAlias = (String)entityAliases.get( entity.getUniqueName() );
			String fieldName = entityAlias + "." + queryName;
			buffer.append(" " + selectField.getFunction().apply(fieldName));
			buffer.append(" " + (selectField.isAscendingOrder()?"ASC": "DESC") );
						
			if( it.hasNext() ) {
				buffer.append(",");
			}
		}
		
		return buffer.toString().trim();
	}
	
	
	public Set getSelectedEntities() {
		Set selectedEntities;
		Map entityAliasesMaps;
		Iterator entityUniqueNamesIterator;
		String entityUniqueName;
		IModelEntity entity;
		
		
		Assert.assertNotNull( getQuery(), "Input parameter 'query' cannot be null");
		Assert.assertTrue(! getQuery().isEmpty(), "Input query cannot be empty (i.e. with no selected fields)");
		
		selectedEntities = new HashSet();
		
		// one map of entity aliases for each queries (master query + subqueries)
		// each map is indexed by the query id
		entityAliasesMaps = new HashMap();
		
		// let's start with the query at hand
		entityAliasesMaps.put( getQuery().getId(), new HashMap());
		
		buildSelectClause( getQuery(), entityAliasesMaps);
		buildWhereClause( getQuery(), entityAliasesMaps);
		buildGroupByClause( getQuery(), entityAliasesMaps);
		buildOrderByClause( getQuery(), entityAliasesMaps);
		buildFromClause( getQuery(), entityAliasesMaps);
		
		Map entityAliases = (Map)entityAliasesMaps.get( getQuery().getId());
		entityUniqueNamesIterator = entityAliases.keySet().iterator();
		while(entityUniqueNamesIterator.hasNext()) {
			entityUniqueName = (String)entityUniqueNamesIterator.next();
			//entity = getDataMartModel().getDataMartModelStructure().getRootEntity( entityUniqueName );
			entity = getDataSource().getModelStructure().getEntity( entityUniqueName );
			selectedEntities.add(entity);
		}
		
		return selectedEntities;
	}
	/*
	 * internally used to generate the parametric statement string. Shared by the prepare method and the buildWhereClause method in order
	 * to recursively generate subquery statement string to be embedded in the parent query.
	 */
	private String compose(Query query, Map entityAliasesMaps) {
		String queryStr = null;
		String selectClause = null;
		whereClause = null;
		String groupByClause = null;
		String orderByClause = null;
		String fromClause = null;
		String havingClause = null;
		
		Assert.assertNotNull(query, "Input parameter 'query' cannot be null");
		Assert.assertTrue(!query.isEmpty(), "Input query cannot be empty (i.e. with no selected fields)");
				
		// let's start with the query at hand
		entityAliasesMaps.put(query.getId(), new HashMap());
		
		selectClause = buildSelectClause(query, entityAliasesMaps);
		whereClause = buildWhereClause(query, entityAliasesMaps);
		groupByClause = buildGroupByClause(query, entityAliasesMaps);
		orderByClause = buildOrderByClause(query, entityAliasesMaps);
		fromClause = buildFromClause(query, entityAliasesMaps);
		havingClause = buildHavingClause(query, entityAliasesMaps);
		 
		queryStr = selectClause + " " + fromClause + " " + whereClause + " " +  groupByClause + " " + havingClause + " " + orderByClause;
		
		Set subqueryIds;
		try {
			subqueryIds = StringUtils.getParameters(queryStr, "Q");
		} catch (IOException e) {
			throw new SpagoBIRuntimeException("Impossible to set parameters in query", e);
		}
		
		Iterator it = subqueryIds.iterator();
		while(it.hasNext()) {
			String id = (String)it.next();
			Query subquery = query.getSubquery(id);
			
			String subqueryStr = compose(subquery, entityAliasesMaps);
			queryStr = queryStr.replaceAll("Q\\{" + subquery.getId() + "\\}", subqueryStr);
		} 
		
		return queryStr;
	}
	
	public void prepare() {
		// TODO Auto-generated method stub
		String queryStr;
		
		// one map of entity aliases for each queries (master query + subqueries)
		// each map is indexed by the query id
		Map entityAliasesMaps = new HashMap();
		
		queryStr = compose(getQuery(), entityAliasesMaps);	

		
		if(getParameters() != null) {
			try {
				queryStr = StringUtils.replaceParameters(queryStr.trim(), "P", getParameters());
			} catch (IOException e) {
				throw new SpagoBIRuntimeException("Impossible to set parameters in query", e);
			}
			
		}	
		
		setQueryString(queryStr);
		
	}


	public String getQueryString() {		
		if(super.getQueryString() == null) {
			this.prepare();
		}
		
		return super.getQueryString();
	}
	
	public String getSqlQueryString() {
		// TODO Auto-generated method stub
		String sqlQuery = null;
		EntityManager em = dataSource.getEntityManager();
		java.sql.Connection connection = em.unwrap(java.sql.Connection.class);

		JPQL2SQLStatementRewriter queryRewriter;
		try {
			queryRewriter = new JPQL2SQLStatementRewriter(em);
			sqlQuery = queryRewriter.rewrite( getQueryString() );
		} finally {
			//if (em != null && em.isOpen()) em.close(); 
		}
		
		return sqlQuery;		
	}

	
}
