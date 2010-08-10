package it.eng.spagobi.kpi.alarm.dao;

/**
 * Title: SpagoBI
 * Description: SpagoBI
 * Copyright: Copyright (c) 2008
 * Company: Xaltia S.p.A.
 * 
 * @author Enrico Cesaretti
 * @version 1.0
 */




import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmEvent;

import java.util.List;

import org.hibernate.Session;

/**
 * 
 * @see it.eng.spagobi.kpi.alarm.metadata.SbiAlarmEvent
 * @author Enrico Cesaretti
 */
public interface ISbiAlarmEventDAO {

    public void insert(SbiAlarmEvent item);
    
    
    public void insert(Session session, SbiAlarmEvent item);

    public void update(SbiAlarmEvent item);
    
    public void update(Session session, SbiAlarmEvent item);
    
    public void delete(SbiAlarmEvent item);
    
    public void delete(Session session, SbiAlarmEvent item);

    public void delete(Integer id);
    
    
    public void delete(Session session, Integer id);
	
    public SbiAlarmEvent findById(Integer id);

    public List<SbiAlarmEvent> findAll();
    
    public List<SbiAlarmEvent> findActive();
}

