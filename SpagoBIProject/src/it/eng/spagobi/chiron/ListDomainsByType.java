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
import it.eng.spagobi.chiron.serializer.DomainJSONSerializer;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.service.AbstractBaseHttpAction;
import it.eng.spagobi.utilities.service.JSONSuccess;


/**
 * @author Andtra Gioia (andrea.gioia@eng.it)
 *
 */
public class ListDomainsByType extends AbstractBaseHttpAction {
	
	// request parameters
	public static final String DOMAIN_TYPE = "DOMAIN_TYPE";
	
	// logger component
	private static Logger logger = Logger.getLogger(ListDomainsByType.class);

	public void service(SourceBean request, SourceBean response) throws Exception {
		
		String domainType;
		List domains;
		
		logger.debug("IN");
		
		try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );
			
			domainType = getAttributeAsString( DOMAIN_TYPE );
			logger.debug("Parameter [" + DOMAIN_TYPE + "] is equal to [" + domainType + "]");
			
			domains = DAOFactory.getDomainDAO().loadListDomainsByType( domainType );
			JSONArray domainsJSON = (JSONArray)SerializerFactory.getSerializer("application/json").serialize( domains ,null);
			
			try {
				writeBackToClient( new JSONSuccess( createJSONResponse(domainsJSON) ) );
			} catch (IOException e) {
				throw new SpagoBIException("Impossible to write back the responce to the client", e);
			}
		
		} catch (Throwable t) {
			throw new SpagoBIException("An unexpected error occured while executing LIST_DOMAINS_BY_TYPE_ACTION", t);
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
		field.put("dataIndex", DomainJSONSerializer.DOMAIN_CODE);
		field.put("name", "Domain Code"); // localize me please
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", DomainJSONSerializer.DOMAIN_NAME);
		field.put("name", "Domain Name"); // localize me please
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", DomainJSONSerializer.VALUE_CODE);
		field.put("name", "Value Code"); // localize me please
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", DomainJSONSerializer.VALUE_NAME);
		field.put("name", "Value Name"); // localize me please
		fields.put(field);
		
		field = new JSONObject();
		field.put("dataIndex", DomainJSONSerializer.VALUE_DECRIPTION);
		field.put("name", "Value Description"); // localize me please
		fields.put(field);
		
		return results;
	}

}
