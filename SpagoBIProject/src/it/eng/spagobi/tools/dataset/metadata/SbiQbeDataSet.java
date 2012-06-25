/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.metadata;

import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

public class SbiQbeDataSet extends SbiDataSetHistory {

	private String sqlQuery = null;
	
	private String jsonQuery = null;
	 
	private SbiDataSource dataSource = null;
		
	private String datamarts = null;

	public String getJsonQuery() {
		return jsonQuery;
	}

	public void setJsonQuery(String jsonQuery) {
		this.jsonQuery = jsonQuery;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public SbiDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(SbiDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDatamarts() {
		return datamarts;
	}

	public void setDatamarts(String datamarts) {
		this.datamarts = datamarts;
	}

}
