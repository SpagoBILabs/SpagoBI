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
package it.eng.spagobi.utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceClassLoader extends ClassLoader {

	protected  ClassLoader parentClassLoader;
	protected  File resourceRootFolder;

	public ResourceClassLoader(String resourceRootFolderPath,ClassLoader parentClassLoader) {		
		this(new File(resourceRootFolderPath), parentClassLoader);
	}
	
	public ResourceClassLoader(File resourceRootFolder, ClassLoader parentClassLoader) {	
		super(parentClassLoader);
		this.resourceRootFolder = resourceRootFolder;
		
		if(!this.resourceRootFolder.exists()) throw new RuntimeException("Root folder [" + this.resourceRootFolder + "] does not exist");
		if(!this.resourceRootFolder.isDirectory()) throw new RuntimeException("Root folder [" + this.resourceRootFolder + "] is a file not a folder");
	}

	
	@Override
	public InputStream getResourceAsStream(String resourceFileName) {
		File resourceFile = new File(resourceRootFolder, resourceFileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(resourceFile);
		} catch (FileNotFoundException e) {
			return null;
		}
		return fis;
	}






}
