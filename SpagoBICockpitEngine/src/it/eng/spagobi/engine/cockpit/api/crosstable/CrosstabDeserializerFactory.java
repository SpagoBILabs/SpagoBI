package it.eng.spagobi.engine.cockpit.api.crosstable;

import it.eng.qbe.serializer.IDeserializer;
import it.eng.qbe.serializer.IDeserializerFactory;
import it.eng.qbe.serializer.SerializationManager;

public class CrosstabDeserializerFactory implements IDeserializerFactory {
	
	static CrosstabDeserializerFactory instance;
	
	static CrosstabDeserializerFactory getIntsnce() {
		return instance;
	}
	
	static {
		instance = new CrosstabDeserializerFactory();
		SerializationManager.registerDeserializerFactory(CrosstabDefinition.class, instance);
		
	}
	
	public static CrosstabDeserializerFactory getInstance() {
		if (instance == null) {
			instance = new CrosstabDeserializerFactory();
		}
		return instance;
	}
	
	private CrosstabDeserializerFactory() {}

	public IDeserializer getDeserializer(String mimeType) {
		return new CrosstabJSONDeserializer();
	}

}