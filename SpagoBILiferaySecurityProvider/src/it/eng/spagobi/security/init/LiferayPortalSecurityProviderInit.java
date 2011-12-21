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
package it.eng.spagobi.security.init;


import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;



/**
 * 
 * @author Angelo Bernabei (angelo.bernabei@eng.it)
 * @author Sven Werlen (sven.werlen@savoirfairelinux.com)
 */
public class LiferayPortalSecurityProviderInit implements InitializerIFace {
	
	static private Logger logger = Logger.getLogger(LiferayPortalSecurityProviderInit.class);

	private SourceBean _config = null;
	
	public SourceBean getConfig() {
		return _config;
	}

	public void init(SourceBean config) {
		_config = config;
		logger.warn( "NOT IMPLEMENTED");
	}

}
