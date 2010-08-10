/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.spagoext;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.presentation.PublisherDispatcherIFace;


// TODO: Auto-generated Javadoc
/**
 * The Class DynamicForwardPublisher.
 * 
 * @author Andrea Zoppello
 * This class implements a SpagoBublisher that determine next Publisher assking the
 * service request for a parameter called PUBLISHER_NAME
 */
public class DynamicForwardPublisher implements PublisherDispatcherIFace {
	
	/**
	 * Instantiates a new dynamic forward publisher.
	 */
	public DynamicForwardPublisher() {
		super();

	}

	/* (non-Javadoc)
	 * @see it.eng.spago.presentation.PublisherDispatcherIFace#getPublisherName(it.eng.spago.base.RequestContainer, it.eng.spago.base.ResponseContainer)
	 */
	public String getPublisherName(RequestContainer request,
			ResponseContainer response) {

		String navPar = (String) request.getServiceRequest().getAttribute(
				"PUBLISHER_NAME");
		if (navPar != null) {
			return navPar;
		} else {
			return "SERVICE_ERROR_PUBLISHER";
		}
	}
}

