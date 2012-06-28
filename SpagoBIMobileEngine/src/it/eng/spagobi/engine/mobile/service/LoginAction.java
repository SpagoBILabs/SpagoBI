/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.mobile.service;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.services.security.service.SecurityServiceSupplierFactory;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;
import it.eng.spagobi.wapp.util.MenuUtilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class LoginAction extends AbstractEngineAction{
	
	IEngUserProfile profile = null;
	EMFErrorHandler errorHandler = null;
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String PROP_NODE = "changepwd.";
	
	private static transient Logger logger = Logger.getLogger(LoginAction.class);

	public void service(SourceBean request, SourceBean response) {
		
		RequestContainer reqCont = RequestContainer.getRequestContainer();
		SessionContainer sessCont = reqCont.getSessionContainer();
		SessionContainer permSess = sessCont.getPermanentContainer();
		HttpServletRequest servletRequest=getHttpRequest();

		UserProfile previousProfile = (UserProfile) permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		String userId = (String)request.getAttribute("userID");
		logger.debug("userID="+userId);
		try {
			if (userId == null) {
				if (previousProfile != null) {
					profile = previousProfile;
					// user is authenticated, nothing to do
					logger.debug("User is authenticated");
					// fill response
					
						MenuUtilities.getMenuItems(request, response, profile);
	
					// set publisher name
					response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "userhome");
					return;
				} else {
					// user must authenticate
					logger.debug("User must authenticate");
					String url = servletRequest.getProtocol().substring(0,servletRequest.getProtocol().indexOf("/")) + 
					"://"+servletRequest.getServerName()+":"+servletRequest.getLocalPort()+servletRequest.getContextPath();
					response.setAttribute("start_url", url);
					response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "login");
					logger.debug("OUT");
					return;
				}
	
			}			

			ISecurityServiceSupplier supplier= SecurityServiceSupplierFactory.createISecurityServiceSupplier();
			String pwd=(String)request.getAttribute("password");       
			try {
				Object ris=supplier.checkAuthentication(userId, pwd);
				if (ris==null){
					logger.error("pwd uncorrect");
					EMFUserError emfu = new EMFUserError(EMFErrorSeverity.ERROR, 501);
					errorHandler.addError(emfu); 		    	
					return;
				}
			} catch (Exception e) {
				logger.error("Reading user information... ERROR");
				SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Login", "Authentication failure");
				writeBackToClient(new JSONFailure(serviceError));
				throw new SecurityException("Reading user information... ERROR",e);
			}
			//MenuUtilities.getMenuItems(request, response, profile);

			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "userhome");
			try {	
				logger.info("User ["+ userId + "] has been autheticated succesfully");
				
				profile = UserUtilities.getUserProfile( userId );
				Assert.assertNotNull(profile, "Impossible to load profile for the user [" + userId + "]");

				
				logger.info("User ["+ userId + "] profile has been loaded succesfully");
				
				// Propagate user profile
				getSessionContainer().getPermanentContainer().setAttribute(IEngUserProfile.ENG_USER_PROFILE, profile);
				writeBackToClient(new JSONSuccess("userhome"));
				


			} catch (Exception e) {
				SpagoBIEngineServiceException serviceError = new SpagoBIEngineServiceException("Login", "Authentication failure");
				logger.error("Exception occurred writing back to client", e);
				writeBackToClient(new JSONFailure(serviceError));
                                                                                                                                                                                                                                                   
			}
			logger.debug("OUT");		

		} catch (EMFUserError e) {
			
			logger.error("Error retrieving menu items", e);
		} catch (SourceBeanException e) {
			logger.error("Error reading response", e);
		} catch (Exception e) {
			
			logger.error("Error checking password", e);
			
		}
		
	}	
	private boolean checkPwd(SbiUser user) throws Exception{
		logger.debug ("IN");
		boolean toReturn = false;
		if (user==null) return toReturn;
		Date currentDate = new Date();

		//gets the active controls to applicate:
		IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
		List lstConfigChecks = configDao.loadConfigParametersByProperties(PROP_NODE);
		logger.debug("checks found on db: " + lstConfigChecks.size());

		for(int i=0; i<lstConfigChecks.size(); i++){
			Config check = (Config)lstConfigChecks.get(i);
			if ((SpagoBIConstants.CHANGEPWD_CHANGE_FIRST).equals(check.getLabel()) && user.getDtLastAccess() == null){
				//if dtLastAccess isn't enhanced it represents the first login, so is necessary change the pwd
				logger.info("The pwd needs to activate!");
				toReturn = true;
				break;
			}

			if ((SpagoBIConstants.CHANGEPWD_EXPIRED_TIME).equals(check.getLabel()) &&
					user.getDtPwdEnd() != null && currentDate.compareTo(user.getDtPwdEnd()) >= 0){
				//check if the pwd is expiring, in this case it's locked.
				logger.info("The pwd is expiring... it should be changed");
				toReturn = true;
				break;
			}
			if ((SpagoBIConstants.CHANGEPWD_DISACTIVE_TIME).equals(check.getLabel())){
				//defines the end date for uselessness
				Date tmpEndForUnused = null;
				if (user.getDtLastAccess() != null){
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Calendar cal = Calendar.getInstance();
					cal.set(user.getDtLastAccess().getYear()+1900, user.getDtLastAccess().getMonth(), user.getDtLastAccess().getDate());
					cal.add(Calendar.MONTH, 6);
					try{
						tmpEndForUnused = StringUtilities.stringToDate(sdf.format(cal.getTime()), DATE_FORMAT);
						logger.debug ("End Date For Unused: " + tmpEndForUnused);
					}catch(Exception e){
						logger.error("The control pwd goes on error: "+e);							
					}	
				}
				if (tmpEndForUnused != null && currentDate.compareTo(tmpEndForUnused) >= 0){
					//check if the pwd is unused by 6 months, in this case it's locked.
					logger.info("The pwd is unused more than 6 months! It's locked!!");
					toReturn = true;
					break;
				}
			}					
		} //for

		//general controls: check if the account is already blocked, otherwise update dtLastAccess field
		if (user.getFlgPwdBlocked() != null && user.getFlgPwdBlocked()){
			//if flgPwdBlocked is true the user cannot goes on
			logger.info("The pwd needs to activate!");
			toReturn = true;					
		}		
		logger.debug("OUT");
		return toReturn;
	}
}
