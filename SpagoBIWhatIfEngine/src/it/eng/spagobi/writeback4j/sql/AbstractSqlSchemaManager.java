/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j.sql;

import it.eng.spagobi.engines.whatif.common.WhatIfConstants;
import it.eng.spagobi.writeback4j.IMemberCoordinates;
import it.eng.spagobi.writeback4j.ISchemaRetriver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.olap4j.metadata.Member;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 */
public abstract class AbstractSqlSchemaManager {

	private int tableCount = 0;
	protected ISchemaRetriver retriver;

	public String getCubeAlias() {
		return "cubealias";
	}

	protected void addInnerDimensionJoinConditions(Set<String> from, Set<EquiJoin> joins, IMemberCoordinates cordinates) {

		EquiJoin coordinateInnerJoin = cordinates.getInnerDimensionJoinConditions();
		if (coordinateInnerJoin != null) {
			joins.add(coordinateInnerJoin);
			from.add(coordinateInnerJoin.leftField.getTable());
			from.add(coordinateInnerJoin.rightField.getTable());
		}
	}

	protected Map<TableEntry, String> buildWhereConditions(IMemberCoordinates cordinates, Set<String> from, Integer version) {
		Map<TableEntry, String> condition2Value = new HashMap<TableEntry, String>();
		Map<TableEntry, Member> lelvel2Member = cordinates.getLevel2Member();

		if (lelvel2Member != null && !cordinates.isAllMember()) {
			Iterator<TableEntry> i = lelvel2Member.keySet().iterator();
			while (i.hasNext()) {
				TableEntry aLevel = i.next();
				if (from != null) {
					from.add(aLevel.table);
				}
				if (cordinates.getDimensionName().equalsIgnoreCase(WhatIfConstants.VERSION_DIMENSION_NAME)) {
					condition2Value.put(new TableEntry(aLevel.column, aLevel.table), version.toString());
				} else {
					condition2Value.put(new TableEntry(aLevel.column, aLevel.table), lelvel2Member.get(aLevel).getName());
				}

			}
		}

		return condition2Value;
	}

	protected void addWhereCondition(StringBuffer whereConditions, Set<EquiJoin> joins, Map<String, String> table2Alias) {
		if (joins != null) {
			Iterator<EquiJoin> iter = joins.iterator();
			while (iter.hasNext()) {
				EquiJoin join = iter.next();
				if (whereConditions.length() != 0) {
					whereConditions.append(" and ");
				}

				String leftEntry = null;
				String rightEntry = null;

				if (join.leftField.isCubeDimension) {
					leftEntry = join.leftField.toString();
				} else {
					leftEntry = join.leftField.toString(table2Alias, this);
				}

				if (join.rightField.isCubeDimension) {
					rightEntry = join.rightField.toString();
				} else {
					rightEntry = join.rightField.toString(table2Alias, this);
				}

				whereConditions.append(" ( ");
				whereConditions.append(leftEntry);
				whereConditions.append(" = ");
				whereConditions.append(rightEntry);
				whereConditions.append(" ) ");
			}
		}
	}

	protected String getTableAlias(Map<String, String> table2Alias, String table) {
		String alias = table2Alias.get(table);
		if (alias == null) {
			alias = "t" + tableCount;
			table2Alias.put(table, alias);
			tableCount++;
		}
		return alias;
	}

	protected void addWhereCondition(StringBuffer whereConditionsBuffer, Map<TableEntry, String> whereConditions, Map<String, String> table2Alias, Integer fixValue) {

		if (whereConditions != null) {
			Iterator<TableEntry> iter = whereConditions.keySet().iterator();
			while (iter.hasNext()) {
				TableEntry entry = iter.next();

				if (whereConditionsBuffer.length() != 0) {
					whereConditionsBuffer.append(" and ");
				}

				whereConditionsBuffer.append(" ( ");
				whereConditionsBuffer.append(entry.toString(table2Alias, this));// add
																				// the
																				// clause
																				// for
																				// the
																				// dimension
				whereConditionsBuffer.append(" = ");

				String value = whereConditions.get(entry);
				if (fixValue != null) {
					value = fixValue.toString();
				}
				whereConditionsBuffer.append(formatValue(value));// add the
																	// clause
																	// for the
																	// cube
				whereConditionsBuffer.append(" ) ");
			}
		}
	}

	private String formatValue(String value) {
		return "'" + value + "'";
	}

	protected void addJoinConditions(Set<String> from, Set<EquiJoin> joins, IMemberCoordinates cordinates, boolean withAllMembers) {
		if (withAllMembers || !cordinates.isAllMember()) {
			String tableName = cordinates.getTableName();

			TableEntry hierarchyTableEntry = new TableEntry(cordinates.getPrimaryKey(), tableName);

			TableEntry cubeTableEntry = new TableEntry(cordinates.getForeignKey(), getCubeAlias(), true);
			joins.add(new EquiJoin(hierarchyTableEntry, cubeTableEntry));

			from.add(tableName);
		}

	}

	protected void addFromConditions(StringBuffer buffer, Set<String> froms, Map<String, String> table2Alias) {
		if (froms != null) {
			Iterator<String> iter = froms.iterator();
			while (iter.hasNext()) {

				if (buffer.length() != 0) {
					buffer.append(", ");
				}

				String table = iter.next();
				buffer.append(" ");
				buffer.append(table);
				buffer.append(" ");
				buffer.append(getTableAlias(table2Alias, table));
			}
		}
	}

}
