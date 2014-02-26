/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.services.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.initializers.WhatIfEngineStartAction;

/**
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 *
 */
public abstract class AbstractRestService {

	public static transient Logger logger = Logger.getLogger(AbstractRestService.class);
	
	public WhatIfEngineInstance getEngineInstance(HttpServletRequest request) {
		HttpSession session = request.getSession();
		WhatIfEngineInstance engineInstance = (WhatIfEngineInstance) session.getAttribute(WhatIfEngineStartAction.ENGINE_INSTANCE);
		return engineInstance;
	}
	
}
