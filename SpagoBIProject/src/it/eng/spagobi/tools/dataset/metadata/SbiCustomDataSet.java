package it.eng.spagobi.tools.dataset.metadata;

public class SbiCustomDataSet extends SbiDataSetHistory {
	
	 private String customData =null;
	 private String javaClassName =null;

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		this.customData = customData;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

}
