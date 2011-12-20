/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.

 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

 **/
package it.eng.spagobi.utilities;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

public class DynamicClassLoader extends URLClassLoader {

	private static transient Logger logger = Logger.getLogger(DynamicClassLoader.class);
	
	private ClassLoader parentCL = null;
	private File jar;

	/**
	 * Instantiates a new dynamic class loader.
	 * 
	 * @param jarFileName the jar file name
	 * @param cl the cl
	 */
	public DynamicClassLoader(String jarFileName, ClassLoader cl) {		
		this (new File(jarFileName), cl);
	}



	/**
	 * Instantiates a new dynamic class loader.
	 * 
	 * @param jarFile the jar file
	 * @param cl the cl
	 */
	public DynamicClassLoader(File jarFile, ClassLoader cl) {
		super(new URL[0], cl);
		jar = jarFile;
		parentCL = cl;
	}
	
//	/* (non-Javadoc)
//	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
//	 */
//	public Class loadInnerClass(String className) throws ClassNotFoundException {
//		return (loadClass(className, true));
//	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	
	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	public synchronized Class loadClass(String className, boolean resolve) throws ClassNotFoundException {

		Class classToReturn = null;
		try {
			classToReturn = super.loadClass(className, resolve);
		} catch (Exception e) {
			logger.warn("Impossible to load class [" + className + "]");
		}
		if(classToReturn == null) {
			ZipFile zipFile = null;
			BufferedInputStream bis = null;
			byte[] res = null;
			try {
				zipFile = new ZipFile(jar);
				ZipEntry zipEntry = zipFile.getEntry(className.replace('.', '/')+".class");
				res = new byte[(int)zipEntry.getSize()];
				bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				bis.read(res, 0, res.length);
			} catch (Exception ex) {
				logger.warn("className: " +  className + " Exception: "+ ex);
			} finally {
				if (bis!=null) {
					try {
						bis.close();
					} catch (IOException ioex) {}
				}
				if (zipFile!=null) {
					try {
						zipFile.close();
					} catch (IOException ioex) {}
				}
			}

//			try {
				if (res == null) 
					return super.findSystemClass(className);

				classToReturn = defineClass(className, res, 0, res.length);
				if (classToReturn == null) 
					throw new ClassFormatError();

				if (resolve) 
					resolveClass(classToReturn);
//			} catch (Throwable ex) {
//				logger.error(ex);
//				throw new ClassNotFoundException("Impossible to load class " + className, ex);
//			}
		}
		return classToReturn;
	}

	
	
    /**
     * Returns an input stream for reading the specified resource. 
     * We overwrite the parent method for get class from the datamart.jar file
     * @param The resource name 
     * @return An input stream for reading the resource, or null if the resource could not be found
     */
	public synchronized InputStream getResourceAsStream(String resourceName)  {
		ZipFile zipFile = null;
		InputStream bis = null;
		try{
			bis = super.getResourceAsStream(resourceName);
		}catch (Exception ex) {}
		if(bis==null){
			try {
				zipFile = new ZipFile(jar);
				ZipEntry zipEntry = zipFile.getEntry(resourceName);
				bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				zipFile.close();
			} catch (Exception ex2) {
				logger.warn("className: " +  resourceName + " Exception: "+ ex2);
			} 
		}
		return bis;
	}
	
    /**
     * Finds the resource with the given name. A resource is some data (images, audio, text, etc) 
     * that can be accessed by class code in a way that is independent of the location of the code.
     * The name of a resource is a '/'-separated path name that identifies the resource. 
     * We overwrite the parent method for the persistence.xml from the datamart.jar file
     * @param The resource name 
     * @return An enumeration of URL objects for the resource. If no resources could be found, 
     * the enumeration will be empty. Resources that the class loader doesn't have access to will not be in the enumeration. 
     */
	public Enumeration<URL> getResources(String descriptorPath)  throws IOException{
		
		if(descriptorPath.equals("META-INF/persistence.xml")){
			//load the persistence.xml from the jar file
			try{
				String s = jar.getAbsolutePath().replace(File.separatorChar, '/');		
				final URL jarUrl = new URL("jar","",-1,"file:/"+s+"!/META-INF/persistence.xml");
				//build the enumeration with only the URL with the location of the persistence.xml
				return new Enumeration<URL>() {
					private int position = 0;
					
					public boolean hasMoreElements() {
						return position>=0;
					}
					
					public URL nextElement() {
						if(position<0)
							throw new NoSuchElementException();
						position --;
						return jarUrl;
					}
				};
			}catch (Exception e) {
				logger.error("Error loading the "+descriptorPath+" from the jar file "+jar.getAbsolutePath(),e);
				logger.error("Use the default loader..");
				return super.getResources(descriptorPath);
			}
		}else{
			return super.getResources(descriptorPath);
		}
	}
	
}
