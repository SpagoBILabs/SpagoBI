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
