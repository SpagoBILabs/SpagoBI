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
import it.eng.spagobi.utilities.service.JSONFailure;
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
		IDomainDAO domainDao;
		String serviceType;
		
		logger.debug("IN");
		
		try {
			domainDao = DAOFactory.getDomainDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		

		serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Parameter [" + MESSAGE_DET +"] is equal to [" + serviceType + "]");
		
		if(serviceType != null) {
			if (serviceType.equalsIgnoreCase(DOMAIN_LIST)) {
				 doDomainList();
			}else if (serviceType.equalsIgnoreCase(DOMAIN_DELETE)) {
				doDelete();
			}else if (serviceType.equalsIgnoreCase(DOMAIN_SAVE)) {
				doSave();
			} else {
				throw new SpagoBIServiceException(SERVICE_NAME,	"Unable to execute service [" + serviceType + "]");
			}
		}
		
		
		logger.debug("OUT");

	}
	
	public void doSave() {
		try {	
			//Recupero le informazioni della richiesta
			
			//poi chiamo le set dei vari parametri dell'oggetto domain (meglio creare un metodo dedicato?)
			
			//Salvo su db
			
			
			//domainDao.saveDomain(codeDomain, codeValue);
			writeBackToClient(new JSONAcknowledge("Operation succeded"));
			//writeBackToClient(new JSONFailure(new SpagoBIServiceException(SERVICE_NAME, "Qualcosa è andato male!")));

		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving domain data", e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Exception occurred while retrieving domain data", e);
		}		
	}
	
	public void doDelete() {
		try {	
			//domainDao.delete(codeDomain, codeValue);
			writeBackToClient(new JSONAcknowledge("Operation succeded"));

		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving domain data", e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Exception occurred while retrieving domain data", e);
		}	
	}
	
	public void doDomainList() {
		try {				
//			List<Domain> domainList = domainDao.loadListDomains();
//			logger.debug("Loaded domain list");
//			JSONArray domainListJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(domainList,locale);
//			writeBackToClient(new JSONSuccess(domainListJSON));
			writeBackToClient(new JSONAcknowledge("Operation succeded"));
		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving domain data", e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Exception occurred while retrieving domain data", e);
		}
	}
	
	

}
