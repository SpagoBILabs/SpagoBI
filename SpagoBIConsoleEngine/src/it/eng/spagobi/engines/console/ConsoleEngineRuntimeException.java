/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */

package it.eng.spagobi.engines.console;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */

/**
 * The Class ConsoleEngineException.
 */
public class ConsoleEngineRuntimeException extends SpagoBIEngineRuntimeException {
    
	/** The hints. 
	List hints;
	*/
	
	ConsoleEngineInstance engineInstance;
	
	
	/**
	 * Builds a <code>ConsoleException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public ConsoleEngineRuntimeException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>ConsoleEngineException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public ConsoleEngineRuntimeException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    public ConsoleEngineInstance getEngineInstance() {
		return engineInstance;
	}

	public void setEngineInstance(ConsoleEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
	}
    
   


}

