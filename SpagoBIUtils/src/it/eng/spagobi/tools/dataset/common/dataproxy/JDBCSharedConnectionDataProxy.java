/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.common.dataproxy;

import it.eng.spago.dbaccess.Utils;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.SQLCommand;
import it.eng.spago.dbaccess.sql.mappers.SQLMapper;
import it.eng.spago.dbaccess.sql.result.DataResult;
import it.eng.spago.dbaccess.sql.result.ScrollableDataResult;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * This class is a JDBC data proxy that can be used when you have a java.sql.Connection object and you want to execute a query against 
 * that connection.
 * The connection must be not null and must be active.
 * When the query is executed, THE CONNECTION IS NOT CLOSED: it is caller class's responsibility to close the connection.
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class JDBCSharedConnectionDataProxy extends AbstractDataProxy {
	
	private Connection connection;
	private String statement;
	
	private static transient Logger logger = Logger.getLogger(JDBCSharedConnectionDataProxy.class);
	
	public JDBCSharedConnectionDataProxy(Connection connection) {
		setConnection(connection);
	}
	
	public JDBCSharedConnectionDataProxy(Connection connection, String statement) {
		setConnection(connection);
		setStatement(statement);
	}
	
	/**
	 * Loads the input statement using the input reader.
	 * CONNECTION IS NOT CLOSED AFTER STATEMENT EXECUTED: it is caller class's responsibility to close the connection.
	 * 
	 * @param statement The statement to be executed
	 * @param dataReader The data reader to be used
	 * @return the data store
	 * @throws EMFUserError
	 */
	public IDataStore load(String statement, IDataReader dataReader) throws EMFUserError {
		if(statement != null) {
			setStatement(statement);
		}
		return load(dataReader);
	}
	
	/**
	 * Loads the internal statement using the input reader.
	 * CONNECTION IS NOT CLOSED AFTER STATEMENT EXECUTED: it is caller class's responsibility to close the connection.
	 * 
	 * @param dataReader The data reader to be used
	 * @return the data store
	 * @throws EMFUserError
	 */
	public IDataStore load(IDataReader dataReader) {
		
		IDataStore dataStore = null;
		Object result = null;
		logger.debug("IN");
		
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			if (connection == null) {
				throw new Exception("JDBC connection not set!!");
			}
			if (connection.isClosed()) {
				throw new Exception("JDBC connection is closed!!");
			}
			dataConnection = getDataConnection(connection);
			logger.debug("Executing statemnet ["  + statement + "]");
			sqlCommand = dataConnection.createSelectCommand( statement );
			dataResult = sqlCommand.execute();
			if (dataResult != null) {
				result = (ScrollableDataResult) dataResult.getDataObject();				
			}
			dataStore = dataReader.read( result );
		} catch(Throwable t){
			throw new SpagoBIRuntimeException("An error occurrede while executing query [" + statement + "]", t);
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
			logger.debug("OUT");
		}
		
		return dataStore;
	}
	
	
	private DataConnection getDataConnection(Connection con) {
		DataConnection dataCon = null;
		try {
			Class mapperClass = Class.forName("it.eng.spago.dbaccess.sql.mappers.OracleSQLMapper");
			SQLMapper sqlMapper = (SQLMapper)mapperClass.newInstance();
			dataCon = new DataConnection(con, "2.1", sqlMapper);
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An error occurred while instatiating object [" + DataConnection.class.getName() + "]", t);
		}
		return dataCon;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
