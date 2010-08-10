package it.eng.spagobi.engines.chart.bo.charttypes.utils;

public class DrillParameter {

	String name;
	String type;
	String value;
	
	
	public DrillParameter(String name, String type, String value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
