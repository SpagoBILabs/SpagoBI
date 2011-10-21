/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.qbe.classloader;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

public class DynamicClassLoader extends URLClassLoader {

	//private ClassLoader parentClassLoader;
	private File jarFile;

	
	private static transient Logger logger = Logger.getLogger(DynamicClassLoader.class);
	
	/**
	 * Instantiates a new dynamic class loader.
	 * 
	 * @param jarFileName the jar file name
	 * @param parentClassLoader the parent class loader
	 */
	public DynamicClassLoader(String jarFileName, ClassLoader parentClassLoader) {		
		this (new File(jarFileName), parentClassLoader);
	}

 

	/**
	 * Instantiates a new dynamic class loader.
	 * 
	 * @param jarFileName the jar file name
	 * @param parentClassLoader the parent class loader
	 */
	public DynamicClassLoader(File jarFile, ClassLoader parentClassLoader) {
		super(new URL[0], parentClassLoader);
		this.jarFile = jarFile;
		//this.parentClassLoader = parentClassLoader;
	}


	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	
	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	public synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

		Class<?> classToLoad;
		
		classToLoad = null;
		try {
			classToLoad = super.loadClass(className, resolve);
		} catch (Exception e) {
			logger.warn("DynamicClassLoader cannot load class [" + className + "]");
		}
		
		if(classToLoad == null) {
			JarFile file = null;
			byte[] buffer = null;
			try {
				file = new JarFile(jarFile);
				JarEntry jarEntry = file.getJarEntry(className.replace('.', '/') + ".class");
				buffer = getJarEntityContent(file, jarEntry);
			} catch (Throwable t) {
				logger.warn("Impossible to load class [" +  className + "]",  t);
			} finally {
				this.closeJarFile(file);
			}

			if (buffer == null) {
				return super.findSystemClass(className);
			}

			try {
				classToLoad = defineClass(className, buffer, 0, buffer.length);
			} catch (ClassFormatError e) {
				logger.error("Error defining class " + className , e);
				throw e;
			}
			if (classToLoad == null) { 
				throw new ClassFormatError();
			}

			if (resolve) {
				resolveClass(classToLoad);
			}

		}
		
		logger.warn("Class [" + className + "] succesfully loaded");
		
		return classToLoad;
	}

	
	
    /**
     * Returns an input stream for reading the specified resource. 
     * We overwrite the parent method for get class from the datamart.jar file
     * @param The resource name 
     * @return An input stream for reading the resource, or null if the resource could not be found
     */
	public synchronized InputStream getResourceAsStream(String resourceName)  {
		
		JarFile file;
		InputStream resultStream;
		
		logger.debug("loading resource [" + resourceName + "]");
		
		resultStream = null;
		try{
			resultStream = super.getResourceAsStream(resourceName);
		} catch (Exception ex) {
			logger.debug("Impossible to load resource [" + resourceName + "] using parent class loader");
		}
		
		if(resultStream == null) {
			
			file = null;
			
			try {
				byte[] buffer = null;
				
				file = new JarFile(jarFile);
				JarEntry jarEntry = file.getJarEntry(resourceName);
				if(jarEntry != null){
					buffer = getJarEntityContent(file, jarEntry);
					resultStream = new ByteArrayInputStream (buffer);
					logger.warn("Resource [" + resourceName + "] loaded from jar file [" + jarFile.getAbsolutePath() + "]");
				} else {
					resultStream = super.getResourceAsStream(resourceName);
					logger.warn("Impossible to load resource [" + resourceName + "] from jar file [" + jarFile.getAbsolutePath() + "]");
				}
				
				
			} catch (Throwable t) {
				resultStream = super.getResourceAsStream(resourceName);
				logger.warn("Impossible to load resource [" + resourceName + "] from jar file [" + jarFile.getAbsolutePath() + "]", t);
			} finally {
				closeJarFile(file);
			}		
		}
		return resultStream;
	}
	
	private byte[] getJarEntityContent(JarFile jarFile, JarEntry jarEntry) {
		byte[] buffer;
		
		buffer = null;
		if(jarEntry != null){
			InputStream jarInputStream = null;
			try {
				buffer = new byte[(int)jarEntry.getSize()];
				jarInputStream = new BufferedInputStream( jarFile.getInputStream(jarEntry) );
				jarInputStream.read(buffer, 0, buffer.length);
			} catch(Throwable t) {
				logger.warn("Impossible to read content from entry [" + jarEntry + "]", t);
			} finally {
				closeInputStream(jarInputStream);
				closeJarFile(jarFile);
			}
		}
		
		return buffer;
	}
	
	private void closeJarFile(JarFile jarFile) {
		if (jarFile != null) {
			try {
				jarFile.close();
			} catch (Throwable t) {
				throw new RuntimeException("Impossible to close file  [" + jarFile + "]");
			}
		}
	}
	
	private void closeInputStream(InputStream inputStram) {
		if (inputStram != null) {
			try {
				inputStram.close();
			} catch (Throwable t) {
				throw new RuntimeException("Impossible to close stream");
			}
		}
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
				String s = jarFile.getAbsolutePath().replace(File.separatorChar, '/');		
//				final URL jarUrl = new URL("jar","",-1,"file:/"+s+"!/META-INF/persistence.xml");  // this works only on Windows!!
				final URL jarUrl = new URL("jar:file:"+s+"!/META-INF/persistence.xml");
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
				logger.error("Error loading the "+descriptorPath+" from the jar file "+jarFile.getAbsolutePath(),e);
				logger.error("Use the default loader..");
				return super.getResources(descriptorPath);
			}
		}else{
			return super.getResources(descriptorPath);
		}
	}

	public File getJarFile() {
		return jarFile;
	}
	
	
	
}

