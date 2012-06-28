/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/*
 * Created on 21-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.commons.services;

import it.eng.spago.base.Constants;
import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.AbstractHttpModule;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.services.common.SsoServiceFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.services.security.service.SecurityServiceSupplierFactory;
import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tenant.TenantManager;
import it.eng.spagobi.wapp.services.ChangeTheme;
import it.eng.spagobi.wapp.util.MenuUtilities;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.jdbc.JDBCAppender;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LoginModule extends AbstractHttpModule {

	static Logger logger = Logger.getLogger(LoginModule.class);

	private static final String PROP_NODE = "changepwd.";

	/**  The format date to manage the data validation. */
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	IEngUserProfile profile = null;
	EMFErrorHandler errorHandler = null;

	/**
	 * Service.
	 * 
	 * @param request the request
	 * @param response the response
	 * 
	 * @throws Exception the exception
	 * 
	 * @see it.eng.spago.dispatching.action.AbstractHttpAction#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");

		String theme_name=(String)request.getAttribute(ChangeTheme.THEME_NAME);
		logger.debug("theme selected: "+theme_name);

		String activeStr = SingletonConfig.getInstance().getConfigValue("SPAGOBI_SSO.ACTIVE");
		boolean activeSoo=false;
		if (activeStr != null && activeStr.equalsIgnoreCase("true")) {
			activeSoo=true;
		}
		RequestContainer reqCont = RequestContainer.getRequestContainer();
		SessionContainer sessCont = reqCont.getSessionContainer();
		SessionContainer permSess = sessCont.getPermanentContainer();

		HttpServletRequest servletRequest=getHttpRequest();
		HttpSession httpSession=servletRequest.getSession();

		// Set THEME
		if (theme_name!=null && theme_name.length()>0){
			permSess.setAttribute(SpagoBIConstants.THEME, theme_name);
		}



		// updates locale information on permanent container for Spago messages mechanism
		// search firstly if a default language is set on configuraiton file, else take browser from spago

		if(permSess.getAttribute(Constants.USER_LANGUAGE)== null || permSess.getAttribute(Constants.USER_COUNTRY) == null){
			logger.debug("getting locale...");
			Locale locale =GeneralUtilities.getStartingDefaultLocale();
			if(locale == null){
				locale = MessageBuilder.getBrowserLocaleFromSpago();
			}
			else{
				logger.debug("Locale "+locale.getLanguage()+" - "+locale.getCountry()+" taken as default from configuraiton file");
			}
			if (locale != null) {
				logger.debug("locale taken as default is "+locale.getLanguage()+" - "+locale.getCountry());
				permSess.setAttribute(Constants.USER_LANGUAGE, locale.getLanguage());
				permSess.setAttribute(Constants.USER_COUNTRY, locale.getCountry());
			}
		}
		else{
			logger.debug("locale already found in session");	
		}


		// Set BACK URL if present
		String backUrl=(String)request.getAttribute(SpagoBIConstants.BACK_URL);

		if (backUrl!=null && !backUrl.equalsIgnoreCase("")){
			//permSess.setAttribute(SpagoBIConstants.BACK_URL, backUrl);
			httpSession.setAttribute(SpagoBIConstants.BACK_URL, backUrl);		
		}

		errorHandler = getErrorHandler();

		UserProfile previousProfile = (UserProfile) permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		String userId=null;
		if (!activeSoo) {
			userId = (String)request.getAttribute("userID");
			logger.debug("userID="+userId);
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
					String url = GeneralUtilities.getSpagoBiHost() + servletRequest.getContextPath();
					response.setAttribute("start_url", url);
					response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "login");
					logger.debug("OUT");
					return;
				}
				//logger.error("User identifier not found. Cannot build user profile object");
				//throw new SecurityException("User identifier not found.");
			}			
		} else {

			SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
			userId = userProxy.readUserIdentifier(servletRequest);
			logger.debug("OUT,userId:"+userId);
			// if we are in SSO and user has a previous profile keep it!
			if (previousProfile != null && previousProfile.getUserId().equals(userId)) {
				if (previousProfile != null) {
					profile = previousProfile;
					// user is authenticated, nothing to do
					logger.debug("User is authenticated");
					// fill response
					MenuUtilities.getMenuItems(request, response, profile);
					// set publisher name
					response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "userhome");
					return;
				} 
			}	

		}
		
		
		ISecurityServiceSupplier supplier=SecurityServiceSupplierFactory.createISecurityServiceSupplier();
		// If SSO is not active, check username and password, i.e. performs the authentication;
		// instead, if SSO is active, the authentication mechanism is provided by the SSO itself, so SpagoBI does not make 
		// any authentication, just creates the user profile object and puts it into Spago permanent container
		if (!activeSoo) {
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
				throw new SecurityException("Reading user information... ERROR",e);
			}
			//getting security type: if it's internal (SpagoBI) active pwd management and checks
			boolean isInternalSecurity = ("true".equalsIgnoreCase((String)request.getAttribute("isInternalSecurity")))?true:false;
			logger.debug("isInternalSecurity: " + isInternalSecurity);
			if (isInternalSecurity)  {			 
				//gets the user bo
				ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
				SbiUser user = userDao.loadSbiUserByUserId(userId);

				//check user's role: if he's admin it doesn't apply checks on password
				String strAdminPatter =  SingletonConfig.getInstance().getConfigValue("SPAGOBI.SECURITY.ROLE-TYPE-PATTERNS.ADMIN-PATTERN");
				int sbiUserId=-1;
				if (user!=null)sbiUserId=user.getId();
				List lstRoles = userDao.loadSbiUserRolesById(sbiUserId);
				boolean isAdminUser = false;

				for (int i=0; i<lstRoles.size(); i++){
					SbiExtRoles tmpRole = (SbiExtRoles)lstRoles.get(i);
					Role role = DAOFactory.getRoleDAO().loadByID(tmpRole.getExtRoleId());
					if (role.getName().equals(strAdminPatter)){
						isAdminUser = true;
						logger.debug("User is administrator. Checks on the password are not applied !");
						break;
					}
				}

				if (!isAdminUser){
					//check validation of the password
					logger.debug("Validation password starting...");

					boolean goToChangePwd = checkPwd(user);
					if (goToChangePwd){
						response.setAttribute("user_id", user.getUserId());
						String url = GeneralUtilities.getSpagoBiHost() + servletRequest.getContextPath();
						response.setAttribute("start_url", url);
						response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ChangePwdPublisher"); 
						return;
					}

					logger.info("The pwd is active!");
					//update lastAccessDate on db with current date
					try{
						if (user!=null){
							user.setDtLastAccess(new Date());
							userDao.updateSbiUser(user, user.getId());
						}
					}catch(Exception e){
						logger.error("Error while update user's dtLastAccess: " + e);
					}
				}
			}
		}

		try {
			profile=UserUtilities.getUserProfile(userId);
			if (profile == null){		            	
				logger.error("user not created");
				EMFUserError emfu = new EMFUserError(EMFErrorSeverity.ERROR, 501);
				errorHandler.addError(emfu); 		    	
				return;
			}

			Boolean userHasChanged = Boolean.TRUE;
			// try to find if the user has changed: if so, the session parameters must be reset, see also homebis.jsp
			// check previous userId with current one: if they are equals, user has not changed
			if (previousProfile != null && previousProfile.getUserId().equals(((UserProfile)profile).getUserId())) {
				userHasChanged = Boolean.FALSE;
			}
			response.setAttribute("USER_HAS_CHANGED", userHasChanged);
			// put user profile into session
			permSess.setAttribute(IEngUserProfile.ENG_USER_PROFILE, profile);


		} catch (Exception e) {
			logger.error("Reading user information... ERROR");
			throw new SecurityException("Reading user information... ERROR",e);
		}

		//String username = (String) profile.getUserUniqueIdentifier();
		String username = (String) ((UserProfile)profile).getUserId();
		
		// putting tenant id on thread local
		Tenant tenant = new Tenant(((UserProfile)profile).getOrganization());
        TenantManager.setTenant(tenant);
        
        try {
		
			if (!UserUtilities.userFunctionalityRootExists(username)) {
				logger.debug("funcitonality root not yet exists for "+username);	
				//UserUtilities.createUserFunctionalityRoot(profile);
			}
			else{
				logger.debug("funcitonality root already exists for "+username);					
			}
	
			//Start writing log in the DB
			Session aSession =null;
			try {
				aSession = HibernateUtil.currentSession();
				//Connection jdbcConnection = aSession.connection();
				Connection jdbcConnection = HibernateUtil.getConnection(aSession);
				AuditLogUtilities.updateAudit(jdbcConnection, profile, "activity.Login", null);
			} catch (HibernateException he) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
			} finally {
				if (aSession!=null){
					if (aSession.isOpen()) aSession.close();
				}
			}
			//End writing log in the DB
	
			MenuUtilities.getMenuItems(request, response, profile);
	
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "userhome");
		
        } finally {
        	// since TenantManager uses a ThreadLocal, we must clean  after request processed in each case
        	TenantManager.unset();
        }

		logger.debug("OUT");		
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
