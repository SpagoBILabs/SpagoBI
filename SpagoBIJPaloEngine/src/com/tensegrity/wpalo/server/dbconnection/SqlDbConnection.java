/*
*
* @file SqlDbConnection.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: SqlDbConnection.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
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
