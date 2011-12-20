/**

Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.spagobi.security.init;

import org.apache.log4j.Logger;

import it.eng.spago.base.Constants;
import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spago.tracing.TracerSingleton;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.security.RoleSynchronizer;

public class SecurityInitializer implements InitializerIFace {
	
	static private Logger logger = Logger.getLogger(SecurityInitializer.class);
	private SourceBean _config = null;
	
	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#getConfig()
	 */
	public SourceBean getConfig() {
		return _config;
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
		logger.debug("IN");

		logger.debug("SecurityInitializer::init: roles synchronization ended.");
		_config = config;
		SingletonConfig configSingleton = SingletonConfig.getInstance();
		String portalSecurityInitClassName = (configSingleton.getConfigValue("SPAGOBI.SECURITY.PORTAL-SECURITY-INIT-CLASS.className"));
		logger.debug("SecurityInitializer::init: Portal security initialization class name: '" + portalSecurityInitClassName + "'");
		if (portalSecurityInitClassName == null || portalSecurityInitClassName.trim().equals("")) return;
		portalSecurityInitClassName = portalSecurityInitClassName.trim();
		InitializerIFace portalSecurityInit = null;
		try {
			portalSecurityInit = (InitializerIFace)Class.forName(portalSecurityInitClassName).newInstance();
		} catch (Exception e) {
			logger.error("SecurityInitializer::init: error while instantiating portal security initialization class name: '" + portalSecurityInitClassName + "'", e);
			return;
		}
		logger.debug("SecurityInitializer::init: invoking init method of the portal security initialization class name: '" + portalSecurityInitClassName + "'");
		portalSecurityInit.init(config);
		/*roles syncronizing after tables initialization*/
		logger.debug("SecurityInitializer::init: starting synchronizing roles...");
		RoleSynchronizer synch = new RoleSynchronizer();
		synch.synchronize();
		
		logger.debug("OUT");
	}

}
