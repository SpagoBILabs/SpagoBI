/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.exoaddins;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.security.ISecurityInfoProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

public class Utilities {
	static private Logger logger = Logger.getLogger(Utilities.class);
	
	public static List getExoUserFiltered(List allUser) {
		logger.debug("IN");
		List filteredUser = new ArrayList();
		try {
			// recover spago configuration
			ConfigSingleton conf = ConfigSingleton.getInstance();
			if(conf==null) throw new Exception("Configuration not Found");
			// create instance of the portal security class
			SourceBean secClassSB = (SourceBean)conf.getAttribute("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS");
			if(secClassSB==null) throw new Exception("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS tag not Found");
			SourceBean secClassConfigSB = (SourceBean)secClassSB.getAttribute("CONFIG");
			if(secClassConfigSB==null) throw new Exception("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS.CONFIG tag not Found");
        	String portalSecurityProviderClass = (String) secClassSB.getAttribute("className");
        	if(portalSecurityProviderClass==null) throw new Exception("Attribute className of the SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS tag not Found");
        	portalSecurityProviderClass = portalSecurityProviderClass.trim();
        	Class secProvClass = Class.forName(portalSecurityProviderClass);
        	ISecurityInfoProvider portalSecurityProvider = (ISecurityInfoProvider)secProvClass.newInstance();
            // create the pattern for the role name filter
			SourceBean secFilterSB = (SourceBean)conf.getAttribute("SPAGOBI.SECURITY.ROLE-NAME-PATTERN-FILTER");
			if(secFilterSB==null) throw new Exception("SPAGOBI.SECURITY.ROLE-NAME-PATTERN-FILTER tag not Found");
			String rolePatternFilter = secFilterSB.getCharacters();
	        if(rolePatternFilter==null) throw new Exception("Role filter regular expression not found");
			Pattern pattern = Pattern.compile(rolePatternFilter);
	        Matcher matcher = null;
			// for each user checks if at least one of his roles is suitable for the filter
			Iterator iterUser = allUser.iterator();
			Iterator iterRoles = null;
			while(iterUser.hasNext()) {
				User user = (User)iterUser.next();
				String userName = user.getUserName();
				boolean allowed  = false;
				List roles = getUserRoles(userName, secClassConfigSB);
				iterRoles = roles.iterator();
				while(iterRoles.hasNext()) {
					Role role = (Role)iterRoles.next();
					String rolename = role.getName();
					matcher = pattern.matcher(rolename);
					if(matcher.find()){
						allowed  = true;	
						break;
					}
				}
				
				if(allowed) {
					filteredUser.add(user);
				}
			}
		} catch (Exception e) {
			logger.error("Error while filter exo user list ", e);
			filteredUser = new ArrayList();
		}
		logger.debug("OUT");
		return filteredUser;
		
	}
	
	/**
	 * Get the list of the user roles. If the user doesn't exist the roles list is empty
	 * @param user Username
	 * @param config The SourceBean configuration
	 * @return List of user roles (list of it.eng.spagobi.bo.Role)
	 */
	private static List getUserRoles(String user, SourceBean config) {
		logger.debug("IN");
		logger.debug(" Config SourceBean in input: " + config);
		List roles = new ArrayList();
		String paramCont = "NAME_PORTAL_APPLICATION";
		logger.debug(" Use param " + paramCont);
		SourceBean paramContSB = (SourceBean)config.getAttribute(paramCont);
		logger.debug(" Param context name Source Bean retrived: " + paramContSB);
		String nameCont = (String)paramContSB.getCharacters();
		logger.debug(" Use context name " + nameCont);
		RootContainer rootCont = RootContainer.getInstance();
		logger.debug(" Root container retrived: " + rootCont);
		PortalContainer container = rootCont.getPortalContainer(nameCont);
		logger.debug(" Portal container retrived: " + container);
		OrganizationService service = 
			(OrganizationService)container.getComponentInstanceOfType(OrganizationService.class);
		logger.debug(" Organization service retrived: " + service);
		try {
			Collection groups = service.getGroupHandler().findGroupsOfUser(user);
			Iterator iterGroups = groups.iterator();
			while(iterGroups.hasNext()) {
				Group group = (Group)iterGroups.next();
				add(group, service, roles);
				//String groupid = group.getId();
				//roles.add(groupid);
			}
		} catch (Exception e) {
			logger.error("Error retrieving groups of user "+user, e);
		}
		logger.debug(" End method return roles: " + roles);
		logger.debug("OUT");
		return roles;
	}

	/**
	 * Add the current group(role) and it's child to the roles list
	 * @param group Group of the portal
	 * @param orgService OrganizationService of the portal
	 * @param roles List of roles (list of it it.eng.spagobi.bo.Role)
	 */
	private static void add(Group group, OrganizationService orgService, List roles){
		Role role = new Role(group.getId(), group.getDescription());
    	roles.add(role);
    	try{
    		Collection children = orgService.getGroupHandler().findGroups(group);
    		if ((children == null) || (children.size() == 0)){
    			// End recursion
    			return;
    		}else{
    			Iterator it = children.iterator();
    			while (it.hasNext()){
    				add((Group)it.next(), orgService, roles);
    			}
    		}
    	}catch(Exception e){
    		logger.error(" Exception when retrieving child of group "+group.getId(), e);
    	}
	}
}
