/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.common.datawriter;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spagobi.tools.dataset.bo.DataSetVariable;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSONDataWriter implements IDataWriter {
	
	public static final String TOTAL_PROPERTY = "results";
	public static final String ROOT = "rows";
	
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy" );
	private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );

	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JSONDataWriter.class);
	
	public Object write(IDataStore dataStore) throws RuntimeException {
		JSONObject  result = null;
		JSONObject metadata;
		IField field;
		JSONArray fieldsMetaDataJSON;		
		JSONObject fieldMetaDataJSON;
		IRecord record;
		JSONObject recordJSON;
		int recNo;
		
		
		JSONArray recordsJSON;
		int resultNumber;
		Object propertyRawValue;
		String detailProperty;
		
		Assert.assertNotNull(dataStore, "Object to be serialized connot be null");
		
		try {
			result = new JSONObject();
			
			metadata = new JSONObject();
				
			metadata.put("totalProperty", TOTAL_PROPERTY);
			metadata.put("root", ROOT);
			metadata.put("id", "id");
			result.put("metaData", metadata);
			
			propertyRawValue = dataStore.getMetaData().getProperty("resultNumber");
			detailProperty = (String)dataStore.getMetaData().getProperty("detailProperty");
			
			Assert.assertNotNull(propertyRawValue, "DataStore property [resultNumber] cannot be null");
			Assert.assertTrue(propertyRawValue instanceof Integer, "DataStore property [resultNumber] must be of type [Integer]");
			resultNumber = ((Integer)propertyRawValue).intValue();
			Assert.assertTrue(resultNumber >= 0, "DataStore property [resultNumber] cannot be equal to [" + resultNumber + "]. It must be greater or equal to zero");	
			result.put(TOTAL_PROPERTY, resultNumber);
			
			recordsJSON = new JSONArray();
			result.put(ROOT, recordsJSON);
		
			// field's meta
			fieldsMetaDataJSON = new JSONArray();
			fieldsMetaDataJSON.put("recNo"); // counting column
			for(int i = 0; i < dataStore.getMetaData().getFieldCount(); i++) {
				IFieldMetaData fieldMetaData = dataStore.getMetaData().getFieldMeta(i);
				
				propertyRawValue = fieldMetaData.getProperty("visible");
				if(propertyRawValue != null 
						&& (propertyRawValue instanceof Boolean) 
						&& ((Boolean)propertyRawValue).booleanValue() == false) {
					continue;
				}
				
				String fieldName = "column_" + (i+1);
				String fieldHeader = fieldMetaData.getAlias() != null? fieldMetaData.getAlias(): fieldMetaData.getName();
				
				fieldMetaDataJSON = new JSONObject();
				fieldMetaDataJSON.put("name", fieldName);						
				fieldMetaDataJSON.put("dataIndex", fieldName);
				fieldMetaDataJSON.put("header", fieldHeader);
				
				
				Class clazz = fieldMetaData.getType();
				if (clazz == null) {
					logger.debug("Metadata class is null; considering String as default");
					clazz = String.class;
				} else {
					logger.debug("Column [" + (i+1) + "] class is equal to [" + clazz.getName() + "]");
				}
				if( Number.class.isAssignableFrom(clazz) ) {
					//BigInteger, Integer, Long, Short, Byte
					if(Integer.class.isAssignableFrom(clazz) 
				       || BigInteger.class.isAssignableFrom(clazz) 
					   || Long.class.isAssignableFrom(clazz) 
					   || Short.class.isAssignableFrom(clazz)
					   || Byte.class.isAssignableFrom(clazz)) {
						logger.debug("Column [" + (i+1) + "] type is equal to [" + "INTEGER" + "]");
						fieldMetaDataJSON.put("type", "int");
					} else {
						logger.debug("Column [" + (i+1) + "] type is equal to [" + "FLOAT" + "]");
						fieldMetaDataJSON.put("type", "float");
					}
					
					String format = (String) fieldMetaData.getProperty("format");
					if ( format != null ) {
						fieldMetaDataJSON.put("format", format);
					}
					
				} else if( String.class.isAssignableFrom(clazz) ) {
					logger.debug("Column [" + (i+1) + "] type is equal to [" + "STRING" + "]");
					fieldMetaDataJSON.put("type", "string");
				} else if( Timestamp.class.isAssignableFrom(clazz) ) {
					logger.debug("Column [" + (i+1) + "] type is equal to [" + "TIMESTAMP" + "]");
					fieldMetaDataJSON.put("type", "date");
					fieldMetaDataJSON.put("subtype", "timestamp");
					fieldMetaDataJSON.put("dateFormat", "d/m/Y H:i:s");
				} else if( Date.class.isAssignableFrom(clazz) ) {
					logger.debug("Column [" + (i+1) + "] type is equal to [" + "DATE" + "]");
					fieldMetaDataJSON.put("type", "date");
					fieldMetaDataJSON.put("dateFormat", "d/m/Y");
				} else if( Boolean.class.isAssignableFrom(clazz) ) {
					logger.debug("Column [" + (i+1) + "] type is equal to [" + "BOOLEAN" + "]");
					fieldMetaDataJSON.put("type", "boolean");
				} else {
					logger.warn("Column [" + (i+1) + "] type is equal to [" + "???" + "]");
					fieldMetaDataJSON.put("type", "string");
				}
				
				Boolean calculated = (Boolean)fieldMetaData.getProperty("calculated");
				calculated = calculated == null? Boolean.FALSE: calculated;
				if(calculated.booleanValue() == true) {
					DataSetVariable variable =  (DataSetVariable)fieldMetaData.getProperty("variable");
					if(variable.getType().equalsIgnoreCase(DataSetVariable.HTML)) {
						fieldMetaDataJSON.put("type", "auto");
						fieldMetaDataJSON.remove("type");
						fieldMetaDataJSON.put("subtype", "html");
					}
					
				}
				
				if(detailProperty != null && fieldHeader.equalsIgnoreCase(detailProperty)) {
					metadata.put("detailProperty", fieldName);
					fieldMetaDataJSON.put("hidden", true);
				}
				
				fieldsMetaDataJSON.put(fieldMetaDataJSON);
			}
			metadata.put("fields", fieldsMetaDataJSON);
			
			// records
			recNo = 0;
			Iterator records = dataStore.iterator();
			while(records.hasNext()) {
				record = (IRecord)records.next();
				recordJSON = new JSONObject();
				recordJSON.put("id", ++recNo);
				
				for(int i = 0; i < dataStore.getMetaData().getFieldCount(); i++) {
					IFieldMetaData fieldMetaData = dataStore.getMetaData().getFieldMeta(i);
					
					propertyRawValue = fieldMetaData.getProperty("visible");
					if(propertyRawValue != null 
							&& (propertyRawValue instanceof Boolean) 
							&& ((Boolean)propertyRawValue).booleanValue() == false) {
						continue;
					}
										
					field = record.getFieldAt( dataStore.getMetaData().getFieldIndex( fieldMetaData.getName() ) );
					
					
					
					String fieldValue = "";
					if(field.getValue() != null) {
						if(Timestamp.class.isAssignableFrom(fieldMetaData.getType())) {
							fieldValue =  TIMESTAMP_FORMATTER.format(  field.getValue() );
						} else if (Date.class.isAssignableFrom(fieldMetaData.getType())) {
							fieldValue =  DATE_FORMATTER.format(  field.getValue() );
						} else {
							fieldValue =  field.getValue().toString();
						}
					}
					
					
					recordJSON.put("column_" + (i+1), fieldValue);
				}
				
				recordsJSON.put(recordJSON);
			}
			
		
			
			
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while serializing dataStore", t);
		} finally {
			
		}
		
		return result;
	}
}
