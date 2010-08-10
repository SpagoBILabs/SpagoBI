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
package it.eng.spagobi.commons.presentation;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.presentation.PublisherDispatcherIFace;

/**
 * @author zoppello
 *
 * This publisher is useful as utilities to forward to a publisher defined in  service request
 * 
 * To use this publisher declare action as follow
 * 
 *  <ACTION name="START_ACTION" class="it.eng.spago.dispatching.action.util.PublishAction" scope="REQUEST">
 *		<CONFIG></CONFIG>
 *	</ACTION>
 * 
 * and associate in publisher with dynamic publisher in file presentation.xml
 * 
 * <MAPPING business_type="ACTION" business_name="PUBLISH_ACTION" publisher_name="DYN_FORWARD_PUBLISHER"/>
 * 
 *	where DYN_FORWARD_PUBLISHER is defined in publicher.xml file
 *
 *	<PUBLISHER name="DYN_FORWARD_PUBLISHER">
 *		<RENDERING channel="HTTP" type="JAVA" mode="">
 *			<RESOURCES>
 *				<ITEM prog="0"
 *					resource="it.eng.spagoextensions.DynamicForwardPublisher" />
 *			</RESOURCES>
 *		</RENDERING>
 *
 *		<RENDERING channel="PORTLET" type="JAVA" mode="">
 *			<RESOURCES>
 *				<ITEM mode="VIEW"
 *					resource="it.eng.spagoextensions.DynamicForwardPublisher" />
 *				<ITEM mode="EDIT"
 *					resource="it.eng.spagoextensions.DynamicForwardPublisher" />
 *			</RESOURCES>
 *		</RENDERING>
 *	</PUBLISHER>
 * 
 */
public class DynamicForwardPublisher implements PublisherDispatcherIFace {
	
	/**
	 * Class constructor.
	 */
	public DynamicForwardPublisher() {
		super();

	}
	
	/**
	 * Given the request at input, gets the name of the reference publisher,driving
	 * the execution into the correct jsp page, or jsp error page, if any error occurred.
	 * 
	 * @param request The request container object containing all request information
	 * @param response The response container object containing all response information
	 * 
	 * @return A string representing the name of the correct publisher, which will
	 * call the correct jsp reference.
	 */
	public String getPublisherName(RequestContainer request,
			ResponseContainer response) {

		String publisherName = (String) request.getServiceRequest().getAttribute(
				"PUBLISHER_NAME");
		
		if (publisherName != null) {
			return publisherName;
		} else {
			return "SERVICE_ERROR_PUBLISHER";
		}
	}
}

