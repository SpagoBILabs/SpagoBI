package it.eng.spagobi.writeback4j.sql.dbdescriptor;

import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.HashSet;
import java.util.Set;

public class FoodmartDbDescriptor implements IDbSchemaDescriptor {


	public Set<String> getColumnNames(String table, IDataSource dataSource){

		Set<String> toReturn = new HashSet<String>();
		
		toReturn.add("product_id");
		toReturn.add("time_id");
		toReturn.add("customer_id");
		toReturn.add("promotion_id");
		toReturn.add("store_id");
		toReturn.add("store_sales");
		toReturn.add("store_cost");
		toReturn.add("wbversion");
		
		return toReturn;
		
		
	}
	
	
	
}
