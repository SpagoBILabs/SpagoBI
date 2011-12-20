/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.commons.services;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.SQLCommand;
import it.eng.spago.dbaccess.sql.result.DataResult;
import it.eng.spago.dbaccess.sql.result.ScrollableDataResult;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.paginator.basic.ListIFace;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AbstractHibernateConnectionCheckListModule extends
		AbstractBasicCheckListModule {

	/* (non-Javadoc)
	 * @see it.eng.spagobi.commons.services.AbstractBasicCheckListModule#createCheckedObjectMap(it.eng.spago.base.SourceBean)
	 */
	public void createCheckedObjectMap(SourceBean request) throws Exception {
		checkedObjectsMap = new HashMap();

		// get CHECKED_QUERY query parameters

		String[] parameters = getQueryParameters("CHECKED_QUERY", request);

		// get CHECKED_QUERY statment
		String statement = getQueryStatement("CHECKED_QUERY", parameters);

		Session aSession = null;
		Transaction tx = null;
		
		// exec CHECKED_QUERY
		ScrollableDataResult scrollableDataResult = null;
		SQLCommand sqlCommand = null;
		DataConnection dataConnection = null;
		DataResult dataResult = null;
		try {
			aSession = HibernateUtil.currentSession();
			tx = aSession.beginTransaction();
			Connection jdbcConnection = aSession.connection();
			dataConnection = DelegatedHibernateConnectionListService.getDataConnection(jdbcConnection);
        	sqlCommand = dataConnection.createSelectCommand(statement);
        	dataResult = sqlCommand.execute();
        	scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean chekedObjectsBean = scrollableDataResult.getSourceBean();
			List checkedObjectsList = chekedObjectsBean
					.getAttributeAsList("ROW");
			for (int i = 0; i < checkedObjectsList.size(); i++) {
				SourceBean objects = (SourceBean) checkedObjectsList.get(i);
				String key = getObjectKey(objects);
				checkedObjectsMap.put(key, key);
			}
//			tx.commit();
		} catch (HibernateException he) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, 
		            this.getClass().getName(), 
		            "execCheckedQuery", 
		            he.getMessage());
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} catch (Exception e) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, this.getClass()
					.getName(), "createCheckedObjectMap", e.getMessage(), e);
		} finally {
			if (aSession != null) {
				if (aSession.isOpen()) aSession.close();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.commons.services.AbstractBasicCheckListModule#getList(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
		return DelegatedHibernateConnectionListService.getList(this, request, response);
	} 
	
}
