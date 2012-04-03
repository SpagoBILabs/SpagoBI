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

	private String getId() {
		String id = null;
		String aggregationFunction = (String)properties.get("aggregationFunction");
		if(aggregationFunction == null || "NONE".equalsIgnoreCase(aggregationFunction)) {
			id = getName();
		} else {
			id = aggregationFunction + "(" +  getName() + ")";
		}
		
		return id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldMetadata other = (FieldMetadata) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		
		return true;
	}
	
	
	
}
