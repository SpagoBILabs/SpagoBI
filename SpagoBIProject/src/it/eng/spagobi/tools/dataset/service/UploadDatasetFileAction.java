/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.service;

import it.eng.spagobi.commons.SingletonConfig;

import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;


import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import it.eng.spagobi.utilities.service.IServiceResponse;
import it.eng.spagobi.utilities.service.JSONResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 * 
 * @author Marco Cortella 
 */
public class UploadDatasetFileAction extends AbstractSpagoBIAction {
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(UploadDatasetFileAction.class);
    
		
    public void doService() {
    	
    	logger.debug("IN");
    	
		String logOperation = null;
		HashMap logParameters = new HashMap<String, String>();
		
       
    	try {
			
			FileItem uploaded = (FileItem) getAttribute("UPLOADED_FILE");
			if (uploaded == null) {
				throw new SpagoBIEngineServiceException(getActionName(), "No file was uploaded");
			}
				
			UserProfile userProfile = (UserProfile) this.getUserProfile();
			logger.info("User [id : " + userProfile.getUserId() + ", name : " + userProfile.getUserName() + "] " +
					"is uploading file [" + uploaded.getName() + "] with size [" + uploaded.getSize() + "]");
			
			checkUploadedFile(uploaded);
			
			logger.debug("Saving file...");
			saveFile(uploaded);
			logger.debug("File saved");
			
			replayToClient( null );
			
		} catch (Throwable t) {
			logger.error("Error while saving file into server: "+t);
			t.printStackTrace();
			AuditLogUtilities.updateAudit(getHttpRequest(), this.getUserProfile(), logOperation, logParameters , "KO");
			SpagoBIServiceException e = new SpagoBIServiceException(this.getActionName(), "Error while saving file data set", t);
			replayToClient( e );
		} finally {
			logger.debug("OUT");
		}	

	}

    /*
     * see Ext.form.BasicForm for file upload
     */
	private void replayToClient(final SpagoBIServiceException e) {
		
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
					return "{success:true, file:null}";
				}
				
			});
			
		} catch (IOException ioException) {
			logger.error("Impossible to write back the responce to the client", ioException);
		}
	}

	private void checkUploadedFile(FileItem uploaded) {
	
		logger.debug("IN");
		try {

			// check if the uploaded file is empty
			if (uploaded.getSize() == 0) {
				throw new SpagoBIEngineServiceException(getActionName(), "The uploaded file is empty");
			}
			// check if the uploaded file exceeds the maximum dimension
			int maxSize = GeneralUtilities.MAX_DEFAULT_FILE_DATASET_SIZE;
			if (uploaded.getSize() > maxSize) {
				throw new SpagoBIEngineServiceException(getActionName(), "The uploaded file exceeds the maximum size, that is " + maxSize);
			}
		} finally {
			logger.debug("OUT");
		}
	}

	private void saveFile(FileItem uploaded) {
		logger.debug("IN");
		try {
			String fileName = SpagoBIUtilities.getRelativeFileNames(uploaded.getName());
			SingletonConfig configSingleton = SingletonConfig.getInstance();
			String path  = configSingleton.getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
			String resourcePath= SpagoBIUtilities.readJndiResource(path);
			File datasetFileDir = new File(resourcePath+File.separatorChar+"dataset"+File.separatorChar+"files");
			if (!datasetFileDir.exists()){
				//Create Directory \dataset\files under \resources if don't exists
				boolean mkdirResult = datasetFileDir.mkdirs();
				if (!mkdirResult) {
					throw new SpagoBIServiceException(getActionName(), "Cannot create \\dataset\\files directory into server resources");
				}
			}

			File saveTo = new File(datasetFileDir, fileName);
			// check if the file already exists
			if (saveTo.exists()){
				//Overwriting existing file
				logger.debug("Overwriting existing file "+fileName);
			}
			
			uploaded.write(saveTo);
		} catch (Throwable t) {
			logger.error("Error while saving file into server: "+t);
			t.printStackTrace();
			throw new SpagoBIServiceException(getActionName(), "Error while saving file into server", t);
		} finally {
			logger.debug("OUT");
		}
	}
	




}
