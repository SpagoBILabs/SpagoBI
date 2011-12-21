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
package it.eng.spagobi.utilities.engines;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class EngineVersion {
	String major;
	String minor;
	String revision;
	String codename;
	
	public EngineVersion(String major, String minor, String revision, String codename) {
		this.setMajor(major);
		this.setMinor(minor);
		this.setRevision(revision);
		this.setRevision(revision);
	}
	
	public String toString() {
		return getMajor() + "." + getMinor() + "." + getRevision();
	}



	public String getMajor() {
		return major;
	}



	public void setMajor(String major) {
		this.major = major;
	}



	public String getMinor() {
		return minor;
	}



	public void setMinor(String minor) {
		this.minor = minor;
	}



	public String getRevision() {
		return revision;
	}



	public void setRevision(String revision) {
		this.revision = revision;
	}



	public String getCodename() {
		return codename;
	}



	public void setCodename(String codename) {
		this.codename = codename;
	}
	
}
