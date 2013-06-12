/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.dataproxy.FileDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.CsvDataReader;
import it.eng.spagobi.tools.dataset.common.datareader.FileDatasetCsvDataReader;
import it.eng.spagobi.tools.dataset.common.datareader.FileDatasetXlsDataReader;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;
import it.eng.spagobi.utilities.json.JSONUtils;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors
 *  Angelo Bernabei
 *         angelo.bernabei@eng.it
 *  Giulio Gavardi
 *         giulio.gavardi@eng.it
 *  Andrea Gioia
 *         andrea.gioia@eng.it
 *  Davide Zerbetto
 *         davide.zerbetto@eng.it
 *         
 */
public class FileDataSet extends ConfigurableDataSet{
    
	public static String DS_TYPE = "SbiFileDataSet";
	public static final String FILE_NAME = "fileName";
	public static final String FILE_TYPE = "fileType";

	public String fileType;
	
	private static transient Logger logger = Logger.getLogger(FileDataSet.class);
    
    /**
     * Instantiates a new empty file data set.
     */
    public FileDataSet(){
    	super();
    }
    
    public FileDataSet( SpagoBiDataSet dataSetConfig ) {
    	super(dataSetConfig);
    	
    	logger.debug("IN");
    	try{
    		//JSONObject jsonConf  = ObjectUtils.toJSONObject(dataSetConfig.getConfiguration());
    		String config = JSONUtils.escapeJsonString(dataSetConfig.getConfiguration());		
    		JSONObject jsonConf  = ObjectUtils.toJSONObject(config);
    		String fileName = (jsonConf.get(FILE_NAME) != null)?jsonConf.get(FILE_NAME).toString():"";
    		if(fileName == null || fileName.length() == 0) {
    			throw new  IllegalArgumentException("fileName member of SpagoBiDataSet object parameter cannot be null or empty" +
    					"while creating a FileDataSet. If you whant to create an empty FileDataSet use the proper constructor.");
    		} 
    		this.setFileName((jsonConf.get(FILE_NAME) != null)?jsonConf.get(FILE_NAME).toString():"");
    		logger.info("File name: " + fileName);
		}catch (Exception e){
			logger.error("Error while defining dataset configuration.  Error: " + e.getMessage());
		}
    	//setFileName(  dataSetConfig.getFileName() );
    	
    	logger.debug("OUT");    	
    }
     
	
	public SpagoBiDataSet toSpagoBiDataSet( ) {
		SpagoBiDataSet sbd;
		FileDataProxy dataProxy;
		
		sbd = super.toSpagoBiDataSet();
		
		sbd.setType( DS_TYPE );
				
		dataProxy = (FileDataProxy)getDataProxy();
		/* next informations are already loaded in method super.toSpagoBiDataSet() through the table field configuration
		try{
			JSONObject jsonConf  = new JSONObject();
			jsonConf.put(FILE_NAME,  dataProxy.getFileName());			
			sbd.setConfiguration(jsonConf.toString());
		}catch (Exception e){
			logger.error("Error while defining dataset configuration.  Error: " + e.getMessage());
		}
		//sbd.setFileName( dataProxy.getFileName() );
		*/
		return sbd;
	}
	
	/**
	 * try to guess the proper dataReader to use depending on the file extension
	 * 
	 * @param fileName the target filename
	 */
	public void setDataReader(String fileName) {
		JSONObject jsonConf = null;
		if (this.getConfiguration() != null){
			jsonConf  = ObjectUtils.toJSONObject(this.getConfiguration());
		}
		String fileExtension;
		String fileType = this.getFileType();
		
		fileExtension = fileName.lastIndexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.') + 1): null;
		logger.debug("File extension: [" + fileExtension +"]");
		
		if ((fileType != null) && (!fileType.isEmpty())){
			logger.debug("File type is: [" + fileType +"]");
		} else {
			logger.debug("No file type specified, using file extension as file type: [" + fileExtension +"]");
			fileType = fileExtension;
		}

		
		if("CSV".equalsIgnoreCase( fileType )) {
			logger.info("File format: [CSV]");
			//setDataReader( new CsvDataReader() );
			setDataReader( new FileDatasetCsvDataReader(jsonConf));
		} 
		else if ("XLS".equalsIgnoreCase( fileType )){
			logger.info("File format: [XLS Office 2003]");
			setDataReader( new FileDatasetXlsDataReader(jsonConf) );
		}		
		else if ("xml".equalsIgnoreCase( fileExtension ) || "txt".equalsIgnoreCase( fileExtension )) {
			logger.info("File format: [XML]");
			setDataReader( new XmlDataReader() );
		} 
		
		else {
			throw new  IllegalArgumentException("[" + fileExtension+ "] is not a supported file type");
		}
	}
	
	public FileDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new FileDataProxy(this.getResourcePath()) );
			dataProxy = getDataProxy();
		}
		
		if(!(dataProxy instanceof  FileDataProxy)) throw new RuntimeException("DataProxy cannot be of type [" + 
				dataProxy.getClass().getName() + "] in FileDataSet");
		
		return (FileDataProxy)dataProxy;
	}
	
	public String getFileName() {		
		return getDataProxy().getFileName();		
	}
	
	public void setFileName(String fileName) {
		setFileName(fileName, true);
	}
	
	public void setFileName(String fileName, boolean updateFileFormat) {
		if(fileName == null || fileName.length() == 0) {
			throw new  IllegalArgumentException("fileName argument cannot be null or an empty string");
		}
		getDataProxy().setFileName(fileName);
		
		if( updateFileFormat ) {
			try{	
				setDataReader(fileName);
			} catch (Exception e) {
				throw new RuntimeException("Missing right exstension", e);
			}
		}
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	@Override
	public String getSignature() {
		return this.getDataProxy().getMD5Checksum();
	}
}
