/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.dao;

import it.eng.qbe.bo.ViewJarFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ViewJarFileDAOFilesystemImpl.
 * 
 * @author Andrea Gioia
 */
public class ViewJarFileDAOFilesystemImpl implements IViewJarFileDAO {

	/** The views dir. */
	private File viewsDir;
	
	/**
	 * Instantiates a new view jar file dao filesystem impl.
	 * 
	 * @param viewsDir the views dir
	 */
	public ViewJarFileDAOFilesystemImpl(File viewsDir) {
		setViewsDir(viewsDir);
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.dao.ViewJarFileDAO#loadViewJarFile(java.lang.String, java.lang.String)
	 */
	public ViewJarFile loadViewJarFile(String datamartName, String viewName) {
		ViewJarFile jarFile = null;
		File targetViewDir = null;
		File viewJarFile = null;

		
		targetViewDir = new File(getViewsDir(), datamartName);
		viewJarFile = new File(targetViewDir, viewName + "View.jar");
	    
		if(viewJarFile.exists()) {
			jarFile = new ViewJarFile(viewJarFile);
		}
		
		return jarFile;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.dao.ViewJarFileDAO#loadViewJarFiles(java.lang.String)
	 */
	public List loadViewJarFiles(String datamartName) {
		List viewJarFiles = new ArrayList();
		List viewNames = loadViewNames(datamartName);
		
		if(viewNames.size() > 0) {
			for(int i = 0; i < viewNames.size(); i++) {
				String viewName = (String)viewNames.get(i);
				ViewJarFile viewJarFile = loadViewJarFile(datamartName, viewName);
				if(viewJarFile != null) {
					viewJarFiles.add(viewJarFile);
				} else {
					// if happens it's a BUG :-(
				}
			}
		}
		
		return viewJarFiles;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.dao.ViewJarFileDAO#loadViewNames(java.lang.String)
	 */
	public List loadViewNames(String datamartName) {
		List viewNames = new ArrayList();
		File targetViewsDir = null;
		
		targetViewsDir = new File(getViewsDir(), datamartName);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar") && !name.equalsIgnoreCase("datamart.jar");
			}
		};
	    
        String[] children = targetViewsDir.list(filter);
        if (children == null) {
              // Either dir does not exist or is not a directory
        } else {
            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                String viewName = filename.substring(0, filename.indexOf("View.jar"));
                viewNames.add(viewName);
            }
        }
          
        return viewNames;
	}	
	
	

	/**
	 * Gets the views dir.
	 * 
	 * @return the views dir
	 */
	private File getViewsDir() {
		return viewsDir;
	}

	/**
	 * Sets the views dir.
	 * 
	 * @param viewsDir the new views dir
	 */
	private void setViewsDir(File viewsDir) {
		this.viewsDir = viewsDir;
	}
}
