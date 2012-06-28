/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.exoaddins.modules;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dbaccess.sql.DataRow;
import it.eng.spago.dispatching.module.list.basic.AbstractBasicListModule;
import it.eng.spago.dispatching.service.RequestContextIFace;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.init.InitializerIFace;
import it.eng.spago.paginator.basic.ListIFace;
import it.eng.spago.paginator.basic.PaginatorIFace;
import it.eng.spago.paginator.basic.impl.GenericList;
import it.eng.spago.paginator.basic.impl.GenericPaginator;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.services.DelegatedBasicListService;
import it.eng.spagobi.exoaddins.Utilities;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;

public class ExoProfileAttributeManagerListUserModule extends AbstractBasicListModule {
	
	static private Logger logger = Logger.getLogger(ExoProfileAttributeManagerListUserModule.class);
	
	public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
		logger.debug("IN");
		PaginatorIFace paginator = new GenericPaginator();
		InitializerIFace serviceInitializer = (InitializerIFace) this;
		int pagedRows = 10;
		paginator.setPageSize(pagedRows);
		RequestContextIFace serviceRequestContext = (RequestContextIFace) this;		
		SourceBean rowsSourceBean = getExoUsers();
		if ((rowsSourceBean == null)) {
			EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.INFORMATION, 100));
		} else { 
			List rowsVector = null;
			rowsVector = rowsSourceBean.getAttributeAsList(DataRow.ROW_TAG);
			for (int i = 0; i < rowsVector.size(); i++) {
				paginator.addRow(rowsVector.get(i));
			}
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
					                                    columnfilter, typeFilter, 
					                                    serviceRequestContext.getErrorHandler());
		}
		logger.debug("OUT");
		return list;
	}

	
	
	
	private SourceBean getExoUsers() {
		logger.debug("IN");
		SourceBean rows = null;
		try{
			rows = new SourceBean("ROWS");
			PortalContainer container = PortalContainer.getInstance();	
			if(container==null) throw new Exception("Portal container not retrived");
			OrganizationService service = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
			if(service==null) throw new Exception("Organization service not retrived");
			UserHandler userHandler = service.getUserHandler();
			if(userHandler==null) throw new Exception("User handler component not retrived");
			GroupHandler groupHandler = service.getGroupHandler();
			if(groupHandler==null) throw new Exception("Group handler component not retrived");
			PageList pagelist = userHandler.getUserPageList(10);
			List allUser = pagelist.getAll();
			allUser = Utilities.getExoUserFiltered(allUser);
			Iterator iterUser = allUser.iterator();
			while(iterUser.hasNext()) {
				User user = (User)iterUser.next();
				String userName = user.getUserName();
				if(userName == null) userName = "";
				String firstName = user.getFirstName();
				if(firstName == null) firstName = "";
				String lastName = user.getLastName();
				if(lastName == null) lastName = "";
				String email = user.getEmail();
				if(email == null) email = "";
				SourceBean row = new SourceBean(DataRow.ROW_TAG);	
				row.setAttribute("UserName", userName);
				row.setAttribute("FirstName", firstName);
				row.setAttribute("LastName", lastName);
				row.setAttribute("Email", email);
				rows.setAttribute(row);
			}
		} catch (Exception e){
			logger.error("Error while loading exo user list ", e);
			rows = null;
		}
		logger.debug("OUT");
		return rows;	
	}
} 

