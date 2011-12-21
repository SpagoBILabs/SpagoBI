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

package it.eng.spagobi.utilities.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SpagoBIRuntimeException extends RuntimeException {
    
	/* 
	 * User oriented description of the exception. It is usually prompted to the user.
	 * Instead the message passed to the constructor is developer oriented and it should be just logged. 
	 */
	private String description;
	
	/*
	 * A list of possible solutions to the problem that have caused the exception
	 */
	private List hints;
	
	/**
	 * Builds a <code>SpagoBIRuntimeException</code>.
	 * 
	 * @param message Text of the exception
	 */
    public SpagoBIRuntimeException(String message) {
    	super(message);
    }
	
    /**
     * Builds a <code>SpagoBIRuntimeException</code>.
     * 
     * @param message Text of the exception
     * @param ex previous Throwable object
     */
    public SpagoBIRuntimeException(String message, Throwable ex) {
    	super(message, ex);
    }
    
    /**
     * Builds a <code>SpagoBIRuntimeException</code>.
     * 
     * @param ex previous Throwable object
     */
    public SpagoBIRuntimeException(Throwable ex) {
    	super(ex);
    }
    
    public String getRootCause() {
		String rootCause;		
		Throwable rootException;
		
		rootException = this;
		while(rootException.getCause() != null) {
			rootException = rootException.getCause();
		}
		
		rootCause = rootException.getMessage()!=null
			? rootException.getClass().getName() + ": " + rootException.getMessage()
			: rootException.getClass().getName();
		
		return rootCause;
	}
    
    public String getStackTraceDump() {
    	StringWriter buffer = new StringWriter();
    	this.printStackTrace(new PrintWriter(buffer));
    	return buffer.toString();
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List getHints() {
		if(hints == null) {
			hints = new ArrayList();
		}
		return hints;
	}

	public void addHint(String hint) {
		getHints().add(hint);
	}

}

