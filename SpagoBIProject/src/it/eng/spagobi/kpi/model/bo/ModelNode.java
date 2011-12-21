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
package it.eng.spagobi.kpi.model.bo;

import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.tools.udp.bo.UdpValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelNode implements Serializable{
	
	Boolean isRoot = null;
	String name = null;
	String code = null;
	String descr = null;
	String type = null;
	ModelNode father = null;
	List children = null;// List of ModelNodes children
	Kpi kpiAssociated = null;
	Integer id = null;
	
	List udpValues = new ArrayList<UdpValue>();
	
	public List getUdpValues() {
		return udpValues;
	}

	public void setUdpValues(List udpValues) {
		this.udpValues = udpValues;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ModelNode() {
		super();
		List children = new ArrayList();
		isRoot = false ;
	}

	public Boolean getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(Boolean isRoot) {
		this.isRoot = isRoot;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ModelNode getFather() {
		return father;
	}

	public void setFather(ModelNode father) {
		this.father = father;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}

	public Kpi getKpiAssociated() {
		return kpiAssociated;
	}

	public void setKpiAssociated(Kpi kpiAssociated) {
		this.kpiAssociated = kpiAssociated;
	}
	

}
