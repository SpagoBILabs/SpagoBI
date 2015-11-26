/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.mobile;

import it.eng.spago.base.SourceBean;

import java.util.Map;

import org.apache.log4j.Logger;


public class MobileEngine {
	private static MobileEngineConfig engineConfig;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(MobileEngine.class);
	
    // init engine
    static {
    	engineConfig = MobileEngineConfig.getInstance();
    }
    
    public static MobileEngineConfig getConfig() {
    	return engineConfig;
    }
    
	/**
	 * Creates the instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @return the mobile engine instance
	 */
	public static MobileEngineInstance createInstance(SourceBean template, Map env) {
		MobileEngineInstance mobileEngineInstance = null;
		logger.debug("IN");
		mobileEngineInstance = new MobileEngineInstance(template, env);
		logger.debug("OUT");
		return mobileEngineInstance;	
	}
}
