/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see WEKA.LICENSE.txt file
 * 
 */

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

