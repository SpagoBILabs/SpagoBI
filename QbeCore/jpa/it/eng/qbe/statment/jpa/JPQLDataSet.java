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
package it.eng.qbe.statment.jpa;

import it.eng.qbe.datasource.jpa.IJpaDataSource;
import it.eng.qbe.statement.AbstractQbeDataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.CollectionExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.hibernate.ScrollableResults;
import org.hibernate.ejb.HibernateQuery;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPQLDataSet extends AbstractQbeDataSet {

	
	private List resultList;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JPQLDataSet.class);
    
	
	public JPQLDataSet(JPQLStatement statement) {
		super(statement);
	}
	
	
	public void loadData(int offset, int fetchSize, int maxResults) {
		EntityManager entityManager = null;
		entityManager = ((IJpaDataSource)statement.getDataSource()).getEntityManager();
		loadDataEclipseLink(offset, fetchSize, maxResults, entityManager);
	
	}
	
	private void loadWithDataHibernate(int offset, int fetchSize, int maxResults, EntityManager entityManager) {

		HibernateQuery jpqlQuery;
		boolean overflow = false;
		int resultNumber;
		
		jpqlQuery = (HibernateQuery)entityManager.createQuery( statement.getQueryString() );
		resultNumber =getResultNumber(jpqlQuery, entityManager);
		logger.info("Number of fetched records: " + resultNumber + " for query " + statement.getQueryString());
		overflow = (maxResults > 0) && (resultNumber >= maxResults);

		ScrollableResults sr = jpqlQuery.getHibernateQuery().scroll();
		sr.last();
		resultNumber =  sr.getRowNumber();
		sr.first();
		
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
			jpqlQuery.setFirstResult(offset).setMaxResults(fetchSize);			
			result = jpqlQuery.getResultList();
			logger.debug("Query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize + " executed");
		}	

		dataStore = toDataStore(result);
		dataStore.getMetaData().setProperty("resultNumber", resultNumber);		
	}
	
	
	
	private void loadDataEclipseLink(int offset, int fetchSize, int maxResults, EntityManager entityManager) {

		javax.persistence.Query jpqlQuery;
		boolean overflow = false;
		int resultNumber;
		
		jpqlQuery = entityManager.createQuery( statement.getQueryString() );
		resultNumber =getResultNumber(jpqlQuery, entityManager);
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
			jpqlQuery.setFirstResult(offset).setMaxResults(fetchSize);			
			result = jpqlQuery.getResultList();
			logger.debug("Query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize + " executed");
		}	

		dataStore = toDataStore(result);
		dataStore.getMetaData().setProperty("resultNumber", resultNumber);		
	}
	
	private void loadDataWithMyPagination(int offset, int fetchSize, int maxResults,EntityManager entityManager){
		javax.persistence.Query jpqlQuery;
		boolean overflow = false;
		int maxPageResult=offset+fetchSize;

		jpqlQuery = entityManager.createQuery( statement.getQueryString() );	
		this.resultList = jpqlQuery.getResultList();
		int resultNumber = this.resultList.size();
		logger.info("Number of fetched records: " + resultNumber + " for query " + statement.getQueryString());
		overflow = (maxResults > 0) && (resultNumber >= maxResults);

		
		if (overflow && abortOnOverflow) {
			// does not execute query
			this.resultList = new ArrayList();
		} else {
			offset = offset < 0 ? 0 : offset;
			if(maxResults > 0) {
				maxPageResult = (maxPageResult > 0)? Math.min(maxPageResult, maxResults): maxResults;				
			}
			maxPageResult = Math.min(maxPageResult, resultNumber);
			logger.debug("Executing query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize);
			dataStore = toDataStore(this.resultList.subList(offset, maxPageResult));
			logger.debug("Query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize + " executed");
		}	
		dataStore.getMetaData().setProperty("resultNumber", resultNumber);	
	}
	
	
	private int getResultNumber(Query jpqlQuery, EntityManager entityManager) {
		int resultNumber = 0;
		try {
			resultNumber = getResultNumberUsingInlineView(jpqlQuery,entityManager);
		} catch (Exception e) {
			logger.warn("Error getting result number using inline view!!", e);
			resultNumber = (jpqlQuery).getResultList().size();
		}
		return resultNumber;
	}
	
	/**
	 * ONLY FOR ECLIPSELINK
	 * Get the result number with an in line view
	 * @param jpqlQuery
	 * @param entityManager
	 * @return
	 * @throws Exception
	 */
	private int getResultNumberUsingInlineView(Query jpqlQuery, EntityManager entityManager) throws Exception {
		int resultNumber = 0;
		String parameterValue;
		logger.debug("IN: counting query result");
		
		EJBQueryImpl qi = (EJBQueryImpl)jpqlQuery;
		String sqlQueryString = qi.getDatabaseQuery().getSQLString();
		//In qi, all the constants are substituted with ? (a place holder for the parameter)..
		//so we shold get the parameter values with getParameters
		List<String> queryParameters = getParameters(qi.getDatabaseQuery().getQueryMechanism().getSelectionCriteria());//((JPQLStatement)statement).getQueryParameters();
		logger.debug("Preparing count query");
		EJBQueryImpl countQuery = (EJBQueryImpl)entityManager.createNativeQuery("SELECT COUNT(*) FROM (" + sqlQueryString + ") temp");
		for(int i=0; i<queryParameters.size(); i++ ){
			parameterValue = queryParameters.get(i);
			if(parameterValue.startsWith("'") && parameterValue.endsWith("'")){
				parameterValue = parameterValue.substring(1,parameterValue.length()-1);
			}
			countQuery.setParameter(1+i, parameterValue);
		}
		logger.debug("Count query prepared and parameters setted..");
		logger.debug("Executing query..");
		resultNumber = ((Long)countQuery.getResultList().get(0)).intValue();
		logger.debug("Query " + "SELECT COUNT(*) FROM (" + sqlQueryString + ")" + " executed");
		logger.debug("Result number is " + resultNumber);
		resultNumber = resultNumber < 0? 0: resultNumber;
		logger.debug("OUT: returning " + resultNumber);

		return resultNumber;
	}
	
	
	/**
	 * ONLY FOR ECLIPSELINK
	 * Get the list of constants from the expression
	 * @param e Expression to parse
	 * @return list of Constant values of the expression
	 */
	private List<String> getParameters(Expression e){
		if(e instanceof FunctionExpression){
			List<String> l =  new ArrayList<String>();
			Vector<Expression> children = ((FunctionExpression) e).getChildren();
			Iterator<Expression> it=children.iterator();
			while(it.hasNext()){
				Expression o = it.next();
				l.addAll(getParameters(o));
			}
			return l;
		}else if(e instanceof CollectionExpression){
			List<String> l =  new ArrayList<String>();
			Object value = ((CollectionExpression) e).getValue();
			if(value instanceof Collection){
				Iterator<Expression> it=((Collection) value).iterator();
				while(it.hasNext()){
					Expression o = it.next();
					l.addAll(getParameters(o));
				}
			}
			return l;
		}else if(e instanceof ConstantExpression){
			List<String> l =  new ArrayList<String>();
			l.add(""+((ConstantExpression)e).getValue());
			return l;
		}else if(e instanceof RelationExpression){
			Expression fchild = ((RelationExpression)e).getFirstChild();
			List<String> firstList = getParameters(fchild);
			Expression schild = ((RelationExpression)e).getSecondChild();
			List<String> secondList = getParameters(schild);
			firstList.addAll(secondList);
			return firstList;
		}else if(e instanceof LogicalExpression){
			Expression fchild = ((LogicalExpression)e).getFirstChild();
			List<String> firstList = getParameters(fchild);
			Expression schild = ((LogicalExpression)e).getSecondChild();
			List<String> secondList = getParameters(schild);
			firstList.addAll(secondList);
			return firstList;
		}
		return new ArrayList<String>();

	}


	public Map getUserProfileAttributes() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setUserProfileAttributes(Map parameters) {
		// TODO Auto-generated method stub
		
	}


	public Object getQuery() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setQuery(Object query) {
		// TODO Auto-generated method stub
		
	}
	

}
