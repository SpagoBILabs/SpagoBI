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
package it.eng.qbe.statment;

import java.util.Map;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.query.Query;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicStatement.
 * 
 * @author Andrea Gioia
 */
public abstract class  AbstractStatement implements IStatement{

	
	IDataSource dataSource;
	

	Query query;
	
	
	Map parameters;
	
	
	String queryString;
	
	

	/** The max results. */
	int maxResults;
	
	/** If it is true (i.e. the maxResults limit is exceeded) then query execution should be stopped */
	boolean isBlocking;

	/** The fetch size. */
	int fetchSize;
	
	/** The offset. */
	int offset;
	
	/**
	 * Instantiates a new basic statement.
	 * 
	 * @param dataMartModel the data mart model
	 */
	protected AbstractStatement(IDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Create a new statement from query bound to the specific datamart-model
	 * 
	 * @param dataMartModel the data mart model
	 * @param query the query
	 */
	protected AbstractStatement(IDataSource dataSource, Query query) {
		this.dataSource = dataSource;
		this.query = query;
	}
	
	public IDataSource getDataSource() {
		return dataSource;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public void setQuery(Query query) {
		this.query = query;
		this.queryString = null;
	}
	
	
	public Map getParameters() {
		return parameters;
	}

	
	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.model.IStatement#getOffset()
	 */
	public int getOffset() {
		return offset;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.model.IStatement#setOffset(int)
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.IStatement#getFetchSize()
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.model.IStatement#setFetchSize(int)
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.model.IStatement#getMaxResults()
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.model.IStatement#setMaxResults(int)
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	
	public boolean isMaxResultsLimitBlocking() {
		return isBlocking;
	}

	public void setIsMaxResultsLimitBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}
	
	public String getQueryString() {
		return queryString;
	}	
	
	protected void setQueryString(String queryString) {
		this.queryString = queryString;
	}


}
