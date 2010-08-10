/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.georeport.features;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class SbiFeature extends MfFeature {
	
	private String id;
	private MfGeometry geometry;
	private JSONObject properties;
	
	SbiFeature(final String id, final MfGeometry geometry, final JSONObject properties) {
		this.id = id;
		this.geometry = geometry;
		this.properties = properties;
	}
	
	public String getFeatureId() {
		return id;
    }
    
	public MfGeometry getMfGeometry() {
		return geometry;
    }
    
	public void toJSON(JSONWriter builder) throws JSONException {
		Iterator iter = properties.keys();
		while (iter.hasNext()){
			String key = (String) iter.next();
            Object value = properties.get(key);
            builder.key(key).value(value);                    	  
        }
    }
	
	public JSONObject getProperties() {
		return properties;
	}
}
