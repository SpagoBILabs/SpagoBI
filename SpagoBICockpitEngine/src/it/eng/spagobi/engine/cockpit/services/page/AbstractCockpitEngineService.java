/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engine.cockpit.services.page;

import java.io.IOException;
import java.util.HashMap;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.engine.cockpit.CockpitEngineInstance;
import it.eng.spagobi.engine.cockpit.CockpitEngineRuntimeException;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.services.proxy.ContentServiceProxy;
import it.eng.spagobi.utilities.ParametersDecoder;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.rest.AbstractRestService;
import it.eng.spagobi.utilities.engines.rest.ExecutionSession;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

/**
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class AbstractCockpitEngineService extends AbstractRestService {


	

	@Context
	protected HttpServletRequest request;
	@Context 
	protected HttpServletResponse response;
	
	
	
	public static transient Logger logger = Logger.getLogger(AbstractCockpitEngineService.class);
	
	public EngineStartServletIOManager getIOManager() {
		EngineStartServletIOManager ioManager = null;
		
		try {
			ioManager = new EngineStartServletIOManager(request, response);
			UserProfile userProfile = (UserProfile) ioManager.getParameterFromSession(IEngUserProfile.ENG_USER_PROFILE);
			if(userProfile == null) {
				String userId = request.getHeader("user");
				userProfile = (UserProfile) UserUtilities.getUserProfile(userId);
				ioManager.setUserProfile(userProfile);
			}			
		} catch (Throwable t) {
			throw new CockpitEngineRuntimeException("An unexpected error occured while inizializing ioManager", t);
		}
		
		return ioManager;
	}
	
	/**
	 * Gets the cockpit engine instance.
	 * 
	 * @return the console engine instance
	 */
	public CockpitEngineInstance getEngineInstance() {
		ExecutionSession es = getExecutionSession();
		return (CockpitEngineInstance)es.getAttributeFromSession( EngineConstants.ENGINE_INSTANCE );
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.rest.AbstractRestService#getServletRequest()
	 */
	@Override
	public HttpServletRequest getServletRequest() {
		// TODO Auto-generated method stub
		return request;
	}
	
	

}
