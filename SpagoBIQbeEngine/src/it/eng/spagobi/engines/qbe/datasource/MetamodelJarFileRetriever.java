/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.datasource;

import it.eng.qbe.datasource.configuration.dao.DAOException;
import it.eng.spagobi.services.proxy.MetamodelServiceProxy;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.DataHandler;

/**
 * @author Andrea Gioia
 */
public class MetamodelJarFileRetriever {

	protected File metamodelsLocalDir;
	protected MetamodelServiceProxy metamodelServiceProxy;
	
	// ============================================================
	// COSTRUCTORS
	// ============================================================
	
	public MetamodelJarFileRetriever(MetamodelServiceProxy metamodelServiceProxy, File metamodelsLocalDir) {
		Assert.assertNotNull(metamodelsLocalDir, "Parameter [datamartsDir] cannot be null");
		if(!metamodelsLocalDir.exists()) {
			throw new DAOException("Folder [" + metamodelsLocalDir.getName() + "] does not exist.");
		}
		if(!metamodelsLocalDir.isDirectory()) {
			throw new DAOException("File [" + metamodelsLocalDir.getName() + "] is not a folder.");
		}
		this.setDatamartsDir(metamodelsLocalDir);
		
		this.metamodelServiceProxy = metamodelServiceProxy;
	}
	
	// ============================================================
	// ACCESSORS
	// ============================================================
	
	private File getDatamartsDir() {
		return metamodelsLocalDir;
	}


	private void setDatamartsDir(File lacalDir) {
		this.metamodelsLocalDir = lacalDir;
	}
	
	// ============================================================
	// DAO (load & save)
	// ============================================================
	
	public File loadMetamodelJarFile(String metamodelName) {
		File targetMetamodelDir;
		File metamodelJarFile;
		
		targetMetamodelDir = new File(getDatamartsDir(), metamodelName);		
		metamodelJarFile = new File(targetMetamodelDir, "datamart.jar");
		
		if (!metamodelJarFile.exists()) {
			DataHandler handler = null;
			try {
				handler = metamodelServiceProxy.getMetamodelContentByName(metamodelName);	
			} catch(Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to load metamodel [" + metamodelName + "]", t);
			}
			if(handler != null) {
				FileOutputStream fos = null;
				InputStream is = null;
				try {
					fos = new FileOutputStream(metamodelJarFile);
			        is = handler.getInputStream();
					int c = 0;
					byte[] b = new byte[1024];
					while ((c = is.read(b)) != -1) {
						if (c == 1024)
							fos.write(b);
						else
							fos.write(b, 0, c);
					}
			        fos.flush();
				} catch (IOException e) {
					//logger.error("Error while storing Mondrian schema into a file", e);
					throw new SpagoBIEngineRuntimeException("Error while storing Mondrian schema into a file", e);
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						//logger.error("Error while closing DataHandler input stream", e);
					}
					try {
						fos.close();
					} catch (IOException e) {
						//logger.error("Error while closing file output stream", e);
					}
				}
			} else {
				throw new SpagoBIRuntimeException("Impossible to load metamodel [" + metamodelName + "]");
			}
		}

		return metamodelJarFile;
	}
	
	public void saveMetamodelJarFile(String metamodelName, File metamodelJarFile) {
		
	}

	public boolean isAJPADatamartJarFile(File metamodelJarFile) {
		ZipFile zipFile;
		ZipEntry zipEntry;
		
		try {
			zipFile = new ZipFile(metamodelJarFile);
		} catch (Throwable t) {
			throw new DAOException("Impossible to read jar file [" + metamodelJarFile + "]");
		} 
		
		zipEntry = zipFile.getEntry("META-INF/persistence.xml");
		
		return zipEntry!=null;
	}
	

	


}
