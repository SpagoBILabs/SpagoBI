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
package it.eng.spagobi.tools.importexport.bo;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.GeneralUtilities;

import java.io.FileInputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class AssociationFile implements Serializable{

	private String id = "";
	private String name = "";
	private String description = "";
	private long dateCreation = 0;
	
	static private Logger logger = Logger.getLogger(AssociationFile.class);
	
	/**
	 * Gets the date creation.
	 * 
	 * @return the date creation
	 */
	public long getDateCreation() {
		return dateCreation;
	}
	
	/**
	 * Sets the date creation.
	 * 
	 * @param dateCreation the new date creation
	 */
	public void setDateCreation(long dateCreation) {
		this.dateCreation = dateCreation;
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
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Checks if is valid content.
	 * 
	 * @param xmlStr the xml str
	 * 
	 * @return true, if is valid content
	 */
	public static boolean isValidContent(String xmlStr) {
		logger.debug("IN");
		try {
			if (xmlStr == null || xmlStr.trim().equals("")) throw new Exception("Empty content in input!");
			SourceBean associationSBtmp = SourceBean.fromXMLString(xmlStr);
			SourceBean roleAssSBtmp = (SourceBean)associationSBtmp.getAttribute("ROLE_ASSOCIATIONS");
			if(roleAssSBtmp==null) throw new Exception("Cannot recover ROLE_ASSOCIATIONS bean");
			SourceBean engineAssSBtmp = (SourceBean)associationSBtmp.getAttribute("ENGINE_ASSOCIATIONS");
			if(engineAssSBtmp==null) throw new Exception("Cannot recover ENGINE_ASSOCIATIONS bean");
			SourceBean connectionAssSBtmp = (SourceBean)associationSBtmp.getAttribute("DATA_SOURCE_ASSOCIATIONS");
			if(connectionAssSBtmp==null) throw new Exception("Cannot recover DATA_SOURCE_ASSOCIATIONS bean");
			return true;
		} catch (Exception e) {
			logger.error("Association file not valid: \n " + e.getMessage());
			return false;
		} finally {
			logger.debug("OUT");
		}
	}
	
	/**
	 * Checks if is valid content.
	 * 
	 * @param bytes the bytes
	 * 
	 * @return true, if is valid content
	 */
	public static boolean isValidContent(byte[] bytes) {
		logger.debug("IN");
		try {
			if (bytes == null || bytes.length == 0) return false;
			else return isValidContent(new String(bytes));
		} finally {
			logger.debug("OUT");
		}
	}
	
	/**
	 * Checks if is valid content.
	 * 
	 * @param fis the fis
	 * 
	 * @return true, if is valid content
	 */
	public static boolean isValidContent(FileInputStream fis) {
		logger.debug("IN");
		try {
			if (fis == null) return false;
			byte[] bytes = GeneralUtilities.getByteArrayFromInputStream(fis);
			return isValidContent(bytes);
		} finally {
			logger.debug("OUT");
		}
	}
	
}
