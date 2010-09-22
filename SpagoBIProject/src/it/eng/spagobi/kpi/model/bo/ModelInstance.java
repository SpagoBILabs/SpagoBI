package it.eng.spagobi.kpi.model.bo;

import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.tools.udp.bo.UdpValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelInstance implements Serializable{
	
	private Integer id = null;
	private Model model = null;//Model to which this instance refers
	private ModelNode root =null; //root of the tree made of ModelInstance, representing the model
	private List childrenNodes = null;//List of ModelInstanceNodes children
	private String name = null;//name of the complete model instance(like "my own CMMI")
	private String description = null;//description of the complete model instance
	private Integer parentId = null;
	private KpiInstance kpiInstance = null;
	private String label;
	private Date startDate;
	private Date endDate;
	private String modelUUID;
	private String guiId = null;
	List udpValues = new ArrayList<UdpValue>();

	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public KpiInstance getKpiInstance() {
		return kpiInstance;
	}
	public void setKpiInstance(KpiInstance kpiInstance) {
		this.kpiInstance = kpiInstance;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public ModelNode getRoot() {
		return root;
	}
	public void setRoot(ModelNode root) {
		this.root = root;
	}
	public List getChildrenNodes() {
		return childrenNodes;
	}
	public void setChildrenNodes(List childrenNodes) {
		this.childrenNodes = childrenNodes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public void setModelUUID(String modelUUID) {
		this.modelUUID = modelUUID;
	}
	public String getModelUUID() {
		return modelUUID;
	}
	public String getGuiId() {
		return guiId;
	}
	public void setGuiId(String guiId) {
		this.guiId = guiId;
	}
	public List getUdpValues() {
		return udpValues;
	}
	public void setUdpValues(List udpValues) {
		this.udpValues = udpValues;
	}

	
	
}
