/**
Copyright (c) 2005-2010, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.spagobi.engines.commonj.process;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

public class CmdExecWork extends SpagoBIWork {

	String command;
	String commandEnvironment;

	/** parameters  passed to command*/
	Vector<String> cmdParameters;
	/** to be added to classpath*/
	Vector<String> classpathParameters;
	/** Logger */
	static private Logger logger = Logger.getLogger(CmdExecWork.class);
	/** the process aunche*/
	Process process = null;
	/** the flag for automatic instance ID (external creation) */
	static final String INSTANCE_AUTO="INSTANCE=AUTO";

	public boolean isDaemon() {
		return false;
	}

	public void release() {
		logger.debug("IN");
		super.release();
		if(process != null){
			process.destroy();
			logger.info("Release the JOB");
		}
		logger.debug("OUT");
	}




	/** this method executes command followed by command parameters taken from template
	 *  and by sbi parameters
	 *  and add classpath variables followed by -cp
	 * 
	 * @param cmd
	 * @param envFile
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */

	public int execCommand() throws InterruptedException, IOException {
		logger.debug("IN");
		File directoryExec = null;
		boolean isInstanceAuto = false;
		
		if(commandEnvironment != null) {
			directoryExec = new File(commandEnvironment);
			logger.info("commandEnvironment="+commandEnvironment);
		}
		// add -cp 
		if(classpathParameters.size()>0){
			command += " -cp ";
			for (Iterator iterator = classpathParameters.iterator(); iterator.hasNext();) {
				String add = (String) iterator.next();
				command += add;
				if(iterator.hasNext()){
					command += ";";
				}
			}
		}

		command += " ";

		// add command parameters
		for (Iterator iterator = cmdParameters.iterator(); iterator.hasNext();) {
			String par = (String) iterator.next();		
			command += par + " ";
			isInstanceAuto = (par.toUpperCase().indexOf(INSTANCE_AUTO) > -1)?true:false;
		}


		// select sbi driver from MAP and add values!
		for (Iterator iterator = analyticalParameters.iterator(); iterator.hasNext();) {
			String url= (String) iterator.next();
			if (sbiParameters.get(url) != null){
				Object value = sbiParameters.get(url);
				if (value!=null && !value.equals("") ) {
					command += url + "=" + value.toString() +" "; 
				}
			}
		}

		// add pid to command if it's non defined as 'AUTO' definition
		if (!isInstanceAuto){
			String pidStr = "instance="+pid;
			command += pidStr;
		}
		

    	if(isRunning()){
			logger.info("launch command "+command);
			process = Runtime.getRuntime().exec(command, null, directoryExec);
			logger.info("Wait for the end of the process... ");
			
			StreamGobbler errorGobbler = new 
            StreamGobbler(process.getErrorStream(), "ERROR");
			
			 StreamGobbler outputGobbler = new 
             StreamGobbler(process.getInputStream(), "OUTPUT");
			 
			 errorGobbler.start();
	         outputGobbler.start();
	         
	         int exitVal = process.waitFor();

/*
		 		BufferedReader input =
		 	 	new BufferedReader(new InputStreamReader(process.getInputStream()));
		 	 	while (( input.readLine()) != null) {
		 	 		
		 	 	}
		 	 	
		 	 	input.close();
		 	 	process.waitFor();
		 	 	*/
		 	 	
			logger.info("Process END "+command);
		}
		else{
			logger.warn("Command not launched cause work has been stopper");
		}

		logger.debug("OUT");
		return 0;

	}


	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommandEnvironment() {
		return commandEnvironment;
	}

	public void setCommandEnvironment(String commandEnvironment) {
		this.commandEnvironment = commandEnvironment;
	}

	public Vector<String> getCmdParameters() {
		return cmdParameters;
	}

	public void setCmdParameters(Vector<String> cmdParameters) {
		this.cmdParameters = cmdParameters;
	}

	public Vector<String> getClasspathParameters() {
		return classpathParameters;
	}

	public void setClasspathParameters(Vector<String> classpathParameters) {
		this.classpathParameters = classpathParameters;
	}


	class StreamGobbler extends Thread
	{
	    InputStream is;
	    String type;
	    
	    StreamGobbler(InputStream is, String type)
	    {
	        this.is = is;
	        this.type = type;
	    }
	    
	    public void run()
	    {
	        try
	        {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	                System.out.println(type + ">" + line);    
	            } catch (IOException ioe)
	              {
	            	logger.error(ioe);
	              }
	    }
	}

}
