package it.eng.spagobi.engines.whatif.services.serializer;

import java.util.List;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.olap4j.metadata.Member;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonSerializerUtils {
	
//	public static final String serialize(List<Member> list){
//		ObjectMapper mapper = new ObjectMapper();   
//		SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
//		simpleModule.addSerializer(Member.class, new MemberJsonSerializer());
//		mapper.registerModule(simpleModule);
//		try {
//			return mapper.writeValueAsString(list);
//		} catch (Exception e) {
//			logger.error("Error serializing the MemberEntry",e);
//			throw new SpagoBIRuntimeException("Error serializing the MemberEntry",e);
//		}
//	}

}
