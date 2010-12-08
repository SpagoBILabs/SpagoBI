/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.engines.jasperreport;

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
	public static JasperReportEngineInstance createInstance(JasperReportEngineTemplate template, Map env) {
		JasperReportEngineInstance engineInstance = null;
		logger.debug("IN");
		engineInstance = new JasperReportEngineInstance(template, env);
		
		if(JasperReportEngine.getConfig().isVirtualizationEbabled() == true) {
    		engineInstance.setVirtualizationEnabled( true );
    		engineInstance.setVirtualizer( JasperReportEngine.getConfig().getVirtualizer());
    	}
		engineInstance.setLibDir( JasperReportEngine.getConfig().getLibDir() );
		engineInstance.setWorkingDir( JasperReportEngine.getConfig().getTempDir() );
		    	
		logger.debug("---> " + env.get("outputType"));
		String outputType = (String) env.get("outputType");
		engineInstance.setOutputType( outputType );
		engineInstance.setExporter( JasperReportEngine.getConfig().getExporter(outputType) );
		
		logger.debug("OUT");
		return engineInstance;	
	}
}
