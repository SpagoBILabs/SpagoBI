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

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeoFactory;
import org.mapfish.geo.MfGeometry;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class SbiFeatureFactory extends MfGeoFactory {
	
	private static SbiFeatureFactory instance;
	public static SbiFeatureFactory getInstance() {
		if(instance == null) {
			instance = new SbiFeatureFactory();
		}
		return instance;
	}
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(SbiFeatureFactory.class);
    
	
	private SbiFeatureFactory() {}
	
	public MfFeature createFeature(final String id, final MfGeometry geometry, final JSONObject properties) {
		MfFeature feature;
		
		logger.debug("IN");
		
		feature = null;
		
		try {				
			feature = new SbiFeature(id, geometry, properties);
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to create a new SbiFeature for [" + id + "], ["+ geometry+ "], [" + properties+ "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return feature;
	}
}
