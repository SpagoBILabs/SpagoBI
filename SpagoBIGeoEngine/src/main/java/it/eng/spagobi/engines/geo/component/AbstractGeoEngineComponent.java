/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.component;

import it.eng.spagobi.engines.geo.GeoEngineException;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractGeoEngineComponent.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractGeoEngineComponent implements IGeoEngineComponent {
	
	/** The conf. */
	Object conf;
	
	/** The env. */
	Map env;
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.IGeoEngineComponent#init(java.lang.Object)
	 */
	public void init(Object conf) throws GeoEngineException {
		this.conf = conf;	
	}
	
	/**
	 * Gets the conf.
	 * 
	 * @return the conf
	 */
	protected Object getConf() {
		return conf;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.IGeoEngineComponent#setEnv(java.util.Map)
	 */
	public void setEnv(Map env) {
		this.env = env; 	
	}
	
	/**
	 * Gets the env.
	 * 
	 * @return the env
	 */
	public Map getEnv() {
		return env;
	}

}
