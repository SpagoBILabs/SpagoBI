/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j.sql;

import it.eng.spagobi.engines.whatif.common.WhatIfConstants;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.writeback4j.ISchemaRetriver;
import it.eng.spagobi.writeback4j.sql.dbdescriptor.FoodmartDbDescriptor;
import it.eng.spagobi.writeback4j.sql.dbdescriptor.IDbSchemaDescriptor;

import java.util.Iterator;

import org.apache.log4j.Logger;


/**
 *  @author Giulio Gavardi (giulio.gavardi@eng.it)
 *
 */
public class VersionManagementStatements {
	
	
	ISchemaRetriver retriever;
	IDataSource dataSource;
	String editCubeTableName;
	
	
	public static transient Logger logger = Logger.getLogger(VersionManagementStatements.class);
	
	public VersionManagementStatements( ISchemaRetriver retriever, IDataSource dataSource){
		this.retriever = retriever;
		this.dataSource = dataSource;
		
		editCubeTableName = retriever.getEditCubeTableName();
		logger.debug("Edit table name is: "+editCubeTableName);

	}

	
	
	public 	String buildGetLastVersion(){
		logger.debug("IN");
		String statement = "select MAX("+WhatIfConstants.VERSION_COLUMN_NAME+") as "+WhatIfConstants.VERSION_COLUMN_NAME+" from "+editCubeTableName;
		logger.debug("OUT");
		return statement;
	}
	
	


	
	
	public String buildInserttoDuplicateData(Integer lastVersion){
	logger.debug("IN");
	
	
	IDbSchemaDescriptor descriptor = new FoodmartDbDescriptor();

	
	String columnsListString="";
	String columnsListStringVersionWritten="";
	
	for (Iterator iterator = descriptor.getColumnNames(editCubeTableName).iterator(); iterator.hasNext();) {
		String s = (String) iterator.next();
		
		if(s.equals(WhatIfConstants.VERSION_COLUMN_NAME)){
			columnsListString+=" "+s+" ";
			columnsListStringVersionWritten+=" "+(lastVersion+1)+" ";
			
		}
		else{
		columnsListString+=" "+s+" ";
		columnsListStringVersionWritten+=" "+s+" ";
		
		}
		
		
		if(iterator.hasNext()){
			columnsListString+=",";
			columnsListStringVersionWritten+=",";
		}
	}
	
	
	
	logger.debug("Columns of virtual table are: "+columnsListString);
	
	
	String statement = "";
	
	statement = "insert into "+editCubeTableName+" ("+columnsListString+") "
			+" select "+columnsListStringVersionWritten+" from "+editCubeTableName
			+" where "+WhatIfConstants.VERSION_COLUMN_NAME+"="+(lastVersion);

	logger.debug("Statement for duplicating data of last version: "+statement);

	logger.debug("OUT");
	return statement;
	}
	


	/*
	
	public String buildInsertInTemporaryStatement(Integer actualVersion){
	logger.debug("IN");
	
	String statement = "";
	
	statement = "insert into "+editCubeTemporaryTableName+" select * from "+editCubeTableName+" where "+MondrianSchemaRetriver.VERSION_COLUMN_NAME+"="+actualVersion;
	logger.debug("Statement for inserting in temporary table: "+statement);

	logger.debug("OUT");
	return statement;
	}
	
	
	public String buildUpdateVersionNumberStatement(Integer lastVersion){	
		logger.debug("IN");
		String statement = "";
		
		statement = "update "+editCubeTemporaryTableName+" set "+MondrianSchemaRetriver.VERSION_COLUMN_NAME+"="+(lastVersion+1);
	
		logger.debug("Statement for updating actual version is : "+statement);
		logger.debug("OUT");
		return statement;
	}
	

	public String buildInsertInVirtualStatement(Integer lastVersion){
	logger.debug("IN");
	
	String statement = "";
	
	statement = "insert into "+editCubeTableName+" select * from "+editCubeTemporaryTableName+" where "+MondrianSchemaRetriver.VERSION_COLUMN_NAME+"="+(lastVersion+1);

	logger.debug("Statement for inserting into virtual table the content of temporary table: "+statement);

	logger.debug("OUT");
	return statement;
	}
	
	public String buildDeleteTemporaryStatement(){
	logger.debug("IN");
	
	String statement = "";
	
	statement = "delete from "+editCubeTemporaryTableName;

	logger.debug("Statement for truncateing temporary table is: "+statement);

	logger.debug("OUT");
	return statement;
	}

	

	public String buildStatement(Long actualVersion){
	logger.debug("IN");
	
	String statement = "";
	
	// get table name
	String editCubeTableName = retriever.getEditCubeTableName();
	logger.debug("Edit table name is: "+editCubeTableName);
	
	// get all columns names
	List<String> columnNames = retriever.getColumnNamesList();
	String columnsListString ="";
	for (int i = 0; i < columnNames.size(); i++) {
		String name = columnNames.get(i);
		if(i > 0){
			columnsListString += ", ";
		}
		columnsListString += name;
	}
	
	logger.debug("List of columns of edit table is "+columnsListString);
	
	
	// add version column
	String columnsListToInsert = columnsListString+", "+MondrianSchemaRetriver.VERSION_COLUMN_NAME;
	String columnsListToSelect = columnsListString+", "+(actualVersion+1);
	
	
	statement = "insert into "+editCubeTableName+" ("+columnsListToInsert+") select "+columnsListToSelect+" from "+editCubeTableName+";";

	logger.debug("Statement built is "+statement);
	
	logger.debug("OUT");
	return statement;
	}
	*/
	
	
	
	

	
}
