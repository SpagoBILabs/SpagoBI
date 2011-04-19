package it.eng.spagobi.kpi.config.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;

import java.util.List;

public interface IKpiInstPeriodDAO extends ISpagoBIDao{

	/**
	 * Load couples by Kpi Instance Id .
	 * 
	 * @param modelId
	 *            the id of modelInstance to check.

	 * @return list of modelResource Id
	 * 
	 * @throws EMFUserError
	 */
	List loadKpiInstPeriodId(Integer kpiInstId) throws EMFUserError;

	
	
}
