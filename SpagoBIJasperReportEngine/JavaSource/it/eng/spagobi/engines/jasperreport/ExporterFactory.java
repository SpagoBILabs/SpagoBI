/**
 * 
 * LICENSE: see COPYING file. 
 * 
 */
package it.eng.spagobi.engines.jasperreport;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;

import java.util.List;

import net.sf.jasperreports.engine.JRExporter;

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
