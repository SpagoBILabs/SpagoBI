/**
 *
 *	LICENSE: see COPYING file
 *
**/

/*
 *    DatabaseConnection.java
 *    Copyright (C) 2004 Len Trigg, Stefan Mutter
 *
 */

package weka.core.converters;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import weka.core.Utils;

/**
 * Connects to a database.
 *  WARNING:This class was taken from the weka_src.jar and modified in order to put some logs and to make possible the connection to the DB through SpagoBI
 * 
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Stefan Mutter (mutter@cs.waikato.ac.nz)
 * @version $Revision: 1.2 $
 */
public class DatabaseConnection implements Serializable {

	private boolean connectionMenagedByExternalPool;

	/** The name of the properties file */
	protected static String PROPERTY_FILE = "database.properties";

	/** Holds the jdbc drivers to be used (only to stop them being gc'ed) */
	protected static Vector DRIVERS = new Vector();

	/** Properties associated with the database connection */
	protected static Properties PROPERTIES;

	/** Database URL */
	protected String m_DatabaseURL;

	/** The prepared statement used for database queries. */
	protected PreparedStatement m_PreparedStatement;

	/** The database connection */
	protected Connection connection;

	/** True if debugging output should be printed */
	protected boolean m_Debug = false;

	/* For databases where Tables and Columns are created in upper case */
	protected boolean m_checkForUpperCaseNames = false;

	/* setAutoCommit on the database? */
	protected boolean m_setAutoCommit = true;

	/* create index on the database? */
	protected boolean m_createIndex = false;

	/** Database username */
	protected String m_userName = "";

	/** Database Password */
	protected String m_password = "";

	/* Type mapping used for reading experiment results */
	public static final int STRING = 0;

	public static final int BOOL = 1;

	public static final int DOUBLE = 2;

	public static final int BYTE = 3;

	public static final int SHORT = 4;

	public static final int INTEGER = 5;

	public static final int LONG = 6;

	public static final int FLOAT = 7;

	public static final int DATE = 8;

	private static transient Logger logger = Logger.getLogger(DatabaseConnection.class);
	
	/*
	 * Load the database drivers -- the properties files only get consulted when
	 * the class is initially loaded, not for every object instantiated
	 */
	static {

		try {
			PROPERTIES = Utils.readProperties(PROPERTY_FILE);

			// Register the drivers in jdbc DriverManager
			String drivers = PROPERTIES.getProperty("jdbcDriver",
					"jdbc.idbDriver");

			if (drivers == null) {
				throw new Exception("No jdbc drivers specified");
			}
			// The call to newInstance() is necessary on some platforms
			// (with some java VM implementations)
			StringTokenizer st = new StringTokenizer(drivers, ", ");
			while (st.hasMoreTokens()) {
				String driver = st.nextToken();
				try {
					DRIVERS.addElement(Class.forName(driver).newInstance());
				} catch (Exception ex) {
					logger.error("Error while adding driver", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("static block DatabaseConnection", ex);
		}
	}

	/**
	 * Sets up the database drivers.
	 * 
	 * @throws Exception the exception
	 * 
	 * @exception Exception
	 * if an error occurs
	 */
	public DatabaseConnection() throws Exception {

		connectionMenagedByExternalPool = false;
		
		m_DatabaseURL = PROPERTIES.getProperty("jdbcURL",
				"jdbc:idb=experiments.prp");
		String uctn = PROPERTIES.getProperty("checkUpperCaseNames");
		logger.debug("propertiy [checkUpperCaseNames] is equal to [" + uctn +"]");
		if (uctn.equals("true")) {
			logger.debug("set propertiy [checkUpperCaseNames] to TRUE");
			m_checkForUpperCaseNames = true;
		} else {
			logger.debug("set propertiy [checkUpperCaseNames] to FALSE");
			m_checkForUpperCaseNames = false;
		}
		uctn = PROPERTIES.getProperty("setAutoCommit");
		if (uctn.equals("true")) {
			m_setAutoCommit = true;
		} else {
			m_setAutoCommit = false;
		}
		uctn = PROPERTIES.getProperty("createIndex");
		if (uctn.equals("true")) {
			m_createIndex = true;
		} else {
			m_createIndex = false;
		}
	}

	/**
	 * Set the database username.
	 * 
	 * @param username Username for Database.
	 */
	public void setUsername(String username) {
		m_userName = username;
	}

	/**
	 * Get the database username.
	 * 
	 * @return Database username
	 */
	public String getUsername() {
		return (m_userName);
	}

	/**
	 * Set the database password.
	 * 
	 * @param password Password for Database.
	 */
	public void setPassword(String password) {
		m_password = password;
	}

	/**
	 * Get the database password.
	 * 
	 * @return Password for Database.
	 */
	public String getPassword() {
		return (m_password);
	}

	/**
	 * Returns the tip text for this property.
	 * 
	 * @return tip text for this property suitable for displaying in the
	 * explorer/experimenter gui
	 */
	public String databaseURLTipText() {
		return "Set the URL to the database.";
	}

	/**
	 * Get the value of DatabaseURL.
	 * 
	 * @return Value of DatabaseURL.
	 */
	public String getDatabaseURL() {

		return m_DatabaseURL;
	}

	/**
	 * Set the value of DatabaseURL.
	 * 
	 * @param newDatabaseURL Value to assign to DatabaseURL.
	 */
	public void setDatabaseURL(String newDatabaseURL) {

		m_DatabaseURL = newDatabaseURL;
	}

	/**
	 * Check if the property checkUpperCaseNames in the DatabaseUtils file is
	 * set to true or false.
	 * 
	 * @return true if the property checkUpperCaseNames in the DatabaseUtils
	 * file is set to true, false otherwise.
	 */
	public boolean getUpperCase() {
		return m_checkForUpperCaseNames;
	}

	/**
	 * translates the column data type string to an integer value that indicates
	 * which data type / get()-Method to use in order to retrieve values from
	 * the database (see DatabaseUtils.Properties, InstanceQuery())
	 * 
	 * @param type
	 *            the column type as retrieved with
	 *            java.sql.MetaData.getColumnTypeName(int)
	 * @return an integer value that indicates which data type / get()-Method to
	 *         use in order to retrieve values from the
	 */
	int translateDBColumnType(String type) {
		return Integer.parseInt(PROPERTIES.getProperty(type));
	}

	/**
	 * Converts an array of objects to a string by inserting a space between
	 * each element. Null elements are printed as ?
	 * 
	 * @param array the array of objects
	 * 
	 * @return a value of type 'String'
	 */
	public static String arrayToString(Object[] array) {

		String result = "";
		if (array == null) {
			result = "<null>";
		} else {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					result += " ?";
				} else {
					result += " " + array[i];
				}
			}
		}
		return result;
	}

	/**
	 * Returns the name associated with a SQL type.
	 * 
	 * @param type the SQL type
	 * 
	 * @return the name of the type
	 */
	public static String typeName(int type) {

		switch (type) {
		case Types.BIGINT:
			return "BIGINT ";
		case Types.BINARY:
			return "BINARY";
		case Types.BIT:
			return "BIT";
		case Types.CHAR:
			return "CHAR";
		case Types.DATE:
			return "DATE";
		case Types.DECIMAL:
			return "DECIMAL";
		case Types.DOUBLE:
			return "DOUBLE";
		case Types.FLOAT:
			return "FLOAT";
		case Types.INTEGER:
			return "INTEGER";
		case Types.LONGVARBINARY:
			return "LONGVARBINARY";
		case Types.LONGVARCHAR:
			return "LONGVARCHAR";
		case Types.NULL:
			return "NULL";
		case Types.NUMERIC:
			return "NUMERIC";
		case Types.OTHER:
			return "OTHER";
		case Types.REAL:
			return "REAL";
		case Types.SMALLINT:
			return "SMALLINT";
		case Types.TIME:
			return "TIME";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.TINYINT:
			return "TINYINT";
		case Types.VARBINARY:
			return "VARBINARY";
		case Types.VARCHAR:
			return "VARCHAR";
		default:
			return "Unknown";
		}
	}

	/**
	 * Opens a connection to the database.
	 * 
	 * @throws Exception the exception
	 * 
	 * @exception Exception
	 * if an error occurs
	 */
	public void connectToDatabase() throws Exception {
		logger.debug("IN");
		if (connectionMenagedByExternalPool)
			return;

		if (m_Debug) {
			logger.error("Connecting to " + m_DatabaseURL);
		}
		if (connection == null) {
			if (m_userName.equals("")) {
				connection = DriverManager.getConnection(m_DatabaseURL);
			} else {
				connection = DriverManager.getConnection(m_DatabaseURL,
						m_userName, m_password);
			}
		}
		if (m_setAutoCommit) {
			connection.setAutoCommit(true);
		} else {
			connection.setAutoCommit(false);
		}
		logger.debug("OUT");
	}

	/**
	 * Closes the connection to the database.
	 * 
	 * @throws Exception the exception
	 * 
	 * @exception Exception
	 * if an error occurs
	 */
	public void disconnectFromDatabase() throws Exception {
		logger.debug("IN");
		if (connectionMenagedByExternalPool)
			return;
		if (m_Debug) {
			logger.error("Disconnecting from " + m_DatabaseURL);
		}
		if (connection != null) {
			connection.close();
			connection = null;
		}
		logger.debug("OUT");
	}

	/**
	 * Gets meta data for the database connection object.
	 * 
	 * @return the meta data.
	 * 
	 * @throws Exception the exception
	 * 
	 * @exception SQLException
	 * if an error occurs
	 */
	public DatabaseMetaData getMetaData() throws Exception {

		return connection.getMetaData();
	}

	/**
	 * Returns true if a database connection is active.
	 * 
	 * @return a value of type 'boolean'
	 */
	public boolean isConnected() {

		return (connection != null);
	}

	/**
	 * Executes a SQL query.
	 * 
	 * @param query the SQL query
	 * 
	 * @return true if the query generated results
	 * 
	 * @throws SQLException the SQL exception
	 * 
	 * @exception SQLException
	 * if an error occurs
	 */
	public boolean execute(String query) throws SQLException {
		m_PreparedStatement = connection.prepareStatement(query,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		return (m_PreparedStatement.execute());
		
		
	}
	
	private Statement stmt = null;
	
	/**
	 * Fast execute.
	 * 
	 * @param query the query
	 * 
	 * @return true, if successful
	 * 
	 * @throws SQLException the SQL exception
	 */
	public boolean fastExecute(String query) throws SQLException {
		//Statement stmt = connection.createStatement();	
		if(stmt == null) stmt = connection.createStatement();
		return (stmt.execute(query));
	}
	
	/**
	 * Gets the results generated by a previous query.
	 * 
	 * @return the result set.
	 * 
	 * @throws SQLException the SQL exception
	 * 
	 * @exception SQLException
	 * if an error occurs
	 */
	public ResultSet getResultSet() throws SQLException {

		return m_PreparedStatement.getResultSet();
	}

	/**
	 * Dewtermines if the current query retrieves a result set or updates a
	 * table.
	 * 
	 * @return the update count (-1 if the query retrieves a result set).
	 * 
	 * @throws SQLException the SQL exception
	 * 
	 * @exception SQLException
	 * if an error occurs
	 */
	public int getUpdateCount() throws SQLException {

		return m_PreparedStatement.getUpdateCount();
	}

	/**
	 * Checks that a given table exists.
	 * 
	 * @param tableName the name of the table to look for.
	 * 
	 * @return true if the table exists.
	 * 
	 * @throws Exception the exception
	 * 
	 * @exception Exception
	 * if an error occurs.
	 */
	public boolean tableExists(String tableName) throws Exception {

		if (m_Debug) {
			logger.error("Checking if table " + tableName + " exists...");
		}
		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rs;
		if (m_checkForUpperCaseNames == true) {
			rs = dbmd.getTables(null, null, tableName.toUpperCase(), null);
		} else {
			rs = dbmd.getTables(null, null, tableName, null);
		}
		boolean tableExists = rs.next();
		if (rs.next()) {
			throw new Exception("This table seems to exist more than once!");
		}
		rs.close();
		if (m_Debug) {
			if (tableExists) {
				logger.error("... " + tableName + " exists");
			} else {
				logger.error("... " + tableName + " does not exist");
			}
		}
		return tableExists;
	}
	
	/**
	 * Checks that a given table is empty.
	 * 
	 * @param tableName the name of the table to look for.
	 * 
	 * @return true if the table is empty.
	 * 
	 * @throws Exception the exception
	 * 
	 * @exception Exception
	 * if an error occurs.
	 */
	public boolean isTableEmpty(String tableName) throws Exception {

		 // Select the number of rows in the table
        Statement stmt = connection.createStatement();
        String query = "SELECT COUNT(*) FROM";
        
        if (m_checkForUpperCaseNames == true) {
        	query += " " + tableName.toUpperCase();        	
		} else {
			query += " " + tableName;
		}
        ResultSet resultSet = stmt.executeQuery(query);
        
        // Get the number of rows from the result set
        resultSet.next();
        int rowcount = resultSet.getInt(1);

		return (rowcount == 0);
	}

	
	
	/**
	 * Checks if is connection menaged by external pool.
	 * 
	 * @return true, if is connection menaged by external pool
	 */
	public boolean isConnectionMenagedByExternalPool() {
		return connectionMenagedByExternalPool;
	}

	/**
	 * Sets the connection menaged by external pool.
	 * 
	 * @param connectionMenagedByExternalPool the new connection menaged by external pool
	 */
	public void setConnectionMenagedByExternalPool(
			boolean connectionMenagedByExternalPool) {
		this.connectionMenagedByExternalPool = connectionMenagedByExternalPool;
	}

	/**
	 * Sets the connection.
	 * 
	 * @param connection the new connection
	 */
	public void setConnection(Connection connection) {
		logger.debug("IN");
		this.connection = connection;
		setConnectionMenagedByExternalPool(true);
		logger.debug("OUT");
	}
}
