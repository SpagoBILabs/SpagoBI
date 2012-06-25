/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.exporter;

import java.io.File;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ExporterConfiguration {
	
	// -- Common exporter settings ---------------------------------------------------------
	private File baseDir;
	
	
	// -- JasperReport exporter settings ---------------------------------------------------------
	/**
	 * if virtualization is enabled the exportation process will be paged on disk if necessary
	 */
	private boolean virtualizationEnabled = true;

	/** 
	 * the base directory in the filesystem where the paged out data by JRVirtualizer is to be stored
	 */
	private File pagingDir;	
	
	/**
	 * the maximum size (in JRVirtualizable objects) of the paged in cache
	 */
	private int maxNumOfPages = 100;
	
	/**
	 * the base template used to generate jasperreport's template file
	 */
	private File baseTemplateFile;

	

	private static final String DEFAULT_PAGINGDIR_NAME = "cache";
	private static final String DEFAULT_BASETEMPLATE_FILENAME = "C:\\ProgramFiles\\apache-tomcat-6.0.18\\resources\\console\\template.jrxml";
	
	
	
	private static ExporterConfiguration instance;
	public static ExporterConfiguration getInstance() {
		if(instance == null) {
			instance = new ExporterConfiguration();
		}
		return instance;
	}
	
	private ExporterConfiguration() {
		
	}
	
	public File getBaseDir() {
		if(baseDir == null) {
			baseDir = new File(System.getProperty("java.io.tmpdir"));
		}
		return baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}
	
	public File getPagingDir() {
		if(pagingDir == null) {
			pagingDir = new File(getBaseDir(), DEFAULT_PAGINGDIR_NAME);
		}
		return pagingDir;
	}

	public void setPagingDir(File pagingDir) {
		this.pagingDir = pagingDir;
	}
	
	public int getMaxNumOfPages() {
		return maxNumOfPages;
	}

	public void setMaxNumOfPages(int maxNumOfPages) {
		this.maxNumOfPages = maxNumOfPages;
	}
	
	public boolean isVirtualizationEnabled() {
		return virtualizationEnabled;
	}

	public void setVirtualizationEnabled(boolean virtualizationEnabled) {
		this.virtualizationEnabled = virtualizationEnabled;
	}
	
	public File getBaseTemplateFile() {
		if(baseTemplateFile == null) {
			setBaseTemplateFile( new File(DEFAULT_BASETEMPLATE_FILENAME) );
		}
		return baseTemplateFile;
	}

	public void setBaseTemplateFile(File baseTemplateFile) {
		this.baseTemplateFile = baseTemplateFile;
	}
}
