/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.functionalities.temporarytable;


import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.DataSetTableDescriptor;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


public class DatasetTempTable {

	private static transient Logger logger = Logger.getLogger(DatasetTempTable.class);

	/**
	 * Creates a table with columns got from metadata.
	 * PAY ATTENTION TO THE FACT THAT THE INPUT CONNECTION ISN'T CLOSED!!!!!
	 * @param conn
	 * @param meta
	 * @param tableName
	 * @return
	 * @throws Exception
	 */

	public static DataSetTableDescriptor createTemporaryTable(Connection conn, IMetaData meta, String tableName) {
		logger.debug("IN");

		DataSetTableDescriptor dstd = null;
		Statement st = null;
		String sqlQuery = null;

		try {
			CreateTableCommand createTableCommand = new CreateTableCommand(tableName, conn.getMetaData().getDriverName());

			// run through all columns in order to build the SQL columndefinition
			int count = meta.getFieldCount();
			for (int i = 0 ; i < count ; i++) {
				IFieldMetaData fieldMeta = meta.getFieldMeta(i);
				createTableCommand.addColumn(fieldMeta);
			}

			// after built columns create SQL Query
			sqlQuery = createTableCommand.createSQLQuery();

			// execute 
			st = conn.createStatement();
			st.execute(sqlQuery);

			dstd = createTableCommand.getDsTableDescriptor();

		} catch (SQLException e) {
			logger.error("Error in excuting statement " + sqlQuery, e);
			throw new SpagoBIRuntimeException("Error creating temporary table", e);
		}
		finally {
			try {
				if ( st != null ) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("could not free resources ", e);
			}
		}
		logger.debug("OUT");
		return dstd;
	}




	//	public static void main(String[] args) throws Exception {
	//
	//		MetaData meta = new MetaData();
	//
	//		IFieldMetaData fieldMetaDataIntero = new FieldMetadata();
	//		fieldMetaDataIntero.setAlias("Colonna alias intero");
	//		fieldMetaDataIntero.setName("ColonnaIntera");
	//		fieldMetaDataIntero.setType(java.math.BigDecimal.class);
	//
	//		IFieldMetaData fieldMetaDataStringa = new FieldMetadata();
	//		fieldMetaDataStringa.setAlias("Colonna alias stringa");
	//		fieldMetaDataStringa.setName("ColonnaStringa");
	//		fieldMetaDataStringa.setProperty("size", 50);
	//		fieldMetaDataStringa.setType(java.lang.String.class);
	//		
	//		IFieldMetaData fieldMetaDataBool = new FieldMetadata();
	//		fieldMetaDataBool.setAlias("Colonna alias Bool");
	//		fieldMetaDataBool.setName("ColonnaBool");
	//		fieldMetaDataBool.setType(java.lang.Boolean.class);
	//
	//		IFieldMetaData fieldMetaDataStringa2 = new FieldMetadata();
	//		fieldMetaDataStringa2.setAlias("Colonna alias text");
	//		fieldMetaDataStringa2.setName("ColonnaTextCLOb");
	//		fieldMetaDataStringa2.setType(java.lang.String.class);
	//
	//		IFieldMetaData fieldMetaDataFloat = new FieldMetadata();
	//		fieldMetaDataFloat.setAlias("Colonna alias text");
	//		fieldMetaDataFloat.setName("ColonnaFloatt");
	//		fieldMetaDataFloat.setProperty("precision", 10);
	//		fieldMetaDataFloat.setProperty("scale", 5);
	//		fieldMetaDataFloat.setType(java.lang.Float.class);
	//		
	//		IFieldMetaData fieldMetaDataDate = new FieldMetadata();
	//		fieldMetaDataDate.setAlias("Colonna alias Date");
	//		fieldMetaDataDate.setName("ColonnaDate");
	//		fieldMetaDataDate.setType(java.sql.Date.class);
	//		
	//		IFieldMetaData fieldMetaDataTime = new FieldMetadata();
	//		fieldMetaDataTime.setAlias("Colonna alias Time");
	//		fieldMetaDataTime.setName("ColonnaTime");
	//		fieldMetaDataTime.setType(oracle.sql.TIMESTAMP.class);
	//		
	//		
	//		meta.addFiedMeta(fieldMetaDataIntero);
	//		meta.addFiedMeta(fieldMetaDataStringa);
	//		meta.addFiedMeta(fieldMetaDataStringa2);
	//		meta.addFiedMeta(fieldMetaDataFloat);
	//		meta.addFiedMeta(fieldMetaDataBool);
	//		meta.addFiedMeta(fieldMetaDataDate);
	//		meta.addFiedMeta(fieldMetaDataTime);
	//		
	//		Connection connection = null;
	//		try{
	////			Class.forName( "com.mysql.jdbc.Driver" );
	////			connection = DriverManager.getConnection("jdbc:mysql://localhost/foodmart", "root", "admin");
	//			Class.forName( "oracle.jdbc.OracleDriver" );
	//			connection = DriverManager.getConnection("jdbc:oracle:thin:@sibilla2:1521:REPO", "spagobi", "bispago");
	//		}
	//		catch (Exception e) {
	//			// TODO: handle exception
	//		}
	//
	//		// map alias to index
	//
	//		int r =new Random().nextInt();
	//		if(r <0 ) r*=-1;
	//		String tableName = "DatasetName"+r;
	//		System.out.println(tableName);
	//		DatasetTempTable.createTemporaryTable(connection, meta, tableName);
	//	}



}


