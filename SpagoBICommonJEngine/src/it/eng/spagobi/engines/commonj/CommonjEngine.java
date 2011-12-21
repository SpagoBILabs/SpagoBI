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
package it.eng.spagobi.engines.commonj;



import java.io.File;

import it.eng.spagobi.engines.commonj.runtime.WorksRepository;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

public class CommonjEngine  {
	
	private static CommonjEngineConfig config;	
		
	
	private static WorksRepository worksRepository;
		
	
	static { 
		CommonjEngine.config = CommonjEngineConfig.getInstance();
		
		File rrRootDir = CommonjEngineConfig.getInstance().getWorksRepositoryRootDir();
		CommonjEngine.setWorksRepository( new WorksRepository(rrRootDir) );		
	}
	
	
	public static WorksRepository getWorksRepository() throws SpagoBIEngineException {
		if(worksRepository == null || !worksRepository.getRootDir().exists()) {
			throw new SpagoBIEngineException("Works-Repository not available",
					"repository.not.available");
		}
		return CommonjEngine.worksRepository;
	}


	private static void setWorksRepository(WorksRepository worksRepository) {
		CommonjEngine.worksRepository = worksRepository;
	}



	public static CommonjEngineConfig getConfig() {
		return CommonjEngine.config;
	}

	
	
	
	
}
