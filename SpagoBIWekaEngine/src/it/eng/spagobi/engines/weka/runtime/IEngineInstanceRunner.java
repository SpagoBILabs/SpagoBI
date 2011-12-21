/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see WEKA.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.weka.runtime;

import it.eng.spagobi.utilities.engines.IEngineInstance;


/**
 * @author Andrea Gioia
 *
 */
public interface IEngineInstanceRunner {
 	public abstract void run(IEngineInstance engineInstance);
}
