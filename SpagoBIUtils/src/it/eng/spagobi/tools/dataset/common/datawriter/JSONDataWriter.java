/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.common.datawriter;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spagobi.tools.dataset.bo.DataSetVariable;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JSONDataWriter implements IDataWriter {
	
	public static final String TOTAL_PROPERTY = "results";
	public static final String ROOT = "rows";
	
	public static final String PROPERTY_PUT_IDS = "putIDs";
	
	private boolean putIDs = true;
	private boolean adjust = false;
	private JSONArray fieldsOptions;
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy" );
	private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );
	public static final String WORKSHEETS_ADDITIONAL_DATA_FIELDS_OPTIONS_OPTIONS = "options";
	public static final String WORKSHEETS_ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR = "measureScaleFactor";
	public static final String METADATA = "metaData";
	public static final String PROPERTY_FIELD_OPTION = "PROPERTY_FIELD_OPTION";
	public static final String PROPERTY_ADJUST = "PROPERTY_ADJUST";
	
	public JSONDataWriter() {}
	
	public JSONDataWriter(Map<String, Object> properties) {
		if (properties != null) {
			Object o = properties.get(PROPERTY_PUT_IDS);
			if (o != null) {
				this.putIDs = Boolean.parseBoolean(o.toString());
			}
			o = properties.get(PROPERTY_FIELD_OPTION);
			if (o != null) {
				this.fieldsOptions = (JSONArray)o;
			}
			o = properties.get(PROPERTY_ADJUST);
			if (o != null) {
				this.adjust = Boolean.parseBoolean(o.toString());
			}
		}
	}
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(JSONDataWriter.class);
    

    
	public Object write(IDataStore dataStore) throws RuntimeException {
		JSONObject  result = null;
		JSONObject metadata;
		IField field;
		IRecord record;
		JSONObject recordJSON;
		int recNo;
		JSONArray recordsJSON;
		int resultNumber;
		Object propertyRawValue;
		
		Assert.assertNotNull(dataStore, "Object to be serialized connot be null");
		
		metadata = (JSONObject) write(dataStore.getMetaData());
		
		try {
			result = new JSONObject();
			
			result.put(METADATA, metadata);
			
			propertyRawValue = dataStore.getMetaData().getProperty("resultNumber");
			if ( propertyRawValue == null ) {
				propertyRawValue = new Integer(1);
			}
			Assert.assertNotNull(propertyRawValue, "DataStore property [resultNumber] cannot be null");
			Assert.assertTrue(propertyRawValue instanceof Integer, "DataStore property [resultNumber] must be of type [Integer]");
			resultNumber = ((Integer)propertyRawValue).intValue();
			Assert.assertTrue(resultNumber >= 0, "DataStore property [resultNumber] cannot be equal to [" + resultNumber + "]. It must be greater or equal to zero");	
			result.put(TOTAL_PROPERTY, resultNumber);
			
			recordsJSON = new JSONArray();
			result.put(ROOT, recordsJSON);
			
			// records
			recNo = 0;
			Iterator records = dataStore.iterator();
			while(records.hasNext()) {
				record = (IRecord)records.next();
				recordJSON = new JSONObject();
				if (this.putIDs) {
					recordJSON.put("id", ++recNo);
				}
				
				for(int i = 0; i < dataStore.getMetaData().getFieldCount(); i++) {
					IFieldMetaData fieldMetaData = dataStore.getMetaData().getFieldMeta(i);
					
					propertyRawValue = fieldMetaData.getProperty("visible");
					if(propertyRawValue != null 
							&& (propertyRawValue instanceof Boolean) 
							&& ((Boolean)propertyRawValue).booleanValue() == false) {
						continue;
					}
					String key = fieldMetaData.getName();
					field = record.getFieldAt( dataStore.getMetaData().getFieldIndex( key ) );
					
					
					
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
					String fieldName;
					
					if(adjust){
						fieldName = fieldMetaData.getName();
					}else{
						fieldName = getFieldName(fieldMetaData, i);
					}
					recordJSON.put(fieldName, fieldValue);
				}
				
				recordsJSON.put(recordJSON);
			}
			
		
			
			
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while serializing dataStore", t);
		} finally {
			
		}
		
		return result;
	}

	protected String getFieldHeader(IFieldMetaData fieldMetaData, int i) {
		String fieldHeader = fieldMetaData.getAlias() != null? fieldMetaData.getAlias(): fieldMetaData.getName();
		return fieldHeader;
	}

	protected String getFieldName(IFieldMetaData fieldMetaData, int i) {
		String fieldName = "column_" + (i+1);
		return fieldName;
	}
	
	public Object write(IMetaData metadata) {
		
		try {
		
			JSONObject toReturn = new JSONObject();
			
			toReturn.put("totalProperty", TOTAL_PROPERTY);
			toReturn.put("root", ROOT);
			if (this.putIDs) {
				toReturn.put("id", "id");
			}
			
			// field's meta
			JSONArray fieldsMetaDataJSON = new JSONArray();
			fieldsMetaDataJSON.put("recNo"); // counting column
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				IFieldMetaData fieldMetaData = metadata.getFieldMeta(i);
				
				Object propertyRawValue = fieldMetaData.getProperty("visible");
				if(propertyRawValue != null 
						&& (propertyRawValue instanceof Boolean) 
						&& ((Boolean)propertyRawValue).booleanValue() == false) {
					continue;
				}
				
				String fieldName = getFieldName(fieldMetaData, i);
				String fieldHeader = getFieldHeader(fieldMetaData, i);
				
				JSONObject fieldMetaDataJSON = new JSONObject();
				if(adjust){
					fieldMetaDataJSON.put("name", fieldHeader);
				}else{
					fieldMetaDataJSON.put("name", fieldName);
				}
				fieldMetaDataJSON.put("name", fieldHeader);						
				fieldMetaDataJSON.put("dataIndex", fieldName);

				
				addMeasuresScaleFactor(fieldsOptions,fieldMetaData.getName(),fieldMetaDataJSON);
				
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
					String decimalPrecision = 	(String) fieldMetaData.getProperty(IFieldMetaData.DECIMALPRECISION);	
					if(decimalPrecision!=null){
						fieldMetaDataJSON.put("format", "{"+IFieldMetaData.DECIMALPRECISION+": "+decimalPrecision+"}");
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
				
				String detailProperty = (String) metadata.getProperty("detailProperty");
				if(detailProperty != null && fieldHeader.equalsIgnoreCase(detailProperty)) {
					toReturn.put("detailProperty", fieldName);
					fieldMetaDataJSON.put("hidden", true);
				}
				
				fieldsMetaDataJSON.put(fieldMetaDataJSON);
			}
			toReturn.put("fields", fieldsMetaDataJSON);
			
			return toReturn;
		
		
		} catch(Throwable t) {
			throw new RuntimeException("An unpredicted error occurred while serializing dataStore", t);
		} finally {
			
		}
	}
	
	private void addMeasuresScaleFactor(JSONArray fieldOptions, String fieldId,
			JSONObject fieldMetaDataJSON) {
		if (fieldOptions != null) {
			for (int i = 0; i < fieldOptions.length(); i++) {
				try {
					JSONObject afield = fieldOptions.getJSONObject(i);
					JSONObject aFieldOptions = afield
							.getJSONObject(WORKSHEETS_ADDITIONAL_DATA_FIELDS_OPTIONS_OPTIONS);
					String afieldId = afield.getString("id");
					String scaleFactor = aFieldOptions
							.optString(WORKSHEETS_ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR);
					if (afieldId.equals(fieldId) && scaleFactor != null) {
						fieldMetaDataJSON
								.put(WORKSHEETS_ADDITIONAL_DATA_FIELDS_OPTIONS_SCALE_FACTOR,
										scaleFactor);
						return;
					}
				} catch (Exception e) {
					throw new RuntimeException(
							"An unpredicted error occurred while adding measures scale factor",
							e);
				}
			}
		}
	}

}
