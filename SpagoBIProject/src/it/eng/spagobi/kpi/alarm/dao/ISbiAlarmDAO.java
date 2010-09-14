/**
 * Title: SpagoBI
 * Description: SpagoBI
 * Copyright: Copyright (c) 2008
 * Company: Xaltia S.p.A.
 * 
 * @author Enrico Cesaretti
 * @version 1.0
 */

package it.eng.spagobi.kpi.alarm.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.alarm.bo.Alarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.config.bo.KpiValue;

import java.util.List;

import org.hibernate.Session;

/**
 * 
 * @see it.eng.spagobi.kpi.alarm.metadata.SbiAlarm
 * @author Enrico Cesaretti
 */
public interface ISbiAlarmDAO{

    public void insert(SbiAlarm item);
    
    public void insert(Session session, SbiAlarm item);

    public Integer update(SbiAlarm item);
    
    public void update(Session session, SbiAlarm item);
	
    public void delete(SbiAlarm item);
    
    public void delete(Session session, SbiAlarm item);

    public void delete(Integer id);
    
    public void delete(Session session, Integer id);
	
    public SbiAlarm findById(Integer id);

    public List<SbiAlarm> findAll();

	public List<Alarm> loadAllByKpiInstId(Integer kpiInstanceId)  throws EMFUserError;

	public void isAlarmingValue(KpiValue value) throws EMFUserError;
	
	public List<SbiAlarm> loadPagedAlarmsList(Integer offset, Integer fetchSize)throws EMFUserError;
	
	public Integer countAlarms()throws EMFUserError;
}

