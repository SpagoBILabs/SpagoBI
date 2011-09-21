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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.qbe.query.CriteriaConstants;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.FormState;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.qbe.worksheet.Sheet;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheetDefinition;
import it.eng.spagobi.engines.qbe.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.services.runtime.LoadCrosstabAction;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class LoadWorksheetCrosstabAction extends LoadCrosstabAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6934324402054147606L;

	// INPUT PARAMETERS
	public static final String SHEET = "sheetName";
	
	/**
	 * Loads the values of the form if the calling engine is smart filter
	 * @return
	 * @throws JSONException
	 */
//	@Override
//	protected JSONObject loadSmartFilterFormValues() throws JSONException{
//		FormState formState = getEngineInstance().getFormState();
//		if (formState == null) {
//			return null;
//		} else {
//			return  formState.getFormStateValues();
//		}
//	}
	
	
	/**
	 * Build the sql statement for the temporary table...
	 * Apply also the filters
	 * @param crosstabDefinition definition of the crosstab
	 * @param baseQuery base query
	 * @param sqlQuery the sql rappresentation of the base query
	 * @param stmt the qbe statement
	 * @return
	 * @throws JSONException
	 */
//	@Override
//	protected String buildSqlStatement(CrosstabDefinition crosstabDefinition, Query baseQuery, String sqlQuery, IStatement stmt) throws JSONException {
//		//optional filters specified by the user (now used in the worksheet)
//		JSONObject optionalUserFilters = getAttributeAsJSONObject(QbeEngineStaticVariables.OPTIONAL_FILTERS);
//		logger.debug("Found those optional filters " + optionalUserFilters);
//		List<WhereField> whereFields = new ArrayList<WhereField>();
//		String sheetName = this.getAttributeAsString(SHEET);
//		whereFields.addAll(getMandatoryFilters(this.getEngineInstance(), sheetName));
//		whereFields.addAll(getOptionalFilters(optionalUserFilters));
//		
//		return CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, baseQuery, whereFields, sqlQuery, stmt);
//	}
	
//	public static List<WhereField> getMandatoryFilters(WorksheetEngineInstance engineInstance, String sheetName) throws JSONException {
//		WorkSheetDefinition worksheetDefinition = engineInstance.getWorkSheetDefinition();
//		Sheet sheet = worksheetDefinition.getSheetConfiguration(sheetName);
//		if (sheet == null) {
//			throw new SpagoBIEngineRuntimeException("Sheet with name " + sheetName + " not found!!");
//		}
//		
//		List<WhereField> toReturn = new ArrayList<WhereField>();
//		List<Attribute> attributes = worksheetDefinition.getGlobalFilters();
//		toReturn.addAll(transformIntoWhereClauses(attributes));
//		attributes = sheet.getFilteringAttributes();
//		toReturn.addAll(transformIntoWhereClauses(attributes));
//		return toReturn;
//	}


	public static List<WhereField> transformIntoWhereClauses(
			List<Attribute> attributes) throws JSONException {
		Iterator<Attribute> it = attributes.iterator();
		List<WhereField> whereFields = new ArrayList<WhereField>();
		
		while (it.hasNext()) {
			Attribute attribute = it.next();
			String valuesStr = attribute.getValues();
			JSONArray valuesArray = new JSONArray(valuesStr);

			//if the filter has some value
			if ( valuesArray.length() > 0 ) {
				String operator = valuesArray.length() > 1 ? CriteriaConstants.IN : CriteriaConstants.EQUALS_TO;
				Operand leftOperand = new Operand(new String[] {attribute.getEntityId()}, attribute.getAlias(), AbstractStatement.OPERAND_TYPE_FIELD, null, null);
				String[] values = new String[valuesArray.length()];
				for (int i = 0; i < valuesArray.length(); i++) {
					try {
						values[i] = valuesArray.getString(i);
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}
				Operand rightOperand = new Operand(values, attribute.getAlias(), AbstractStatement.OPERAND_TYPE_STATIC, null, null);
				WhereField whereField = new WhereField(attribute.getAlias(), attribute.getAlias(), false, leftOperand, operator, rightOperand, "AND");

				whereFields.add(whereField);
			}
		}
		return whereFields;
	}


	public static List<WhereField> getOptionalFilters(
			JSONObject optionalUserFilters) throws JSONException {
		if (optionalUserFilters != null) {
			return transformIntoWhereClauses(optionalUserFilters);
		} else {
			return new ArrayList<WhereField>();
		}
	}


	private static List<WhereField> transformIntoWhereClauses(JSONObject optionalUserFilters) throws JSONException {
		String[] fields = JSONObject.getNames(optionalUserFilters);
		List<WhereField> whereFields = new ArrayList<WhereField>();
		
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

				String operator = "EQUALS TO";
				if(valuesArray.length()>1){
					operator="IN";
				}

				whereFields.add(new WhereField("OptionalFilter"+i, "OptionalFilter"+i, false, leftOperand, operator, rightOperand, "AND"));
			}
		}
		return whereFields;
	}

	
}
