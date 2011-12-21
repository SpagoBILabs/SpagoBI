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
package it.eng.spagobi.behaviouralmodel.analyticaldriver.bo;

import java.io.Serializable;

/**
 * Defines a Business Intelligence object
 */
public class ObjParview implements Serializable {

	private Integer objParId;
    private Integer objParFatherId;
    private Integer prog;
    private String operation;
    private String compareValue;
    private String viewLabel;
    
    
    
	public Integer getObjParId() {
		return objParId;
	}
	public void setObjParId(Integer objParId) {
		this.objParId = objParId;
	}
	public Integer getObjParFatherId() {
		return objParFatherId;
	}
	public void setObjParFatherId(Integer objParFatherId) {
		this.objParFatherId = objParFatherId;
	}
	public Integer getProg() {
		return prog;
	}
	public void setProg(Integer prog) {
		this.prog = prog;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getCompareValue() {
		return compareValue;
	}
	public void setCompareValue(String compareValue) {
		this.compareValue = compareValue;
	}
	public String getViewLabel() {
		return viewLabel;
	}
	public void setViewLabel(String viewLabel) {
		this.viewLabel = viewLabel;
	}
    


}
