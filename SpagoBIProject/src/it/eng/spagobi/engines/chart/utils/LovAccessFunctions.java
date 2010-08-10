/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/

package it.eng.spagobi.engines.chart.utils;



import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.behaviouralmodel.lov.dao.IModalitiesValueDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;

import org.apache.log4j.Logger;

/** Internal Engine
 *  * @author Giulio Gavardi
 *     giulio.gavardi@eng.it
 */

public class LovAccessFunctions {

	private static transient Logger logger=Logger.getLogger(LovAccessFunctions.class);


	/**
	 * Gets the lov result.
	 * 
	 * @param profile the profile
	 * @param lovLabel the lov label
	 * 
	 * @return the lov result
	 */
	public static String getLovResult(IEngUserProfile profile, String lovLabel){
		String result = "";
		logger.debug("IN");
		try{
			if (profile == null) {
				result = GeneralUtilities.getLovResult(lovLabel);
			} else {
				result = GeneralUtilities.getLovResult(lovLabel, profile);
			}
		}	
		catch (Exception e) {
			logger.error("Error",e);
		}
		logger.debug("OUT");
		return result;
	}



	private static String getLovType(String lovName) {
		String toReturn = "";
		logger.debug("IN");
		try{
			IModalitiesValueDAO lovDAO = DAOFactory.getModalitiesValueDAO();
			ModalitiesValue lov = lovDAO.loadModalitiesValueByLabel(lovName);
			String type = lov.getITypeCd();
			toReturn = type;
		} catch (Exception e) {
			logger.error("Error while recovering type of lov " + lovName,e);
		}
		logger.debug("OUT");
		return toReturn;
	}

	
}
