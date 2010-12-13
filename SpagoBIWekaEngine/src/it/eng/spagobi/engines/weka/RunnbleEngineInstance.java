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
package it.eng.spagobi.engines.weka;

import java.util.Map;

import it.eng.spagobi.utilities.engines.EngineAnalysisMetadata;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class RunnbleEngineInstance implements IEngineInstance {
	
	Map env = null;
	
	abstract void run();
	
	public String getId() {
		return null;
	}
	
	public EngineAnalysisMetadata getAnalysisMetadata() {
		return null;
	}
	
	public void setAnalysisMetadata(EngineAnalysisMetadata analysisMetadata) {
		
	}

	public IEngineAnalysisState getAnalysisState() {
		return null;
	}
	
	public void setAnalysisState(IEngineAnalysisState analysisState) {
		
	}

	public Map getEnv() {
		return env;
	}

	public void setEnv(Map env) {
		this.env = env;
	}

	public void validate() throws SpagoBIEngineException {
		
	}
}
