/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.dataproxy.FileDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.CsvDataReader;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;

import org.apache.log4j.Logger;

/**
 * @authors
 *  Angelo Bernabei
 *         angelo.bernabei@eng.it
 *  Giulio Gavardi
 *         giulio.gavardi@eng.it
 *  Andrea Gioia
 *         andrea.gioia@eng.it
 *         
 */
public class FileDataSet extends ConfigurableDataSet{
    
	public static String DS_TYPE = "SbiFileDataSet";
	
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
    	
    	if(dataSetConfig.getFileName() == null || dataSetConfig.getFileName().length() == 0) {
			throw new  IllegalArgumentException("fileName member of SpagoBiDataSet object parameter cannot be null or empty" +
					"while creating a FileDataSet. If you whant to create an empty FileDataSet use the proper constructor.");
		}    	
    	logger.info("File name: " + dataSetConfig.getFileName());
    	
    	setFileName(  dataSetConfig.getFileName() );
    	
    	logger.debug("OUT");    	
    }
     
	
	public SpagoBiDataSet toSpagoBiDataSet( ) {
		SpagoBiDataSet sbd;
		FileDataProxy dataProxy;
		
		sbd = super.toSpagoBiDataSet();
		
		sbd.setType( DS_TYPE );
				
		dataProxy = (FileDataProxy)getDataProxy();
		sbd.setFileName( dataProxy.getFileName() );
		
		return sbd;
	}
	
	/**
	 * try to guess the proper dataReader to use depending on the file extension
	 * 
	 * @param fileName the target filename
	 */
	public void setDataReader(String fileName) {
		String fileExtension;
		
		fileExtension = fileName.lastIndexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.') + 1): null;
		logger.debug("File extension: [" + fileExtension +"]");
		
		if("csv".equalsIgnoreCase( fileExtension )) {
			logger.info("File format: [CSV]");
			setDataReader( new CsvDataReader() );
		} else if ("xml".equalsIgnoreCase( fileExtension ) || "txt".equalsIgnoreCase( fileExtension )) {
			logger.info("File format: [XML]");
			setDataReader( new XmlDataReader() );
		} else {
			throw new  IllegalArgumentException("[" + fileExtension+ "] is not a supported file extension");
		}
	}
	
	public FileDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new FileDataProxy() );
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
}
