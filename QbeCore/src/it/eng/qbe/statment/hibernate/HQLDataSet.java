/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.statment.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.statment.AbstractJPADataSet;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.common.dataproxy.JDBCSharedConnectionDataProxy;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class HQLDataSet extends AbstractJPADataSet {

	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(HQLDataSet.class);
    
	
	public HQLDataSet(HQLStatement statement) {
		super(statement);
		
	}
	
	public void loadData(int offset, int fetchSize, int maxResults) throws EMFUserError, EMFInternalError {
		Session session = null;
		org.hibernate.Query hibernateQuery;
		int resultNumber;
		boolean overflow;
		
		try{		
			session = ((IHibernateDataSource)statement.getDataSource()).getSessionFactory().openSession();
			
			// execute query
			hibernateQuery = session.createQuery( statement.getQueryString() );	
			resultNumber = getResultNumber(hibernateQuery, session);
			logger.info("Number of fetched records: " + resultNumber + " for query " + statement.getQueryString());
			overflow = (maxResults > 0) && (resultNumber >= maxResults);
			
			List result = null;
			
			if (overflow && abortOnOverflow) {
				// does not execute query
				result = new ArrayList();
			} else {
				offset = offset < 0 ? 0 : offset;
				if(maxResults > 0) {
					fetchSize = (fetchSize > 0)? Math.min(fetchSize, maxResults): maxResults;
				}
				logger.debug("Executing query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize);
				hibernateQuery.setFirstResult(offset);
				if(fetchSize > 0) hibernateQuery.setMaxResults(fetchSize);			
				result = hibernateQuery.list();
				logger.debug("Query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize + " executed");
			}	
			
			dataStore = toDataStore(result);
			dataStore.getMetaData().setProperty("resultNumber", resultNumber);						
		} finally {
			if (session != null && session.isOpen())
			session.close();
		}		
	}
	
	private int getResultNumber(org.hibernate.Query hibernateQuery, Session session) throws EMFUserError, EMFInternalError {
		int resultNumber = 0;
		try {
			resultNumber = getResultNumberUsingInlineView(hibernateQuery, session);
		} catch (Exception e) {
			logger.warn("Error getting result number using inline view!!", e);
			resultNumber = getResultNumberUsingScrollableResults(hibernateQuery, session);
		}
		return resultNumber;
	}
	
	private int getResultNumberUsingInlineView(org.hibernate.Query hibernateQuery, Session session) throws Exception {
		int resultNumber = 0;
		logger.debug("IN");
		String sqlQuery = "SELECT COUNT(*) FROM (" + statement.getSqlQueryString() + ") temptable";
		logger.debug("Executing query " + sqlQuery + " ...");
		JDBCDataSet dataSet = new JDBCDataSet();
		JDBCSharedConnectionDataProxy proxy = new JDBCSharedConnectionDataProxy(session.connection());
		dataSet.setDataProxy(proxy);
		dataSet.setQuery(sqlQuery);
		dataSet.loadData(0, 1, -1);
		logger.debug("Query " + sqlQuery + " executed");
		IDataStore dataStore = dataSet.getDataStore();
		logger.debug("Data store retrieved");
		resultNumber = ((Number)dataStore.getRecordAt(0).getFieldAt(0).getValue()).intValue();
		logger.debug("Result number is " + resultNumber);
		resultNumber = resultNumber < 0? 0: resultNumber;
		logger.debug("OUT: returning " + resultNumber);
		return resultNumber;
	}
	
	private int getResultNumberUsingScrollableResults(org.hibernate.Query hibernateQuery, Session session) {
		int resultNumber = 0;
		logger.debug("Scrolling query " + statement.getQueryString() + " ...");
		ScrollableResults scrollableResults = hibernateQuery.scroll();
		scrollableResults.last();
		logger.debug("Scrolled query " + statement.getQueryString());
		resultNumber = scrollableResults.getRowNumber() + 1; // Hibernate ScrollableResults row number starts with 0
		logger.debug("Number of fetched records: " + resultNumber + " for query " + statement.getQueryString());
		resultNumber = resultNumber < 0? 0: resultNumber;
		return resultNumber;
	}
		
	public IDataStore fetchNext() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public IEngUserProfile getUserProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFetchSize(int l) {
		// TODO Auto-generated method stub
		
	}

	public void setQuery(Object query) {
		// TODO Auto-generated method stub
		
	}

	public void setUserProfile(IEngUserProfile userProfile) {
		// TODO Auto-generated method stub
		
	}
}
