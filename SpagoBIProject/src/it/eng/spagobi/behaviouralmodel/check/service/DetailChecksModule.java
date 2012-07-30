/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.behaviouralmodel.check.service;

import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spagobi.behaviouralmodel.check.bo.Check;
import it.eng.spagobi.behaviouralmodel.check.dao.ICheckDAO;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.constants.AdmintoolsConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * Implements a module which  handles all values constraints management: 
 * has methods for values constraint load 
 * detail, modify/insertion and deleting operations. The <code>service</code> method has a 
 * switch for all these operations, differentiated the ones from the others by a 
 * <code>message</code> String.
 * 
 * @author sulis
 */

public class DetailChecksModule extends AbstractModule {
	
	private String modalita = "";
	private Boolean back = new Boolean(false);

	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.module.AbstractModule#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
	}

	/**
	 * Reads the operation asked by the user and calls the insertion, modify, detail and
	 * deletion methods.
	 * 
	 * @param request The Source Bean containing all request parameters
	 * @param response The Source Bean containing all response parameters
	 * 
	 * @throws exception If an exception occurs
	 * @throws Exception the exception
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		String message = (String) request.getAttribute("MESSAGEDET");
		SpagoBITracer.debug(AdmintoolsConstants.NAME_MODULE, "DetailChecksModule","service","begin of detail Engine modify/visualization service with message =" +message);

		EMFErrorHandler errorHandler = getErrorHandler();
		try {
			if (message == null) {
				EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 101);
				SpagoBITracer.debug(AdmintoolsConstants.NAME_MODULE, "DetailChecksModule", "service", "The message parameter is null");
				throw userError;
			}
			if (message.trim().equalsIgnoreCase(AdmintoolsConstants.DETAIL_SELECT)) {
				String id = (String) request.getAttribute("ID");
				getDetailCheck(id,response);
			} else if (message.trim().equalsIgnoreCase(AdmintoolsConstants.DETAIL_MOD)) {
				//nameControl(request,"MODIFY");
				modDetailCheck(request, AdmintoolsConstants.DETAIL_MOD, response);
			} else if (message.trim().equalsIgnoreCase(AdmintoolsConstants.DETAIL_NEW)) {
				newDetailCheck(response);
			} else if (message.trim().equalsIgnoreCase(AdmintoolsConstants.DETAIL_INS)) {
				//nameControl (request,"INSERT");
				modDetailCheck(request, AdmintoolsConstants.DETAIL_INS, response);
			} else if (message.trim().equalsIgnoreCase(AdmintoolsConstants.DETAIL_DEL)) {
				delDetailCheck(request, AdmintoolsConstants.DETAIL_DEL, response);
			}

		} catch (EMFUserError eex) {
			errorHandler.addError(eex);
			return;
		} catch (Exception ex) {
			EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, ex);
			errorHandler.addError(internalError);
			return;
		}
	}


	
	/**
	 * Gets the detail of a value comstraint choosed by the user from the 
	 * values constraints list. It reaches the key from the request and asks 
	 * to the DB all detail value constraint
	 * information, by calling the method <code>loadCheckbyID</code>.
	 *   
	 * @param key The choosed value constraint id key
	 * @param response The response Source Bean
	 * @throws EMFUserError If an exception occurs
	 */
	private void getDetailCheck(String key, SourceBean response) throws EMFUserError {
		try {
			this.modalita = AdmintoolsConstants.DETAIL_MOD;
			response.setAttribute("modality", modalita);	
			Check aCheck= DAOFactory.getChecksDAO().loadCheckByID(new Integer(key));
			response.setAttribute("checkObj", aCheck);			
		} catch (Exception ex) {
			// PER MONIA, CHECK.ADD/MODIFY, userId, aCheck.getName(), aCheck.getValueTypeCd()
			SpagoBITracer.major(AdmintoolsConstants.NAME_MODULE, "DettaglioEngineModule","getDettaglioEngine","Cannot fill response container", ex  );
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	
	/**
	 * Inserts/Modifies the detail of a value constraint according to the user request. 
	 * When a value constraint is modified, the <code>modifyCheck</code> 
	 * method is called; when a new value constraint is added, the <code>insertCheck</code>
	 * method is called. These two cases are differentiated by the <code>mod</code> String input value. 
	 * 
	 * @param request The request information contained in a SourceBean Object
	 * @param mod A request string used to differentiate insert/modify operations
	 * @param response The response SourceBean 
	 * @throws EMFUserError If an exception occurs
	 * @throws SourceBeanException If a SourceBean exception occurs
	 */
	private void modDetailCheck(SourceBean request, String mod, SourceBean response)
		throws EMFUserError, SourceBeanException {
		try {
			
			Check aCheck = recoverCheckDetails(request);
			EMFErrorHandler errorHandler = getErrorHandler();
			
			// if there are some errors into the errorHandler does not write into DB
			if(!errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR)) {
				response.setAttribute("checkObj", aCheck);
				response.setAttribute("modality", mod);
				// PER MONIA, CHECK.ADD/MODIFY, userId, aCheck.getName(), aCheck.getValueTypeCd()
				return;
			}
			SessionContainer permSess = getRequestContainer().getSessionContainer().getPermanentContainer();
			IEngUserProfile profile = (IEngUserProfile)permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);		
			ICheckDAO dao=DAOFactory.getChecksDAO();
			dao.setUserProfile(profile);
			if (mod.equalsIgnoreCase(AdmintoolsConstants.DETAIL_INS)) {
				dao.insertCheck(aCheck);
			} else {
				dao.modifyCheck(aCheck);
			}
            
		} catch (Exception ex) {			
			// PER MONIA, CHECK.ADD/MODIFY, userId, aCheck.getName(), aCheck.getValueTypeCd()
			SpagoBITracer.major(AdmintoolsConstants.NAME_MODULE, "DetailEngineModule","modDetailEngine","Cannot fill response container", ex  );
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
		response.setAttribute("loopback", "true");
		// PER MONIA, CHECK.ADD/MODIFY, userId, aCheck.getName(), aCheck.getValueTypeCd() --> ESITO OK
	
	}
	
	private Check recoverCheckDetails (SourceBean request) throws EMFUserError {
		
		String idStr = (String)request.getAttribute("id");
		Integer checkId = Integer.valueOf(idStr);
		String description = (String)request.getAttribute("description");
		String name = (String)request.getAttribute("name");
		String label = (String)request.getAttribute("label");
		
		String checkType = (String)request.getAttribute("checkType");
		
		String checkTypeIDStr = checkType.substring(0, checkType.indexOf(";"));
		String checkTypeCode = checkType.substring(checkType.indexOf(";")+1);
		
		Check check = new Check();
		check.setCheckId(checkId);
		check.setDescription(description);
		check.setName(name);
		check.setLabel(label);
		check.setValueTypeId(Integer.valueOf(checkTypeIDStr));
		check.setValueTypeCd(checkTypeCode);
		
		String fieldValue1Name = checkTypeCode+"_value1";
		String fieldValue2Name = checkTypeCode+"_value2";
		
        String value1 = (String)request.getAttribute(fieldValue1Name);
        String value2 = (String)request.getAttribute(fieldValue2Name);
        if (value1!=null && value1.length()<400)check.setFirstValue(value1);
        if (value2!=null && value2.length()<400)check.setSecondValue(value2);

        labelControl(label, checkId);
        
        return check;
        
	}
	
	/**
	 * Deletes a value constraint choosed by user from the values constraints list.
	 * 
	 * @param request	The request SourceBean
	 * @param mod	A request string used to differentiate delete operation
	 * @param response	The response SourceBean
	 * @throws EMFUserError	If an Exception occurs
	 * @throws SourceBeanException If a SourceBean Exception occurs
	 */
	private void delDetailCheck(SourceBean request, String mod, SourceBean response)
	
	throws EMFUserError, SourceBeanException {
		try {
			String id = (String) request.getAttribute("id");
			//if check is in use cannot be erased
			boolean isRef = DAOFactory.getChecksDAO().isReferenced(id);
			if (isRef) {
				// PER MONIA, CHECK.DELETE, userId, (aCheck.getName(), aCheck.getValueTypeCd()) 
				HashMap params = new HashMap();
				params.put(AdmintoolsConstants.PAGE, ListChecksModule.MODULE_PAGE);
				EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, 1028, new Vector(), params);
				getErrorHandler().addError(error);
				return;
			}
			Check aCheck = new Check();
			aCheck.setCheckId(Integer.valueOf(id));
			DAOFactory.getChecksDAO().eraseCheck(aCheck);
		} catch (Exception ex) {
			// PER MONIA, CHECK.DELETE, userId, aCheck.getName(), aCheck.getValueTypeCd()
			SpagoBITracer.major(AdmintoolsConstants.NAME_MODULE, "DetailEngineModule","delDetailRuolo","Cannot fill response container", ex  );
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
		response.setAttribute("loopback", "true");
		// PER MONIA, CHECK.DELETE, userId, aCheck.getName(), aCheck.getValueTypeCd() --> ESITO OK
	}
	
	
	
	/**
	 * Instantiates a new <code>parametere<code> object when a new value constraint 
	 * insertion is required, in order to prepare the page for the insertion.
	 * 
	 * @param response The response SourceBean
	 * @throws EMFUserError If an Exception occurred
	 */
	private void newDetailCheck(SourceBean response) throws EMFUserError {
		try {
			this.modalita = AdmintoolsConstants.DETAIL_INS;
			response.setAttribute("modality", modalita);
            Check aCheck=new Check();
            aCheck.setCheckId(new Integer(-1));
			aCheck.setDescription("");
			aCheck.setFirstValue("");
			aCheck.setSecondValue("");
			aCheck.setName("");
			aCheck.setLabel("");
			aCheck.setValueTypeCd("");
			List checkTypes = DAOFactory.getDomainDAO().loadListDomainsByType("CHECK");
			if (checkTypes == null || checkTypes.size() == 0) {
				aCheck.setValueTypeCd("");
			} else {
				Domain domain = (Domain) checkTypes.get(0);
				aCheck.setValueTypeCd(domain.getValueCd());
			}
			response.setAttribute("checkObj", aCheck);
		} catch (Exception ex) {
			// PER MONIA, CHECK.ADD, userId, aCheck.getName(), aCheck.getValueTypeCd() -
			SpagoBITracer.major(AdmintoolsConstants.NAME_MODULE, "DetailEngineModule","newDetailEngine","Cannot prepare page for the insertion", ex  );
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	
	/**
	 * During a value constraint insertion/modify, controls if the label given to the value constraint
	 * is yet in use.
	 * 
	 * @param label The label of the check to insert/modify
	 * @param checkId The id of the check to insert/modify
	 * 
	 * @throws EMFUserError If any Exception occurred
	 */
	public void labelControl (String label, Integer checkId) throws EMFUserError {
        List checks = DAOFactory.getChecksDAO().loadAllChecks();
		Iterator i = checks.listIterator();
		while (i.hasNext()) {
			Check aCheck = (Check) i.next();
			if (aCheck.getLabel().equals(label) && !aCheck.getCheckId().equals(checkId)) {
				HashMap params = new HashMap();
				params.put(AdmintoolsConstants.PAGE, ListChecksModule.MODULE_PAGE);
				EMFValidationError error = new EMFValidationError(EMFErrorSeverity.ERROR, "label", "1029", new Vector(), params);
				getErrorHandler().addError(error);
			}
		}
	}

}
