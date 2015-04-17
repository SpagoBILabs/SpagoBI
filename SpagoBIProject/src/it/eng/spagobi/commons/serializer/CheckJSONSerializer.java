package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.behaviouralmodel.check.bo.Check;

import java.util.Locale;

import org.json.JSONObject;

public class CheckJSONSerializer implements Serializer {

	public static final String CHECKID = "CHECKID";
	public static final String VALUETYPEID = "VALUETYPEID";
	public static final String VALUETYPECD = "VALUETYPECD";
	public static final String NAME = "NAME";
	public static final String LABEL = "LABEL";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String FIRSTVALUE = "FIRSTVALUE";
	public static final String SECONDVALUE = "SECONDVALUE";

	public Object serialize(Object o, Locale locale) throws SerializationException {

		JSONObject result = null;

		if (!(o instanceof Check)) {

			throw new SerializationException("CheckJSONSerializer is unable to serialize object of type: " + o.getClass().getName());

		}

		try {

			Check check = null;
			result = new JSONObject();
			check = (Check) o;

			result.put(CHECKID, check.getCheckId());
			result.put(VALUETYPEID, check.getValueTypeId());
			result.put(VALUETYPECD, check.getValueTypeCd());
			result.put(NAME, check.getName());
			result.put(LABEL, check.getLabel());
			result.put(DESCRIPTION, check.getDescription());
			result.put(FIRSTVALUE, check.getFirstValue());
			result.put(SECONDVALUE, check.getSecondValue());
			result.put("CHECKED", false);

		} catch (Throwable t) {

			throw new SerializationException("An error occurred while serializing object: " + o, t);

		} finally {

		}

		return result;
	}

}
