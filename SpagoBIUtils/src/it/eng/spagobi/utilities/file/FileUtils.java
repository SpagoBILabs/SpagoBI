/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
