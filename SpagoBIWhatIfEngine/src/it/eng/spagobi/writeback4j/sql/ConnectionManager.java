/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Giulio Gavardi (giulio.gavardi@eng.it)
 */
package it.eng.spagobi.writeback4j.sql;

import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;

import org.apache.log4j.Logger;

public class ConnectionManager {

	public static transient Logger logger = Logger.getLogger(ConnectionManager.class);

	private IDataSource dataSource;
	private java.sql.Connection connection = null;


	public ConnectionManager(IDataSource dataSource){
		super();
		this.dataSource = dataSource;
	}

	/**
	 * Open the connection
	 * @return
	 * @throws SpagoBIEngineException
	 */
	public Connection openConnection() throws SpagoBIRuntimeException{
		logger.debug("IN");
		try{		
			connection = dataSource.getConnection( null );
		} catch (Exception e) {
		logger.error("Error in opening connection to datasource "+dataSource.getLabel());
			throw new SpagoBIRuntimeException("Error in opening connection to datasource "+dataSource.getLabel(), e);	
		} 
		logger.debug("OUT");
		return connection;

	}

	/**
	 * Close the connection
	 * @throws SpagoBIEngineException
	 */

	public void closeConnection(){
		logger.debug("IN");

		try {
			if(connection != null){
				connection.close();
			}
		} catch (Exception e) {
			logger.error("Error in closing connection to "+dataSource.getLabel());
		} 
		logger.debug("OUT");

	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public java.sql.Connection getConnection() {
		return connection;
	}

	public void setConnection(java.sql.Connection connection) {
		this.connection = connection;
	}





}
