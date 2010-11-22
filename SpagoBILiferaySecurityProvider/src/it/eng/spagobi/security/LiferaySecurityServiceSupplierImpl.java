/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.security;


import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.liferay.client.soap.portal.model.RoleSoap;
import com.liferay.client.soap.portal.model.UserSoap;
import com.liferay.client.soap.portal.service.http.RoleServiceSoap;
import com.liferay.client.soap.portal.service.http.RoleServiceSoapServiceLocator;
import com.liferay.client.soap.portal.service.http.UserServiceSoap;
import com.liferay.client.soap.portal.service.http.UserServiceSoapServiceLocator;

/**
 * 
 * @author Angelo Bernabei (angelo.bernabei@eng.it)
 */
public class LiferaySecurityServiceSupplierImpl implements ISecurityServiceSupplier {


	static private Logger logger = Logger.getLogger(LiferaySecurityServiceSupplierImpl.class);

	/**
	 * Return an SpagoBIUserProfile implementation starting from the id of the
	 * user.
	 * 
	 * @param userId
	 *            the user id
	 * 
	 * @return The User Profile Interface implementation object
	 */

	public SpagoBIUserProfile createUserProfile(String userId) {
		logger.debug("IN,userId="+userId);
		SpagoBIUserProfile profile = new SpagoBIUserProfile();
		profile.setUniqueIdentifier(userId);
		profile.setUserId(userId);
		

		try {
		  
		  UserServiceSoapServiceLocator uService = new UserServiceSoapServiceLocator();
		  UserServiceSoap userService = uService.getPortal_UserService(_getURL("Portal_UserService"));
		  UserSoap user = userService.getUserById(Integer.parseInt(userId));   

			if (user != null) {
				// user attributes
				HashMap<String, String> userAttributes = new HashMap<String, String>();

				userAttributes.put("USER_ID", String.valueOf(user.getUserId()));
				userAttributes.put("NAME", user.getFirstName());
				userAttributes.put("SURNAME", user.getLastName());
				userAttributes.put("E_MAIL", user.getEmailAddress());

				//set up the userID with email - adress
				profile.setUserName(user.getEmailAddress());
				profile.setUserId(user.getEmailAddress());
				
				logger.debug("user.getUserId()="+ user.getUserId());
				logger.debug("user.getScreenName()="+ user.getScreenName());
				logger.debug( "user.getFirstName()="+ user.getFirstName());
				logger.debug( "user.getLastName()="+ user.getLastName());

				profile.setAttributes(userAttributes);

				// user roles
				RoleServiceSoapServiceLocator rService = new RoleServiceSoapServiceLocator();
				RoleServiceSoap roleService = rService.getPortal_RoleService(_getURL("Portal_RoleService"));
				RoleSoap[] roles = roleService.getUserRoles(Integer.parseInt(userId));
				String[] roleNames = new String[roles.length];
				if (roles != null) {
					int i = 0;
					while (i<roles.length) {
					    RoleSoap role=roles[i];
						logger.debug("ruolo.getName()="+ role.getName());
						logger.debug("ruolo.getDescription()="+ role.getDescription());
						logger.debug("ruolo.getRoleId()=" + role.getRoleId());
						roleNames[i++] = role.getName();
					}
				}
				else {
					logger.warn("THE LIST OF ROLES IS EMPTY, CHECK THE PROFILING CONFIGURATION...");
				} 
				profile.setRoles(roleNames);
			}

		} 

		catch (RemoteException e) {
    logger.error("PortalException", e);
    } 
		catch (ServiceException e) {
    logger.error("PortalException", e);
    } 
		catch (MalformedURLException e) {
    logger.error("PortalException", e);
    }finally{
    	logger.debug("OUT");
    }
		
		return profile;
	}

	public SpagoBIUserProfile checkAuthentication(String userId, String psw) {
		logger.error("checkAuthentication NOT implemented");
		return null;
	}

	public SpagoBIUserProfile checkAuthenticationWithToken(String userId,
			String token) {
		logger.error("checkAuthenticationWithToken NOT implemented");
		return null;
	}

	public boolean checkAuthorization(String userId, String function) {
		logger.error("checkAuthorization NOT implemented");
		return false;
	}

	/**
	 * 
	 * @param serviceName
	 * @return
	 * @throws MalformedURLException
	 */
	private URL _getURL(String serviceName) throws MalformedURLException {

		SourceBean wsSB = (SourceBean) ConfigSingleton.getInstance().getAttribute("SPAGOBI.SECURITY.WS_SERVICE");

		String adminUser=(String)wsSB.getAttribute("username");
		String psw=(String)wsSB.getAttribute("password");
		String url=(String)wsSB.getAttribute("url");

	    url = "http://" + adminUser + ":" + psw + "@"+url + serviceName;
	    logger.debug("URL="+url);
	    return new URL(url);
	}
	


}
