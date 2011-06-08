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

import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.services.crosstab.LoadCrosstabAction;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class LoadWorksheetCrosstabAction extends LoadCrosstabAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6934324402054147606L;

	/**
	 * Loads the values of the form if the calling engine is smart filter
	 * @return
	 * @throws JSONException
	 */
	@Override
	protected JSONObject loadSmartFilterFormValues() throws JSONException{
		return  getEngineInstance().getFormState().getFormStateValues();
	}
	
	
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
	@Override
	protected String buildSqlStatement(CrosstabDefinition crosstabDefinition, Query baseQuery, String sqlQuery, IStatement stmt) throws JSONException{
		//optional filters specified by the user (now used in the worksheet)
		JSONObject optionalUserFilters= null;
		List<WhereField> optinalWhereFields = null;
		
		try {
			optionalUserFilters = getAttributeAsJSONObject( QbeEngineStaticVariables.OPTIONAL_FILTERS );
			logger.debug("Found those optional filters "+optionalUserFilters);
		} catch (Exception e) {
			logger.debug("Found no optional filters");
		}
		
		if(optionalUserFilters!=null){			
			optinalWhereFields = applyOptionalFilters(optionalUserFilters);
		}
		return CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, baseQuery, optinalWhereFields, sqlQuery, stmt);
	}
	
	private List<WhereField> applyOptionalFilters(JSONObject optionalUserFilters) throws JSONException{
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

				String operator = "NOT EQUALS TO";
				if(valuesArray.length()>0){
					operator="IN";
				}

				whereFields.add(new WhereField("OptionalFilter"+i, "OptionalFilter"+i, false, leftOperand, operator, rightOperand, "AND"));
			}
		}
		return whereFields;
	}

	
}
