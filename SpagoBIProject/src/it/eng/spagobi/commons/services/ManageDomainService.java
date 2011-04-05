package it.eng.spagobi.commons.services;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.kpi.model.dao.IModelInstanceDAO;
import it.eng.spagobi.kpi.model.dao.IModelResourceDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;


public class ManageDomainService extends AbstractSpagoBIAction {

	/**
	 * 
	 */
	 private static final long serialVersionUID = 1L;

	// logger component
	private static Logger logger = Logger.getLogger(ManageDomainService.class);

	//Service parameter
	private final String MESSAGE_DET = "MESSAGE_DET";
	
	private static final String DOMAIN_LIST = "DOMAIN_LIST";
	private static final String DOMAIN_DELETE = "DOMAIN_DELETE";
	private static final String DOMAIN_SAVE = "DOMAIN_SAVE";	
	
	@Override
	public void doService() {
		logger.debug("IN");
		IDomainDAO domainDao;
		try {
			domainDao = DAOFactory.getDomainDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		
		if (serviceType != null && serviceType.equalsIgnoreCase(DOMAIN_LIST)) {

			try {				
				List<Domain> domainList = domainDao.loadListDomains();
				logger.debug("Loaded domain list");
				JSONArray domainListJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(domainList,locale);
				writeBackToClient(new JSONSuccess(domainListJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving domain data", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving domain data", e);
			}
		}else if (serviceType != null && serviceType.equalsIgnoreCase(DOMAIN_DELETE)) {
			try {	
				String codeDomain =null; // da leggere da request
				String codeValue=null;
				domainDao.delete(codeDomain, codeValue);
				writeBackToClient(new JSONAcknowledge("Operation succeded"));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving domain data", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving domain data", e);
			}			
		}else if (serviceType != null && serviceType.equalsIgnoreCase(DOMAIN_SAVE)) {
			try {	
				String codeDomain =null; // da leggere da request
				String codeValue=null;
				//domainDao.saveDomain(codeDomain, codeValue);
				writeBackToClient(new JSONAcknowledge("Operation succeded"));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving domain data", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving domain data", e);
			}			
		}
		
		
		logger.debug("OUT");

	}

}
