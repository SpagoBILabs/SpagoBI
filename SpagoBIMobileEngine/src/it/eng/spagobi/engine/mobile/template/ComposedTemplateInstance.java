package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class ComposedTemplateInstance implements IMobileTemplateInstance{
	private static transient Logger logger = Logger.getLogger(ComposedTemplateInstance.class);


	public ComposedTemplateInstance(SourceBean template) {
	}


	@Override
	public void loadTemplateFeatures() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getDocumentType() {
		// TODO Auto-generated method stub
		return MobileConstants.COMPOSED_TYPE;
	}


	@Override
	public JSONObject getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

}
