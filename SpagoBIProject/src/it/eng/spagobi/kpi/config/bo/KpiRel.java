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
package it.eng.spagobi.kpi.config.bo;

import java.io.Serializable;

public class KpiRel implements Serializable{
	
	private Integer kpiRelId =null;
	private Integer kpiFatherId = null;
	private Integer kpiChildId =null;
	private String parameter = null;
	private Kpi kpiChild =null;
	
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

	public Kpi getKpiChild() {
		return kpiChild;
	}
	public void setKpiChild(Kpi kpiChild) {
		this.kpiChild = kpiChild;
	}

}
