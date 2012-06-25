/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console;


import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ConsoleEngine {
	
	private static ConsoleEngineConfig engineConfig;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(ConsoleEngine.class);
	
    // init engine
    static {
    	engineConfig = ConsoleEngineConfig.getInstance();
    }
    
    public static ConsoleEngineConfig getConfig() {
    	return engineConfig;
    }
    
    
	/**
	 * Creates the instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @return the console engine instance
	 */
	public static ConsoleEngineInstance createInstance(Object template, Map env) {
		ConsoleEngineInstance consoleEngineInstance = null;
		logger.debug("IN");
		consoleEngineInstance = new ConsoleEngineInstance(template, env);
		logger.debug("OUT");
		return consoleEngineInstance;
	}
}
