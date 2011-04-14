/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.service;

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.catalogue.QueryCatalogue;
import it.eng.qbe.statement.QbeDatasetFactory;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.QbeDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
	private final String DATASET_TEST = "DATASET_TEST";
	
	private final String DATASET_VERSION_RESTORE = "DATASET_VERSION_RESTORE";
	private final String DATASET_VERSION_DELETE = "DATASET_VERSION_DELETE";
	private final String DATASET_ALL_VERSIONS_DELETE = "DATASET_ALL_VERSIONS_DELETE";
	
	private final String DATASETS_FOR_KPI_LIST = "DATASETS_FOR_KPI_LIST";
	
	private final String CATEGORY_DOMAIN_TYPE = "CATEGORY_TYPE";
	private final String SCRIPT_TYPE = "SCRIPT_TYPE";
	private final String DATA_SET_TYPE = "DATA_SET_TYPE";	
	private final String TRANSFORMER_TYPE = "TRANSFORMER_TYPE";		
	
	public static final String DS_ID = "dsId";
	public static final String VERSION_ID = "versId";
	public static final String VERSION_NUM = "versNum";
		
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
	private static final String DS_QBE = "SbiQbeDataSet";
	
	private static final String QBE_DATA_SOURCE = "qbeDataSource";
	private static final String QBE_DATAMARTS = "qbeDatamarts";
	private static final String QBE_JSON_QUERY = "qbeJSONQuery";
	private static final String QBE_SQL_QUERY = "qbeSQLQuery";
	
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
				List<SbiDataSetConfig> items = dsDao.loadPagedSbiDatasetConfigList(start,limit);
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
				List<GuiGenericDataSet> items = dsDao.loadPagedDatasetList(start,limit);
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
			
			JSONArray parsJSON = getAttributeAsJSONArray(PARS);
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

			if (name != null && label != null && dsType!=null && !dsType.equals("")) {
				
				GuiGenericDataSet ds = new GuiGenericDataSet();
				GuiDataSetDetail dsActiveDetail = null;
				
				if(dsType.equalsIgnoreCase(DS_FILE)){
					dsActiveDetail = new FileDataSetDetail();
					String fileName = getAttributeAsString(FILE_NAME);
					if(fileName!=null && !fileName.equals("")){
						((FileDataSetDetail)dsActiveDetail).setFileName(fileName);
					}
				}else if(dsType.equalsIgnoreCase(DS_JCLASS)){
					dsActiveDetail = new JClassDataSetDetail();
					String jclassName = getAttributeAsString(JCLASS_NAME);
					if(jclassName!=null && !jclassName.equals("")){
						((JClassDataSetDetail)dsActiveDetail).setJavaClassName(jclassName);
					}
				}else if(dsType.equalsIgnoreCase(DS_QUERY)){
					dsActiveDetail = new QueryDataSetDetail();
					String query = getAttributeAsString(QUERY);
					String dataSourceLabel = getAttributeAsString(DATA_SOURCE);
					if(query!=null && !query.equals("")){
						((QueryDataSetDetail)dsActiveDetail).setQuery(query);
					}
					if(dataSourceLabel!=null && !dataSourceLabel.equals("")){
						((QueryDataSetDetail)dsActiveDetail).setDataSourceLabel(dataSourceLabel);
					}
				}else if(dsType.equalsIgnoreCase(DS_QBE)){
					dsActiveDetail = new QbeDataSetDetail();
					String sqlQuery = getAttributeAsString(QBE_SQL_QUERY);
					String jsonQuery = getAttributeAsString(QBE_JSON_QUERY);
					String dataSourceLabel = getAttributeAsString(QBE_DATA_SOURCE);
					String datamarts = getAttributeAsString(QBE_DATAMARTS);
					((QbeDataSetDetail) dsActiveDetail).setSqlQuery(sqlQuery);
					((QbeDataSetDetail) dsActiveDetail).setJsonQuery(jsonQuery);
					((QbeDataSetDetail) dsActiveDetail).setDataSourceLabel(dataSourceLabel);
					((QbeDataSetDetail) dsActiveDetail).setDatamarts(datamarts);
				}else if(dsType.equalsIgnoreCase(DS_SCRIPT)){
					dsActiveDetail = new ScriptDataSetDetail();
					String script = getAttributeAsString(SCRIPT);
					String scriptLanguage = getAttributeAsString(SCRIPT_LANGUAGE);
					if(scriptLanguage!=null && !scriptLanguage.equals("")){
						((ScriptDataSetDetail)dsActiveDetail).setLanguageScript(scriptLanguage);
					}
					if(script!=null && !script.equals("")){
						((ScriptDataSetDetail)dsActiveDetail).setScript(script);
					}
				}else if(dsType.equalsIgnoreCase(DS_WS)){
					dsActiveDetail = new WSDataSetDetail();
					String wsAddress = getAttributeAsString(WS_ADDRESS);
					String wsOperation = getAttributeAsString(WS_OPERATION);
					if(wsOperation!=null && !wsOperation.equals("")){
						((WSDataSetDetail)dsActiveDetail).setOperation(wsOperation);
					}
					if(wsAddress!=null && !wsAddress.equals("")){
						((WSDataSetDetail)dsActiveDetail).setAddress(wsAddress);
					}
				}
				
				if(ds!=null){
					ds.setLabel(label);
					ds.setName(name);
					
					if(description != null && !description.equals("")){
						ds.setDescription(description);
					}
					
					if(dsActiveDetail!=null){
						dsActiveDetail.setDsType(dsType);
						
						if(meta != null && !meta.equals("")){
							dsActiveDetail.setDsMetadata(meta);
						}
						
						if(parsJSON != null){
							String pars;
							try {
								pars = deserializeParsListJSONArray(parsJSON);
								dsActiveDetail.setParameters(pars);
							} catch (JSONException e) {
								logger.error("Error in deserializing parameter",e);
								e.printStackTrace();
							} catch (SourceBeanException e) {
								logger.error("Source Bean Exception",e);
								e.printStackTrace();
							}
						}
						
						if(catTypeID!=null){
							dsActiveDetail.setCategoryCd(catTypeCd);
							dsActiveDetail.setCategoryId(catTypeID);
						}
						
						if(trasfTypeCd!=null && !trasfTypeCd.equals("")){
						    List<Domain> domainsTrasf = (List<Domain>)getSessionContainer().getAttribute("trasfTypesList");
						    HashMap<String, Integer> domainTrasfIds = new HashMap<String, Integer> ();
						    if(domainsTrasf != null){
							    for(int i=0; i< domainsTrasf.size(); i++){
							    	domainTrasfIds.put(domainsTrasf.get(i).getValueCd(), domainsTrasf.get(i).getValueId());
							    }
						    }
						    Integer transformerId = domainTrasfIds.get(trasfTypeCd);
						    dsActiveDetail.setTransformerId(transformerId);
						    dsActiveDetail.setTransformerCd(trasfTypeCd);
							
						    String pivotColName = getAttributeAsString(PIVOT_COL_NAME);
							String pivotColValue = getAttributeAsString(PIVOT_COL_VALUE);
							String pivotRowName = getAttributeAsString(PIVOT_ROW_NAME);
							Boolean pivotIsNumRows = getAttributeAsBoolean(PIVOT_IS_NUM_ROWS);
							
							if(pivotColName != null && !pivotColName.equals("")){
								dsActiveDetail.setPivotColumnName(pivotColName);
							}
							if(pivotColValue != null && !pivotColValue.equals("")){
								dsActiveDetail.setPivotColumnValue(pivotColValue);
							}
							if(pivotRowName != null && !pivotRowName.equals("")){
								dsActiveDetail.setPivotRowName(pivotRowName);
							}	
							if(pivotIsNumRows != null){
								dsActiveDetail.setNumRows(pivotIsNumRows);
							}
						}		
					  ds.setActiveDetail(dsActiveDetail);
					}			

					try {
						if(id != null && !id.equals("") && !id.equals("0")){							
							ds.setDsId(Integer.valueOf(id));
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
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please fill DataSet name, label and type");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_TEST)) {			
			try {
				JSONObject dataSetJSON = datasetTest();
				if(dataSetJSON!=null){
					try {
						writeBackToClient( new JSONSuccess( dataSetJSON ) );
					} catch (IOException e) {
						throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
					}
				}else{
					throw new SpagoBIServiceException(SERVICE_NAME,"DataSet Test has errors");
				}
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while Testing Dataset", e);
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
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_VERSION_DELETE)) {
			Integer dsVersionID = getAttributeAsInteger(VERSION_ID);
			try {
				boolean deleted = dsDao.deleteInactiveDataSetVersion(dsVersionID);
				logger.debug("Dataset Version deleted");
				if(deleted){
					writeBackToClient( new JSONAcknowledge("Operation succeded") );
				}else{
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception occurred while retrieving dataset Version to delete");
				}
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving dataset to delete", e);
			}
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_ALL_VERSIONS_DELETE)) {
			Integer dsID = getAttributeAsInteger(DS_ID);
			try {
				dsDao.deleteAllInactiveDataSetVersions(dsID);
				logger.debug("All Older Dataset versions deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving dataset to delete", e);
			}
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(DATASET_VERSION_RESTORE)) {
			Integer dsID = getAttributeAsInteger(DS_ID);
			Integer dsVersionNum = getAttributeAsInteger(VERSION_NUM);
			try {
				dsDao.restoreOlderDataSetVersion(dsID, dsVersionNum);
				logger.debug("Dataset Version correctly Restored");
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
						"Exception retrieving dataset types", e);
			}
		}
		logger.debug("OUT");
	}
	
	private JSONObject datasetTest() throws Exception{
		JSONObject dataSetJSON = null;
		
		String id = getAttributeAsString(ID);		
		String dsTypeCd = getAttributeAsString(DS_TYPE_CD);				
		JSONArray parsJSON = getAttributeAsJSONArray(PARS);
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

		IDataSet ds = null;			
		if ( dsType!=null && !dsType.equals("")) {
			
			if(dsType.equalsIgnoreCase(DS_FILE)){	
				ds = new FileDataSet();
				String fileName = getAttributeAsString(FILE_NAME);
				((FileDataSet)ds).setFileName(fileName);		
			}

			if(dsType.equalsIgnoreCase(DS_QUERY)){		
				ds=new JDBCDataSet();
				String query = getAttributeAsString(QUERY);
				String dataSourceLabel = getAttributeAsString(DATA_SOURCE);
				((JDBCDataSet)ds).setQuery(query);
				if(dataSourceLabel!=null && !dataSourceLabel.equals("")){
					IDataSource dataSource;
					try {
						dataSource = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dataSourceLabel);
						if(dataSource!=null){
							((JDBCDataSet)ds).setDataSource(dataSource);
						}
					} catch (EMFUserError e) {
						logger.error("Error while retrieving Datasource with label="+dataSourceLabel,e);
						e.printStackTrace();
					}			
				}
			}

			if(dsType.equalsIgnoreCase(DS_WS)){	
				ds=new WebServiceDataSet();
				String wsAddress = getAttributeAsString(WS_ADDRESS);
				String wsOperation = getAttributeAsString(WS_OPERATION);
				((WebServiceDataSet)ds).setAddress(wsAddress);
				((WebServiceDataSet)ds).setOperation(wsOperation);
			}

			if(dsType.equalsIgnoreCase(DS_SCRIPT)){	
				ds=new ScriptDataSet();
				String script = getAttributeAsString(SCRIPT);
				String scriptLanguage = getAttributeAsString(SCRIPT_LANGUAGE);
				((ScriptDataSet)ds).setScript(script);
				((ScriptDataSet)ds).setLanguageScript(scriptLanguage);
			}

			if(dsType.equalsIgnoreCase(DS_JCLASS)){		
				ds=new JavaClassDataSet();
				String jclassName = getAttributeAsString(JCLASS_NAME);
				((JavaClassDataSet)ds).setClassName(jclassName);
			}
			
			if(dsType.equalsIgnoreCase(DS_QBE)){
				
				String qbeDatamarts = getAttributeAsString(QBE_DATAMARTS);
				String dataSourceLabel = getAttributeAsString(QBE_DATA_SOURCE);
				String jsonQuery = getAttributeAsString(QBE_JSON_QUERY);
				
				IDataSource dataSource = null;
				try {
					dataSource = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dataSourceLabel);
				} catch (EMFUserError e) {
					logger.error("Error while retrieving Datasource with label " + dataSourceLabel, e);
					throw new SpagoBIServiceException(SERVICE_NAME, 
							"Error while retrieving Datasource with label " + dataSourceLabel, e);
				}
				
				it.eng.qbe.datasource.IDataSource qbeDataSource = getQbeDataSource(qbeDatamarts, dataSource);
				QueryCatalogue catalogue = getCatalogue(jsonQuery, qbeDataSource);
				Query query = catalogue.getFirstQuery();
				
				ds = QbeDatasetFactory.createDataSet(qbeDataSource.createStatement(query));

			}
		
			if(ds!=null){						
				
				if(trasfTypeCd!=null && !trasfTypeCd.equals("")){
				    List<Domain> domainsTrasf = (List<Domain>)getSessionContainer().getAttribute("trasfTypesList");
				    HashMap<String, Integer> domainTrasfIds = new HashMap<String, Integer> ();
				    if(domainsTrasf != null){
					    for(int i=0; i< domainsTrasf.size(); i++){
					    	domainTrasfIds.put(domainsTrasf.get(i).getValueCd(), domainsTrasf.get(i).getValueId());
					    }
				    }
				    Integer transformerId = domainTrasfIds.get(trasfTypeCd);
					
				    String pivotColName = getAttributeAsString(PIVOT_COL_NAME);
					String pivotColValue = getAttributeAsString(PIVOT_COL_VALUE);
					String pivotRowName = getAttributeAsString(PIVOT_ROW_NAME);
					Boolean pivotIsNumRows = getAttributeAsBoolean(PIVOT_IS_NUM_ROWS);
					
					if(pivotColName != null && !pivotColName.equals("")){
						ds.setPivotColumnName(pivotColName);
					}
					if(pivotColValue != null && !pivotColValue.equals("")){
						ds.setPivotColumnValue(pivotColValue);
					}
					if(pivotRowName != null && !pivotRowName.equals("")){
						ds.setPivotRowName(pivotRowName);
					}	
					if(pivotIsNumRows != null){
						ds.setNumRows(pivotIsNumRows);
					}
					
					ds.setTransformerId(transformerId);	

					if(ds.getPivotColumnName() != null 
							&& ds.getPivotColumnValue() != null
							&& ds.getPivotRowName() != null){
						ds.setDataStoreTransformer(
								new PivotDataSetTransformer(ds.getPivotColumnName(), ds.getPivotColumnValue(), ds.getPivotRowName(), ds.isNumRows()));
					}
				}
				HashMap h = new HashMap();
				if(parsJSON!=null){
					h = deserializeParValuesListJSONArray(parsJSON);
				}
				IEngUserProfile profile = getUserProfile();
				dataSetJSON = getDatasetTestResultList(ds, h, profile);					
			}							
		}else{
			logger.error("DataSet type is not existent");
			throw new SpagoBIServiceException(SERVICE_NAME,	"Please change DataSet Type");
		}
		return dataSetJSON;
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
	
	private HashMap deserializeParValuesListJSONArray(JSONArray parsListJSON) throws JSONException{
		HashMap h = new HashMap();
		for(int i=0; i< parsListJSON.length(); i++){
			JSONObject obj = (JSONObject)parsListJSON.get(i);
			String name = obj.getString("name");	
			String value = "'"+obj.getString("value")+"'";	
			h.put(name,value);
		}	
		return h;
	}
	
	private String deserializeParsListJSONArray(JSONArray parsListJSON) throws JSONException, SourceBeanException{
		String toReturn = "";
		SourceBean sb = new SourceBean("PARAMETERSLIST");	
		SourceBean sb1 = new SourceBean("ROWS");
		
		for(int i=0; i< parsListJSON.length(); i++){
			JSONObject obj = (JSONObject)parsListJSON.get(i);
			String name = obj.getString("name");	
			String type = obj.getString("type");	
			SourceBean b = new SourceBean("ROW");
			b.setAttribute("NAME", name);
			b.setAttribute("TYPE", type);
			sb1.setAttribute(b);	
		}	
		sb.setAttribute(sb1);
		toReturn = sb.toXML(false);
		return toReturn;
	}
	
	public JSONObject getDatasetTestResultList(IDataSet dataSet, HashMap parametersFilled, IEngUserProfile profile) throws Exception {
		logger.debug("IN");
		JSONObject dataSetJSON = null;
		
		dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( profile ));

		dataSet.setParamsMap(parametersFilled);		
		try{
			dataSet.loadData();
			IDataStore dataStore = dataSet.getDataStore();
			
//			try {
//				dataSetJSON = (JSONObject)SerializerFactory.getSerializer("application/json").serialize( dataStore, null );
//			} catch (SerializationException e) {
//				throw new SpagoBIServiceException("Impossible to serialize datastore", e);
//			}
			
			JSONDataWriter dataSetWriter = new JSONDataWriter();
			dataSetJSON = (JSONObject) dataSetWriter.write(dataStore);
		}
		catch (Exception e) {
			logger.error("Error while executing dataset for test purpose",e);
			return null;		
		}

		logger.debug("OUT");
		return dataSetJSON;
	}
	
	
	public it.eng.qbe.datasource.IDataSource getQbeDataSource(String datamartName, IDataSource dataSource) {

        Map<String, Object> dataSourceProperties = new HashMap<String, Object>();
       
        String modelName = datamartName;
        List<String> modelNames = new ArrayList<String>();
        modelNames.add( modelName );
   
        DBConnection connection = new DBConnection();
        connection.setName( modelName );
        Domain dialect;
		try {
			dialect = DAOFactory.getDomainDAO().loadDomainById(dataSource.getDialectId());
		} catch (EMFUserError e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Error while loading domain with id " + dataSource.getDialectId(), e);
		}
        connection.setDialect( dialect.getValueCd() );           
        connection.setJndiName( dataSource.getJndi() );           
        connection.setDriverClass( dataSource.getDriver() );           
        connection.setPassword( dataSource.getPwd() );
        connection.setUrl( dataSource.getUrlConnection() );
        connection.setUsername( dataSource.getUser() );   

        dataSourceProperties.put("connection", connection);
        dataSourceProperties.put("dblinkMap", new HashMap());

	    File modelJarFile = null;
	    List<File> modelJarFiles = new ArrayList<File>();
	    CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration();
	    compositeConfiguration.loadDataSourceProperties().putAll( dataSourceProperties);
	    
		SourceBean jndiBean = (SourceBean) ConfigSingleton.getInstance().getAttribute("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
		String jndi = jndiBean.getCharacters();
		String resourcePath = SpagoBIUtilities.readJndiResource(jndi);
	    modelJarFile = new File(resourcePath+File.separator+"qbe" + File.separator + "datamarts" + File.separator + modelNames.get(0)+File.separator+"datamart.jar");
	    modelJarFiles.add(modelJarFile);
	    compositeConfiguration.addSubConfiguration(new FileDataSourceConfiguration(modelNames.get(0), modelJarFile));
	
	    logger.debug("OUT: Finish to load the data source for the model names "+modelNames+"..");
	    return DriverManager.getDataSource(getDriverName(modelJarFile), compositeConfiguration);
	}
	
    /**
     * Get the driver name (hibernate or jpa). It checks if the passed jar file contains the persistence.xml
     * in the META-INF folder
     * @param jarFile a jar file with the model definition
     * @return jpa if the persistence provder is JPA o hibernate otherwise
     */
    private static String getDriverName(File jarFile){
        logger.debug("IN: Check the driver name. Looking if "+jarFile+" is a jpa jar file..");
        JarInputStream zis;
        JarEntry zipEntry;
        String dialectName = null;
        boolean isJpa = false;
           
        try {
            FileInputStream fis = new FileInputStream(jarFile);
            zis = new JarInputStream(fis);
            while((zipEntry=zis.getNextJarEntry())!=null){
                logger.debug("Zip Entry is [" + zipEntry.getName() + "]");
                if(zipEntry.getName().equals("META-INF/persistence.xml") ){
                    isJpa = true;
                    break;
                }
                zis.closeEntry();
            }
            zis.close();
            if(isJpa){
                dialectName = "jpa";
            } else{
                dialectName = "hibernate";
            }
        } catch (Throwable t) {
            logger.error("Impossible to read jar file [" + jarFile + "]",t);
            throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to read jar file [" + jarFile + "]", t);
        }


        logger.debug("OUT: "+jarFile+" has the dialect: "+dialectName);
        return dialectName;
    }
    
    
	public QueryCatalogue getCatalogue(String json, it.eng.qbe.datasource.IDataSource dataSource) throws Exception {
		QueryCatalogue catalogue;
		JSONObject catalogueJSON;
		JSONArray queriesJSON;
		JSONObject queryJSON;
		Query query;
		
		catalogue = new QueryCatalogue();
		catalogueJSON = new JSONObject(json).getJSONObject("catalogue");
		try {
			queriesJSON = catalogueJSON.getJSONArray("queries");
		
			for(int i = 0; i < queriesJSON.length(); i++) {
				queryJSON = queriesJSON.getJSONObject(i);
				query = it.eng.qbe.query.serializer.SerializerFactory.getDeserializer("application/json").deserializeQuery(queryJSON, dataSource);
								
				catalogue.addQuery(query);
			}
		} catch (Throwable e) {
			throw new SpagoBIEngineRuntimeException("Impossible to deserialize catalogue", e);
		}
		
		return catalogue;
	}
}
