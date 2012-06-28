/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
