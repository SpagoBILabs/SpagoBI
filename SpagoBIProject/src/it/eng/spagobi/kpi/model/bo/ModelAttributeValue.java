package it.eng.spagobi.kpi.model.bo;

import java.io.Serializable;

public class ModelAttributeValue implements Serializable{
	
	private Integer id = null;// SBI_MODEL_ATTR_ID
	private Integer modelId = null;// SBI_KPI_MODEL Name
	private Integer attrId = null;// SBI_KPI_MODEL_ATTR code
	private String value = null; // VAL value

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getModelId() {
		return modelId;
	}
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
	public Integer getAttrId() {
		return attrId;
	}
	public void setAttrId(Integer attrId) {
		this.attrId = attrId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	

}
