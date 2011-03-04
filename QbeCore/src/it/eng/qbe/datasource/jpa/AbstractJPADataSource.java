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
package it.eng.qbe.datasource.jpa;

import it.eng.qbe.bo.DatamartJarFile;
import it.eng.qbe.conf.QbeCoreSettings;
import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.model.io.IDataMartModelRetriever;
import it.eng.spago.base.ApplicationContainer;
import it.eng.spagobi.utilities.DynamicClassLoader;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;




public abstract class AbstractJPADataSource extends AbstractDataSource implements IJpaDataSource {

	
	private static transient Logger logger = Logger.getLogger(AbstractJPADataSource.class);
	
	
	/**
	 * Gets the datamart jar file.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the datamart jar file
	 */
	protected File getDatamartJarFile(String datamartName){
		//File datamartJarFile = null;
		File datamartFile = null;
		
		try{
			DatamartJarFile datamartJarFile = DAOFactory.getDatamartJarFileDAO().loadDatamartJarFile(datamartName);
			datamartFile = datamartJarFile.getFile() ;
		}catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
		
		//return datamartJarFile;
		return datamartFile;
	}
	
	/**
	 * Gets the view names.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the view names
	 */
	protected List getViewNames(String datamartName) {
		List viewNames = null;
		IDataMartModelRetriever dataMartModelRetriever;
		try {
			dataMartModelRetriever = QbeCoreSettings.getInstance().getDataMartModelRetriever();
			viewNames = dataMartModelRetriever.getViewNames(datamartName);
		} catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}		
		
		return viewNames;
	}
	
	/**
	 * Gets the view jar file.
	 * 
	 * @param datamartName the datamart name
	 * @param viewName the view name
	 * 
	 * @return the view jar file
	 */
	protected File getViewJarFile(String datamartName, String viewName){
		File viewJarFile = null;
		
		try{
			IDataMartModelRetriever dataMartModelRetriever = QbeCoreSettings.getInstance().getDataMartModelRetriever();
			viewJarFile =  dataMartModelRetriever.getViewJarFile(datamartName, viewName);
		}catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
		
		return viewJarFile;
	}

	
	
	/**
	 * Load formula file.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the file
	 */
	protected File loadFormulaFile(String datamartName) {
		String formulaFile = getDatamartJarFile( datamartName ).getParent() + "/formula.xml";
		return new File(formulaFile);
	}
	
	/**
	 * Update current class loader.
	 * 
	 * @param jarFile the jar file
	 */
	protected static void updateCurrentClassLoader(File jarFile){
		
		boolean wasAlreadyLoaded = false;
		ApplicationContainer container = null;
		
		logger.debug("IN");
		
		try {
			
			logger.debug("jar file to be loaded: " + jarFile.getAbsoluteFile());
			
			container = ApplicationContainer.getInstance();
			if (container != null) {
				ClassLoader cl = (ClassLoader) container.getAttribute("DATAMART_CLASS_LOADER");
				if (cl != null) {
					logger.debug("Found a cached loader of type: " + cl.getClass().getName());
					logger.debug("Set as current loader the one previusly cached");
					Thread.currentThread().setContextClassLoader(cl);
				}
			}
			
			JarFile jar = new JarFile(jarFile);
			Enumeration entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String entryName = entry.getName();
					String className = entryName.substring(0, entryName.lastIndexOf(".class"));
					className = className.replaceAll("/", ".");
					className = className.replaceAll("\\\\", ".");
					try {
						logger.debug("loading class [" + className  + "]" + " with class loader [" + Thread.currentThread().getContextClassLoader().getClass().getName()+ "]");
						Thread.currentThread().getContextClassLoader().loadClass(className);
						wasAlreadyLoaded = true;
						logger.debug("Class [" + className  + "] has been already loaded (?");
						break;
					} catch (Exception e) {
						wasAlreadyLoaded = false;
						logger.debug("Class [" + className  + "] hasn't be loaded yet (?)");
						break;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
		
		logger.debug("Jar file [" + jarFile.getName()  + "] already loaded: " + wasAlreadyLoaded);
		
		try {
			/*
			 * TEMPORARY: the next instruction forcing the loading of all classes in the path...
			 * (ie. for some qbe that have in common any classes but not all and that at the moment they aren't loaded correctly)
			 */
			//wasAlreadyLoaded = false;

			if (!wasAlreadyLoaded) {
				
				ClassLoader previous = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().getContextClassLoader();
    		    DynamicClassLoader current = new DynamicClassLoader(jarFile, previous);
			    Thread.currentThread().setContextClassLoader(current);

			    //Thread.currentThread().getContextClassLoader().loadClass("it.eng.spagobi.meta.Customer");
			    
				//ClassLoader current = URLClassLoader.newInstance(new URL[]{jarFile.toURL()}, previous);				
				//Thread.currentThread().setContextClassLoader(current);
				
				if (container != null) container.setAttribute("DATAMART_CLASS_LOADER", current);
			}
			
		} catch (Exception e) {
			logger.error(DataMartModel.class, e);
		}
	}




	

}
