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
package it.eng.spagobi.engines.qbe.services.formbuilder;
		
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.qbe.commons.serializer.SerializationException;
import it.eng.qbe.commons.serializer.SerializerFactory;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.json.QuerySerializationConstants;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetSelectedColumnsAction  extends AbstractQbeEngineAction {	

	// INPUT PARAMETERS
	public static final String QUERY_ID = "queryId";
	
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GetSelectedColumnsAction.class);
   
	public void service(SourceBean request, SourceBean response)  {				
	
		String queryId;
		Query query;
		JSONObject queryJSON;
		JSONArray fieldsJSON;
		JSONObject resultsJSON;
		
		logger.debug("IN");
		
		try {		
			super.service(request, response);	
			
			queryId = getAttributeAsString( QUERY_ID );
			logger.debug("Parameter [" + QUERY_ID + "] is equals to [" + queryId + "]");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			// get query
			if(queryId != null) {
				logger.debug("Loading query [" + queryId + "] from catalogue");
				query = getEngineInstance().getQueryCatalogue().getQuery(queryId);
				Assert.assertNotNull(query, "Query object with id [" + queryId + "] does not exist in the catalogue");
			} else {
				logger.debug("Loading active query");
				query = getEngineInstance().getActiveQuery();
				logger.warn("Active query not available");
				logger.debug("Loading first query from catalogue");
				query = getEngineInstance().getQueryCatalogue().getFirstQuery();
				Assert.assertNotNull(query, "Query catalogue is empty");
			}
			logger.debug("Query [" + query.getId() + "] succesfully loaded");
			
			
			// serialize query
			try {
				queryJSON = (JSONObject)SerializerFactory.getSerializer("application/json").serialize(query, getEngineInstance().getDatamartModel(), getLocale());
			} catch (SerializationException e) {
				throw new SpagoBIEngineServiceException(getActionName(), "Cannot serialize query [" + query.getId() + "]", e);
			}
			
			
			fieldsJSON = queryJSON.getJSONArray(QuerySerializationConstants.FIELDS);			
			resultsJSON = new JSONObject();
			resultsJSON.put("results", fieldsJSON);
			
			try {
				writeBackToClient( new JSONSuccess( resultsJSON ) );
			} catch (IOException e) {
				throw new SpagoBIEngineServiceException(getActionName(), "Impossible to write back the responce to the client [" + resultsJSON.toString(2)+ "]", e);
			}
			
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {			
			logger.debug("OUT");
		}	
	}
}
