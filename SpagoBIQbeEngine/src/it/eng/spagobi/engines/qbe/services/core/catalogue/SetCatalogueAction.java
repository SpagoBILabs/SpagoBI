/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.services.core.catalogue;

import it.eng.qbe.query.Query;
import it.eng.qbe.query.QueryMeta;
import it.eng.qbe.query.serializer.SerializerFactory;
import it.eng.qbe.serializer.SerializationException;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Commit all the modifications made to the catalogue on the client side
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SetCatalogueAction extends AbstractQbeEngineAction {
	
	public static final String SERVICE_NAME = "SET_CATALOGUE_ACTION";
	public String getActionName(){return SERVICE_NAME;}
	
	// INPUT PARAMETERS
	public static final String CATALOGUE = "catalogue";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(SetCatalogueAction.class);
    
	
	public void service(SourceBean request, SourceBean response)  {				
		
		String jsonEncodedCatalogue = null;
		JSONArray queries;
		JSONObject itemJSON;
		JSONObject queryJSON;
		JSONObject metaJSON;
		Query query;
		QueryMeta meta;
		
		logger.debug("IN");
		
		try {
		
			super.service(request, response);		
			
			jsonEncodedCatalogue = getAttributeAsString( CATALOGUE );			
			logger.debug(CATALOGUE + " = [" + jsonEncodedCatalogue + "]");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			Assert.assertNotNull(jsonEncodedCatalogue, "Input parameter [" + CATALOGUE + "] cannot be null in oder to execute " + this.getActionName() + " service");
			
			
			try {		
				queries = new JSONArray( jsonEncodedCatalogue );
				for(int i = 0; i < queries.length(); i++) {					
					queryJSON = queries.getJSONObject(i);
					query = deserializeQuery(queryJSON);
					
					getEngineInstance().getQueryCatalogue().addQuery(query);
					getEngineInstance().resetActiveQuery();
				}				
				
			} catch (SerializationException e) {
				String message = "Impossible to syncronize the query with the server. Query passed by the client is malformed";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
		
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			logger.debug("OUT");
		}
		
		
	}


	private QueryMeta deserializeMeta(JSONObject metaJSON) throws JSONException {
		QueryMeta meta = new QueryMeta();
		meta.setId( metaJSON.getString("id") );
		meta.setName( metaJSON.getString("name") );
		return null;
	}


	private Query deserializeQuery(JSONObject queryJSON) throws SerializationException, JSONException {
		//queryJSON.put("expression", queryJSON.get("filterExpression"));
		return SerializerFactory.getDeserializer("application/json").deserializeQuery(queryJSON.toString(), getEngineInstance().getDataSource());
	}

}
