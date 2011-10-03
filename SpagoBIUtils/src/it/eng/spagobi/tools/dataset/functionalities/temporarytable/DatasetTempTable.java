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


import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.persist.DataSetTableDescriptor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;


public class DatasetTempTable {

	private static transient Logger logger = Logger.getLogger(DatasetTempTable.class);

	/**
	 *  Creates a table with columns got from metadata
	 * @param conn
	 * @param meta
	 * @param tableName
	 * @return
	 * @throws Exception
	 */

	public static DataSetTableDescriptor createTemporaryTable(Connection conn, MetaData meta, String tableName){
		logger.debug("IN");

		DataSetTableDescriptor dstd = null;
		boolean result= false;
		Statement st = null;
		String hqlQuery = null;

		try {
			CreateTableCommand createTableCommand = new CreateTableCommand(tableName);

			// run through all columns in order to build the SQL columndefinition
			for (Iterator iterator = meta.getFieldsMeta().iterator(); iterator.hasNext();) {
				IFieldMetaData fieldMeta = (IFieldMetaData) iterator.next();
				createTableCommand.addColumn(fieldMeta);
			}

			// after built columns create SQL Query
			hqlQuery = createTableCommand.createSQLQuery();

			// excute 
			st = conn.createStatement();
			result =  st.execute(hqlQuery);

			dstd = createTableCommand.getDsTableDescriptor();

		} catch (SQLException e) {
			logger.error("Error in excuting statement "+hqlQuery, e);
			System.out.println("Errore "+e);
			return null;
		}
		finally{
			try{
				if( conn != null) conn.close();
				if( st != null)	st.close();
			}
			catch (SQLException e) {
				logger.warn("could not free resources ",e);
			}
		}

		System.out.println("Query "+hqlQuery);
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


