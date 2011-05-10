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

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.apache.log4j.Logger;

/**
 * Defines the Singleton SpagoBI implementations.
 * 
 * @author Monia Spinelli
 */

public class SingletonConfig {

	private static SingletonConfig instance = null;
	private static transient Logger logger = Logger.getLogger(SingletonConfig.class);
	
	private ISingletonConfigCache cache=null;
	
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
		logger.debug("IN");
		try {
			cache = (ISingletonConfigCache)Class.forName("it.eng.spagobi.commons.SingletonConfigCache").newInstance();
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error creating SingletonConfigCache", e);
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
		try{
		instance= new SingletonConfig();
		}catch(Exception e){
			logger.error("Impossible to create a new istance", e);
		}
	}
}