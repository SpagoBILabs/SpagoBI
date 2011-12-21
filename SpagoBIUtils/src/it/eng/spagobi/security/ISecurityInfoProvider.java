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
package it.eng.spagobi.security;

import java.util.List;

/**
 * This is interface for gathering security information from portal server.
 * A Specific subclass exists for each portal server.
 */
public interface ISecurityInfoProvider {
	
	/**
	 * Gets the roles.
	 * 
	 * @return The Role list. (list of it.eng.spagobi.bo.Role)
	 */
	public List getRoles();
	

	/**
	 * Gets the list of names of all attributes of all profiles defined in the portal server.
	 * 
	 * @return the list of names of all attributes of all profiles defined in the portal server
	 */
	public List getAllProfileAttributesNames ();
	

}
