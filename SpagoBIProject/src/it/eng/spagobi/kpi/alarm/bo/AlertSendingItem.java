/**
 * 
 */
package it.eng.spagobi.kpi.alarm.bo;

import java.io.Serializable;

import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmEvent;

/**
 * @author Enrico Cesaretti
 *         e.cesaretti@xaltia.it
 */
public class AlertSendingItem implements Serializable{

    private SbiAlarm sbiAlarm;
    private SbiAlarmEvent sbiAlarmEvent;
    
    public AlertSendingItem(){}
    
    public AlertSendingItem(SbiAlarm sbiAlarm, SbiAlarmEvent sbiAlarmEvent){
	this.sbiAlarm=sbiAlarm;
	this.sbiAlarmEvent=sbiAlarmEvent;
    }
    
    public SbiAlarm getSbiAlarm() {
        return sbiAlarm;
    }
    public void setSbiAlarm(SbiAlarm sbiAlarm) {
        this.sbiAlarm = sbiAlarm;
    }
   
    public SbiAlarmEvent getSbiAlarmEvent() {
        return sbiAlarmEvent;
    }
    public void setSbiAlarmEvent(SbiAlarmEvent sbiAlarmEvent) {
        this.sbiAlarmEvent = sbiAlarmEvent;
    }
    
    
}
