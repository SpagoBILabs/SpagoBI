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

package it.eng.spagobi.commons;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import it.eng.spagobi.commons.metadata.SbiConfig;
import it.eng.spagobi.services.common.EnginConf;

/**
 * Defines the Singleton SpagoBI implementations.
 * 
 * @author Monia Spinelli
 */

public class SingletonConfig {

	private static SingletonConfig instance = null;
	private static transient Logger logger = Logger.getLogger(EnginConf.class);
	
	private SbiConfig config=null;

	public static SingletonConfig getInstance() {
		if (instance == null)
			instance = new SingletonConfig();
		return instance;
	}

	private SingletonConfig() {
		logger.debug("Resource: Table SbiConfig");
		config = new SbiConfig();
		try {
			if (config != null) {
				InputSource source = new InputSource(getClass()
						.getResourceAsStream("/spagobi.xml"));

				getConfigValue();
			} else
				logger
						.debug("Impossible to load data to table SbiConfig");
		} catch (Exception e) {
			logger.error("Impossible to load configuration for report engine",
					e);
		}
	}

	/**
	 * Gets the config.
	 * 
	 * @return SourceBean contain the configuration
	 */
	public SbiConfig getConfigValue() {
		return config;

	}
}