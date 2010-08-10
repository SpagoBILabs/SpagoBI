/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.services.core.catalogue;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import it.eng.qbe.catalogue.QueryCatalogue;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.QuerySerializerFactory;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * This action is responsible to retrieve the first query contained into the catalog.
 * It is used for Read-only users (a Read-only user is able only to execute the first query of the catalog)
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 * 
 * @deprecated use GetQueryAction?SEARCH_TYPE=byType&SEARCH_FILTER=first
 */
public class GetFirstQueryAction extends AbstractQbeEngineAction {
	
	public static final String SERVICE_NAME = "GET_FIRST_QUERY_ACTION";
	public String getActionName(){return SERVICE_NAME;}

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(GetFirstQueryAction.class);
    
	
	public void service(SourceBean request, SourceBean response) {
		
		logger.debug("IN");
		
		try {
			super.service(request, response);	
			// retrieve first query from catalog
			QbeEngineInstance engineInstance = getEngineInstance();
			QueryCatalogue queryCatalogue = engineInstance.getQueryCatalogue();
			Query query = queryCatalogue.getFirstQuery();
			// serialize query
			JSONObject queryJSON = (JSONObject)QuerySerializerFactory.getSerializer("application/json").serialize(query, getEngineInstance().getDatamartModel(), getLocale());
			
			try {
				writeBackToClient( new JSONSuccess(queryJSON) );
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

}
