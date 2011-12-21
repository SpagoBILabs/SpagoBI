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

package it.eng.spagobi.kpi.alarm.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
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
public interface ISbiAlarmDAO extends ISpagoBIDao{

    public void insert(SbiAlarm item);
    
   // public void insert(Session session, SbiAlarm item);

    public Integer update(SbiAlarm item);
    
  //  public void update(Session session, SbiAlarm item);
	
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

