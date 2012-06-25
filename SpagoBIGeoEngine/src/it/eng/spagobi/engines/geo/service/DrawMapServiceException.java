/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.service;

import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DrawMapServiceException extends SpagoBIEngineServiceException {
	
	public DrawMapServiceException(String serviceName, String message) {
    	super(serviceName, message);
    }
	
   
    public DrawMapServiceException(String serviceName, String message, Throwable ex) {
    	super(serviceName, message, ex);
    }
    
   
    public DrawMapServiceException(String serviceName, Throwable ex) {
    	super(serviceName,  ex);
    }
}
