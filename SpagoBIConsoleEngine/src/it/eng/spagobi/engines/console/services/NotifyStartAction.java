/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.console.services;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.ConsoleEngineRuntimeException;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;




/**
 * @author Andrea Gioia (andrea.gioiao@eng.it)
 */
public class NotifyStartAction extends AbstractConsoleEngineAction {
	
	
	public static final String SERVICE_NAME = "NOTIFY_START_ACTION";
	
	// request parameters
	public static String USER_ID = "userId";
	public static String ID_SERVICE = "idService";
	public static String RESOURCE_NAME = "resourceName";
	public static String PID = "pid";
	public static String FORMAT_DATE = "formatDate";
	

		
	// logger component
	private static Logger logger = Logger.getLogger(NotifyStartAction.class);
	
	public void service(SourceBean request, SourceBean response) {
		
	
		String user;
		Integer idService = null;
		String resourceName = null; // es. 'azienda_0'
		String pid = null; // es. 'azienda_0'
		String formatDate = null; //es. dd-mm-yyyy HH:mi:ss
		
		IDataSource dataSource;
		
		Connection conn;
		PreparedStatement stmt;
		String sql;     
		
		Double idServiceInstanceParam = null;
		Integer idServiceParam = null;
		//Date sysDate = null;
		Timestamp sysDate = null;
		String pidParam = null;
		Integer resourceIdParam = null;
		

		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("SpagoBI_Console.NotifyStartAction.service");	

		conn = null;
		stmt = null;
		try {
			super.service(request,response);
		
			
			//check for mandatory parameters 						
			user = getAttributeAsString( USER_ID );
			logger.debug("Parameter [" + USER_ID + "] is equals to [" + user + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( user ), "Parameter [" + USER_ID + "] cannot be null or empty");
			
			idService = this.getAttributeAsInteger( ID_SERVICE );
			logger.debug("Parameter [" + ID_SERVICE + "] is equals to [" + idService + "]");			
			Assert.assertNotNull(idService, "Parameter [" + ID_SERVICE + "] cannot be null or empty");
			
			pid = getAttributeAsString( PID );
			logger.debug("Parameter [" + PID + "] is equals to [" + pid + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( pid ), "Parameter [" + PID + "] cannot be null or empty");
			
			resourceName = getAttributeAsString( RESOURCE_NAME );
			logger.debug("Parameter [" + RESOURCE_NAME + "] is equals to [" + resourceName + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( resourceName ), "Parameter [" + RESOURCE_NAME + "] cannot be null or empty");
			
			formatDate = getAttributeAsString( FORMAT_DATE );
			logger.debug("Parameter [" + FORMAT_DATE + "] is equals to [" + formatDate + "]");			

			
			dataSource = getConsoleEngineInstance().getDataSource();	
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			
			sql = "INSERT INTO SERVICE_INSTANCE " 
			    + "(ID_SERVICE_INSTANCE, " +					// 1
			    		"ID_SERVICE, " +						// 2
			    		"ID_TARGET_SERVICE_INSTANCE, " +		// 3
			    		"MESSAGE_ID, " +						// 4
			    		"CORRELATION_ID, " +					// 5
			    		"STATE, " +								// 6
			    		"STARTDATE, " +							// 7
			    		"ENDDATE, " +							// 8
			    		"LABEL, " +								// 9
			    		"MARKER, " +							// 10
			    		"MONITOR_CHECK, " +						// 11
			    		"ERRORS_CHECK, " +						// 12
			    		"ALARMS_CHECK, " +						// 13
			    		"VIEWS_CHECK, " +						// 14
			    		"USER_ID, " +							// 15
			    		"TS_UPDATE, " +							// 16
			    		"RESOURCE_ID) "							// 17
			    + "VALUES "
			    + " (?, ?, NULL, NULL, NULL, " +
			    	"0, ?, to_date('12/12/9999','dd/MM/yyyy'), ?, 0, " +
			    	"0, 0, 0, 0, NULL, ?, ?)";
						
			if (formatDate != null){
				//sets the format specified into template
				String dateStr = new SimpleDateFormat(formatDate).format(new Timestamp(System.currentTimeMillis())); //HH --> hh24
				sysDate = Timestamp.valueOf(dateStr);
			}else{
				sysDate = new Timestamp( System.currentTimeMillis() );
			}
			
			// sysDate = new Date( System.currentTimeMillis() );
			logger.debug("Query parameter [sysDate] is equals to [" + sysDate + "]");			
			
			idServiceInstanceParam = getIdServiceInstance(conn);
			logger.debug("Query parameter [idServiceInstanceParam] is equals to [" + idServiceInstanceParam + "]");			
			
			idServiceParam = idService;
			logger.debug("Query parameter [idServiceParam] is equals to [" + idServiceParam + "]");		
		
			pidParam = pid;
			logger.debug("Query parameter [pid] is equals to [" + pidParam + "]");			
			
			resourceIdParam = getResourceId(conn, resourceName);
			logger.debug("Query parameter [resourceIdParam] is equals to [" + resourceIdParam + "]");			
		
			try {
				stmt = conn.prepareStatement(sql); 
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to create a prepared statement for query [" + sql + "]", e);
			} 
			
			try {
				stmt.setDouble(1, idServiceInstanceParam);
				stmt.setInt(2, idServiceParam);
				//stmt.setDate(3, sysDate);
				stmt.setTimestamp(3, sysDate);
				stmt.setString(4, pidParam);
				//stmt.setDate(5, sysDate);
				stmt.setTimestamp(5, sysDate);
				stmt.setInt(6, resourceIdParam.intValue());
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to set the value of the parameter [resourceName] to [" + resourceName + "] in query [" + sql + "]", e);
			}
			logger.debug("Prameters has been  succesfully replaced in statement  [" + stmt.toString() + "]");
			
			try {
				stmt.executeUpdate(); 
			} catch (SQLException e) {
			    logger.error("Query parameter [pid] is equals to [" + pidParam + "]");
			    logger.error("Query parameter [resourceIdParam] is equals to [" + resourceIdParam + "]");
			    logger.error("Query parameter [idServiceParam] is equals to [" + idServiceParam + "]"); 
			    logger.error("Query parameter [sysDate] is equals to [" + sysDate + "]"); 
			    logger.error("Query parameter [idServiceInstanceParam] is equals to [" + idServiceInstanceParam + "]"); 
				throw new ConsoleEngineRuntimeException("Impossible to execute statement [" + stmt.toString() + "]", e);
			}
			logger.debug("Statement [" + stmt.toString() + "] has been  succesfully executed");
		
			conn.commit(); 
								
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String msg = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), msg, e);
			}
			
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			releaseResources(conn, stmt, null);
			monitor.stop();
			logger.debug("OUT");
		}
	}
	
	private Double getIdServiceInstance(Connection conn) {
		Double idServiceInstance = null;
		
		
		String sqlUpdate, sqlSelect; 
		PreparedStatement stmtUpdate, stmtSelect;
		ResultSet resultSet;
		
		
		logger.debug("IN");
		
		stmtUpdate = null;
		stmtSelect = null;
		resultSet = null;
		
		try {
			Assert.assertNotNull(conn, "Input parameter [conn] cannot be null");
			
			sqlUpdate = "UPDATE ID_GEN SET ID_VAL=ID_VAL+1 WHERE ID_NAME='SERVICE_INSTANCE_ID'";
			sqlSelect = "SELECT ID_VAL FROM ID_GEN WHERE ID_NAME='SERVICE_INSTANCE_ID'";
			
			try {
				stmtUpdate = conn.prepareStatement(sqlUpdate);
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to create a prepared statement for query [" + sqlUpdate + "]", e);
			} 
			logger.debug("Statement [" + stmtUpdate.toString() + "] has been succesfully created");
			
			try {
				stmtUpdate.executeUpdate();
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to execute statement [" + stmtUpdate.toString() + "]", e);
			}
			logger.debug("Statement [" + stmtUpdate.toString() + "] has been  succesfully executed");
			
			
			try {
				stmtSelect = conn.prepareStatement(sqlSelect);
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to create a prepared statement for query [" + sqlUpdate + "]", e);
			} 
			logger.debug("Statement [" + stmtSelect.toString() + "] has been succesfully created");
			
			try {
				stmtSelect.execute();
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to execute statement [" + stmtSelect.toString() + "]", e);
			}
			logger.debug("Statement [" + stmtSelect.toString() + "] has been  succesfully executed");
			
			
			resultSet = stmtSelect.getResultSet();
			Assert.assertTrue(resultSet.getMetaData().getColumnCount() > 0, "The query [" + stmtSelect.toString()+ "] returned a multicolumn resultset");
			if(resultSet.next()) {
				idServiceInstance = new Double( resultSet.getDouble(1) );
			} else {
				Assert.assertUnreachable("The query [" + stmtSelect.toString()+ "] returned no results");
			}
			
			Assert.assertTrue(!resultSet.next(), "The query [" + stmtSelect.toString()+ "] returned more than one record");
			
			conn.commit();
			
		} catch(Throwable t ) {
			throw new ConsoleEngineRuntimeException("Impossible to get resource id", t);
		} finally {
			releaseResources(null, stmtUpdate, null);
			releaseResources(null, stmtSelect, resultSet);
			logger.debug("OUT");
		}	
		
		return idServiceInstance;
	}
	
	private Integer getResourceId(Connection conn, String resourceName) {
		Integer resourceId;
		String sql; 
		PreparedStatement stmt;
		ResultSet resultSet;
		
		
		logger.debug("IN");
		
		stmt = null;
		resourceId = null;
		resultSet = null;
		
		try {
			Assert.assertNotNull(conn, "Input parameter [conn] cannot be null");
			Assert.assertTrue(!StringUtilities.isEmpty(resourceName), "Input parameter [" + resourceName + "] cannot be null or empty");
			
			sql = "SELECT RESOURCE_ID FROM SBI_RESOURCES WHERE RESOURCE_NAME = ?";
			
			try {
				stmt = conn.prepareStatement(sql);
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to create a prepared statement for query [" + sql + "]", e);
			} 
			logger.debug("Statement [" + stmt.toString() + "] has been succesfully created");
			
			try {
				stmt.setString(1, resourceName);
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to set the value of the parameter [resourceName] to [" + resourceName + "] in query [" + sql + "]", e);
			}
			logger.debug("Prameters has been  succesfully replaced in statement  [" + stmt.toString() + "]");
			
			try {
				stmt.execute();
			} catch (SQLException e) {
				throw new ConsoleEngineRuntimeException("Impossible to execute statement [" + stmt.toString() + "]", e);
			}
			logger.debug("Statement [" + stmt.toString() + "] has been  succesfully executed");
			
			
			resultSet = stmt.getResultSet();
			Assert.assertTrue(resultSet.getMetaData().getColumnCount() > 0, "The query [" + stmt.toString()+ "] returned a multicolumn resultset");
			if(resultSet.next()) {
				resourceId = new Integer( resultSet.getInt(1) );
			} else {
				Assert.assertUnreachable("The query [" + stmt.toString()+ "] returned no results");
			}
			
			Assert.assertTrue(!resultSet.next(), "The query [" + stmt.toString()+ "] returned more than one record");
			
		} catch(Throwable t ) {
			throw new ConsoleEngineRuntimeException("Impossible to get resource id", t);
		} finally {
			releaseResources(null, stmt, resultSet);
			logger.debug("OUT");
		}
		
		return resourceId;
	}
	

	// ==============================================================================================
	// Release resources
	// ==============================================================================================
	
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
