package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;

import java.util.Locale;

import org.json.JSONObject;

public class ThresholdValueJSONSerializer  implements Serializer{
	public static final String ID = "id";
	public static final String LABEL = "label";
	
	public Object serialize(Object o, Locale locale)
			throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof ThresholdValue) ) {
			throw new SerializationException("ThresholdValueJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			ThresholdValue tresholdValue = (ThresholdValue)o;
			result = new JSONObject();
			result.put(ID, tresholdValue.getId());
			result.put(LABEL, tresholdValue.getLabel());

		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}
}
