/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.dao;

import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Andrea Gioia
 */
public class DatamartJarFileDAOFilesystemImpl implements IDatamartJarFileDAO {

	protected File datamartsDir;
	
	// ============================================================
	// COSTRUCTORS
	// ============================================================
	
	public DatamartJarFileDAOFilesystemImpl(File datamartsDir) {
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
