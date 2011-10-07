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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;



/** Action that get in request infos to build a biObject (of type Worksheet) or a dataset (o ftype custom)
 * 
 * @author Giulio gavardi
 */
public class CreateDatasetForWorksheetAction extends ExecuteDocumentAction {

	public static final String SERVICE_NAME = "CREATE_DATASET_FOR WORKSHEET_ACTION";



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


	// request parameters for dataset	

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

			UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
			UUID uuidObj = uuidGen.generateTimeBasedUUID();
			String executionContextId = uuidObj.toString();


			String valsJson = getAttributeAsString( PARAMETERS_VALUES );
			// put in response parameters ANNO and ENTE
			Map valsMap = null;
			if(valsJson != null)
				valsMap = getParameterValues(valsJson);


			// ExecutionInstance has been created it's time to prepare the response with the instance unique id and flush it to the client

			try {
				getServiceResponse().setAttribute("dataset_id", returnId);					
				getServiceResponse().setAttribute("dataset_label", ds.getLabel());	

				if(valsMap != null){
					for (Iterator iterator = valsMap.keySet().iterator(); iterator.hasNext();) {
						String n = (String) iterator.next();
						Object v = valsMap.get(n);
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

		return parsValuesMap;
	}


	/**
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

		dsLabel = getAttributeAsString( DataSetConstants.LABEL );
		dsName = getAttributeAsString( DataSetConstants.NAME );
		dsDescr = getAttributeAsString( DataSetConstants.DESCRIPTION );
		dsPars = getAttributeAsString( DataSetConstants.PARS );
		dsCustomData = getAttributeAsString( DataSetConstants.CUSTOM_DATA );
		dsJClassName = getAttributeAsString( DataSetConstants.JCLASS_NAME );

		dsMetadata = getAttributeAsString( DataSetConstants.DS_METADATA );

		if(getAttributeAsString( PARAMETERS_DEFINITION ) != null){
			parametersDefinitionJson = getAttributeAsString( PARAMETERS_DEFINITION );
			parametersDefinitionXML = parametersJsonToXML(parametersDefinitionJson);
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
		dsDetail.setDsMetadata(dsMetadata);

		ds = new GuiGenericDataSet();
		ds.setLabel(dsLabel);
		ds.setName(dsName);
		ds.setDescription(dsDescr);

		ds.setActiveDetail(dsDetail);

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




















	/**
	 * 
	 * @param biObject
	 * @param parameters
	 */
	//	private void collectParametersInformation(BIObject biObject, String parameters){
	//		logger.debug("IN");
	//
	//		// parameters let's assume is a json St5rring
	//
	//		try {
	//			JSONArray jsonArray = new JSONArray(parameters);
	//
	//			for (int i = 0; i < jsonArray.length(); i++) {
	//				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
	//				// recover informations
	//				String label = jsonObject.getString(OBJPAR_LABEL);
	//				String parId = jsonObject.getString(OBJPAR_PAR_ID);
	//				String parameterUrlName = jsonObject.getString(OBJPAR_PARAMETER_URL_NAME);
	//
	//				if(biObject.getBiObjectParameters()== null ){
	//					biObject.setBiObjectParameters(new ArrayList<BIObjectParameter>());
	//				}
	//
	//				BIObjectParameter biObjectParameter = new BIObjectParameter();
	//				biObjectParameter.setParID(Integer.valueOf(parId));
	//				biObjectParameter.setParameterUrlName(parameterUrlName);
	//				biObjectParameter.setLabel(label);
	//				biObject.getBiObjectParameters().add(biObjectParameter);
	//			}
	//		} catch (JSONException e) {
	//			logger.error("parameters JSON string not well formed: "+parameters);
	//			throw new SpagoBIServiceException(SERVICE_NAME, "parameters JSON string not well formed: "+parameters);
	//		}
	//
	//		logger.debug("OUT");
	//	}


	//	private BIObject collectBiObjectInformations(){
	//		logger.debug("IN");
	//		// Dataset Fields
	//		String objLabel;
	//		String objName;
	//		String objDescr;
	//		String objPars;
	//		Integer objFunctionality;
	//
	//		Integer engineId;
	//		Integer dataSetId;
	//		Integer dataSourceId;
	//		Integer stateId;
	//
	//		String parameters;
	//
	//		try{
	//
	//			objLabel = getAttributeAsString( OBJ_LABEL );
	//			objName = getAttributeAsString( OBJ_NAME );
	//			objDescr = getAttributeAsString( OBJ_DESCR );
	//			objPars = getAttributeAsString( OBJ_PARS );
	//			objFunctionality = getAttributeAsInteger( OBJ_FUNCTIONALITY );
	//			engineId = getAttributeAsInteger( OBJ_ENGINE_ID );
	//			dataSourceId = getAttributeAsInteger( OBJ_DATASOURCE_ID );
	//			dataSetId = getAttributeAsInteger( OBJ_DATASET_ID );
	//			stateId = getAttributeAsInteger(OBJ_STATE_ID);
	//
	//
	//			if(objLabel== null){
	//				logger.error("Action "+SERVICE_NAME+ " ");
	//				throw new SpagoBIServiceException(SERVICE_NAME, "Could not find label for object in request: label is mandatory.");
	//			}
	//			biObj = new BIObject();
	//
	//			biObj.setLabel(objLabel);
	//			biObj.setName(objName);
	//			biObj.setDescription(objDescr);
	//			biObj.setDataSetId(dataSetId);
	//			biObj.setDataSourceId(dataSourceId);
	//			biObj.setStateID(stateId);
	//			// get Worksheet Engine
	//
	//			//List<Engine> engines = DAOFactory.getEngineDAO().loadEngineByObjType(BIOBJ_TYPE_DOMAIN_CD, WORKSHEET_VALUE_CODE);
	//			List<Engine> engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType(WORKSHEET_VALUE_CODE);
	//
	//			if(engines != null){
	//				if(engines.size()>1){
	//					logger.warn("More than one engine found for type "+WORKSHEET_VALUE_CODE+": should be at most one, first found will be assigned ");
	//				}
	//				// should be at most one
	//				boolean stop = false;
	//				for (Iterator iterator = engines.iterator(); iterator.hasNext() && !stop;) {
	//					Engine engine = (Engine) iterator.next();
	//					biObj.setEngine(engine);			
	//					logger.warn("ASeeigne3d engine with label "+engine.getLabel());
	//					stop = true;
	//				}
	//			}
	//			else{
	//				throw new SpagoBIServiceException(SERVICE_NAME, "Wotksheet engine not found");
	//			}
	//
	//			//set type
	//			Domain typeDom = DAOFactory.getDomainDAO().loadDomainByCodeAndValue(BIOBJ_TYPE_DOMAIN_CD, WORKSHEET_VALUE_CODE);
	//			biObj.setBiObjectTypeID(typeDom.getValueId());
	//			biObj.setBiObjectTypeCode(typeDom.getValueCd());
	//			biObj.setVisible(Integer.valueOf(1));
	//			biObj.setCreationUser(getUserProfile().getUserUniqueIdentifier().toString());
	//
	//
	//			// get functionalities, assume it is simply the id
	//			List toInsert = null;
	//			if(objFunctionality != null){
	//				toInsert = new ArrayList<Integer>();
	//				toInsert.add(objFunctionality);
	//			}
	//			biObj.setFunctionalities(toInsert);
	//
	//			// parameters,a ssume it is a json
	//			logger.debug("recover parameters informations with JSON string "+objPars);
	//			if(objPars != null && !objPars.equals("")){
	//				collectParametersInformation(biObj, objPars);
	//			}
	//		}
	//		catch (EMFUserError e) {
	//			logger.error("Error in collecting object informations ",e);
	//			throw new SpagoBIServiceException(SERVICE_NAME, "Error in collecting object informations");
	//		}
	//
	//
	//
	//		logger.debug("OUT");
	//
	//		return biObj;
	//	}







}
