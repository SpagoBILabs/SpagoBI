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
