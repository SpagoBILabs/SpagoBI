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
package it.eng.spagobi.hotlink.rememberme.dao;

import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.hotlink.rememberme.bo.RememberMe;

import java.util.List;

/**
 * 
 * @author Zerbetto (davide.zerbetto@eng.it)
 *
 */
public interface IRememberMeDAO extends ISpagoBIDao{
	
	/**
	 * Save remember me.
	 * 
	 * @param name the name
	 * @param description the description
	 * @param docId the doc id
	 * @param subObjId the sub obj id
	 * @param userId the user id
	 * @param parameters the parameters
	 * 
	 * @return true, if successful
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public boolean saveRememberMe(String name, String description, Integer docId, Integer subObjId, String userId, String parameters) throws EMFInternalError;
	
	/**
	 * Gets all user's remember me.
	 * 
	 * @param userId the user id
	 * 
	 * @return the my remember me
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public List getMyRememberMe(String userId) throws EMFInternalError;
	
	/**
	 * Deletes the remember me with the given id.
	 * 
	 * @param rememberMeId the remember me id
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public void delete(Integer rememberMeId) throws EMFInternalError;
	
	/**
	 * Loads the remember me with the given id.
	 * 
	 * @param rememberMeId the remember me id
	 * 
	 * @return the required remember me
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public RememberMe getRememberMe(Integer rememberMeId) throws EMFInternalError;
}
