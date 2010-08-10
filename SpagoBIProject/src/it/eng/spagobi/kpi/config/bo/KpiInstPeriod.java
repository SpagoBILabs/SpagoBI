package it.eng.spagobi.kpi.config.bo;

import java.io.Serializable;

public class KpiInstPeriod implements Serializable{

	Integer id;
	Integer kpiInstId;
	Integer periodicityId;
	Boolean defaultValue;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getKpiInstId() {
		return kpiInstId;
	}
	public void setKpiInstId(Integer kpiInstId) {
		this.kpiInstId = kpiInstId;
	}
	public Integer getPeriodicityId() {
		return periodicityId;
	}
	public void setPeriodicityId(Integer periodicityId) {
		this.periodicityId = periodicityId;
	}
	public Boolean getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}


