/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j.sql;

import it.eng.spagobi.writeback4j.IMemberCoordinates;
import it.eng.spagobi.writeback4j.ISchemaRetriver;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.utils.ByteArrayOutputStream;



/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class AnalysisExporter extends AbstractSqlSchemaManager{

	public AnalysisExporter( ISchemaRetriver retriver){
		this.retriver = retriver;
	}

	public byte[] exportCSV(List<IMemberCoordinates> memberCordinates, Connection connection, Integer version, String fieldSeparator, String lineSeparator)  throws Exception{
		ResultSet resultSet =  executeExportDataQuery(memberCordinates, connection, version);

		ByteArrayOutputStream fos = new ByteArrayOutputStream();   
		Writer out = new OutputStreamWriter(fos);      


		int ncols = resultSet.getMetaData().getColumnCount(); 
		for (int j=1; j<(ncols+1); j++) {     
			out.append(resultSet.getMetaData().getColumnName (j));       
			if (j<ncols) out.append(fieldSeparator); else out.append(lineSeparator);      
		}   


		while (resultSet.next()) {   

			for (int k=1; k<(ncols+1); k++) {   

				out.append(resultSet.getString(k));    

				if (k<ncols) out.append(fieldSeparator); else out.append(lineSeparator);    
			}   

		}  

		return fos.toByteArray();
	}

	private ResultSet executeExportDataQuery(List<IMemberCoordinates> memberCordinates, Connection connection, Integer version) throws Exception{

		//List of where conditions
		Map<TableEntry, String> whereConditions = new HashMap<TableEntry, String>();

		//List of joins
		Set<EquiJoin> joinConditions = new HashSet<EquiJoin>();

		//List of form 
		Set<String> fromTables = new HashSet<String>();


		StringBuffer degenerateDimensionConditions = new StringBuffer();

		for (Iterator<IMemberCoordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {
			IMemberCoordinates aIMemberCordinates = (IMemberCoordinates) iterator.next();
			if(aIMemberCordinates.getTableName()==null){//degenerate dimension
				//create a where in the cube for each level of the degenerate dimension
				Map<TableEntry, String> where = buildWhereConditions(aIMemberCordinates, null);
				Map<String, String> cubeTable2Alias = new HashMap<String, String>();
				cubeTable2Alias.put(null, getCubeAlias());
				addWhereCondition(degenerateDimensionConditions, where, cubeTable2Alias, version);
			}else{
				addJoinConditions(fromTables, joinConditions, aIMemberCordinates, true);
				addInnerDimensionJoinConditions(fromTables, joinConditions, aIMemberCordinates);
			}
		}

		StringBuffer query = new StringBuffer();
		buildQueryForExport(memberCordinates, whereConditions, joinConditions, fromTables, query);
		query.append(" and ");
		query.append(degenerateDimensionConditions);
		String queryString = query.toString();

		SqlQueryStatement exportStatement = new SqlQueryStatement(queryString);
		ResultSet resultset = exportStatement.getValues(connection);
		return resultset;
	}


	private void buildQueryForExport(List<IMemberCoordinates> memberCordinates, Map<TableEntry, String> whereConditions, Set<EquiJoin> joinConditions, Set<String> fromTables, StringBuffer query){

		Map<String, String> table2Alias = new HashMap<String, String>();
		getTableAlias(table2Alias, getCubeAlias());

		StringBuffer from = new StringBuffer();
		StringBuffer where = new StringBuffer();
		query = query.append("select ");

		buildSelectClauseForExport(memberCordinates, table2Alias, query);

		// adding in the from clause the cube
		fromTables.add(retriver.getEditCubeTableName());
		table2Alias.put(retriver.getEditCubeTableName() , getCubeAlias());

		addWhereCondition(where, joinConditions, table2Alias);
		addWhereCondition(where, whereConditions, table2Alias, null);
		addFromConditions(from, fromTables, table2Alias);

		query.append(" from ");
		query.append(from);
		query.append(" where ");
		query.append(where);
	}


	private void buildSelectClauseForExport(List<IMemberCoordinates> memberCordinates, Map<String, String> table2Alias, StringBuffer query){

		List<String> selects = new ArrayList<String>();

		//for each dimension get the columns of each level
		for (Iterator<IMemberCoordinates> iterator = memberCordinates.iterator(); iterator.hasNext();) {
			IMemberCoordinates aIMemberCordinates = (IMemberCoordinates) iterator.next();
			if(aIMemberCordinates.getTableName()!=null){//not degenerate dimension
				List<TableEntry> levels = aIMemberCordinates.getLevels();
				for (Iterator<TableEntry> tableIterator = levels.iterator(); tableIterator.hasNext();) {
					TableEntry entry = (TableEntry) tableIterator.next();
					String cluse = (entry.toString(table2Alias, this));
					if(!selects.contains(cluse)){
						selects.add(cluse);
					}	
				}
			}
		}

		//get the columns of the measures
		List<String> measuresColumns = retriver.getMeasuresColumn();
		for (Iterator<String> iterator = measuresColumns.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			String cluse =(getCubeAlias()+"."+string);
			if(!selects.contains(cluse)){
				selects.add(cluse);
			}
		}

		for (Iterator<String> iterator = selects.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			query.append(string);
			query.append(" ,");
		}

		query.deleteCharAt(query.length()-1);
	}


}
