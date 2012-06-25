/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
