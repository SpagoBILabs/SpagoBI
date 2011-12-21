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
package it.eng.spagobi.kpi.config.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.kpi.config.bo.MeasureUnit;

public interface IMeasureUnitDAO extends ISpagoBIDao{

	/**
	 * Returns the MeasureUnit of the referred id
	 * 
	 * @param id of the Measure Unit
	 * @return Threshold of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */
	public MeasureUnit loadMeasureUnitById(Integer id) throws EMFUserError;

	/**
	 * Returns the MeasureUnit of the referred code
	 * 
	 * @param cd of the Measure Unit
	 * @return Threshold of the referred cd
	 * @throws EMFUserError If an Exception occurred
	 */
	public MeasureUnit loadMeasureUnitByCd(String cd) throws EMFUserError;



}
