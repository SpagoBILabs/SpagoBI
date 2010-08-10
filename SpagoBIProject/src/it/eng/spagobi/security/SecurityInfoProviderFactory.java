/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/

package it.eng.spagobi.security;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;

import org.apache.log4j.Logger;

/**
 * @author Zerbetto
 */
public class SecurityInfoProviderFactory {
		
		static private Logger logger = Logger.getLogger(SecurityInfoProviderFactory.class);
		
		/**
		 * Reads the security provider class from the spagobi.xml file
		 * 
		 * @return the instance of ISecurityInfoProvider
		 */
		public static synchronized ISecurityInfoProvider getPortalSecurityProvider() throws Exception {
			logger.debug("IN");
			SourceBean configSingleton = ConfigSingleton.getInstance();
			SourceBean portalSecuritySB = (SourceBean) configSingleton.getAttribute("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS");
			logger.debug(" Portal security class configuration " + portalSecuritySB);
			String portalSecurityClassName = (String) portalSecuritySB.getAttribute("className");
			logger.debug(" Portal security class name: " + portalSecurityClassName);
			if (portalSecurityClassName == null || portalSecurityClassName.trim().equals("")) {
				logger.error(" Portal security class name not set!!!!");
				throw new Exception("Portal security class name not set");
			}
			portalSecurityClassName = portalSecurityClassName.trim();
			ISecurityInfoProvider portalSecurityProvider = null;
			try {
				portalSecurityProvider = (ISecurityInfoProvider)Class.forName(portalSecurityClassName).newInstance();
			} catch (Exception e) {
				logger.error(" Error while istantiating portal security class '" + portalSecurityClassName + "'.", e);
				throw e;
			}
			return portalSecurityProvider;
		}
	}

