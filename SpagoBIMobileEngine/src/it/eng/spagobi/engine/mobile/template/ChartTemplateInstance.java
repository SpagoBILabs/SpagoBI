package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class ChartTemplateInstance implements IMobileTemplateInstance{
	private SourceBean template;
	
	private static transient Logger logger = Logger.getLogger(ChartTemplateInstance.class);


	public ChartTemplateInstance(SourceBean template) {
		this.template = template;
	}


	@Override
	public void loadTemplateFeatures() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getDocumentType() {
		return MobileConstants.CHART_TYPE;
	}


	@Override
	public JSONObject getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

}
