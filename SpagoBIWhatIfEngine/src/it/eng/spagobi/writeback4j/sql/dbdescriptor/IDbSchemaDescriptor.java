package it.eng.spagobi.writeback4j.sql.dbdescriptor;

import java.util.List;

public interface IDbSchemaDescriptor {

	
	public List<String> getColumnNames(String table);
	
	
	
}
