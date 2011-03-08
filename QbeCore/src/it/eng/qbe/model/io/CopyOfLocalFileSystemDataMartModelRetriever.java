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
package it.eng.qbe.model.io;

import it.eng.qbe.utility.FileUtils;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class CopyOfLocalFileSystemDataMartModelRetriever implements IDataMartModelRetriever {

	private File datamartsDir;
	
	private static final String DATAMART_JAR_FILE_NAME = "datamart.jar";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(CopyOfLocalFileSystemDataMartModelRetriever.class);
	
	
	
	
	public CopyOfLocalFileSystemDataMartModelRetriever() {
		setContextDir(null);
	}
	
	public CopyOfLocalFileSystemDataMartModelRetriever(File contextDir) {
		setContextDir(contextDir);
	}
	
	public File getContextDir() {
		return datamartsDir;
	}

	public void setContextDir(File contextDir) {
		this.datamartsDir = contextDir;
	}
	
	
	public File getDatamartJarFile(String datamartName) {
		
		File targetDatamartDir;
		File datamartJarFile;
		
		logger.debug("IN");
		
		datamartJarFile = null;
		
		try {
			logger.debug("searching for datamart [" + datamartName + "] jar file ...");
			Assert.assertTrue(!StringUtilities.isEmpty(datamartName), "Input parameter [datamartName] cannot be null or empty");
			
			logger.debug("Resources directory is [" + getContextDir() + "]");
			
			targetDatamartDir = new File( getContextDir() , datamartName );
			logger.debug("Target datamart dir is equals to [" + targetDatamartDir.getAbsoluteFile()+ "]");
			//Assert.assertTrue(targetDatamartDir.exists(), "Target datamart dir [" + targetDatamartDir.getAbsoluteFile()+ "] does not exist");
			
			datamartJarFile = new File( targetDatamartDir, DATAMART_JAR_FILE_NAME );
			logger.debug("target datamart file is equals to [" + datamartJarFile.getCanonicalFile()+ "]");
			//Assert.assertTrue(datamartJarFile.exists(), "Target datamart file [" + datamartJarFile.getAbsoluteFile()+ "] does not exist");
						
			if (!datamartJarFile.exists()) {
				logger.warn("[" + datamartName + "] datamart jar file [" + datamartJarFile + "] does not exist");
				datamartJarFile = null;				
			}else {
				logger.debug("Datamart [" + datamartName + "] jar file succesfully found in [" + datamartJarFile.getAbsolutePath() + "]");
			}
			
			
		} catch (Throwable t) {
			logger.error("Ok, we are in truble", t);
		} finally {
			logger.debug("OUT");
		}
		return datamartJarFile;
	}
		
	
	
	
	
	public List getViewJarFiles(String datamartName) {
		List viewJarFiles = new ArrayList();
		List viewNames = getViewNames(datamartName);
		
		if(viewNames.size() > 0) {
			for(int i = 0; i < viewNames.size(); i++) {
				String viewName = (String)viewNames.get(i);
				File viewJarFile = getViewJarFile(datamartName, viewName);
				if(viewJarFile != null) {
					viewJarFiles.add(viewJarFile);
				} else {
					// if happens it's a BUG :-(
				}
			}
		}
		
		return viewJarFiles;
	}
	
	public File getViewJarFile(String datamartName, String viewName) {

		File targetDatamartDir = null;
		File viewJarFile = null;

		
		targetDatamartDir = new File(datamartsDir, datamartName);
		viewJarFile = new File(targetDatamartDir, viewName + "View.jar");
	    
		if(!viewJarFile.exists()) viewJarFile = null;
		
		return viewJarFile;
	}

	public List getViewNames(String datamartName) {
		List viewNames = new ArrayList();

		
		String directory = datamartsDir.getAbsolutePath() + System.getProperty("file.separator") + datamartName + System.getProperty("file.separator");
		File dir = new File(directory);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar") && !name.equalsIgnoreCase("datamart.jar");
			}
		};
	    
        String[] children = dir.list(filter);
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
	
	public static List getAllDataMartPath(File contextDir) {
		String qbeDataMartDir = FileUtils.getQbeDataMartDir(contextDir);
		
		
		File f = new File(qbeDataMartDir);
		
		List dataMartPaths = new ArrayList();

		String[] childrens = f.list();
		if(childrens != null) {
			for(int i = 0; i < childrens.length; i++) {
				File children = new File(f, childrens[i]);
				if(children.exists() && children.isDirectory())
					dataMartPaths.add(childrens[i]);
			}
		}
		
		return dataMartPaths;
	}
}
