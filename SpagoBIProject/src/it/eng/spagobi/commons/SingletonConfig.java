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

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;

/**
 * Defines the Singleton SpagoBI implementations.
 * 
 * @author Monia Spinelli
 */

public class SingletonConfig {

	private static SingletonConfig instance = null;
	private static transient Logger logger = Logger.getLogger(SingletonConfig.class);
	
	private HashMap<String, String> cache=new HashMap<String, String>();
	
	public synchronized static SingletonConfig getInstance() {
		try{
		if (instance == null)
			instance = new SingletonConfig();
		}catch(Exception e) {
			logger.error("Impossible to load configuration",e);
		}
		return instance;
	}

	private SingletonConfig() throws Exception {
		logger.debug("Resource: Table SbiConfig");
		
		IConfigDAO dao= null;  
		try {
			dao= DAOFactory.getSbiConfigDAO();
			List<Config> allConfig= dao.loadAllConfigParameters();
			
			for (Config config: allConfig ) {
				cache.put(config.getLabel(), config.getValueCheck());
				logger.info("Add: "+config.getLabel() +" / "+config.getValueCheck());
			}
			
		} catch (EMFUserError e) {
			logger.error("Impossible to load configuration for report engine",e);
		}
	}

	/**
	 * Gets the config.
	 * 
	 * @return SourceBean contain the configuration
	 * 
	 * QUESTO METODO LO UTILIZZI PER LEGGERE LA CONFIGURAZIONE DEI SINGOLI ELEMENTI:
	 * ES:    String configurazione= SingletonConfig.getInstance().getConfigValue("home.banner");
	 */
	public synchronized String getConfigValue(String key) {
		return cache.get(key);

	}
	/**
	 * QUESTO METODO LO UTILIZZI ALL'INTERNO DEL SERVIZIO DI SALVATAGGIO CONFIGURAZIONE
	 * OGNI VOLTA CHE SALVIAMO UNA RIGA SVUOTIAMO LA CACHE
	 */
	public synchronized void clearCache() {
		instance=null;
	}	
}