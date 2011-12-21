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
package it.eng.spagobi.commons;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class SingletonConfigCache implements ISingletonConfigCache{
	
	private static transient Logger logger = Logger.getLogger(SingletonConfigCache.class);
	private HashMap<String, String> cache=new HashMap<String, String>();
	
	public SingletonConfigCache() {
		logger.debug("IN");
		
		IConfigDAO dao= null;  
		try {
			dao= DAOFactory.getSbiConfigDAO();
			List<Config> allConfig= dao.loadAllConfigParameters();
			if (allConfig.size()==0) logger.error("The table sbi_config is EMPTY");
			for (Config config: allConfig ) {
				cache.put(config.getLabel(), config.getValueCheck());
				logger.info("Add: "+config.getLabel() +" / "+config.getValueCheck());
			}
			
		} catch (EMFUserError e) {
			logger.error("Impossible to load configuration for report engine",e);
		} catch (Exception e) {
			logger.error("Impossible to load configuration for report engine",e);
		}finally{
			logger.debug("OUT");
		}
	}
	
	public String get(String key){
		if (cache.get(key)==null) {
			logger.error("The property '"+key+"' doens't have any value assigned, check SBI_CONFIG table");
			return null;
		}
		logger.debug("GET :"+key+"="+cache.get(key));
		return cache.get(key);
	}
}
