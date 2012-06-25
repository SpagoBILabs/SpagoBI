/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;

/**
 * Implementation of the IEngUserProfile interface Factory. Defines methods to
 * get a IEngUserProfile starting from the exo user information
 */
public class ExoUserProfileImpl implements ISecurityServiceSupplier {

    public SpagoBIUserProfile checkAuthentication(String userId, String psw) {
	logger.warn("checkAuthentication NOT implemented");
	return null;
    }

    static private Logger logger = Logger.getLogger(ExoUserProfileImpl.class);

    /**
     * Return an SpagoBIUserProfile
     * 
     * @param principal
     *                Principal of the current user
     * @return The User Profile Interface implementation object
     */
    public SpagoBIUserProfile createUserProfile(String userId) {
	return createSpagoBIUserProfile(userId);
    }

    public boolean checkAuthorization(String userId, String function) {
	logger.warn("checkAuthorization NOT implemented");
	return false;
    }

    private SpagoBIUserProfile createSpagoBIUserProfile(String userId) {
	logger.debug("IN. userId=" + userId);
	SpagoBIUserProfile profile = new SpagoBIUserProfile();
	try {
		profile.setUniqueIdentifier(userId);
	    profile.setUserId(userId);
	    ArrayList roles = new ArrayList();
	    HashMap userAttributes = new HashMap();
	    PortalContainer container = PortalContainer.getInstance();

	    if (container == null) {
		ConfigSingleton config = ConfigSingleton.getInstance();
		SourceBean securityconfSB = (SourceBean) config
			.getAttribute("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS.CONFIG");
		String paramCont = "NAME_PORTAL_APPLICATION";
		SecurityProviderUtilities.debug(this.getClass(), "<init(Principal)>", " Use param " + paramCont);
		SourceBean paramContSB = (SourceBean) securityconfSB.getAttribute(paramCont);
		SecurityProviderUtilities.debug(this.getClass(), "<init(Principal)>",
			" Param context name Source Bean " + "retrived: " + paramContSB);
		String nameCont = (String) paramContSB.getCharacters();
		SecurityProviderUtilities.debug(this.getClass(), "<init(Principal)>", " Use context name " + nameCont);
		RootContainer rootCont = RootContainer.getInstance();
		SecurityProviderUtilities.debug(this.getClass(), "<init(Principal)>", " Root container retrived: "
			+ rootCont);
		container = rootCont.getPortalContainer(nameCont);
	    }

	    OrganizationService service = (OrganizationService) container
		    .getComponentInstanceOfType(OrganizationService.class);

	    // load user roles
	    Collection tmpRoles = service.getGroupHandler().findGroupsOfUser(userId);
	    GroupHandler groupHandler = service.getGroupHandler();
	    SecurityProviderUtilities.debug(this.getClass(), "init", "Group Handler retrived " + groupHandler);
	    MembershipHandler memberHandler = service.getMembershipHandler();
	    SecurityProviderUtilities.debug(this.getClass(), "init", "Membership Handler retrived " + memberHandler);
	    Group group = null;
	    Matcher matcher = null;
	    for (Iterator it = tmpRoles.iterator(); it.hasNext();) {
		group = (Group) it.next();
		String groupID = group.getId();
		Pattern pattern = SecurityProviderUtilities.getFilterPattern();
		matcher = pattern.matcher(groupID);
		if (!matcher.find()) {
		    continue;
		}
		roles.add(group.getId());
		logger.debug("Roles load into SpagoBI profile: " + group.getId());
	    }

	    // start load profile attributes
	    userAttributes = SecurityProviderUtilities.getUserProfileAttributes(userId, service);
	    logger.debug("Attributes load into SpagoBI profile: " + userAttributes);
	    // end load profile attributes
	    
	    profile.setAttributes(userAttributes);
	    
	    if (roles.size()==0){
		   logger.warn("THE LIST OF ROLES IS EMPTY, CHECK THE PROFILING CONFIGURATION...");
	    }else {
        	    String[] roleStr = new String[roles.size()];
        	    for (int i = 0; i < roles.size(); i++) {
        		roleStr[i] = (String) roles.get(i);
        	    }
        	    profile.setRoles(roleStr);
        	  
	    }
	} catch (Exception e) {
	    logger.error("Exception", e);
	}finally{
	    logger.debug("OUT");
	}
	return profile;
    }

	public SpagoBIUserProfile checkAuthenticationWithToken(String userId,
			String token) {
		logger.warn("checkAuthentication NOT implemented");
		return null;
	}


	
}
