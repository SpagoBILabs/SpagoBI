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
package it.eng.spagobi.engines.worksheet.services.runtime;

import it.eng.qbe.query.CriteriaConstants;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.crosstable.CrossTab;
import it.eng.spagobi.engines.qbe.services.formviewer.ExecuteMasterQueryAction;
import it.eng.spagobi.engines.qbe.services.worksheet.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheetDefinition;
import it.eng.spagobi.engines.qbe.worksheet.bo.Field;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.SelectableFieldsBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.uuid.UUIDGenerator;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class LoadCrosstabAction extends AbstractWorksheetEngineAction {	
	
	// INPUT PARAMETERS
	private static final String CROSSTAB_DEFINITION = QbeEngineStaticVariables.CROSSTAB_DEFINITION;
	private static final String FORM_STATE = ExecuteMasterQueryAction.FORM_STATE;
	private static final String OPTIONAL_FILTERS = QbeEngineStaticVariables.OPTIONAL_FILTERS;
	public static final String SHEET = "sheetName";

	private static final long serialVersionUID = -5780454016202425492L;

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(LoadCrosstabAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		IDataStore dataStore = null;
		JSONObject jsonFormState = null;
		CrosstabDefinition crosstabDefinition = null;
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("WorksheetEngine.loadCrosstabAction.totalTime");
			
			JSONObject crosstabDefinitionJSON = getAttributeAsJSONObject( CROSSTAB_DEFINITION );
			jsonFormState = loadSmartFilterFormValues();
			logger.debug("Form state retrieved as a string: " + jsonFormState);
			
//			//build the query filtered for the smart filter
//			if (jsonFormState != null) {
//				query = getFilteredQuery(query, jsonFormState);
//			}
			
			Assert.assertNotNull(crosstabDefinitionJSON, "Parameter [" + CROSSTAB_DEFINITION + "] cannot be null in oder to execute " + this.getActionName() + " service");
			logger.debug("Parameter [" + crosstabDefinitionJSON + "] is equals to [" + crosstabDefinitionJSON.toString() + "]");
			crosstabDefinition = (CrosstabDefinition) SerializationManager.deserialize(crosstabDefinitionJSON, "application/json", CrosstabDefinition.class);
			crosstabDefinition.setCellLimit( new Integer((String) ConfigSingleton.getInstance().getAttribute("QBE.QBE-CROSSTAB-CELLS-LIMIT.value")) );
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			// get temporary table name
			String tableName = this.getTemporaryTableName();
			// set all filters into dataset, because dataset's getSignature() and persist() methods may depend on them
			WorksheetEngineInstance engineInstance = getEngineInstance();
			IDataSet dataset = engineInstance.getDataSet();
			Map<String, List<String>> filters = getAllFilters();
			if (dataset.hasBehaviour(FilteringBehaviour.ID)) {
				FilteringBehaviour filteringBehaviour = (FilteringBehaviour) dataset.getBehaviour(FilteringBehaviour.ID);
				filteringBehaviour.setFilters(filters);
			}
			
			if (dataset.hasBehaviour(SelectableFieldsBehaviour.ID)) {
				List<String> fields = getAllFields();
				SelectableFieldsBehaviour selectableFieldsBehaviour = (SelectableFieldsBehaviour) dataset.getBehaviour(SelectableFieldsBehaviour.ID);
				selectableFieldsBehaviour.setSelectedFields(fields);
			}
			
			// persist dataset into temporary table	
			IDataSetTableDescriptor descriptor = this.persistDataSet(tableName);
			// build SQL query against temporary table
			List<WhereField> whereFields = new ArrayList<WhereField>();
			if (!dataset.hasBehaviour(FilteringBehaviour.ID)) {
				List<WhereField> temp = transformIntoWhereClauses(filters);
				whereFields.addAll(temp);
			}
			String sheetName = this.getAttributeAsString(SHEET);
			Map<String, List<String>> sheetFilters = getSheetFilters(sheetName);
			List<WhereField> temp = transformIntoWhereClauses(sheetFilters);
			whereFields.addAll(temp);
			
			temp = getOptionalFilters();
			whereFields.addAll(temp);
			
			String worksheetQuery = this.buildSqlStatement(crosstabDefinition, descriptor, whereFields);
			// execute SQL query against temporary table
			dataStore = this.executeWorksheetQuery(worksheetQuery, null, null);
			// serialize crosstab
			CrossTab crossTab = new CrossTab(dataStore, crosstabDefinition);
			JSONObject crossTabDefinition = crossTab.getJSONCrossTab();
			
			try {
				writeBackToClient( new JSONSuccess(crossTabDefinition) );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch(Throwable t) {
			errorHitsMonitor = MonitorFactory.start("WorksheetEngine.errorHits");
			errorHitsMonitor.stop();
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			if (totalTimeMonitor != null) totalTimeMonitor.stop();
			logger.debug("OUT");
		}	
	}

	private List<String> getAllFields() {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		List<Field> fields = workSheetDefinition.getAllFields();
		Iterator<Field> it = fields.iterator();
		List<String> toReturn = new ArrayList<String>();
		while (it.hasNext()) {
			Field field = it.next();
			toReturn.add(field.getEntityId());
		}
		return toReturn;
	}

	/**
	 * Build the sql statement to query the temporary table 
	 * @param crosstabDefinition definition of the crosstab
	 * @param descriptor the temporary table descriptor
	 * @param tableName the temporary table name
	 * @return the sql statement to query the temporary table 
	 */
	protected String buildSqlStatement(CrosstabDefinition crosstabDefinition,
			IDataSetTableDescriptor descriptor, List<WhereField> filters) {
		return CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, descriptor, filters);
	}

	/**
	 * Loads the values of the form if the calling engine is smart filter
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject loadSmartFilterFormValues() throws JSONException {
		String jsonEncodedFormState = getAttributeAsString( FORM_STATE );
		if ( jsonEncodedFormState != null ) {
			return new JSONObject(jsonEncodedFormState);
		}
		return null;
	}
	
	public static List<WhereField> transformIntoWhereClauses(
			Map<String, List<String>> filters) throws JSONException {
		
		List<WhereField> whereFields = new ArrayList<WhereField>();
		
		Set<String> keys = filters.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String aFilterName = it.next();
			List<String> values = filters.get(aFilterName);
			if (values != null && values.size() > 0) {
				String operator = values.size() > 1 ? CriteriaConstants.IN : CriteriaConstants.EQUALS_TO;
				Operand leftOperand = new Operand(new String[] {aFilterName}, null, AbstractStatement.OPERAND_TYPE_FIELD, null, null);
				String[] valuesArray = (String[]) values.toArray();
				Operand rightOperand = new Operand(valuesArray, null, AbstractStatement.OPERAND_TYPE_STATIC, null, null);
				WhereField whereField = new WhereField(UUIDGenerator.getInstance().generateRandomBasedUUID().toString(), 
						aFilterName, false, leftOperand, operator, rightOperand, "AND");

				whereFields.add(whereField);
			}
		}
		
		return whereFields;
	}
	

	public List<WhereField> getOptionalFilters() throws JSONException {
		JSONObject optionalUserFilters = getAttributeAsJSONObject(OPTIONAL_FILTERS);
		if (optionalUserFilters != null) {
			return transformIntoWhereClauses(optionalUserFilters);
		} else {
			return new ArrayList<WhereField>();
		}
	}
	
	
	private static List<WhereField> transformIntoWhereClauses(
			JSONObject optionalUserFilters) throws JSONException {
		String[] fields = JSONObject.getNames(optionalUserFilters);
		List<WhereField> whereFields = new ArrayList<WhereField>();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i];
			JSONArray valuesArray = optionalUserFilters.getJSONArray(fieldName);

			// if the filter has some value
			if (valuesArray.length() > 0) {
				String[] values = new String[1];
				values[0] = fieldName;

				Operand leftOperand = new Operand(values, fieldName,
						AbstractStatement.OPERAND_TYPE_FIELD, values, values);

				values = new String[valuesArray.length()];
				for (int j = 0; j < valuesArray.length(); j++) {
					values[j] = valuesArray.getString(j);
				}

				Operand rightOperand = new Operand(values, fieldName,
						AbstractStatement.OPERAND_TYPE_STATIC, values, values);

				String operator = "EQUALS TO";
				if (valuesArray.length() > 1) {
					operator = "IN";
				}

				whereFields.add(new WhereField("OptionalFilter" + i,
						"OptionalFilter" + i, false, leftOperand, operator,
						rightOperand, "AND"));
			}
		}
		return whereFields;
	}
	
}
