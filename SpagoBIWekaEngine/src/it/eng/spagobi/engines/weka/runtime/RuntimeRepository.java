/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
