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
package it.eng.spagobi.analiticalmodel.document.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.commons.dao.ISpagoBIDao;

public interface IBIObjectRating extends ISpagoBIDao{
	
	
	/**
	 * Implements the query to insert a rating for a BI Object.
	 * 
	 * @param obj the obj
	 * @param userid the userid
	 * @param rating the rating
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	
	public void voteBIObject(BIObject obj,String userid, String rating) throws EMFUserError;
	
	/**
	 * Implements the query to calculate the medium rating for a BI Object.
	 * 
	 * @param obj the obj
	 * 
	 * @return The BI object medium rating
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public Double calculateBIObjectRating(BIObject obj) throws EMFUserError;

}
