/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.dataset.cache.test;

import java.io.File;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class TestConstants {
	
	public static String workspaceFolder = "C:/Users/cortella/workspaceJEE";
	
	public static String RESOURCE_PATH = workspaceFolder+"/SpagoBICockpitEngine/test/resources/";
	public static String WEBCONTENT_PATH = workspaceFolder+"/SpagoBICockpitEngine/WebContent";
	public static String AF_CONFIG_FILE = "/WEB-INF/conf/master.xml";
	
	public enum DatabaseType { MYSQL, POSTGRES, ORACLE, SQLSERVER };
	
	public static boolean enableTestsOnMySql = true;
	public static boolean enableTestsOnPostgres = false;
	public static boolean enableTestsOnOracle = false;
	public static boolean enableTestsOnSQLServer = false;



	// =======================================================
	// MYSQL 
	// =======================================================
	public static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static String MYSQL_DIALECT_CLASS = "org.hibernate.dialect.MySQLInnoDBDialect";
	public static String MYSQL_DIALECT_NAME = "sbidomains.nm.mysql";

	//-------------
	// FOR WRITING
	//-------------
	public static String MYSQL_LABEL_WRITING = "datasetTest_mysql_write";
	public static String MYSQL_URL_WRITING = "jdbc:mysql://localhost:3306/writetestschema";
	public static String MYSQL_USER_WRITING = "root";
	public static String MYSQL_PWD_WRITING = "root";
	
	//-------------
	// FOR READING
	//-------------
	public static String MYSQL_LABEL_READING = "datasetTest_mysql_read";
	public static String MYSQL_URL_READING = "jdbc:mysql://localhost:3306/foodmart";
	public static String MYSQL_USER_READING = "root";
	public static String MYSQL_PWD_READING = "root";
	
	// =======================================================
	// POSTGRES
	// =======================================================
	public static String POSTGRES_DRIVER = "org.postgresql.Driver";
	public static String POSTGRES_DIALECT_CLASS = "org.hibernate.dialect.PostgreSQLDialect";
	public static String POSTGRES_DIALECT_NAME = "sbidomains.nm.postgresql";

	//-------------
	// FOR WRITING
	//-------------
	public static String POSTGRES_LABEL_WRITING = "datasetTest_postgres_write";
	public static String POSTGRES_URL_WRITING = "jdbc:postgresql://localhost:5433/testwrite";
	public static String POSTGRES_USER_WRITING = "postgres";
	public static String POSTGRES_PWD_WRITING = "postgres";
	
	//-------------
	// FOR READING
	//-------------
	public static String POSTGRES_LABEL_READING = "datasetTest_postgres_read";
	public static String POSTGRES_URL_READING = "jdbc:postgresql://localhost:5433/testwrite";
	public static String POSTGRES_USER_READING = "postgres";
	public static String POSTGRES_PWD_READING = "postgres";
	
	// =======================================================
	// ORACLE
	// =======================================================
	public static String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
	public static String ORACLE_DIALECT_CLASS = "org.hibernate.dialect.Oracle9Dialect";
	public static String ORACLE_DIALECT_NAME = "sbidomains.nm.oracle_9i10g";

	//-------------
	// FOR WRITING
	//-------------
	public static String ORACLE_LABEL_WRITING = "datasetTest_oracle_write";
	public static String ORACLE_URL_WRITING = "jdbc:oracle:thin:@172.27.1.83:1521:repo"; //sibilla2
	public static String ORACLE_USER_WRITING = "bilancio_mi";
	public static String ORACLE_PWD_WRITING = "bilancio_mi";
	
	//-------------
	// FOR READING
	//-------------
	public static String ORACLE_LABEL_READING = "datasetTest_oracle_read";
	public static String ORACLE_URL_READING = "jdbc:oracle:thin:@172.27.1.83:1521:repo";
	public static String ORACLE_USER_READING = "bilancio_mi";
	public static String ORACLE_PWD_READING = "bilancio_mi";
	
	// =======================================================
	// SQL SERVER
	// =======================================================
	public static String SQLSERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String SQLSERVER_DIALECT_CLASS = "org.hibernate.dialect.SQLServerDialect";
	public static String SQLSERVER_DIALECT_NAME = "sbidomains.nm.sqlserver";

	//-------------
	// FOR WRITING
	//-------------
	public static String SQLSERVER_LABEL_WRITING = "datasetTest_sqlserver_write";
	public static String SQLSERVER_URL_WRITING = "jdbc:sqlserver://172.27.1.80:1410;databaseName=testSpagoBI"; //server Padova
	public static String SQLSERVER_USER_WRITING = "brasile";
	public static String SQLSERVER_PWD_WRITING = "spagobi";
	
	//-------------
	// FOR READING
	//-------------
	public static String SQLSERVER_LABEL_READING = "datasetTest_sqlserver_read";
	public static String SQLSERVER_URL_READING = "jdbc:sqlserver://172.27.1.80:1410;databaseName=testSpagoBI";
	public static String SQLSERVER_USER_READING = "brasile";
	public static String SQLSERVER_PWD_READING = "spagobi";
}
