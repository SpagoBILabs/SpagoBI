/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.services.worksheet;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.qbe.query.AbstractSelectField;
import it.eng.qbe.query.ExpressionNode;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.query.serializer.SerializerFactory;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.services.core.ExecuteQueryAction;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class ExecuteWorksheetQueryAction extends ExecuteQueryAction{
	

	private static final long serialVersionUID = -9134072368475124558L;

	/**
	 * Gets the active query.. If no active query is specified
	 * returns the first query in the catalogue..
	 * The query is filtered: it applies some projection (the visible columns
	 * are specified in the request variable jsonVisibleSelectFields) and
	 * some selection (the rows are specified in the variable optionalUserFilters)
	 * If the worksheet has been built with the smart filter the query must be transformed 
	 * @return the filtered query
	 */
	@Override
	public Query getQuery() {
		QbeEngineInstance engineInstance = getEngineInstance();
		Query clonedQuery=null;
		Query activeQuery = engineInstance.getActiveQuery();
		if (activeQuery == null) {
			activeQuery = engineInstance.getQueryCatalogue().getFirstQuery();
		}
		try {
			if( getEngineInstance().getFormState()==null || getEngineInstance().getFormState().getFormStateValues()==null){
				//clone the query
				String store = ((JSONObject)SerializerFactory.getSerializer("application/json").serialize(activeQuery, getEngineInstance().getDataSource(), getLocale())).toString();
				clonedQuery = SerializerFactory.getDeserializer("application/json").deserializeQuery(store, getEngineInstance().getDataSource());
			}else{
				//the builder engine is the smart filter, so the query must be transformed 
				clonedQuery = getFilteredQuery(activeQuery,  getEngineInstance().getFormState().getFormStateValues());
			}
			
			if(getEngineInstance().getActiveQuery() == null || !getEngineInstance().getActiveQuery().getId().equals(clonedQuery.getId())) {
				logger.debug("Query with id [" + activeQuery.getId() + "] is not the current active query. A new statment will be generated");
				getEngineInstance().setActiveQuery(clonedQuery);
			}
			
			applyFilters(clonedQuery);
			return clonedQuery;
		} catch (Exception e) {
			activeQuery = null;
		}
		return activeQuery;
	}
	
	
	private void applyFilters(Query query) throws JSONException{
		List<String> visibleSelectFields = new ArrayList<String>();
		JSONArray jsonVisibleSelectFields  = null;
		try {
			jsonVisibleSelectFields = getAttributeAsJSONArray(QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS);
			if (jsonVisibleSelectFields != null) {
				for (int i = 0; i < jsonVisibleSelectFields.length(); i++) {
					JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(i);
					visibleSelectFields.add(jsonVisibleSelectField.getString("alias"));
				}	
			}
		} catch (Exception e) {
			logger.debug("The optional attribute visibleselectfields is not valued. No visible select field selected.. All fields will be taken..");
		}
		
		//Apply optional filters
		JSONObject optionalUserFilters= null;
		try {
			optionalUserFilters = getAttributeAsJSONObject( QbeEngineStaticVariables.OPTIONAL_FILTERS );
			logger.debug("Found those optional filters "+optionalUserFilters);
		} catch (Exception e) {
			logger.debug("Found no optional filters");
		}
			
		if(jsonVisibleSelectFields!=null || optionalUserFilters!=null){
							
			//hide the fields not present in the request parameter visibleselectfields
			if(visibleSelectFields!=null && visibleSelectFields.size()>0){
				List<AbstractSelectField> selectedField = query.getSelectFields(true);
				for(int i=0; i<selectedField.size(); i++){
					String alias = selectedField.get(i).getAlias();
					if(!visibleSelectFields.contains(alias)){
						selectedField.get(i).setVisible(false);
						visibleSelectFields.remove(alias);
					}else{
						selectedField.get(i).setVisible(true);
					}
				}
			}

			if(optionalUserFilters!=null){			
				applyOptionalFilters(query, optionalUserFilters);			
			}
		}		
	}
	
	/**
	 * Get the query and add the where fields defined in the optionalUserFilters
	 * @param query
	 * @param optionalUserFilters
	 * @throws JSONException
	 */
	private void applyOptionalFilters(Query query, JSONObject optionalUserFilters) throws JSONException{
		String[] fields = JSONObject.getNames(optionalUserFilters);
		ExpressionNode leftExpression = query.getWhereClauseStructure();
		for(int i=0; i<fields.length; i++){
			String fieldName = fields[i];
			JSONArray valuesArray = optionalUserFilters.getJSONArray(fieldName);

			//if the filter has some value
			if(valuesArray.length()>0){

				String[] values = new String[1];
				values[0] =fieldName;

				Operand leftOperand = new Operand(values,fieldName, AbstractStatement.OPERAND_TYPE_FIELD, values,values);

				values = new String[valuesArray.length()];
				for(int j=0; j<valuesArray.length(); j++){
					values[j] = valuesArray.getString(j);
				}

				Operand rightOperand = new Operand(values,fieldName, AbstractStatement.OPERAND_TYPE_STATIC, values, values);

				String operator = "NOT EQUALS TO";
				if(valuesArray.length()>0){
					operator="IN";
				}

				query.addWhereField("OptionalFilter"+i, "OptionalFilter"+i, false, leftOperand, operator, rightOperand, "AND");



				ExpressionNode filterNode = new ExpressionNode("NO_NODE_OP","$F{OptionalFilter"+i+"}");

				//build the where clause tree 
				if(leftExpression==null){
					leftExpression = filterNode;
				}else{
					ExpressionNode operationNode = new ExpressionNode("NODE_OP", "AND");
					operationNode.addChild(leftExpression);
					operationNode.addChild(filterNode);
					leftExpression = operationNode;
				}
			}
		}
		query.setWhereClauseStructure(leftExpression);
	}
	
	@Override
	protected IStatement getStatement(Query query){
		IStatement statement =  getDataSource().createStatement( query );
		return statement;
	}
}
