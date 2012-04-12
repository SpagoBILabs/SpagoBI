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
package it.eng.spagobi.engine.mobile.service;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.execution.service.GetParametersForExecutionAction;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.serializer.JSONStoreFeedTransformer;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 */
public class GetDocumentParametersAction extends GetParametersForExecutionAction {


	private static final long serialVersionUID = 7908624899621065025L;

	public static final String SERVICE_NAME = "GET_PARAMETERS_FOR_EXECUTION_SERVICE";

	// request parameters
	public static String DOCUMENT_ID = ObjectsTreeConstants.OBJECT_ID;
	public static String DOCUMENT_LABEL = ObjectsTreeConstants.OBJECT_LABEL;
	public static String CALLBACK = "callback";
	public static String MODE = "MODE";
	public static String MODE_SIMPLE = "simple";
	public static String MODE_COMPLETE = "complete";
	// logger component
	private static Logger logger = Logger
			.getLogger(GetDocumentParametersAction.class);

	public void doService() {

		List parametersForExecution = getParameters();

		JSONArray parametersJSON = null;
		try {
			parametersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( parametersForExecution, getLocale() );
		} catch (SerializationException e) {
			logger.debug("Error serializing the parameters",e);
			throw new SpagoBIRuntimeException(" Error serializing the parameters", e);
		}
		
		try {
			for(int i=0; i<parametersForExecution.size(); i++){
				JSONObject parameterMetadata = getParameterMetadata(((ParameterForExecution)(parametersForExecution.get(i))).getId(), MODE_COMPLETE);	
				parametersJSON.getJSONObject(i).put("metaData", parameterMetadata);
			}
			
			String callback = getAttributeAsString( CALLBACK );
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");

			writeBackToClient( new JSONSuccess( parametersJSON, callback )  );
		} catch (Exception e) {
			SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Execution", "Error getting parameters");
			try {
				writeBackToClient(new JSONFailure(serviceError));
			} catch (Exception ex) {
				logger.error("Exception occurred writing back to client", ex);
				throw new SpagoBIServiceException("Exception occurred writing back to client", ex);
			}
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
		}
	}

	

	public JSONObject getParameterMetadata(String biparameterId, String mode) {

		BIObjectParameter biObjectParameter;
		ExecutionInstance executionInstance;
		String valueColumn;
		String displayColumn;
		String descriptionColumn;
		ILovDetail lovProvDet;
		JSONObject valuesJSON = new JSONObject();
		

//		try {

//			logger.debug("Parameter [MODE] is equals to [" + mode + "]");

			if (mode == null) {
				mode = MODE_SIMPLE;
			}

			Assert.assertNotNull(getContext(),
					"Execution context cannot be null");
			Assert.assertNotNull(
					getContext().getExecutionInstance(
							ExecutionInstance.class.getName()),
					"Execution instance cannot be null");

			executionInstance = getContext().getExecutionInstance(
					ExecutionInstance.class.getName());

			BIObject obj = executionInstance.getBIObject();

			// START get the relevant biobject parameter
			biObjectParameter = null;
			List parameters = obj.getBiObjectParameters();
			for (int i = 0; i < parameters.size(); i++) {
				BIObjectParameter p = (BIObjectParameter) parameters.get(i);
				if (biparameterId.equalsIgnoreCase(p.getParameterUrlName())) {
					biObjectParameter = p;
					break;
				}
			}
			Assert.assertNotNull(biObjectParameter,
					"Impossible to find parameter [" + biparameterId + "]");
			// END get the relevant biobject parameter

			lovProvDet = executionInstance.getLovDetail(biObjectParameter);

			if(lovProvDet==null){
				return null;
			}
			// START building JSON object to be returned

			try {
				JSONArray valuesDataJSON = new JSONArray();

				valueColumn = lovProvDet.getValueColumnName();
				displayColumn = lovProvDet.getDescriptionColumnName();
				descriptionColumn = displayColumn;

				String[] visiblecolumns;

				if (MODE_COMPLETE.equalsIgnoreCase(mode)) {
					visiblecolumns = (String[]) lovProvDet.getVisibleColumnNames().toArray(new String[0]);
					for (int j = 0; j < visiblecolumns.length; j++) {
						visiblecolumns[j] = visiblecolumns[j].toUpperCase();
					}
				} else {

					valueColumn = "value";
					displayColumn = "label";
					descriptionColumn = "description";

					visiblecolumns = new String[] { "value", "label",
							"description" };
				}

				valuesJSON = (JSONObject) JSONStoreFeedTransformer
						.getInstance().transform(valuesDataJSON,
								valueColumn.toUpperCase(),
								displayColumn.toUpperCase(),
								descriptionColumn.toUpperCase(),
								visiblecolumns, 0);
			} catch (Exception e) {
				throw new SpagoBIServiceException(
						"Impossible to serialize response", e);
			}
			// END building JSON object to be returned

//		} 
//		finally {
//			logger.debug("OUT");
//		}
		return valuesJSON;
	}

}
