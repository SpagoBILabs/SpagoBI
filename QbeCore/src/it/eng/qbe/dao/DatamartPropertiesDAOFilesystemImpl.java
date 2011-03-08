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

import it.eng.qbe.bo.DatamartProperties;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

// TODO: Auto-generated Javadoc
/**
 * The Class DatamartPropertiesDAOFilesystemImpl.
 * 
 * @author Andrea Gioia
 */
public class DatamartPropertiesDAOFilesystemImpl implements
		IDatamartPropertiesDAO {
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.dao.DatamartPropertiesDAO#loadDatamartProperties(java.lang.String)
	 */
	public DatamartProperties loadDatamartProperties(String datamartName) {
		Properties properties = null;
		
		File datamartJarFile = DAOFactory.getDatamartJarFileDAO().loadDatamartJarFile(datamartName);
		//File dmJarFile = datamartJarFile
		if(datamartJarFile == null) return new DatamartProperties();
		
		JarFile jf = null;
		try {
			jf = new JarFile( datamartJarFile );
			properties = loadQbePropertiesFormJarFile(jf);
			
		} catch (IOException e) {
			e.printStackTrace();
			return new DatamartProperties();
		}				
		
		return new DatamartProperties( properties );	
	}
	
	/**
	 * Load qbe properties form jar file.
	 * 
	 * @param jf the jf
	 * 
	 * @return the properties
	 */
	private Properties loadQbePropertiesFormJarFile(JarFile jf){
		Properties prop = null;
		
		try{
			ZipEntry ze = jf.getEntry("qbe.properties");
			if (ze != null){
				prop = new Properties();
				prop.load(jf.getInputStream(ze));
			} else {
				prop = new Properties();
			}
		} catch(IOException ioe){
			ioe.printStackTrace();
			return new Properties();
		}
		return prop;
	}
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.dao.DatamartPropertiesDAO#saveDatamartProperties(java.lang.String, it.eng.qbe.bo.DatamartProperties)
	 */
	public void saveDatamartProperties(String datamartName,
			DatamartProperties datamartProperties) {

	}
}
