/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.qbe.statement;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.SimpleSelectField;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */

public class AbstractSelectStatementClause extends AbstractStatementClause{
	
	protected String[] statementFields;
	protected int index;
	protected Map entityAliases;
	public static final String SELECT = "SELECT";
	public static final String DISTINCT = "DISTINCT";
	public static transient Logger logger = Logger.getLogger(AbstractStatementClause.class);
	
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
		
		
		rootEntityAlias = getEntityAlias(rootEntity, entityAliases, entityAliasesMaps);
		
		logger.debug("select field root entity alias [" + rootEntityAlias + "]");
			
		selectClauseElement = parentStatement.getFieldAlias(rootEntityAlias, queryName);
		
		logger.debug("select clause element before aggregation [" + selectClauseElement + "]");
		
		selectClauseElement = selectField.getFunction().apply(selectClauseElement);
		logger.debug("select clause element after aggregation [" + selectClauseElement + "]");
		
		
		statementFields[index] = " " + selectClauseElement;
		index++;
		
		logger.debug("select clause element succesfully added to select clause");
	}

}
