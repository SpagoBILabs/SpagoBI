/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.tools.dataset.common.metadata;


import java.util.HashMap;
import java.util.Map;

public class FieldMetadata implements IFieldMetaData {

	String name;
	String alias;
	Class type;
	Map properties;
	FieldType fieldType;
	
	public FieldMetadata() {
		super();
		this.properties= new HashMap();
		fieldType = FieldType.ATTRIBUTE;
	}

	public FieldMetadata(String name, Class type) {
		super();
		setName(name);
		setType(type);
		this.properties = new HashMap();
		fieldType = FieldType.ATTRIBUTE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public Object getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public void setProperty(String propertyName, Object propertyValue) {
		properties.put(propertyName, propertyValue);
	}

	public Map getProperties() {
		return properties;
	}
	
	public void setProperties(Map properties) {
		this.properties = properties;
	}
	
	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}
	
	@Override
	public String toString() {
		return "FieldMetadata [name=" + name + ", alias=" + alias + ", type="
				+ type + ", properties=" + properties + ", fieldType="
				+ fieldType + "]";
	}
	
}
