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
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.tools.udp.bo.Udp;
import it.eng.spagobi.tools.udp.bo.UdpValue;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SaveDocumentAction extends AbstractSpagoBIAction {

	// logger component
	private static Logger logger = Logger.getLogger(SaveDocumentAction.class);
	
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String DOC_SAVE = "DOC_SAVE";

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

	@Override
	public void doService() {
		logger.debug("IN");
		IBIObjectDAO objDao;
		try {
			objDao = DAOFactory.getBIObjectDAO();
			objDao.setUserProfile(getUserProfile());
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		try {
		if (serviceType != null	&& serviceType.equalsIgnoreCase(DOC_SAVE)) {
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

			if (name != null && name != "" && label != null && label != "" && 
				template != null && type!=null && 
				functsArrayJSon!=null && functsArrayJSon.length()!= 0) {
				
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
						
				String creationUser =  (String)getUserProfile().getUserUniqueIdentifier();
				o.setCreationUser(creationUser);
				if(dataSourceId!=null && dataSourceId!=""){
					o.setDataSourceId(new Integer(dataSourceId));
				}	
				List<Integer> functionalities = new ArrayList<Integer>();
				for(int i=0; i< functsArrayJSon.length(); i++){
					String funcId = functsArrayJSon.getString(i);
					functionalities.add(new Integer(funcId));
				}		
				o.setFunctionalities(functionalities);
				
				Domain objState = DAOFactory.getDomainDAO().loadDomainByCodeAndValue(SpagoBIConstants.DOC_STATE, SpagoBIConstants.DOC_STATE_REL);			
				Integer stateID = objState.getValueId();
				o.setStateID(stateID);
				o.setStateCode(objState.getValueCd());		
				
				ObjTemplate objTemp = new ObjTemplate();
				byte[] content = template.getBytes();
				objTemp.setContent(content);
				objTemp.setCreationUser(creationUser);
				objTemp.setDimension(Long.toString(content.length/1000)+" KByte");
				objTemp.setName(name);
				
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
							BIObject orig_obj = objDao.loadBIObjectById(new Integer(orig_biobj_id));
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
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
					}

				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
					throw new SpagoBIServiceException(SERVICE_NAME,"sbi.document.saveError", e);
				}
				
			}else{
				logger.error("Document name or label are missing");
				throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.document.missingFieldsError");
			}		
		  }
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.document.saveError", e1);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.document.saveError", e);
		}
		logger.debug("OUT");
	}

}
