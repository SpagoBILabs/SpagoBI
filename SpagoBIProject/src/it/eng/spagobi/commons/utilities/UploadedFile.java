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
/*
 * Created on 4-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.commons.utilities;

import java.io.Serializable;

/**
 * Defines an <code>UploadedFile</code> object.
 * 
 * @author Zoppello
 *
 */
public class UploadedFile implements Serializable {
	private byte[] fileContent = null;
	
	private String fileName = null;
	
	private long sizeInBytes;
	
	/**
	 * Gets the field name in form.
	 * 
	 * @return Returns the fieldNameInForm.
	 */
	public String getFieldNameInForm() {
		return fieldNameInForm;
	}
	
	/**
	 * Sets the field name in form.
	 * 
	 * @param fieldNameInForm The fieldNameInForm to set.
	 */
	public void setFieldNameInForm(String fieldNameInForm) {
		this.fieldNameInForm = fieldNameInForm;
	}
	
	/**
	 * Gets the file content.
	 * 
	 * @return Returns the fileContent.
	 */
	public byte[] getFileContent() {
		return fileContent;
	}
	
	/**
	 * Sets the file content.
	 * 
	 * @param fileContent The fileContent to set.
	 */
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	/**
	 * Gets the file name.
	 * 
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the file name.
	 * 
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Gets the size in bytes.
	 * 
	 * @return Returns the sizeInBytes.
	 */
	public long getSizeInBytes() {
		return sizeInBytes;
	}
	
	/**
	 * Sets the size in bytes.
	 * 
	 * @param sizeInBytes The sizeInBytes to set.
	 */
	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	private String fieldNameInForm = null;
}
