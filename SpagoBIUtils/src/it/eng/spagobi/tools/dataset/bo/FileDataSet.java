/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
