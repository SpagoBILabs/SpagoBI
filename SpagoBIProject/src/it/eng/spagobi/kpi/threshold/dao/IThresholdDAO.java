package it.eng.spagobi.kpi.threshold.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.kpi.threshold.metadata.SbiThreshold;

import java.util.List;

public interface IThresholdDAO {
	
	/**
	 * Returns the Threshold of the referred id
	 * 
	 * @param id of the Threshold
	 * @return Threshold of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */
	public Threshold loadThresholdById(Integer id) throws EMFUserError ;
	
	public Threshold loadThresholdByCode(String code) throws EMFUserError ;
	
	/**
	 * Returns the list of Thresholds.
	 * @param typeOrder DESC or ASC.
	 * @param fieldOrder Name of the column in the view to Order.
	 * @return the list of Thresholds.
	 * @throws EMFUserError If an Exception occurred.
	 */
	public List loadThresholdList(String fieldOrder, String typeOrder) throws EMFUserError ;
	
	public List loadThresholdList() throws EMFUserError ;
	
	public List loadPagedThresholdList(Integer offset, Integer fetchSize)throws EMFUserError ;
	
	public Integer countThresholds()throws EMFUserError ;

	public void modifyThreshold(Threshold threshold) throws EMFUserError ;

	public Integer insertThreshold(Threshold toCreate)throws EMFUserError ;

	public boolean deleteThreshold(Integer thresholdId)throws EMFUserError;
	
	public Threshold toThreshold(SbiThreshold t) throws EMFUserError; 

	
}
