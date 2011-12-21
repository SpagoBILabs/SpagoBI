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
package it.eng.spagobi.container;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A wrapper of the Spago SourceBean object. 
 * Inherits all it.eng.spagobi.container.AbstractContainer utility methods.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SpagoBISourceBeanContainer 
	extends AbstractContainer 
	implements IReadOnlyContainer {

	static private Logger logger = Logger.getLogger(SpagoBISourceBeanContainer.class);
	
	private SourceBean sourceBean;
	
	public SpagoBISourceBeanContainer(SourceBean sb) {
		if (sb == null) {
			logger.error("SourceBean is null. " +
					"Cannot initialize " + this.getClass().getName() + "  instance");
			throw new ExceptionInInitializerError("SourceBean in input is null");
		}
		setSourceBean( sb );
	}
	
	private void setSourceBean(SourceBean sb) {
		sourceBean = sb;
	}
	
	public SourceBean getSourceBean() {
		return sourceBean;
	}
	
	public Object get(String key) {
		return getSourceBean().getAttribute(key);
	}

	public List getKeys() {
		logger.debug("IN");
		List toReturn = new ArrayList();
		List list = getSourceBean().getContainedAttributes();
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
			getSourceBean().delAttribute(key);
		} catch (SourceBeanException e) {
			logger.error(e);
		}
	}

	public void set(String key, Object value) {
		try {
			getSourceBean().setAttribute(key, value);
		} catch (SourceBeanException e) {
			logger.error(e);
		}
		
	}

}
