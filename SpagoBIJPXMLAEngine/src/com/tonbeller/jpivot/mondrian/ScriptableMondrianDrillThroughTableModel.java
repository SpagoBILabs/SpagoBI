/**
 * 
 * LICENSE: see LICENSE.html file
 * 
 */
package com.tonbeller.jpivot.mondrian;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import mondrian.rolap.RolapConnectionProperties;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.tonbeller.jpivot.mondrian.script.ScriptColumn;
import com.tonbeller.wcf.table.AbstractTableModel;
import com.tonbeller.wcf.table.DefaultCell;
import com.tonbeller.wcf.table.DefaultTableRow;
import com.tonbeller.wcf.table.TableRow;

/**
 * @author Engineering Ingegneria Informatica S.p.A. - Luca Barozzi
 * 
 * A wcf table model for drill through data,
 * requires an sql query and connection information to be set.
 */

public class ScriptableMondrianDrillThroughTableModel extends AbstractTableModel {
	private static Logger logger = Logger.getLogger(MondrianDrillThroughTableModel.class);
	private String title = "Drill Through Table";
	private String caption = "";
	private String sql = "";
	private String jdbcUser;
	private String jdbcUrl;
	private String jdbcPassword;
	private String jdbcDriver;
	private String dataSourceName;
	private String catalogExtension;
	private int maxResults;
	private String scriptRootUrl;
	private List scripts = new ArrayList(); 
	private GroovyScriptEngine scriptEngine = null;
	
	private DataSource dataSource;
	private static Context jndiContext;

	private boolean ready = false;
	
	private TableRow[] rows = new TableRow[0];
	private String [] columnTitles = new String[0];

	public ScriptableMondrianDrillThroughTableModel() {
	}

	public int getRowCount() {
		if ( !ready ) {
			executeQuery();
		}
		return rows.length;
	}

	public TableRow getRow(int rowIndex) {
		if ( !ready ) {
			executeQuery();
		}
		return rows[rowIndex];
	}

	public String getTitle() {
		return title;
	}
	/**
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
		this.ready = false;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * wcf table component calls this method from it's constructor
	 * to get the number of columns
	 * 
	 */
	public int getColumnCount() {
		if ( !ready ) {
			executeQuery();
		}
		return columnTitles.length;
	}

	public String getColumnTitle(int columnIndex) {
		if ( !ready ) {
			executeQuery();
		}
		return columnTitles[columnIndex];
	}

	/**
	 * execute sql query
	 * @throws Exception
	 */	
	private void executeQuery() {
		Connection con=null;
		try {
			InputStream catExtIs = ScriptableMondrianDrillThroughTableModel.class.getClassLoader().getResourceAsStream("/" + catalogExtension);
			Digester catExtDigester = new Digester();
			catExtDigester.push(this);
			catExtDigester.addSetProperties("extension");
			catExtDigester.addObjectCreate("extension/script", "com.tonbeller.jpivot.mondrian.script.ScriptColumn");
			catExtDigester.addSetProperties("extension/script");
			catExtDigester.addSetNext("extension/script", "addScript");
			catExtDigester.parse(catExtIs);

			URL scriptsBaseURL = Thread.currentThread().getContextClassLoader().getResource(scriptRootUrl);
			scriptEngine = new GroovyScriptEngine(new URL[] {scriptsBaseURL});
			
			con = getConnection();
			Statement s = con.createStatement();
			s.setMaxRows(maxResults);
			ResultSet rs = s.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int numCols = md.getColumnCount();
			List columnTitlesList = new ArrayList();
			// set column headings
			for ( int i = 0; i < numCols; i++ ) {
				//	columns are 1 based
				columnTitlesList.add(i, md.getColumnName(i+1));
			}
			// loop on script columns
			for (ListIterator sIt = scripts.listIterator(); sIt.hasNext();) {
				final ScriptColumn sc = (ScriptColumn)sIt.next();
				columnTitlesList.add(sc.getPosition() - 1, sc.getTitle());
			}
			columnTitles = (String[])columnTitlesList.toArray(new String[0]);
			// loop through rows
			List tempRows = new ArrayList();
			Map scriptInput = new HashMap();
			Binding binding = new Binding();
			while (rs.next()) {
				List rowList = new ArrayList();
				scriptInput.clear();
				// loop on columns, 1 based
				for ( int i = 0; i < numCols; i++ ) {
					rowList.add(i, rs.getObject(i+1));
					scriptInput.put(columnTitles[i], rs.getObject(i+1));
				}
				binding.setVariable("input", scriptInput);
				// loop on script columns
				for (ListIterator sIt = scripts.listIterator(); sIt.hasNext();) {
					final ScriptColumn sc = (ScriptColumn)sIt.next();
					scriptEngine.run(sc.getFile(), binding);
					final Object output = binding.getVariable("output");
					if (output instanceof Map) {
						Map outMap = (Map)output;
						rowList.add(sc.getPosition() - 1, new DefaultCell((String)outMap.get("URL"), (String)outMap.get("Value")));
					} else if (output instanceof String) {
						rowList.add(sc.getPosition() - 1, (String)output);
					} else {
						throw new Exception("Unknown groovy script return type (not a Map nor String).");
					}
				}
				tempRows.add(new DefaultTableRow(rowList.toArray()));
			}
			rs.close();
			rows = (TableRow[]) tempRows.toArray(new TableRow[0]);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("?", e);
			// problem occured, set table model to zero size
			rows = new TableRow[1];
			columnTitles = new String[1];
			columnTitles[0] = "An error occured";
			Object[] row = new Object[1];
			row[0] = e.toString();
			rows[0] = new DefaultTableRow(row);
			ready=false;
			return;
		} finally {
			try {
				con.close();
			} catch (Exception e1) {
				// ignore
			}
		}
		ready = true;
	}
	
	/**
	 * get sql connection
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		if (dataSourceName == null) {

			if (jdbcUrl == null) {
				throw new RuntimeException(
						"Mondrian Connect string '" +
						"' must contain either '" + RolapConnectionProperties.Jdbc +
						"' or '" + RolapConnectionProperties.DataSource + "'");
			}
			return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
		} else {
			return getDataSource().getConnection();
		}
	}

	private DataSource getDataSource() {
		if (dataSource == null) {
			// Get connection from datasource.
			try {
				dataSource = (DataSource) getJndiContext().lookup(dataSourceName);
			} catch (NamingException e) {
				throw new RuntimeException("Error while looking up data source (" +
						dataSourceName + ")", e);
			}
		}
		return dataSource;
	}
	
	private Context getJndiContext() throws NamingException {
		if (jndiContext == null) {
			jndiContext = new InitialContext();
		}
		return jndiContext;
	}
	/**
	 * @return
	 */
	public String getJdbcDriver() {
		return jdbcDriver;
	}

	/**
	 * @param jdbcDriver
	 */
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	/**
	 * @return
	 */
	public String getJdbcPassword() {
		return jdbcPassword;
	}

	/**
	 * @param jdbcPassword
	 */
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	/**
	 * @return
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * @param jdbcUrl
	 */
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	/**
	 * @return
	 */
	public String getJdbcUser() {
		return jdbcUser;
	}

	/**
	 * @param jdbcUser
	 */
	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}

	/**
	 * @return
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @param caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

    /**
     * @return
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @param string
     */
    public void setDataSourceName(String string) {
        dataSourceName = string;
    }

    /**
     * @return
     */
    public String getCatalogExtension() {
        return catalogExtension;
    }

    /**
     * @param string
     */
    public void setCatalogExtension(String string) {
    	catalogExtension = string;
    }

    /**
     * @return
     */
	public int getMaxResults() {
		return maxResults;
	}

    /**
     * @param string
     */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

    /**
     * @return
     */
	public List getScripts() {
		return scripts;
	}

    /**
     * @param List
     */
	public void setScripts(List scripts) {
		this.scripts = scripts;
	}

	public void addScript(ScriptColumn column) {
		this.scripts.add(column);
	}

    /**
     * @return
     */
	public String getScriptRootUrl() {
		return scriptRootUrl;
	}

    /**
     * @param String
     */
	public void setScriptRootUrl(String scriptRootUrl) {
		this.scriptRootUrl = scriptRootUrl;
	}

}
