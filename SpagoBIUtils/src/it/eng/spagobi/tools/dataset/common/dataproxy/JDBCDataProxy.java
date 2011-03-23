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
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.exceptions.ConnectionDsException;
import it.eng.spagobi.tools.dataset.exceptions.QueryDsExecutionException;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * @deprecated 
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JDBCDataProxy extends AbstractDataProxy {

	IDataSource dataSource;
	String statement;
	String schema=null;

	private static transient Logger logger = Logger.getLogger(JDBCDataProxy.class);


	public JDBCDataProxy() {

	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public JDBCDataProxy(IDataSource dataSource, String statement) {
		setDataSource(dataSource);
		setStatement(statement);
	}

	public JDBCDataProxy(IDataSource dataSource) {
		setDataSource(dataSource);
		setStatement(statement);
	}

	public IDataStore load(String statement, IDataReader dataReader) throws EMFUserError {
		if(statement != null) {
			setStatement(statement);
		}
		return load(dataReader);
	}

	public IDataStore load(IDataReader dataReader) {

		IDataStore dataStore = null;
		Object result = null;
		logger.debug("IN");

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		Connection conn = null;
		try{
			conn = dataSource.toSpagoBiDataSource().readConnection(schema); 
			dataConnection = getDataConnection(conn);

		} catch (Throwable t) {
			String dataSourceL ="''";
			if(this.dataSource != null) dataSourceL = "'"+this.dataSource.getLabel()+"'";
			throw new ConnectionDsException("An error occurred while connecting with datasource [" + dataSourceL + "]", t);
		}

		try {
			logger.debug("Executing statemnet ["  + statement + "]");
			sqlCommand = dataConnection.createSelectCommand( statement );
			dataResult = sqlCommand.execute();
			if(dataResult != null){
				result = (ScrollableDataResult) dataResult.getDataObject();				
			}
			dataStore = dataReader.read( result );
		} catch(Throwable t){
			throw new QueryDsExecutionException("An error occurred while executing statement [" + statement + "]", t);
		} finally {
			Utils.releaseResources(dataConnection, sqlCommand, dataResult);
			logger.debug("OUT");
		}

		return dataStore;
	}


	private DataConnection getDataConnection(Connection con) throws EMFInternalError {
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

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}
}
