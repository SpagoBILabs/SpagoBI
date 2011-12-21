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
package it.eng.spagobi.commons.utilities.messages;

import it.eng.spago.base.ApplicationContainer;
import it.eng.spagobi.commons.constants.SpagoBIConstants;


public class MessageBuilderFactory {
	
	/**
	 * Gets the message builder.
	 * 
	 * @return the message builder
	 */
	public static IMessageBuilder getMessageBuilder() {
		ApplicationContainer spagoContext = ApplicationContainer.getInstance();
		IMessageBuilder msgBuilder = (IMessageBuilder)spagoContext.getAttribute(SpagoBIConstants.MESSAGE_BUILDER);
		if(msgBuilder==null) {
			msgBuilder = new MessageBuilder();
			spagoContext.setAttribute(SpagoBIConstants.MESSAGE_BUILDER, msgBuilder);
		}	
		return msgBuilder;
	}
	
}
