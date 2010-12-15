package it.eng.spagobi.kpi.goal.metadata.bo;

import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;

public class GoalNode {
	private String name;
	private String label;
	private String goalDescr;
	private Integer goalId;
	private Integer ouId;
	private Integer id;
	private Integer fatherCountId;

	
	
	public GoalNode(String name, String label, String goalDescr, Integer goal,
			Integer ou) {
		super();
		this.name = name;
		this.label = label;
		this.goalDescr = goalDescr;
		this.goalId = goal;
		this.ouId = ou;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getGoalDescr() {
		return goalDescr;
	}
	public void setGoalDescr(String goalDescr) {
		this.goalDescr = goalDescr;
	}
	public Integer getGoalId() {
		return goalId;
	}
	public void setGoalId(Integer goalId) {
		this.goalId = goalId;
	}
	public Integer getOuId() {
		return ouId;
	}
	public void setOuId(Integer ouId) {
		this.ouId = ouId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}



	public Integer getFatherCountId() {
		return fatherCountId;
	}



	public void setFatherCountId(Integer fatherCountId) {
		this.fatherCountId = fatherCountId;
	}
	
	
}
