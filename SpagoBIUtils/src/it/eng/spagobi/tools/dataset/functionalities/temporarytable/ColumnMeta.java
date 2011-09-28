package it.eng.spagobi.tools.dataset.functionalities.temporarytable;


import java.util.HashMap;
import java.util.Map;

public class ColumnMeta {

	String name;
	Class type;
	String alias;

	Integer size;
	Map properties = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public ColumnMeta(String name, Class type, Map properties) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
		if(properties != null) this.properties = properties;
		else properties = new HashMap<String, String>();
	}
	public Map getProperties() {
		return properties;
	}
	public void setProperties(Map properties) {
		this.properties = properties;
	}
	
	
	
	
	
}
