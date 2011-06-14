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

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.drivers.worksheet.WorksheetDriver;
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

	private IBIObjectDAO objDao = null;
	
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

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		try {
			if (serviceType != null && serviceType.equalsIgnoreCase(DOC_SAVE)) {
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
		String template = getAttributeAsString(TEMPLATE);	
		JSONArray functsArrayJSon = getAttributeAsJSONArray(FUNCTS);
		String wk_definition = getAttributeAsString(OBJECT_WK_DEFINITION);
		JSONObject smartFilterValues = getAttributeAsJSONObject(FORMVALUES);
		String query = getAttributeAsString(OBJECT_QUERY);

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
			
			if(engineId!=null){
				Engine engine = DAOFactory.getEngineDAO().loadEngineByID(new Integer(engineId));
				o.setEngine(engine);
			}else{
				List<Engine> engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType(type);
				if(engines!=null && !engines.isEmpty()){
					o.setEngine(engines.get(0));
				}
			}
			
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
			
			BIObject orig_obj = objDao.loadBIObjectById(new Integer(orig_biobj_id));
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
			}else{
				logger.error("Document template not available");
				throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.document.saveError");
			}
			
			objTemp.setContent(content);
			objTemp.setCreationUser(creationUser);
			objTemp.setDimension(Long.toString(content.length/1000)+" KByte");
			objTemp.setName("template.sbiworksheet");
			
			try {
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
					logger.debug("New document inserted");
					JSONObject attributesResponseSuccessJSON = new JSONObject();
					attributesResponseSuccessJSON.put("responseText", "Operation succeded");
					writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
				}

			} catch (Throwable e) {
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
		String temp = q.composeWorksheetTemplate(wkDefinition, query, smartFilterValuesString, templCont);
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

}
