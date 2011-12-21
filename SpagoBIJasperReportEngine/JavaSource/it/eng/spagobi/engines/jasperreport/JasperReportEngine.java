/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
