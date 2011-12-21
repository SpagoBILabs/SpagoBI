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
package it.eng.spagobi.services.security;


import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;


/**
 * This is the SecurityService interfaces
 * @author Bernabei Angelo
 *
 */
public interface SecurityService {

    	/**
    	 * return the user profile informations
    	 * @param token
    	 * @return
    	 */
        SpagoBIUserProfile getUserProfile(String token,String userId);
	
	/**
	 * Check if the user can access to the path
	 * @param token
	 * @param idFolder ( object tree )
	 * @param mode
	 * @return
	 */
	boolean isAuthorized(String token,String userId,String idFolder,String mode);
	
	/**
	 * check if the user can access to this function 
	 * @param token
	 * @param function
	 * @return
	 */
	boolean checkAuthorization(String token,String userId,String function);	
}
