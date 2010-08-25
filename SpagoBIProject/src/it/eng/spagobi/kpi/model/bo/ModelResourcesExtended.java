package it.eng.spagobi.kpi.model.bo;

public class ModelResourcesExtended {	
	
	Integer modelResourcesId;
	Integer modelInstId;
	Integer resourceId;
	String resourceName = null;
	String resourceCode = null;
	String resourceType = null; 

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
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceCode() {
		return resourceCode;
	}
	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public ModelResourcesExtended(Resource resource, ModelResources modelResources) {
		this.modelInstId = modelResources.getModelInstId();
		this.modelResourcesId = resource.getId();
		this.resourceCode = resource.getCode();
		this.resourceId = resource.getId();
		this.resourceName = resource.getName();
		this.resourceType = resource.getType();
	}
	

}
