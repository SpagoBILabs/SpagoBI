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
package it.eng.spagobi.tools.dataset.exceptions;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.List;


/** This class is extended by  other classes representing particular datasetExceptions
 * 
 * @author gavardi
 *
 */

public class DatasetException extends SpagoBIRuntimeException {

	private static final long serialVersionUID = 1L;
	
	// this field is filled with a message for the user
	String userMessage;
	String fullMessage;
	
	public static final String USER_MESSAGE = "DataSet Exception";
	
	/**
	 * Builds a <code>SpagoBIRuntimeException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public DatasetException(String message) {
    	super(message);
    	this.userMessage = USER_MESSAGE;
    	this.fullMessage = message;    	
    }
	
    /**
     * Builds a <code>SpagoBIRuntimeException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public DatasetException(String message, Throwable ex) {
    	super(message, ex);
    	this.userMessage = USER_MESSAGE;
    	this.fullMessage = message;    
    }

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	
	public String getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

	

}
