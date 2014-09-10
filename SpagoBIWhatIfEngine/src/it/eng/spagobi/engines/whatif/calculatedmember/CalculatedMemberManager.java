package it.eng.spagobi.engines.whatif.calculatedmember;



import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;

public class CalculatedMemberManager {
	public static transient Logger logger = Logger.getLogger(CalculatedMemberManager.class);
	private PivotModel model;
	
	public CalculatedMemberManager(PivotModel model) {
		super();
		this.model = model;
	}
	
	public PivotModel getModel() {
		return model;
	}


	public void setModel(PivotModel model) {
		this.model = model;
	}
	
	
}
