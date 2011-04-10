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
package it.eng.qbe.datasource.configuration.dao;

import it.eng.qbe.model.properties.SimpleModelProperties;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;

/**
 * 
 * @author Andrea Gioia
 */
public class ModelI18NPropertiesDAOFileImpl implements IModelI18NPropertiesDAO {
	
	File modelJarFile;
	
    public static transient Logger logger = Logger.getLogger(ModelI18NPropertiesDAOFileImpl.class);
    
    public ModelI18NPropertiesDAOFileImpl(File file) {
    	Assert.assertNotNull(file, "Parameter [file] connot be null");
    	modelJarFile = file;
    }
    
    
	public SimpleModelProperties loadProperties() {
		return loadProperties(null);
	}

	
	public SimpleModelProperties loadProperties(Locale locale) {
		Properties properties;		
		JarFile jarFile;
		
		Assert.assertTrue(modelJarFile.exists(), "The model file [" + modelJarFile+ "] does not exist");
		Assert.assertTrue(modelJarFile.isFile(), "The model file [" + modelJarFile+ "] is a folder");
		
		properties = null;
		try {
			jarFile = new JarFile( modelJarFile );			
			properties = getLabelProperties(jarFile, locale);
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to load i18n properties from file [" + modelJarFile + "]", t);
		}
		
		return new SimpleModelProperties(properties);
	}
	
	private Properties getLabelProperties(JarFile modelJarFile, Locale locale){
		Properties properties;
		ZipEntry zipEntry;
		
		zipEntry = null;
		try {
			
			zipEntry = modelJarFile.getEntry( getI18NPropertiesFileName(locale) );
			if(zipEntry == null) {
				zipEntry = modelJarFile.getEntry(getI18NPropertiesFileName(null));
			}
			
			properties = new Properties();
			if (zipEntry != null) {
				properties.load(modelJarFile.getInputStream(zipEntry));
			} 		
			
			return properties;
		}catch(IOException ioe){
			throw new SpagoBIRuntimeException("Impossible to load properties from file [" + zipEntry + "]");
		}

	}
	
	private String getI18NPropertiesFileName(Locale locale) {
		String fileName;
		
		fileName = null;
		if(locale != null) {
			fileName = "label_" + locale.getLanguage() + ".properties";
		} else {
			fileName = "label.properties";
		}
		
		return fileName;
	}

}
