package it.eng.spagobi.commons.domains;

import it.eng.spago.base.Constants;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.serializer.DomainJSONSerializer;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
@Path("/domains")
public class DomainCRUD {
	
	private static final String DOMAIN_TYPE = "DOMAIN_TYPE";
	
	@GET
	@Path("/listValueDescriptionByType")
	@Produces(MediaType.APPLICATION_JSON)
	public String getListDomainsByType(@Context HttpServletRequest req){
		IDomainDAO domaindao = null;
		List<Domain> dialects = null;
		
		String language = (String) req.getSession().getAttribute(Constants.USER_LANGUAGE);
		String country = (String) req.getSession().getAttribute(Constants.USER_COUNTRY);
		Locale locale =  Locale.UK;
		if(language!=null){
			if(country==null && language!=null){
				locale = new Locale(language);
			}else{
				new Locale(language, country);
			}
		}

		JSONArray dialectsJSONArray = new JSONArray();
		
		String type= (String)req.getParameter(DOMAIN_TYPE);
		JSONObject datasorcesJSON= new JSONObject();
		try {
			
			domaindao = DAOFactory.getDomainDAO();
			dialects = domaindao.loadListDomainsByType(type);
			dialectsJSONArray= translate(dialects,locale);
			
		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while instatiating the dao", t);			
		}
		return dialectsJSONArray.toString();

	}
	
	private JSONArray translate(List<Domain> domains, Locale locale) throws JSONException{
		JSONArray dialectsJSONArray = new JSONArray();
		if(domains!=null){
			for(int i=0; i<domains.size(); i++){
				JSONObject domain = new JSONObject();
				domain.put(DomainJSONSerializer.VALUE_NAME,domains.get(i).getTranslatedValueName(locale));
				domain.put(DomainJSONSerializer.VALUE_DECRIPTION,domains.get(i).getTranslatedValueDescription(locale));
				domain.put(DomainJSONSerializer.VALUE_ID,domains.get(i).getValueId());
				dialectsJSONArray.put(domain);
			}
		}
		return dialectsJSONArray;
	}
}
