
package it.eng.spagobi.chiron.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SerializerFactory {
	
	static Map<String, Serializer> mappings;
	
	static {
		mappings = new HashMap();
		mappings.put( "application/json", new JSONSerializer() );
	}
	
	public static Serializer getSerializer(String mimeType) {
		return mappings.get( mimeType );
	}
}
