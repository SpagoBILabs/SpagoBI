/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.service;

import it.eng.spagobi.engines.geo.GeoEngineInstance;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractGeoEngineAction.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractGeoEngineAction extends AbstractEngineAction {
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(AbstractGeoEngineAction.class);
    	
		
	/**
	 * Gets the geo engine instance.
	 * 
	 * @return the geo engine instance
	 */
	public GeoEngineInstance getGeoEngineInstance() {
		return (GeoEngineInstance)getEngineInstance();
	}
	
	
}
