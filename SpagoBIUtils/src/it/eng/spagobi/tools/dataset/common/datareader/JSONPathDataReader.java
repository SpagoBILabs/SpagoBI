/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.datareader;

import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.utilities.Helper;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.json.JSONUtils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.minidev.json.JSONArray;

import com.jayway.jsonpath.JsonPath;

/**
 * This reader convert JSON string to an {@link IDataStore}. The JSON must contains the items to convert, they are found using {@link JsonPath}. The name of
 * each {@link IField} must be defined. The type can be fixed or can be defined dinamically by {@link JsonPath}. The value is found dinamically by
 * {@link JsonPath}. For an example of usage check the related Test class.
 * 
 * @author fabrizio
 *
 */
public class JSONPathDataReader extends AbstractDataReader {

	private static final Class<String> ALL_OTHER_TYPES = String.class;

	private static final String DATE_FORMAT_FIELD_METADATA_PROPERTY = "dateFormat";

	private static final String JSON_PATH_VALUE_METADATA_PROPERTY = "jsonPathValue";

	private static final String JSON_PATH_TYPE_METADATA_PROPERTY = "jsonPathType";

	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

	private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

	private static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String ATTRIBUTES_DIRECTLY = "attributesDirectly";

	private static final String ID_NAME = "id";

	public static class JSONPathAttribute {
		private String name;
		private String jsonPathValue;
		private String jsonPathType;

		public JSONPathAttribute(String name, String jsonPathValue, String jsonPathType) {
			this.name = name;
			this.jsonPathValue = jsonPathValue;
			this.jsonPathType = jsonPathType;
		}

		public String getName() {
			return name;
		}

		public String getJsonPathValue() {
			return jsonPathValue;
		}

		public String getJsonPathType() {
			return jsonPathType;
		}

	}

	private final String jsonPathItems;
	private final List<JSONPathAttribute> jsonPathAttributes;
	private final boolean useItemsAttributes;

	public JSONPathDataReader(String jsonPathItems, List<JSONPathAttribute> jsonPathAttributes, boolean useItemsAttributes) {
		Helper.checkNotNullNotTrimNotEmpty(jsonPathItems, "jsonPathItems");
		Helper.checkWithoutNulls(jsonPathAttributes, "pathAttributes");
		Helper.checkNotNull(jsonPathAttributes, "jsonPathAttributes");
		this.jsonPathItems = jsonPathItems;
		this.jsonPathAttributes = jsonPathAttributes;
		this.useItemsAttributes = useItemsAttributes;
	}

	private static boolean isJSON(String responseBody) {
		try {
			JSONUtils.toJSONObject(responseBody);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public IDataStore read(Object data) {
		Helper.checkNotNull(data, "data");
		if (!(data instanceof String)) {
			throw new IllegalArgumentException("data must be a string");
		}
		String d = (String) data;
		Assert.assertTrue(isJSON(d), String.format("Data must be a valid JSON: %s", data));

		try {
			DataStore dataStore = new DataStore();
			MetaData dataStoreMeta = new MetaData();
			dataStore.setMetaData(dataStoreMeta);
			addFieldMetadata(dataStoreMeta);
			addData(d, dataStore, dataStoreMeta);
			return dataStore;
		} catch (ParseException e) {
			throw new JSONPathDataReaderException(e);
		} catch (JSONPathDataReaderException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONPathDataReaderException(e);
		}
	}

	private void addData(String data, DataStore dataStore, MetaData dataStoreMeta) throws ParseException {
		List<Object> parsedData = getItems(data);

		for (Object o : parsedData) {
			IRecord record = new Record(dataStore);

			for (int j = 0; j < dataStoreMeta.getFieldCount(); j++) {
				IFieldMetaData fieldMeta = dataStoreMeta.getFieldMeta(j);
				Object propAttr = fieldMeta.getProperty(ATTRIBUTES_DIRECTLY);
				if (propAttr != null && (Boolean) propAttr) {
					// managed after this process
					continue;
				}
				String jsonPathValue = (String) fieldMeta.getProperty(JSON_PATH_VALUE_METADATA_PROPERTY);
				Assert.assertNotNull(jsonPathValue != null, "jsonPathValue!=null");
				// can be fixed (not real JSONPath) or null (after value calculation)
				String stringValue = isRealJsonPath(jsonPathValue) ? getJSONPathValue(o, jsonPathValue) : jsonPathValue;
				IFieldMetaData fm = fieldMeta;
				Class<?> type = fm.getType();
				if (type == null) {
					// dinamically defined, from json data path
					String typeString = getJSONPathValue(o, (String) fieldMeta.getProperty(JSON_PATH_TYPE_METADATA_PROPERTY));
					Assert.assertNotNull(typeString, "type of jsonpath type");
					type = getType(typeString);
					fm.setType(type);
					if (type.equals(Date.class)) {
						setDateTypeFormat(fm, typeString);
					}
				}
				Assert.assertNotNull(type != null, "type!=null");

				IField field = new Field(getValue(stringValue, fm));
				record.appendField(field);
			}
			if (useItemsAttributes) {
				manageItemsAttributes(o, record, dataStoreMeta, dataStore);
			}

			dataStore.appendRecord(record);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object> getItems(String data) {
		Object parsed = JsonPath.read(data, jsonPathItems);
		if (parsed == null) {
			throw new JSONPathDataReaderException(String.format("Items not found in %s with json path %s", data, jsonPathItems));
		}

		// can be an array or a single object
		List<Object> parsedData;
		if (parsed instanceof List) {
			parsedData = (List<Object>) parsed;
		} else {
			parsedData = Arrays.asList(parsed);
		}
		return parsedData;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void manageItemsAttributes(Object data, IRecord rec, MetaData dsm, DataStore dataStore) {
		Assert.assertTrue(data instanceof Map, "data instanceof Map");
		Map jsonObject = (Map) data;

		for (int j = 0; j < dsm.getFieldCount(); j++) {
			IFieldMetaData fieldMeta = dsm.getFieldMeta(j);
			Object propAttr = fieldMeta.getProperty(ATTRIBUTES_DIRECTLY);
			if (propAttr == null || !(Boolean) propAttr) {
				// managed before
				continue;
			}

			String name = fieldMeta.getName();
			if (jsonObject.containsKey(name)) {
				Object value = jsonObject.get(name);
				Assert.assertNotNull(value, "value is null");
				rec.appendField(new Field(value.toString()));
			} else {
				// add null value
				rec.appendField(new Field(null));
			}
		}

		// find new elements
		for (String key : new TreeSet<String>(jsonObject.keySet())) {
			int index = dsm.getFieldIndex(key);
			if (index != -1) {
				// already present,manged before
				continue;
			}

			// not found
			FieldMetadata fm = new FieldMetadata(key, ALL_OTHER_TYPES);
			fm.setProperty(ATTRIBUTES_DIRECTLY, true);
			dsm.addFiedMeta(fm);

			Object value = jsonObject.get(key);
			Assert.assertNotNull(value, "value is null");
			rec.appendField(new Field(value.toString()));

			// add null to previous records
			// current record not added
			for (int i = 0; i < dataStore.getRecordsCount(); i++) {
				IRecord previousRecord = dataStore.getRecordAt(i);
				Assert.assertTrue(previousRecord != rec, "previousRecord!=rec");
				previousRecord.appendField(new Field(null));
			}
		}
	}

	private static String getJSONPathValue(Object o, String jsonPathValue) {
		// can be an array with a single value, a single object or also null (not found)
		Object res = JsonPath.read(o, jsonPathValue);
		if (res == null) {
			return null;
		}

		if (res instanceof JSONArray) {
			JSONArray array = (JSONArray) res;
			if (array.size() > 1) {
				throw new IllegalArgumentException(String.format("There is no unique value: %s", array.toString()));
			}
			if (array.isEmpty()) {
				return null;
			}

			res = array.get(0);
		}

		return res.toString();
	}

	private void addFieldMetadata(MetaData dataStoreMeta) {
		int index = 0;
		boolean idSet = false;
		for (JSONPathAttribute jpa : jsonPathAttributes) {
			FieldMetadata fm = new FieldMetadata();
			String header = jpa.name;
			fm.setAlias(header);
			fm.setName(header);
			if (ID_NAME.equalsIgnoreCase(header)) {
				if (idSet) {
					throw new JSONPathDataReaderException("There is no unique id field.");
				}
				idSet=true;
				dataStoreMeta.setIdField(index);
			}
			fm.setProperty(JSON_PATH_VALUE_METADATA_PROPERTY, jpa.jsonPathValue);
			if (isRealJsonPath(jpa.jsonPathType)) {
				// dinamically defined
				fm.setProperty(JSON_PATH_TYPE_METADATA_PROPERTY, jpa.jsonPathType);
				// type == null, defined later
			} else {
				Class<?> type = getType(jpa.jsonPathType);
				Assert.assertNotNull(type, "type");
				// type statically defined
				fm.setType(type);
				if (type.equals(Date.class)) {
					setDateTypeFormat(fm, jpa.jsonPathType);
				}

			}
			dataStoreMeta.addFiedMeta(fm);
			index++;
		}
	}

	private void setDateTypeFormat(IFieldMetaData fm, String jsonPathType) {
		String dateFormat = getDateFormat(jsonPathType);
		fm.setProperty(DATE_FORMAT_FIELD_METADATA_PROPERTY, dateFormat);
	}

	private static Object getValue(String value, IFieldMetaData fmd) throws ParseException {
		if (value == null) {
			return null;
		}

		Class<?> fieldType = fmd.getType();
		if (fieldType.equals(String.class)) {
			return value;
		} else if (fieldType.equals(BigInteger.class)) {
			return Long.parseLong(value);
		} else if (fieldType.equals(Double.class)) {
			return Double.parseDouble(value);
		} else if (fieldType.equals(Date.class)) {
			String dateFormat = (String) fmd.getProperty(DATE_FORMAT_FIELD_METADATA_PROPERTY);
			Assert.assertNotNull(dateFormat != null, "dateFormat != null");
			return getSimpleDateFormat(dateFormat).parse(value);
		} else if (fieldType.equals(Boolean.class)) {
			return Boolean.valueOf(value);
		}
		Assert.assertUnreachable(String.format("Impossible to resolve field type: %s", fieldType));
		throw new RuntimeException(); // unreachable
	}

	private static SimpleDateFormat getSimpleDateFormat(String dateFormat) {
		SimpleDateFormat res = new SimpleDateFormat(dateFormat);
		res.setLenient(true);
		return res;
	}

	/**
	 * format like: 'date yyyyMMdd'
	 * 
	 * @param typeString
	 * @return
	 */
	private String getDateFormat(String typeString) {
		int index = typeString.indexOf(' ');
		if (index >= 0) {
			while (typeString.charAt(index) == ' ') {
				++index;
			}
			String res = typeString.substring(index).trim();
			if (!res.isEmpty()) {
				try {
					new SimpleDateFormat(res); // try the pattern
				} catch (IllegalArgumentException e) {
					throw new JSONPathDataReaderException("Invalid pattern: " + res, e);
				}
				return res;
			}
		}
		if (typeString.toLowerCase().startsWith("datetime") || typeString.toLowerCase().startsWith("timestamp")) {
			return DEFAULT_TIMESTAMP_PATTERN;
		}

		if (typeString.toLowerCase().startsWith("date")) {
			return DEFAULT_DATE_PATTERN;
		}

		// time or everything else
		if (typeString.toLowerCase().startsWith("time")) {
			return DEFAULT_TIME_PATTERN;
		}

		Assert.assertUnreachable("type date not recognized: " + typeString);
		return null;
	}

	private static boolean isRealJsonPath(String jsonPath) {
		// don't start with param substitution
		return jsonPath.startsWith("$")
				&& (!jsonPath.startsWith(StringUtilities.START_PARAMETER) && !jsonPath.startsWith(StringUtilities.START_USER_PROFILE_ATTRIBUTE));
	}

	private static Class<?> getType(String jsonPathType) {
		if (jsonPathType == null) {
			return ALL_OTHER_TYPES;
		}

		if (jsonPathType.equalsIgnoreCase("string")) {
			return String.class;
		} else if (jsonPathType.equalsIgnoreCase("int") || jsonPathType.equalsIgnoreCase("long") || jsonPathType.equalsIgnoreCase("bigint")) {
			return Long.class;
		} else if (jsonPathType.equalsIgnoreCase("float") || jsonPathType.equalsIgnoreCase("double")) {
			return Double.class;
		} else if (jsonPathType.toLowerCase().startsWith("date")) {
			return Date.class;
		} else if (jsonPathType.toLowerCase().startsWith("timestamp")) {
			return Date.class;
		} else if (jsonPathType.toLowerCase().startsWith("time")) {
			return Date.class;
		} else if (jsonPathType.toLowerCase().startsWith("datetime")) {
			return Date.class;
		} else if (jsonPathType.equalsIgnoreCase("boolean")) {
			return Boolean.class;
		}

		// everything else
		return ALL_OTHER_TYPES;
	}

	public String getJsonPathItems() {
		return jsonPathItems;
	}

	public List<JSONPathAttribute> getJsonPathAttributes() {
		return jsonPathAttributes;
	}

	public boolean isUseItemsAttributes() {
		return useItemsAttributes;
	}
}
