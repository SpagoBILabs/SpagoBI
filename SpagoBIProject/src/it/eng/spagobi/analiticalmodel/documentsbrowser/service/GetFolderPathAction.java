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

*/
package it.eng.spagobi.analiticalmodel.documentsbrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.chiron.serializer.FoldersJSONSerializer;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;
import it.eng.spagobi.utilities.service.JSONFailure;
import it.eng.spagobi.utilities.service.JSONSuccess;


/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public class GetFolderPathAction extends AbstractBaseHttpAction{
	
	// REQUEST PARAMETERS
	public static final String FOLDER_ID = "folderId";
	public static final String ROOT_FOLDER_ID = "rootFolderId";
	
	public static final String ROOT_NODE_ID = "rootNode";
	
	// logger component
	private static Logger logger = Logger.getLogger(GetFolderPathAction.class);
	
	public void service(SourceBean request, SourceBean response) throws Exception {
		
		List functionalities = new ArrayList();
		
		logger.debug("IN");
		
		try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );
			
			String functID = getAttributeAsString(FOLDER_ID);
			String rootFolderID = getAttributeAsString(ROOT_FOLDER_ID);	
			
			logger.debug("Parameter [" + FOLDER_ID + "] is equal to [" + functID + "]");
			logger.debug("Parameter [" + ROOT_FOLDER_ID + "] is equal to [" + rootFolderID + "]");
			
			if (functID == null || functID.equalsIgnoreCase(ROOT_NODE_ID)){
				//getting default folder (root)
				LowFunctionality rootFunct = DAOFactory.getLowFunctionalityDAO().loadRootLowFunctionality(false);
				functionalities.add(rootFunct);
			} else {
				functionalities = DAOFactory.getLowFunctionalityDAO()
					.loadParentFunctionalities(Integer.valueOf(functID), (rootFolderID==null?null:Integer.valueOf(rootFolderID)) );	
			}
			
			HttpServletRequest httpRequest = getHttpRequest();
			MessageBuilder m = new MessageBuilder();
			Locale locale = m.getLocale(httpRequest);
			JSONArray foldersJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( functionalities,locale );
			
			try {
				writeBackToClient( new JSONSuccess(  createJSONResponse(foldersJSON) ) ) ;
			} catch (IOException e) {
				throw new SpagoBIException("Impossible to write back the responce to the client", e);
			}
			
		} catch (Throwable t) {
			// TODO set up spago's trap mechanism and move this error handling code to a specialized action 
			String message = "Impossible to write back the responce to the client";
			logger.error(message, t);
			writeBackToClient( new JSONFailure( new SpagoBIEngineServiceException(getActionName(), message, t) ) );
			//throw new SpagoBIException("An unexpected error occured while executing " + getActionName(), t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	/**
	 * Creates a json array with parents folder informations
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONArray createJSONResponse(JSONArray rows) throws JSONException {
		JSONObject node;
		JSONArray nodes;

		nodes = new JSONArray();
		
		for (int i=rows.length()-1; i>=0; i--){
			JSONObject tmpNode = rows.getJSONObject(i);
			node = new JSONObject();
			node.put("id", tmpNode.get(FoldersJSONSerializer.ID));
			node.put("name", tmpNode.get(FoldersJSONSerializer.NAME));
			node.put("path", tmpNode.get(FoldersJSONSerializer.PATH));
			
			nodes.put(node);
		}
		return nodes;
	}
	
}
