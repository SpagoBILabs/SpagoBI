/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.persist;

import java.util.HashMap;
import java.util.Map;

public class DataSetTableDescriptor implements IDataSetTableDescriptor {

	private String tableName = null;
	private Map<String, String> field2ColumnMap = null;
	private Map<String, Class> field2ClassMap = null;
	
	public DataSetTableDescriptor() {
		this.field2ColumnMap = new HashMap<String, String>();
		this.field2ClassMap = new HashMap<String, Class>();
	}
	
	public void addField(String fieldName, String columnName, Class type) {
		this.field2ColumnMap.put(fieldName, columnName);
		this.field2ClassMap.put(fieldName, type);
	}

	public String getColumnName(String fieldName) {
		return this.field2ColumnMap.get(fieldName);
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
