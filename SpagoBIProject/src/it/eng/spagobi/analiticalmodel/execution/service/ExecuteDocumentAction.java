/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.service;

import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * 
 * @author Zerbetto Davide
 *
 */
public class ExecuteDocumentAction extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "EXECUTE_DOCUMENT_ACTION";
	
	// logger component
	private static Logger logger = Logger.getLogger(ExecuteDocumentAction.class);
	
	public void doService() {
		logger.debug("IN");
		try {
			BIObject obj = getRequiredBIObject();
			if (obj != null) {
				UserProfile profile = (UserProfile) this.getUserProfile();
		    	boolean canSee = ObjectsAccessVerifier.canSee(obj, profile);
		    	if (!canSee) {
		    		logger.error("User [" + profile.getUserId() + "] cannot see document [id: '" + obj.getId() + "', label: '" + obj.getLabel() + "'].");
		    	} else {
					this.getServiceResponse().setAttribute(SpagoBIConstants.OBJECT, obj);
					SubObject subObject = getRequiredSubObject(obj);
					if (subObject != null) {
						if (profile.isAbleToExecuteAction(SpagoBIConstants.DOCUMENT_MANAGEMENT_ADMIN) || 
								(subObject.getIsPublic().booleanValue() || subObject.getOwner().equals(profile.getUserId()))) {
							this.getServiceResponse().setAttribute(SpagoBIConstants.SUBOBJECT, subObject);
						} else {
							logger.warn("User cannot see subobject [" + subObject.getName() + "] of document with label [" + obj.getLabel() + "].");
						}
					}
		    	}
			} else {
				logger.error("Document not found.");
			}
		} catch (EMFInternalError e) {
			logger.error("Service internal error", e);
		} catch (SourceBeanException e) {
			logger.error("Service internal error", e);
		} catch (EMFUserError e) {
			logger.error("Service internal error", e);
		}
		logger.debug("OUT");
	}
	
	protected BIObject getRequiredBIObject() throws EMFUserError {
		logger.debug("IN");
	    Integer id = this.getAttributeAsInteger(ObjectsTreeConstants.OBJECT_ID);
	    logger.debug("Document id in request is [" + id + "]");
	    String label = this.getAttributeAsString(ObjectsTreeConstants.OBJECT_LABEL);
	    logger.debug("Document label in request is [" + label + "]");
	    BIObject obj = null;
    	if (id != null) {
    		obj = DAOFactory.getBIObjectDAO().loadBIObjectById(id);
    	} else if (label != null) {
    		obj = DAOFactory.getBIObjectDAO().loadBIObjectByLabel(label);
    	}
	    logger.debug("OUT");
	    return obj;
	}

	protected SubObject getRequiredSubObject(BIObject obj) throws EMFUserError {
		logger.debug("IN");
		SubObject subObject = null;
		try {
			String subobjectName = this.getAttributeAsString(SpagoBIConstants.SUBOBJECT_NAME);
			if (subobjectName != null) {
				subObject = DAOFactory.getSubObjectDAO().getSubObjectByNameAndBIObjectId(subobjectName, obj.getId());
				if (subObject == null) {
					logger.warn("No accessible subObject with name [" + subobjectName + "] found.");
				}
			} else {
				logger.debug("No subobjectName parameter found on request");
			}
		} finally {
			logger.debug("OUT");
		}
		return subObject;
	}
	
}
