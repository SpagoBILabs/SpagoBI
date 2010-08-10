/**
 * 
 */
package it.eng.spagobi.chiron;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.chiron.serializer.EngineJSONSerializer;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;
import it.eng.spagobi.utilities.service.JSONSuccess;


/**
 * @author Andtra Gioia (andrea.gioia@eng.it)
 *
 */
public class ListEnginesAction extends AbstractBaseHttpAction{
	
	// logger component
	private static Logger logger = Logger.getLogger(ListEnginesAction.class);
	
	public void service(SourceBean request, SourceBean response) throws Exception {
		
		List engines;
		
		logger.debug("IN");
		
		try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );
			
			engines = DAOFactory.getEngineDAO().loadAllEngines();
			JSONArray enginesJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( engines,null );
			
			try {
				writeBackToClient( new JSONSuccess( createJSONResponse(enginesJSON) ) );
			} catch (IOException e) {
				throw new SpagoBIException("Impossible to write back the responce to the client", e);
			}
		} catch (Throwable t) {
			throw new SpagoBIException("An unexpected error occured while executing LIST_ENGINES_ACTION", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private JSONObject createJSONResponse(JSONArray rows) throws JSONException {
		JSONObject results;
		JSONObject metadata;
		JSONArray fields;
		JSONObject field;
		
		results = new JSONObject();
		metadata = new JSONObject();
		fields = new JSONArray();
		
		metadata.put("fields", fields);
		results.put("metaData", metadata);
		results.put("rows", rows);
		
		// create metadata
		field = new JSONObject();
		field.put("dataIndex", EngineJSONSerializer.LABEL);
		field.put("name", "Label");
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", EngineJSONSerializer.NAME);
		field.put("name", "Name");
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", EngineJSONSerializer.DESCRIPTION);
		field.put("name", "Description");
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", EngineJSONSerializer.DOCUMENT_TYPE);
		field.put("name", "DocumentType");
		fields.put(field);
		
		return results;
	}

}
