/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
/*
 * Created on 21-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.commons.services;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

import java.util.List;

public class ViewBuilderModule extends AbstractModule {

	/**
	 * Service.
	 * 
	 * @param request the request
	 * @param response the response
	 * 
	 * @throws Exception the exception
	 * 
	 * @see it.eng.spago.dispatching.action.AbstractHttpAction#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		String viewCode = (String)request.getAttribute("viewCode");
		ConfigSingleton configuration = ConfigSingleton.getInstance();
		SourceBean viewSB = (SourceBean)configuration.getFilteredSourceBeanAttribute("MENU.VIEW", "code", viewCode);
		List containers = viewSB.getAttributeAsList("CONTAINERS.CONTAINER");
		String widthViewStr = (String)viewSB.getAttribute("width");
		Integer widthView = new Integer(widthViewStr); 
		String heightViewStr = (String)viewSB.getAttribute("height");
		Integer heightView = new Integer(heightViewStr); 
		response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ViewBuilder");
		response.setAttribute("CONTAINERS_LIST", containers);
		response.setAttribute("VIEW_HEIGHT", heightView);
		response.setAttribute("VIEW_WIDTH", widthView);
	}

}
