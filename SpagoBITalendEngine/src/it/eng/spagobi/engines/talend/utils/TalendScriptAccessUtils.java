/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.spagobi.engines.talend.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import it.eng.spagobi.engines.talend.TalendEngineConfig;
import it.eng.spagobi.engines.talend.runtime.Job;
import it.eng.spagobi.engines.talend.runtime.RuntimeRepository;

/**
 * @author Andrea Gioia
 *
 */
public class TalendScriptAccessUtils {
	
	
	public static File getContextsBaseDir(Job job, RuntimeRepository runtimeRepository) {
		return runtimeRepository.getExecutableJobDir(job);
		//return new File(runtimeRepository.getExecutableJobDir(job), job.getProject().toLowerCase());
	}
	
	
	
	/**
	 * Gets the executable class.
	 * 
	 * @param job the job
	 * 
	 * @return the executable class
	 */
	public static String getExecutableClass(Job job) {
		StringBuffer buffer = new StringBuffer();
		
		if(job.isJavaJob()) {
			buffer.append(job.getProject().toLowerCase());
    		buffer.append(".");
    		buffer.append(job.getName().toLowerCase());
    		buffer.append("_" + job.getVersion().replace('.', '_'));
    		buffer.append(".");
    		//not more Java convention but original name
    		buffer.append(job.getName().substring(0,1) + job.getName().substring(1));
    		//buffer.append(job.getName().substring(0,1).toUpperCase() + job.getName().substring(1));    		
    	} else{
    		return null;
    	}
		
		
		return buffer.toString();
	}
	

	/**
	 * Gets the executable file name.
	 * 
	 * @param job the job
	 * 
	 * @return the executable file name
	 */
	public static String getExecutableFileName(Job job) {
		StringBuffer buffer = new StringBuffer();
		TalendEngineConfig config = TalendEngineConfig.getInstance();
		if(job.isPerlJob()) {
    		buffer.append(job.getProject());
    		buffer.append(config.getJobSeparator());
    		buffer.append(config.getWordSeparator());
    		buffer.append(job.getName());
    		buffer.append(config.getPerlExt());
    	} else if(job.isJavaJob()) {
    		buffer.append(job.getName().toLowerCase());
    		buffer.append("_" + job.getVersion().replace('.', '_'));
    		buffer.append(".jar");    		
    	} else{
    		return null;
    	}
		
		
		return buffer.toString();
	}
	
	/**
	 * Gets the included libs.
	 * 
	 * @param job the job
	 * @param runtimeRepository the runtime repository
	 * 
	 * @return the included libs
	 */
	public static List getIncludedLibs(Job job, RuntimeRepository runtimeRepository) {
		List libs = new ArrayList();
		if(job.isPerlJob()) {
    		// do nothing
    	} else if(job.isJavaJob()) {
    		File executableJobProjectDir = runtimeRepository.getExecutableJobProjectDir(job);
    		File libsDir = new File(executableJobProjectDir, "lib");
    		File[] files	= libsDir.listFiles();  
    		for(int i  = 0; i < files.length; i++) {
    			libs.add(files[i]);
    		}
    	} else{
    		return null;
    	}
		
		return libs;
	}
	
	
	/**
	 * Gets the class path.
	 * 
	 * @param job the job
	 * 
	 * @return the class path
	 */
	public static String getClassPath(Job job, RuntimeRepository runtimeRepository) {
    	StringBuffer classpath = new StringBuffer();
    	File libsDir;
    	File[] files;
    	
    	if(job.isPerlJob()) {
    		// do nothing
    	} else if(job.isJavaJob()) {
    		
    		//STEP1 : include ../lib directory
    		classpath.append( "../lib");
    		
    		//STEP2 : include jar in ../lib directory
    		File executableJobProjectDir = runtimeRepository.getExecutableJobProjectDir(job);
    		libsDir = new File(executableJobProjectDir, "lib");
    		files = libsDir.listFiles();  
    		for(int i  = 0; i < files.length; i++) {
    			classpath.append(File.pathSeparator + "../lib/" + files[i].getName());
    		}
    		
    		//STEP3 : include current directory
    		//classpath.append(File.pathSeparator + ".");
    		
    		//STEP4 : include jar in current directory
    		File executableJobDir = runtimeRepository.getExecutableJobDir(job);
    		files = executableJobDir.listFiles(new FilenameFilter(){
    			public boolean accept(File dir, String name) {
    				return name.endsWith(".jar");
    			}
    		});  
    		for(int i  = 0; i < files.length; i++) {
    			classpath.append(File.pathSeparator + files[i].getName());
    		}
    	}
    	
    	
    	return classpath.toString();
    }
	
	
	
	
	
	///////////////////////////////////////////////////////////////
	// CONTEXT HANDLING METHODS
	///////////////////////////////////////////////////////////////
	
	/**
	 * Gets the context file name.
	 * 
	 * @param job the job
	 * 
	 * @return the context file name
	 */
	public static String getContextFileName(Job job) {		
		StringBuffer buffer = new StringBuffer();
		TalendEngineConfig config = TalendEngineConfig.getInstance();
		if(job.isPerlJob()) {
    		buffer.append(job.getProject());
    		buffer.append(config.getJobSeparator());
    		buffer.append(config.getWordSeparator());
    		buffer.append(job.getName());
    		buffer.append(config.getWordSeparator());
    		buffer.append(job.getContext());
    		buffer.append(config.getPerlExt());
    	} else if(job.isJavaJob()) {
    		return null;
    	} else{
    		return null;
    	}
				
		return buffer.toString();
	}
	
	/**
	 * Gets the context file.
	 * 
	 * @param job the job
	 * @param runtimeRepository the runtime repository
	 * 
	 * @return the context file
	 */
	public static File getContextFile(Job job, RuntimeRepository runtimeRepository) {		
		File contextFile = null;
		StringBuffer buffer = new StringBuffer();
		
		TalendEngineConfig config = TalendEngineConfig.getInstance();
		if(job.isPerlJob()) {
    		buffer.append(job.getProject());
    		buffer.append(config.getJobSeparator());
    		buffer.append(config.getWordSeparator());
    		buffer.append(job.getName());
    		buffer.append(config.getWordSeparator());
    		buffer.append(job.getContext());
    		buffer.append(config.getPerlExt());
    		File executableJobDir = runtimeRepository.getExecutableJobDir(job);
    		String contextFileName = buffer.toString();
    		contextFile = new File(executableJobDir, contextFileName);
    	} else if(job.isJavaJob()) {
    		buffer.append(runtimeRepository.getExecutableJobFile(job));
    		buffer.append("!");
    		buffer.append("\\" + job.getProject().toLowerCase());
    		buffer.append("\\" + job.getName().toLowerCase());
    		buffer.append("\\context");
    		buffer.append("\\" + job.getContext() + ".properties");
    		contextFile = new File(buffer.toString());
    	} else{
    		return null;
    	}
				
		return contextFile;
	}
}
