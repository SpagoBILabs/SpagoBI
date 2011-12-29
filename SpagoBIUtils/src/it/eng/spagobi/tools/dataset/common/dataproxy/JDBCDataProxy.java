/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.common.dataproxy;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JDBCDataProxy extends AbstractDataProxy {

	IDataSource dataSource;
	String statement;
	String schema;
	
	private static transient Logger logger = Logger.getLogger(JDBCDataProxy.class);
	
	
	public JDBCDataProxy() { }
	
	public JDBCDataProxy(IDataSource dataSource, String statement) {
		setDataSource(dataSource);
		setStatement(statement);
	}
	
	public JDBCDataProxy(IDataSource dataSource) {
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
	
	public IDataStore load(IDataReader dataReader) {
		
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
			String dialect = dataSource.getHibDialectClass();
			if(dialect==null){
				 dialect = dataSource.getHibDialectName();
			}
			try {				
				//ATTENTION: For the most db sets the stmt as a scrollable stmt, only for the compatibility with Ingres sets
				//a stmt forward only 			
				if (dialect.contains("Ingres")){
					stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);	
				}else{
					stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);					
				}
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
				logger.error("Trovata!:",t);
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
					if (!connection.isClosed()) {
					    connection.close();
					}
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
