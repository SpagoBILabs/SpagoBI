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

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SpagoBIResponseContainer 
extends AbstractContainer 
implements IWriteOnlyContainer {
   
	private Logger logger = Logger.getLogger(SpagoBIResponseContainer.class);
	
	private SourceBean response;
	
	public SpagoBIResponseContainer(SourceBean response) {
		if (response == null) {
			logger.error("SourceBean request is null. " +
					"Cannot initialize " + this.getClass().getName() + "  instance");
			throw new ExceptionInInitializerError("SourceBean request in input is null");
		}
		setResponse( response );
	}
	
	private void setResponse(SourceBean r) {
		response = r;
	}
	
	private SourceBean getResponse() {
		return response;
	}
	
	public Object get(String key) {
		return getResponse().getAttribute(key);
	}

	public List getKeys() {
		logger.debug("IN");
		List toReturn = new ArrayList();
		List list = getResponse().getContainedAttributes();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			SourceBeanAttribute sba = (SourceBeanAttribute) it.next();
			String key = sba.getKey();
			toReturn.add(key);
		}
		logger.debug("OUT");
		return toReturn;
	}

	public void remove(String key) {
		try {
			getResponse().delAttribute(key);
		} catch (SourceBeanException e) {
			logger.error(e);
		}
	}

	public void set(String key, Object value) {
		try {
			getResponse().setAttribute(key, value);
		} catch (SourceBeanException e) {
			logger.error(e);
		}
		
	}
}
