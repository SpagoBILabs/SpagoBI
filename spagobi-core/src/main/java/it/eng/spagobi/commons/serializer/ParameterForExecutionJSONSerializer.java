/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.serializer;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.DefaultValue;

import org.json.JSONArray;
import org.json.JSONObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ParameterForExecution;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.AnalyticalDriverValue;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.AnalyticalDriverValueList;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParview;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;


import it.eng.spagobi.analiticalmodel.execution.service.GetParametersForExecutionAction;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParview;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ParameterForExecutionJSONSerializer implements Serializer {

	private static final String OPTIONS_PARAM = "options";

	@Override
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject result = null;

		if (!(o instanceof ParameterForExecution)) {
			throw new SerializationException("ParameterForExecutionJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}

		try {
			ParameterForExecution parameter = (ParameterForExecution) o;
			result = new JSONObject();
			result.put("id", parameter.getId());
			MessageBuilder msgBuild = new MessageBuilder();

			// String label=msgBuild.getUserMessage(parameter.getLabel(),null,
			// locale);
			String label = parameter.getLabel();
			label = msgBuild.getI18nMessage(locale, label);

			result.put("label", label);
			result.put("type", parameter.getParType());
			result.put("selectionType", parameter.getSelectionType());
			result.put("allowInternalNodeSelection", parameter.getPar().getModalityValue().getLovProvider().contains("<LOVTYPE>treeinner</LOVTYPE>"));
			result.put("enableMaximizer", parameter.isEnableMaximizer());
			result.put("typeCode", parameter.getTypeCode());
			result.put("mandatory", parameter.isMandatory());
			result.put("colspan", parameter.getColspan());
			result.put("thickPerc", parameter.getThickPerc());
			result.put("multivalue", parameter.isMultivalue());
			result.put("visible", parameter.isVisible());
			result.put("valuesCount", parameter.getValuesCount());

			result.put(OPTIONS_PARAM, parameter.getOptions());

			JSONArray valuesJSON = new JSONArray();
			AnalyticalDriverValueList values = parameter.getValues();
			Iterator<AnalyticalDriverValue> valuesIt = values.iterator();
			while (valuesIt.hasNext()) {
				AnalyticalDriverValue aValue = valuesIt.next();
				JSONObject aValueJSON = new JSONObject();
				aValueJSON.put("value", aValue.getValue());
				aValueJSON.put("description", aValue.getDescription());
				valuesJSON.put(aValueJSON);
			}
			
				
			if(valuesJSON.length()==0){
				//result.put("values", null);	
			}
			else{
				result.put("values", valuesJSON);	
			}
		
			if (parameter.getValues() != null && parameter.getValues().size() == 1 && parameter.getValues().get(0) != null) {
				result.put("value", parameter.getValues().get(0).getValue().toString());
			}
			else{
				if(parameter.getValues().size()!=0){
					result.put("value", parameter.getValue());
				}
			}
			
			
			
			// put admissible value 
			
			
			if (parameter.getObjParameterIds() != null) {
				JSONArray objParameterIds = new JSONArray();
				for (Iterator iterator = parameter.getObjParameterIds().iterator(); iterator.hasNext();) {
					Integer id = (Integer) iterator.next();
					objParameterIds.put(id);
				}
				result.put("objParameterIds", objParameterIds);
			}

			JSONArray dependencies = new JSONArray();
			Iterator it = parameter.getDependencies().keySet().iterator();
			while (it.hasNext()) {
				String paramUrlName = (String) it.next();
				JSONObject dependency = new JSONObject();
				dependency.put("urlName", paramUrlName);
				dependency.put("hasDataDependency", false);
				dependency.put("hasVisualDependency", false);
				dependency.put("isLovDependency", false);
				JSONArray visualDependencyConditions = new JSONArray();
				dependency.put("visualDependencyConditions", visualDependencyConditions);

				List<ParameterForExecution.ParameterDependency> parameterDependencies;
				parameterDependencies = parameter.getDependencies().get(paramUrlName);

				for (int i = 0; i < parameterDependencies.size(); i++) {
					Object pd = parameterDependencies.get(i);
					if (pd instanceof ParameterForExecution.DataDependency) {
						dependency.put("hasDataDependency", true);
					} else if (pd instanceof ParameterForExecution.VisualDependency) {
						ObjParview visualCondition = ((ParameterForExecution.VisualDependency) pd).condition;
						dependency.put("hasVisualDependency", true);
						JSONObject visualDependencyCondition = new JSONObject();
						visualDependencyCondition.put("operation", visualCondition.getOperation());
						visualDependencyCondition.put("value", visualCondition.getCompareValue());
						String viewLabel = visualCondition.getViewLabel();
						viewLabel = msgBuild.getI18nMessage(locale, viewLabel);
						visualDependencyCondition.put("label", viewLabel);
						visualDependencyConditions.put(visualDependencyCondition);
					} else if (pd instanceof ParameterForExecution.LovDependency) {
						dependency.put("isLovDependency", true);
					}
				}

				dependencies.put(dependency);
			}
			result.put("dependencies", dependencies);
			result.put("parameterUseId", parameter.getParameterUseId());

			JSONArray defaultValues = new JSONArray();
			AnalyticalDriverValueList defaults = parameter.getDefaultValues();
			Iterator<AnalyticalDriverValue> defaultsIt = defaults.iterator();
			while (defaultsIt.hasNext()) {
				AnalyticalDriverValue aDefault = defaultsIt.next();
				JSONObject aDefaultJSON = new JSONObject();
				aDefaultJSON.put("value", aDefault.getValue());
				aDefaultJSON.put("description", aDefault.getDescription());
				defaultValues.put(aDefaultJSON);
			}
			if(defaultValues.length()>0){
				result.put("defaultValues", defaultValues);
			}

			JSONArray admissibleValues = new JSONArray();
			AnalyticalDriverValueList admissibles = parameter.getAdmissibleValuesList();
			Iterator<AnalyticalDriverValue> admissiblesIt = admissibles.iterator();
			while (admissiblesIt.hasNext()) {
				AnalyticalDriverValue aAdmissible = admissiblesIt.next();
				JSONObject aAdmissibleJSON = new JSONObject();
				aAdmissibleJSON.put("value", aAdmissible.getValue());
				aAdmissibleJSON.put("label", aAdmissible.getDescription());
				aAdmissibleJSON.put("description", aAdmissible.getDescription());
				admissibleValues.put(aAdmissibleJSON);
			}
			if(admissibleValues.length()>0){
				result.put("admissibleValues", admissibleValues);
			}
			
		
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {

		}

		return result;
	}

}
