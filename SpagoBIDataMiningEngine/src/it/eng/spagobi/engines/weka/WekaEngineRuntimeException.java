/* SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This program is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, either version 2.1 
 * of the License, or (at your option) any later version. This program is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU  General Public License for more details. You should have received a copy of the GNU  General Public License along with 
 * this program. If not, see: http://www.gnu.org/licenses/. */

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

