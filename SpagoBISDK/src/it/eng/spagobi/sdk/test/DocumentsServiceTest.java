/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.sdk.test;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;



import it.eng.spagobi.sdk.documents.bo.SDKDocument;
import it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter;
import it.eng.spagobi.sdk.documents.bo.SDKExecutedDocumentContent;
import it.eng.spagobi.sdk.proxy.DocumentsServiceProxy;
import junit.framework.TestCase;

public class DocumentsServiceTest extends TestCase {

	String user = "biadmin";
	String password = "biadmin";
	DocumentsServiceProxy proxy = null;
	int documentId=5;
	int engineTypeId=10;// report jasper
	
	public DocumentsServiceTest(String name) {
		super(name);
		proxy = new DocumentsServiceProxy(user,password);
		proxy.setEndpoint("http://localhost:8080/SpagoBI/sdk/DocumentsService");
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testExecuteDocument()throws Exception {


			// opening a session into SpagoBI
			SDKDocument doc=new SDKDocument();
			
			doc.setLabel("ElencoImpiegati");
			doc.setName("ElencoImpiegati");
			doc.setDescription("ElencoImpiegati");
			doc.setId(documentId);
			doc.setState("REL");
			doc.setType("REPORT");
			doc.setEngineId(engineTypeId);
			
			
			SDKDocumentParameter par=new SDKDocumentParameter();
			par.setLabel("Dipartimento");
			par.setUrlName("department");
			Object[] ob={new String("3")};
			par.setValues(ob);

			
			SDKDocumentParameter[] array={par};
						
			
			SDKExecutedDocumentContent cont=	proxy.executeDocument(doc, array, "/spagobi/admin","PDF");

			DataHandler dh=cont.getContent();
			InputStream is=dh.getInputStream();

			
			
			File file=new File("C:/ciaociao.pdf");
			
			try {
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				int c;
				while((c = is.read()) != -1) {
					out.writeByte(c);
				}
				is.close();
				out.close();
			}
			catch(IOException e) {
				System.err.println("Error Writing/Reading Streams.");
				throw e;
			}

			assertNotNull(file);

	} 

}
