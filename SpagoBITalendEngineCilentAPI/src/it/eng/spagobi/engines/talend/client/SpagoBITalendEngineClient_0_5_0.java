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

import it.eng.spagobi.engines.talend.client.exception.AuthenticationFailedException;
import it.eng.spagobi.engines.talend.client.exception.EngineUnavailableException;
import it.eng.spagobi.engines.talend.client.exception.ServiceInvocationFailedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

/**
 * @author Andrea Gioia
 *
 */
class SpagoBITalendEngineClient_0_5_0 implements ISpagoBITalendEngineClient {
	String host;
	String port;
	String appContext;
			
	private static final String JOB_UPLOAD_SERVICE = "JobUploadService";
	private static final String ENGINE_INFO_SERVICE = "EngineInfoService";
	
	private String getServiceUrl(String serviceName) {
		return ("http://" + host + ":" + port + "/" + appContext + "/" + serviceName);
	}
	
	
	/**
	 * Instantiates a new spago bi talend engine client_0_5_0.
	 * 
	 * @param usr the usr
	 * @param pwd the pwd
	 * @param host the host
	 * @param port the port
	 * @param appContext the app context
	 */
	public SpagoBITalendEngineClient_0_5_0(String usr, String pwd, String host,String port, String appContext) { 
		this.host = host;
		this.port = port;
		this.appContext = appContext;
	} 

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#getEngineVersion()
	 */
	public String getEngineVersion() 
	throws EngineUnavailableException, ServiceInvocationFailedException {
		return getEngineInfo("version");
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#getEngineName()
	 */
	public String getEngineName() 
	throws EngineUnavailableException, ServiceInvocationFailedException {
		return getEngineInfo("name");
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#isEngineAvailible()
	 */
	public boolean isEngineAvailible()  {
		
		try {
			getEngineInfo("version");
		} catch (EngineUnavailableException e) {
			return false;
		} catch (ServiceInvocationFailedException e) {
			return false;
		}
		
		return true;		
	}
	
	private String getEngineInfo(String infoType) 
	throws EngineUnavailableException, ServiceInvocationFailedException {
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
	    	  throw new ServiceInvocationFailedException("Service '" + ENGINE_INFO_SERVICE + "' execution failed", 
		        		method.getStatusLine().toString(),
		        		method.getResponseBodyAsString());
	      } else {
	    	  version = method.getResponseBodyAsString();
	      }      

	    } catch (HttpException e) {
	    	throw new EngineUnavailableException("Fatal protocol violation: " + e.getMessage());	
	    } catch (IOException e) {
	    	throw new EngineUnavailableException("Fatal transport error: " + e.getMessage());
	    } finally {
	      // Release the connection.
	      method.releaseConnection();
	    }  	
		
		return version;
	}
		
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#deployJob(it.eng.spagobi.engines.talend.client.JobDeploymentDescriptor, java.io.File)
	 */
	public boolean deployJob(JobDeploymentDescriptor jobDeploymentDescriptor, File executableJobFiles)  
	throws EngineUnavailableException, AuthenticationFailedException, ServiceInvocationFailedException {
		
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
            }  else {
            	throw new ServiceInvocationFailedException("Service '" + JOB_UPLOAD_SERVICE + "' execution failed", 
 		        		method.getStatusLine().toString(),
 		        		method.getResponseBodyAsString());
            }
		} catch (HttpException e) {
	    	throw new EngineUnavailableException("Fatal protocol violation: " + e.getMessage());	
	    } catch (IOException e) {
	    	throw new EngineUnavailableException("Fatal transport error: " + e.getMessage());
	    } finally {
            method.releaseConnection();
            if(deploymentDescriptorFile != null) deploymentDescriptorFile.delete();
        }
        
        return result;
	}	
}
