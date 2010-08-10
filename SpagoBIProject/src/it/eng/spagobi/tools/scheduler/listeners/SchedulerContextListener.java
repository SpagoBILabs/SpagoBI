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
package it.eng.spagobi.tools.scheduler.listeners;

import it.eng.spagobi.commons.utilities.SpagoBITracer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerContextListener implements ServletContextListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		SpagoBITracer.debug("SPAGOBI", this.getClass().getName(), "contextDestroyed", 
				"Entering method ...");
		// shutdown Quartz scheduler
		Scheduler scheduler = null;
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			SpagoBITracer.critical("SPAGOBI", this.getClass().getName(), "contextDestroyed", 
					"Error while retrieving scheduler", e);
		}
		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				SpagoBITracer.critical("SPAGOBI", this.getClass().getName(), "contextDestroyed", 
						"Error while stopping scheduler", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// does nothing
	}

}
