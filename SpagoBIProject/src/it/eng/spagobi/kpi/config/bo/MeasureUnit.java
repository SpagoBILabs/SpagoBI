package it.eng.spagobi.kpi.config.bo;

import java.io.Serializable;

public class MeasureUnit implements Serializable{

	Integer id=null;
	String name=null;
	String scaleCd=null;
	String scaleNm=null;
	Integer scaleTypeId=null;
	
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
	public String getScaleCd() {
		return scaleCd;
	}
	public void setScaleCd(String scaleCd) {
		this.scaleCd = scaleCd;
	}
	public String getScaleNm() {
		return scaleNm;
	}
	public void setScaleNm(String scaleNm) {
		this.scaleNm = scaleNm;
	}
	public Integer getScaleTypeId() {
		return scaleTypeId;
	}
	public void setScaleTypeId(Integer scaleTypeId) {
		this.scaleTypeId = scaleTypeId;
	}

}
