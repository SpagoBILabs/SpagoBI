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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 * TODO: rename JDBCDataProxy to JDBCSpagoDataProxy and use this proxy as the default one for any JDBC dataset
 *
 */
public class JDBCStandardDataProxy extends AbstractDataProxy {
	
	IDataSource dataSource;
	String statement;
	String schema;
	
	private static transient Logger logger = Logger.getLogger(JDBCStandardDataProxy.class);
	
	
	public JDBCStandardDataProxy() { }
	
	public JDBCStandardDataProxy(IDataSource dataSource, String statement) {
		setDataSource(dataSource);
		setStatement(statement);
	}
	
	public JDBCStandardDataProxy(IDataSource dataSource) {
		setDataSource(dataSource);
		setStatement(statement);
	}
	
	
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public IDataStore load(String statement, IDataReader dataReader) throws EMFUserError {
		if(statement != null) {
			setStatement(statement);
		}
		return load(dataReader);
	}
	
	public IDataStore load(IDataReader dataReader) throws EMFUserError {
		
		IDataStore dataStore;
		Connection connection;
		Statement stmt;
		ResultSet resultSet;
		
		logger.debug("IN");
		
		connection = null;
		stmt = null;
		resultSet = null;
		
		try {			
			try {
				connection = getDataSource().getConnection( getSchema() );
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An error occurred while creating connection", t);
			}
			
			try {
				stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An error occurred while creating connection steatment", t);
			}
			
			
	        try {
	        	//get max size 
	        	if(getMaxResults() > 0){
	        		stmt.setMaxRows(getMaxResults());
	        	}
				resultSet = stmt.executeQuery( getStatement() );
				
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An error occurred while executing statement", t);
			}
			
			dataStore = null;
			try {
				dataStore = dataReader.read( resultSet );
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An error occurred while parsing resultset", t);
			}
			
			if( isCalculateResultNumberOnLoadEnabled() ) {
				
			}
			
		} finally {		
			try {
				releaseResources(connection, stmt, resultSet);
			} catch(Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to release allocated resources properly", t);
			}
		}
		
		return dataStore;
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	private void releaseResources(Connection connection, Statement statement, ResultSet resultSet) {
		
		logger.debug("IN");
		
		try {
			logger.debug("Relesing resources ...");
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [resultSet]", e);
				}
				logger.debug("[resultSet] released succesfully");
			}
			
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [statement]", e);
				}
				logger.debug("[statement] released succesfully");
			}
			
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [connection]", e);
				}
				logger.debug("[connection] released succesfully");
			}		
			logger.debug("All resources have been released succesfully");
		} finally {
			logger.debug("OUT");
		}
	}
}
