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
package it.eng.spagobi.workflow.publishers;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.presentation.PublisherDispatcherIFace;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class AcceptActivityPublisher implements PublisherDispatcherIFace {

	/* (non-Javadoc)
	 * @see it.eng.spago.presentation.PublisherDispatcherIFace#getPublisherName(it.eng.spago.base.RequestContainer, it.eng.spago.base.ResponseContainer)
	 */
	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		JbpmContext jbpmContext = null;
		try {
			SourceBean request = requestContainer.getServiceRequest();
	    	JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
	    	jbpmContext = jbpmConfiguration.createJbpmContext();
	    	String activityKeyIdStr = (String) request.getAttribute("ActivityKey");
			long activityKeyId = Long.valueOf(activityKeyIdStr).longValue();
			TaskInstance taskInstance = jbpmContext.getTaskInstance(activityKeyId);
			if(taskInstance.getStart()==null) {
				taskInstance.start();
			}
			String publisherName = taskInstance.getVariable("spago_handler").toString(); 
			return publisherName;
		} finally {
	    	if (jbpmContext != null) {
	    		jbpmContext.close();
	    	}
		}
	}

}
