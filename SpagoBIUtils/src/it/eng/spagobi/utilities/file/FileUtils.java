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
package it.eng.spagobi.utilities.file;

import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.io.IOException;

/**
 * @author Andrea Gioia
 *
 */
public class FileUtils {
	
	public static boolean isAbsolutePath(String path) {
		if(path == null) return false;
		return (path.startsWith("/") || path.startsWith("\\") || path.charAt(1) == ':');
	}
	
	 /**
	  * Utility method that gets the extension of a file from its name if it has one
	  */ 
	public static String getFileExtension(File file) {
    	try {
    		return getFileExtension(file.getCanonicalPath());
    	}catch(IOException e) {
    		return "";
    	}
    }
	
	/**
	  * Utility method that gets the extension of a file from its name if it has one
	  */ 
	public static String getFileExtension(String fileName) {
		if (fileName == null || fileName.lastIndexOf(".") < 0) {
			return "";
		}
		
		// Could be that the file name actually end with a '.' so lets check
		if(fileName.lastIndexOf(".") + 1 == fileName.length()) {
			return "";
		} 
		
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		// Could be that the path actually had a '.' in it so lets check
		if(extension.contains(File.separator)) {
			extension = "";
		}
		
		return extension;
	}
	
	public static void doForEach(File rootDir, IFileTransformer transformer) {
		Assert.assertNotNull(rootDir, "rootDir parameters cannot be null");
		Assert.assertTrue(rootDir.exists() && rootDir.isDirectory(), "rootDir parameter [" + rootDir + "] is not an existing directory");
		Assert.assertNotNull(transformer, "transformer parameters cannot be null");
		
		File[] files = rootDir.listFiles() ;
		for(int i = 0; i < files.length; i ++) {
			File file = files[i];
			if(file.isDirectory()) {
				doForEach(file, transformer);
			} else {
				transformer.transform(file);
			}
		}
	}
	
	
}
