/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.model.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ModelViewEntityDescriptor implements IModelViewEntityDescriptor {
	JSONObject viewJSON;
	
	public ModelViewEntityDescriptor(JSONObject viewJSON) {
		this.viewJSON = viewJSON;
	}

	public Set<String> getInnerEntityUniqueNames() {
		Set<String> innerEntityUniqueNames = new HashSet<String>();
		
		try {
			JSONArray tables = viewJSON.optJSONArray("tables");
			for(int i = 0; i < tables.length(); i++) {
				JSONObject table = tables.getJSONObject(i);
				String tableName = table.optString("name");
				String tableType = table.optString("package");
				innerEntityUniqueNames.add(tableType + "." + tableName + "::" + tableName);
			}
		} catch(Throwable t) {
			throw new RuntimeException("Impossible to read inner entities name from conf file", t);
		}
		
		return innerEntityUniqueNames;
	}

	public String getName() {
		String name = viewJSON.optString("name");
		Assert.assertNotNull("View name cannot be null", name);
		return name;
	}

	public String getType() {
		JSONArray tables = viewJSON.optJSONArray("tables");
	
		String pkg = null;
		try {
			pkg = tables.getJSONObject(0).optString("package");
		} catch (JSONException e) {
			throw new RuntimeException("Package attribute cannot be null", e);
		}
		
		return pkg + "." + getName();
	}
}
