/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
**/
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
