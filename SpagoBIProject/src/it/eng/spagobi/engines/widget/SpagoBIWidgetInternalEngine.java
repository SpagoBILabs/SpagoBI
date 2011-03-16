/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.widget;


import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;


public class SpagoBIWidgetInternalEngine extends SpagoBIAbstractInternalEngine {

	private static transient Logger logger = Logger.getLogger(SpagoBIWidgetInternalEngine.class);

	public void doExecute() {
		JSONObject jsonResponse;
		IDataSet  dataSet = getDataSet();
		IDataStore dataStore;
		
		try {
			
			jsonResponse = new JSONObject();
			
			// get data
			dataSet.setParamsMap(new HashMap());
			dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( getUserProfile() ));
			dataSet.loadData();
			dataStore = dataSet.getDataStore();
			Object resultNumber = dataStore.getMetaData().getProperty("resultNumber");
			if(resultNumber == null) dataStore.getMetaData().setProperty("resultNumber", new Integer(0));
			
			JSONDataWriter dataStoreWriter = new JSONDataWriter();
			JSONObject data = (JSONObject)dataStoreWriter.write(dataStore);
			
			jsonResponse.put("data", data);
			
			// get metadata			
			JSONObject metadata = new JSONObject();
			metadata.put("name", getObj().getName());
			metadata.put("description", getObj().getDescription());
			metadata.put("owner", getObj().getCreationUser());
			metadata.put("template", getTemplateAsJSONObject());
			
			jsonResponse.put("metadata", metadata);
			
			// generate response
			getResponse().setAttribute("content", jsonResponse.toString());
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "WIDGET_ENGINE_JSON_RESPONSE");
			
			String executionId = (String) requestContainer.getServiceRequest().getAttribute("SBI_EXECUTION_ID");
			if (executionId != null) response.setAttribute("SBI_EXECUTION_ID", executionId);
			response.setAttribute(ObjectsTreeConstants.SESSION_OBJ_ATTR, obj);
			
		} catch(Throwable t) {
			throw new  SpagoBIEngineRuntimeException(t);	
		}
		
	}
	
	public void doExecuteSubObject() {
		throw new  SpagoBIEngineRuntimeException("Unsupported functionality");	
		
	}

	public void doCreateDocumentTemplate() {
		throw new  SpagoBIEngineRuntimeException("Unsupported functionality");	
	}

	public void doModifyDocumentTemplate() {
		throw new  SpagoBIEngineRuntimeException("Unsupported functionality");		
	}
}




