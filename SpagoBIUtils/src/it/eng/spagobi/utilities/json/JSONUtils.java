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
package it.eng.spagobi.utilities.json;

import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JSONUtils {
	public static List asList(JSONArray array) {
		List list;
		
		if(array == null) {
			throw new IllegalArgumentException();
		}
		list = new ArrayList();
		
		for(int i = 0; i < array.length(); i++) {
			try {
				list.add( array.get(i) );
			} catch (JSONException e) {
				Assert.assertUnreachable("An out of bound error here is a signal of an internal JSON.org's bug");
			}
		}
		
		return list;
	}
	
	public static String[] asStringArray(JSONArray jSONArray) throws JSONException {
		if (jSONArray == null) {
			return null;
		}
		int length = jSONArray.length();
		String[] toReturn = new String[jSONArray.length()];
		for (int i = 0; i < length; i++) {
			toReturn[i] = jSONArray.getString(i).toString();
		}
		return toReturn;
	}
	
	public static JSONArray asJSONArray(String[] stringArray) throws JSONException {
		if (stringArray == null) {
			return null;
		}
		int length = stringArray.length;
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < length; i++) {
			toReturn.put(stringArray[i]);
		}
		return toReturn;
	}
}
