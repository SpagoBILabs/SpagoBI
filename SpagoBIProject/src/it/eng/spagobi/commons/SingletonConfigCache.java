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
