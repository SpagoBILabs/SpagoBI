/* SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This program is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, either version 2.1 
 * of the License, or (at your option) any later version. This program is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU  General Public License for more details. You should have received a copy of the GNU  General Public License along with 
 * this program. If not, see: http://www.gnu.org/licenses/. */
package it.eng.spagobi.engines.weka;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaEngine {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(WekaEngine.class);
	
    
    // init engine
    private static WekaEngineConfig engineConfig;
    
    static {
    	engineConfig = WekaEngineConfig.getInstance();
    }
    
    public static WekaEngineConfig getConfig() {
    	return engineConfig;
    }
    // init engine
    
	/**
	 * Creates the instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @return the weka engine instance
	 */
	public static WekaEngineInstance createInstance(String template, Map env) {
		WekaEngineInstance wekaEngineInstance = null;
		logger.debug("IN");
		wekaEngineInstance = new WekaEngineInstance(template, env);
		logger.debug("OUT");
		return wekaEngineInstance;	
	}
}
