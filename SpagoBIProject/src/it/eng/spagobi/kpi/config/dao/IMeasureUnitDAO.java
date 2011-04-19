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
