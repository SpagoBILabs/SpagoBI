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
package it.eng.spagobi.services.common;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

/**
 * Class that read engine-config.xml file
 */
public class EnginConf {
	private SourceBean config = null;
	
	private String resourcePath = null;
	private String spagoBiServerUrl = null;
	private String spagoBiSsoClass = null;
	private String sessionExpiredUrl = null;
	


	private static transient Logger logger = Logger.getLogger(EnginConf.class);

	private static EnginConf instance = null;
	
	/**
	 * Gets the instance.
	 * 
	 * @return EnginConf
	 */
	public static EnginConf getInstance(){
		if(instance==null) instance = new EnginConf();
		return instance;
	}
	
	private EnginConf() {
		try {
			logger.debug("Resource: " + getClass().getResource("/engine-config.xml"));
			if (getClass().getResource("/engine-config.xml")!=null){
				InputSource source=new InputSource(getClass().getResourceAsStream("/engine-config.xml"));
				config = SourceBean.fromXMLStream(source);   

				setResourcePath();
				setSpagoBiServerUrl();
				setSpagoBiSsoClass();
				setSessionExpiredUrl();
			}else logger.debug("Impossible to load configuration for report engine");
		} catch (SourceBeanException e) {
			logger.error("Impossible to load configuration for report engine", e);
		}
	}
	
	/**
	 * Gets the config.
	 * 
	 * @return SourceBean contain the configuration
	 */
	public SourceBean getConfig() {
		return config;
	}

	
	
	/**
	 * Gets the pass.
	 * 
	 * @return the pass
	 */
	public String getPass(){
	    SourceBean passSB = (SourceBean)config.getAttribute("PASS");
	    String pass = (String) passSB.getCharacters();
	    return pass;
	}

	/**
	 * @return the resourcePath
	 */
	private void setResourcePath() {
		logger.debug("IN");
		SourceBean sb = (SourceBean)config.getAttribute("RESOURCE_PATH_JNDI_NAME");
		String path = (String) sb.getCharacters();
		resourcePath= SpagoBIUtilities.readJndiResource(path);
		logger.debug("OUT");
	}

	public String getSessionExpiredUrl() {
	    return sessionExpiredUrl;
	}
	
	private void setSessionExpiredUrl() {
		logger.debug("IN");
		SourceBean sb = (SourceBean)config.getAttribute("SESSION_EXPIRED_URL");
		if (sb != null) {
			sessionExpiredUrl = sb.getCharacters();
		} else {
			sessionExpiredUrl = null;
		}
		logger.debug("OUT");
	}


	public String getResourcePath() {
	    return resourcePath;
	}

	public String getSpagoBiServerUrl() {
	    return spagoBiServerUrl;
	}

	private void setSpagoBiServerUrl() {
		logger.debug("IN");
		SourceBean sb = (SourceBean)config.getAttribute("SPAGOBI_SERVER_URL");
		String server = (String) sb.getCharacters();
		if (server!=null && server.length()>0){
		    spagoBiServerUrl=server;
		}else {
			sb = (SourceBean)config.getAttribute("SPAGOBI_SERVER_URL_JNDI_NAME");
			server = (String) sb.getCharacters();
			spagoBiServerUrl= SpagoBIUtilities.readJndiResource(server);			    
		}

		logger.debug("OUT");

	}
	
	/*
	public String getSpagoBiDomain() {
		return spagoBiDomain;
	}
	*/
	
	/*
	private void setSpagoBiDomain() {
		logger.debug("IN");
		SourceBean sb = (SourceBean) config.getAttribute("SPAGOBI_DOMAIN_JNDI_NAME");
		String domain = (String) sb.getCharacters();
		if (domain!=null && domain.length()>0){
			spagoBiDomain = SpagoBIUtilities.readJndiResource(domain);	
		}
		logger.debug("OUT");
	}
	*/
	
	public String getSpagoBiSsoClass() {
		return spagoBiSsoClass;
	}

	private void setSpagoBiSsoClass() {
		logger.debug("IN");
		SourceBean sb = (SourceBean)config.getAttribute("INTEGRATION_CLASS_JNDI");
		String classSso = (String) sb.getCharacters();
		if (classSso!=null && classSso.length()>0){
			spagoBiSsoClass=SpagoBIUtilities.readJndiResource(classSso);	
		}

		logger.debug("OUT");
	}
	
}
