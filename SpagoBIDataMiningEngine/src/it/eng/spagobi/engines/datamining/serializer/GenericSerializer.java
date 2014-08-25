package it.eng.spagobi.engines.datamining.serializer;


import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.apache.log4j.Logger;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericSerializer implements ISerializer {

	public static transient Logger logger = Logger.getLogger(GenericSerializer.class);
	private static final String mimeType = "application/json";
	
	ObjectMapper mapper;

	public GenericSerializer() {
		mapper = new ObjectMapper();
	}

	public String serialize(Object object) throws SerializationException {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			logger.error("Error serializing the MemberEntry",e);
			throw new SpagoBIRuntimeException("Error serializing the MemberEntry",e);
		}
	}
	
	public Object deserialize(String toDeserialize, Class object) throws SerializationException {
		try {
			return mapper.readValue(toDeserialize, object);
		} catch (Exception e) {
			logger.error("Error deserializing the MemberEntry",e);
			throw new SpagoBIRuntimeException("Error deserializing the MemberEntry",e);
		}
	}
	
	public Object deserialize(String toDeserialize, TypeReference object) throws SerializationException {
		try {
			return mapper.readValue(toDeserialize, object);
		} catch (Exception e) {
			logger.error("Error deserializing the MemberEntry",e);
			throw new SpagoBIRuntimeException("Error deserializing the MemberEntry",e);
		}
	}

	public String getVersion() {
		return SerializationManager.DEFAULT_VERSION;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.whatif.services.serializer.ISerializer#getFormat()
	 */
	public String getFormat() {
		return mimeType;
	}   

}
