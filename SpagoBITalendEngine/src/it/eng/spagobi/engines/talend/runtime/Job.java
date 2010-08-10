/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
**/
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
