/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.analiticalmodel.execution.service.GetParametersForExecutionAction;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParview;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ParameterForExecutionJSONSerializer implements Serializer {


	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;

		if( !(o instanceof GetParametersForExecutionAction.ParameterForExecution) ) {
			throw new SerializationException("ParameterForExecutionJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}

		try {
			GetParametersForExecutionAction.ParameterForExecution parameter = (GetParametersForExecutionAction.ParameterForExecution)o;
			result = new JSONObject();
			result.put("id", parameter.getId() );
			MessageBuilder msgBuild=new MessageBuilder();

			//String label=msgBuild.getUserMessage(parameter.getLabel(),null, locale);
			String label = parameter.getLabel();
			label = msgBuild.getI18nMessage(locale, label);

			result.put("label",label);
			result.put("type", parameter.getParType() );
			result.put("selectionType", parameter.getSelectionType() );
			result.put("typeCode", parameter.getTypeCode() );
			result.put("mandatory", parameter.isMandatory() );
			result.put("visible", parameter.isVisible() );
			result.put("valuesCount", parameter.getValuesCount() );
			if(parameter.getValuesCount() == 1) result.put("value", parameter.getValue() );
			JSONArray dependencies = new JSONArray();
			Iterator it = parameter.getDependencies().keySet().iterator();
			while( it.hasNext() ) {
				String paramUrlName = (String)it.next();
				JSONObject dependency = new JSONObject();
				dependency.put("urlName", paramUrlName);
				dependency.put("hasDataDependency", false);
				dependency.put("hasVisualDependency", false);
				JSONArray visualDependencyConditions = new JSONArray();
				dependency.put("visualDependencyConditions", visualDependencyConditions);

				List<GetParametersForExecutionAction.ParameterForExecution.ParameterDependency> parameterDependencies;
				parameterDependencies = (List<GetParametersForExecutionAction.ParameterForExecution.ParameterDependency>)parameter.getDependencies().get(paramUrlName);

				for(int i = 0; i < parameterDependencies.size(); i++) {
					Object pd = parameterDependencies.get(i);
					if(pd instanceof GetParametersForExecutionAction.ParameterForExecution.DataDependency) {
						dependency.put("hasDataDependency", true);
					} else if(pd instanceof GetParametersForExecutionAction.ParameterForExecution.VisualDependency) {
						ObjParview visualCondition = ((GetParametersForExecutionAction.ParameterForExecution.VisualDependency)pd).condition;
						dependency.put("hasVisualDependency", true);
						JSONObject visualDependencyCondition = new JSONObject();
						visualDependencyCondition.put("operation", visualCondition.getOperation());
						visualDependencyCondition.put("value", visualCondition.getCompareValue());
						String viewLabel = visualCondition.getViewLabel();
						viewLabel = msgBuild.getI18nMessage(locale, viewLabel);
						visualDependencyCondition.put("label", viewLabel);
						visualDependencyConditions.put( visualDependencyCondition );
					}
				}




				dependencies.put(dependency);
			}
			result.put("dependencies", dependencies);
			result.put("parameterUseId", parameter.getParameterUseId());
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {

		}

		return result;
	}





}
