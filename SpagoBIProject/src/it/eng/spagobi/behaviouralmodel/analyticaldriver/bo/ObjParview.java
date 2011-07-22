/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
