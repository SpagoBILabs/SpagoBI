
package it.eng.qbe.query.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QuerySerializerFactory {
	
	static Map<String, QuerySerializer> serializerMappings;
	static Map<String, QueryDeserializer> deserializerMappings;
	
	static {
		serializerMappings = new HashMap();
		serializerMappings.put( "application/json", new QueryJSONSerializer() );
		
		deserializerMappings = new HashMap();
		deserializerMappings.put( "application/json", new QueryJSONDeserializer() );
	}
	
	public static QuerySerializer getSerializer(String mimeType) {
		return serializerMappings.get( mimeType );
	}
	
	public static QueryDeserializer getDeserializer(String mimeType) {
		return deserializerMappings.get( mimeType );
	}
}

