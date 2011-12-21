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

/*
 * Created on 21-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.services.security.service;

import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;

/**
 * The interface for the User Profile Factory, in order to manage user information.
 */
public interface ISecurityServiceSupplier {
    
    /**
     * 
     * @return SpagoBIUserProfile
     */
	SpagoBIUserProfile createUserProfile(String userId);
	
	/**
     * if SpagoBIUserProfile is NULL the password is incorrect!!!!
     * @param userId
     * @param psw
     * @return
     */
    SpagoBIUserProfile checkAuthentication(String userId, String psw);
	
    /**
     * if SpagoBIUserProfile is NULL the token is incorrect!!!!
     * @param userId
     * @param token
     * @return
     * 
     * @deprecated
     */
     SpagoBIUserProfile checkAuthenticationWithToken(String userId, String token);
        
    /**
     * 
     * @param userId
     * @param function
     * @return
     * 
     * @deprecated
     */
     boolean checkAuthorization(String userId, String function); 
            
}