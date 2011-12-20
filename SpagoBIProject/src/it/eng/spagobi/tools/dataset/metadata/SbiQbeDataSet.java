/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
