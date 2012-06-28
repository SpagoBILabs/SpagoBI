/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.component;

import it.eng.spagobi.engines.geo.GeoEngineException;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface IGeoEngineComponent.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IGeoEngineComponent {
	
	/**
	 * Inits the.
	 * 
	 * @param conf the conf
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	void init(Object conf) throws GeoEngineException;
	
	/**
	 * Sets the env.
	 * 
	 * @param env the new env
	 */
	void setEnv(Map env);
}
