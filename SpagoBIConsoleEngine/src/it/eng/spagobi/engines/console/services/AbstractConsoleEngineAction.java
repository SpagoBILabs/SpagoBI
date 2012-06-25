/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.services;

import it.eng.spagobi.engines.console.ConsoleEngineInstance;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;

import org.apache.log4j.Logger;

/**
 * The Class AbstractConsoleEngineAction.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class AbstractConsoleEngineAction extends AbstractEngineAction {
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(AbstractConsoleEngineAction.class);
    	
		
	/**
	 * Gets the console engine instance.
	 * 
	 * @return the console engine instance
	 */
	public ConsoleEngineInstance getConsoleEngineInstance() {
		return (ConsoleEngineInstance)getEngineInstance();
	}
	
	
}
