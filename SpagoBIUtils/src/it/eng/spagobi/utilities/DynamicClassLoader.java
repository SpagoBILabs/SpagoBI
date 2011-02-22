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
package it.eng.spagobi.utilities;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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
			logger.warn("Not found class in super.loadClass(), try to find class in JAR file");
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




}
