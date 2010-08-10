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
import it.eng.spagobi.engines.talend.client.exception.UnsupportedEngineVersionException;

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
public class SpagoBITalendEngineClient implements ISpagoBITalendEngineClient {

	public static final int CLIENTAPI_MAJOR_VERSION_NUMBER = 0;
	public static final int CLIENTAPI_MINOR_VERSION_NUMBER = 5;
	public static final int CLIENTAPI_REVISION_VERSION_NUMBER = 0;
	public static final String CLIENTAPI_VERSION_NUMBER = CLIENTAPI_MAJOR_VERSION_NUMBER + "." 
														+ CLIENTAPI_MINOR_VERSION_NUMBER + "."
														+ CLIENTAPI_REVISION_VERSION_NUMBER;
	
	private ISpagoBITalendEngineClient client;
	
	/**
	 * Instantiates a new spago bi talend engine client.
	 * 
	 * @param usr the usr
	 * @param pwd the pwd
	 * @param host the host
	 * @param port the port
	 * @param appContext the app context
	 * 
	 * @throws EngineUnavailableException the engine unavailable exception
	 * @throws ServiceInvocationFailedException the service invocation failed exception
	 * @throws UnsupportedEngineVersionException the unsupported engine version exception
	 */
	public SpagoBITalendEngineClient(String usr, String pwd, String host,String port, String appContext) 
	throws EngineUnavailableException, ServiceInvocationFailedException, UnsupportedEngineVersionException  { 
		String url = "http://" + host + ":" + port + "/" + appContext + "/EngineInfoService";
		String complianceVersion = getEngineComplianceVersion(url);
		String[] versionChunks = complianceVersion.split("\\.");
		int major = Integer.parseInt(versionChunks[0]);
		int minor = Integer.parseInt(versionChunks[1]);
		if(major > CLIENTAPI_MAJOR_VERSION_NUMBER 
				|| (major == CLIENTAPI_MAJOR_VERSION_NUMBER && minor > CLIENTAPI_MINOR_VERSION_NUMBER)) {
			throw new UnsupportedEngineVersionException("Unsupported engine version", complianceVersion);
		}
		
		if (major == 0 && minor == 5) {
			client = new SpagoBITalendEngineClient_0_5_0(usr, pwd, host, port, appContext);
		}		
	}
		
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#deployJob(it.eng.spagobi.engines.talend.client.JobDeploymentDescriptor, java.io.File)
	 */
	public boolean deployJob(JobDeploymentDescriptor jobDeploymentDescriptor, File executableJobFiles) 
	throws EngineUnavailableException, AuthenticationFailedException, ServiceInvocationFailedException {
		return client.deployJob(jobDeploymentDescriptor, executableJobFiles);
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#getEngineName()
	 */
	public String getEngineName() throws EngineUnavailableException, ServiceInvocationFailedException {
		return client.getEngineName();
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#getEngineVersion()
	 */
	public String getEngineVersion() throws EngineUnavailableException, ServiceInvocationFailedException {
		return client.getEngineVersion();
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient#isEngineAvailible()
	 */
	public boolean isEngineAvailible() throws EngineUnavailableException {
		return client.isEngineAvailible();
	}	

	/**
	 * Gets the engine compliance version.
	 * 
	 * @param url the url
	 * 
	 * @return the engine compliance version
	 * 
	 * @throws EngineUnavailableException the engine unavailable exception
	 * @throws ServiceInvocationFailedException the service invocation failed exception
	 */
	public static String getEngineComplianceVersion(String url) 
	throws EngineUnavailableException, ServiceInvocationFailedException {
		String version;
		HttpClient client;
		PostMethod method;
		NameValuePair[] nameValuePairs;
		
		version = null;
		client = new HttpClient();
		method = new PostMethod(url);
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));

	    nameValuePairs   = new NameValuePair[] {
	    		new NameValuePair("infoType", "complianceVersion")
	    };
       
        method.setRequestBody( nameValuePairs );
	    
	    try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {  
	        throw new ServiceInvocationFailedException("Service failed", 
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
}
