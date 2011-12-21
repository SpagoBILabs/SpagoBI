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
package it.eng.spagobi.security.init;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;

public class ExoPortalSecurityProviderInit implements InitializerIFace {
	
	private SourceBean _config = null;
	private String configFileName = "it/eng/spagobi/security/conf/portalsecurity.cfg.xml";
	
	public SourceBean getConfig() {
		return _config;
	}

	public void init(SourceBean config) {
		SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
		        "start method with configuration:\n" + config);
		_config = config;
		SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
                "Security configuration file: " + configFileName);
		SourceBean configSingleton = (SourceBean)ConfigSingleton.getInstance();
		SourceBean exoPortalSecuritySB = (SourceBean) configSingleton.getAttribute("EXO_PORTAL_SECURITY");
		SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
                "EXO_PORTAL_SECURITY attribute in ConfigSingleton:\n" + exoPortalSecuritySB);
		if (exoPortalSecuritySB == null) {
			SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
	                "EXO_PORTAL_SECURITY attribute is not present in ConfigSingleton: putting it into ConfigSingleton");
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
			try {
				if (is == null) {
					SpagoBITracer.critical(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
			                "Security configuration file '" + configFileName + "' not found!!!");
				} else {
					InputSource stream = new InputSource(is);
					SourceBean securityConfig;
					try {
						securityConfig = SourceBean.fromXMLStream(stream);
						configSingleton.setAttribute(securityConfig);
					} catch (SourceBeanException e) {
						SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
					            "Error while loading security configuration file to ConfigSingleton", e);
					}
				}
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
					            "Error while closing input stream", e);
					}
			}
		} else {
			SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
            "EXO_PORTAL_SECURITY attribute is already present in ConfigSingleton:\n" + exoPortalSecuritySB.toXML());
		}
		SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, this.getClass().getName(),"init()", 
		        "end method.");
	}

}
