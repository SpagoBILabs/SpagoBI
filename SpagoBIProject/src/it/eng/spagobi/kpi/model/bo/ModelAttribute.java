package it.eng.spagobi.kpi.model.bo;

import java.io.Serializable;

public class ModelAttribute implements Serializable{
	
	private Integer id = null;// SBI_MODEL_ATTR_ID
	private String name = null;// SBI_KPI_MODEL_ATTR Name
	private String code = null;// SBI_KPI_MODEL_ATTR code
	private String descr = null;// SBI_KPI_MODEL_ATTR descr
	private String value = null; // SBI_KPI_MODEL_ATTR_VAL value
	private Integer typeId = null; // DOMAIN ID
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String type) {
		this.value = type;
	}

	public ModelAttribute() {
		super();
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	
	
}
