package it.eng.spagobi.kpi.config.bo;

public class KpiAlarmInstance {
	
	Integer kpiInstanceId = null;
	String kpiName = null;
	String kpiModelName = null;
	
	public Integer getKpiInstanceId() {
		return kpiInstanceId;
	}
	public void setKpiInstanceId(Integer kpiInstanceId) {
		this.kpiInstanceId = kpiInstanceId;
	}
	public String getKpiName() {
		return kpiName;
	}
	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}
	public String getKpiModelName() {
		return kpiModelName;
	}
	public void setKpiModelName(String kpiModelName) {
		this.kpiModelName = kpiModelName;
	}


}
