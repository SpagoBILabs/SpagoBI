package it.eng.spagobi.kpi.config.bo;

import java.io.Serializable;

public class KpiDocuments implements Serializable{

	Integer kpiDocId;
	Integer KpiId;
	Integer biObjId;
	String biObjLabel;
	
	public Integer getKpiDocId() {
		return kpiDocId;
	}
	public void setKpiDocId(Integer kpiDocId) {
		this.kpiDocId = kpiDocId;
	}
	public Integer getKpiId() {
		return KpiId;
	}
	public void setKpiId(Integer kpiId) {
		KpiId = kpiId;
	}
	public Integer getBiObjId() {
		return biObjId;
	}
	public void setBiObjId(Integer biObjId) {
		this.biObjId = biObjId;
	}
	public String getBiObjLabel() {
		return biObjLabel;
	}
	public void setBiObjLabel(String biObjLabel) {
		this.biObjLabel = biObjLabel;
	}
	

	
}
