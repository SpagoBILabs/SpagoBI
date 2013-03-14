/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.datasource;

import it.eng.qbe.datasource.configuration.dao.DAOException;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.qbe.exporter.QbeCSVExporter;
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

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia
 */
public class MetamodelJarFileRetriever {

	protected File metamodelsLocalDir;
	protected MetamodelServiceProxy metamodelServiceProxy;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(MetamodelJarFileRetriever.class);
	
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
	
		File metamodelJarFile;
		
		logger.trace("IN");
		
		metamodelJarFile = null;
		try {
			Assert.assertTrue(StringUtilities.isNotEmpty(metamodelName), "Input parameter [metamodelName] cannot be null");
			logger.debug("Load metamodel jar file for model [" + metamodelName + "]");
			
			File targetMetamodelFolder = new File(getDatamartsDir(), metamodelName);		
			metamodelJarFile = new File(targetMetamodelFolder, "datamart.jar");
			
			if (metamodelJarFile.exists()) {
				logger.debug("jar file for metamodel [" + metamodelName + "] has been already loaded in folder [" + targetMetamodelFolder + "]");
				long localVersionLastModified = metamodelJarFile.lastModified();
				long remoteVersionLastModified = metamodelServiceProxy.getMetamodelContentLastModified(metamodelName);
				if(localVersionLastModified < remoteVersionLastModified) {
					downloadJarFile(metamodelName, targetMetamodelFolder);
				}
			} else {
				logger.debug("jar file for metamodel [" + metamodelName + "] has not been already downloaded");
				downloadJarFile(metamodelName, targetMetamodelFolder);
			}
			
			Assert.assertTrue(metamodelJarFile.exists(), "After load opertion file [" + metamodelJarFile + "] must exist");
		} catch (Throwable t) {
			if(t instanceof SpagoBIEngineRuntimeException) throw (SpagoBIEngineRuntimeException)t;
			throw new SpagoBIEngineRuntimeException("An unexpected error occured while loading metamodel's jar file", t);
		}
		
		return metamodelJarFile;
	}
	
	/**
	 * Download the jarFile from SpagoBI server and store it on the local filesystem in the
	 * specified folder
	 * 
	 * @param metamodelName the name of the metamodel to download
	 * @param destinationFolder the destination folder on the local filesystem
	 */
	private void downloadJarFile(String metamodelName, File destinationFolder) {
		DataHandler handler = null;
		try {
			logger.debug("Loading jar file for metamodel [" + metamodelName + "] from SpagoBI server...");
			handler = metamodelServiceProxy.getMetamodelContentByName(metamodelName);
			if(handler == null) throw new SpagoBIEngineRuntimeException("Metamodel Service returns null value");
			logger.debug("jar file for metamodel [" + metamodelName + "] has been loaded succesfully from SpagoBI server");
		} catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load jar file of metamodel [" + metamodelName + "] from SpagoBiServer", t);
		}
		
		logger.debug("Copying jar file of metamodel [" + metamodelName + "] locally into folder [" + destinationFolder + "] ...");
		storeJarFile(handler, destinationFolder);
		logger.debug("jar file of metamodel [" + metamodelName + "] succesfully copied locally into folder [" + destinationFolder + "] ...");
	}
	
	/**
	 * Store the jarFile on local filesystem
	 * 
	 * @param dataHandler the jarFile content
	 * @param destinationFolder the destination folder on the local filesystem
	 */
	private void storeJarFile(DataHandler dataHandler, File destinationFolder) {
		
		File metamodelJarFile = new File(destinationFolder, "datamart.jar");
		
		if(metamodelJarFile.exists()) {
			metamodelJarFile.delete();
		}
		
		if(!destinationFolder.exists()) destinationFolder.mkdirs();
		
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			fos = new FileOutputStream(metamodelJarFile);
		       is = dataHandler.getInputStream();
			int c = 0;
			byte[] b = new byte[1024];
			while ((c = is.read(b)) != -1) {
				if (c == 1024)
					fos.write(b);
				else
					fos.write(b, 0, c);
			}
		       fos.flush();
		} catch (Throwable t) {
			throw new SpagoBIEngineRuntimeException("An unexpected error occured while saving localy metamodel's jar file", t);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("Error while closing DataHandler input stream", e);
			}
			try {
				fos.close();
			} catch (IOException e) {
				logger.error("Error while closing file output stream", e);
			}
		}
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
