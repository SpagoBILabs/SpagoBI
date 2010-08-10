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
package it.eng.spagobi.container;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SpagoBIServletRequestContainer 
	extends AbstractContainer implements IReadOnlyContainer {

static private Logger logger = Logger.getLogger(SpagoBIRequestContainer.class);
	
	ServletRequest request;
	
	public SpagoBIServletRequestContainer(ServletRequest request) {
		if (request == null) {
			logger.error("ServletRequest is null. " +
					"Cannot initialize " + this.getClass().getName() + "  instance");
			throw new ExceptionInInitializerError("ServletRequest request in input is null");
		}
		setRequest( request );
	}

	private ServletRequest getRequest() {
		return request;
	}

	private void setRequest(ServletRequest request) {
		this.request = request;
	}
	
	public Object get(String key) {
		return getRequest().getParameter(key);
	}

	public List getKeys() {
		return Collections.list( getRequest().getParameterNames() );
	}

	public void remove(String key) {
		throw new UnsupportedOperationException ("Impossible to write in a ReadOnlyContainer");	
	}

	public void set(String key, Object value) {
		throw new UnsupportedOperationException ("Impossible to write in a ReadOnlyContainer");	
	}
}
