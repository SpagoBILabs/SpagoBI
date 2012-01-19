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
package it.eng.spagobi.analiticalmodel.execution.service;


import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.bo.CustomDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.utils.CreationUtilities;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.json.JSONObject;



/** Action that get in request infos to build a biObject (of type Worksheet) or a dataset (o ftype custom)
 * 
 * @author Giulio gavardi
 */
public class CreateDatasetForWorksheetAction extends ExecuteDocumentAction {

	private static final long serialVersionUID = 1L;

	public static final String SERVICE_NAME = "CREATE_DATASET_FOR_WORKSHEET_ACTION";



	/**  FIELDS EXPECTED IN REQUEST
	 * 	
	 * label       
		name      
		description   
		jClassName        
		customData      
		parametersDefinition 
		parametersValues  
		metadata  
	 */
	public static final String  INPUT_PARAMETER_DS_LABEL = DataSetConstants.LABEL;
	public static final String  INPUT_PARAMETER_DS_NAME = DataSetConstants.NAME;
	public static final String  INPUT_PARAMETER_DS_DESCRIPTION = DataSetConstants.DESCRIPTION;
	public static final String  INPUT_PARAMETER_DS_CUSTOM_DATA = DataSetConstants.CUSTOM_DATA;
	public static final String  INPUT_PARAMETER_DS_JCLASS_NAME = DataSetConstants.JCLASS_NAME;
	public static final String  INPUT_PARAMETER_DS_METADATA = DataSetConstants.DS_METADATA;
	public static final String  INPUT_PARAMETER_DS_PARAMETER_DEFINITION = "parametersDefinition";
	public static final String  INPUT_PARAMETER_DS_PARAMETERS_VALUES = "parametersValues";	
	
	public static final String  OUTPUT_PARAMETER_DS_ID = "dataset_id";	
	public static final String  OUTPUT_PARAMETER_DS_LABEL = "dataset_label";
	public static final String  OUTPUT_PARAMETER_DS_PARAMETERS_VALUES_PREFIX = "PAR_";	
	

	
	// logger component
	private static Logger logger = Logger.getLogger(CreateDatasetForWorksheetAction.class);
	
	public void doService() {
		
		CreationUtilities creationUtilities;
		GuiGenericDataSet datasetBean;
		Integer datasetId;
		
		logger.debug("IN");

		datasetId = null;
		
		try {
				
			logger.trace("Creating the dataset...");
			datasetBean = getDatasetAttributesFromRequest();
			try{		
				creationUtilities = new CreationUtilities();
				datasetId = creationUtilities.creatDataSet(datasetBean);
				Assert.assertNotNull(datasetId, "Dataset Id cannot be null");
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "An error occurred while creating dataset from bean [" + datasetBean + "]", t);				
			}			
			LogMF.debug(logger, "Datset [{0}]succesfully created with id [{1}]", datasetBean, datasetId);
			
					
			logger.trace("Copying output parameters to response...");
			try {
				
				getServiceResponse().setAttribute("OUTPUT_PARAMETER_DS_ID", datasetId);					
				getServiceResponse().setAttribute("OUTPUT_PARAMETER_DS_LABEL", datasetBean.getLabel());	

				Map<String, String> parametersMap = getDatasetParametersMapFromRequest();
				for (String key : parametersMap.keySet()) {		
					Object value = parametersMap.get(key);
					getServiceResponse().setAttribute(OUTPUT_PARAMETER_DS_PARAMETERS_VALUES_PREFIX + key, value);
				}
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "An error occurred while creating dataset from bean [" + datasetBean + "]", t);				
			}
			logger.trace("Output parameter succesfully copied to response");


		} finally {
			logger.debug("OUT");
		}
	}

	
	/** 
	 * Read dataset's attributes from request and save them in a bean object.
	 * 
	 * @return  GuiGenericDataSet the bean object that collect all the dataset 
	 * attributes read from request.
	 */
	private GuiGenericDataSet getDatasetAttributesFromRequest(){
		
		
		GuiGenericDataSet datasetBean;
		
		String label;
		String name;
		String description;
		String customData;
		String jClassName;
		String metadata;
		String parametersDefinitionJson;
		String parametersDefinitionXML;

		logger.debug("IN");
		
		datasetBean = null;
		
		try {
			logger.trace("Reading from request attributes used to build the new dataset...");
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_LABEL);
			label = getAttributeAsString( INPUT_PARAMETER_DS_LABEL );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_LABEL, label);
			Assert.assertNotNull(label, "Input parameter [" + INPUT_PARAMETER_DS_LABEL + "] cannot be null");
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_NAME);
			name = getAttributeAsString( INPUT_PARAMETER_DS_NAME );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_LABEL, name);
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_DESCRIPTION);
			description = getAttributeAsString( INPUT_PARAMETER_DS_DESCRIPTION );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_DESCRIPTION, description);
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_CUSTOM_DATA);
			customData = getAttributeAsString( INPUT_PARAMETER_DS_CUSTOM_DATA );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_CUSTOM_DATA, customData);
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_JCLASS_NAME);
			jClassName = getAttributeAsString( INPUT_PARAMETER_DS_JCLASS_NAME );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_JCLASS_NAME, jClassName);
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_METADATA);
			metadata = getAttributeAsString( INPUT_PARAMETER_DS_METADATA );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_METADATA, metadata);
			
			LogMF.trace(logger, "Reading input parametr [{0}] from request...", INPUT_PARAMETER_DS_PARAMETER_DEFINITION);
			parametersDefinitionJson = getAttributeAsString( INPUT_PARAMETER_DS_PARAMETER_DEFINITION );
			LogMF.debug(logger, "Input parameter [{0}] is equal to [{1}]", INPUT_PARAMETER_DS_METADATA, parametersDefinitionJson);
			
			
			parametersDefinitionXML = null;
			if(!StringUtilities.isEmpty(parametersDefinitionJson)){
				LogMF.trace(logger, "Coverting input parameter [{0}] from JSON format to XML format ...", INPUT_PARAMETER_DS_PARAMETER_DEFINITION);
				parametersDefinitionXML = parametersJsonToXML(parametersDefinitionJson);
				LogMF.trace(logger, "Input parameter [{0}] succesfully converted to xml formt [{1}]", INPUT_PARAMETER_DS_PARAMETER_DEFINITION, parametersDefinitionXML);
			} 
			
			logger.trace("Attributes used to build the new dataset succesfully read from request");
	
			CustomDataSetDetail customDataSetDetail = new CustomDataSetDetail();
			customDataSetDetail.setCustomData(customData);
			customDataSetDetail.setJavaClassName(jClassName);
			customDataSetDetail.setParameters(parametersDefinitionXML);
			customDataSetDetail.setDsType(DataSetConstants.DS_CUSTOM);
			if(!StringUtilities.isEmpty(metadata)) {
				customDataSetDetail.setDsMetadata(metadata);
			}
			
			logger.trace("Building the dataset bean...");
			datasetBean = new GuiGenericDataSet();
			datasetBean.setLabel(label);
			datasetBean.setName(name);
			datasetBean.setDescription(description);
			datasetBean.setActiveDetail(customDataSetDetail);
			logger.trace("Dataset bean succesfully built");
						
		} catch(Throwable t) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to read from request all parameters required to create the new dataset", t);
		} finally {
			logger.debug("OUT");
		}
		
		return datasetBean;
	}

	private Map<String, String> getDatasetParametersMapFromRequest() {
		Map<String, String> parametersMap;
		
		parametersMap = new HashMap<String, String>(); 
		
		try {
			if(requestContainsAttribute(INPUT_PARAMETER_DS_PARAMETERS_VALUES)){
				logger.trace("Reading input parametr [" + INPUT_PARAMETER_DS_PARAMETERS_VALUES + "] from request...");
				JSONObject parameterValues = this.getAttributeAsJSONObject( INPUT_PARAMETER_DS_PARAMETERS_VALUES );
				logger.debug("Input parameter [" + INPUT_PARAMETER_DS_PARAMETERS_VALUES + "] is equal to [" + parameterValues + "]");
				
				for (Iterator<String> iterator = parameterValues.keys(); iterator.hasNext();) {
					String key = iterator.next();
					String value = parameterValues.getString(key);
					parametersMap.put(key, value);
				}			
			}
		} catch(Throwable t) {
			logger.error("Impossible to parse input parameter [" + INPUT_PARAMETER_DS_PARAMETERS_VALUES+ "]", t);
		}
		
		return parametersMap;
	}
	
	private String parametersJsonToXML(String parsJson) {
		String xml = null;
		SourceBean sb = null;
		
		try{
			JSONObject json = new JSONObject(parsJson);
			sb = new SourceBean("PARAMETERSLIST");	
			SourceBean sb1 = new SourceBean("ROWS");
			for (Iterator iterator = json.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String t = json.getString(key);
				SourceBean b = new SourceBean("ROW");
				b.setAttribute("NAME", key);
				b.setAttribute("TYPE", t);
				sb1.setAttribute(b);
			}

			sb.setAttribute(sb1);
			xml = sb.toXML(false);
		}
		catch (Exception e) {
			logger.error("error in parsing "+parsJson,e);
		}
		Assert.assertNotNull(xml, "There was an error in parsing "+parsJson);

		return xml;

	}
}
