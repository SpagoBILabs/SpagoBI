package it.eng.spagobi.writeback4j.sql.dbdescriptor;

import java.util.ArrayList;
import java.util.List;

public class FoodmartDbDescriptor implements IDbSchemaDescriptor {


	public List<String> getColumnNames(String table){

		List<String> toReturn = new ArrayList<String>();
		
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
