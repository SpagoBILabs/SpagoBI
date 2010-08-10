package it.eng.spagobi.tools.dataset.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageDatasets extends AbstractSpagoBIAction {

	// logger component
	private static Logger logger = Logger.getLogger(ManageDatasets.class);
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String DATASETS_LIST = "DATASETS_LIST";
	private final String DATASET_INSERT = "DATASET_INSERT";
	private final String DATASET_DELETE = "DATASET_DELETE";
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 16;

	@Override
	public void doService() {
		logger.debug("IN");
		IDataSetDAO dsDao;
		try {
			dsDao = DAOFactory.getDataSetDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		if (serviceType != null && serviceType.equalsIgnoreCase(DATASETS_LIST)) {
			
			try {		
				
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}

				Integer totalItemsNum = dsDao.countDatasets();
				List items = dsDao.loadPagedDatasetList(start,limit);
				logger.debug("Loaded items list");
				JSONArray itemsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(items, locale);
				JSONObject responseJSON = createJSONResponse(itemsJSON, totalItemsNum);

				writeBackToClient(new JSONSuccess(responseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving items", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving items", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_INSERT)) {
			//TODO
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_DELETE)) {
			//TODO
		}else if(serviceType == null){
			//TODO
		}
		logger.debug("OUT");
	}

	/**
	 * Creates a json array with children users informations
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponse(JSONArray rows, Integer totalResNumber)
			throws JSONException {
		JSONObject results;

		results = new JSONObject();
		results.put("total", totalResNumber);
		results.put("title", "Datasets");
		results.put("rows", rows);
		return results;
	}
}
