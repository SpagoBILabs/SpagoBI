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
package it.eng.spagobi.engines.talend.runtime;


import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.talend.exception.TalendEngineException;
import it.eng.spagobi.engines.talend.exception.TemplateParseException;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

/**
 * @author Andrea Gioia
 *
 */
public class Job {
	String name;
	String project;
	String language;
	String context;
	String version;
		
	
	public Job(SourceBean template) throws SpagoBIEngineException {
		this.load(template);
	}
	
	/**
	 * Instantiates a new job.
	 * 
	 * @param name the name
	 * @param project the project
	 * @param language the language
	 */
	public Job(String name, String project, String language) {
		this(name, project, language, null);
	}
	
	/**
	 * Instantiates a new job.
	 * 
	 * @param name the name
	 * @param project the project
	 * @param language the language
	 * @param context the context
	 */
	public Job(String name, String project, String language, String context) {
		this.name = name;
		this.project = project;
		this.language = language;
		this.context = context;
	}		
	
	public void load(SourceBean template) throws TemplateParseException {
		SourceBean jobSB;
		
		Assert.assertNotNull(template, "Input parameter [template] cannot be null");
		
		jobSB = (SourceBean)template.getAttribute("JOB");
		Assert.assertNotNull(jobSB, "template cannot be null");
		
		name = (String)jobSB.getAttribute("jobName");
		if(name == null) {
			throw new TemplateParseException(template, "Missing Talend project name in document template");
		}
	 
		project = (String)jobSB.getAttribute("project");
		if(project == null) {
			throw new TemplateParseException(template, "Missing Talend project name in document template");
		}
		
		language = (String)jobSB.getAttribute("language");
		if(language == null) {
			throw new TalendEngineException("Missing Talend job language in document template");
		}
		
		context = (String)jobSB.getAttribute("context");
	    
	    version = jobSB.getAttribute("version") != null? (String)jobSB.getAttribute("version"): "0.1";	    
	}
	
	
		
	/**
	 * Checks if is perl job.
	 * 
	 * @return true, if is perl job
	 */
	public boolean isPerlJob() {
		return (language!= null && language.equalsIgnoreCase("perl"));
	}
	
	/**
	 * Checks if is java job.
	 * 
	 * @return true, if is java job
	 */
	public boolean isJavaJob() {
		return (language!= null && language.equalsIgnoreCase("java"));
	}
	
	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public String getContext() {
		return context;
	}
	
	/**
	 * Sets the context.
	 * 
	 * @param context the new context
	 */
	public void setContext(String context) {
		this.context = context;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the project.
	 * 
	 * @return the project
	 */
	public String getProject() {
		return project;
	}
	
	/**
	 * Sets the project.
	 * 
	 * @param project the new project
	 */
	public void setProject(String project) {
		this.project = project;
	}
	
	/**
	 * Gets the language.
	 * 
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 * 
	 * @param language the new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * To xml.
	 * 
	 * @return the string
	 */
	public String toXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<ETL>");
		buffer.append("<JOB");
		if(name != null && !name.trim().equalsIgnoreCase("")) buffer.append(" jobName=" + name);
		if(project != null && !project.trim().equalsIgnoreCase("")) buffer.append(" project=" + project);
		if(language != null && !language.trim().equalsIgnoreCase("")) buffer.append(" language=" + language);
		if(context != null && !context.trim().equalsIgnoreCase("")) buffer.append(" context=" + context);
		buffer.append("/>");
		buffer.append("</ETL>");
		
		return buffer.toString();
	}
	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
