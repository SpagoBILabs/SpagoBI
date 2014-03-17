/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBIServiceExceptionHandler;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.common.datareader.FileDatasetCsvDataReader;
import it.eng.spagobi.tools.dataset.common.datareader.FileDatasetXlsDataReader;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.IServiceResponse;
import it.eng.spagobi.utilities.service.JSONResponse;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
    
    private static final String UPLOADED_FILE= "UPLOADED_FILE";
    private static final String SKIP_CHECKS= "SKIP_CHECKS";
    
    String fileExtension = "";
		
    public void doService() {
    	
    	logger.debug("IN");
    	
    	try {
			
			FileItem uploaded = (FileItem) getAttribute(UPLOADED_FILE);
			Object skipChecksObject = getAttribute(SKIP_CHECKS);
			Boolean skipChecks = false; 
			if(skipChecksObject!=null){
				skipChecks =((String)skipChecksObject).equals("on");
			}
			
			if (uploaded == null) {
				throw new SpagoBIServiceException(getActionName(), "No file was uploaded");
			}
				
			UserProfile userProfile = (UserProfile) this.getUserProfile();
			logger.info("User [id : " + userProfile.getUserId() + ", name : " + userProfile.getUserName() + "] " +
					"is uploading file [" + uploaded.getName() + "] with size [" + uploaded.getSize() + "]");
			
			checkUploadedFile(uploaded);
			
			
			File file = checkAndCreateDir(uploaded);
			
			/*
			if(!skipChecks){
				checkFile(uploaded, file);
			}
			*/
			
			logger.debug("Saving file...");
			saveFile(uploaded, file);
			logger.debug("File saved");
			
			replayToClient( null );
			
    	} catch (Throwable t) {
    		logger.error("Error while uploading dataset file", t);
    		SpagoBIServiceException e = SpagoBIServiceExceptionHandler.getInstance().getWrappedException(this.getActionName(), t);
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
					JSONObject toReturn = new JSONObject();
					if ( e != null) {
						try {							
							toReturn.put("success", false);							
							toReturn.put("msg", e.getMessage());
							return toReturn.toString();
						} catch (JSONException jSONException) {
							logger.error(jSONException);
						}
					}
					toReturn = new JSONObject();
					try {				
						toReturn.put("success", true);
						toReturn.put("fileExtension",fileExtension);
						toReturn.put("file","null");
					} catch (JSONException jSONException) {
						logger.error(jSONException);
					}					
//					return "{success:true, file:null}";
					return toReturn.toString();
					
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
				throw new SpagoBIServiceException(getActionName(), "The uploaded file is empty");
			}
			// check if the uploaded file exceeds the maximum dimension
			int maxSize = GeneralUtilities.getDataSetFileMaxSize();
			if (uploaded.getSize() > maxSize) {
				throw new SpagoBIServiceException(getActionName(), "The uploaded file exceeds the maximum size, that is " + maxSize + " bytes");
			}
			// check if the extension is valid (XLS, CSV)
			fileExtension =  uploaded.getName().lastIndexOf('.') > 0 ?  
					uploaded.getName().substring( uploaded.getName().lastIndexOf('.') + 1) : null;
			logger.debug("File extension: [" + fileExtension +"]");			
			if(!"CSV".equalsIgnoreCase( fileExtension )&& !"XLS".equalsIgnoreCase( fileExtension )) {
				throw new SpagoBIServiceException(getActionName(), "The uploaded file has an invalid extension. Choose a CSV or XLS file.");
			} 
				
		} finally {
			logger.debug("OUT");
		}
	}

	
	
	
	private File checkAndCreateDir(FileItem uploaded) {
		logger.debug("IN");
		try {
			String fileName = SpagoBIUtilities.getRelativeFileNames(uploaded.getName());
			SingletonConfig configSingleton = SingletonConfig.getInstance();
			String path  = configSingleton.getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
			String resourcePath= SpagoBIUtilities.readJndiResource(path);
			File datasetFileDir = new File(resourcePath+File.separatorChar+"dataset"+File.separatorChar+"files"+File.separatorChar+"temp");
			if (!datasetFileDir.exists()){
				//Create Directory \dataset\files\temp under \resources if don't exists
				boolean mkdirResult = datasetFileDir.mkdirs();
				if (!mkdirResult) {
					throw new SpagoBIServiceException(getActionName(), "Cannot create \\dataset\\files directory into server resources");
				}
			}

			return new File(datasetFileDir, fileName);
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
	
	private void checkFile(FileItem uploaded, File saveTo){
		
		int used = getDatasetsNumberUsingFile(uploaded);
		if(used>0){
			throw new SpagoBIServiceException(getActionName(), "{NonBlockingError: true, error:\"USED\", used:\""+used+"\"}");
		}
		boolean alreadyExist = 	checkFileIfExist(saveTo);
		if(alreadyExist){
			throw new SpagoBIServiceException(getActionName(), "{NonBlockingError: true, error:\"EXISTS\"}");
		}

		
	}
	

	
	private boolean checkFileIfExist(File file){
		return (file.exists());
	}
	
	/**
	 * Gets the number of datasets that use the fiel
	 * @param fileName the name of the file
	 * @return the number of datasets using the file
	 * @throws EMFUserError
	 */
	private int getDatasetsNumberUsingFile(FileItem uploaded){
		String configuration;
		String fileName = SpagoBIUtilities.getRelativeFileNames(uploaded.getName());
		String fileToSearch = "\"fileName\":\""+fileName+"\"";
		IDataSet iDataSet;
		int datasetUisng = 0;

		try {
			IDataSetDAO ds = DAOFactory.getDataSetDAO();
			List<IDataSet> datasets = ds.loadDataSets();
			if(datasets!=null){
				for (Iterator<IDataSet> iterator = datasets.iterator(); iterator.hasNext();) {
					iDataSet =  iterator.next();
					configuration = iDataSet.getConfiguration();
					if(configuration.indexOf(fileToSearch)>=0){
						datasetUisng++;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error checking if the file is used by other datasets ", e);
			throw new SpagoBIServiceException(getActionName(),"Error checking if the file is used by other datasets ", e);
		}
		return datasetUisng;

	}
	




}
