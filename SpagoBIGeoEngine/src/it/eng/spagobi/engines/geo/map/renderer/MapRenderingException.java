/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.renderer;

import it.eng.spagobi.engines.geo.GeoEngineException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class MapRenderingException extends GeoEngineException {
	public MapRenderingException(String message) {
	   	super(message);
	}
	
	public MapRenderingException(String message, Throwable ex) {
	  	super(message, ex);
	}
	
	public MapRenderingException(Throwable ex) {
		super(ex);
    }
}
