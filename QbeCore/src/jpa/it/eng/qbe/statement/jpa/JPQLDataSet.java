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
package it.eng.qbe.statement.jpa;

import it.eng.qbe.datasource.jpa.IJpaDataSource;
import it.eng.qbe.query.HavingField;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.statement.AbstractQbeDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;



/**
 * @author Andrea Gioia (andrea.gioia@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class JPQLDataSet extends AbstractQbeDataSet {

	
	private List resultList;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JPQLDataSet.class);
    
	
	public JPQLDataSet(JPQLStatement statement) {
		super(statement);
	}
	
	
	public void loadData(int offset, int fetchSize, int maxResults) {
		EntityManager entityManager;
		
		try {
			entityManager = ((IJpaDataSource)statement.getDataSource()).getEntityManager();
			loadDataPersistenceProvider(offset, fetchSize, maxResults, entityManager);
		} catch (Throwable t) {
			throw new RuntimeException("Impossible to load data", t);
		}
	
	}
	
	private void loadDataPersistenceProvider(int offset, int fetchSize, int maxResults, EntityManager entityManager) {

		javax.persistence.Query jpqlQuery;
		boolean overflow = false;
		int resultNumber;
		it.eng.qbe.query.Query query = this.statement.getQuery();
		Map params = this.getParamsMap();
		if (params != null && !params.isEmpty()) {
			this.updateParameters(query, params);
		}
		String statementStr = statement.getQueryString();
		
		try {
			jpqlQuery = entityManager.createQuery( statementStr );
		} catch (Throwable t) {
			throw new RuntimeException("Impossible to compile query statement [" + statementStr + "]", t);
		}
			
		resultNumber = getResultNumber(statementStr, jpqlQuery, entityManager);
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
			jpqlQuery.setFirstResult(offset);
			if(fetchSize > 0) {
				jpqlQuery.setMaxResults(fetchSize);		
			}
			
			try {
				result = jpqlQuery.getResultList();
			} catch (Throwable t) {
				throw new RuntimeException("Impossible to execute statement [" + statementStr + "]", t);
			}

			logger.debug("Query " + statement.getQueryString() + " with offset = " + offset + " and fetch size = " + fetchSize + " executed");
		}	

		dataStore = toDataStore(result);
		dataStore.getMetaData().setProperty("resultNumber", resultNumber);	
				
		if(hasDataStoreTransformer()) {
			getDataStoreTransformer().transform(dataStore);
		}
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
		
		if(hasDataStoreTransformer()) {
			getDataStoreTransformer().transform(dataStore);
		}
	}
	
	
	private int getResultNumber(String statementStr, Query jpqlQuery, EntityManager entityManager) {
		int resultNumber = 0;
		try {
			resultNumber = getResultNumberUsingInlineView(statementStr,entityManager);
		} catch (Exception e) {
			logger.warn("Error getting result number using inline view!!", e);
			resultNumber = (jpqlQuery).getResultList().size();
		}
		return resultNumber;
	}
	
	/**
	 * Get the result number with an in line view
	 * @param jpqlQuery
	 * @param entityManager
	 * @return
	 * @throws Exception
	 */
	private int getResultNumberUsingInlineView(String jpqlQuery, EntityManager entityManager) throws Exception {
		int resultNumber = 0;
		logger.debug("IN: counting query result");
		
		JPQL2SQLStatementRewriter translator = new JPQL2SQLStatementRewriter(entityManager);
		String sqlQueryString = translator.rewrite(jpqlQuery);
		javax.persistence.Query countQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM (" + sqlQueryString + ") temp");

		logger.debug("Count query prepared and parameters setted..");
		logger.debug("Executing query..");
		resultNumber = ((BigInteger)countQuery.getResultList().get(0)).intValue();
		logger.debug("Query " + "SELECT COUNT(*) FROM (" + sqlQueryString + ")" + " executed");
		logger.debug("Result number is " + resultNumber);
		resultNumber = resultNumber < 0? 0: resultNumber;
		logger.debug("OUT: returning " + resultNumber);

		return resultNumber;
	}

	public Map getUserProfileAttributes() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setUserProfileAttributes(Map parameters) {
		// TODO Auto-generated method stub
		
	}

	
	public void updateParameters(it.eng.qbe.query.Query query, Map parameters) {
		logger.debug("IN");
		List whereFields = query.getWhereFields();
		Iterator whereFieldsIt = whereFields.iterator();
		while (whereFieldsIt.hasNext()) {
			WhereField whereField = (WhereField) whereFieldsIt.next();
			if (whereField.isPromptable()) {
				String key = getParameterKey(whereField.getRightOperand().values[0]);
				if (key != null) {
					String parameterValues = (String) parameters.get(key);
					if (parameterValues != null) {
						String[] promptValues = new String[] {parameterValues}; // TODO how to manage multi-values prompts?
						logger.debug("Read prompts " + promptValues + " for promptable filter " + whereField.getName() + ".");
						whereField.getRightOperand().lastValues = promptValues;
					}
				}
			}
		}
		List havingFields = query.getHavingFields();
		Iterator havingFieldsIt = havingFields.iterator();
		while (havingFieldsIt.hasNext()) {
			HavingField havingField = (HavingField) havingFieldsIt.next();
			if (havingField.isPromptable()) {
				String key = getParameterKey(havingField.getRightOperand().values[0]);
				if (key != null) {
					String parameterValues = (String) parameters.get(key);
					if (parameterValues != null) {
						String[] promptValues = new String[] {parameterValues}; // TODO how to manage multi-values prompts?
						logger.debug("Read prompt value " + promptValues + " for promptable filter " + havingField.getName() + ".");
						havingField.getRightOperand().lastValues = promptValues; 
					}
				}
			}
		}
		logger.debug("OUT");
	}
	
	private String getParameterKey(String fieldValue) {
		int beginIndex = fieldValue.indexOf("P{");
		int endIndex = fieldValue.indexOf("}");
		if (beginIndex > 0 && endIndex > 0 && endIndex > beginIndex) {
			return fieldValue.substring(beginIndex + 2, endIndex);
		} else {
			return null;
		}
		
	}


	public IMetaData getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setMetadata(IMetaData metadata) {
		// TODO Auto-generated method stub
		
	}


	public IDataStore test() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}


	public IDataSetTableDescriptor persist(String tableName,
			Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<String>> getDomainDescriptions(
			Map<String, List<String>> codes) {
		return codes;
	}
}
