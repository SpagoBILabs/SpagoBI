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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceClassLoader extends ClassLoader {

	private ClassLoader parentCL = null;
	private String resourcePath=null;

	/**
	 * Instantiates a new dynamic class loader.
	 * 
	 * @param jarFileName the jar file name
	 * @param cl the cl
	 */

	public ResourceClassLoader(String path,ClassLoader cl) {		
		super(cl);
		resourcePath=path;
	}




	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}



	
	@Override
	public InputStream getResourceAsStream(String name) {
		String resourceFile = resourcePath + name;		
		File file=new File(resourceFile);
		FileInputStream fis=null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		return fis;
	}






}
