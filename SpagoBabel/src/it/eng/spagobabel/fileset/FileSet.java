/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobabel.fileset;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class FileSet {
	
	private File dir;
	private File file;
	
	private boolean defaultexcludes;
	private String excludes;
	
	private Set defaultExludeSet;
	private Set exludeSet;
	
	
	public FileSet(File f) {
		if(f.isDirectory()) {
			dir = f;
		} else {
			file = f;
		}
		
		exludeSet = new LinkedHashSet();
		defaultExludeSet = new LinkedHashSet();
		
		defaultexcludes = true;		
		defaultExludeSet.add(".svn");
	}
	
	public boolean isSingleFileFleSet(){
		return file != null;
	}
	
	public boolean excludeFile(File f) {
		List<String> checkList = new ArrayList();
		
		if(defaultexcludes) {
			checkList.addAll(defaultExludeSet);
		}		
		checkList.addAll(exludeSet);
		
		for(int i = 0; i < checkList.size(); i++) {
			String check = checkList.get(i);
			if( f.toString().contains(check) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public Set<File> getFiles() {
		Set fileSet;
		List dirStack;
		
		fileSet = new LinkedHashSet();
		
		if(isSingleFileFleSet()) {
			fileSet.add(file);
		} else {
			dirStack = new ArrayList();
			dirStack.add(dir);
			
			while(dirStack.size() > 0) {
				File targetDir = (File)dirStack.remove(0);
				System.out.println("Scanning dir [" + targetDir + "]");
				File[] dirContents = targetDir.listFiles();
				for(int i = 0; i < dirContents.length; i++) {
					
					if(excludeFile(dirContents[i]) == true) continue;
					
					if(dirContents[i].isFile()) {
						fileSet.add(dirContents[i]);
					} else {
						dirStack.add(dirContents[i]);
					}
					
				}
			}
		}
				
		return fileSet;
	}
}
