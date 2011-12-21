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
package it.eng.spagobi.commons.utilities;

import it.eng.spago.base.SessionContainer;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

import java.util.List;

/**
 * @author Gioia
 *	
 *	provides mothods for a quick and dirty session monitoring and debugging
 */
public class SessionMonitor {
	
	/**
	 * Prints the session.
	 * 
	 * @param session the session
	 */
	public static void printSession(SessionContainer session) {
		List list = session.getAttributeNames();
		for(int i = 0; i < list.size(); i++) {
			SpagoBITracer.debug(SpagoBIConstants.NAME_MODULE, SessionMonitor.class.getName(),
					            "printSession", list.get(i).toString());
		}				
	}
}
