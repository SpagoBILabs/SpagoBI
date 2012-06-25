/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
