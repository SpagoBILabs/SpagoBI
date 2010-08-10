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
package it.eng.spagobi.engines.talend.client.demo;

import it.eng.spagobi.engines.talend.client.ISpagoBITalendEngineClient;
import it.eng.spagobi.engines.talend.client.JobDeploymentDescriptor;
import it.eng.spagobi.engines.talend.client.SpagoBITalendEngineClient;
import it.eng.spagobi.engines.talend.client.exception.AuthenticationFailedException;
import it.eng.spagobi.engines.talend.client.exception.EngineUnavailableException;
import it.eng.spagobi.engines.talend.client.exception.ServiceInvocationFailedException;
import it.eng.spagobi.engines.talend.client.exception.UnsupportedEngineVersionException;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

/**
 * @author Andrea Gioia
 *
 */
public class SpagoBITalendEngineClientDemo {

	private static void usage() {
		System.out.println("cmdName usr pwd host port context file");		
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
		
		if(args.length < 6) {
			usage();
			System.exit(1);
		}
		
		String user = args[0];
		String password = args[1];
		String host = args[2];
		String port = args[3];
		String applicationContext = args[4];
		String deploymentFile = args[5];
		
		
		try {		
			
			// create the client
			ISpagoBITalendEngineClient client 
				= new SpagoBITalendEngineClient(user, password, host, port, applicationContext);
			
			// get some informations about the engine instance referenced by the client
			System.out.println("Engine version: " + client.getEngineVersion());
			System.out.println("Engine fullname: " + client.getEngineName());
				
			// prepare parameters used during deployment
			JobDeploymentDescriptor jobDeploymentDescriptor = new JobDeploymentDescriptor("SpagoBITalendTest", "perl");
			File zipFile = new File(deploymentFile);
				
			// deploy job on engine runtime
			boolean result = client.deployJob(jobDeploymentDescriptor, zipFile);
			if(result) System.out.println("Jobs deployed succesfully");
			else System.out.println("Jobs not deployed");
				
		
		} catch (EngineUnavailableException e) {
			System.err.println("ERRORE: " + e.getMessage());
		} catch(AuthenticationFailedException e) {
			System.err.println("ERRORE: " + e.getMessage());
		} catch (UnsupportedEngineVersionException e) {
			System.err.println("ERROR: Unsupported engine version");	
			System.err.println("You are using TalendEngineClientAPI version " 
					+ SpagoBITalendEngineClient.CLIENTAPI_VERSION_NUMBER + ". "
					+ "The TalendEngine instance you are trying to connect to require TalendEngineClientAPI version "
					+ e.getComplianceVersion() + " or grater.");
		} catch (ServiceInvocationFailedException e) {
			System.err.println("ERRORE: " + e.getMessage());
			System.err.println("StatusLine: " + e.getStatusLine()
							   + "\nresponseBody: " + e.getResponseBody());
		} 
	}

	
}
