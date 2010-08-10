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
package it.eng.spagobi.engines.talend.client;

import it.eng.spagobi.engines.talend.exception.AuthenticationFailedException;
import it.eng.spagobi.engines.talend.runtime.JobDeploymentDescriptor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia
 *
 */
public class SpagoBITalendEngineClient {
	String host;
	String port;
	String appContext;
	
	private static transient Logger logger = Logger.getLogger(SpagoBITalendEngineClient.class);
	
	private static final String JOB_UPLOAD_SERVICE = "JobUploadService";
	private static final String ENGINE_INFO_SERVICE = "EngineInfoService";
	
	private String getServiceUrl(String serviceName) {
		return ("http://" + host + ":" + port + "/" + appContext + "/" + serviceName);
	}
	
	
	/**
	 * Instantiates a new spago bi talend engine client.
	 * 
	 * @param usr the usr
	 * @param pwd the pwd
	 * @param host the host
	 * @param port the port
	 * @param appContext the app context
	 * 
	 * @throws AuthenticationFailedException the authentication failed exception
	 */
	public SpagoBITalendEngineClient(String usr, String pwd, String host,String port, String appContext)  
	throws AuthenticationFailedException { 
		this.host = host;
		this.port = port;
		this.appContext = appContext;
	} 

	/**
	 * Gets the engine version.
	 * 
	 * @return the engine version
	 */
	public String getEngineVersion() {
		return getEngineInfo("version");
	}
	
	/**
	 * Gets the engine name.
	 * 
	 * @return the engine name
	 */
	public String getEngineName() {
		return getEngineInfo("name");
	}
	
	/**
	 * Checks if is engine availible.
	 * 
	 * @return true, if is engine availible
	 */
	public boolean isEngineAvailible() {
		return (getEngineInfo("version") != null);
	}
	
	private String getEngineInfo(String infoType) {
		String version;
		HttpClient client;
		PostMethod method;
		NameValuePair[] nameValuePairs;
		
		version = null;
		client = new HttpClient();
		method = new PostMethod(getServiceUrl(ENGINE_INFO_SERVICE));
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));

	   nameValuePairs   = new NameValuePair[] {
	    		new NameValuePair("infoType", infoType)
	    };
       
        method.setRequestBody( nameValuePairs );
	    
	    try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        System.err.println("Method failed: " + method.getStatusLine());
	        System.err.println("Response body: " + method.getResponseBodyAsString());
	      } else {
	    	  version = method.getResponseBodyAsString();
	      }      

	    } catch (HttpException e) {
	      System.err.println("Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    } catch (IOException e) {
	      System.err.println("Fatal transport error: " + e.getMessage());
	      e.printStackTrace();
	    } finally {
	      // Release the connection.
	      method.releaseConnection();
	    }  	
		
		return version;
	}
		
	/**
	 * Deploy job.
	 * 
	 * @param jobDeploymentDescriptor the job deployment descriptor
	 * @param executableJobFiles the executable job files
	 * 
	 * @return true, if successful
	 */
	public boolean deployJob(JobDeploymentDescriptor jobDeploymentDescriptor, File executableJobFiles)  {
		
		HttpClient client;
		PostMethod method;	
		File deploymentDescriptorFile;
		boolean result = false;
		
		client = new HttpClient();
		method = new PostMethod(getServiceUrl(JOB_UPLOAD_SERVICE));
		deploymentDescriptorFile = null;
		
		try {			
			deploymentDescriptorFile = File.createTempFile("deploymentDescriptor", ".xml");		
			FileWriter writer = new FileWriter(deploymentDescriptorFile);
			writer.write(jobDeploymentDescriptor.toXml());
			writer.flush();
			writer.close();
					
				        
			Part[] parts = {
					new FilePart(executableJobFiles.getName(), executableJobFiles),
	                new FilePart("deploymentDescriptor", deploymentDescriptorFile)
	        };
			
			method.setRequestEntity(
					new MultipartRequestEntity(parts, method.getParams())
	        );
			
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			
        	int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                if(method.getResponseBodyAsString().equalsIgnoreCase("OK")) result = true;
            } else {
            	System.out.println(
                    "Upload failed, response=" + HttpStatus.getStatusText(status)
                );
            }
        } catch (Exception ex) {
        	System.err.println("ERROR: " + ex.getClass().getName() + " "+ ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            method.releaseConnection();
            if(deploymentDescriptorFile != null) deploymentDescriptorFile.delete();
        }
        
        return result;
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * 
	 * @throws ZipException the zip exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws ZipException, IOException {
		
		
		try {		
			SpagoBITalendEngineClient client 
			= new SpagoBITalendEngineClient("biadmin", "biadmin", "localhost", "8080", "SpagoBITalendEngine");
			
			if(client.isEngineAvailible()) {
				System.out.println("Engine version: " + client.getEngineVersion());
				System.out.println("Engine fullname: " + client.getEngineName());
				
				JobDeploymentDescriptor jobDeploymentDescriptor = new JobDeploymentDescriptor("Test", "perl");
				File zipFile = new File("C:\\Prototipi\\TalendJob2.zip");
				
				boolean result = client.deployJob(jobDeploymentDescriptor, zipFile);
				if(result) System.out.println("Jobs deployed succesfully");
				else System.out.println("Jobs not deployed");
			}	
		} catch(AuthenticationFailedException e) {
			System.err.println("Authentication failed");
		}
	}
	
}
