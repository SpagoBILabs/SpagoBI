/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.datasource;

import it.eng.qbe.datasource.configuration.dao.DAOException;
import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Andrea Gioia
 */
public class JarFileRetriever {

	protected File datamartsDir;
	
	// ============================================================
	// COSTRUCTORS
	// ============================================================
	
	public JarFileRetriever(File datamartsDir) {
		Assert.assertNotNull(datamartsDir, "Parameter [datamartsDir] cannot be null");
		if(!datamartsDir.exists()) {
			throw new DAOException("Folder [" + datamartsDir.getName() + "] does not exist.");
		}
		if(!datamartsDir.isDirectory()) {
			throw new DAOException("File [" + datamartsDir.getName() + "] is not a folder.");
		}
		this.setDatamartsDir(datamartsDir);
	}
	
	// ============================================================
	// ACCESSORS
	// ============================================================
	
	private File getDatamartsDir() {
		return datamartsDir;
	}


	private void setDatamartsDir(File datamartsDir) {
		this.datamartsDir = datamartsDir;
	}
	
	// ============================================================
	// DAO (load & save)
	// ============================================================
	
	public File loadDatamartJarFile(String datamartName) {
		File targetDatamartDir;
		File datamartJarFile;
		
		targetDatamartDir = new File(getDatamartsDir(), datamartName);		
		datamartJarFile = new File(targetDatamartDir, "datamart.jar");
		
		if (!datamartJarFile.exists()) {
			throw new DAOException("Impossible to load datamart [" + datamartName + "]. The associated mapping file [" + datamartJarFile + "] does not exist");
		}
		
		return datamartJarFile;
	}
	
	public void saveDatamartJarFile(String datamartName, File jarFile) {
		
	}

	public boolean isAJPADatamartJarFile(File datamartFile) {
		ZipFile zipFile;
		ZipEntry zipEntry;
		
		try {
			zipFile = new ZipFile(datamartFile);
		} catch (Throwable t) {
			throw new DAOException("Impossible to read jar file [" + datamartFile + "]");
		} 
		
		zipEntry = zipFile.getEntry("META-INF/persistence.xml");
		
		return zipEntry!=null;
	}
	

	


}
