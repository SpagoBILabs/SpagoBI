/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ChartEngine {
	
	private static ChartEngineConfig engineConfig;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(ChartEngine.class);
	
    // init engine
    static {
    	engineConfig = ChartEngineConfig.getInstance();
    }
    
    public static ChartEngineConfig getConfig() {
    	return engineConfig;
    }
    
	/**
	 * Creates the instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @return the geo report engine instance
	 */
	public static ChartEngineInstance createInstance(Object template, Map env) {
		ChartEngineInstance chartEngineInstance = null;
		logger.debug("IN");
		chartEngineInstance = new ChartEngineInstance(template, env);
		logger.debug("OUT");
		return chartEngineInstance;	
	}
}
