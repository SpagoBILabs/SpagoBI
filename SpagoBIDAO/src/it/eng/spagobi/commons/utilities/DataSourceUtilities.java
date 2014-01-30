/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.utilities;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.dbaccess.sql.DataConnection;
import it.eng.spago.dbaccess.sql.mappers.SQLMapper;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;


public class DataSourceUtilities {
	private static transient Logger logger = Logger.getLogger(DataSourceUtilities.class);
	
	/**
	 * This method, based on the data sources table, gets a
	 * database connection and return it.
	 * 
	 * @param dsLabel the ds label
	 * 
	 * @return the database connection
	 * 
	 * N.B. You MUST use this method only when RequestContainer is OK.
	 */
	public Connection getConnection(RequestContainer requestContainer,String dsLabel) {
		Connection connection =  null;
		//calls implementation for gets data source object
		
		SpagoBiDataSource ds = getDataSourceByLabel(dsLabel);
		logger.debug("Schema Attributes:"+ ds.getSchemaAttribute());
		String schema=UserUtilities.getSchema(ds.getSchemaAttribute(),requestContainer);
		logger.debug("Schema:"+ schema);
		
		try {
			connection = ds.readConnection(schema);
		} catch (NamingException e) {
			logger.error("JNDI error", e);
		} catch (SQLException e) {
			logger.error("Cannot retrive connection", e);
		} catch (ClassNotFoundException e) {
			logger.error("Driver not found", e);
		}
		
		return connection;
	}
	
	/**
	 * use this method in service implementation. If RequestContainer isn't correct.
	 * @param profile
	 * @param dsLabel
	 * @return
	 */
	public Connection getConnection(IEngUserProfile profile,String dsLabel) {
		Connection connection =  null;
		//calls implementation for gets data source object
		
		
		SpagoBiDataSource ds = getDataSourceByLabel(dsLabel);
		logger.debug("Schema Attribute:"+ ds.getSchemaAttribute());
		String schema=null;
		if (profile!=null){
			schema=UserUtilities.getSchema(ds.getSchemaAttribute(),profile);
			logger.debug("Schema:"+ schema);
		}
		try {
			connection = ds.readConnection(schema);
		} catch (NamingException e) {
			logger.error("JNDI error", e);
		} catch (SQLException e) {
			logger.error("Cannot retrive connection", e);
		} catch (ClassNotFoundException e) {
			logger.error("Driver not found", e);
		}
		
		return connection;
	}	
	/**
	 * Creates a ago DataConnection object starting from a sql connection.
	 * 
	 * @param con Connection to the export database
	 * 
	 * @return The Spago DataConnection Object
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public DataConnection getDataConnection(Connection con) throws EMFInternalError {
		DataConnection dataCon = null;
		try {
			Class mapperClass = Class.forName("it.eng.spago.dbaccess.sql.mappers.OracleSQLMapper");
			SQLMapper sqlMapper = (SQLMapper)mapperClass.newInstance();
			dataCon = new DataConnection(con, "2.1", sqlMapper);
		} catch(Exception e) {
			logger.error("Error while getting Data Source " + e);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "cannot build spago DataConnection object");
		}
		return dataCon;
	}
	
	/*
	 * Methods copied from DataSourceSupplier for DAO refactoring
	 */
	/**
	 * Gets the data source by label.
	 * 
	 * @param dsLabel
	 *            the ds label
	 * 
	 * @return the data source by label
	 */
	public SpagoBiDataSource getDataSourceByLabel(String dsLabel) {
		logger.debug("IN");
		SpagoBiDataSource sbds = new SpagoBiDataSource();

		// gets data source data from database
		try {
			IDataSource ds = DAOFactory.getDataSourceDAO()
					.loadDataSourceByLabel(dsLabel);
			if (ds == null) {
				logger.warn("The data source with label " + dsLabel
						+ " is not found on the database.");
				return null;
			}
			sbds = toSpagoBiDataSource(ds);

		} catch (Exception e) {
			logger.error("The data source is not correctly returned", e);
		}
		logger.debug("OUT");
		return sbds;
	}

	private SpagoBiDataSource toSpagoBiDataSource(IDataSource ds)
	throws Exception {
		SpagoBiDataSource sbds = new SpagoBiDataSource();
		sbds.setLabel(ds.getLabel());
		sbds.setJndiName(ds.getJndi());
		sbds.setUrl(ds.getUrlConnection());
		sbds.setUser(ds.getUser());
		sbds.setPassword(ds.getPwd());
		sbds.setDriver(ds.getDriver());
		sbds.setMultiSchema(ds.getMultiSchema());
		sbds.setSchemaAttribute(ds.getSchemaAttribute());
		// gets dialect informations
		IDomainDAO domaindao = DAOFactory.getDomainDAO();
		Domain doDialect = domaindao.loadDomainById(ds.getDialectId());
		sbds.setHibDialectClass(doDialect.getValueCd());
		sbds.setHibDialectName(doDialect.getValueName());
		sbds.setReadOnly(ds.checkIsReadOnly());
		sbds.setWriteDefault(ds.checkIsWriteDefault());
		return sbds;
	}	

}
