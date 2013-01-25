/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.datareader;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Monica Franceschini (monica.franceschini@eng.it)
 */
public class JDBCHiveDataReader extends AbstractDataReader {

	private static transient Logger logger = Logger.getLogger(JDBCHiveDataReader.class);
    
	
	public JDBCHiveDataReader() { }
	
	public boolean isOffsetSupported() {return true;}	
	public boolean isFetchSizeSupported() {return true;}	
	public boolean isMaxResultsSupported() {return true;}
    
    public IDataStore read(Object data) throws EMFUserError, EMFInternalError {
    	DataStore dataStore = null;
		MetaData dataStoreMeta;
    	int columnCount;
    	int columnIndex;
		ResultSet rs;

    	FieldMetadata fieldMeta;
    	
		logger.debug("IN");

		rs = (ResultSet) data;  

		
		dataStore = new DataStore();
		dataStoreMeta = new MetaData();
		
		try {				

    		while (rs.next()) {
    			IRecord record = new Record(dataStore);
            	logger.debug("Reading metadata ...");
            	columnCount = rs.getMetaData().getColumnCount();
            	
        		for(columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            		fieldMeta = new FieldMetadata();
            		
    				Object columnValue = rs.getObject(columnIndex);
    				IField field = new Field( columnValue );

    				record.appendField( field );					
					
            		String fieldName = rs.getMetaData().getColumnLabel(columnIndex);
            		
            		logger.debug("Field [" + columnIndex + "] name is equal to [" + fieldName + "]");
            		if(dataStoreMeta.getFieldIndex(fieldName) == -1){
                		fieldMeta.setName( fieldName );
                		fieldMeta.setType(String.class);
                		dataStoreMeta.addFiedMeta(fieldMeta);
            		}

            		
            	}    
        		
    			dataStore.appendRecord(record);
    			
    		}
    		dataStore.setMetaData(dataStoreMeta);
				
		} catch (SQLException e) {
			logger.error("An unexpected error occured while reading resultset", e);
		}finally {
    		logger.debug("OUT");
    	}
		
		return dataStore;
    }


	
}

