/* SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This program is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, either version 2.1 
 * of the License, or (at your option) any later version. This program is distributed in the hope that 
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU  General Public License for more details. You should have received a copy of the GNU  General Public License along with 
 * this program. If not, see: http://www.gnu.org/licenses/. */
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
