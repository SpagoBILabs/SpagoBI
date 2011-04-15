/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.classloader;


import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
//import it.eng.spagobi.utilities.DynamicClassLoader;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ClassLoaderManager{
	
	public static ClassLoader qbeClassLoader;
	
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
			
			if(qbeClassLoader != null) {
				logger.debug("Found a cached loader of type: " + qbeClassLoader.getClass().getName());
				logger.debug("Set as current loader the one previusly cached");
				Thread.currentThread().setContextClassLoader(qbeClassLoader);
			}
			
			updateCurrentClassLoader(jarFile);
			
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
			Enumeration entries = jarFile.entries();
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
			}
			
		} catch (Exception e) {
			logger.error("Impossible to update current class loader", e);
		}
		
		return cl;
	}
	
}
