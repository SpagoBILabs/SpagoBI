/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.analiticalmodel.execution.service;


import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
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

	public static final String SERVICE_NAME = "CREATE_DATASET_FOR_WORKSHEET_ACTION";



	/**  FIELDS EXPECTEDIN REQUEST
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
	public static final String  PARAMETERS_DEFINITION = "parametersDefinition";
	public static final String  PARAMETERS_VALUES = "parametersValues";	


	// request parameters for dataset	 (Taken from DatsetConstants)

	//	public static String MODALITY = "MODALITY";
	//	public static String MODALITY_DATASET = "DATASET";
	//	public static String MODALITY_BIOBJECT = "BIOBJECT";
	//
	//	public static final String OBJ_LABEL ="label";
	//	public static final String OBJ_NAME ="name";
	//	public static final String OBJ_DESCR ="descr";
	//	public static final String OBJ_PARS ="pars";
	//	public static final String OBJ_FUNCTIONALITY ="functionality";
	//	public static final String OBJ_ENGINE_ID ="engineId";
	//	public static final String OBJ_DATASOURCE_ID ="dataSourceId";
	//	public static final String OBJ_DATASET_ID ="dataSetId";
	//	public static final String OBJ_STATE_ID ="stateId";

	public static final String DS_TYPE = DataSetConstants.DS_CUSTOM;	

	public static final String WORKSHEET_VALUE_CODE ="WORKSHEET";
	public static final String BIOBJ_TYPE_DOMAIN_CD ="BIOBJ_TYPE";

	// JSON Object representing object parameters
	//	public static final String OBJPAR_PAR_ID ="parId";
	//	public static final String OBJPAR_PARAMETER_URL_NAME ="parameterUrlName";
	//	public static final String OBJPAR_LABEL ="label";


	// logger component
	private static Logger logger = Logger.getLogger(CreateDatasetForWorksheetAction.class);

	GuiGenericDataSet ds;
	CustomDataSetDetail dsDetail;

	BIObject biObj;



	public void doService() {
		ExecutionInstance instance;

		//	String modality;


		IEngUserProfile profile;

		logger.debug("IN");

		try {

			profile = getUserProfile();

			ds = collectDatasetInformations();

			CreationUtilities creatUtils = new CreationUtilities();
			Integer returnId = null;

			try{			
				returnId = creatUtils.creatDataSet(ds);
			}
			catch (Exception e) {
				logger.error("Error during creation method.", e);
				throw new SpagoBIServiceException(SERVICE_NAME, 
						"Error during dataset creation method "+e.getMessage());				
			}

			// check and id has been returned.
			if(returnId == null){
				throw new SpagoBIServiceException(SERVICE_NAME, 
				" dataset creation method did not return any result: check log");	
			}
			
			logger.debug("Getting PARAMETERS_VALUES from the request");
			String valsJson = getAttributeAsString( PARAMETERS_VALUES );
			
			// put in response parameters 
			Map valsMap = null;
			if(valsJson != null && !valsJson.equals("")){
				valsMap = getParameterValues(valsJson);
				LogMF.debug(logger,"The PARAMETERS_VALUES are: {0}", valsJson);
			}else{
				logger.debug("No PARAMETERS_VALUES is empty ");
			}
				


			// ExecutionInstance has been created it's time to prepare the response with the instance unique id and flush it to the client

			try {
				getServiceResponse().setAttribute("dataset_id", returnId);					
				getServiceResponse().setAttribute("dataset_label", ds.getLabel());	

				if(valsMap != null){
					for (Iterator iterator = valsMap.keySet().iterator(); iterator.hasNext();) {
						String n = (String) iterator.next();
						Object v = valsMap.get(n);
						// passing parameters with prefix PAR_
						getServiceResponse().setAttribute("PAR_"+n, v);
					}

				}

			} catch (SourceBeanException e1) {
				logger.error("Error ins etting response",e1);
			}


		} finally {
			logger.debug("OUT");
		}
	}

	private Map getParameterValues(String valsJson){
		logger.debug("Getting the parameters value");
		Map parsValuesMap = new HashMap<String, String>();
		try{
			JSONObject json = new JSONObject(valsJson);
			for (Iterator iterator = json.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String v = json.getString(key);
				parsValuesMap.put(key, v);
			}

		}
		catch (Exception e) {
			logger.error("error in parsing "+valsJson,e);
			parsValuesMap = null;
		}
		Assert.assertNotNull(parsValuesMap, "error in parsing "+valsJson);
		LogMF.debug(logger, "Parameters map loaded.. {0}", parsValuesMap);
		return parsValuesMap;
	}


	/** get dataset information from request
	 * 
	 * @return  GuiGenericDataSet
	 */
	private GuiGenericDataSet collectDatasetInformations(){
		logger.debug("IN");
		// Dataset Fields
		String dsLabel;
		String dsName;
		String dsDescr;
		String dsPars;
		String dsCustomData;
		String dsJClassName;
		String dsMetadata;
		String parametersDefinitionJson = null;
		String parametersDefinitionXML = null;

		logger.debug("Collecting information necessary for build thd dataset:");
		logger.debug("Getting LABEL from request");
		dsLabel = getAttributeAsString( DataSetConstants.LABEL );
		logger.debug("LABEL= "+dsLabel);
		logger.debug("Getting NAME from request");
		dsName = getAttributeAsString( DataSetConstants.NAME );
		logger.debug("NAME= "+dsLabel);
		logger.debug("Getting DESCRIPTION from request");
		dsDescr = getAttributeAsString( DataSetConstants.DESCRIPTION );
		logger.debug("DESCRIPTION= "+dsLabel);
		logger.debug("Getting PARS from request");
		dsPars = getAttributeAsString( DataSetConstants.PARS );
		logger.debug("PARS= "+dsLabel);
		logger.debug("Getting CUSTOM_DATA from request");
		dsCustomData = getAttributeAsString( DataSetConstants.CUSTOM_DATA );
		logger.debug("CUSTOM_DATA= "+dsLabel);
		logger.debug("Getting JCLASS_NAME from request");
		dsJClassName = getAttributeAsString( DataSetConstants.JCLASS_NAME );
		logger.debug("JCLASS_NAME= "+dsLabel);
		logger.debug("Getting DS_METADATA from request");
		dsMetadata = getAttributeAsString( DataSetConstants.DS_METADATA );
		logger.debug("DS_METADATA= "+dsLabel);
			
		
		if(getAttributeAsString( PARAMETERS_DEFINITION ) != null && !getAttributeAsString( PARAMETERS_DEFINITION ).equals("")){
			logger.debug("Getting PARAMETERS_DEFINITION from request");
			parametersDefinitionJson = getAttributeAsString( PARAMETERS_DEFINITION );
			logger.debug("parametersDefinitionJson = "+parametersDefinitionJson);
			parametersDefinitionXML = parametersJsonToXML(parametersDefinitionJson);
			logger.debug("parametersJsonToXML = "+parametersDefinitionXML);
		}else{
			logger.debug("No PARAMETERS_DEFINITION in the request");
		}

		if(dsLabel== null){
			logger.error("Action "+SERVICE_NAME+ " ");
			throw new SpagoBIServiceException(SERVICE_NAME, "Could not find label for dataset in request: label is mandatory.");
		}

		dsDetail = new CustomDataSetDetail();
		dsDetail.setCustomData(dsCustomData);
		dsDetail.setJavaClassName(dsJClassName);
		dsDetail.setParameters(parametersDefinitionXML);
		dsDetail.setDsType(DataSetConstants.DS_CUSTOM);
		
		if(dsMetadata != null && !dsMetadata.equals(""))
			dsDetail.setDsMetadata(dsMetadata);
		
		logger.debug("Start building the GuiGenericDataSet...");
		ds = new GuiGenericDataSet();
		ds.setLabel(dsLabel);
		ds.setName(dsName);
		ds.setDescription(dsDescr);

		ds.setActiveDetail(dsDetail);
		logger.debug("GuiGenericDataSet builded...");
		logger.debug("OUT");

		return ds;
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
