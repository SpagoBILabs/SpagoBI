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

import java.io.File;

import it.eng.qbe.bo.DatamartJarFile;

/**
 * @author Andrea Gioia
 */
public class DatamartJarFileDAOFilesystemImpl implements IDatamartJarFileDAO {

	
	private File datamartsDir;
	
	
	public DatamartJarFileDAOFilesystemImpl(File datamartsDir) {
		this.setDatamartsDir(datamartsDir);
	}
	
	public DatamartJarFile loadDatamartJarFile(String datamartName) {
		DatamartJarFile jarFile = null;
		File targetDatamartDir = null;
		File datamartJarFile = null;
		
		targetDatamartDir = new File(getDatamartsDir(), datamartName);
		datamartJarFile = new File(targetDatamartDir, "datamart.jar");
		
		if (datamartJarFile.exists()) {
			jarFile = new DatamartJarFile(datamartJarFile);
		}

		return jarFile;
	}

	public void saveDatamartJarFile(String datamartName, DatamartJarFile jarFile) {
		
	}
	

	private File getDatamartsDir() {
		return datamartsDir;
	}


	private void setDatamartsDir(File datamartsDir) {
		this.datamartsDir = datamartsDir;
	}

}
