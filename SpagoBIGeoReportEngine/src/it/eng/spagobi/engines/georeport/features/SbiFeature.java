/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
