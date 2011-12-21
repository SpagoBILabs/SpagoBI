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
package it.eng.spagobi.tools.scheduler.presentation;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.presentation.GenericPublisher;

public class LovLookupAjaxPublisher extends GenericPublisher {
	
	/* (non-Javadoc)
	 * @see it.eng.spago.presentation.PublisherDispatcherIFace#getPublisherName(it.eng.spago.base.RequestContainer, it.eng.spago.base.ResponseContainer)
	 */
	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		// get the module response
		SourceBean moduleResponse = (SourceBean)responseContainer.getServiceResponse().getAttribute("LovLookupAjaxModule");
		// return publisher
		return getPublisherName(requestContainer, responseContainer, moduleResponse);
	}
}
