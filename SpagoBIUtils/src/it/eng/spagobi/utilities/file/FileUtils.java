/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.utilities.file;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;



/**
 * @author Andrea Gioia
 *
 */
public class FileUtils {
	
	private static transient Logger logger = Logger.getLogger(FileUtils.class);
	
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
	

	public static void checkUploadedFile(FileItem uploaded,Integer maxSize, List extFiles ) throws Exception {
		// check if the uploaded file is empty
		if (uploaded.getSize() == 0) {
			logger.error("The uploaded file is empty");
			throw new SpagoBIServiceException("", "The uploaded file is empty");	
		}
		// check if the uploaded file exceeds the maximum dimension
		if (maxSize != null && uploaded.getSize() > maxSize) {
			logger.error("The uploaded file exceeds the maximum size, that is " + maxSize + " bytes");
			throw new SpagoBIServiceException("", "The uploaded file exceeds the maximum size, that is " + maxSize + " bytes");
		}
					
		if (extFiles != null){
			// check if the extension is valid
			String fileExtension =  uploaded.getName().lastIndexOf('.') > 0 ?  
					uploaded.getName().substring( uploaded.getName().lastIndexOf('.') + 1) : null;	
			
			if (!extFiles.contains(fileExtension.toLowerCase()) && !extFiles.contains(fileExtension.toUpperCase())){
				logger.error( "The uploaded file has an invalid extension. Choose a "+ extFiles.toString() +" file.");
				String msg = "The uploaded file has an invalid extension. Choose a "+ extFiles.toString() +" file.";
				throw new SpagoBIServiceException("", msg);
			} 
		}		
		return;
	}

	
	public static File checkAndCreateDir(FileItem uploaded,String directory) {
		File toReturn = null;
		try {
			String fileName = SpagoBIUtilities.getRelativeFileNames(uploaded.getName());		
			SingletonConfig configSingleton = SingletonConfig.getInstance();
			String path  = configSingleton.getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
			String resourcePath= SpagoBIUtilities.readJndiResource(path);
			if (directory != null){
				resourcePath += (!directory.startsWith("/") && !directory.startsWith("\\"))?File.separatorChar+directory:directory;
			}else{
				resourcePath += File.separatorChar+"files"; //default: is correct?
			}
			File fileDir = new File(resourcePath);
			if (!fileDir.exists()){
				//Create Directory if doesn't exist
				boolean mkdirResult = fileDir.mkdirs();
				if (!mkdirResult) {
					logger.error( "Cannot create files directory into server resources");
					throw new SpagoBIServiceException("", "Cannot create files directory into server resources");
				}
			}
			
			// uuid generation
			String uuid = createNewExecutionId();
			String fileExtension =  uploaded.getName().lastIndexOf('.') > 0 ?  
					uploaded.getName().substring( uploaded.getName().lastIndexOf('.')) : null;			
			fileName  =  uuid + fileExtension;
			toReturn =  new File(fileDir, fileName);
			
		} catch (Throwable t) {
			logger.error("Error while saving file into server", t);
			throw new SpagoBIRuntimeException( "Error while saving file into server");
		}
		return toReturn;
	}
	
	public static void saveFile(FileItem uploaded, File saveTo) {
		try {
			uploaded.write(saveTo);
		} catch (Throwable t) {
			logger.error("Error while saving file into server: " + t);
			throw new SpagoBIServiceException("", "Error while saving file into server");
		}
	}
	
	public static boolean checkFileIfExist(File file){
		return (file.exists());
	}
	
	public static String createNewExecutionId() {
		String executionId;
		
		executionId = null;
		try {
			UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
			UUID uuidObj = uuidGen.generateTimeBasedUUID();
			executionId = uuidObj.toString();
			executionId = executionId.replaceAll("-", "");
		} catch(Throwable t) {
			
		} 
		
		return executionId;
	}
	
}
