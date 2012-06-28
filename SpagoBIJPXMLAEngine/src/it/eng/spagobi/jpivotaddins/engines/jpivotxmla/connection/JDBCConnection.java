/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
