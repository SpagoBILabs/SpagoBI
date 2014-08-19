/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j.sql;

import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.writeback4j.IMemberCoordinates;
import it.eng.spagobi.writeback4j.ISchemaRetriver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension.Type;
import org.olap4j.metadata.Member;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 */
public class DefaultWeightedAllocationAlgorithmPersister extends AbstractSqlSchemaManager {

	IDataSource dataSource;
	private boolean useInClause = true;

	public static transient Logger logger = Logger.getLogger(DefaultWeightedAllocationAlgorithmPersister.class);

	public DefaultWeightedAllocationAlgorithmPersister(ISchemaRetriver retriver, IDataSource dataSource) {
		this.retriver = retriver;
		this.dataSource = dataSource;
	}

	public String executeProportionalUpdate(Member[] members, double prop, Connection connection, Integer version) throws Exception {
		// list of the coordinates for the members
		List<IMemberCoordinates> memberCordinates = new ArrayList<IMemberCoordinates>();

		// init the query with the update set statement
		StringBuffer query = new StringBuffer();

		// gets the measures and the coordinates of the dimension members
		for (int i = 0; i < members.length; i++) {
			Member aMember = members[i];

			try {
				if (!(aMember.getDimension().getDimensionType().equals(Type.MEASURE))) {
					memberCordinates.add(retriver.getMemberCordinates(aMember));
				} else {
					buildUpdate(query, aMember, prop);
				}
			} catch (OlapException e) {
				logger.error("Error loading the type of the dimension of the member " + aMember.getUniqueName(), e);
				throw new SpagoBIEngineException("Error loading the type of the dimension of the member " + aMember.getUniqueName(), e);
			}
		}

		String queryString;

		if (useInClause) {
			queryString = buildProportionalUpdateSingleSubquery(memberCordinates, query, connection, version);
		} else {
			queryString = buildProportionalUpdateOneSubqueryForDimension(memberCordinates, query, connection, version);
		}

		return queryString;

	}

	/**
	 * Create an update statement using the exists clause in the where. For
	 * every row of the fact table it makes a look up in the dimension to check
	 * if the row is edited. the db cells linked to the edited cell of the cube)
	 * 
	 * @param memberCordinates
	 *            the coordinates of the edited cube cell
	 * @param query
	 *            the first part of the update statement (update TABLE set X 0
	 *            X*prop)
	 * @param connection
	 *            the java.sql connection to the db
	 * @param version
	 *            the version to update
	 * @return the sql query ( ex update sales_fact_1998_virtual cubealias set
	 *         store_sales = store_sales*5 where exists ( select * from region
	 *         t4, product t1, product_class t2, store t3, customer t5 where (
	 *         t1.product_class_id = t2.product_class_id ) and ( t3.store_id =
	 *         cubealias.store_id ) and ( t1.product_id = cubealias.product_id )
	 *         and ( t3.region_id = t4.region_id ) and ( t5.customer_id =
	 *         cubealias.customer_id ) and ( t2.product_family = 'Food' ) and (
	 *         t2.product_department = 'Breakfast Foods' ) and ( t4.sales_region
	 *         = 'Mexico Central' ) and ( t5.country = 'Mexico' ) ) and (
	 *         cubealias.wbversion = '0' )
	 * @throws Exception
	 */
	private String buildProportionalUpdateOneSubqueryForDimension(List<IMemberCoordinates> memberCordinates, StringBuffer query, Connection connection, Integer version)
			throws Exception {

		// List of where conditions
		Map<TableEntry, String> whereConditions = new HashMap<TableEntry, String>();

		// List of joins
		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();

		// List of form
		Set<String> fromTables = new HashSet<String>();

		query.append(" where exists ( ");

		StringBuffer degenerateDimensionConditions = new StringBuffer();

		for (Iterator<IMemberCoordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {
			IMemberCoordinates aIMemberCordinates = iterator.next();
			if (aIMemberCordinates.getTableName() == null) {// degenerate
															// dimension
				// create a where in the cube for each level of the degenerate
				// dimension
				Map<TableEntry, String> where = buildWhereConditions(aIMemberCordinates, null, version);
				Map<String, String> cubeTable2Alias = new HashMap<String, String>();
				cubeTable2Alias.put(null, getCubeAlias());
				addWhereCondition(degenerateDimensionConditions, where, cubeTable2Alias, null);
			} else {
				whereConditions.putAll(buildWhereConditions(aIMemberCordinates, fromTables, version));
				addJoinConditions(fromTables, joinConditions, aIMemberCordinates, false);
				addInnerDimensionJoinConditions(fromTables, joinConditions, aIMemberCordinates);
			}
		}

		buildSelectQuery(whereConditions, degenerateDimensionConditions, joinConditions, fromTables, query);

		query.append(" ) ");

		String queryString = query.toString();

		SqlUpdateStatement updateStatement = new SqlUpdateStatement(queryString);
		updateStatement.executeStatement(connection);

		return queryString;
	}

	/**
	 * Create an update statement using an in clause for each dimension of the
	 * cube.
	 * 
	 * @param memberCordinates
	 *            the coordinates of the edited cube cell
	 * @param query
	 *            the first part of the update statement (update TABLE set X 0
	 *            X*prop)
	 * @param connection
	 *            the java.sql connection to the db
	 * @param version
	 *            the version to update
	 * @return the sql query (example update sales_fact_virtual cube set
	 *         sales=sales*5 where cube.store_id in (select store_id from store
	 *         where state="NV")
	 * @throws Exception
	 */
	private String buildProportionalUpdateSingleSubquery(List<IMemberCoordinates> memberCordinates, StringBuffer query, Connection connection, Integer version) throws Exception {

		// List of where conditions
		Map<TableEntry, String> whereConditions;

		// List of joins
		Set<EquiJoin> selectFields;

		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();

		// List of form
		Set<String> fromTables;

		query.append(" where ");

		boolean first = true;
		for (Iterator<IMemberCoordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {
			StringBuffer subquery = null;
			IMemberCoordinates aIMemberCordinates = iterator.next();
			if (aIMemberCordinates.getTableName() == null) {// degenerate
															// dimension

				// create a where in the cube for each level of the degenerate
				// dimension
				subquery = new StringBuffer();
				Map<TableEntry, String> where = buildWhereConditions(aIMemberCordinates, null, version);
				Map<String, String> cubeTable2Alias = new HashMap<String, String>();
				cubeTable2Alias.put(null, getCubeAlias());
				addWhereCondition(subquery, where, cubeTable2Alias, null);
			} else if (!aIMemberCordinates.isAllMember()) {
				whereConditions = new HashMap<TableEntry, String>();
				selectFields = new HashSet<EquiJoin>();
				fromTables = new HashSet<String>();
				joinConditions = new HashSet<EquiJoin>();

				whereConditions.putAll(buildWhereConditions(aIMemberCordinates, fromTables, version));
				addJoinConditions(fromTables, selectFields, aIMemberCordinates, false);
				addInnerDimensionJoinConditions(fromTables, joinConditions, aIMemberCordinates);

				subquery = buildSelectQueryForIn(whereConditions, selectFields, joinConditions, fromTables);

			}
			if (subquery != null) {
				if (!first) {
					query.append(" and ");
				}
				first = false;
				query.append(subquery);
			}

		}

		String queryString = query.toString();

		SqlUpdateStatement updateStatement = new SqlUpdateStatement(queryString);
		updateStatement.executeStatement(connection);

		return queryString;
	}

	/**
	 * Build the update statement for the measure
	 * 
	 * @param buffer
	 *            the buffer of the query
	 * @param measure
	 *            the measure to update
	 * @param prop
	 *            the ratio
	 */
	private void buildUpdate(StringBuffer buffer, Member measure, double prop) throws SpagoBIEngineException {
		String measureColumn = null;
		try {
			measureColumn = retriver.getMeasureColumn(measure);
		} catch (SpagoBIEngineException e) {
			logger.error("Error loading the column for the table measure " + measure.getName(), e);
			throw new SpagoBIEngineException("Error loading the column for the table measure " + measure.getName(), e);
		}

		buffer.append("update ");
		buffer.append(retriver.getEditCubeTableName());
		buffer.append(" " + getCubeAlias());
		buffer.append(" set " + measureColumn + " = " + measureColumn + "*" + prop);

	}

	public void buildSelectQuery(Map<TableEntry, String> whereConditions, StringBuffer degenerateDimensionConditions, Set<EquiJoin> joinConditions, Set<String> fromTables,
			StringBuffer query) {

		Map<String, String> table2Alias = new HashMap<String, String>();
		getTableAlias(table2Alias, getCubeAlias());

		StringBuffer from = new StringBuffer();
		StringBuffer where = new StringBuffer();
		query = query.append("select * ");

		addWhereCondition(where, joinConditions, table2Alias);
		addWhereCondition(where, whereConditions, table2Alias, null);
		addFromConditions(from, fromTables, table2Alias);

		query.append(" from ");
		query.append(from);
		query.append(" where ");

		// add the degenerate dimensions.
		if (degenerateDimensionConditions.length() > 2) {
			query.append(degenerateDimensionConditions);
			query.append(" and ");
		}

		query.append(where);

	}

	public StringBuffer buildSelectQueryForIn(Map<TableEntry, String> whereConditions, Set<EquiJoin> selectFields, Set<EquiJoin> joinConditions, Set<String> fromTables) {

		Map<String, String> table2Alias = new HashMap<String, String>();
		getTableAlias(table2Alias, getCubeAlias());

		StringBuffer select = new StringBuffer();
		StringBuffer from = new StringBuffer();
		StringBuffer where = new StringBuffer();

		addSelectCondition(select, selectFields, table2Alias);
		addWhereCondition(where, joinConditions, table2Alias);
		addWhereCondition(where, whereConditions, table2Alias, null);
		addFromConditions(from, fromTables, table2Alias);

		StringBuffer subquery = new StringBuffer();

		addInCondition(subquery, selectFields);
		subquery.append("(select ");
		subquery.append(select);
		subquery.append(" from ");
		subquery.append(from);
		subquery.append(" where ");
		subquery.append(where);
		subquery.append(")");

		return subquery;

	}

	/**
	 * Builds the select statement
	 * 
	 * @param selectClause
	 *            the buffer in witch append the clause
	 * @param selectFields
	 *            the select table entry
	 * @param table2Alias
	 *            the map column alias
	 */
	private void addSelectCondition(StringBuffer selectClause, Set<EquiJoin> selectFields, Map<String, String> table2Alias) {
		if (selectFields != null) {
			Iterator<EquiJoin> iter = selectFields.iterator();
			while (iter.hasNext()) {
				EquiJoin select = iter.next();
				if (selectClause.length() != 0) {
					selectClause.append(" , ");
				}

				// the left is the couple table/column of the hierarchy
				String leftEntry = select.leftField.toString(table2Alias, this);
				selectClause.append(leftEntry);
			}
		}
	}

	private void addInCondition(StringBuffer subquery, Set<EquiJoin> selectFields) {
		if (selectFields != null) {
			Iterator<EquiJoin> iter = selectFields.iterator();
			while (iter.hasNext()) {
				EquiJoin select = iter.next();

				// the left is the couple table/column of the hierarchy
				String rightEntry = select.rightField.toString();

				subquery.append(rightEntry);
				subquery.append(" in ");

			}
		}
	}

	public boolean isUseInClause() {
		return useInClause;
	}

	public void setUseInClause(boolean useInClause) {
		this.useInClause = useInClause;
	}

}
