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
import it.eng.spagobi.kpi.config.bo.KpiError;
import it.eng.spagobi.kpi.config.metadata.SbiKpiError;
import it.eng.spagobi.tools.dataset.exceptions.DatasetException;

import java.util.List;

public interface IKpiErrorDAO extends ISpagoBIDao{


	/**
	 * @param kpiError
	 * @return
	 * @throws EMFUserError
	 */
	public Integer insertKpiError(SbiKpiError kpiError) throws EMFUserError;

	/**	
	 * @return
	 * @throws EMFUserError
	 */
	public List<KpiError> loadAllKpiErrors() throws EMFUserError;

	/**
	 * 
	 * @return
	 * @throws EMFUserError
	 */
	public KpiError loadKpiErrorById(Integer id) throws EMFUserError;

	/**
	 * @param kpiError
	 * @return
	 * @throws EMFUserError
	 */
	public void updateKpiError(SbiKpiError kpiError) throws EMFUserError;

	/**
	 * 
	 * @param exception
	 * @param modelInstanceId
	 * @param resourceName
	 * @return
	 * @throws EMFUserError
	 */
	public Integer insertKpiError(	DatasetException exception, Integer modelInstanceId, String resourceName) throws EMFUserError;


}
