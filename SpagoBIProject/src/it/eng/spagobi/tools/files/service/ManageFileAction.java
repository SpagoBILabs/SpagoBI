/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.files.service;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBIServiceExceptionHandler;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.file.FileUtils;
import it.eng.spagobi.utilities.json.JSONUtils;
import it.eng.spagobi.utilities.service.IServiceResponse;
import it.eng.spagobi.utilities.service.JSONResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

public class ManageFileAction extends AbstractSpagoBIAction{ //AbstractHttpAction {

	static private Logger logger = Logger.getLogger(ManageFileAction.class);

	private static final String UPLOADED_FILE= "UPLOADED_FILE";
	private static final String OPER_UPLOAD= "UPLOAD";
	private static final String OPER_DOWNLOAD= "DOWNLOAD";
	
	private static final String OPERATION= "operation";
	private static final String DIRECTORY= "directory";
	private static final String MAX_SIZE= "maxSize";
	private static final String EXT_FILES= "extFiles";
	
	private String fileName = null;
	private String directory = null;
	private String maxSize = null;
	private List extFiles = null;

	public String getActionName(){return SERVICE_NAME;}


	public void doService()  {
		logger.debug("IN");
    	
    	try {
			
			String operation = (String)  getAttribute(OPERATION);
			if (operation == null) {
				throw new SpagoBIServiceException(getActionName(), "No operation [UPLOAD, DOWNLAOD] is defined. ");
			}
			logger.info("Manage operation: " + operation);
			//get request informations
			directory = (("").equals(getAttribute(DIRECTORY)))?null:(String)getAttribute(DIRECTORY);
			maxSize = (("").equals(getAttribute(MAX_SIZE)))?null:(String)getAttribute(MAX_SIZE);
			String strExtFiles = (String)getAttribute(EXT_FILES);
			if (strExtFiles==null)
				extFiles = null;
			else
				extFiles = JSONUtils.asList((JSONUtils.toJSONArray(strExtFiles)));
			
			JSONObject jsonToReturn = new JSONObject();
			if (OPER_UPLOAD.equalsIgnoreCase(operation)){
				jsonToReturn = uploadFile();
				replayToClient( null, jsonToReturn );
			}else if (OPER_DOWNLOAD.equalsIgnoreCase(operation)){				
				freezeHttpResponse();
				String fileName = (String)getAttribute("fileName");
				HttpServletResponse response = getHttpResponse();	
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
				FileInputStream fis = downloadFile(fileName);
				if (fis != null){
					byte[] content = GeneralUtilities.getByteArrayFromInputStream(fis);
					response.setContentLength(content.length);
					response.getOutputStream().write(content);
					response.getOutputStream().flush();
					if (fis != null)
						fis.close();
				}
			}else{
				throw new SpagoBIServiceException(getActionName(), "No valid operation [UPLOAD, DOWNLAOD] is required. ");
			}
			
			
    	} catch (Throwable t) {
    		logger.error("Error while uploading file", t);
    		SpagoBIServiceException e = SpagoBIServiceExceptionHandler.getInstance().getWrappedException(this.getActionName(), t);
    		replayToClient( e, null );
		} finally {
			logger.debug("OUT");
		}	
	}
	
	
	private JSONObject uploadFile() throws Exception{		
		FileItem uploaded = (FileItem) getAttribute(UPLOADED_FILE);
		
		if (uploaded == null) {
			throw new SpagoBIServiceException(getActionName(), "No file was uploaded");
		}
					
		UserProfile userProfile = (UserProfile) this.getUserProfile();
		logger.info("User [id : " + userProfile.getUserId() + ", name : " + userProfile.getUserName() + "] " +
				"is uploading file [" + uploaded.getName() + "] with size [" + uploaded.getSize() + "]");
		
		FileUtils.checkUploadedFile(uploaded, Integer.valueOf(maxSize), extFiles);
		
		
		File file = FileUtils.checkAndCreateDir(uploaded, directory);

		logger.debug("Saving file...");
		FileUtils.saveFile(uploaded, file);
		logger.debug("File saved");
		
		JSONObject toReturn = new JSONObject();
		try {				
			toReturn.put("success", true);
			toReturn.put("file","null");
			toReturn.put("fileName", file.getName());
		} catch (JSONException jSONException) {
			logger.error(jSONException);
		}
	
		return toReturn;
		
	}
	
	private FileInputStream downloadFile(String fileName){
		FileInputStream toReturn = null;		
		try{
					
			SingletonConfig configSingleton = SingletonConfig.getInstance();
			String path  = configSingleton.getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
			String resourcePath= SpagoBIUtilities.readJndiResource(path);
			if (directory != null){
				resourcePath += (!directory.startsWith("/") && !directory.startsWith("\\"))?File.separatorChar+directory:directory;
			}else{
				resourcePath += File.separatorChar+"files"; //default: is correct?
			}
			File file = new File(resourcePath + File.separatorChar+ fileName);
			if (!file.exists()){					
				throw new SpagoBIServiceException(getActionName(), "Cannot found files or directory into server resources");
			}
			toReturn = new FileInputStream(file);
			
			
		} catch (Exception e) {
    		logger.error("Error while uploading file", e);
    		SpagoBIServiceException e2 = SpagoBIServiceExceptionHandler.getInstance().getWrappedException(this.getActionName(), e);
    		replayToClient( e2, null);
		} 
		return toReturn;
	}

	 /*
     * see Ext.form.BasicForm for file upload
     */
	private void replayToClient(final SpagoBIServiceException e, final JSONObject jr) {
		
		try {
			
			writeBackToClient(  new IServiceResponse() {
				
				public boolean isInline() {
					return false;
				}
				
				public int getStatusCode() {
					if ( e != null) {
						return JSONResponse.FAILURE;
					}
					return JSONResponse.SUCCESS;
				}
				
				public String getFileName() {
					return null;
				}
				
				public String getContentType() {
					return "text/html";
				}
				
				public String getContent() throws IOException {
					if ( e != null) {
						try {
							JSONObject toReturn = new JSONObject();
							toReturn.put("success", false);
							toReturn.put("msg", e.getMessage());
							return toReturn.toString();
						} catch (JSONException jSONException) {
							logger.error(jSONException);
						}
					}
					if ( jr != null) {						
						return jr.toString();
					}
					return "{success:true, file:null}";
				}
				
			});
			
		} catch (IOException ioException) {
			logger.error("Impossible to write back the responce to the client", ioException);
		}
	}

/*	private void checkUploadedFile(FileItem uploaded) {
	
		logger.debug("IN");
		try {

			// check if the uploaded file is empty
			if (uploaded.getSize() == 0) {
				throw new SpagoBIServiceException(getActionName(), "The uploaded file is empty");
			}
			// check if the uploaded file exceeds the maximum dimension
			Integer maxSizeN = (maxSize!=null)?Integer.valueOf(maxSize):null;
			if (maxSizeN != null && uploaded.getSize() > maxSizeN) {
				throw new SpagoBIServiceException(getActionName(), "The uploaded file exceeds the maximum size, that is " + maxSize + " bytes");
			}
						
			if (extFiles != null){
				// check if the extension is valid
				String fileExtension =  uploaded.getName().lastIndexOf('.') > 0 ?  
						uploaded.getName().substring( uploaded.getName().lastIndexOf('.') + 1) : null;
				logger.debug("File extension: [" + fileExtension +"]");			
				
				if (!extFiles.contains(fileExtension.toLowerCase()) && !extFiles.contains(fileExtension.toUpperCase())){
					String msg = "The uploaded file has an invalid extension. Choose a "+ extFiles.toString() +" file.";
					throw new SpagoBIServiceException(getActionName(), msg);
				} 
			}
		} finally {
			logger.debug("OUT");
		}
	}

	
	
	
	private File checkAndCreateDir(FileItem uploaded) {
		logger.debug("IN");
		try {
			fileName = SpagoBIUtilities.getRelativeFileNames(uploaded.getName());		
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
					throw new SpagoBIServiceException(getActionName(), "Cannot create files directory into server resources");
				}
			}
			
			// uuid generation
			UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();
			UUID uuidObj = uuidGenerator.generateTimeBasedUUID();
			String uuid = uuidObj.toString();
			String fileExtension =  uploaded.getName().lastIndexOf('.') > 0 ?  
					uploaded.getName().substring( uploaded.getName().lastIndexOf('.')) : null;
//			fileName  = uploaded.getName().substring(0,uploaded.getName().lastIndexOf('.')) + uuid + fileExtension;			
			fileName  =  uuid + fileExtension;

			return new File(fileDir, fileName);
		} catch (Throwable t) {
			logger.error("Error while saving file into server: " + t);
			throw new SpagoBIServiceException(getActionName(),
					"Error while saving file into server", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private void saveFile(FileItem uploaded, File saveTo) {
		logger.debug("IN");
		try {
			uploaded.write(saveTo);
		} catch (Throwable t) {
			logger.error("Error while saving file into server: " + t);
			throw new SpagoBIServiceException(getActionName(),
					"Error while saving file into server", t);
		} finally {
			logger.debug("OUT");
		}
	}
	*/
	private boolean checkFileIfExist(File file){
		return (file.exists());
	}
	
	
	
}
