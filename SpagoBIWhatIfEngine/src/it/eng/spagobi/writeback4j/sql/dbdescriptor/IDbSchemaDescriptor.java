package it.eng.spagobi.writeback4j.sql.dbdescriptor;

import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.Set;

public interface IDbSchemaDescriptor {

	
	public Set<String> getColumnNames(String table, IDataSource dataSource);
	
	
	
}
