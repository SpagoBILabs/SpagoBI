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
package it.eng.spagobi.hotlink.service;

import java.sql.Connection;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.hotlink.rememberme.bo.RememberMe;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * 
 * @author Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class HotLinkModule extends AbstractModule {

	private static transient Logger logger = Logger.getLogger(HotLinkModule.class);
	public static final String MODULE_NAME = "HotLinkModule"; 
	
	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.ServiceIFace#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response)
			throws Exception {
		logger.debug("IN");
		//Start writing log in the DB
		Session aSession =null;
		try {
			aSession = HibernateUtil.currentSession();
			Connection jdbcConnection = aSession.connection();
			IEngUserProfile profile = UserUtilities.getUserProfile();
			AuditLogUtilities.updateAudit(jdbcConnection,  profile, "activity.HotLinkMenu", null);
		} catch (HibernateException he) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		//End writing log in the DB
		
		try {
			String operation = (String) request.getAttribute("OPERATION");
			if (operation == null || operation.equalsIgnoreCase("GET_HOT_LINK_LIST")) {
				getHotLinkListHandler(request, response);
			} else if (operation.equalsIgnoreCase("DELETE_REMEMBER_ME")) {
				deleteRememberMeHandler(request, response);
			} 
		} catch (EMFUserError error) {
			logger.error(error);
			getErrorHandler().addError(error);
		} catch (Exception e) {
			logger.error(e);
			EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, e);
			getErrorHandler().addError(internalError);
		} finally {
			logger.debug("OUT");
		}
	}

	private void getHotLinkListHandler(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");
		try {
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "HOT_LINK_HOME");
		} finally {
			logger.debug("OUT");
		}
	}
	
	private void deleteRememberMeHandler(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");
		try {
			String rememberMeIdStr = (String) request.getAttribute("REMEMBER_ME_ID");
			Integer rememberMeId = new Integer(rememberMeIdStr);
			RememberMe rm = DAOFactory.getRememberMeDAO().getRememberMe(rememberMeId);
			UserProfile profile = (UserProfile) this.getRequestContainer().getSessionContainer().getPermanentContainer().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			// check if user is able to erase remember me
			String userId = profile.getUserId().toString();
			if (rm != null) {
				if (userId.equals(rm.getUserName())) {
					logger.warn("Deleting RememberMe with id = " + rememberMeIdStr + " ...");
					DAOFactory.getRememberMeDAO().delete(rememberMeId);
				} else {
					logger.error("User [" + userId + "] CANNOT erase RememberMe with id = " + rememberMeIdStr + " since he is not the owner.");
				}
			} else {
				logger.warn("RememberMe with id = " + rememberMeIdStr + " not found.");
			}
			getHotLinkListHandler(request, response);
		} finally {
			logger.debug("OUT");
		}
	}
	
}
