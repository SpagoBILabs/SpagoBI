package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;

import java.util.Locale;

import org.json.JSONObject;

public class SbiDataSetConfigJSONSerializer implements Serializer {

	public static final String DATASET_ID = "id";
	private static final String DATASET_NAME = "name";
	private static final String DATASET_DESCRIPTION = "description";
	private static final String DATASET_LABEL = "label";
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof SbiDataSetConfig) ) {
			throw new SerializationException("SbiDataSetConfigJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			SbiDataSetConfig ds = (SbiDataSetConfig)o;
			result = new JSONObject();
			
			result.put(DATASET_ID, ds.getDsId() );
			result.put(DATASET_NAME, ds.getName() );
			result.put(DATASET_DESCRIPTION, ds.getDescription() );
			result.put(DATASET_LABEL, ds.getLabel() );		
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}