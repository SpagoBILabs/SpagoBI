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
package it.eng.spago.navigation;

import it.eng.spago.base.SourceBean;

public class MarkedRequest {

	private SourceBean request;
	
	private String mark;
	
	public MarkedRequest (SourceBean request, String mark) {
		this.request = request;
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public SourceBean getRequest() {
		return request;
	}

	public void setRequest(SourceBean request) {
		this.request = request;
	}
	
	public String toString() {
		String toReturn = "";
		toReturn += "Request SourceBean " + (request == null ? "null." : " = " + request.toString());
		toReturn += "Request Mark = '" + (mark == null ? "" : mark) + "'";
		
		return toReturn;
	}
	
}
