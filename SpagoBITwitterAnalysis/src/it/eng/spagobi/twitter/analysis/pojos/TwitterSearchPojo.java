/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.twitter.analysis.pojos;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterSearchPojo {

	private long searchID;
	private String label;
	private String keywords;
	private java.sql.Date creationDate;
	private java.sql.Timestamp lastActivationTime;
	private String frequency;
	private String type;

	public TwitterSearchPojo(long searchID, String label, String keywords, Date creationDate, Timestamp lastActivationTime, String frequency, String type) {

		this.searchID = searchID;
		this.label = label;
		this.keywords = keywords;
		this.creationDate = creationDate;
		this.lastActivationTime = lastActivationTime;
		this.frequency = frequency;
		this.type = type;
	}

	public long getSearchID() {
		return searchID;
	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public java.sql.Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(java.sql.Date creationDate) {
		this.creationDate = creationDate;
	}

	public java.sql.Timestamp getLastActivationTime() {
		return lastActivationTime;
	}

	public void setLastActivationTime(java.sql.Timestamp lastActivationTime) {
		this.lastActivationTime = lastActivationTime;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "TwitterSearchPojo [searchID=" + searchID + ", label=" + label + ", keywords=" + keywords + ", creationDate=" + creationDate + ", lastActivationTime="
				+ lastActivationTime + ", frequency=" + frequency + ", type=" + type + "]";
	}

}
