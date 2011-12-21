/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPIVOT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection;

import it.eng.spago.base.SourceBean;

/**
 * @author Andrea Gioia
 *
 */
public class JDBCConnection implements IConnection {
	private String name;
	private int type;
	private String driver;
	private String jdbcUrl;
	private String user;
	private String password;
	
	public JDBCConnection(SourceBean connSb) {
		name = (String)connSb.getAttribute("name");
		type = JDBC_CONNECTION;
		driver = (String)connSb.getAttribute("driver");
		jdbcUrl = (String)connSb.getAttribute("jdbcUrl");
		user = (String)connSb.getAttribute("user");
		password = (String)connSb.getAttribute("password");
	}

	public String getName() {
		return name;
	}

	public String getDriver() {
		return driver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getPassword() {
		return password;
	}

	public String getUser() {
		return user;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}
}
