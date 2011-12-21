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
package it.eng.spagobi.tools.udp.bo;

/**
* @author Antonella Giachino (antonella.giachino@eng.it)
*/

import java.io.Serializable;
import java.util.Date;

public class Udp implements Serializable {
	
	private Integer udpId=null;
    private String label=null;
    private String name=null;
    private String description=null;
    private Integer dataTypeId=null;
    private Integer familyId=null;
	private Boolean multivalue=null;

	private String dataTypeValeCd = null;
	
	/**
	 * @return the udpId
	 */
	public Integer getUdpId() {
		return udpId;
	}
	/**
	 * @param udpId the udpId to set
	 */
	public void setUdpId(Integer udpId) {
		this.udpId = udpId;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the dataTypeId
	 */
	public Integer getDataTypeId() {
		return dataTypeId;
	}
	/**
	 * @param dataTypeId the dataTypeId to set
	 */
	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}
	/**
	 * @return the familyId
	 */
	public Integer getFamilyId() {
		return familyId;
	}
	/**
	 * @param familyId the familyId to set
	 */
	public void setFamilyId(Integer familyId) {
		this.familyId = familyId;
	}
	/**
	 * @return the multivalue
	 */
	public Boolean getMultivalue() {
		return multivalue;
	}
	/**
	 * @param multivalue the multivalue to set
	 */
	public void setMultivalue(Boolean multivalue) {
		this.multivalue = multivalue;
	}
	public String getDataTypeValeCd() {
		return dataTypeValeCd;
	}
	public void setDataTypeValeCd(String dataTypeValeCd) {
		this.dataTypeValeCd = dataTypeValeCd;
	}
	
	
	
}
