/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
