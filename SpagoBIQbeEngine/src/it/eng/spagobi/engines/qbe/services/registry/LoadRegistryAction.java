/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.services.registry;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.IStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.engines.qbe.registry.serializer.RegistryJSONDataWriter;
import it.eng.spagobi.engines.qbe.services.core.ExecuteQueryAction;
import it.eng.spagobi.engines.qbe.template.QbeTemplate;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;

import java.util.Iterator;
import java.util.List;

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
			request.setAttribute(START, new Integer(0));
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
			while (it.hasNext()) {
				Column column = it.next();
				getMandatoryMetadata(column);
				getColumnsInfos(column);
				IModelField field = getColumnModelField(column, entity);
				if (field == null) {
					logger.error("Field " + column.getField() + " not found!!");
				} else {
					orderCol = column.getSorter();
					query.addSelectFiled(field.getUniqueName(), "NONE", field.getPropertyAsString("label"), true, true, false, orderCol, field.getPropertyAsString("format"));
				}
			}
		} finally {
			logger.debug("OUT");
		}
		return query;
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
