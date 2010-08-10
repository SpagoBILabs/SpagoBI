package it.eng.spagobi.tools.dataset.common.datastore;

import java.util.HashMap;
import java.util.Map;

public class FieldMetadata implements IFieldMetaData {


	String name;
	String alias;
	Class type;
	Map properties;

	public FieldMetadata() {
		super();
		this.properties= new HashMap();
	}

	public FieldMetadata(String name, Class type) {
		super();
		setName(name);
		setType(type);
		this.properties= new HashMap();
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
	
	public String toString() {
		return name + " (" + type.getName()+ ")";
	}

	public Map getProperties() {
		return properties;
	}
	
}
