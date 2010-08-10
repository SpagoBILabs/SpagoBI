package it.eng.spagobi.kpi.config.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.config.bo.Periodicity;

import java.util.List;

public interface IPeriodicityDAO {
	
	/**
	 * Returns the Periodicity of the referred id
	 * 
	 * @param id of the Periodicity
	 * @return Periodicity of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */
	public Periodicity loadPeriodicityById(Integer id) throws EMFUserError;

	/**
	 * Returns the list of Periodicity.
	 * 
	 * @return the list of all Periodicity.
	 * @throws EMFUserError if an Exception occurs
	 */
	public List loadPeriodicityList() throws EMFUserError;

	
	public Integer getPeriodicitySeconds(Integer periodicityId)
	throws EMFUserError;
}
