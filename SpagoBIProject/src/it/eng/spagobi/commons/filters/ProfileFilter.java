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
package it.eng.spagobi.commons.filters;

import it.eng.spago.base.Constants;
import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.services.common.SsoServiceFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tenant.TenantManager;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * @author Zerbetto (davide.zerbetto@eng.it)
 * 
 *         This filter tries to build the user profile object, using the user
 *         identifier
 */

public class ProfileFilter implements Filter {

	private static transient Logger logger = Logger.getLogger(ProfileFilter.class);

	public void destroy() {
		// do nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//logger.debug("IN");
		try {
			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				HttpSession session = httpRequest.getSession();

				RequestContainer requestContainer = (RequestContainer) session.getAttribute(Constants.REQUEST_CONTAINER);
				if (requestContainer == null) {
					// RequestContainer does not exists yet (maybe it is the
					// first call to Spago)
					// initializing Spago objects (user profile object must
					// be put into PermanentContainer)
					requestContainer = new RequestContainer();
					SessionContainer sessionContainer = new SessionContainer(true);
					requestContainer.setSessionContainer(sessionContainer);
					session.setAttribute(Constants.REQUEST_CONTAINER, requestContainer);
				}
				SessionContainer sessionContainer = requestContainer.getSessionContainer();
				SessionContainer permanentSession = sessionContainer.getPermanentContainer();
				IEngUserProfile profile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
				if (profile == null) {
					logger.debug("User profile not found in session, creating a new one and putting in session....");
					// in case the profile does not exist, creates a new one
					String userId = findUserId(httpRequest);
					// in case the user is not specified, does nothing
					if (userId == null || userId.trim().equals("")) {
						logger.debug("User identifier not found.");
					}
					logger.debug("User id = " + userId);
					if (userId!=null) {
						profile = GeneralUtilities.createNewUserProfile(userId);
						permanentSession.setAttribute(IEngUserProfile.ENG_USER_PROFILE, profile);
					}
					
				} else {
					// in case the profile is different, creates a new one
					// and overwrites the existing
					/*
					 * if (!((UserProfile)
					 * profile).getUserUniqueIdentifier().toString
					 * ().equals(userId)) {logger.debug(
					 * "Different user profile found in session, creating a new one and replacing in session...."
					 * ); profile =
					 * GeneralUtilities.createNewUserProfile(userId);
					 * permanentSession
					 * .setAttribute(IEngUserProfile.ENG_USER_PROFILE, profile);
					 * } else { logger.debug("User profile object for user [" +
					 * userId + "] already existing in session, ok"); }
					 */
				}
				
				if (profile != null) {
					manageTenant(profile);
				}

			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			//logger.debug("OUT");
			try {
				chain.doFilter(request, response);
			} finally {
				// since TenantManager uses a ThreadLocal, we must clean  after request processed in each case
				TenantManager.unset();
			}

		}
	}

	private void manageTenant(IEngUserProfile profile) {
		UserProfile userProfile = (UserProfile) profile;
		// retrieving tenant id
		String tenantId = userProfile.getOrganization();
		// putting tenant id on thread local
		Tenant tenant = new Tenant(tenantId);
        TenantManager.setTenant(tenant);
	}

	public void init(FilterConfig config) throws ServletException {
		// do nothing
	}

	/**
	 * Finds the user identifier from http request or from SSO system (by the
	 * http request in input). Use the SsoServiceInterface for read the userId
	 * in all cases, if SSO is disabled use FakeSsoService. Check
	 * spagobi_sso.xml
	 * 
	 * @param httpRequest
	 *            The http request
	 * 
	 * @return the current user unique identified
	 * 
	 * @throws Exception
	 *             in case the SSO is enabled and the user identifier specified
	 *             on http request is different from the SSO detected one.
	 */

	private static String findUserId(HttpServletRequest request) throws Exception {
		logger.debug("IN");
		String userId = null;
		try {
			SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
			userId = userProxy.readUserIdentifier(request);
		} finally {
			logger.debug("OUT");
		}
		return userId;
	}

}
