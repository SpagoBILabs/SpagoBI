/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.exceptions;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;

import java.util.List;


/** This class is extended by  other classes representing particular datasetExceptions
 * 
 * @author gavardi
 *
 */

public class DatasetException extends EMFUserError {

	String message;
	Throwable throwable;
	Object additionalInfo;
	SourceBean sourceBean; 
	String severity;
	String description; 
	String category;
 // this field is filled with a message for the user
	String userMessage;
	
	public DatasetException(String severity, int code, List params) {
		super(severity, code, params);
	}

	public DatasetException(String severity, int code, Exception e) {
		super(severity, code);
		setStackTrace(e.getStackTrace());
		message = e.getMessage();
		throwable = e.getCause();		
	}

	public DatasetException(String severity, int code, EMFInternalError e) {
		super(severity, code);
		e.getAdditionalInfo();
		additionalInfo = e.getAdditionalInfo();
		category =e.getCategory();
		description = e.getDescription();
		severity = e.getSeverity();
		sourceBean = e.getSourceBean();
		setStackTrace(e.getStackTrace());
		message = e.getMessage();
		throwable = e.getCause();		
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Object getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Object additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public SourceBean getSourceBean() {
		return sourceBean;
	}

	public void setSourceBean(SourceBean sourceBean) {
		this.sourceBean = sourceBean;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	

}
