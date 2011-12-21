/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
