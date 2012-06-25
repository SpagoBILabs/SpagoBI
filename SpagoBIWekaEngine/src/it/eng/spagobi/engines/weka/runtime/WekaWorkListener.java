/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.weka.runtime;

import org.apache.log4j.Logger;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkListener;

import it.eng.spagobi.engines.weka.WekaEngineInstanceMonitor;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaWorkListener implements WorkListener {

	WekaEngineInstanceMonitor wekaEngineInstanceMonitor;
	
	private static transient Logger logger = Logger.getLogger(WekaWorkListener.class);
	
	public WekaWorkListener(WekaEngineInstanceMonitor wekaEngineInstanceMonitor) {
		this.wekaEngineInstanceMonitor = wekaEngineInstanceMonitor; //new WekaEngineInstanceMonitor(engineInstance.getEnv());
	}
	
	public void workAccepted(WorkEvent event) {
		logger.info("IN");
		logger.info("OUT");
	}

	public void workRejected(WorkEvent event) {
		logger.info("IN");
		logger.info("OUT");
	}

	public void workStarted (WorkEvent event) {
		wekaEngineInstanceMonitor.start();
	}
	
	public void workCompleted (WorkEvent event) {
		WorkException workException;
		WekaWork wekaWork;
    	
    	logger.info("IN");
    	
    	try {
			workException = event.getException();
			//wekaWork = (WekaWork) event.getWorkItem().getResult();
			
			if (workException != null) {
				wekaEngineInstanceMonitor.setError(workException);
			} 
			
			wekaEngineInstanceMonitor.stop();
    	} catch (Throwable t) {
    		logger.error(t);
			throw new RuntimeException("An error occurred while handling process completed event", t);
		} finally {
    		logger.debug("OUT");
    	}
	}

}
