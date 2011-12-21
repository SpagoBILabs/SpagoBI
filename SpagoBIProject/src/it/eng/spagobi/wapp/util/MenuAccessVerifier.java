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
package it.eng.spagobi.wapp.util;


import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.wapp.bo.Menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MenuAccessVerifier {



	public static boolean canView(Menu menu, IEngUserProfile profile) {

		Role[] menuRoles=menu.getRoles();
		Collection profileRoles=null;

		try {
			profileRoles = ((UserProfile)profile).getRolesForUse();
		} catch (EMFInternalError e) {
			return false;
		}


		boolean found=false;
		for (Iterator iterator = profileRoles.iterator(); iterator.hasNext() && !found;) {
			String profileRole = (String) iterator.next();
			for(int i=0;i<menuRoles.length && !found;i++){
				Role menuRole=menuRoles[i];
				String menuRoleName=menuRole.getName();

				if(menuRoleName.equals(profileRole)){
					found=true;
				}
			}

		}
		return found;


	}



}
