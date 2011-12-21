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
package it.eng.spagobi.utilities;

public class GenericSavingException extends Exception {
	
	private String description;
	
	/**
	 * Instantiates a new generic saving exception.
	 */
	public GenericSavingException() {
		super();
	}
	
	/**
	 * Instantiates a new generic saving exception.
	 * 
	 * @param msg the msg
	 */
	public GenericSavingException(String msg) {
		super();
		this.setLocalizedMessage(msg);
	}
	
    /* (non-Javadoc)
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return description;
    }
    
    /**
     * Sets the localized message.
     * 
     * @param msg the new localized message
     */
    public void setLocalizedMessage(String msg) {
        this.description = msg;
    }
}
