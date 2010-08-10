/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.query;

import java.util.HashMap;
import java.util.Map;

public class AggregationFunctions {
	
	private static Map<String, IAggregationFunction> aggregationFunctions;
	
	public static String NONE = "NONE";
	public static String SUM = "SUM";
	public static String AVG = "AVG";
	public static String MAX = "MAX";
	public static String MIN = "MIN";
	public static String COUNT = "COUNT";
	
	
	public static IAggregationFunction NONE_FUNCTION = new IAggregationFunction() {
		public String getName() {return NONE;}
		public String apply(String fieldName) {
			return fieldName;
		}
	};
	
	public static IAggregationFunction SUM_FUNCTION = new IAggregationFunction() {
		public String getName() {return SUM;}
		public String apply(String fieldName) {
			return "SUM(" + fieldName + ")";
		}
	};
	
	public static IAggregationFunction AVG_FUNCTION = new IAggregationFunction() {
		public String getName() {return AVG;}
		public String apply(String fieldName) {
			return "AVG(" + fieldName + ")";
		}
	};
	
	public static IAggregationFunction MAX_FUNCTION = new IAggregationFunction() {
		public String getName() {return MAX;}
		public String apply(String fieldName) {
			return "MAX(" + fieldName + ")";
		}
	};
	
	public static IAggregationFunction MIN_FUNCTION = new IAggregationFunction() {
		public String getName() {return MIN;}
		public String apply(String fieldName) {
			return "MIN(" + fieldName + ")";
		}
	};
	
	public static IAggregationFunction COUNT_FUNCTION = new IAggregationFunction() {
		public String getName() {return COUNT;}
		public String apply(String fieldName) {
			return "COUNT(" + fieldName + ")";
		}
	};
	
	static {
		aggregationFunctions = new HashMap<String, IAggregationFunction>();
		aggregationFunctions.put(NONE, NONE_FUNCTION);
		aggregationFunctions.put(SUM, SUM_FUNCTION);
		aggregationFunctions.put(AVG, AVG_FUNCTION);
		aggregationFunctions.put(MAX, MAX_FUNCTION);
		aggregationFunctions.put(MIN, MIN_FUNCTION);
		aggregationFunctions.put(COUNT, COUNT_FUNCTION);
	}
	
	public static IAggregationFunction get(String functionName) {
		IAggregationFunction toReturn = null;
		if (functionName != null && aggregationFunctions.containsKey(functionName.toUpperCase())) {
			toReturn = aggregationFunctions.get(functionName.toUpperCase());
		} else {
			toReturn = NONE_FUNCTION;
		}
		return toReturn;
	}
	
}
