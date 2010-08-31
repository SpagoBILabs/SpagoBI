package it.eng.spagobi.kpi.config.dao;

import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiDocuments;
import it.eng.spagobi.kpi.config.bo.KpiValue;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

import java.util.Date;
import java.util.List;

public interface IKpiDAO {
	
	
	public String loadKPIValueXml(Integer kpiValueId)throws EMFUserError ;
	/**
	 * Inserts a new KPI Value with its date, value period and thresholds
	 * 
	 * @param KpiValue to insert 
	 * @throws EMFUserError If an Exception occurred
	 */	
	public Integer insertKpiValue(KpiValue value) throws EMFUserError;
	
	public KpiDocuments loadKpiDocByKpiIdAndDocId(Integer kpiId,Integer docId) throws EMFUserError ;

	/**
	 * Returns the ModelInstance of the referred label
	 * 
	 * @param label of the ModelInstance
	 * @return ModelInstance of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */	
	//public ModelInstanceNode loadModelInstanceByLabel(String label,Date requestedDate) throws EMFUserError ;
	
	/**
	 * Returns the ModelInstance of the referred id
	 * 
	 * @param id of the ModelInstance
	 * @return ModelInstance of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */	
	//public ModelInstanceNode loadModelInstanceById(Integer id,Date requestedDate) throws EMFUserError ;
	
	/**
	 * Returns the KpiInstance of the referred id
	 * 
	 * @param id of the KpiInstance
	 * @return KpiInstance of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */
	//public KpiInstance loadKpiInstanceById(Integer id) throws EMFUserError ;
	
	/**
	 * Returns the KpiInstance with id 'id' that was valid in date d 
	 * 
	 * @param id of the KpiInstance
	 * @param Date of when the KpiInstance has to be valid
	 * @return KpiInstance of the referred id valid in date d
	 * @throws EMFUserError If an Exception occurred
	 */
	//public KpiInstance loadKpiInstanceByIdFromHistory(Integer id, Date d) throws EMFUserError ;
	
	/**
	 * Loads the list of Threshold interval for the threshold with id 'id'
	 * 
	 * @param Integer id of the threshold
	 * @return List of all the the Threshols for the threshold with id 'id'
	 * @throws EMFUserError If an Exception occurred
	 */
	//public List loadThresholdsById(Integer id) throws EMFUserError ;
	
	/**
	 * Returns a List of all the the Threshols of the KpiInstance
	 * 
	 * @param KpiInstance k
	 * @return List of all the the Threshols of the KpiInstance
	 * @throws EMFUserError If an Exception occurred
	 */
	//public List getThresholds(KpiInstance k)throws EMFUserError;
	
	/**
	 * Returns a List of all the the Threshols of the KpiInstance
	 * 
	 * @param Integer resId, Integer kpiInstId, String endDate
	 * @return List of all the the Threshols of the KpiInstance
	 * @throws EMFUserError If an Exception occurred
	 */
	public String getKpiTrendXmlResult(Integer resId, Integer kpiInstId, Date endDate) throws SourceBeanException;
	
	public IDataSet getDsFromKpiId(Integer kpiId) throws EMFUserError; 
	
	/**
	 * Returns a List of all the the Threshols of the KpiInstance
	 * 
	 * @param Integer resId, Integer kpiInstId, String endDate
	 * @return List of all the the Threshols of the KpiInstance
	 * @throws EMFUserError If an Exception occurred
	 */
	public String getKpiTrendXmlResult(Integer resId, Integer kpiInstId, Date beginDate , Date endDate) throws SourceBeanException;

	/**
	 * Returns an Integer representing the seconds of the Periodicity with id periodicityId
	 * 
	 * @param Integer periodicityId 
	 * @return Integer representing the seconds of the Periodicity with id periodicityId
	 * @throws EMFUserError If an Exception occurred
	 */
	//public Integer getPeriodicitySeconds(Integer periodicityId)throws EMFUserError;
	
	/**
	 * Returns the Kpi of the referred id
	 * 
	 * @param id of the Kpi
	 * @return Kpi of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */	
	public Kpi loadKpiById(Integer id) throws EMFUserError ;
	
	/**
	 * Returns the Kpi Definition of the referred id
	 * 
	 * @param id of the Kpi
	 * @return Kpi of the referred id
	 * @throws EMFUserError If an Exception occurred
	 */		
	public Kpi loadKpiDefinitionById(Integer id) throws EMFUserError ;
	
	/**
	 * Returns the Resource of the referred id
	 * 
	 * @param id of the Resource
	 * @return Resource with the referred id
	 * @throws EMFUserError If an Exception occurred
	 */	
	//public Resource loadResourceById(Integer id) throws EMFUserError ;
	
	/**
	 * Returns the Resource of the referred id
	 * 
	 * @param id of the Resource
	 * @return Resource with the referred id
	 * @throws EMFUserError If an Exception occurred
	 */	
	//public Resource loadResourcesByNameAndModelInst(String resourceName) throws EMFUserError ;
	
	/**
	 * Returns the DatasetConfig for the KPI with id kpiId
	 * 
	 * @param kpiId of the KPI 
	 * @return DataSetConfig used to calculate the KPI with ID kpiId 
	 * @throws EMFUserError if an Exception occurs
	 */
	//public IDataSet getDsFromKpiId(Integer kpiId) throws EMFUserError;
	
	/**
	 * KpiValue valid for the the KpiInstance selected, for the resource selected, in the date selected 
	 * 
	 * @param KpiValue 
	 * @return KpiValue valid for the the KpiInstance selected, for the resource selected, in the date selected 
	 * @throws EMFUserError if an Exception occurs
	 */
	public KpiValue getKpiValue(Integer kpiInstanceId, Date d, Resource r) throws EMFUserError;
	
	/**
	 * The last KpiValue for the the KpiInstance selected, for the resource selected, in the date selected or before
	 * 
	 * @param KpiValue 
	 * @return The last KpiValue valid for the the KpiInstance selected, for the resource selected, in the date selected or before
	 * @throws EMFUserError if an Exception occurs
	 */
	public KpiValue getDisplayKpiValue(Integer kpiInstanceId, Date d, Resource r) throws EMFUserError;
	
	/**
	 * Returns True if the KPIInstance with id kpiInstID is under AlarmControl, false if it is not 
	 * 
	 * @param kpiInstID of the KPIInstance that we want to monitor
	 * @return Boolean that shows if the KPIInstance with id kpiInstID is under AlarmControl  
	 * @throws EMFUserError if an Exception occurs
	 */
	//public Boolean isKpiInstUnderAlramControl(Integer kpiInstID) throws EMFUserError;
	
	//public boolean hasActualValues(KpiInstance inst, Date d) throws EMFUserError ;
	
	/**
	 * For the specific KpiValue verifies if it is ok with every threshold and if not writes an alarm event in the AlarmEvent table so that later on an alarm will be sent
	 * 
	 * @param KpiValue 
	 *  
	 * @throws EMFUserError if an Exception occurs
	 */
	//public void isAlarmingValue(KpiValue value)	throws EMFUserError;
	
	/**
	 * Returns the ChartType of the specific KpiInstance (it could also be null)
	 * 
	 * @param kpiInstanceID 
	 * @return Returns the ChartType of the specific KpiInstance (it could also be null)
	 * @throws EMFUserError if an Exception occurs
	 */
	//public String getChartType(Integer kpiInstanceID) throws EMFUserError;
	
	/**
	 * Returns the list of Kpi.
	 * 
	 * @return the list of all Kpi.
	 * @throws EMFUserError if an Exception occurs
	 */
	public List loadKpiList() throws EMFUserError;
	
	public List loadKpiList(String fieldOrder, String typeOrder) throws EMFUserError;
	
    public List loadPagedKpiList(Integer offset, Integer fetchSize)throws EMFUserError ;
	
	public Integer countKpis()throws EMFUserError ;

	/*public void modifyResource(Resource resource) throws EMFUserError;

	public Integer insertResource(Resource toCreate) throws EMFUserError;
	
	public void setKpiInstanceFromKPI(KpiInstance kpiInstance, Integer kpiId) throws EMFUserError;

	public void deleteResource(Integer resourceId) throws EMFUserError;*/

	public void modifyKpi(Kpi kpi) throws EMFUserError;

	public Integer insertKpi(Kpi toCreate)throws EMFUserError;

	public boolean deleteKpi(Integer kpiId) throws EMFUserError;

	//public List loadResourcesList(String fieldOrder, String typeOrder)throws EMFUserError;
	

}
