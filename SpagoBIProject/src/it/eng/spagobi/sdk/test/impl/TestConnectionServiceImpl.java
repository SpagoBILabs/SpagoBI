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
package it.eng.spagobi.sdk.test.impl;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.sdk.AbstractSDKService;
import it.eng.spagobi.sdk.test.TestConnectionService;

import org.apache.log4j.Logger;

public class TestConnectionServiceImpl extends AbstractSDKService implements TestConnectionService {

	static private Logger logger = Logger.getLogger(TestConnectionServiceImpl.class);

	public boolean connect() {
		boolean toReturn = false;
        logger.debug("IN");
        try {
        	IEngUserProfile profile = getUserProfile();
        	if (profile != null) {
        		UserProfile userProfile = (UserProfile) profile;
        		logger.info("User recognized: " +
        				"userUniqueIdentifier = [" + userProfile.getUserAttributeNames() + "]; " +
        						"userId = [" + userProfile.getUserId() + "]; " +
        								"userName = [" + userProfile.getUserName() + "]");
        		toReturn = true;
        	} else {
        		logger.error("User not recognized.");
        		toReturn = false;
        	}
        } catch(Exception e) {
            logger.error("Error while creating user profile object", e);
        }
        logger.debug("OUT");
        return toReturn;
	}
	

}
