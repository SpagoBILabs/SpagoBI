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
package it.eng.spagobi.commons.bo;

import java.io.Serializable;

/**
 * Defines a <code>TemplateVersion</code> object.
 * 
 * @author sulis
 */
public class TemplateVersion implements Serializable {

	String versionName = null;
	String nameFileTemplate = null;
	String dataLoad = null;
	
	/**
	 * Gets the data load.
	 * 
	 * @return the template version data load
	 */
	public String getDataLoad() {
		return dataLoad;
	}
	
	/**
	 * Sets the data load.
	 * 
	 * @param dataLoad the template version data load to set
	 */
	public void setDataLoad(String dataLoad) {
		this.dataLoad = dataLoad;
	}
	
	/**
	 * Gets the name file template.
	 * 
	 * @return the template version name file
	 */
	public String getNameFileTemplate() {
		return nameFileTemplate;
	}
	
	/**
	 * Sets the name file template.
	 * 
	 * @param nameFileTemplate the template version name file to set
	 */
	public void setNameFileTemplate(String nameFileTemplate) {
		this.nameFileTemplate = nameFileTemplate;
	}
	
	/**
	 * Gets the version name.
	 * 
	 * @return the template version version name
	 */
	public String getVersionName() {
		return versionName;
	}
	
	/**
	 * Sets the version name.
	 * 
	 * @param versionName the template version version name to set
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
}
