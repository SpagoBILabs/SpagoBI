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
package it.eng.spagobi.utilities.container;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class HttpServletRequestContainer extends AbstractContainer {
	HttpServletRequest request;
	
	public HttpServletRequestContainer(HttpServletRequest request) {
		setRequest(request);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean containsProperty(String propertyName) {
		return getProperty(propertyName) != null;
	}

	public Object getProperty(String propertyName) {
		return getRequest().getParameter( propertyName );
	}

	public void setProperty(String propertyName, Object propertyValue) {
		// TODO rise an unsupported operation exception		
	}
	
	
}
