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
package it.eng.spagobi.tools.dataset.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Chiara Chiarelli
 */
public class GuiGenericDataSet implements Serializable{

	private int dsId;	
	private String name=null;
	private String description=null;
	private String label=null;
	
	private GuiDataSetDetail activeDetail = null;
	private List<GuiDataSetDetail> nonActiveDetails = null;

	private String userIn=null;
	private String userUp=null;
	private String userDe=null;
	private String sbiVersionIn=null;
	private String sbiVersionUp=null;
	private String sbiVersionDe=null;
	private String metaVersion=null;
	private String organization=null;
	private Date timeIn = null;
	private Date timeUp = null;
	private Date timeDe = null;
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
	    return name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
	    this.name = name;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
	    return description;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
	    this.description = description;
	}
	
	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
	    return label;
	}
	
	/**
	 * Sets the label.
	 * 
	 * @param label the new label
	 */
	public void setLabel(String label) {
	    this.label = label;
	}
	
	/**
	 * Gets the ds id.
	 * 
	 * @return the ds id
	 */
	public int getDsId() {
		return dsId;
	}

	/**
	 * Sets the ds id.
	 * 
	 * @param dsId the new ds id
	 */
	public void setDsId(int dsId) {
		this.dsId = dsId;
	}

	public String getUserIn() {
		return userIn;
	}

	public void setUserIn(String userIn) {
		this.userIn = userIn;
	}

	public String getUserUp() {
		return userUp;
	}

	public void setUserUp(String userUp) {
		this.userUp = userUp;
	}

	public String getUserDe() {
		return userDe;
	}

	public void setUserDe(String userDe) {
		this.userDe = userDe;
	}

	public String getSbiVersionIn() {
		return sbiVersionIn;
	}

	public void setSbiVersionIn(String sbiVersionIn) {
		this.sbiVersionIn = sbiVersionIn;
	}

	public String getSbiVersionUp() {
		return sbiVersionUp;
	}

	public void setSbiVersionUp(String sbiVersionUp) {
		this.sbiVersionUp = sbiVersionUp;
	}

	public String getSbiVersionDe() {
		return sbiVersionDe;
	}

	public void setSbiVersionDe(String sbiVersionDe) {
		this.sbiVersionDe = sbiVersionDe;
	}

	public String getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(String metaVersion) {
		this.metaVersion = metaVersion;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}

	public Date getTimeUp() {
		return timeUp;
	}

	public void setTimeUp(Date timeUp) {
		this.timeUp = timeUp;
	}

	public Date getTimeDe() {
		return timeDe;
	}

	public void setTimeDe(Date timeDe) {
		this.timeDe = timeDe;
	}
	
	public GuiDataSetDetail getActiveDetail() {
		return activeDetail;
	}

	public void setActiveDetail(GuiDataSetDetail activeDetail) {
		this.activeDetail = activeDetail;
	}

	public List<GuiDataSetDetail> getNonActiveDetails() {
		return nonActiveDetails;
	}

	public void setNonActiveDetails(List<GuiDataSetDetail> nonActiveDetails) {
		this.nonActiveDetails = nonActiveDetails;
	}
}
