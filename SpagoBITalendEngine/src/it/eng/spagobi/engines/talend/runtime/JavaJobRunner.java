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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.safehaus.uuid.UUIDGenerator;

import it.eng.spagobi.engines.talend.TalendEngineConfig;
import it.eng.spagobi.engines.talend.exception.ContextNotFoundException;
import it.eng.spagobi.engines.talend.exception.JobExecutionException;
import it.eng.spagobi.engines.talend.exception.JobNotFoundException;
import it.eng.spagobi.engines.talend.utils.TalendScriptAccessUtils;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.file.FileUtils;
import it.eng.spagobi.utilities.file.IFileTransformer;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

/**
 * @author Andrea Gioia
 *
 */
public class JavaJobRunner implements IJobRunner {

	
	private RuntimeRepository runtimeRepository;
	
	public static final String DEFAULT_CONTEXT = "Default";
	
	public static final String DEFAULT_XMS_VALUE = "256";
	public static final String DEFAULT_XMX_VALUE = "1024";
	
	private static transient Logger logger = Logger.getLogger(JavaJobRunner.class);
	
	JavaJobRunner(RuntimeRepository runtimeRepository) {
		this.runtimeRepository = runtimeRepository;
	}
	
    public void run(Job job, Map parameters)  throws JobNotFoundException, ContextNotFoundException, JobExecutionException {
    	
    	File executableJobDir;    	
    	File tempDir;    	
    	String cmd;
    	
    	try {
	    	logger.debug("Starting run method with parameters: " +
	    			"project = [" + job.getProject() + "] ; " +
	    			"job name = [" + job.getName() + "] ; " +
	    			"context = [" + job.getContext() + "] ; " +
	    			"base dir = [" + runtimeRepository.getRootDir() + "].");
	
	    	
	    	
	    	executableJobDir = runtimeRepository.getExecutableJobDir(job);
	    	logger.debug("Job base folder is equals to [" + runtimeRepository.getExecutableJobDir(job) + "]");
	    		    	
	    	if (!runtimeRepository.containsJob(job)) {	    		
	    		throw new JobNotFoundException("Job [" + 
	    				runtimeRepository.getExecutableJobFile(job) + "] not found in repository");
	    	}
	    	logger.debug("Job [" + job.getName() +"] succesfully found in repository");
	    	
	    	tempDir = getTempDir(job);
	   		Assert.assertNotNull(tempDir, "Impossible to create a temporary working folder");
	    	logger.debug("Temporary working folder created succesfully in [" + tempDir + "]");
	    	
	    	initContexts(job, parameters, tempDir);
	    	cmd = getJavaExecutionCmd(job, tempDir);
	    		    		    	
	    	logger.debug("Java execution command is equal to [" + cmd + "]");
	    	
	    	// TODO: improve post-processing cleaning
	    	List filesToBeDeleted = new ArrayList();	    	
	    	executeCommand(cmd, job, parameters, filesToBeDeleted);
	    	
    	} catch (Throwable e) {
    		throw new JobExecutionException("An error occurred while starting up java command execution for job [" + job.getName() + "]", e);
    	}
    }
    
    private void executeCommand(String cmd, Job job, Map parameters, List filesToBeDeleted) {
    	WorkManager wm;
    	TalendWork jrt;
    	TalendWorkListener listener;
    	
    	logger.debug("IN");
    	try {
	    	wm = new WorkManager();
	    	jrt = new TalendWork(cmd, null, runtimeRepository.getExecutableJobDir(job), filesToBeDeleted,  parameters);
	    	listener = new TalendWorkListener((AuditServiceProxy)parameters.get(EngineConstants.ENV_AUDIT_SERVICE_PROXY), (
	    			EventServiceProxy)parameters.get(EngineConstants.ENV_EVENT_SERVICE_PROXY));
	    	wm.run(jrt, listener);
    	} catch (Throwable t) {
    		throw new RuntimeException("Impossible to execute command in a new thread", t);
		} finally {
    		logger.debug("OUT");
    	}
    }
    
    private String getJavaExecutionCmd(Job job, File tempDir) {
    	TalendEngineConfig config = TalendEngineConfig.getInstance();
    	String cmd = "java";
    	String classpath;
    	
    	classpath = "." + File.separatorChar + tempDir.getName() + File.pathSeparator 
		+ TalendScriptAccessUtils.getClassPath(job, runtimeRepository);
    	
    	String javaInstallDir = config.getJavaInstallDir();
    	if(config.getJavaInstallDir() != null) {
    		String javaBinDir = (config.getJavaBinDir() != null? config.getJavaBinDir(): "bin");
    		String javaCommand = (config.getJavaCommand() != null? config.getJavaCommand(): "java");
    		cmd = javaInstallDir + File.separatorChar + javaBinDir + File.separatorChar + javaCommand;
    	}
    	
    	String xmsValue = (config.getJavaCommandOption("Xms")!= null? config.getJavaCommandOption("Xms"): DEFAULT_XMS_VALUE);
    	String xmxValue = (config.getJavaCommandOption("Xmx")!= null? config.getJavaCommandOption("Xmx"): DEFAULT_XMX_VALUE);
    	cmd += " -Xms" + xmsValue +"M -Xmx" + xmxValue + "M -cp " + classpath  + " " + TalendScriptAccessUtils.getExecutableClass(job);
        
    	cmd = cmd + " --context=" + job.getContext();
    	
    	return cmd;
    }
    
    private File getTempDir(Job job) {
    	
    	File tempDir;
    	UUIDGenerator uuidGenerator;
    	String uuid;
    	String tempDirName;
    	String tempDirPath;
    	
    	logger.debug("IN");
    	
    	try {
	    	uuidGenerator = UUIDGenerator.getInstance();
	    	uuid = uuidGenerator.generateTimeBasedUUID().toString();
	    	tempDirName = "tmp" + uuid;
	    	tempDirPath = runtimeRepository.getExecutableJobDir(job).getAbsolutePath() + File.separatorChar + tempDirName;
	    	tempDir =  new File(tempDirPath);
    	} catch(Throwable t) {
    		throw new RuntimeException("An error ccurred while retriving temporary working folder");
    	} finally {
    		logger.debug("OUT");
    	}
    	
    	return tempDir;
    }
    
    private void initContexts(Job job, Map parameters, File destDir) {
    	
    	File contextsBaseDir;
    	File contextsProjectDir;
    	IFileTransformer transformer;
    	    	
    	logger.debug("IN");
    	
    	try {
    	
	    	contextsBaseDir = TalendScriptAccessUtils.getContextsBaseDir(job, runtimeRepository);
	    	contextsProjectDir = new File(contextsBaseDir, job.getProject().toLowerCase());
	    	transformer = new ContextFileTransformer(job, parameters, contextsBaseDir, destDir);
	    	    	
	    	// for each context files in contextsProjectDir apply transformation
	    	FileUtils.doForEach(contextsProjectDir, transformer);
    	
    	} catch(Throwable t) {
    		throw new RuntimeException("An error ccurred while inizializing contexts");
    	} finally{
    		logger.debug("OUT");
    	}
  
    }
    
    
    private static class ContextFileTransformer implements IFileTransformer {
		Job job;
		Map parameters;
		File destDir;
		File srcDir; 
		
		public ContextFileTransformer(Job job, Map parameters, File srcDir, File destDir) {
			this.job = job;
			this.parameters = parameters;
			this.destDir = destDir;
			this.srcDir = srcDir;
		}

		public boolean transform(File file) {
			String str1 = file.getName();
			String str2 = job.getContext() + ".properties";
			
			if(file.getName().equalsIgnoreCase(job.getContext() + ".properties")) {
				try {
				
					Properties context = new Properties();
					FileInputStream in = new FileInputStream(file);
					
		        	context.load( in );
		        	Iterator it = parameters.keySet().iterator();
		        	while(it.hasNext()) {
		        		String pname = (String)it.next();
		        		Object o = parameters.get(pname);
		        		
		        		if(o instanceof String) {
		        			String pvalue = (String)parameters.get(pname);
		        			context.setProperty(pname, pvalue);
		        		}		        		
		        	}
		        	File destFile = null;
		        	
		        	String pathToContext = file.getAbsolutePath().substring(srcDir.getAbsolutePath().length());
		        	destFile = new File(destDir, pathToContext);
		        	destFile.getParentFile().mkdirs();
		        	OutputStream out = new FileOutputStream(destFile);
		        	context.store(out, "Talend Context");
		        	
		        	in.close();
		        	out.flush();
		        	out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			
			return true;
			
		}    		
	}
}
