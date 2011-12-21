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


import java.io.File;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.weka.WekaEngine;
import it.eng.spagobi.engines.weka.WekaEngineInstance;
import it.eng.spagobi.engines.weka.WekaEngineInstanceMonitor;
import it.eng.spagobi.engines.weka.WekaEngineRuntimeException;
import it.eng.spagobi.engines.weka.knowledgeflow.WekaKnowledgeFlow;
import it.eng.spagobi.engines.weka.knowledgeflow.WekaKnowledgeFlowEnv;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

/**
 * Questa classe disaccoppia l'attivita di lancio (pre & post processing) dal work effettivamente lanciato.
 * In tal modo è possibile avere diversi runner che utilizzano lo stesso work come nel caso del motore talend
 * dove il work utilizzato è sempre lo stesso sia per il runner Java che per quello perl 
 * ed ha l'unica responsibilità di eseguire un comando tramite System.Ruintime.exec
 * 
 * In questo caso ha poco senso ma viene mantenuto per coerenza strutturale.
 * 
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
		WekaKnowledgeFlow knowledgeFlow;
		WekaKnowledgeFlowEnv knowledgeFlowEnv;
		WekaEngineInstanceMonitor wekaEngineInstanceMonitor;
		
		WorkManager wm;
    	WekaWork wekaWork;
    	WekaWorkListener wekaWorkListener;
    	   	
    	logger.debug("IN");
    	try {
    		Assert.assertNotNull(engineInstance, "Parameter [engineInstance] cannot be null");
    		
    		// wrap ...
    		knowledgeFlowEnv = new WekaKnowledgeFlowEnv(engineInstance.getEnv());
    		knowledgeFlow = WekaKnowledgeFlow.load(engineInstance.getTemplate(), knowledgeFlowEnv);
    		wekaEngineInstanceMonitor = new WekaEngineInstanceMonitor(engineInstance.getEnv());
    		
    		WekaEngine.getConfig().getEngineOutputFilesDir();
    		File outputFile = new File(WekaEngine.getConfig().getEngineOutputFilesDir(), "out" + System.currentTimeMillis() + ".txt");
    		logger.debug("Outputfile set to [" + outputFile + "]");
    		knowledgeFlowEnv.setOutputFile( outputFile );
    		
    		//knowledgeFlowEnv.setOutputFile(new File("C:\\ProgramFiles\\apache-tomcat-6.0.18\\resources\\weka\\outputfiles\\pippo.txt"));
    		
    		// ...and run
	    	wm = new WorkManager();
	    	wekaWork = new WekaWork(knowledgeFlow);
	    	wekaWorkListener = new WekaWorkListener( wekaEngineInstanceMonitor );
	    	wm.run(wekaWork, wekaWorkListener);
    	} catch (Throwable t) {
    		throw new RuntimeException("Impossible to execute command in a new thread", t);
		} finally {
    		logger.debug("OUT");
    	}
	} 
}
