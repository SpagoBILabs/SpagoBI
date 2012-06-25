/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.classloader;


import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ClassLoaderManager{
	
	public static ClassLoader qbeClassLoader;
	private static long jarFileTimeStamp;
	
	private static transient Logger logger = Logger.getLogger(ClassLoaderManager.class);
	
	
	/**
	 * Updates the class loader of the thread and sets the class loader in the
	 * variable qbeClassLoader..
	 * NOTE: The qbeClassLoader is static
	 * @param jarFile
	 * @return
	 */
	public static ClassLoader updateCurrentWebClassLoader(File jarFile){
		
		logger.debug("IN");
		  
		try {
			
			logger.debug("jar file to be loaded: " + jarFile.getAbsoluteFile());
			jarFile.lastModified();
			if(qbeClassLoader!=null){
				if (qbeClassLoader instanceof DynamicClassLoader) {
					DynamicClassLoader dcl = (DynamicClassLoader) qbeClassLoader;
					//check if the cached loader has the same jar of the one we need now
					if (	(dcl.getJarFile().equals(jarFile)) 
							&& jarFileTimeStamp==jarFile.lastModified()//check if the file has been updated..
						) {
						logger.debug("Found a cached loader of type: "+ qbeClassLoader.getClass().getName());
						logger.debug("Set as current loader the one previusly cached");
						Thread.currentThread().setContextClassLoader(qbeClassLoader);
						return qbeClassLoader;//if so we return the cached one
					} else {//else we set the previous class loader
						Thread.currentThread().setContextClassLoader(dcl.getParent());
					}
				}else{
					logger.debug("Found a cached loader of type: "+ qbeClassLoader.getClass().getName());
					logger.debug("Set as current loader the one previusly cached");
					Thread.currentThread().setContextClassLoader(qbeClassLoader);
				}
			}
			//update the class loader 
			qbeClassLoader = updateCurrentClassLoader(jarFile);
			
		} catch (Exception e) {
			logger.error("Impossible to update current class loader", e);
		}

		return qbeClassLoader;
	}
	
	/**
	 * Update the thread class loader with a dynamic class loader that
	 * considers also the jar file
	 * @param file
	 * @return
	 */
	public static ClassLoader updateCurrentClassLoader(File file){
		
		ClassLoader cl =  Thread.currentThread().getContextClassLoader();
		
		boolean wasAlreadyLoaded = false;
		
		logger.debug("IN");
		
		JarFile jarFile = null;
		try {			
			jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String entryName = entry.getName();
					String className = entryName.substring(0, entryName.lastIndexOf(".class"));
					className = className.replaceAll("/", ".");
					className = className.replaceAll("\\\\", ".");
					try {
						logger.debug("loading class [" + className  + "]" + " with class loader [" + Thread.currentThread().getContextClassLoader().getClass().getName()+ "]");
						Thread.currentThread().getContextClassLoader().loadClass(className);
						wasAlreadyLoaded = true;
						logger.debug("Class [" + className  + "] has been already loaded (?)");
						break;
					} catch (Exception e) {
						wasAlreadyLoaded = false;
						logger.debug("Class [" + className  + "] hasn't be loaded yet (?)");
						break;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("Impossible to update current class loader", e);
		} finally{
			try {
				if(jarFile!=null){
					jarFile.close();
				}
			} catch (Exception e2) {
				logger.error("Error closing the jar file",e2);
			}
		}
		
		logger.debug("Jar file [" + file.getName()  + "] already loaded: " + wasAlreadyLoaded);
		
		try {

			if (!wasAlreadyLoaded) {
				
				ClassLoader previous = cl;
				Thread.currentThread().getContextClassLoader();
    		    DynamicClassLoader current = new DynamicClassLoader(file, previous);
			    Thread.currentThread().setContextClassLoader(current);
			    cl = current;
			    jarFileTimeStamp = file.lastModified();
			}
			
		} catch (Exception e) {
			logger.error("Impossible to update current class loader", e);
		}
		
		return cl;
	}
	
}

