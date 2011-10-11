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

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.jpa.IJpaDataSource;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPQLStatement extends AbstractStatement {
	
	protected IJpaDataSource dataSource;
	
	public static transient Logger logger = Logger.getLogger(JPQLStatement.class);
		
	protected JPQLStatement(IDataSource dataSource) {
		super(dataSource);
	}
	
	
	public JPQLStatement(IDataSource dataSource, Query query) {
		super(dataSource, query);
	}
	
	public void prepare() {
		String queryStr;
		
		// one map of entity aliases for each queries (master query + subqueries)
		// each map is indexed by the query id
		Map entityAliasesMaps = new HashMap();
		
		queryStr = compose(getQuery(), entityAliasesMaps);	

		if(getParameters() != null) {
			try {
				queryStr = StringUtils.replaceParameters(queryStr.trim(), "P", getParameters());
			} catch (IOException e) {
				throw new SpagoBIRuntimeException("Impossible to set parameters in query", e);
			}
			
		}	
		
		setQueryString(queryStr);
		
	}
	
	/*
	 * internally used to generate the parametric statement string. Shared by the prepare method and the buildWhereClause method in order
	 * to recursively generate subquery statement string to be embedded in the parent query.
	 */
	private String compose(Query query, Map entityAliasesMaps) {
		String queryStr = null;
		String selectClause = null;
		String whereClause = null;
		String groupByClause = null;
		String orderByClause = null;
		String fromClause = null;
		String havingClause = null;
		String viewRelation = null;
		
		Assert.assertNotNull(query, "Input parameter 'query' cannot be null");
		Assert.assertTrue(!query.isEmpty(), "Input query cannot be empty (i.e. with no selected fields)");
				
		// let's start with the query at hand
		entityAliasesMaps.put(query.getId(), new HashMap());
		
		JPQLBusinessViewUtility viewsUtility = new JPQLBusinessViewUtility(this);
		
		selectClause = buildSelectClause(query, entityAliasesMaps);
		whereClause = buildWhereClause(query, entityAliasesMaps);
		groupByClause = buildGroupByClause(query, entityAliasesMaps);
		orderByClause = buildOrderByClause(query, entityAliasesMaps);
		havingClause = buildHavingClause(query, entityAliasesMaps);
		viewRelation = viewsUtility.buildViewsRelations(entityAliasesMaps, query, whereClause);
		fromClause = buildFromClause(query, entityAliasesMaps);
		
		JPQLStatementWhereClause clause = new JPQLStatementWhereClause(this);
		whereClause = clause.fixWhereClause(whereClause, query, entityAliasesMaps);
		
		queryStr = selectClause + " " + fromClause + " " + whereClause + " "+viewRelation+" " +  groupByClause + " " + havingClause + " " + orderByClause;
		
		Set subqueryIds;
		try {
			subqueryIds = StringUtils.getParameters(queryStr, "Q");
		} catch (IOException e) {
			throw new SpagoBIRuntimeException("Impossible to set parameters in query", e);
		}
		
		Iterator it = subqueryIds.iterator();
		while(it.hasNext()) {
			String id = (String)it.next();
			Query subquery = query.getSubquery(id);
			
			String subqueryStr = compose(subquery, entityAliasesMaps);
			queryStr = queryStr.replaceAll("Q\\{" + subquery.getId() + "\\}", subqueryStr);
		} 
		
		return queryStr;
	}

	
	public static String getNextAlias(Map entityAliasesMaps) {
		int aliasesCount = 0;
		Iterator it = entityAliasesMaps.keySet().iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			Map entityAliases = (Map)entityAliasesMaps.get(key);
			aliasesCount += entityAliases.keySet().size();
		}
		
		return "t_" + aliasesCount;
	}
	
	private String buildSelectClause(Query query, Map entityAliasesMaps) {
		JPQLStatementSelectClause clause = new JPQLStatementSelectClause(this);
		return clause.buildSelectClause(query, entityAliasesMaps);
	}
	
	
	
	private String buildFromClause(Query query, Map entityAliasesMaps) {
		JPQLStatementFromClause clause = new JPQLStatementFromClause(this);
		return clause.buildFromClause(query, entityAliasesMaps);
	}

	private String buildHavingClause(Query query, Map entityAliasesMaps) {
		JPQLStatementHavingClause clause = new JPQLStatementHavingClause(this);		
		return clause.buildHavingClause(query, entityAliasesMaps);
	}
	
	private String buildWhereClause(Query query, Map entityAliasesMaps) {
		JPQLStatementWhereClause clause = new JPQLStatementWhereClause(this);		
		return clause.buildWhereClause(query, entityAliasesMaps);
	}
	
	private String buildOrderByClause(Query query, Map entityAliasesMaps) {
		JPQLStatementOrderByClause clause = new JPQLStatementOrderByClause(this);		
		return clause.buildOrderByClause(query, entityAliasesMaps);
	}
	
	private String buildGroupByClause(Query query, Map entityAliasesMaps) {
		JPQLStatementGroupByClause clause = new JPQLStatementGroupByClause(this);		
		return clause.buildGroupByClause(query, entityAliasesMaps);
	}
	
	
	
	
	public Set getSelectedEntities() {
		Set selectedEntities;
		Map entityAliasesMaps;
		Iterator entityUniqueNamesIterator;
		String entityUniqueName;
		IModelEntity entity;
		
		
		Assert.assertNotNull( getQuery(), "Input parameter 'query' cannot be null");
		Assert.assertTrue(! getQuery().isEmpty(), "Input query cannot be empty (i.e. with no selected fields)");
		
		selectedEntities = new HashSet();
		
		// one map of entity aliases for each queries (master query + subqueries)
		// each map is indexed by the query id
		entityAliasesMaps = new HashMap();
		
		// let's start with the query at hand
		entityAliasesMaps.put( getQuery().getId(), new HashMap());
		
		buildSelectClause( getQuery(), entityAliasesMaps);
		buildWhereClause( getQuery(), entityAliasesMaps);
		buildGroupByClause( getQuery(), entityAliasesMaps);
		buildOrderByClause( getQuery(), entityAliasesMaps);
		buildFromClause( getQuery(), entityAliasesMaps);
		
		Map entityAliases = (Map)entityAliasesMaps.get( getQuery().getId());
		entityUniqueNamesIterator = entityAliases.keySet().iterator();
		while(entityUniqueNamesIterator.hasNext()) {
			entityUniqueName = (String)entityUniqueNamesIterator.next();
			//entity = getDataMartModel().getDataMartModelStructure().getRootEntity( entityUniqueName );
			entity = getDataSource().getModelStructure().getEntity( entityUniqueName );
			selectedEntities.add(entity);
		}
		return selectedEntities;
	}
	
	
	


	public String getQueryString() {		
		if(super.getQueryString() == null) {
			this.prepare();
		}
		
		return super.getQueryString();
	}
	
	public String getSqlQueryString() {

		JPADataSource ds = ((JPADataSource)getDataSource());
		EntityManager em = ds.getEntityManager();

		JPQL2SQLStatementRewriter translator = new JPQL2SQLStatementRewriter(em);
		return translator.rewrite( getQueryString());

	}


	@Override
	public String getValueBounded(String operandValueToBound, String operandType) {
		JPQLStatementWhereClause clause = new JPQLStatementWhereClause(this);
		return clause.getValueBounded(operandValueToBound, operandType);
	}
	

	
}
