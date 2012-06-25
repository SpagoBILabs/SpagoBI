/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.exoaddins.modules;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.exoaddins.Utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;


public class ExoProfileAttributeManagerModule extends AbstractModule {
	static private Logger logger = Logger.getLogger(ExoProfileAttributeManagerModule.class);
	
	public void init(SourceBean config) {	}

	public void service(SourceBean request, SourceBean response) throws Exception { 
		logger.debug("IN");
		String message = (String) request.getAttribute("MESSAGE");
		logger.debug("start service with message =" +message);
		EMFErrorHandler errorHandler = getErrorHandler();
		try{
			if(message==null) {
				// 
			} else if (message.trim().equalsIgnoreCase("DETAIL_PROFILE_USER")) {
				String username = (String) request.getAttribute("UserName");
				getUserProfile(username, response);
			} else if(message.trim().equalsIgnoreCase("SYNCH_ATTRIBUTES")) {
				synchAttributes(response);
			} else if(message.trim().equalsIgnoreCase("SAVE_PROFILE")) {
				String username = (String) request.getAttribute("UserName");
				saveUserProfile(username, request, response);
			}
		} catch (EMFUserError eex) {
			errorHandler.addError(eex);
			logger.debug("OUT");
			return;
		} catch (Exception ex) {
			EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, ex);
			errorHandler.addError(internalError);
			logger.debug("OUT");
			return;
		}
		logger.debug("OUT");
	}
	
	
	
	private void saveUserProfile(String username, SourceBean request, SourceBean response) {
		try{
			logger.debug("IN");
			PortalContainer container = PortalContainer.getInstance();	
			OrganizationService service = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
			UserProfileHandler userProfileHandler = service.getUserProfileHandler();
			UserProfile userProfile = null;
			Map exoprofileattrs = new HashMap();
			userProfile = userProfileHandler.findUserProfileByName(username);
			exoprofileattrs = userProfile.getUserInfoMap();
			SourceBean profileAttributesSB = (SourceBean)ConfigSingleton.getInstance().getAttribute("EXO_PORTAL_SECURITY.PROFILE_ATTRIBUTES");
		
			if(profileAttributesSB!=null) {
				List attrs = profileAttributesSB.getAttributeAsList("ATTRIBUTE");
				if(attrs != null && attrs.size() > 0) {
					Iterator iterAttrs = attrs.iterator();
					SourceBean attrSB = null;
					String nameattr = null;
					String source = null;
					String exoName = null;
					while(iterAttrs.hasNext()) {
						attrSB = (SourceBean) iterAttrs.next();
						source = (String) attrSB.getAttribute("source");
						nameattr = (String) attrSB.getAttribute("name");
						if((source!=null) && source.equalsIgnoreCase("exo")) {
							exoName = (String) attrSB.getAttribute("exoname");
							String reqKey = (String) request.getAttribute("keys");
							String value = null;
							if (reqKey.equalsIgnoreCase(exoName)){
								value = (String) request.getAttribute("attributes");
								String valueFormatted =  "{,{"; 
								String[] arValues = value.split(",");
								for (int i=0; i < arValues.length; i++){
									valueFormatted += arValues[i];
									if (i<arValues.length-1)
										valueFormatted += ",";
								}
								valueFormatted += "}}";
								value = valueFormatted;
							}
								//value = (String) request.getAttribute(exoName);
							if(value!=null) {
								exoprofileattrs.put(exoName, value);
							}
						}
					}
				}
			}
			userProfile.setUserInfoMap(exoprofileattrs);
			userProfileHandler.saveUserProfile(userProfile, false);
			response.setAttribute("UserName", username);
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ExoProfileAttributeManagerrDetailProfileLoop");
		} catch (Exception e) {
			logger.error("Error while saving profile for user " + username, e);
		}
		logger.debug("OUT");
	}
	
	
	
	
	private void synchAttributes(SourceBean response) throws Exception {
		logger.debug("IN");
		try{
			ConfigSingleton conf = ConfigSingleton.getInstance();
			if(conf==null) throw new Exception("Configuration not found");
			SourceBean profileAttributesSB = (SourceBean)conf.getAttribute("EXO_PORTAL_SECURITY.PROFILE_ATTRIBUTES");
			if(profileAttributesSB==null) throw new Exception("EXO_PORTAL_SECURITY.PROFILE_ATTRIBUTES tag not found");
			List attrs = profileAttributesSB.getAttributeAsList("ATTRIBUTE");		
			PortalContainer container = PortalContainer.getInstance();	
			if(container==null) throw new Exception("Portal container not retrived");
			OrganizationService service = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
			if(service==null) throw new Exception("Organization service not retrived");
			UserHandler userHandler = service.getUserHandler();
			if(userHandler==null) throw new Exception("User Handler not retrived");
			UserProfileHandler userProfileHandler = service.getUserProfileHandler();
			if(userProfileHandler==null) throw new Exception("User Profile Handler not retrived");
			GroupHandler groupHandler =  service.getGroupHandler();
			if(groupHandler==null) throw new Exception("Group Handler not retrived");
			PageList pagelist = userHandler.getUserPageList(10);
			List allUser = pagelist.getAll();
			allUser = Utilities.getExoUserFiltered(allUser);
			Iterator iterUser = allUser.iterator();
			while(iterUser.hasNext()) {
				User user = (User)iterUser.next();
				String userName = user.getUserName();
				UserProfile userProfile = userProfileHandler.findUserProfileByName(userName);
				Map userAttrMap = userProfile.getUserInfoMap();
				Iterator iterAttrs = attrs.iterator();
				while(iterAttrs.hasNext()) {
					SourceBean attrSB = (SourceBean) iterAttrs.next();
					String source = (String) attrSB.getAttribute("source");
					String nameattr = (String) attrSB.getAttribute("name");
					String defaultVal = (String) attrSB.getAttribute("default");
					if((source!=null) && source.equalsIgnoreCase("exo")) {
						String exoName = (String) attrSB.getAttribute("exoname");
						if(!userAttrMap.containsKey(exoName)) {
							userAttrMap.put(exoName, defaultVal);
						}
					}
				}
				userProfile.setUserInfoMap(userAttrMap);
				userProfileHandler.saveUserProfile(userProfile, false);
			}
		} catch (Exception e) {
			logger.error("Error while synch exo user profile attributes ", e);
		}
		response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ExoProfileAttributeManagerHomeLoop");
		logger.debug("OUT");
	}
	
	
	
	private void getUserProfile(String username, SourceBean response) throws Exception {
		logger.debug("IN");
		Map attributes = new HashMap();
		Map attributeKeys = new HashMap();
		User user = null;
		try{
			PortalContainer container = PortalContainer.getInstance();	
			OrganizationService service = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
			UserProfileHandler userProfileHandler = service.getUserProfileHandler();
			UserHandler userHandler = service.getUserHandler();
			UserProfile userProfile = null;
			Map exoprofileattrs = new HashMap();
			userProfile = userProfileHandler.findUserProfileByName(username);
		    // get the name of the user and use it to recover the user bean
			String userName = userProfile.getUserName();
			user = userHandler.findUserByName(userName);
			// recover profile attributes
			exoprofileattrs = userProfile.getUserInfoMap();
			SourceBean profileAttributesSB = (SourceBean)ConfigSingleton.getInstance().getAttribute("EXO_PORTAL_SECURITY.PROFILE_ATTRIBUTES");
			if(profileAttributesSB!=null) {
				List attrs = profileAttributesSB.getAttributeAsList("ATTRIBUTE");
				if(attrs != null && attrs.size() > 0) {
					Iterator iterAttrs = attrs.iterator();
					SourceBean attrSB = null;
					String nameattr = null;
					String source = null;
					String exoName = null;
					while(iterAttrs.hasNext()) {
						attrSB = (SourceBean) iterAttrs.next();
						source = (String) attrSB.getAttribute("source");
						nameattr = (String) attrSB.getAttribute("name");
						if((source!=null) && source.equalsIgnoreCase("exo")) {
							exoName = (String) attrSB.getAttribute("exoname");
							String exoValue = (String)exoprofileattrs.get(exoName);
							if(exoValue!=null) {
								attributes.put(nameattr, exoValue);
								attributeKeys.put(nameattr, exoName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting user profile ", e);
			attributes = new HashMap();
			attributeKeys = new HashMap();
		}
		// load into response user information
		response.setAttribute("UserName", username);
		String firstName = "";
		String lastName = "";
		String email = "";
		firstName = user.getFirstName();
		if(firstName == null) firstName = "";
		lastName = user.getLastName();
		if(lastName == null) lastName = "";
		email = user.getEmail();
		if(email == null) email = "";
		response.setAttribute("FirstName", firstName);
		response.setAttribute("LastName", lastName);
		response.setAttribute("Email", email);
		// load into response profile attributes
		response.setAttribute("attributes", attributes);
		response.setAttribute("attributeKeys", attributeKeys);
		// load into response publisher name
		response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ExoProfileAttributeManagerDetailProfile");
		logger.debug("OUT");
	}


}	
	
	
