/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.services.oauth2;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.security.DefaultCipher;
import it.eng.spagobi.services.common.AbstractSsoServiceInterface;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.utilities.exceptions.SpagoBIDefaultCipherException;

import java.io.IOException;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oauth2SsoService extends AbstractSsoServiceInterface implements SsoServiceInterface {

	private static final Logger logger = LoggerFactory.getLogger(Oauth2SsoService.class);
	
	private static DefaultCipher df = null;
	
	@Override
	public void validateTicket(String ticket, String userId) throws SecurityException {
		/*logger.debug("Start ticket validation");
		initCipher();
		if (df == null){
			logger.error("Fail ticket validation");
			throw new SpagoBIDefaultCipherException("Fail initialization Default Cipher");
		}
		String encryptedUserID = df.encrypt(userId);
		if (!encryptedUserID.equals(ticket)){
			logger.error("Ticket is not valid");
			throw new SecurityException("Fail ticket validation");
		}
		
		logger.debug("End ticket validation");*/
	} 
	 
	public String readTicket(HttpSession session) throws IOException {
		/*logger.debug("Start reading ticket");
		initCipher();
		if (df == null){
			logger.error("Fail ticket validation");
			throw new SpagoBIDefaultCipherException("Fail initialization Default Cipher");
		}
		
		String encryptedTicket = (String) session.getAttribute("access_token");
		String ticket = df.decrypt(encryptedTicket);
		
		logger.debug("End reading ticket");
		
		return ticket;*/
		return "NA";
	}

	public String readUserIdentifier(HttpServletRequest request) {
		HttpSession session = request.getSession();

		// This is for SpagoBI core: we consider the OAuth2 access token as user unique identifier
		String userId = (String) session.getAttribute("access_token");

		// In external engines we read userId from request
		if (userId == null)
			userId = request.getParameter(SsoServiceInterface.USER_ID);

		return userId;
	}

	public String readUserIdentifier(PortletSession session) {
		// TODO Auto-generated method stub
		return null;
	}


	public void initCipher() {
		/*if (df == null){
			String jndiBean = SingletonConfig.getInstance().getConfigValue("SPAGOBI.RESOURCE_ENCRIPTION_KEY");
			if (jndiBean != null){
				String key = SpagoBIUtilities.readJndiResource(jndiBean);
				if (key != null && key.length()>0){
					df = new DefaultCipher(key);
				}
			}
		}
		if (df == null){
			df = new DefaultCipher();
		}*/
	}
	
}
