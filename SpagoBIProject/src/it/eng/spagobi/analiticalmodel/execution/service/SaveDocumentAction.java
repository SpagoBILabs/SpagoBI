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
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.drivers.worksheet.WorksheetDriver;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
import it.eng.spagobi.tools.dataset.bo.DataSetParameterItem;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class SaveDocumentAction extends AbstractSpagoBIAction {

	// logger component
	private static Logger logger = Logger.getLogger(SaveDocumentAction.class);

	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String DOC_SAVE = "DOC_SAVE";
	private final String DOC_SAVE_FROM_DATASET = "DOC_SAVE_FROM_DATASET";
	private final String DOC_UPDATE = "DOC_UPDATE";

	// RES detail
	private final String ID = "id";
	private final String OBJ_ID = "obj_id";	
	private final String NAME = "name";
	private final String LABEL = "label";
	private final String DESCRIPTION = "description";
	private final String ENGINE = "engineid";
	private final String TYPE = "typeid";
	private final String TEMPLATE = "template";
	private final String DATASOURCE = "datasourceid";
	private final String FUNCTS = "functs";
	private final String OBJECT_WK_DEFINITION = "wk_definition";
	private final String OBJECT_QUERY = "query";
	private final String FORMVALUES = "formValues";

	public static final String OBJ_DATASET_ID ="dataSetId";
	public static final String OBJ_DATASET_LABEL ="dataset_label";


	private IBIObjectDAO objDao = null;



	// default type
	public static final String WORKSHEET_VALUE_CODE ="WORKSHEET";
	public static final String BIOBJ_TYPE_DOMAIN_CD ="BIOBJ_TYPE";

	// default for parameters
	public static final Integer REQUIRED = 0;
	public static final Integer MODIFIABLE = 1;
	public static final Integer MULTIVALUE = 0;
	public static final Integer VISIBLE = 1;

	String serviceType = null;

	@Override
	public void doService() {
		logger.debug("IN");
		try {
			objDao = DAOFactory.getBIObjectDAO();
			objDao.setUserProfile(getUserProfile());
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}

		serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		try {
			if (serviceType != null && (serviceType.equalsIgnoreCase(DOC_SAVE) || serviceType.equalsIgnoreCase(DOC_SAVE_FROM_DATASET))) {
				saveDocument();
			} else if (serviceType != null && serviceType.equalsIgnoreCase(DOC_UPDATE)) {
				updateWorksheetTemplate();
			}
		} catch (SpagoBIServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error while updating document's template", e);
			throw new SpagoBIServiceException(SERVICE_NAME, "sbi.document.saveError", e);
		}
		logger.debug("OUT");
	}

	private void saveDocument() throws Exception {
		String id = getAttributeAsString(ID);
		String orig_biobj_id = getAttributeAsString(OBJ_ID);
		String label = getAttributeAsString(LABEL);
		String name = getAttributeAsString(NAME);
		String description = getAttributeAsString(DESCRIPTION);
		String engineId = getAttributeAsString(ENGINE);
		String dataSourceId = getAttributeAsString(DATASOURCE);
		String type = getAttributeAsString(TYPE);
		String typeId;
		String template = getAttributeAsString(TEMPLATE);	
		JSONArray functsArrayJSon = getAttributeAsJSONArray(FUNCTS);
		String wk_definition = getAttributeAsString(OBJECT_WK_DEFINITION);
		JSONObject smartFilterValues = getAttributeAsJSONObject(FORMVALUES);
		String query = getAttributeAsString(OBJECT_QUERY);

		if(type == null || type.equals("")){
			Domain typeDom = DAOFactory.getDomainDAO().loadDomainByCodeAndValue(BIOBJ_TYPE_DOMAIN_CD, WORKSHEET_VALUE_CODE);
			typeId = typeDom.getValueId().toString();
			type = WORKSHEET_VALUE_CODE;
		}

		if (name != null && name != "" && label != null && label != "" && 
				type!=null && functsArrayJSon!=null && functsArrayJSon.length()!= 0) {

			BIObject o = new BIObject();
			BIObject objAlreadyExisting = objDao.loadBIObjectByLabel(label);
			if(objAlreadyExisting!=null){
				logger.error("Document with the same label already exists");
				throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.document.labelAlreadyExistent");
			}
			o.setName(name);
			o.setLabel(label);
			o.setDescription(description);
			o.setVisible(new Integer(1));

			Engine engine = null;
			if ( engineId != null ) {
				engine = DAOFactory.getEngineDAO().loadEngineByID(new Integer(engineId));
				if ( engine == null ) {
					throw new SpagoBIServiceException(SERVICE_NAME,	"No engine found");
				}
			} else {
				List<Engine> engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType(type);
				if ( engines != null && !engines.isEmpty() ){
					engine = engines.get(0);
				} else {
					throw new SpagoBIServiceException(SERVICE_NAME,	"No suitable engine found for " + type + " document type");
				}
			}
			o.setEngine(engine);

			Domain objType = DAOFactory.getDomainDAO().loadDomainByCodeAndValue(SpagoBIConstants.BIOBJ_TYPE, type);
			Integer biObjectTypeID = objType.getValueId();
			o.setBiObjectTypeID(biObjectTypeID);
			o.setBiObjectTypeCode(objType.getValueCd());

			UserProfile userProfile = (UserProfile) this.getUserProfile();
			String creationUser =  userProfile.getUserId().toString();
			o.setCreationUser(creationUser);
			if(dataSourceId!=null && dataSourceId!=""){
				o.setDataSourceId(new Integer(dataSourceId));
			}	
			List<Integer> functionalities = new ArrayList<Integer>();
			for(int i=0; i< functsArrayJSon.length(); i++){
				String funcIdStr = functsArrayJSon.getString(i);
				Integer funcId = new Integer(funcIdStr);
				if (funcId.intValue() == -1) {
					// -1 stands for personal folder: check is it exists
					boolean exists = UserUtilities.userFunctionalityRootExists(userProfile);
					if (!exists) {
						// create personal folder if it doesn't exist
						UserUtilities.createUserFunctionalityRoot(userProfile);
					}
					// load personal folder to get its id
					LowFunctionality lf = UserUtilities.loadUserFunctionalityRoot(userProfile);
					funcId = lf.getId();
				}
				functionalities.add(funcId);
			}		
			o.setFunctionalities(functionalities);

			Domain objState = DAOFactory.getDomainDAO().loadDomainByCodeAndValue(SpagoBIConstants.DOC_STATE, SpagoBIConstants.DOC_STATE_REL);			
			Integer stateID = objState.getValueId();
			o.setStateID(stateID);
			o.setStateCode(objState.getValueCd());		

			BIObject orig_obj = null;
			if(orig_biobj_id != null && !orig_biobj_id.equals("")){
				orig_obj = objDao.loadBIObjectById(new Integer(orig_biobj_id));
			}

			ObjTemplate objTemp = new ObjTemplate();
			byte[] content = null;
			if(template != null && template != ""){
				content = template.getBytes();
			}else if(smartFilterValues!=null){ 
				content = getSmartFilterTemplateContent();
			}else if(wk_definition!=null && query!=null && orig_obj!=null){
				ObjTemplate qbETemplate = orig_obj.getActiveTemplate();
				String templCont = new String(qbETemplate.getContent());
				WorksheetDriver q = new WorksheetDriver();
				String temp = q.composeWorksheetTemplate(wk_definition, query, null, templCont);
				content = temp.getBytes();
			}
			else if(serviceType.equals(DOC_SAVE_FROM_DATASET) && wk_definition!=null){
				WorksheetDriver q = new WorksheetDriver();
				String temp = q.createNewWorksheetTemplate(wk_definition);
				content = temp.getBytes();
			}
			else{
				logger.error("Document template not available");
				throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.document.saveError");
			}

			objTemp.setContent(content);
			objTemp.setCreationUser(creationUser);
			objTemp.setDimension(Long.toString(content.length/1000)+" KByte");
			objTemp.setName("template.sbiworksheet");


			try {


				if(serviceType.equals(DOC_SAVE)){

					if(id != null && !id.equals("") && !id.equals("0")){							
						o.setId(Integer.valueOf(id));
						objDao.modifyBIObject(o, objTemp);
						logger.debug("Document with id "+id+" updated");
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
					}else{
						Integer biObjectID = objDao.insertBIObject(o, objTemp);
						if(orig_biobj_id!=null && orig_biobj_id!=""){					
							List obj_pars = orig_obj.getBiObjectParameters();
							if(obj_pars!=null && !obj_pars.isEmpty()){
								Iterator it = obj_pars.iterator();
								while(it.hasNext()){
									BIObjectParameter par = (BIObjectParameter)it.next();
									par.setBiObjectID(biObjectID);
									par.setId(null);
									DAOFactory.getBIObjectParameterDAO().insertBIObjectParameter(par);
								}
							}
						}
						logger.debug("New document inserted with id "+biObjectID);
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );



					}
				}
				// CASE saveing from dataset: get parameters from dataset
				else if(serviceType.equals(DOC_SAVE_FROM_DATASET)){
					logger.debug("case "+DOC_SAVE_FROM_DATASET+" search parameters in dataset");
					String dataSetLabel = getAttributeAsString( OBJ_DATASET_LABEL );
					GuiGenericDataSet guiGenDs = DAOFactory.getDataSetDAO().loadDataSetByLabel(dataSetLabel);
					o.setDataSetId(guiGenDs.getDsId());
					Assert.assertNotNull(guiGenDs, "Dataset with label "+dataSetLabel+" was not found");
					GuiDataSetDetail dsDetail = guiGenDs.getActiveDetail();			

					// insert parameters
					String parametersXML = dsDetail.getParameters();
					collectParametersInformation(o, parametersXML);
					objDao.insertBIObject(o, objTemp, false);
					Integer biObjectID = o.getId();
					List biObjectParameters = o.getBiObjectParameters();
					if(biObjectParameters != null){
						for (Iterator iterator = biObjectParameters.iterator(); iterator.hasNext();) {
							BIObjectParameter parameter = (BIObjectParameter) iterator.next();
							parameter.setBiObjectID(biObjectID);
							creatBiObjectParameter(parameter);
						}
					}

					logger.debug("New document inserted with id "+biObjectID);
					JSONObject attributesResponseSuccessJSON = new JSONObject();
					attributesResponseSuccessJSON.put("responseText", "Operation succeded");
					writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );

				}


			} 
			catch (SpagoBIServiceException e) {
				throw e;			
				}
			catch (Throwable e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,"sbi.document.saveError", e);
			}

		} else {
			logger.error("Document name or label are missing");
			throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.document.missingFieldsError");
		}	
	}

	private void updateWorksheetTemplate() throws Exception {
		logger.debug("IN");
		byte[] content = getTemplateContent();
		ObjTemplate objTemp = createNewTemplate(content);
		ExecutionInstance executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
		BIObject biobj = executionInstance.getBIObject();
		UserProfile userProfile = (UserProfile) this.getUserProfile();
		logger.info("User with unique identifier " + userProfile.getUserUniqueIdentifier() + ", id " + userProfile.getUserId() 
				+ ", name " + userProfile.getUserName() + " is updating document with id " + biobj.getId() 
				+ ", label " + biobj.getLabel() + ", name " + biobj.getName() + "; new template is:");
		logger.info(new String(objTemp.getContent()));
		objDao.modifyBIObject(biobj, objTemp);
		logger.debug("Template of document with id " + biobj.getId() + ", label " + biobj.getLabel() + ", name " + biobj.getName() + " updated");
		JSONObject response = new JSONObject();
		response.put("text", "Operation succeded");
		writeBackToClient( new JSONSuccess(response) );
		logger.debug("OUT");
	}

	protected byte[] getTemplateContent() throws Exception {
		logger.debug("OUT");
		String wkDefinition = getAttributeAsString(OBJECT_WK_DEFINITION);
		String query = getAttributeAsString(OBJECT_QUERY);
		JSONObject smartFilterValues = getAttributeAsJSONObject(FORMVALUES);
		String smartFilterValuesString = null;
		if(smartFilterValues!=null){
			smartFilterValuesString =  smartFilterValues.toString();
		}
		logger.debug("Worksheet definition : " + wkDefinition);
		logger.debug("Base query definition : " + query);
		logger.debug("Smart filter values : " + smartFilterValues);
		ExecutionInstance executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
		BIObject biobj = executionInstance.getBIObject();
		ObjTemplate qbETemplate = biobj.getActiveTemplate();
		String templCont = new String(qbETemplate.getContent());
		WorksheetDriver q = new WorksheetDriver();
		String temp = q.updateWorksheetTemplate(wkDefinition, query, smartFilterValuesString, templCont);
		byte[] content = temp.getBytes();
		logger.debug("OUT");
		return content;
	}

	protected ObjTemplate createNewTemplate(byte[] content) {
		logger.debug("IN");
		ObjTemplate objTemp = new ObjTemplate();
		objTemp.setContent(content);
		UserProfile userProfile = (UserProfile) this.getUserProfile();
		objTemp.setCreationUser(userProfile.getUserId().toString());
		objTemp.setDimension(Long.toString(content.length/1000)+" KByte");
		objTemp.setName("template.sbiworksheet");
		logger.debug("OUT");
		return objTemp;
	}

	private byte[] getSmartFilterTemplateContent() throws Exception {
		logger.debug("OUT");
		String wkDefinition = getAttributeAsString(OBJECT_WK_DEFINITION);
		JSONObject smartFilterValues = getAttributeAsJSONObject(FORMVALUES);
		String smartFilterValuesString = smartFilterValues.toString();
		Assert.assertNotNull(wkDefinition, "Missing worksheet definition");
		Assert.assertNotNull(smartFilterValues, "Missing smart Filter Values");
		logger.debug("Worksheet definition : " + wkDefinition);
		logger.debug("Smart filter values : " + smartFilterValues);
		ExecutionInstance executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
		BIObject biobj = executionInstance.getBIObject();
		ObjTemplate qbETemplate = biobj.getActiveTemplate();
		String templCont = new String(qbETemplate.getContent());
		WorksheetDriver q = new WorksheetDriver();
		String temp = q.composeWorksheetTemplate(wkDefinition, null, smartFilterValuesString, templCont);
		byte[] content = temp.getBytes();
		logger.debug("OUT");
		return content;
	}




	/** SAVE from DATASET case
	 * 
	 * 	Get parameters from dataset parameters definitiona nd add them to documetn: parameter label must be equals
	 * to parametersUrlnAME and the parameter must be already present in system.
	 * 
	 * @param biObj
	 * @param parsXml
	 */


	private void collectParametersInformation(BIObject biObj , String parsXml){
		logger.debug("IN");	

		// get parameters from Dataset XML

		List<DataSetParameterItem> listDs;
		try {
			listDs = loadFromXML(parsXml);
		} catch (SourceBeanException e) {
			logger.error("error in reading dataset parameters from String "+parsXml);
			throw new SpagoBIServiceException(SERVICE_NAME, "error in reading dataset parameters from String "+parsXml, e);
		}

		// get parameters from SpagoBI
		List<Parameter> listPar = new ArrayList<Parameter>();
		for (Iterator iterator = listDs.iterator(); iterator.hasNext();) {
			DataSetParameterItem dataSetParameterItem = (DataSetParameterItem) iterator.next();

			// check the parameters exist otherwise throw error; they must have same label as dataste parameter name
			Parameter parameter= null;
			try {
				parameter = DAOFactory.getParameterDAO().loadForDetailByParameterLabel(dataSetParameterItem.getName());
			} catch (EMFUserError e) {
				logger.error("Parameter with label "+dataSetParameterItem.getName()+" was not found!");
				throw new SpagoBIServiceException(SERVICE_NAME, "Parameter with label "+dataSetParameterItem.getName()+" was not found!", e);
			}
			if(parameter == null){
				logger.error("Parameter with label "+dataSetParameterItem.getName()+" was not found!");
				throw new SpagoBIServiceException(SERVICE_NAME, "Parameter with label "+dataSetParameterItem.getName()+" was not found!");
			}
			listPar.add(parameter);
		}

		// insert parameters as BiObjectParameters
		for (int i = 0 ; i < listPar.size(); i++) {	
			Parameter parameter = (Parameter) listPar.get(i);
			BIObjectParameter biObjectParameter = new BIObjectParameter();
			biObjectParameter.setParID(parameter.getId());
			biObjectParameter.setParameter(parameter);
			biObjectParameter.setParameterUrlName(parameter.getLabel());
			biObjectParameter.setLabel(parameter.getName());
			biObjectParameter.setRequired(REQUIRED);
			biObjectParameter.setMultivalue(MULTIVALUE);
			biObjectParameter.setModifiable(MODIFIABLE);
			biObjectParameter.setVisible(VISIBLE);
			biObjectParameter.setPriority(i + 1);

			if(biObj.getBiObjectParameters() == null)
				biObj.setBiObjectParameters(new ArrayList<BIObjectParameter>());
			biObj.getBiObjectParameters().add(biObjectParameter);
			logger.debug("inserted parameter with label "+parameter.getLabel());
		}

		logger.debug("OUT");	
	}

	/** SAVE from DATASET case
	 * 
	 * @param parsXml
	 * @return
	 * @throws SourceBeanException
	 */
	public List<DataSetParameterItem> loadFromXML (String parsXml) throws SourceBeanException {
		logger.debug("IN");
		parsXml = parsXml.trim();
		// load data from xml
		SourceBean source = SourceBean.fromXMLString(parsXml);
		List listRows = source.getAttributeAsList("ROWS.ROW");
		Iterator iterRows = listRows.iterator();
		List<DataSetParameterItem> parsList = new ArrayList<DataSetParameterItem>();
		while(iterRows.hasNext()){
			DataSetParameterItem par = new DataSetParameterItem();
			SourceBean element = (SourceBean)iterRows.next();
			String name = (String)element.getAttribute("NAME");
			par.setName(name);
			String type = (String)element.getAttribute("TYPE");
			par.setType(type);
			parsList.add(par);
		}
		logger.debug("OUT");
		return parsList;
	}

	/** SAVE from DATASET case
	 * 
	 * @param biObjectParameter
	 * @return
	 * @throws NotAllowedOperationException
	 * @throws EMFUserError
	 */

	private  Integer creatBiObjectParameter(BIObjectParameter biObjectParameter) throws NotAllowedOperationException, EMFUserError{
		logger.debug("IN");
		Integer toReturn = null;

		IBIObjectParameterDAO biObjParameterDAO = DAOFactory.getBIObjectParameterDAO();
		biObjParameterDAO.insertBIObjectParameter(biObjectParameter);
		logger.debug("Inserted parameter with parameter url "+biObjectParameter.getParameterUrlName());

		logger.debug("OUT");
		return toReturn;
	}


}
