/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.exporter;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ExportException extends SpagoBIRuntimeException {
	/**
	 * Builds a <code>ExportException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public ExportException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>ExportException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public ExportException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    /**
     * Builds a <code>ExportException</code>.
     * 
     * @param ex previous Throwable object
     */
    public ExportException(Throwable ex) {
    	super(ex);
    }
}
