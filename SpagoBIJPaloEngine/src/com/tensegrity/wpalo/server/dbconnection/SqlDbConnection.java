/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.server.dbconnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import org.palo.viewapi.DbConnection;


/**
 * <code>SqlDbConnection</code>
 * <p>Abstract base class for sql based database connections.</p>
 *
 * @version $Id: SqlDbConnection.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public abstract class SqlDbConnection implements DbConnection {

	protected Connection connection;
	protected final Properties commands;
	protected final Properties credentials;
	

	protected SqlDbConnection() {
		commands = loadProperties("commands");
		credentials = loadProperties("credentials");
	}
	
	
	public void connect() throws SQLException {
		if (!isConnected()) {
			try {
				Class.forName(credentials.getProperty("jdbcDriver"));
				connection = DriverManager.getConnection(
						credentials.getProperty("jdbcURL"), 
						credentials.getProperty("userName"),
						credentials.getProperty("userPassword"));
				// initialise connection:
				initialize();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final Connection getConnection() {
		return connection;
	}

	public final Properties getSqlCommands() {
		return commands;
	}

	public final boolean isConnected() {
		return connection != null;
	}

	/**
	 * Specifies the home directory of where to sql commands and the database
	 * credentials properties files are stored.
	 * @return the home directory without an ending '/'
	 */
	protected abstract String getSqlHomeDir();
	/**
	 * Initialises the newly created connection.
	 */
	protected abstract void initialize();

	protected Properties loadProperties(String fromFile) {
		String file = getSqlHomeDir() + "/" + fromFile;
		try {
			Properties props = new Properties();
			InputStream propsIn = getClass().getResourceAsStream(file);
			BufferedInputStream bis = new BufferedInputStream(propsIn);
			if (bis != null) {
				props.load(bis);
				bis.close();
			}
			return props;
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read properties from '" + file
					+ "' !!");
		}
	}

	protected final String buildQuery(Properties props, String key,
			String... params) {
		String format = props.getProperty(key);
		if (format != null) {
			String result = MessageFormat.format(format, (Object[]) params);
			return result;
		}
		return null;
	}

}
