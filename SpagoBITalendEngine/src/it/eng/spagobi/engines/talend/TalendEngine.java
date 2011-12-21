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
package it.eng.spagobi.engines.talend;



import java.io.File;

import it.eng.spagobi.engines.talend.runtime.RuntimeRepository;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

/**
 * @author Andrea Gioia
 *
 */
public class TalendEngine  {
	
	private static TalendEngineVersion version;
	private static TalendEngineConfig config;	
		
	
	private static RuntimeRepository runtimeRepository;
		
	
	static { 
		TalendEngine.version = TalendEngineVersion.getInstance();
		TalendEngine.config = TalendEngineConfig.getInstance();
		
		File rrRootDir = TalendEngineConfig.getInstance().getRuntimeRepositoryRootDir();
		TalendEngine.setRuntimeRepository( new RuntimeRepository(rrRootDir) );		
	}
	
	
	public static RuntimeRepository getRuntimeRepository() throws SpagoBIEngineException {
		if(runtimeRepository == null || !runtimeRepository.getRootDir().exists()) {
			throw new SpagoBIEngineException("Runtime-Repository not available",
					"repository.not.available");
		}
		return TalendEngine.runtimeRepository;
	}


	private static void setRuntimeRepository(RuntimeRepository runtimeRepository) {
		TalendEngine.runtimeRepository = runtimeRepository;
	}


	public static TalendEngineVersion getVersion() {
		return TalendEngine.version;
	}


	public static TalendEngineConfig getConfig() {
		return TalendEngine.config;
	}

	
	
	
	
}
