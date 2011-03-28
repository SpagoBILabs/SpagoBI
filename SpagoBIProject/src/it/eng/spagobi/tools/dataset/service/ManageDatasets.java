package it.eng.spagobi.tools.dataset.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.HashMap;
import java.util.Iterator;
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
	private final String DATASETS_FOR_KPI_LIST = "DATASETS_FOR_KPI_LIST";
	
	private final String CATEGORY_DOMAIN_TYPE = "CATEGORY_TYPE";
	private final String SCRIPT_TYPE = "SCRIPT_TYPE";
	private final String DATA_SET_TYPE = "DATA_SET_TYPE";	
	private final String TRANSFORMER_TYPE = "TRANSFORMER_TYPE";		
	
	public static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String LABEL = "label";
	
	private static final String CATEGORY_TYPE_CD = "catTypeCd";
		
	private static final String PARS = "pars";
	private static final String METADATA = "meta";
	
	private static final String DS_TYPE_CD = "dsTypeCd";
	private static final String FILE_NAME = "fileName";
	private static final String QUERY = "query";
	private static final String DATA_SOURCE = "dataSource";
	private static final String WS_ADDRESS = "wsAddress";
	private static final String WS_OPERATION = "wsOperation";
	private static final String SCRIPT = "script";
	private static final String SCRIPT_LANGUAGE = "scriptLanguage";
	private static final String JCLASS_NAME = "jclassName";
	
	private static final String TRASFORMER_TYPE_CD = "trasfTypeCd";
	private static final String PIVOT_COL_NAME = "pivotColName";
	private static final String PIVOT_COL_VALUE = "pivotColValue";
	private static final String PIVOT_ROW_NAME = "pivotRowName";
	private static final String PIVOT_IS_NUM_ROWS = "pivotIsNumRows";
	
	private static final String DS_WS = "SbiWSDataSet";
	private static final String DS_FILE = "SbiFileDataSet";
	private static final String DS_JCLASS = "SbiJClassDataSet";
	private static final String DS_QUERY = "SbiQueryDataSet";
	private static final String DS_SCRIPT = "SbiScriptDataSet";
	private static final String DS_JSON = "SbiJsonDataSet";
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 14;

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
		
		if (serviceType != null && serviceType.equalsIgnoreCase(DATASETS_FOR_KPI_LIST)) {
			
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
				List items = dsDao.loadPagedSbiDatasetConfigList(start,limit);
				logger.debug("Loaded items list");
				JSONArray itemsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(items, locale);
				JSONObject responseJSON = createJSONResponse(itemsJSON, totalItemsNum);

				writeBackToClient(new JSONSuccess(responseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving items", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving items", e);
			}
		} else if(serviceType != null && serviceType.equalsIgnoreCase(DATASETS_LIST)) {
			
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
				List items = dsDao.loadPagedIDatasetList(start,limit);
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
			
			String id = getAttributeAsString(ID);
			String label = getAttributeAsString(LABEL);
			String name = getAttributeAsString(NAME);
			String description = getAttributeAsString(DESCRIPTION);
			
			String dsTypeCd = getAttributeAsString(DS_TYPE_CD);
			String catTypeCd = getAttributeAsString(CATEGORY_TYPE_CD);		
			
			String pars = getAttributeAsString(PARS);
			String meta = getAttributeAsString(METADATA);			
			
			String trasfTypeCd = getAttributeAsString(TRASFORMER_TYPE_CD);
			
			List<Domain> domainsDs = (List<Domain>)getSessionContainer().getAttribute("dsTypesList");
			
			String dsType = "";
			if(domainsDs!=null && !domainsDs.isEmpty()){
				Iterator it = domainsDs.iterator();
				while(it.hasNext()){
					Domain d = (Domain)it.next();
					if(d!=null && d.getValueCd().equalsIgnoreCase(dsTypeCd)){
						dsType = d.getValueName();
						break;
					}
				}
			}			

			List<Domain> domainsCat = (List<Domain>)getSessionContainer().getAttribute("catTypesList");
			
			HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
		    if(domainsCat != null){
			    for(int i=0; i< domainsCat.size(); i++){
			    	domainIds.put(domainsCat.get(i).getValueCd(), domainsCat.get(i).getValueId());
			    }
		    }
		    Integer catTypeID = domainIds.get(catTypeCd);

			if (name != null && label != null && dsType!=null) {
				
				IDataSet ds = null;
				if(dsType.equalsIgnoreCase(DS_FILE)){
					ds = new FileDataSet();
					String fileName = getAttributeAsString(FILE_NAME);
					if(fileName!=null){
						((FileDataSet)ds).setFileName(fileName);
					}
				}else if(dsType.equalsIgnoreCase(DS_JCLASS)){
					ds = new JavaClassDataSet();
					String jclassName = getAttributeAsString(JCLASS_NAME);
					if(jclassName!=null){
						((JavaClassDataSet)ds).setClassName(jclassName);
					}
				}else if(dsType.equalsIgnoreCase(DS_QUERY)){
					ds = new JDBCDataSet();
					String query = getAttributeAsString(QUERY);
					String dataSourceLabel = getAttributeAsString(DATA_SOURCE);
					if(query!=null){
						((JDBCDataSet)ds).setQuery(query);
					}
					if(dataSourceLabel!=null){
						IDataSourceDAO dSourceDao;
						try {
							dSourceDao = DAOFactory.getDataSourceDAO();
						    IDataSource dataSource = dSourceDao.loadDataSourceByLabel(dataSourceLabel);						
							if(dataSource!=null){
								((JDBCDataSet)ds).setDataSource(dataSource);
							}
						} catch (EMFUserError e) {
							logger.error("Error in retrieving Datasource with label "+dataSourceLabel, e);
							e.printStackTrace();
						}
					}
				}else if(dsType.equalsIgnoreCase(DS_SCRIPT)){
					ds = new ScriptDataSet();
					String script = getAttributeAsString(SCRIPT);
					String scriptLanguage = getAttributeAsString(SCRIPT_LANGUAGE);
					if(scriptLanguage!=null){
						((ScriptDataSet)ds).setLanguageScript(scriptLanguage);
					}
					if(script!=null){
						((ScriptDataSet)ds).setScript(script);
					}
				}else if(dsType.equalsIgnoreCase(DS_WS)){
					ds = new WebServiceDataSet();
					String wsAddress = getAttributeAsString(WS_ADDRESS);
					String wsOperation = getAttributeAsString(WS_OPERATION);
					if(wsOperation!=null){
						((WebServiceDataSet)ds).setOperation(wsOperation);
					}
					if(wsAddress!=null){
						((WebServiceDataSet)ds).setAddress(wsAddress);
					}
				}else if(dsType.equalsIgnoreCase(DS_JSON)){
					//TODO
				}
				
				if(ds!=null){
					ds.setLabel(label);
					ds.setName(name);
					ds.setDsType(dsType);
					
					if(description != null){
						ds.setDescription(description);
					}
					
					if(meta != null){
						ds.setDsMetadata(meta);
					}
					
					if(pars != null){
						ds.setParameters(pars);
					}
					
					if(catTypeID!=null){
						ds.setCategoryCd(catTypeCd);
						ds.setCategoryId(catTypeID);
					}
					
					if(trasfTypeCd!=null){
					    List<Domain> domainsTrasf = (List<Domain>)getSessionContainer().getAttribute("trasfTypesList");
					    HashMap<String, Integer> domainTrasfIds = new HashMap<String, Integer> ();
					    if(domainsTrasf != null){
						    for(int i=0; i< domainsTrasf.size(); i++){
						    	domainTrasfIds.put(domainsTrasf.get(i).getValueCd(), domainsTrasf.get(i).getValueId());
						    }
					    }
					    Integer transformerId = domainTrasfIds.get(trasfTypeCd);
					    ds.setTransformerId(transformerId);
					    ds.setTransformerCd(trasfTypeCd);
						
					    String pivotColName = getAttributeAsString(PIVOT_COL_NAME);
						String pivotColValue = getAttributeAsString(PIVOT_COL_VALUE);
						String pivotRowName = getAttributeAsString(PIVOT_ROW_NAME);
						Boolean pivotIsNumRows = getAttributeAsBoolean(PIVOT_IS_NUM_ROWS);
						
						if(pivotColName != null){
							ds.setPivotColumnName(pivotColName);
						}
						if(pivotColValue != null){
							ds.setPivotColumnValue(pivotColValue);
						}
						if(pivotRowName != null){
							ds.setPivotRowName(pivotRowName);
						}	
						if(pivotIsNumRows != null){
						    ds.setNumRows(pivotIsNumRows);
						}
					}

					try {
						if(id != null && !id.equals("") && !id.equals("0")){							
							ds.setId(Integer.valueOf(id));
							dsDao.modifyDataSet(ds);
							logger.debug("Resource "+id+" updated");
							JSONObject attributesResponseSuccessJSON = new JSONObject();
							attributesResponseSuccessJSON.put("success", true);
							attributesResponseSuccessJSON.put("responseText", "Operation succeded");
							attributesResponseSuccessJSON.put("id", id);
							writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
						}else{
							Integer dsID = dsDao.insertDataSet(ds);
							logger.debug("New Resource inserted");
							JSONObject attributesResponseSuccessJSON = new JSONObject();
							attributesResponseSuccessJSON.put("success", true);
							attributesResponseSuccessJSON.put("responseText", "Operation succeded");
							attributesResponseSuccessJSON.put("id", dsID);
							writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
						}
	
					} catch (Throwable e) {
						logger.error(e.getMessage(), e);
						throw new SpagoBIServiceException(SERVICE_NAME,
								"Exception occurred while saving new resource", e);
					}				
				}else{
					logger.error("DataSet type is not existent");
					throw new SpagoBIServiceException(SERVICE_NAME,	"Please change DataSet Type");
				}
			}else{
				logger.error("DataSet name, label or type are missing");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please fill resource name, label and type");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_DELETE)) {
			Integer dsID = getAttributeAsInteger(ID);
			try {
				dsDao.deleteDataSet(dsID);
				logger.debug("Dataset deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving dataset to delete", e);
			}
		}else if(serviceType == null){
			try {
				List dsTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(DATA_SET_TYPE);
				getSessionContainer().setAttribute("dsTypesList", dsTypesList);				
				List catTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(CATEGORY_DOMAIN_TYPE);
				getSessionContainer().setAttribute("catTypesList", catTypesList);
				List dataSourceList = DAOFactory.getDataSourceDAO().loadAllDataSources();			
				getSessionContainer().setAttribute("dataSourceList", dataSourceList);
				List scriptLanguageList = DAOFactory.getDomainDAO().loadListDomainsByType(SCRIPT_TYPE);
				getSessionContainer().setAttribute("scriptLanguageList", scriptLanguageList);	
				List trasfTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(TRANSFORMER_TYPE);
				getSessionContainer().setAttribute("trasfTypesList", trasfTypesList);	
			} catch (EMFUserError e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception retrieving resources types", e);
			}
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
