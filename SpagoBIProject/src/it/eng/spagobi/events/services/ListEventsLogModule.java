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
package it.eng.spagobi.events.services;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.list.basic.AbstractBasicListModule;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.paginator.basic.ListIFace;
import it.eng.spago.paginator.basic.PaginatorIFace;
import it.eng.spago.paginator.basic.impl.GenericList;
import it.eng.spago.paginator.basic.impl.GenericPaginator;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.util.StringUtils;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.services.DelegatedBasicListService;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.events.EventsManager;
import it.eng.spagobi.events.bo.EventLog;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * This class shows events' notification log 
 * 
 * @author Gioia
 *
 */			 
public class ListEventsLogModule extends AbstractBasicListModule {
	
	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.list.basic.IFaceBasicListService#getList(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
		RequestContainer requestContainer = getRequestContainer();
		IEngUserProfile profile = (IEngUserProfile) requestContainer.getSessionContainer().getPermanentContainer().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		 
		//Start writing log in the DB
		Session aSession =null;
		try {
			aSession = HibernateUtil.currentSession();
			//Connection jdbcConnection = aSession.connection();
			Connection jdbcConnection = HibernateUtil.getConnection(aSession);
			AuditLogUtilities.updateAudit(jdbcConnection,  profile, "activity.EventsMenu", null);
		} catch (HibernateException he) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		//End writing log in the DB
		
		EventsManager eventsManager = EventsManager.getInstance();		
		List firedEventsList = eventsManager.getRegisteredEvents(profile);
        SingletonConfig config = SingletonConfig.getInstance();
        String formatSB = config.getConfigValue("SPAGOBI.DATE-FORMAT.format");
        String format = (formatSB==null)?"": formatSB;
	    format = format.replaceAll("D", "d");
	    format = format.replaceAll("m", "M");
	    format = format.replaceAll("Y", "y");
		PaginatorIFace paginator = new GenericPaginator();
		Iterator it = firedEventsList.iterator();
		while (it.hasNext()) {
			EventLog eventLog = (EventLog) it.next();
			String rowSBStr = "<ROW ";
			rowSBStr += "		ID=\"" + eventLog.getId() + "\"";
			String date = StringUtils.dateToString(eventLog.getDate(), format);
			rowSBStr += "		DATE=\"" + date + "\"";
			rowSBStr += "		USER=\"" + eventLog.getUser() + "\"";
			String description = eventLog.getDesc();
			if (description != null) {
				description = GeneralUtilities.replaceInternationalizedMessages(description);
				description = description.replaceAll("<br/>", " ");
				if (description.length() > 50) description = description.substring(0, 50) + "...";
				description = description.replaceAll(">", "&gt;");
				description = description.replaceAll("<", "&lt;");
				description = description.replaceAll("\"", "&quot;");
			}
			rowSBStr += "		DESCRIPTION=\"" + (description != null ? description : "") + "\"";
			rowSBStr += " 		/>";
			SourceBean rowSB = SourceBean.fromXMLString(rowSBStr);
			paginator.addRow(rowSB);
		}
		ListIFace list = new GenericList();
		list.setPaginator(paginator);
		// filter the list 
		String valuefilter = (String) request.getAttribute(SpagoBIConstants.VALUE_FILTER);
		if (valuefilter != null) {
			String columnfilter = (String) request
					.getAttribute(SpagoBIConstants.COLUMN_FILTER);
			String typeFilter = (String) request
					.getAttribute(SpagoBIConstants.TYPE_FILTER);
			String typeValueFilter = (String) request
					.getAttribute(SpagoBIConstants.TYPE_VALUE_FILTER);
			list = DelegatedBasicListService.filterList(list, valuefilter, typeValueFilter, 
					columnfilter, typeFilter, getResponseContainer().getErrorHandler());
		}
		
		return list;
	}
}
