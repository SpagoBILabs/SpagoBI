/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.chart;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */

/**
 * The Class ChartEngineException.
 */
public class ChartEngineRuntimeException extends SpagoBIEngineRuntimeException {
    
	/** The hints. 
	List hints;
	*/
	
	ChartEngineInstance engineInstance;
	
	
	/**
	 * Builds a <code>ChartException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public ChartEngineRuntimeException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>ChartEngineException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public ChartEngineRuntimeException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    public ChartEngineInstance getEngineInstance() {
		return engineInstance;
	}

	public void setEngineInstance(ChartEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
	}
    
   


}

