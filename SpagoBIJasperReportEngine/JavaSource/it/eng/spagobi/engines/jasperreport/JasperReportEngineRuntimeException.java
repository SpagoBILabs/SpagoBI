/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.jasperreport;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * The Class QbeEngineException.
 */
public class JasperReportEngineRuntimeException extends SpagoBIEngineRuntimeException {
    
	/** The hints. 
	List hints;
	*/
	
	JasperReportEngineInstance engineInstance;
	
	
	/**
	 * Builds a <code>GeoEngineException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public JasperReportEngineRuntimeException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>GeoEngineException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public JasperReportEngineRuntimeException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    public JasperReportEngineInstance getEngineInstance() {
		return engineInstance;
	}

	public void setEngineInstance(JasperReportEngineInstance engineInstance) {
		this.engineInstance = engineInstance;
	}
    
   


}

