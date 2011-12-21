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
package it.eng.spagobi.tools.updatedocument;

import it.eng.spagobi.sdk.documents.bo.SDKDocument;
import it.eng.spagobi.sdk.documents.bo.SDKTemplate;
import it.eng.spagobi.sdk.proxy.DocumentsServiceProxy;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Application for update templetes inside the document of SpagoBI.
 * @date 09-09-2011
 * @author Monia Spinelli
 */

//Questa classe permette di aggiornare i templete dei documenti gi√† caricati in SpagoBI
public class UpdateDocument 
{
	static public void main(String [] args){ 
		UpdateDocument a = new UpdateDocument();
		a.readProperties();
	}
	
	//Leggo le impostazioni di accesso al server dal file .properties e aggiorno i templete
	 public void readProperties(){
	    	InputStream is = null;
	        try {
	        	
	            Properties prop = new Properties();
	            URL url = UpdateDocument.class.getClass().getResource("/image_update.properties");
	            is = url.openStream();
	            prop.load(is);
	            
	            String user = prop.getProperty("USER");
	            String password = prop.getProperty("PASS");
	            System.out.println("la user: "+user);
	            System.out.println("la pass: "+password);
	            DocumentsServiceProxy proxy = new DocumentsServiceProxy(user, password);
	            proxy.setEndpoint(prop.getProperty("URL_SPAGOBI"));
	            
	            this.updateImage(prop, proxy);
	           
	        } catch(IOException e) {
	            e.printStackTrace();
	        } finally {
	        	if (is != null) {
	        		try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
	        }
	  }
	 
	 //Costruisce il nuovo templete da aggiornare in funzione alle immagini presenti nella cartella utente definita nel file .properties
	 public void updateImage(Properties prop, DocumentsServiceProxy proxy) throws RemoteException{
		 File folder = new File(prop.getProperty("LOCAL_FOLDER"));
		 String[] files = folder.list();

 		for (int i = 0; i < files.length; i++){
 			SDKTemplate template = new SDKTemplate();
 		    File image = new File(prop.getProperty("LOCAL_FOLDER")+files[i]);
 		    FileDataSource fileDataSource = new FileDataSource(prop.getProperty("LOCAL_FOLDER")+files[i]);
 		    DataHandler dataHandler = new DataHandler(fileDataSource);	
 		    template.setFileName(image.getName());
 		    template.setContent(dataHandler);
 		    SDKDocument doc = proxy.getDocumentByLabel(prop.getProperty(files[i]));
 		    proxy.uploadTemplate(doc.getId(), template);
 		}
	 }
}