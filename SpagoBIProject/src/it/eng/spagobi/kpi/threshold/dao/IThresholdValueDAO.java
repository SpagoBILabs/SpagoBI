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
package it.eng.spagobi.kpi.threshold.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;
import it.eng.spagobi.kpi.threshold.metadata.SbiThresholdValue;

import java.util.List;

public interface IThresholdValueDAO extends ISpagoBIDao{

	List loadThresholdValueList(Integer thresholdId,String fieldOrder, String typeOrder) throws EMFUserError;

	ThresholdValue loadThresholdValueById(Integer id) throws EMFUserError;
	
	SbiThresholdValue loadSbiThresholdValueById(Integer id) throws EMFUserError;

	void modifyThresholdValue(ThresholdValue thrVal) throws EMFUserError;

	Integer insertThresholdValue(ThresholdValue thrVal) throws EMFUserError;
	
	Integer saveOrUpdateThresholdValue(ThresholdValue thrVal) throws EMFUserError;

	boolean deleteThresholdValue(Integer thresholdId) throws EMFUserError;
	
	ThresholdValue toThresholdValue(SbiThresholdValue t)throws EMFUserError;

	public List getThresholdValues(KpiInstance k) throws EMFUserError;

	public List loadThresholdValuesByThresholdId(Integer id) throws EMFUserError;

}
