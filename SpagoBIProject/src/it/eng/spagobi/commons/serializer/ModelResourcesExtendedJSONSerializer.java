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
package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelResources;
import it.eng.spagobi.kpi.model.bo.ModelResourcesExtended;
import it.eng.spagobi.kpi.model.bo.Resource;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

public class ModelResourcesExtendedJSONSerializer implements Serializer {

	private static final String RESOURCE_ID = "resourceId";
	private static final String RESOURCE_NAME = "resourceName";
	private static final String RESOURCE_CODE = "resourceCode";
	private static final String RESOURCE_TYPE = "resourceType";
	private static final String MODEL_INST_ID = "modelInstId";
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof ModelResourcesExtended) ) {
			throw new SerializationException("ModelResourcesExtendedJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			ModelResourcesExtended modelResource = (ModelResourcesExtended)o;
			result = new JSONObject();

			Integer mrId = modelResource.getResourceId();
			Resource resource  = DAOFactory.getResourceDAO().loadResourceById(mrId);
			result.put(RESOURCE_NAME, modelResource.getResourceName());
			result.put(RESOURCE_CODE, modelResource.getResourceCode());
			result.put(RESOURCE_TYPE, modelResource.getResourceType());
			result.put(RESOURCE_ID, modelResource.getResourceId());
			result.put(MODEL_INST_ID, modelResource.getModelInstId());
	
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
