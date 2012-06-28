/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.statement;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.query.Query;

import java.util.Map;

/**
 * @author Andrea Gioia
 */
public abstract class  AbstractStatement implements IStatement {

	
	public static final String OPERAND_TYPE_STATIC = "Static Content";
	public static final String OPERAND_TYPE_SUBQUERY = "Subquery";
	
	
	public static final String OPERAND_TYPE_SIMPLE_FIELD = "Field Content";
	public static final String OPERAND_TYPE_CALCULATED_FIELD = "calculated.field";
	public static final String OPERAND_TYPE_INLINE_CALCULATED_FIELD = "inline.calculated.field";
	
	public static final String OPERAND_TYPE_PARENT_FIELD = "Parent Field Content";
	
	
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

	public abstract String getValueBounded(String operandValueToBound, String operandType);

}
