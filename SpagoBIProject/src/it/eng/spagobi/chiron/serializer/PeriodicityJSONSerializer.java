package it.eng.spagobi.chiron.serializer;

import it.eng.spagobi.kpi.config.bo.Periodicity;
import it.eng.spagobi.kpi.model.bo.Resource;

import java.util.Locale;
import org.json.JSONObject;

public class PeriodicityJSONSerializer implements Serializer {

	public static final String ID = "idPr";
	private static final String NAME = "name";
	private static final String MONTHS = "months";
	private static final String DAYS = "days";
	private static final String HOURS = "hours";
	private static final String MINUTES = "mins";

	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof Periodicity) ) {
			throw new SerializationException("PeriodicityJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			Periodicity per = (Periodicity)o;
			result = new JSONObject();
			
			result.put(ID, per.getIdKpiPeriodicity());
			result.put(NAME,per.getName() );
			result.put(MONTHS, per.getMonths() );
			result.put(DAYS, per.getDays() );
			result.put(HOURS, per.getHours() );
			result.put(MINUTES, per.getMinutes() );			
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
