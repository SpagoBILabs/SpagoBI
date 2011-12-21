/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see WEKA.LICENSE.txt file
 * 
 */
package it.eng.spagobi.engines.weka.runtime;

import it.eng.spagobi.engines.weka.WekaEngineInstance;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class RuntimeRepository {

	public void runEngineInstance(WekaEngineInstance engineInstance)  {
		IEngineInstanceRunner engineInstanceRunner;
		
		engineInstanceRunner = getEngineInstanceRunner( engineInstance );
		if(engineInstanceRunner != null) {
			engineInstanceRunner.run( engineInstance );
		}		
	}
	
	
	public IEngineInstanceRunner getEngineInstanceRunner(WekaEngineInstance engineInstance) {
		// we can run only weka knowledge folw
		return new WekaKnowledgeFlowRunner();
	}
}
