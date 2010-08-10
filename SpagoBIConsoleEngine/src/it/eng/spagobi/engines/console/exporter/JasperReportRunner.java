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
package it.eng.spagobi.engines.console.exporter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;

import org.apache.log4j.Logger;

import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JasperReportRunner {
	
	private String encoding;

	private static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	private static transient Logger logger = Logger.getLogger(JasperReportRunner.class);
	
	public JasperReportRunner() {
		this.setEncoding(DEFAULT_ENCODING);
	}
	
	public void run(String template, File reportFile, String outputType, JRDataSource dataSource, Locale locale) throws ExportException {
		InputStream in;
		OutputStream out;
		
		logger.debug("IN");
		try {
		
			Assert.assertNotNull(template, "Input parameter [template] cannot be null");
			Assert.assertNotNull(reportFile, "Input parameter [reportFile] cannot be null");
			Assert.assertNotNull(dataSource, "Input parameter [dataSource] cannot be null");
			
			try {
				in = new ByteArrayInputStream( template.getBytes(this.getEncoding()) );
			} catch (UnsupportedEncodingException e) {
				throw new ExportException("Impossible to decode template content", e);
			}
			
			try {
				out = new FileOutputStream(reportFile);
			} catch (FileNotFoundException e) {
				throw new ExportException("Impossible open a stream to file [" + reportFile + "]", e);
			}
			
			run(in, out, outputType, dataSource, locale);
			
			// release streams
			in.close();
			out.flush();
			out.close();
		} catch(Throwable t) {
			if(t instanceof ExportException) throw (ExportException)t;
			throw new ExportException("An unpredictable error occurs while running template", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	
	/**
	 * 
	 * @param in 					the template is read from this input stream
	 * @param out 					the exported report is written to this output stream
	 * @param outputType
	 * @param conn
	 * @param locale
	 * @throws ExportException
	 */
	public void run(InputStream in, OutputStream out, String outputType, JRDataSource dataSource, Locale locale) throws ExportException {
		
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		JRExporter exporter; 
		
		logger.debug("IN");
		
		try {
			logger.debug("Input parameter [outputType] is equal to [" + outputType +"]");
			logger.debug("Input parameter [locale] is equal to [" + locale +"]");
			
			setJasperClasspath();
			
			logger.debug("Compiling report...");
			try {
				jasperReport = JasperCompileManager.compileReport(in);
			} catch (Throwable t) {
				throw new ExportException("Impossible to compile report", t);
			}
			logger.debug("Report compiled succesfully");
	
			
			logger.debug("Filling report...");
			try {
				jasperPrint = JasperFillManager.fillReport(jasperReport, getFillManagerParams(locale), dataSource);
			} catch (Throwable t) {
				throw new ExportException("Impossible to fill report", t);
			}
			logger.debug("Report filled succesfully...");
					    
			logger.debug("Exporting report...");
		    try {
		    	exporter = getExporter(outputType);			
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM , out);
				exporter.exportReport();
		    } catch (Throwable t) {
				throw new ExportException("Impossible to export report to format [" + outputType + "]", t);
			}
		    logger.debug("Report exported succesfully...");
		    
		} catch(Throwable t) {
			if(t instanceof ExportException) throw (ExportException)t;
			throw new ExportException("An unpredictable error occurs while running template", t);
		} finally {
			logger.debug("OUT");
		}
	    
	}
	
	
	public Map getFillManagerParams(Locale locale) {
		Map params;
		ExporterConfiguration exporterConfig;
		
		params = new HashMap();
		exporterConfig = 	ExporterConfiguration.getInstance();
		
		
		String resourcePath = EnginConf.getInstance().getResourcePath()+ "/console/img/";
		params.put("SBI_RESOURCE_PATH", resourcePath);
		
		if (locale != null) {
			params.put("REPORT_LOCALE", locale);
		}
		
		if(exporterConfig.isVirtualizationEnabled()) {
			logger.debug("Virtualization is enabled");
			
			File pagingDir = exporterConfig.getPagingDir();
			logger.debug("Pagination dir is equal to [" + pagingDir.toString() + "]");
			if(!pagingDir.exists()) {
				logger.warn("Pagination dir [" + pagingDir.toString() + "] does not exist. It will be created");
				pagingDir.mkdirs();
				logger.warn("Pagination dir [" + pagingDir.toString() + "] sucesfully created");
			}
			
			int maxNumOfPages = exporterConfig.getMaxNumOfPages();
			logger.debug("Max number of pages is equal to [" + maxNumOfPages + "]");
			
			
			params.put(JRParameter.REPORT_VIRTUALIZER, new JRFileVirtualizer(maxNumOfPages, pagingDir.toString()));
			logger.debug("Virtualizer succesfully created");
		}
		
		return params;
	}
	
	public JRExporter getExporter(String outputType) {
		JRExporter exporter;
		if (outputType.equalsIgnoreCase("text/html")) {
		   	exporter = new JRHtmlExporter();
		   	exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
		   	exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
		} else if (outputType.equalsIgnoreCase("text/xml")) {
		   	exporter = new JRXmlExporter();
		} else if (outputType.equalsIgnoreCase("text/plain")) {
		   	exporter = new JRTextExporter(); 
		} else if (outputType.equalsIgnoreCase("text/csv")) {
		   	exporter = new JRCsvExporter(); 	
		} else if (outputType.equalsIgnoreCase("application/pdf"))	{			
		   	exporter = new JRPdfExporter(); 	
		} else if (outputType.equalsIgnoreCase("application/rtf"))	{			
		   	exporter = new JRRtfExporter(); 		
		} else if (outputType.equalsIgnoreCase("application/vnd.ms-excel")) {
		   	exporter = new JExcelApiExporter();
		} else {
		   	exporter = new JRPdfExporter();
		}
		return exporter;
	}

	private void setJasperClasspath(){
		// get the classpath used by JasperReprorts Engine (by default equals to WEB-INF/lib)
		String webinflibPath = ConfigSingleton.getInstance().getRootPath() + System.getProperty("file.separator") + "WEB-INF" + System.getProperty("file.separator") +"lib";
		
		// get all jar file names in the jasper classpath
		//logger.debug("Reading jar files from lib-dir...");
		StringBuffer jasperReportClassPathStringBuffer  = new StringBuffer();
		File f = new File(webinflibPath);
		String fileToAppend = null;
		
		if (f.isDirectory()){
			String[] jarFiles = f.list();
			for (int i=0; i < jarFiles.length; i++){
				String namefile = jarFiles[i];
				if(!namefile.endsWith("jar"))
					continue; // the inclusion of txt files causes problems
				fileToAppend = webinflibPath + System.getProperty("file.separator")+ jarFiles[i];
				//logger.debug("Appending jar file [" + fileToAppend + "] to JasperReports classpath");
				jasperReportClassPathStringBuffer.append(fileToAppend);
				jasperReportClassPathStringBuffer.append(System.getProperty("path.separator"));  
			}
		}
		
		String jasperReportClassPath = jasperReportClassPathStringBuffer.toString();
		jasperReportClassPath = jasperReportClassPath.substring(0, jasperReportClassPath.length() - 1);
		
		// set jasper classpath property
		System.setProperty("jasper.reports.compile.class.path", jasperReportClassPath);
		//logger.debug("Set [jasper.reports.compile.class.path properties] to value [" + System.getProperty("jasper.reports.compile.class.path")+"]");	
		
		// append HibernateJarFile to jasper classpath
		if(jasperReportClassPath != null && !jasperReportClassPath.equalsIgnoreCase("")) 
			jasperReportClassPath += System.getProperty("path.separator");
		
		//jasperReportClassPath += jarFile.toString();		
		System.setProperty("jasper.reports.compile.class.path", jasperReportClassPath);
	
	}
	
	public String getEncoding() {
		return encoding;
	}


	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
