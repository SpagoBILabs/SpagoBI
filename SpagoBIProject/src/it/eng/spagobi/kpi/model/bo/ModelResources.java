package it.eng.spagobi.kpi.model.bo;

import java.io.Serializable;

public class ModelResources implements Serializable{

	Integer modelResourcesId;
	Integer modelInstId;
	Integer resourceId;
	
	
	public Integer getModelResourcesId() {
		return modelResourcesId;
	}
	public void setModelResourcesId(Integer modelResourcesId) {
		this.modelResourcesId = modelResourcesId;
	}
	public Integer getModelInstId() {
		return modelInstId;
	}
	public void setModelInstId(Integer modelInstId) {
		this.modelInstId = modelInstId;
	}
	public Integer getResourceId() {
		return resourceId;
	}
	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}
	
}
