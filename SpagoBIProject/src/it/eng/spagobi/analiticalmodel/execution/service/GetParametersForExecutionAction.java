/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.service;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.document.handlers.ParameterForExecution;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.AnalyticalDriverValue;
import it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues.AnalyticalDriverValueList;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetParametersForExecutionAction  extends AbstractSpagoBIAction {

	public static final String SERVICE_NAME = "GET_PARAMETERS_FOR_EXECUTION_SERVICE";

	// request parameters
	public static String DOCUMENT_ID = ObjectsTreeConstants.OBJECT_ID;
	public static String DOCUMENT_LABEL = ObjectsTreeConstants.OBJECT_LABEL;
	public static String PARAMETERS_PREFERENCE = ObjectsTreeConstants.PARAMETERS_PREFERENCE;
	public static String SESSION_PARAMETERS = ObjectsTreeConstants.SESSION_PARAMETERS;

	public static String CALLBACK = "callback";
	// logger component
	private static Logger logger = Logger.getLogger(GetParameterValuesForExecutionAction.class);

	// needed List to preserve order
	List<ParameterForExecution> parametersForExecutionList;
	// needed map to retrieve already processed values
	Map<String, AnalyticalDriverValueList> parametersProcessedMap;
	
	String role = null;
	
	
	public void doService() {

		logger.debug("IN");
		parametersForExecutionList = new ArrayList<ParameterForExecution>();
		parametersProcessedMap = new HashMap<String, AnalyticalDriverValueList>();
		
		role = getAttributeAsString("role");
				
		logger.debug("search for proposed values taken by request");
		Map<String,String> proposedValuesMapToComplete = getParametersPreference();

		logger.debug("Search for session parameters");
		Map<String,JSONObject> sessionValuesMap = getSessionParameters();
		
		logger.debug("get parameters and their dependencies");
		getParameters();
		
		logger.debug("get parameters values if parents parameters have values");
		boolean isAutomaticExecution = getParametersValues(proposedValuesMapToComplete, sessionValuesMap);
		
		JSONArray parametersJSON = null;

		try {
			parametersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( parametersForExecutionList, getLocale() );
		} catch (SerializationException e) {
			e.printStackTrace();
		}

		String callback = getAttributeAsString( CALLBACK );
		logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");

		try {
			JSONObject success = new JSONObject();
			success.append("parameters", parametersJSON);
			success.append("isReadyForExecution", new Boolean(isAutomaticExecution));
			writeBackToClient( new JSONSuccess( success, callback )  );
		} catch (IOException e) {
			logger.error("Impossible to write back the responce to the client");
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
		} catch (JSONException e) {
			logger.error("Impossible to parse JSON response to the client");
			throw new SpagoBIServiceException("Impossible to parse JSON response to the client", e);
		}
		logger.debug("OUT");
	}

	
	/** Method checks if parameter currently processed depends on parameters that have been correctly filled
	 * 
	 * @param parameterForExecution
	 * @return
	 */
	
	private boolean checkIfDependenciesHaveValues(ParameterForExecution parameterForExecution){
		logger.debug("IN");
		
		Map<String, List<ParameterForExecution.ParameterDependency>> dependencies = parameterForExecution.getDependencies();
		
		boolean toReturn = true;
		
		// if has dependency check the parents parameter have been processed and have values
		if(dependencies != null && dependencies.keySet().size()>0){
			logger.debug("Parameter "+parameterForExecution.getLabel()+" has dependencies" );
			
			// cycle on each dependency
			for (Iterator iterator2 = dependencies.keySet().iterator(); iterator2.hasNext();) {
				String parentId = (String) iterator2.next();
				if(!parametersProcessedMap.keySet().contains(parentId)){
					logger.error("Parent parametr in dependecy was not processed yet, means there is an errore in parameter ordering");
					throw new SpagoBIServiceException(SERVICE_NAME, "Parameters dependencies are not set in right order, check them");
				}
				AnalyticalDriverValueList advl = parametersProcessedMap.get(parentId);
				if(advl == null || advl.size() == 0){
					logger.debug("Parent parameter has no value, do not complete than nor admissible, default or values");
					toReturn= false;
			}
			}
		}

		logger.debug("OUT");
		return toReturn;
	}
	
	
	
	
	/** This method cycle on each parameter and set its value depending on precedding processed ones
	 * 
	 * @param parameterForExecution
	 * @return
	 */
	
	
	private boolean getParametersValues(Map<String,String> proposedValuesMapToComplete, Map<String, JSONObject> sessionValuesMap){
		logger.debug("IN");
		boolean isAutomaticExecution = false;
		//Cycle on each parameter and assign values
		
		for (Iterator iterator = parametersForExecutionList.iterator(); iterator.hasNext();) {
			ParameterForExecution parameterForExecution = (ParameterForExecution)iterator.next();
			
			boolean isDependenciesOk = checkIfDependenciesHaveValues(parameterForExecution);
			if(!isDependenciesOk){
				// put empty
				parameterForExecution.setValues(new AnalyticalDriverValueList());
				parameterForExecution.setDefaultValues(new AnalyticalDriverValueList());
				parametersProcessedMap.put(parameterForExecution.getId(), new AnalyticalDriverValueList());
				continue;  // go to process next parameter for execution, this will not be pre-loaded 
			}
			
			// take admissible Values
			parameterForExecution.loadAdmissibleValues(parametersProcessedMap);  

			logger.debug("fill default values and check for their validity");
			fillAndCheckDefaultValues(parameterForExecution);
			
			logger.debug("complete proposed values map with description values and check for their validity");
			AnalyticalDriverValueList proposedValuesList = completeAndCheckProposedValues(parameterForExecution, proposedValuesMapToComplete);

			logger.debug("session values check and find right formats");
			AnalyticalDriverValueList sessionValuesList = convertAndCompleteCheckSessionValuesMap(parameterForExecution, sessionValuesMap);

			logger.debug("set parameter value");
			setParametersValues(parameterForExecution, proposedValuesList, sessionValuesList);

			parametersProcessedMap.put(parameterForExecution.getId(), parameterForExecution.getValues());
			
		}
		
		// check if autoimatic execution
		isAutomaticExecution = true;
		for (Iterator iterator = parametersForExecutionList.iterator(); iterator.hasNext();) {
			ParameterForExecution parameterForExecution = (ParameterForExecution)iterator.next();
			
			if(parameterForExecution.isMandatory()){
				AnalyticalDriverValueList advl = parameterForExecution.getValues();
				if(advl == null || advl.size() == 0){
					isAutomaticExecution = false;
				}
			}
		}
		
		logger.debug("OUT");
		return isAutomaticExecution;
	}

	
	
	
	
	
	
	
	private void setParametersValues(ParameterForExecution parameterForExecution, AnalyticalDriverValueList proposedValuesList, AnalyticalDriverValueList sessionValuesList){
		logger.debug("IN");
		
		//set proposed value, otherwise default value, otherwise no value 
		String urlName = parameterForExecution.getId();
		logger.debug("Set values for parameter ="+urlName);
		
		if(parameterForExecution.getAdmissibleValuesList() != null){
			AnalyticalDriverValueList advl = parameterForExecution.getAdmissibleValuesList();
			for (Iterator iterator = advl.iterator(); iterator.hasNext();) {
				AnalyticalDriverValue analyticalDriverValue = (AnalyticalDriverValue) iterator.next();
				logger.debug("Admissible value = "+analyticalDriverValue.getValue()+" and description = "+analyticalDriverValue.getDescription());
			}
		}
		
		if(proposedValuesList != null && !proposedValuesList.isEmpty()){
			logger.debug("Set proposed values");
			parameterForExecution.setValues(proposedValuesList);
		}
		else if (sessionValuesList != null && !sessionValuesList.isEmpty()){ // if session parameters
			logger.debug("Set session values");
			parameterForExecution.setValues(sessionValuesList);
			
		}
		else if(parameterForExecution.getDefaultValues() != null && parameterForExecution.getDefaultValues().size()>0){
			// use default values if present
			logger.debug("Set default values");
			parameterForExecution.setValues(parameterForExecution.getDefaultValues());
		}
		else if(parameterForExecution.getAdmissibleValuesList() != null && parameterForExecution.getAdmissibleValuesList().size()==1){
			// if there is only one admissible value give it
			logger.debug("Set unique value");
			parameterForExecution.setValues(parameterForExecution.getAdmissibleValuesList());
		}
		else{
			logger.debug("Set empty values");
			parameterForExecution.setValues(new AnalyticalDriverValueList());
		}

		logger.debug("OUT");
	}
	
	
	
	
	
	/** returns a list containing only admissible values
	 * 
	 * @param listToCheck
	 * @param parameterForExecution
	 * @return
	 */
	
	private AnalyticalDriverValueList filterValueListWithAdmissible(AnalyticalDriverValueList listToCheck, ParameterForExecution parameterForExecution){
		logger.debug("IN");
		AnalyticalDriverValueList toReturn = new AnalyticalDriverValueList();
		// if is manual imput case no filter is done 
		if( parameterForExecution.getSelectionType() != null && !parameterForExecution.getSelectionType().equals(""))  { 
			AnalyticalDriverValueList admissibleValue = parameterForExecution.getAdmissibleValuesList();
			
			for (Iterator iterator = listToCheck.iterator(); iterator.hasNext();) {
				AnalyticalDriverValue valueToCheck = (AnalyticalDriverValue) iterator.next();
				if(admissibleValue.contains(valueToCheck.getValue())){
					toReturn.add(valueToCheck);
				}
			}
			
		}
		else{
			toReturn = listToCheck;
		}
		
		logger.debug("OUT");
		return toReturn;
	}
	
	
	
	
	
	
	// fill default values and keep only admissible default values
	private void fillAndCheckDefaultValues(ParameterForExecution parameterForExecution){
		
		logger.debug("IN");
		parameterForExecution.loadDefaultValues();
		
		AnalyticalDriverValueList valuesToFilter = parameterForExecution.getDefaultValues();
		
		// if is SLIDER and has only one value parse if is in form val1, val2, val3, // done for back compatibility

		boolean particularSliderCase = false;
		if(parameterForExecution.getSelectionType().equals("SLIDER") && valuesToFilter != null && valuesToFilter.size()==1){
			logger.debug("Slider case, parse default value in the case in the form val1, val2");
			AnalyticalDriverValue adv = valuesToFilter.get(0);
			String toParse = adv.getValue().toString();
			AnalyticalDriverValueList toSubst = new AnalyticalDriverValueList();
			StringTokenizer st = new StringTokenizer(toParse, ",", false);
			int i = 0;
			while (st.hasMoreTokens()) {
				String valS = st.nextToken();
				AnalyticalDriverValue val = new AnalyticalDriverValue();
				val.setValue(valS); 				val.setDescription(valS);
				toSubst.add(i, val);
				i++;
			}
			valuesToFilter = toSubst;
			particularSliderCase = true;
		}
		
		if(valuesToFilter != null){
			valuesToFilter.printList("Default value yet to filter");
		}

		AnalyticalDriverValueList defaultValues = filterValueListWithAdmissible(valuesToFilter, parameterForExecution);
		
		if(particularSliderCase){
			logger.debug("Slider default case val1,val2,val3, fill description ");
			for (Iterator iterator = defaultValues.iterator(); iterator.hasNext();) {
				AnalyticalDriverValue analyticalDriverValue = (AnalyticalDriverValue) iterator.next();
				String description = getDescriptionFromAdmissibles(analyticalDriverValue.getValue().toString(), parameterForExecution.getAdmissibleValuesList());				
				analyticalDriverValue.setDescription(description);
			}
		}
		
		parameterForExecution.setDefaultValues(defaultValues);
		
		if(defaultValues != null){
			defaultValues.printList("Default value filtered");
		}
		
		logger.debug("OUT");
	}
	
	
	
	
	private AnalyticalDriverValueList completeAndCheckProposedValues(
			ParameterForExecution parameterForExecution
			, Map<String, String> proposedValuesMapToComplete){
		logger.debug("IN");

		AnalyticalDriverValueList toReturn = new AnalyticalDriverValueList();

		// completeProposedValues
		String valuetoParse = proposedValuesMapToComplete.get(parameterForExecution.getId());	

		if(valuetoParse != null && valuetoParse.toString().length()>0){
			AnalyticalDriverValueList toCheck = new AnalyticalDriverValueList();

			List<String> values = GeneralUtilities.parseParameterValueString(valuetoParse, parameterForExecution.isMultivalue());

			for (int i = 0; i < values.size(); i++) {
				String value = values.get(i);

				if(parameterForExecution.getPar().getType().equals("DATE")){
					value = convertDateValue(value);
				}

				// get description from admissible
				String description = getDescriptionFromAdmissibles(value, parameterForExecution.getAdmissibleValuesList());
				
				AnalyticalDriverValue advToCheck = new AnalyticalDriverValue(value, description);
				
				toCheck.add(advToCheck);
				
			}
			
			if(toCheck != null){
				toCheck.printList("Proposed value yet to filter");
			}
			
			toReturn = filterValueListWithAdmissible(toCheck, parameterForExecution);
		}

		if(toReturn != null){
			toReturn.printList("Proposed value already filtered");
		}

		
		logger.debug("OUT");
		return toReturn;
	}
	
	/**
	 *  get description for values from admissibles
	 */
	private String getDescriptionFromAdmissibles(String value, AnalyticalDriverValueList admissibles){
		logger.debug("IN");
		String toReturn = value; // case not found is like value
		
		if(admissibles != null && admissibles.getAnalyticalDriverValue(value) != null){
			AnalyticalDriverValue adv = admissibles.getAnalyticalDriverValue(value);
			toReturn = adv.getDescription() != null ? adv.getDescription().toString() : value;
		}
		logger.debug("OUT");
		return toReturn;		
	}
	
	
	/** Function to convert and check session values
	 * 
	 * conversion from JSONObject to AnalyticalDriverValueList
	 * 
	 * @param parameterForExecution
	 * @param sessionValuesMap
	 * @return
	 */
	
	private String getSessionKey(ParameterForExecution parameterForExecution){

		String toReturn = null;

		ParameterUse parameterUse;
		try {
			parameterUse = DAOFactory.getParameterUseDAO().loadByParameterIdandRole(parameterForExecution.getPar().getId(), role);
		} catch (EMFUserError e) {
			throw new SpagoBIRuntimeException(e);
		}
		//toReturn = parameterForExecution.getId() + "_" + parameterUse.getId();
		toReturn = parameterForExecution.getLabel() + "_" + parameterUse.getUseID();
		return toReturn;
	}
	
	private AnalyticalDriverValueList convertAndCompleteCheckSessionValuesMap(
			ParameterForExecution parameterForExecution
			, Map<String, JSONObject> sessionValuesMap){
		logger.debug("IN");
		
		AnalyticalDriverValueList toReturn = new AnalyticalDriverValueList();

		String sessionKey = getSessionKey(parameterForExecution);
		JSONObject sessionValue = sessionValuesMap.get(sessionKey);

		if(sessionValue != null){
			AnalyticalDriverValueList listToCheck = new AnalyticalDriverValueList();

			// START CONVERSION
			String value = sessionValue.optString("value");
			
			if (parameterForExecution.getPar().getType().equals("DATE")) {
				value = convertDateValue(value);
			} 
			
			if (parameterForExecution.isMultivalue()) {
				logger.debug("Multivalue case");
				try {
					// split sessionValue
					JSONArray valuesArray = new JSONArray(value);
//					StringTokenizer st = new StringTokenizer(description, ",", false);

					ArrayList<String> values = new ArrayList<String>();
//					ArrayList<String> descriptions = new ArrayList<String>();

//					int i = 0;
//					while (st.hasMoreTokens()) {
//						String parDescription = st.nextToken();
//						descriptions.add(i, parDescription);
//						i++;
//					}

					for (int j = 0; j < valuesArray.length(); j++) {
						String valueSS = (String) valuesArray.get(j);
						values.add(valueSS);
					}

					for (int z = 0; z < values.size(); z++) {
						String parValue = values.get(z);
						// get description from admissible
						String parDescription = getDescriptionFromAdmissibles(parValue, parameterForExecution.getAdmissibleValuesList());
						AnalyticalDriverValue valueDef = new AnalyticalDriverValue();
						valueDef.setValue(parValue);
						valueDef.setDescription(parDescription);
						listToCheck.add(valueDef);
					}

				} catch (Exception e) {
					logger.error("Error in converting multivalue session values", e);
				}

			} else {
				logger.debug("NOT - multivalue case");
				AnalyticalDriverValue valueDef = new AnalyticalDriverValue();
				valueDef.setValue(value);
				// get description from admissible
				String description = getDescriptionFromAdmissibles(value, parameterForExecution.getAdmissibleValuesList());
				valueDef.setDescription(description);
				listToCheck.add(valueDef);
			}

			toReturn = filterValueListWithAdmissible(listToCheck, parameterForExecution);

		}
		logger.debug("OUT");
		return toReturn;
	}

	
	
	
	private String convertDateValue(String valueDate){
		logger.debug("IN");

		String toReturn = valueDate;
		if(valueDate != null && valueDate.contains("#")){
			// if specified format 
			SimpleDateFormat serverDateFormat = new SimpleDateFormat(SingletonConfig.getInstance().getConfigValue("SPAGOBI.DATE-FORMAT-SERVER.format"));
			try {	
				String[] date = valueDate.split("#");
				SimpleDateFormat format = new SimpleDateFormat(date[1]);

				Date d = format.parse(date[0]);
				String formattedDate = serverDateFormat.format(d);
				toReturn = formattedDate;
			} catch (java.text.ParseException e) {
				logger.error("Error while formatting date "+valueDate+" to server format "+serverDateFormat, e);
				return valueDate;
			}
		}
		logger.debug("OUT");
		return toReturn;
	}
	
	
	
	
	
	
	
	
	
	public Map getParametersPreference() {
		logger.debug("IN");
		Map parsPrefMap = new HashMap();
		if(getAttribute(PARAMETERS_PREFERENCE) != null){ 
			String parametersPreference = getAttributeAsString(PARAMETERS_PREFERENCE);	
			
//			try {
//				byte[] utf8 = parametersPreference.getBytes("ISO-8859-1");
//				String parametersPreference2 = new String(utf8, "UTF-8"); 
//				logger.debug("Preference iso "+parametersPreference2);
//				byte[] iso = parametersPreference.getBytes("UTF-8");
//				String parametersPreference3 = new String(iso, "UTF-8"); 
//				logger.debug("Preference iso "+parametersPreference3);
//
//				parametersPreference = parametersPreference3;
//
//				
//			} catch (UnsupportedEncodingException e) {
//				logger.error("Error in encoding to UTF 8, go on anyway");
//			}
			
			logger.debug("parametersPreference found is "+parametersPreference);
			parsPrefMap = GeneralUtilities.getParametersFromQueryURL(parametersPreference);
			for (Iterator iterator = parsPrefMap.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				Object val = parsPrefMap.get(key);
				logger.debug("parametersPreference translated is "+key+" = "+val);
			}
		}
		logger.debug("OUT");
		
//		parsPrefMap.put("TEST_PERIOD_SLIDER_M", "{;{201410;201411;201412}NUM}");
		return parsPrefMap;
	}
	
	public Map getSessionParameters() {
		logger.debug("IN");
		Map<String, JSONObject> sessionParametersMap = new HashMap<String, JSONObject>();
		
		Boolean isSessionEnabled = getAttributeAsBoolean("isParametersStatePersistenceEnabled");
if(isSessionEnabled){		
		if(getAttribute(SESSION_PARAMETERS) != null){ 
			String sessionParametersString = getAttributeAsString(SESSION_PARAMETERS);			
					try {
				JSONObject sessionParametersJSON = new JSONObject(sessionParametersString);

				Iterator<String> it = sessionParametersJSON.keys();
				while (it.hasNext()) {
					String key = it.next();
					JSONObject parJson = sessionParametersJSON.getJSONObject(key);
					sessionParametersMap.put(key, parJson);
				}
			} catch (Exception e) {
				logger.error("Error converting session parameters to JSON: ", e);
			}
		
		}
}
		logger.debug("OUT");
		return sessionParametersMap;
	}
	
	
	
	
	public void getParameters() {
		logger.debug("IN");
		ExecutionInstance executionInstance;

		Assert.assertNotNull(getContext(), "Execution context cannot be null" );
		Assert.assertNotNull(getContext().getExecutionInstance( ExecutionInstance.class.getName() ), "Execution instance cannot be null");

		executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() ); 
		
		//executionInstance.getBIObject().getBiObjectParameters()

		BIObject document = executionInstance.getBIObject();

		List parameters = document.getBiObjectParameters();
		if (parameters != null && parameters.size() > 0) {
			Iterator it = parameters.iterator();
			while (it.hasNext()) {
				BIObjectParameter parameter = (BIObjectParameter) it.next();
				ParameterForExecution parameterForExecution = new ParameterForExecution(parameter, getUserProfile(), getContext());

				parametersForExecutionList.add(parameterForExecution);
			}
		}

		logger.debug("OUT");
	}
	

}
