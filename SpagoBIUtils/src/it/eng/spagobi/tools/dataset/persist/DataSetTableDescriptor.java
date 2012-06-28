/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.persist;

import java.util.HashMap;
import java.util.Map;

public class DataSetTableDescriptor implements IDataSetTableDescriptor {

	private String tableName = null;
	private Map<String, String> field2ColumnMap = null;
	private Map<String, Class> field2ClassMap = null;
	private Map<String, String> column2fieldMap = null;
	
	public DataSetTableDescriptor() {
		this.field2ColumnMap = new HashMap<String, String>();
		this.field2ClassMap = new HashMap<String, Class>();
		this.column2fieldMap = new HashMap<String, String>();
	}
	
	public void addField(String fieldName, String columnName, Class type) {
		this.field2ColumnMap.put(fieldName, columnName);
		this.field2ClassMap.put(fieldName, type);
		this.column2fieldMap.put(columnName, fieldName);
	}

	public String getColumnName(String fieldName) {
		return this.field2ColumnMap.get(fieldName);
	}
	
	public String getFieldName(String columnName) {
		return this.column2fieldMap.get(columnName);
	}
	
	public Class getColumnType(String fieldName) {
		return this.field2ClassMap.get(fieldName);
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	@Override
	public String toString() {
		return "DataSetTableDescriptor [tableName=" + tableName
				+ ", field2ColumnMap=" + field2ColumnMap + ", field2ClassMap="
				+ field2ClassMap + "]";
	}

}
