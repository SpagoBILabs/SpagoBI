/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see WEKA.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.weka.runtime;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.weka.knowledgeflow.WekaKnowledgeFlow;

import commonj.work.Work;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaWork implements Work {

	WekaKnowledgeFlow knowledgeFlow;
	
	private static transient Logger logger = Logger.getLogger(WekaWork.class);
	
	public WekaWork(WekaKnowledgeFlow knowledgeFlow) {
		this.knowledgeFlow = knowledgeFlow;	
	}
	
	public boolean isDaemon() {
		return false;
	}

	public void release() {
		logger.debug("IN");
		logger.debug("OUT");
	}

	
	public void run() {
		knowledgeFlow.run(true, true);
	}
}
