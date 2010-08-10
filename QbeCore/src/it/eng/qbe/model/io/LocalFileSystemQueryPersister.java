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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.eng.qbe.conf.QbeCoreSettings;
import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.QueryMeta;
import it.eng.qbe.utility.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalFileSystemQueryPersister.
 * 
 * @author Andrea Zoppello
 * 
 * An implementation of IQueryPersister that retrieve and persist
 * queries using the File System
 */
public class LocalFileSystemQueryPersister implements IQueryPersister {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(LocalFileSystemQueryPersister.class);
	
	

	
	/**
	 * Persist to file.
	 * 
	 * @param dm the dm
	 * @param wizObject the wiz object
	 * @param fileName the file name
	 */
	protected void persistToFile(DataMartModel dm, Query query, String fileName) {

		try {
			File f = new File(fileName);
			if (!f.exists()){
				f.createNewFile();
			}
		    XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
		        new FileOutputStream(fileName)));
		    encoder.writeObject(query);
		    encoder.close();
		} catch (IOException e) {
			logger.error(LocalFileSystemQueryPersister.class, e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.model.io.IQueryPersister#persist(it.eng.qbe.model.DataMartModel, it.eng.qbe.wizard.ISingleDataMartWizardObject)
	 */
	public void persist(DataMartModel dm, Query query, QueryMeta meta) {		
		persist((File)null, dm, query, meta);
	}
	
	/**
	 * Persist.
	 * 
	 * @param baseDir the base dir
	 * @param dm the dm
	 * @param wizObject the wiz object
	 */
	public void persist(File baseDir, DataMartModel dm, Query query, QueryMeta meta) {
		
		String qbeDataMartDir = FileUtils.getQbeDataMartDir(baseDir);
		
		String key = "q-" + query.hashCode();
		String fileName = null;
		
		String publicDmDir = qbeDataMartDir +System.getProperty("file.separator")+  dm.getName();
		String privateDmDir = publicDmDir + System.getProperty("file.separator") + meta.getOwner();
		if ("PRIVATE".equalsIgnoreCase( meta.getScope() )){
			File f = new File(privateDmDir);
			if (!f.exists()){
				f.mkdirs();
			}
			fileName =  privateDmDir + System.getProperty("file.separator") + key+ ".qbe";
		}else{
			File f = new File(publicDmDir);
			if (!f.exists()){
				f.mkdirs();
			}
			fileName = qbeDataMartDir +System.getProperty("file.separator")+  dm.getName() + System.getProperty("file.separator") + key+ ".qbe";
		}
		
		
		persistToFile(dm, query, fileName);
	}
	


    
    /**
	 * Load from file.
	 * 
	 * @param f the f
	 * 
	 * @return the i single data mart wizard object
	 */
	protected Query loadFromFile(File f) {
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
                new FileInputStream(f)));
        
            
            Query o = (Query)decoder.readObject();
            decoder.close();
            return o;
        } catch (FileNotFoundException e) {
    		logger.error("impossible to load file from xml", e);
    		return null;
        }
    }
    
    /**
     * Load first level query.
     * 
     * @param directory the directory
     * 
     * @return the list
     */
    private List loadFirstLevelQuery(String directory){
    	
    	File dir = new File(directory);
//   	 It is also possible to filter the list of returned files.
       // This example does not return any files that start with `.'.
       FilenameFilter filter = new FilenameFilter() {
           public boolean accept(File dir, String name) {
               return name.endsWith("qbe");
           }
       };
       
       
   	
   	Query query = null;
   	List queries = new ArrayList();
   	File f = null;
       boolean isDir = dir.isDirectory();
       if (isDir) {
       	String[] children = dir.list(filter);
           if (children == null) {
               // Either dir does not exist or is not a directory
           } else {
               for (int i=0; i<children.length; i++) {
                   // Get filename of file or directory
                   String filename = children[i];
                   f = new File(dir, filename);
                   if (!f.isDirectory()){
                   	   query = loadFromFile(f);
                       queries.add(query);
                   }
                   
               }
           }
       }
       
       return queries;
    }
    
    /* (non-Javadoc)
     * @see it.eng.qbe.model.io.IQueryPersister#loadAllQueries(it.eng.qbe.model.DataMartModel)
     */
    public List loadAllQueries(DataMartModel dm) {
    	String dmName = dm.getName();
    	File qbeDataMartDir = QbeCoreSettings.getInstance().getQbeDataMartDir();
    	File publicTargetDir = new File(qbeDataMartDir, dmName);
    	return loadFirstLevelQuery(publicTargetDir.getAbsolutePath());
    }
    
    /**
     * Gets the private queries for.
     * 
     * @param dm the dm
     * @param userID the user id
     * 
     * @return the private queries for
     */
    public List getPrivateQueriesFor(DataMartModel dm, String userID) {
    	String dmName = dm.getName();
    	File qbeDataMartDir = QbeCoreSettings.getInstance().getQbeDataMartDir();
    	File publicTargetDir = new File(qbeDataMartDir, dmName);
    	File privateTargetDir = new File(publicTargetDir, userID);
    
    	return loadFirstLevelQuery(privateTargetDir.getAbsolutePath());
    }

	public Query load(DataMartModel dm, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

	

}
