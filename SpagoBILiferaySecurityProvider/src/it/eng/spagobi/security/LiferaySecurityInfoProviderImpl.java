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
package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.bo.Role;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.UserServiceUtil;

/**
 * 
 * @author Angelo Bernabei (angelo.bernabei@eng.it)
 * @author Sven Werlen (sven.werlen@savoirfairelinux.com)
 */
public class LiferaySecurityInfoProviderImpl implements ISecurityInfoProvider {

	static private Logger logger = Logger
			.getLogger(LiferaySecurityInfoProviderImpl.class);

	/**
	 * Get all the portal roles
	 * 
	 * @return List of the portal roles (list of it.eng.spagobi.bo.Role)
	 */
	public List getRoles() {
		logger.debug("IN");
		List groups = new ArrayList();
		SourceBean rolesSB = (SourceBean) ConfigSingleton.getInstance()
				.getAttribute("SPAGOBI_LIFERAY.SECURITY.ROLES");
		List groupsSB = rolesSB.getAttributeAsList("ROLE");

		if (groupsSB != null && groupsSB.size() > 0) {
			Iterator iterAttrs = groupsSB.iterator();
			SourceBean roleSB = null;
			String nameRoles = null;
			String deswcRoles = null;
			while (iterAttrs.hasNext()) {
				roleSB = (SourceBean) iterAttrs.next();
				if (roleSB == null)
					continue;
				nameRoles = (String) roleSB.getAttribute("name");
				deswcRoles = (String) roleSB.getAttribute("desc");
				if (nameRoles == null) {
					logger.error("Error while reading config file.");
				}
				groups.add(new Role(nameRoles, deswcRoles));
			}
		}
		logger.debug("OUT");
		return groups;
	}

	/**
	 * Get the list of the user roles. If the user doesn't exist the roles list
	 * is empty
	 * 
	 * @param user
	 *            Username
	 * @param passwd
	 *            Password of the user
	 * @return List of user roles
	 */
	public List getUserRoles(String userId, SourceBean passwd) {
		logger.debug("IN");
		ArrayList roles = new ArrayList();
		try {
			logger.info("UserID=" + userId);
			User user = UserServiceUtil.getUserById(Integer.parseInt(userId));
			if (user != null) {
				List ruoli = RoleServiceUtil.getUserRoles(user.getUserId());
				if (ruoli != null) {
					Iterator iter = ruoli.iterator();
					while (iter.hasNext()) {
						com.liferay.portal.model.Role ruolo = (com.liferay.portal.model.Role) iter
								.next();
						logger.debug("ruolo.getName()=" + ruolo.getName());
						roles.add(ruolo.getName());
					}
				}
			}

		} catch (SystemException e) {
			logger.error("SystemException", e);
		} catch (PortalException e) {
			logger.error("PortalException", e);
		}
		logger.debug("OUT");
		return roles;
	}

	public List getAllProfileAttributesNames() {

		logger.debug("IN");

		List attributes = new ArrayList();

		SourceBean attrSB = (SourceBean) ConfigSingleton.getInstance()
				.getAttribute("SPAGOBI_LIFERAY.SECURITY.PROFILE_ATTRIBUTES");
		List attrsSB = attrSB.getAttributeAsList("ATTRIBUTE");

		if (attrsSB != null && attrsSB.size() > 0) {
			Iterator iterAttrs = attrsSB.iterator();
			SourceBean attrSBTmp = null;
			String name = null;
			while (iterAttrs.hasNext()) {
				attrSBTmp = (SourceBean) iterAttrs.next();
				if (attrSBTmp == null)
					continue;
				name = (String) attrSBTmp.getAttribute("name");
				if (name == null) {
					logger.error("Error while reading config file.");
				}
				attributes.add(name);
			}
		}
		logger.debug("OUT");
		return attributes;
	}

	/**
	 * Authenticate a user
	 * 
	 * @param userName
	 *            the username
	 * @param password
	 *            bytes of the password, certificate, ...
	 * @return true if the user is autheticated false otherwise
	 */
	public boolean authenticateUser(String userName, byte[] password) {
		// NEVER CALLED BECAUSE AUTHENTICATION IS DONE BY THE PORTAL
		logger.warn("NOT IMPLEMENTED");
		return false;
	}

}
