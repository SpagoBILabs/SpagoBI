/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.jpivotaddins.util;

/**
 * @author Gioia
 */
public class ParameterSetter {
	
	
	
	
	public static String setParameters(String query, String pname, String pvalue) {
		String newQuery = query;
		// substitute the mondrian parameter sintax		
		int index = -1;
		int ptr = 0;
		while( (index = newQuery.indexOf("Parameter", ptr)) != -1 ) {
			ptr = newQuery.indexOf("(", index);
			String firstArg = newQuery.substring(newQuery.indexOf("(", ptr) + 1, newQuery.indexOf(",", ptr));	
			if(!firstArg.trim().equalsIgnoreCase("\""+pname+"\"")) 
				continue;
			ptr = newQuery.indexOf(",", ptr) + 1; // 2 arg
			String secondArg = newQuery.substring(ptr, newQuery.indexOf(",", ptr));	
			ptr = newQuery.indexOf(",", ptr) + 1; // 3 arg
			String thirdArg = newQuery.substring(ptr, newQuery.indexOf(",", ptr));
			// if the parameter type is STRING, add double apix to the value passed by SpagoBI
			if(secondArg.equalsIgnoreCase("STRING")){
				newQuery = newQuery.substring(0, ptr) + '"' + pvalue + '"' + newQuery.substring(newQuery.indexOf(",", ptr+1), newQuery.length());
			} else {
				newQuery = newQuery.substring(0, ptr) + pvalue + newQuery.substring(newQuery.indexOf(",", ptr+1), newQuery.length());
			}
		}
		// substitute the spagobi parameter sintax 
		index = -1;
		ptr = 0;
		while((index=newQuery.indexOf("${", ptr)) != -1 ) {
			int indexEnd = newQuery.indexOf("}", index);
			ptr = indexEnd;
			String namePar = newQuery.substring(index+2, indexEnd);
			
			// TODO manage property parameters type
			// If the parameter comes from a property, a double apix has to be added
			// but we have to pay attention to recognize property parameters and filter 
			// conditions
			
			if(!namePar.trim().equalsIgnoreCase(pname)) 
				continue;
			newQuery = newQuery.substring(0, index) + 
					   pvalue + 
					   newQuery.substring(indexEnd+1, newQuery.length());	
		}
		// return query
		return newQuery;
	}
}
