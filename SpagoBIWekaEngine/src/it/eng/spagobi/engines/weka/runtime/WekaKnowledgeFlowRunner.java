/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.engines.weka.runtime;


import org.apache.log4j.Logger;

import it.eng.spagobi.engines.weka.WekaEngineInstance;
import it.eng.spagobi.engines.weka.WekaEngineRuntimeException;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaKnowledgeFlowRunner implements IEngineInstanceRunner {

	private static transient Logger logger = Logger.getLogger(WekaKnowledgeFlowRunner.class);
	
	public void run(IEngineInstance engineInstance) {
		Assert.assertNotNull(engineInstance, "Parameter [engineInstance]cannot be null");
		
		if(engineInstance instanceof WekaEngineInstance) {
			run( (WekaEngineInstance)engineInstance );
		} else {
			throw new WekaEngineRuntimeException("Impossible tu run engine instance of type [" + engineInstance.getClass().getName() + "]");
		}
	}
	
	public void run(WekaEngineInstance engineInstance) {
		Assert.assertNotNull(engineInstance, "Parameter [engineInstance]cannot be null");
		
		WorkManager wm;
    	WekaWork wekaWork;
    	WekaWorkListener wekaWorkListener;
    	
    	logger.debug("IN");
    	try {
	    	wm = new WorkManager();
	    	wekaWork = new WekaWork(engineInstance);
	    	wekaWorkListener = new WekaWorkListener();
	    	wm.run(wekaWork, wekaWorkListener);
    	} catch (Throwable t) {
    		throw new RuntimeException("Impossible to execute command in a new thread", t);
		} finally {
    		logger.debug("OUT");
    	}
	} 
}
