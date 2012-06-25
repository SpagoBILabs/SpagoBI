/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.weka;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;


public class WekaEngineRuntimeException extends SpagoBIEngineRuntimeException {
   
	WekaEngineInstance engineInstance;
	
	
	/**
	 * Builds a <code>GeoEngineException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public WekaEngineRuntimeException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>GeoEngineException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public WekaEngineRuntimeException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    public WekaEngineInstance getEngineInstance() {
		return engineInstance;
	}

	public void setEngineInstance(WekaEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
	}
    
   


}

