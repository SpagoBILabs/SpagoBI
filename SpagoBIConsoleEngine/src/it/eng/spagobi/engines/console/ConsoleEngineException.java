/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.console;

import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */

/**
 * The Class ConsoleEngineException.
 */
public class ConsoleEngineException extends SpagoBIEngineException {
    
	/** The hints. 
	List hints;
	*/
	
	ConsoleEngineInstance engineInstance;
	
	
	/**
	 * Builds a <code>ConsoleEngineException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public ConsoleEngineException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>XXXEngineException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public ConsoleEngineException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    public ConsoleEngineInstance getEngineInstance() {
		return engineInstance;
	}

	public void setEngineInstance(ConsoleEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
	}
    
   


}

