/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.services.registry;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.query.CriteriaConstants;
import it.eng.qbe.query.ExpressionNode;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.statement.IStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Filter;
import it.eng.spagobi.engines.qbe.registry.parser.RegistryConfigurationXMLParser;
import it.eng.spagobi.engines.qbe.registry.serializer.RegistryJSONDataWriter;
import it.eng.spagobi.engines.qbe.services.core.ExecuteQueryAction;
import it.eng.spagobi.engines.qbe.template.QbeTemplate;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.ParametersDecoder;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */

public class LoadRegistryAction extends ExecuteQueryAction {
	
	private static final long serialVersionUID = -642121076148276452L;
	private String ID_COLUMN = "ID_COLUMN";
	
	private JSONArray mandatories = new JSONArray();
	private JSONArray columnsInfos = new JSONArray();
	private String columnMaxSize = null;

	@Override
	public Query getQuery() {
		Query query = buildQuery();
		return query;
	}
	
	@Override
	protected IStatement getStatement(Query query){
		IStatement statement =  getDataSource().createStatement( query );
		return statement;
	}
	
	@Override
	public void service(SourceBean request, SourceBean response)  {

		try {
			if(!request.containsAttribute(START))
			request.setAttribute(START, new Integer(0));
			if(!request.containsAttribute(LIMIT))
			request.setAttribute(LIMIT, Integer.MAX_VALUE);
		} catch (SourceBeanException e) {
			throw new SpagoBIEngineServiceException(getActionName(), e);
		}
		super.service(request, response);
	}
	
	@Override
	public JSONObject serializeDataStore(IDataStore dataStore) {
		RegistryJSONDataWriter dataSetWriter = new RegistryJSONDataWriter();
		JSONObject gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
		setMandatoryMetadata(gridDataFeed);
		setColumnMaxSize(gridDataFeed);
		setColumnsInfos(gridDataFeed);
		return gridDataFeed;
	}
	private void setMandatoryMetadata(JSONObject gridDataFeed){
		try {
			((JSONObject)gridDataFeed.get("metaData")).put("mandatory", mandatories);

		} catch (JSONException e) {
			logger.error("Error setting mandatory informations "+e.getMessage() );
		}
	}
	private void setColumnsInfos(JSONObject gridDataFeed){
		try {
			((JSONObject)gridDataFeed.get("metaData")).put("columnsInfos", columnsInfos);

		} catch (JSONException e) {
			logger.error("Error setting columns size informations "+e.getMessage() );
		}
	}
	private void setColumnMaxSize(JSONObject gridDataFeed){
		try {
			((JSONObject)gridDataFeed.get("metaData")).put("maxSize", columnMaxSize);

		} catch (JSONException e) {
			logger.error("Error setting max columns size informations "+e.getMessage() );
		}
	}
	private void getMandatoryMetadata(Column column){
		try {
			//mandatory management
			String mandatoryColumn = column.getMandatoryColumn();
			String mandatoryValue = column.getMandatoryValue();
			JSONObject mandatory = new JSONObject();

			if(mandatoryColumn != null && mandatoryValue != null){
				mandatory.put("mandatoryColumn", mandatoryColumn);
				mandatory.put("mandatoryValue", mandatoryValue);
				mandatory.put("column", column.getField());			
				mandatories.put(mandatory);
			}
		} catch (JSONException e) {
			logger.error("Error getting mandatory informations from template "+e.getMessage() );
		}
	}
	private void getColumnsInfos(Column column){
		try {
			Integer size = column.getSize();
			String sizeColumn = column.getField();
			boolean unsigned = column.isUnsigned();
			JSONObject infoObj = new JSONObject();
			
			infoObj.putOpt("sizeColumn", sizeColumn);
			infoObj.putOpt("size", size);	

			infoObj.putOpt("unsigned", unsigned);
			if(size != null || unsigned != false){
				columnsInfos.put(infoObj);
			}
			
		} catch (JSONException e) {
			logger.error("Error getting size column informations from template "+e.getMessage() );
		}
	}
	private Query buildQuery() {
		logger.debug("IN");
		Query query = null;
		try {
			QbeEngineInstance qbeEngineInstance = getEngineInstance();
			Map env = qbeEngineInstance.getEnv();
			
			query = new Query();
			query.setDistinctClauseEnabled(false);
			IModelEntity entity = getSelectedEntity();

			QbeEngineInstance engineInstance = getEngineInstance();
			QbeTemplate template = engineInstance.getTemplate();
			RegistryConfiguration registryConfig = (RegistryConfiguration) template.getProperty("registryConfiguration");
			List<Column> columns = registryConfig.getColumns();
			columnMaxSize = registryConfig.getColumnsMaxSize();
			Iterator<Column> it = columns.iterator();
			String orderCol = null;
			
			Map<String, String> fieldNameIdMap = new HashMap<String, String>();
			
			while (it.hasNext()) {
				Column column = it.next();
				getMandatoryMetadata(column);
				getColumnsInfos(column);
				IModelField field = getColumnModelField(column, entity);
				if (field == null) {
					logger.error("Field " + column.getField() + " not found!!");
				} else {
					orderCol = column.getSorter();
					String name = field.getPropertyAsString("label");
					if(name==null || name.length()==0){
						name = field.getName();
					}
					query.addSelectFiled(field.getUniqueName(), "NONE", field.getName(), true, true, false, orderCol, field.getPropertyAsString("format"));
					fieldNameIdMap.put(column.getField(), field.getUniqueName());
				}
			}
			
			// get Drivers and filters
			
			List<RegistryConfiguration.Filter> filters =  registryConfig.getFilters();
			int i= 0;
			ArrayList<ExpressionNode> expressionNodes = new ArrayList<ExpressionNode>();
			for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
				Filter filter = (Filter) iterator.next();
				addFilter(i, query, env, fieldNameIdMap, filter, expressionNodes);
				i++;
			}
			// put together expression nodes
			if(expressionNodes.size()==1){
				query.setWhereClauseStructure(expressionNodes.get(0));
			}
			else if(expressionNodes.size()>1){
				ExpressionNode exprNodeAnd = new ExpressionNode("NODE_OP", "AND");
				exprNodeAnd.setChildNodes(expressionNodes);
				query.setWhereClauseStructure(exprNodeAnd);
			}
			
		} finally {
			logger.debug("OUT");
		}
		return query;
	}
	
	
	private void addSort(int i, Query query, Map env, Map<String,String> fieldNameIdMap,Filter filter, ArrayList<ExpressionNode> expressionNodes) {
		logger.debug("IN");
		if(requestContainsAttribute("sort")){
			String sortField = getAttributeAsString("sort");
			logger.debug("Sort by "+sortField);

			//query.getO
			
			
			// sorting by 
			
		}
		
		logger.debug("OUT");		
	}

	private void addFilter(int i, Query query, Map env, Map<String,String> fieldNameIdMap,Filter filter, ArrayList<ExpressionNode> expressionNodes) {
		logger.debug("IN");
		
		ExpressionNode node = query.getWhereClauseStructure();
		ExpressionNode nodeToInsert = new ExpressionNode("NODE_OP", "AND");

		// in case it is a driver
		if(filter.getPresentationType().equals(RegistryConfigurationXMLParser.PRESENTATION_TYPE_DRIVER)){
			String driverName = filter.getDriverName();
			String fieldName = filter.getField(); 

			Object value = env.get(driverName);
			
			if(value != null && !value.toString().equals("")){
				
				//TODO, change this behaviour
				if(value.toString().contains(",")){
					value = "{,{"+value+"}}";
				}
				List valuesList = new ParametersDecoder().decode(value.toString());
				String[] valuesArr = new String[valuesList.size()];
				
				for (int j = 0; j < valuesList.size(); j++) {
					String val = valuesList.get(j).toString();
					valuesArr[j] = val;
				}
				
				logger.debug("Set filter from analytical deriver "+driverName+": "+filter.getField()+"="+value);

				String fieldId = fieldNameIdMap.get(fieldName);
				String[] fields = new String[]{fieldId};
				WhereField.Operand left = new WhereField.Operand(fields, "driverName", AbstractStatement.OPERAND_TYPE_SIMPLE_FIELD, null, null);

				WhereField.Operand right = new WhereField.Operand(valuesArr, "value", AbstractStatement.OPERAND_TYPE_STATIC, null, null);

				if(valuesArr.length>1){
					query.addWhereField("Driver_"+i, driverName, false, left, CriteriaConstants.IN, right, "AND");						
				}
				else{
					query.addWhereField("Driver_"+i, driverName, false, left, CriteriaConstants.EQUALS_TO, right, "AND");					
				}
							
				ExpressionNode newFilterNode = new ExpressionNode("NODE_CONST", "$F{" + "Driver_"+i + "}");
				//query.setWhereClauseStructure(newFilterNode);
				expressionNodes.add(newFilterNode);
			}
			
			//query.setWhereClauseStructure(whereClauseStructure)
		}
		// in case it is a filter and has a value setted
		else if(requestContainsAttribute(filter.getField())){

			String value = getAttribute(filter.getField()).toString();			
			if(value != null && !value.equalsIgnoreCase("")){
				logger.debug("Set filter "+filter.getField()+"="+value);

				String fieldId = fieldNameIdMap.get(filter.getField());
				String[] fields = new String[]{fieldId};
				String[] values = new String[]{value};
				
				WhereField.Operand left = new WhereField.Operand(fields, "filterName", AbstractStatement.OPERAND_TYPE_SIMPLE_FIELD, null, null);

				WhereField.Operand right = new WhereField.Operand(values, "value", AbstractStatement.OPERAND_TYPE_STATIC, null, null);

				// if filter type is manual use it as string starting, else as equals
				if(filter.getPresentationType().equals(RegistryConfigurationXMLParser.PRESENTATION_TYPE_COMBO)){
					query.addWhereField("Filter_"+i, filter.getField(), false, left, CriteriaConstants.EQUALS_TO, right, "AND");
				}
				else{
					query.addWhereField("Filter_"+i, filter.getField(), false, left, CriteriaConstants.STARTS_WITH, right, "AND");					
				}
							
				ExpressionNode newFilterNode = new ExpressionNode("NODE_CONST", "$F{" + "Filter_"+i + "}");
				//query.setWhereClauseStructure(newFilterNode);
				expressionNodes.add(newFilterNode);

			}
		}
		logger.debug("OUT");
	}

	
	
	
	
	
	
	

	private IModelField getColumnModelField(Column column, IModelEntity entity) {
		if (column.getSubEntity() != null) { // in case it is a subEntity attribute, look for the field inside it
			String entityUName = entity.getUniqueName();
			String subEntityKey = entityUName.substring(0, entityUName.lastIndexOf("::")) + "::" + column.getSubEntity() + "(" + column.getForeignKey() + ")";
			IModelEntity subEntity = entity.getSubEntity(subEntityKey);
			if (subEntity == null) {
				throw new SpagoBIEngineServiceException(getActionName(), "Sub-entity [" + column.getSubEntity() + "] not found in entity [" + entity.getName() + "]!");
			}
			entity = subEntity;
		}
		logger.debug("Looking for attribute " + column.getField() + " in entity " + entity.getName() + " ...");
		List<IModelField> fields = entity.getAllFields();
		Iterator<IModelField> it = fields.iterator();
		while (it.hasNext()) {
			IModelField field = it.next();
			if (field.getName().equals(column.getField())) {
				return field;
			}
		}
		return null;
	}

	private IModelEntity getSelectedEntity() {
		logger.debug("IN");
		IModelEntity entity = null;
		try {
			IDataSource ds = getDataSource();
			IModelStructure structure = ds.getModelStructure();
			QbeEngineInstance engineInstance = getEngineInstance();
			QbeTemplate template = engineInstance.getTemplate();
			if (template.isComposite()) { // composite Qbe is not supported
				logger.error("Template is composite. This is not supported by the Registry engine");
				throw new SpagoBIEngineServiceException(getActionName(), "Template is composite. This is not supported by the Registry engine");
			}
			// takes the only datamart's name configured
			String modelName = (String) template.getDatamartNames().get(0);
			RegistryConfiguration registryConfig = (RegistryConfiguration) template.getProperty("registryConfiguration");
			String entityName = registryConfig.getEntity();			
			
			int index = entityName.lastIndexOf(".");
			entityName = entityName + "::" + entityName.substring(index + 1);  // entity name is something like it.eng.Store::Store
			logger.debug("Looking for entity [" + entityName + "] in model [" + modelName + "] ...");
			entity = structure.getRootEntity(modelName, entityName);
			logger.debug("Entity [" + entityName + "] was found");
			if (entity == null) {
				logger.error("Entity [" + entityName + "] not found!");
				throw new SpagoBIEngineServiceException(getActionName(), "Entity [" + entityName + "] not found!");
			}
		} finally {
			logger.debug("OUT");
		}
		return entity;
	}

}
