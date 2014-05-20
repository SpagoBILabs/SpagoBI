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
 *  @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class DefaultWeightedAllocationAlgorithmPersister {
	
	
	ISchemaRetriver retriver;
	private int tableCount = 0;
	IDataSource dataSource;
	
	public static transient Logger logger = Logger.getLogger(DefaultWeightedAllocationAlgorithmPersister.class);
	
	public DefaultWeightedAllocationAlgorithmPersister( ISchemaRetriver retriver, IDataSource dataSource){
		this.retriver = retriver;
		this.dataSource = dataSource;
	}
	
	public void executeProportionalUpdate(Member[] members, double prop) throws SpagoBIEngineException{
		//list of the coordinates for the members
		List<IMemberCoordinates> memberCordinates = new ArrayList<IMemberCoordinates>();
		
		//init the query with the update set statement
		StringBuffer query = new StringBuffer();
		
		//gets the measures and the coordinates of the dimension members 
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
	//	buildProportionalUpdateOneSubqueryForDimension(memberCordinates, query);
	}
	
	private void buildProportionalUpdateOneSubqueryForDimension(List<IMemberCoordinates> memberCordinates, StringBuffer query) throws SpagoBIEngineException{
		
		//List of where conditions
		Map<TableEntry, String> whereConditions = new HashMap<TableEntry, String>();
		
		//List of joins
		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();
		
		//List of form 
		Set<String> fromTables = new HashSet<String>();
		
		query.append(" where exists ( ");
		
		for (Iterator<IMemberCoordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {
			IMemberCoordinates aIMemberCordinates = (IMemberCoordinates) iterator.next();
			whereConditions.putAll(buildWhereConditions(aIMemberCordinates, fromTables));
			addJoinConditions(fromTables, joinConditions, aIMemberCordinates);
			addInnerDimensionJoinConditions(fromTables, joinConditions, aIMemberCordinates);
		}
		
		buildSelectQuery(whereConditions, joinConditions, fromTables, query);
		
		query.append(" ) ");
		
		String queryString = query.toString();
		
		ConnectionManager connManager = new ConnectionManager(dataSource);
		Connection connection = connManager.openConnection(); 
		
		SqlUpdateStatement updateStatement = new SqlUpdateStatement(queryString);
		updateStatement.executeStatement(connection);
	
		connManager.closeConnection();
	}
	
	private void buildProportionalUpdateSingleSubquery(List<IMemberCoordinates> memberCordinates, StringBuffer query) throws SpagoBIEngineException{
		
		//List of where conditions
		Map<TableEntry, String> whereConditions;
		
		//List of joins
		Set<EquiJoin> selectFields;
		
		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();

		//List of form 
		Set<String> fromTables;
		
		query.append(" where ");
		
		boolean first = true;
		for (Iterator<IMemberCoordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {

			IMemberCoordinates aIMemberCordinates = (IMemberCoordinates) iterator.next();
			if(!aIMemberCordinates.isAllMember()){
				whereConditions = new HashMap<TableEntry, String>();
				selectFields = new HashSet<EquiJoin>();
				fromTables = new HashSet<String>();
				joinConditions = new HashSet<EquiJoin>();
				
				
				whereConditions.putAll(buildWhereConditions(aIMemberCordinates, fromTables));
				addJoinConditions(fromTables, selectFields , aIMemberCordinates);
				addInnerDimensionJoinConditions(fromTables, joinConditions, aIMemberCordinates);
				
				StringBuffer subquery = buildSelectQueryForIn(whereConditions, selectFields, joinConditions, fromTables);
				
				if (!first){
					query.append(" and ");
				}
				first = false;
				query.append(subquery);
			}
		}
		
		
		
		

		ConnectionManager connManager = new ConnectionManager(dataSource);
		Connection connection = connManager.openConnection(); 

		String queryString = query.toString();
		
		SqlUpdateStatement updateStatement = new SqlUpdateStatement(queryString);
		updateStatement.executeStatement(connection);
	
		connManager.closeConnection();
	}
	
	
	private String formatValue(String value){
		return "'"+value+"'";
		
	}
	
	/**
	 * Build the update statement for the measure
	 * @param buffer the buffer of the query
	 * @param measure the measure to update
	 * @param prop the ratio 
	 */
	private void buildUpdate(StringBuffer buffer, Member measure, double prop) throws SpagoBIEngineException{
		String measureColumn = null;
		try {
			measureColumn = retriver.getMeasureColumn(measure);
		} catch (SpagoBIEngineException e) {
			logger.error("Error loading the column for the table measure "+measure.getName(), e );
			throw new SpagoBIEngineException("Error loading the column for the table measure "+measure.getName(), e );
		}
		
		buffer.append("update ");
		buffer.append(retriver.getEditCubeTableName() );
		buffer.append(" "+getCubeAlias());
		buffer.append(" set "+measureColumn+" = "+measureColumn+"*"+prop);
		
	}
	
	public Map<TableEntry, String> buildWhereConditions(IMemberCoordinates cordinates, Set<String> from){
		Map<TableEntry, String> condition2Value = new HashMap<TableEntry, String>();
		Map<TableEntry,Member> lelvel2Member = cordinates.getLevel2Member();
		
		if(lelvel2Member!=null && !cordinates.isAllMember()){
			Iterator<TableEntry> i = lelvel2Member.keySet().iterator();
			while(i.hasNext()){
				TableEntry aLevel = i.next();
				from.add(aLevel.table);
				condition2Value.put(new TableEntry(aLevel.column, aLevel.table), lelvel2Member.get(aLevel).getName()); 
			}
		}
		
		return condition2Value;
	}

	
	public void addJoinConditions(Set<String> from, Set<EquiJoin> joins, IMemberCoordinates cordinates){
		if(!cordinates.isAllMember()){
			String tableName = cordinates.getTableName();
			
			TableEntry hierarchyTableEntry = new TableEntry(cordinates.getPrimaryKey(), tableName);
			
			TableEntry cubeTableEntry = new TableEntry(cordinates.getForeignKey(), getCubeAlias(), true); 
			joins.add(new EquiJoin(hierarchyTableEntry, cubeTableEntry));
			
			from.add(tableName );	
		}

	}
	
	
	public void addInnerDimensionJoinConditions(Set<String> from, Set<EquiJoin> joins, IMemberCoordinates cordinates){
	
		EquiJoin coordinateInnerJoin =  cordinates.getInnerDimensionJoinConditions();
		if(coordinateInnerJoin!=null){
			joins.add(coordinateInnerJoin);
			from.add(coordinateInnerJoin.leftField.getTable() );
			from.add(coordinateInnerJoin.rightField.getTable() );
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
	
	public String getTableAlias(Map<String, String> table2Alias, String table){
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
				String leftEntry = select.leftField.toString(table2Alias, this);
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
					leftEntry = join.leftField.toString(table2Alias, this);
				}
				
				if(join.rightField.isCubeDimension){
					rightEntry = join.rightField.toString();
				}else{
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
	
	
	private void addWhereCondition(StringBuffer whereConditionsBuffer, Map<TableEntry, String> whereConditions, Map<String, String> table2Alias){
		
		if(whereConditions!=null){
			Iterator<TableEntry> iter = whereConditions.keySet().iterator();
			while (iter.hasNext()) {
				TableEntry entry = iter.next();
				
				if(whereConditionsBuffer.length()!=0){
					whereConditionsBuffer.append(" and ");
				}
				
				whereConditionsBuffer.append(" ( ");
				whereConditionsBuffer.append(entry.toString(table2Alias, this));//add the clause for the dimension
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
	
}
