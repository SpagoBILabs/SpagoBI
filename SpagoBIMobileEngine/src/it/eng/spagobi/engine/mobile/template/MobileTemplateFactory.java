package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import org.apache.log4j.Logger;


public class MobileTemplateFactory{
	

	private static transient Logger logger = Logger.getLogger(MobileTemplateFactory.class);

	public static IMobileTemplateInstance createMobileTemplateInstance(SourceBean template) throws Exception {
		IMobileTemplateInstance instance = null;
		
		logger.debug("IN");
		String name = template.getName();
		if(MobileConstants.TABLE_TAG.equals(name)){
			instance =  new TableTemplateInstance(template);			
		}

		else if(MobileConstants.CHART_TAG.equals(name)){
			instance = new ChartTemplateInstance(template);			
		}

		else if(MobileConstants.COMPOSED_TAG.equals(name)){
			instance = new ComposedTemplateInstance(template);			
		}
		if(instance != null){
			instance.loadTemplateFeatures();
		}
		logger.debug("OUT");	
		
		return instance;
		
	}
}
