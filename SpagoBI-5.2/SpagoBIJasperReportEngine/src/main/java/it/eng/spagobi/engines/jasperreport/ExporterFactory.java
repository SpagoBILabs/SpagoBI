/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport;

import net.sf.jasperreports.engine.JRExporter;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;
/**
 * 
 * @deprecated
 */
public class ExporterFactory {	
	
	/**
	 * Gets the exporter.
	 * 
	 * @param format the format
	 * 
	 * @return the exporter
	 */
	static public JRExporter getExporter(String format) {
		JRExporter exporter = null;
		
		SourceBean config = EnginConf.getInstance().getConfig();
		SourceBean exporterConfig = (SourceBean) config.getFilteredSourceBeanAttribute ("EXPORTERS.EXPORTER", "format", format);
		if(exporterConfig == null) return null;
		String exporterClassName = (String)exporterConfig.getAttribute("class");
		if(exporterClassName == null) return exporter;
		
		try {
			exporter = (JRExporter)Class.forName(exporterClassName).newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return exporter;
	}
	
	/**
	 * Gets the mIME type.
	 * 
	 * @param format the format
	 * 
	 * @return the mIME type
	 */
	static public String getMIMEType(String format) {
		String mimeType = null;
		SourceBean config = EnginConf.getInstance().getConfig();
		SourceBean exporterConfig = (SourceBean) config.getFilteredSourceBeanAttribute ("EXPORTERS.EXPORTER", "format", format);
		if(exporterConfig == null) return null;
		mimeType = (String)exporterConfig.getAttribute("mime");
		return mimeType;
	}
	
	/**
	 * Gets the default type.
	 * 
	 * @return the default type
	 */
	static public String getDefaultType(){
		String defaultType = null;
		SourceBean config = EnginConf.getInstance().getConfig();
		defaultType = (String)config.getAttribute("EXPORTERS.default");
		if(defaultType == null) defaultType = "html";
		return defaultType;
	}
}
