/**
 * 
 */
package it.eng.spagobi.tools.dataset.common.datareader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.DataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JDBCStandardDataReader extends AbstractDataReader {

	private static transient Logger logger = Logger.getLogger(JDBCStandardDataReader.class);
    
	
	public JDBCStandardDataReader() { }
	
	public boolean isOffsetSupported() {return true;}	
	public boolean isFetchSizeSupported() {return true;}	
	public boolean isMaxResultsSupported() {return true;}
    
    public IDataStore read(Object data) throws EMFUserError, EMFInternalError {
    	DataStore dataStore;
    	DataStoreMetaData dataStoreMeta;
    	FieldMetadata fieldMeta;
    	String fieldName;
    	ResultSet rs;
    	int columnCount;
    	int columnIndex;
    	
    	logger.debug("IN");
    	
    	dataStore = null;
    	
    	try {
    		
    		Assert.assertNotNull(data, "Input parameter [data] cannot be null");
    		Assert.assertTrue(data instanceof ResultSet, "Input parameter [data] cannot be of type [" + data.getClass().getName() + "]");
    		
    		rs = (ResultSet)data;
    		    		
    		dataStore = new DataStore();
        	dataStoreMeta = new DataStoreMetaData();
        	
        	logger.debug("Reading metadata ...");
        	columnCount = rs.getMetaData().getColumnCount();
    		for(columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
        		fieldMeta = new FieldMetadata();
        		fieldName = rs.getMetaData().getColumnLabel(columnIndex);
        		logger.debug("Field [" + columnIndex + "] name is equal to [" + fieldName + "]");
        		fieldMeta.setName( fieldName );
        		dataStoreMeta.addFiedMeta(fieldMeta);
        	}    
    		dataStore.setMetaData(dataStoreMeta);
    		logger.debug("Metadata readed succcesfully");
    		
    		
    		logger.debug("Reading data ...");
    		if(getOffset() > 0) {
    			logger.debug("Offset is equal to [" + getOffset() + "]");
    			
    			/*
    			 * The following invokation causes an error on Oracle: java.sql.SQLException: Nessuna riga corrente: relative
    			 * rs.relative(getOffset());
    			 */
    			
    			rs.first();
    			rs.relative(getOffset() - 1);
    			
    		} else {
    			logger.debug("Offset not set");
    		}
    		
    		long maxRecToParse = Long.MAX_VALUE;
    		if(getFetchSize() > 0) {
    			maxRecToParse = getFetchSize();
    			logger.debug("FetchSize is equal to [" + maxRecToParse + "]");
    		} else {
    			logger.debug("FetchSize not set");
    		}
    		
    		long recCount = 0;
    		while (rs.next() && (recCount < maxRecToParse) ) {
    			IRecord record = new Record(dataStore);
    			for(columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
    				Object columnValue = rs.getObject(columnIndex);
    				IField field = new Field( columnValue );
					if(columnValue != null) {
						dataStoreMeta.getFieldMeta(columnIndex-1).setType( columnValue.getClass() );
					}
					record.appendField( field );
    			}
    			dataStore.appendRecord(record);
    			recCount++;
    			logger.debug("[" + recCount + "] - Records [" + rs.getRow()  + "] succesfully readed");
    		}
    		logger.debug("Readed [" + recCount+ "] records");
    		logger.debug("Data readed succcesfully");
    		
    		logger.debug("resultset type [" + rs.getType() + "] (" + (rs.getType()  == rs.TYPE_FORWARD_ONLY) + ")");
    		rs.last();
    		int resultNumber = rs.getRow();
    		dataStore.getMetaData().setProperty("resultNumber", new Integer(resultNumber));
    		logger.debug("Reading total record numeber is equal to [" + resultNumber + "]");
    	} catch (SQLException e) {
			e.printStackTrace();
		} finally {
    		logger.debug("OUT");
    	}
    	
    	return dataStore;
    }
}

