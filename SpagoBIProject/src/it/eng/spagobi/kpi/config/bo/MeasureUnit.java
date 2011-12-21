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

public class MeasureUnit implements Serializable{

	Integer id=null;
	String name=null;
	String scaleCd=null;
	String scaleNm=null;
	Integer scaleTypeId=null;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScaleCd() {
		return scaleCd;
	}
	public void setScaleCd(String scaleCd) {
		this.scaleCd = scaleCd;
	}
	public String getScaleNm() {
		return scaleNm;
	}
	public void setScaleNm(String scaleNm) {
		this.scaleNm = scaleNm;
	}
	public Integer getScaleTypeId() {
		return scaleTypeId;
	}
	public void setScaleTypeId(Integer scaleTypeId) {
		this.scaleTypeId = scaleTypeId;
	}

}
