/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Giulio Gavardi (giulio.gavardi@eng.it)
 * 
 * @class DBWriteResource
 * 
 * Provides services to manage the axis resource
 * 
 */
package it.eng.spagobi.engines.whatif.api;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.version.VersionManager;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;

@Path("/1.0/version")
public class VersionResource extends AbstractWhatIfEngineService {

	public static transient Logger logger = Logger.getLogger(VersionResource.class);

	
	private VersionManager versionManager;

	private VersionManager getVersionBusiness() {
		WhatIfEngineInstance ei = getWhatIfEngineInstance();

		if(versionManager==null){
			versionManager = new VersionManager(ei);
		}
		return versionManager;
	}


	/**
	 * Service to increase Version
	 * @return 
	 * 
	 */
	@POST
	@Path("/increase")
	public String increaseVersion(){
		logger.debug("IN");
			
		PivotModel model =getVersionBusiness().persistNewVersionProcedure();
		
		logger.debug("OUT");
		return renderModel(model);
		
	
	}


}
