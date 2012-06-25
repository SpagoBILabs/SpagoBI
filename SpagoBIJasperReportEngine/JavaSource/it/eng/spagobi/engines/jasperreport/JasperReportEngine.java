/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport;

import it.eng.spagobi.services.proxy.DataSetServiceProxy;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JasperReportEngine {
	
	private static JasperReportEngineConfig engineConfig;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(JasperReportEngine.class);
	
    // init engine
    static {
    	engineConfig = JasperReportEngineConfig.getInstance();
    }
    
    public static JasperReportEngineConfig getConfig() {
    	return engineConfig;
    }
    
	/**
	 * Creates the instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @return the geo report engine instance
	 */
	public static JasperReportEngineInstance createInstance(JasperReportEngineTemplate template, Map env, DataSetServiceProxy dsProxy) {
		JasperReportEngineInstance engineInstance = null;
		logger.debug("IN");
		engineInstance = new JasperReportEngineInstance(template, env, dsProxy);
		
		if(JasperReportEngine.getConfig().isVirtualizationEbabled() == true) {
    		engineInstance.setVirtualizationEnabled( true );
    		engineInstance.setVirtualizer( JasperReportEngine.getConfig().getVirtualizer());
    	}
		engineInstance.setLibDir( JasperReportEngine.getConfig().getLibDir() );
		engineInstance.setWorkingDir( JasperReportEngine.getConfig().getTempDir() );
		    	
		String outputType = (String) env.get("outputType");
		engineInstance.setOutputType( outputType );
		engineInstance.setExporter( JasperReportEngine.getConfig().getExporter(outputType) );
		
		logger.debug("OUT");
		return engineInstance;	
	}
}
