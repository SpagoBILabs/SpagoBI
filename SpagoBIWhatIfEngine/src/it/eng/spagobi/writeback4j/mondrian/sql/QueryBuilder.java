/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j.mondrian.sql;

import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.writeback4j.mondrian.schema.MondrianMemberCordinates;
import it.eng.spagobi.writeback4j.mondrian.schema.MondrianSchemaRetriver;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mondrian.olap.MondrianDef;

import org.apache.log4j.Logger;
import org.eigenbase.xom.NodeDef;
import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension.Type;
import org.olap4j.metadata.Member;


/**
 *  @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class QueryBuilder {
	
	public static transient Logger logger = Logger.getLogger(QueryBuilder.class);
	MondrianSchemaRetriver retriver;
	private int tableCount = 0;
	
	public QueryBuilder(String catalogUri, String editCubeName){
		try {
			retriver = new MondrianSchemaRetriver( catalogUri,  editCubeName);
		} catch (SpagoBIEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buildProportionalUpdate(Member[] members, double prop) throws SpagoBIEngineException{
		//list of the coordinates for the members
		List<MondrianMemberCordinates> memberCordinates = new ArrayList<MondrianMemberCordinates>();
		
		//init the query with the update set statement
		StringBuffer query = new StringBuffer();
				
		for (int i=0; i< members.length; i++) {
			Member aMember = members[i];
			
			try {
				if(!(aMember.getDimension().getDimensionType().equals(Type.MEASURE))){
					memberCordinates.add(retriver.getMemberCordinates(aMember));
				}else{
					buildUpdate(query, aMember, prop);
				}
			} catch (OlapException e) {
				logger.error("Error loading the type of the dimension of the member "+aMember.getUniqueName(), e);
				throw new SpagoBIEngineException("Error loading the type of the dimension of the member "+aMember.getUniqueName(), e);
			}
			
		}
		
		buildProportionalUpdateSingleSubquery(memberCordinates, query);
	}
	
	public void buildProportionalUpdateOneSubqueryForDimension(List<MondrianMemberCordinates> memberCordinates, StringBuffer query){
		
		//List of where conditions
		Map<TableEntry, String> whereConditions = new HashMap<TableEntry, String>();
		
		//List of joins
		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();
		
		//List of form 
		Set<String> fromTables = new HashSet<String>();
		
		query.append(" where exists ( ");
		
		for (Iterator<MondrianMemberCordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {
			MondrianMemberCordinates aMondrianMemberCordinates = (MondrianMemberCordinates) iterator.next();
			whereConditions.putAll(buildWhereConditions(aMondrianMemberCordinates, fromTables));
			addJoinConditions(fromTables, joinConditions, aMondrianMemberCordinates);
			addInnerDimensionJoinConditions(fromTables, joinConditions, aMondrianMemberCordinates);
		}
		
		buildSelectQuery(whereConditions, joinConditions, fromTables, query);
		
		query.append(" ) ");
		
		String queryString = query.toString();
		
		executeQuery(queryString);
	}
	
	public void buildProportionalUpdateSingleSubquery(List<MondrianMemberCordinates> memberCordinates, StringBuffer query){
		
		//List of where conditions
		Map<TableEntry, String> whereConditions;
		
		//List of joins
		Set<EquiJoin> selectFields;
		
		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();

		
		//List of form 
		Set<String> fromTables;
		
		query.append(" where ");
		
		boolean first = true;
		for (Iterator<MondrianMemberCordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {

			MondrianMemberCordinates aMondrianMemberCordinates = (MondrianMemberCordinates) iterator.next();
			if(!aMondrianMemberCordinates.isAllMember()){
				whereConditions = new HashMap<TableEntry, String>();
				selectFields = new HashSet<EquiJoin>();
				fromTables = new HashSet<String>();
				joinConditions = new HashSet<EquiJoin>();
				
				
				whereConditions.putAll(buildWhereConditions(aMondrianMemberCordinates, fromTables));
				addJoinConditions(fromTables, selectFields , aMondrianMemberCordinates);
				addInnerDimensionJoinConditions(fromTables, joinConditions, aMondrianMemberCordinates);
				
				StringBuffer subquery = buildSelectQueryForIn(whereConditions, selectFields, joinConditions, fromTables);
				
				if (!first){
					query.append(" and ");
				}
				first = false;
				query.append(subquery);
			}

			
		}
		
		
		
		
		String queryString = query.toString();
		
		executeQuery(queryString);
	}
	
	
	private String formatValue(String value){
		return "'"+value+"'";
		
	}
	
	private void buildUpdate(StringBuffer buffer, Member measure, double prop){
		MondrianDef.Measure measureDef = null;
		try {
			measureDef = retriver.getMondrianMeasure(measure);
		} catch (SpagoBIEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buffer.append("update ");
		buffer.append(retriver.getEditCube().fact.getAlias());
		buffer.append(" "+getCubeAlias());
		buffer.append(" set `"+measureDef.column+"` = `"+measureDef.column+"`*"+prop);
		
	}
	
	public Map<TableEntry, String> buildWhereConditions(MondrianMemberCordinates cordinates, Set<String> from){
		Map<TableEntry, String> condition2Value = new HashMap<QueryBuilder.TableEntry, String>();
		Map<MondrianDef.Level,Member> lelvel2Member = cordinates.getLevel2Member();
		
		if(lelvel2Member!=null && !cordinates.isAllMember()){
			Iterator<MondrianDef.Level> i = lelvel2Member.keySet().iterator();
			while(i.hasNext()){
				MondrianDef.Level aLevel = i.next();
				from.add(aLevel.table);
				condition2Value.put(new TableEntry(aLevel.column, aLevel.table), lelvel2Member.get(aLevel).getName()); 
			}
		}
		
		return condition2Value;
	}

	
	public void addJoinConditions(Set<String> from, Set<EquiJoin> joins, MondrianMemberCordinates cordinates){
		String tableName = cordinates.getHieararchy().primaryKeyTable;
		if(tableName==null){
			NodeDef[] children = cordinates.getHieararchy().getChildren();
			for(int i=0; i<children.length; i++){
				NodeDef node = children[i];
				if(node instanceof MondrianDef.Table){
					tableName = ((MondrianDef.Table)node).name;
					break;
				}
			}
		}

		TableEntry hierarchyTableEntry = new TableEntry(cordinates.getHieararchy().primaryKey, tableName);
		
		TableEntry cubeTableEntry = new TableEntry(cordinates.getDimension().foreignKey); 
		joins.add(new EquiJoin(hierarchyTableEntry, cubeTableEntry));

		from.add(tableName );
	}
	
	
	public void addInnerDimensionJoinConditions(Set<String> from, Set<EquiJoin> joins, MondrianMemberCordinates cordinates){
	
		MondrianDef.RelationOrJoin relOrJoin = cordinates.getHieararchy().relation;
		if(relOrJoin instanceof MondrianDef.Join){
			MondrianDef.Join join = (MondrianDef.Join) relOrJoin;
			MondrianDef.Table leftT = (MondrianDef.Table) join.left;
			MondrianDef.Table rightT = (MondrianDef.Table) join.right;
			TableEntry leftTable = new TableEntry(join.leftKey, leftT.name);
			TableEntry rightTable = new TableEntry(join.rightKey, rightT.name);
			EquiJoin ej = new EquiJoin(leftTable, rightTable);
			joins.add(ej);
			from.add(leftTable.getTable() );
			from.add(rightTable.getTable() );
		}
	}
	

	public String getCubeAlias(){
		return "cubealias";
	}
	
	
	
	public void buildSelectQuery(Map<TableEntry, String> whereConditions, Set<EquiJoin> joinConditions, Set<String> fromTables, StringBuffer query){

		Map<String, String> table2Alias = new HashMap<String, String>();
		getTableAlias(table2Alias, getCubeAlias());
		
		
		StringBuffer from = new StringBuffer();
		StringBuffer where = new StringBuffer();
		query = query.append("select * ");
		
		
		addWhereCondition(where, joinConditions, table2Alias);
		addWhereCondition(where, whereConditions, table2Alias);
		addFromConditions(from, fromTables, table2Alias);
		
		query.append(" from ");
		query.append(from);
		query.append(" where ");
		query.append(where);
		

		
	}
	
	public StringBuffer buildSelectQueryForIn(Map<TableEntry, String> whereConditions, Set<EquiJoin> selectFields, Set<EquiJoin> joinConditions, Set<String> fromTables){

		Map<String, String> table2Alias = new HashMap<String, String>();
		getTableAlias(table2Alias, getCubeAlias());
		
		StringBuffer select = new StringBuffer();
		StringBuffer from = new StringBuffer();
		StringBuffer where = new StringBuffer();
		
		
		addSelectCondition(select, selectFields, table2Alias);
		addWhereCondition(where, joinConditions, table2Alias);
		addWhereCondition(where, whereConditions, table2Alias);
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
	
	private String getTableAlias(Map<String, String> table2Alias, String table){
		String alias = table2Alias.get(table);
		if(alias==null){
			alias =  "t"+tableCount;
			table2Alias.put(table,alias);
			tableCount++;
		}
		return alias;
	}
	
	/**
	 * Builds the select statement
	 * @param selectClause the buffer in witch append the clause
	 * @param selectFields the select table entry
	 * @param table2Alias the map column alias
	 */
	private void addSelectCondition(StringBuffer selectClause,  Set<EquiJoin> selectFields, Map<String, String> table2Alias){
		if(selectFields!=null){
			Iterator<EquiJoin> iter = selectFields.iterator();
			while (iter.hasNext()) {
				EquiJoin select = iter.next();
				if(selectClause.length()!=0){
					selectClause.append(" , ");
				}
								
				//the left is the couple table/column of the hierarchy
				String leftEntry = select.leftField.toString(table2Alias);
				selectClause.append(leftEntry);
			}
		}
	}
	
	
	private void addInCondition(StringBuffer subquery,  Set<EquiJoin> selectFields){
		if(selectFields!=null){
			Iterator<EquiJoin> iter = selectFields.iterator();
			while (iter.hasNext()) {
				EquiJoin select = iter.next();
								
				//the left is the couple table/column of the hierarchy
				String rightEntry = select.rightField.toString();
				
				subquery.append(rightEntry);
				subquery.append(" in ");
				
			}
		}
	}
	
	private void addWhereCondition(StringBuffer whereConditions,  Set<EquiJoin> joins, Map<String, String> table2Alias){
		if(joins!=null){
			Iterator<EquiJoin> iter = joins.iterator();
			while (iter.hasNext()) {
				EquiJoin join = iter.next();
				if(whereConditions.length()!=0){
					whereConditions.append(" and ");
				}
				
				
				String leftEntry = null;
				String rightEntry = null;
				
				if(join.leftField.isCubeDimension){
					leftEntry = join.leftField.toString();
				}else{
					leftEntry = join.leftField.toString(table2Alias);
				}
				
				if(join.rightField.isCubeDimension){
					rightEntry = join.rightField.toString();
				}else{
					rightEntry = join.rightField.toString(table2Alias);
				}
				
				whereConditions.append(" ( ");
				whereConditions.append(leftEntry);
				whereConditions.append(" = ");
				whereConditions.append(rightEntry);
				whereConditions.append(" ) ");
			}
		}
		

	}
	
	
	private void addWhereCondition(StringBuffer whereConditionsBuffer, Map<TableEntry, String> whereConditions, Map<String, String> table2Alias){
		
		if(whereConditions!=null){
			Iterator<TableEntry> iter = whereConditions.keySet().iterator();
			while (iter.hasNext()) {
				TableEntry entry = iter.next();
				
				if(whereConditionsBuffer.length()!=0){
					whereConditionsBuffer.append(" and ");
				}
				
				whereConditionsBuffer.append(" ( ");
				whereConditionsBuffer.append(entry.toString(table2Alias));//add the clause for the dimension
				whereConditionsBuffer.append(" = ");
				
				whereConditionsBuffer.append(formatValue(whereConditions.get(entry)));//add the clause for the cube
				whereConditionsBuffer.append(" ) ");
			}
		}
	}
	
	private void addFromConditions(StringBuffer buffer, Set<String> froms, Map<String, String> table2Alias){
		if(froms!=null){
			Iterator<String> iter = froms.iterator();
			while (iter.hasNext()) {
				
				if(buffer.length()!=0){
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
	
	public class TableEntry{
		String column;
		String table;
		boolean isCubeDimension;
		
		public TableEntry(String column, String table) {
			super();
			this.column = column;
			this.table = table;
			isCubeDimension= false;
		}
		public TableEntry(String column) {
			super();
			this.column = column;
			this.table = getCubeAlias();
			isCubeDimension= true;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		public String getTable() {
			return table;
		}
		public void setTable(String table) {
			this.table = table;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((column == null) ? 0 : column.hashCode());
			result = prime * result + ((table == null) ? 0 : table.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TableEntry other = (TableEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (column == null) {
				if (other.column != null)
					return false;
			} else if (!column.equals(other.column))
				return false;
			if (table == null) {
				if (other.table != null)
					return false;
			} else if (!table.equals(other.table))
				return false;
			return true;
		}
		private QueryBuilder getOuterType() {
			return QueryBuilder.this;
		}
		public String toString(Map<String, String> table2Alias){
			
			if(table==null || column==null){
				return "";
			}else{
				return QueryBuilder.this.getTableAlias(table2Alias, table)+"."+column;
			}
		}
		public String toString(){
			
			if(table==null || column==null){
				return "";
			}else{
				return table+"."+column;
			}
		}
	}
	
	public class EquiJoin{
		TableEntry leftField;
		TableEntry rightField;
		public EquiJoin(TableEntry leftField, TableEntry rightField) {
			super();
			this.leftField = leftField;
			this.rightField = rightField;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((leftField == null) ? 0 : leftField.hashCode());
			result = prime * result
					+ ((rightField == null) ? 0 : rightField.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EquiJoin other = (EquiJoin) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (leftField == null) {
				if (other.leftField != null)
					return false;
			} else if (!leftField.equals(other.leftField))
				return false;
			if (rightField == null) {
				if (other.rightField != null)
					return false;
			} else if (!rightField.equals(other.rightField))
				return false;
			return true;
		}
		private QueryBuilder getOuterType() {
			return QueryBuilder.this;
		}
		public String toString(){
			if(leftField==null || rightField==null){
				return "";
			}
			return leftField.toString()+" = "+rightField.toString();
		}
	}
	
	private void executeQuery(String sql){
		try {
			java.sql.Connection  connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodmart_key?user=root&password=root");
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
