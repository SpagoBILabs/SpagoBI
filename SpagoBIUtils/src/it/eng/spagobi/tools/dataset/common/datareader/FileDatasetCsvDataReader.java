/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.datareader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class FileDatasetCsvDataReader extends AbstractDataReader {
	
	
	final static String SEPARATOR=";";

	private static transient Logger logger = Logger.getLogger(CsvDataReader.class);
	
  

	public FileDatasetCsvDataReader() {
		super();
	}

	public IDataStore read( Object data ) {
		DataStore dataStore = null;
		
		InputStream inputDataStream;

		
		logger.debug("IN");
		
		inputDataStream = (InputStream)data;
		
		try {				
			dataStore = readWithCsvMapReader(inputDataStream);

				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataStore;
    }
	
 	private DataStore readWithCsvMapReader( InputStream inputDataStream ) throws Exception {
 		
 		InputStreamReader inputStreamReader = new InputStreamReader(inputDataStream);
    	DataStore dataStore = null;
		MetaData dataStoreMeta;
    	dataStore = new DataStore();
		dataStoreMeta = new MetaData();
		dataStore.setMetaData(dataStoreMeta);

	        
	        ICsvMapReader mapReader = null;
	        try {
	                mapReader = new CsvMapReader(inputStreamReader, CsvPreference.STANDARD_PREFERENCE);
	                
	                // the header columns are used as the keys to the Map
	                final String[] header = mapReader.getHeader(true);
	                
	                int columnsNumber = mapReader.length();
	                
                	//Create Datastore Metadata with header file
                    for (int i= 0; i<header.length;i++){
                    	FieldMetadata fieldMeta = new FieldMetadata();
                    	fieldMeta.setName(header[i]);
                    	fieldMeta.setType(String.class);
                    	dataStoreMeta.addFiedMeta(fieldMeta);
	                }

                	
	                
	                
	                final CellProcessor[] processors = new CellProcessor[columnsNumber];
	                for (int i= 0; i<processors.length;i++){
	                	processors[i] = null;
	                }
	                
	                Map<String, Object> contentsMap;
	                while( (contentsMap = mapReader.read(header, processors)) != null ) {
	                	//Create Datastore data 

	                	IRecord record = new Record(dataStore);

	                        for (int i= 0; i<header.length;i++){
	                        	 System.out.println(header[i]+" = "+contentsMap.get(header[i])); 
	                        	 IField field = new Field(contentsMap.get(header[i]));
	 							 record.appendField(field);
	    	                }
	                        dataStore.appendRecord(record);	
	                        
	                }
	                
	        }
	        finally {
	                if( mapReader != null ) {
	                        mapReader.close();
	                }
	        }
	        return dataStore;
	}

}
