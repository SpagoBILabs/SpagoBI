package it.eng.spagobi.kpi.config.bo;

import java.io.Serializable;

public class KpiRel implements Serializable{
	
	private Integer kpiRelId =null;
	private Integer kpiFatherId = null;
	private Integer kpiChildId =null;
	private String childKpiName =null;
	private String parameter = null;
	public Integer getKpiRelId() {
		return kpiRelId;
	}
	public void setKpiRelId(Integer kpiRelId) {
		this.kpiRelId = kpiRelId;
	}
	public Integer getKpiFatherId() {
		return kpiFatherId;
	}
	public void setKpiFatherId(Integer kpiFatherId) {
		this.kpiFatherId = kpiFatherId;
	}
	public Integer getKpiChildId() {
		return kpiChildId;
	}
	public void setKpiChildId(Integer kpiChildId) {
		this.kpiChildId = kpiChildId;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getChildKpiName() {
		return childKpiName;
	}
	public void setChildKpiName(String childKpiName) {
		this.childKpiName = childKpiName;
	}

}
