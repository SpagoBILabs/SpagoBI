package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import org.apache.log4j.Logger;


public class MobileTemplateFactory{
	

	private static transient Logger logger = Logger.getLogger(MobileTemplateFactory.class);

	public static IMobileTemplateInstance createMobileTemplateInstance(SourceBean template) {
		IMobileTemplateInstance instance = null;
		
		logger.debug("IN");
		SourceBean tableInst = (SourceBean)template.getAttribute(MobileConstants.TABLE_TAG);
		if(tableInst != null){
			return new TableTemplateInstance(template);			
		}
		SourceBean chartInst = (SourceBean)template.getAttribute(MobileConstants.CHART_TAG);
		if(chartInst != null){
			return new ChartTemplateInstance(template);			
		}
		SourceBean composedInst = (SourceBean)template.getAttribute(MobileConstants.COMPOSED_TAG);
		if(composedInst != null){
			return new ComposedTemplateInstance(template);			
		}

		logger.debug("OUT");	
		
		return instance;
		
	}
}
