/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.service;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.tools.dataset.bo.CustomDataSet;
import it.eng.spagobi.tools.dataset.bo.CustomDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.DataSetParametersList;
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
import it.eng.spagobi.tools.dataset.common.behaviour.QuerableBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageDatasets extends AbstractSpagoBIAction {

	// logger component
	public static Logger logger = Logger.getLogger(ManageDatasets.class);

	public static final String STRING_TYPE = "string";
	public static final String NUMBER_TYPE = "number";
	public static final String RAW_TYPE = "raw";
	public static final String GENERIC_TYPE = "generic";
	private IEngUserProfile profile;

	@Override
	public void doService() {
		logger.debug("IN");
		IDataSetDAO dsDao;
		profile = getUserProfile();
		try {
			dsDao = DAOFactory.getDataSetDAO();
			dsDao.setUserProfile(profile);
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");			
		}
		Locale locale = getLocale();
		String serviceType = this.getAttributeAsString(DataSetConstants.MESSAGE_DET);
		logger.debug("Service type "+serviceType);

		if (serviceType != null && serviceType.equalsIgnoreCase(DataSetConstants.DATASETS_FOR_KPI_LIST)) {			
			returnDatasetForKpiList(dsDao, locale);
		} else if(serviceType != null && serviceType.equalsIgnoreCase(DataSetConstants.DATASETS_LIST)) {			
			returnDatasetList(dsDao, locale);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_INSERT)) {			
			datatsetInsert(dsDao, locale);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_TEST)) {	
			datatsetTest(dsDao, locale);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_DELETE)) {
			datatsetDelete(dsDao, locale);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_VERSION_DELETE)) {
			datatsetVersionDelete(dsDao, locale);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_ALL_VERSIONS_DELETE)) {
			datatsetAllVersionsDelete(dsDao, locale);
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_VERSION_RESTORE)) {
			datatsetVersionRestore(dsDao, locale);
		} else if(serviceType == null){
			setUsefulItemsInSession(dsDao, locale);
		}
		logger.debug("OUT");
	}

	private void returnDatasetForKpiList(IDataSetDAO dsDao, Locale locale){
		try {	
			Integer totalItemsNum = dsDao.countDatasets();
			List<SbiDataSetConfig> items = getListOfGenericDatasetsForKpi(dsDao);
			logger.debug("Loaded items list");
			JSONArray itemsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(items, locale);
			JSONObject responseJSON = createJSONResponse(itemsJSON, totalItemsNum);
			writeBackToClient(new JSONSuccess(responseJSON));

		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving items", e);
			throw new SpagoBIServiceException(SERVICE_NAME, "sbi.general.retrieveItemsError", e);
		}
	}

	private void returnDatasetList(IDataSetDAO dsDao, Locale locale){
		try {		
			Integer totalItemsNum = dsDao.countDatasets();
			List<GuiGenericDataSet> items = getListOfGenericDatasets(dsDao);
			logger.debug("Loaded items list");
			JSONArray itemsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(items, locale);
			JSONObject responseJSON = createJSONResponse(itemsJSON, totalItemsNum);
			writeBackToClient(new JSONSuccess(responseJSON));

		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving items", e);
			throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.general.retrieveItemsError", e);
		}
	}

	private void datatsetInsert(IDataSetDAO dsDao, Locale locale){
		GuiGenericDataSet ds = getGuiGenericDatasetToInsert();		
		HashMap<String, String> logParam = new HashMap();
		logParam.put("NAME", ds.getName());
		logParam.put("LABEL", ds.getLabel());
		logParam.put("TYPE", ds.getActiveDetail().getDsType());
		
		if(ds!=null){
			String id = getAttributeAsString(DataSetConstants.ID);
			try {
				if(id != null && !id.equals("") && !id.equals("0")){							
					ds.setDsId(Integer.valueOf(id));
					dsDao.modifyDataSet(ds);
					logger.debug("Resource "+id+" updated");
					JSONObject attributesResponseSuccessJSON = new JSONObject();
					attributesResponseSuccessJSON.put("success", true);
					attributesResponseSuccessJSON.put("responseText", "Operation succeded");
					attributesResponseSuccessJSON.put("id", id);
					AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "DATA_SET.MODIFY",logParam , "OK");
					writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );					
					
				}else{
					Integer dsID = dsDao.insertDataSet(ds);
					GuiGenericDataSet dsSaved = dsDao.loadDataSetById(dsID);
					logger.debug("New Resource inserted");
					JSONObject attributesResponseSuccessJSON = new JSONObject();
					attributesResponseSuccessJSON.put("success", true);
					attributesResponseSuccessJSON.put("responseText", "Operation succeded");
					attributesResponseSuccessJSON.put("id", dsID);
					if(dsSaved!=null){
						GuiDataSetDetail dsDetailSaved = dsSaved.getActiveDetail();
						attributesResponseSuccessJSON.put("dateIn", dsDetailSaved.getTimeIn());
						attributesResponseSuccessJSON.put("userIn", dsDetailSaved.getUserIn());
						attributesResponseSuccessJSON.put("versId", dsDetailSaved.getDsHId());
						attributesResponseSuccessJSON.put("versNum", dsDetailSaved.getVersionNum());
					}
					AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "DATA_SET.ADD",logParam , "OK");
					writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
				}
			} catch (Throwable e) {
				try {
					AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "DATA_SET.ADD",logParam , "KO");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.saveDsError", e);
			}
		}else{
			try {
				AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "DATA_SET.ADD/MODIFY",logParam , "ERR");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.error("DataSet name, label or type are missing");
			throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.ds.fillFieldsError");
		}
	}

	private void datatsetTest(IDataSetDAO dsDao, Locale locale){
		try {
			JSONObject dataSetJSON = getDataSetResultsAsJSON();
			if(dataSetJSON!=null){
				try {
					writeBackToClient( new JSONSuccess( dataSetJSON ) );
				} catch (IOException e) {
					throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
				}
			}else{
				throw new SpagoBIServiceException(SERVICE_NAME, "sbi.ds.testError");
			}
		} catch (Throwable t) {
			if(t instanceof SpagoBIServiceException) throw (SpagoBIServiceException)t;
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.testError", t);
		}
	}

	private void datatsetDelete(IDataSetDAO dsDao, Locale locale){
		Integer dsID = getAttributeAsInteger(DataSetConstants.ID);
		GuiGenericDataSet ds = dsDao.loadDataSetById(dsID);
		HashMap<String, String> logParam = new HashMap();
		logParam.put("NAME", ds.getName());
		logParam.put("LABEL", ds.getLabel());
		logParam.put("TYPE", ds.getActiveDetail().getDsType());
		try {
			dsDao.deleteDataSet(dsID);
			logger.debug("Dataset deleted"); 
			AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "DATA_SET.DELETE",logParam , "OK");
			writeBackToClient( new JSONAcknowledge("Operation succeded") );
		} catch (Throwable e) {
			try {
				AuditLogUtilities.updateAudit(getHttpRequest(),  profile, "DATA_SET.DELETE",logParam , "KO");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.error("Exception occurred while retrieving dataset to delete", e);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.deleteDsError", e);
		}
	}

	private void datatsetVersionDelete(IDataSetDAO dsDao, Locale locale){
		Integer dsVersionID = getAttributeAsInteger(DataSetConstants.VERSION_ID);
		try {
			boolean deleted = dsDao.deleteInactiveDataSetVersion(dsVersionID);	
			if(deleted){
				logger.debug("Dataset Version deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			}else{
				throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.deleteVersion");
			}
		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving dataset version to delete", e);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.deleteVersion", e);
		}
	}

	private void datatsetAllVersionsDelete(IDataSetDAO dsDao, Locale locale){
		Integer dsID = getAttributeAsInteger(DataSetConstants.DS_ID);
		try {
			dsDao.deleteAllInactiveDataSetVersions(dsID);
			logger.debug("All Older Dataset versions deleted");
			writeBackToClient( new JSONAcknowledge("Operation succeded") );
		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving dataset to delete", e);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.deleteVersion", e);
		}
	}

	private void datatsetVersionRestore(IDataSetDAO dsDao, Locale locale){
		Integer dsID = getAttributeAsInteger(DataSetConstants.DS_ID);
		Integer dsVersionNum = getAttributeAsInteger(DataSetConstants.VERSION_NUM);
		try {
			GuiGenericDataSet dsNewDetail= dsDao.restoreOlderDataSetVersion(dsID, dsVersionNum);
			logger.debug("Dataset Version correctly Restored");
			List temp = new ArrayList();
			temp.add(dsNewDetail);
			JSONArray itemJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(temp, locale);	
			JSONObject version = itemJSON.getJSONObject(0);
			JSONObject attributesResponseSuccessJSON = new JSONObject();
			attributesResponseSuccessJSON.put("success", true);
			attributesResponseSuccessJSON.put("responseText", "Operation succeded");
			attributesResponseSuccessJSON.put("result", version);
			writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving dataset to restore", e);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.restoreVersionError", e);
		}
	}

	private void setUsefulItemsInSession(IDataSetDAO dsDao, Locale locale){
		try {
			List dsTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.DATA_SET_TYPE);
			getSessionContainer().setAttribute("dsTypesList", dsTypesList);				
			List catTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.CATEGORY_DOMAIN_TYPE);
			getSessionContainer().setAttribute("catTypesList", catTypesList);
			List dataSourceList = DAOFactory.getDataSourceDAO().loadAllDataSources();			
			getSessionContainer().setAttribute("dataSourceList", dataSourceList);
			List scriptLanguageList = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.SCRIPT_TYPE);
			getSessionContainer().setAttribute("scriptLanguageList", scriptLanguageList);	
			List trasfTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.TRANSFORMER_TYPE);
			getSessionContainer().setAttribute("trasfTypesList", trasfTypesList);	
			List sbiAttrs = DAOFactory.getSbiAttributeDAO().loadSbiAttributes();
			getSessionContainer().setAttribute("sbiAttrsList", sbiAttrs);	
			SingletonConfig configSingleton = SingletonConfig.getInstance();
			String pathh = configSingleton.getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
			String filePath= SpagoBIUtilities.readJndiResource(pathh);
			filePath += "/dataset/files";
			File dir = new File(filePath);
			String[] fileNames = dir.list();
			getSessionContainer().setAttribute("fileNames", fileNames);	
		} catch (EMFUserError e) {
			logger.error(e.getMessage(), e);
			throw new SpagoBIServiceException(SERVICE_NAME,"sbi.ds.dsTypesRetrieve", e);
		}
	}


	private List<SbiDataSetConfig> getListOfGenericDatasetsForKpi(IDataSetDAO dsDao) throws JSONException, EMFUserError{
		Integer start = getAttributeAsInteger( DataSetConstants.START );
		Integer limit = getAttributeAsInteger( DataSetConstants.LIMIT );

		if(start==null){
			start = DataSetConstants.START_DEFAULT;
		}
		if(limit==null){
			limit = DataSetConstants.LIMIT_DEFAULT;
		}
		List<SbiDataSetConfig> items = dsDao.loadPagedSbiDatasetConfigList(start,limit);
		return items;
	}

	private List<GuiGenericDataSet> getListOfGenericDatasets(IDataSetDAO dsDao) throws JSONException, EMFUserError{
		Integer start = getAttributeAsInteger( DataSetConstants.START );
		Integer limit = getAttributeAsInteger( DataSetConstants.LIMIT );

		if(start==null){
			start = DataSetConstants.START_DEFAULT;
		}
		if(limit==null){
			limit = DataSetConstants.LIMIT_DEFAULT;
		}
		JSONObject filtersJSON = null;
		List<GuiGenericDataSet> items = null;
		if(this.requestContainsAttribute( DataSetConstants.FILTERS ) ) {
			filtersJSON = getAttributeAsJSONObject( DataSetConstants.FILTERS );
			String hsql = filterList(filtersJSON);
			items = dsDao.loadFilteredDatasetList(hsql, start, limit);
		}else{//not filtered
			items = dsDao.loadPagedDatasetList(start,limit);
		}
		return items;
	}

	private GuiGenericDataSet getGuiGenericDatasetToInsert() {

		GuiGenericDataSet ds = null;

		String label = getAttributeAsString(DataSetConstants.LABEL);
		String name = getAttributeAsString(DataSetConstants.NAME);
		String description = getAttributeAsString(DataSetConstants.DESCRIPTION);		
		String datasetTypeCode = getAttributeAsString(DataSetConstants.DS_TYPE_CD);

		String datasetTypeName = getDatasetTypeName(datasetTypeCode);
	
		if (name != null && label != null && datasetTypeName!=null && !datasetTypeName.equals("")) {

			ds = new GuiGenericDataSet();		
			if(ds!=null){
				ds.setLabel(label);
				ds.setName(name);

				if(description != null && !description.equals("")){
					ds.setDescription(description);
				}
				GuiDataSetDetail dsActiveDetail = constructDataSetDetail(datasetTypeName);
				ds.setActiveDetail(dsActiveDetail);	
			}else{
				logger.error("DataSet type is not existent");
				throw new SpagoBIServiceException(SERVICE_NAME,	"sbi.ds.dsTypeError");
			}
		}    
		return ds;
	}

	private GuiDataSetDetail constructDataSetDetail(String dsType){
		GuiDataSetDetail dsActiveDetail = instantiateCorrectDsDetail(dsType);

		if(dsActiveDetail!=null){
			dsActiveDetail.setDsType(dsType);

			String catTypeCd = getAttributeAsString(DataSetConstants.CATEGORY_TYPE_VN);			
			
			String meta = getAttributeAsString(DataSetConstants.METADATA);					
			String trasfTypeCd = getAttributeAsString(DataSetConstants.TRASFORMER_TYPE_CD);

			List<Domain> domainsCat = (List<Domain>)getSessionContainer().getAttribute("catTypesList");			
			HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
			if(domainsCat != null){
				for(int i=0; i< domainsCat.size(); i++){
					domainIds.put(domainsCat.get(i).getValueName(), domainsCat.get(i).getValueId());
				}
			}
			Integer catTypeID = domainIds.get(catTypeCd);
			if(catTypeID!=null){
				dsActiveDetail.setCategoryValueName(catTypeCd);
				dsActiveDetail.setCategoryId(catTypeID);
			}

			if(meta != null && !meta.equals("")){
				dsActiveDetail.setDsMetadata(meta);
			}

			
			String pars = getDataSetParametersAsString();
			if(pars != null) {
				dsActiveDetail.setParameters(pars);
			}
			
			

			if(trasfTypeCd!=null && !trasfTypeCd.equals("")){
				dsActiveDetail = setTransformer(dsActiveDetail, trasfTypeCd);
			}

			IDataSet ds = null;		
			try {
				if (dsType != null && !dsType.equals("")) {
					ds = getDataSet(dsType);
					if (ds != null) {
						if (trasfTypeCd != null && !trasfTypeCd.equals("")) {
							ds = setTransformer(ds, trasfTypeCd);
						}
						String recalculateMetadata = this.getAttributeAsString(DataSetConstants.RECALCULATE_METADATA);
						String dsMetadata = null;
						if (recalculateMetadata == null || recalculateMetadata.trim().equals("yes")) {
							// recalculate metadata
							logger.debug("Recalculating dataset's metadata: executing the dataset...");
							HashMap parametersMap = new HashMap();
							parametersMap = getDataSetParametersAsMap();
							
							IEngUserProfile profile = getUserProfile();
							dsMetadata = getDatasetTestMetadata(ds,
									parametersMap, profile);
							LogMF.debug(logger, "Dataset executed, metadata are [{0}]", dsMetadata);
						} else {
							// load existing metadata
							logger.debug("Loading existing dataset...");
							String id = getAttributeAsString(DataSetConstants.ID);
							if (id != null && !id.equals("") && !id.equals("0")) {
								IDataSet existingDataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(new Integer(id));
								dsMetadata = existingDataSet.getDsMetadata();
								LogMF.debug(logger, "Reloaded metadata : [{0}]", dsMetadata);
							} else {
								throw new SpagoBIServiceException(SERVICE_NAME,
										"Missing dataset id, cannot retrieve its metadata");
							}
							
						}
						dsActiveDetail.setDsMetadata(dsMetadata);
					}
				} else {
					logger.error("DataSet type is not existent");
					throw new SpagoBIServiceException(SERVICE_NAME,
							"sbi.ds.dsTypeError");
				}
			} catch (Exception e) {
				logger.error("Error while getting dataset metadataa", e);
			}
		}	
		return dsActiveDetail;
	}

	private GuiDataSetDetail instantiateCorrectDsDetail(String dsType){
		GuiDataSetDetail dsActiveDetail = null;

		if(dsType.equalsIgnoreCase(DataSetConstants.DS_FILE)){
			dsActiveDetail = new FileDataSetDetail();
			String fileName = getAttributeAsString(DataSetConstants.FILE_NAME);
			if(fileName!=null && !fileName.equals("")){
				((FileDataSetDetail)dsActiveDetail).setFileName(fileName);
			}
		}else if(dsType.equalsIgnoreCase(DataSetConstants.DS_JCLASS)){
			dsActiveDetail = new JClassDataSetDetail();
			String jclassName = getAttributeAsString(DataSetConstants.JCLASS_NAME);
			if(jclassName!=null && !jclassName.equals("")){
				((JClassDataSetDetail)dsActiveDetail).setJavaClassName(jclassName);
			}
		}else if(dsType.equalsIgnoreCase(DataSetConstants.DS_QUERY)){
			dsActiveDetail = new QueryDataSetDetail();
			String dataSourceLabel = getAttributeAsString(DataSetConstants.DATA_SOURCE);
			String query = getAttributeAsString(DataSetConstants.QUERY);
			String queryScript = getAttributeAsString(DataSetConstants.QUERY_SCRIPT);
			String queryScriptLanguage = getAttributeAsString(DataSetConstants.QUERY_SCRIPT_LANGUAGE);
			
			
			if( StringUtilities.isNotEmpty(dataSourceLabel) ){
				((QueryDataSetDetail)dsActiveDetail).setDataSourceLabel(dataSourceLabel);
			}
			
			if( StringUtilities.isNotEmpty(query) ){
				((QueryDataSetDetail)dsActiveDetail).setQuery(query);
			}
			
			if( StringUtilities.isNotEmpty(queryScript) ){
				((QueryDataSetDetail)dsActiveDetail).setQueryScript(queryScript);
			}
			
			if( StringUtilities.isNotEmpty(queryScriptLanguage) ){
				((QueryDataSetDetail)dsActiveDetail).setQueryScriptLanguage(queryScriptLanguage);
			}
			
		}else if(dsType.equalsIgnoreCase(DataSetConstants.DS_QBE)){
			dsActiveDetail = new QbeDataSetDetail();
			String sqlQuery = getAttributeAsString(DataSetConstants.QBE_SQL_QUERY);
			String jsonQuery = getAttributeAsString(DataSetConstants.QBE_JSON_QUERY);
			String dataSourceLabel = getAttributeAsString(DataSetConstants.QBE_DATA_SOURCE);
			String datamarts = getAttributeAsString(DataSetConstants.QBE_DATAMARTS);
			((QbeDataSetDetail) dsActiveDetail).setSqlQuery(sqlQuery);
			((QbeDataSetDetail) dsActiveDetail).setJsonQuery(jsonQuery);
			((QbeDataSetDetail) dsActiveDetail).setDataSourceLabel(dataSourceLabel);
			((QbeDataSetDetail) dsActiveDetail).setDatamarts(datamarts);
		}else if(dsType.equalsIgnoreCase(DataSetConstants.DS_SCRIPT)){
			dsActiveDetail = new ScriptDataSetDetail();
			String script = getAttributeAsString(DataSetConstants.SCRIPT);
			String scriptLanguage = getAttributeAsString(DataSetConstants.SCRIPT_LANGUAGE);
			if(scriptLanguage!=null && !scriptLanguage.equals("")){
				((ScriptDataSetDetail)dsActiveDetail).setLanguageScript(scriptLanguage);
			}
			if(script!=null && !script.equals("")){
				((ScriptDataSetDetail)dsActiveDetail).setScript(script);
			}
		}else if(dsType.equalsIgnoreCase(DataSetConstants.DS_WS)){
			dsActiveDetail = new WSDataSetDetail();
			String wsAddress = getAttributeAsString(DataSetConstants.WS_ADDRESS);
			String wsOperation = getAttributeAsString(DataSetConstants.WS_OPERATION);
			if(wsOperation!=null && !wsOperation.equals("")){
				((WSDataSetDetail)dsActiveDetail).setOperation(wsOperation);
			}
			if(wsAddress!=null && !wsAddress.equals("")){
				((WSDataSetDetail)dsActiveDetail).setAddress(wsAddress);
			}
		}
		else if(dsType.equalsIgnoreCase(DataSetConstants.DS_CUSTOM)){
			dsActiveDetail = new CustomDataSetDetail();
			String customData = getAttributeAsString(DataSetConstants.CUSTOM_DATA);
			if(customData!=null && !customData.equals("")){
				((CustomDataSetDetail)dsActiveDetail).setCustomData(customData);
			}
			String jClassName = getAttributeAsString(DataSetConstants.JCLASS_NAME);
			if(jClassName!=null && !jClassName.equals("")){
				((CustomDataSetDetail)dsActiveDetail).setJavaClassName(jClassName);
			}
		}
		return dsActiveDetail;
	}


	private GuiDataSetDetail setTransformer(GuiDataSetDetail dsActiveDetail, String trasfTypeCd){
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

		String pivotColName = getAttributeAsString(DataSetConstants.PIVOT_COL_NAME);
		String pivotColValue = getAttributeAsString(DataSetConstants.PIVOT_COL_VALUE);
		String pivotRowName = getAttributeAsString(DataSetConstants.PIVOT_ROW_NAME);
		Boolean pivotIsNumRows = getAttributeAsBoolean(DataSetConstants.PIVOT_IS_NUM_ROWS);

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
		return dsActiveDetail;
	}

	
	private JSONObject getDataSetResultsAsJSON() {

		JSONObject dataSetJSON = null;		
		JSONArray parsJSON = getAttributeAsJSONArray(DataSetConstants.PARS);
		String transformerTypeCode = getAttributeAsString(DataSetConstants.TRASFORMER_TYPE_CD);

		IDataSet dataSet = getDataSet();
		if(dataSet == null){
			throw new SpagoBIRuntimeException("Impossible to retrieve dataset from request");
		}
		
		if( StringUtilities.isNotEmpty(transformerTypeCode) ){
			dataSet = setTransformer(dataSet, transformerTypeCode);
		}
		HashMap<String, String> parametersMap = new HashMap<String, String>();
		if(parsJSON!=null){
			parametersMap = getDataSetParametersAsMap();
		}
		IEngUserProfile profile = getUserProfile();
		
		dataSetJSON = getDatasetTestResultList(dataSet, parametersMap, profile);					
									
		return dataSetJSON;
	}
	
	private String getDatasetTypeName(String datasetTypeCode) {
		String datasetTypeName = null;
		
		try {
		
			if(datasetTypeCode == null) return null;
			
			List<Domain> datasetTypes = (List<Domain>)getSessionContainer().getAttribute("dsTypesList");
			//if the method is called out of DatasetManagement
			if (datasetTypes == null) {
				try {
					datasetTypes = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.DATA_SET_TYPE);
				} catch (Throwable t) {
					throw new SpagoBIRuntimeException("An unexpected error occured while loading dataset types from database", t);
				}
			}
			
			if(datasetTypes == null) {
				return null;
			}
			
			
			for(Domain datasetType : datasetTypes){
				if( datasetTypeCode.equalsIgnoreCase( datasetType.getValueCd() ) ){
					datasetTypeName = datasetType.getValueName();
					break;
				}
			}
		} catch(Throwable t) {
			if(t instanceof SpagoBIRuntimeException) throw (SpagoBIRuntimeException)t;
			throw new SpagoBIRuntimeException("An unexpected error occured while resolving dataset type name from dataset type code [" + datasetTypeCode + "]");
		}
		
		return datasetTypeName;
	}
	
	private IDataSet getDataSet()  {
		IDataSet dataSet = null;
		try {
			String datasetTypeCode = getAttributeAsString(DataSetConstants.DS_TYPE_CD);				
			
			String datasetTypeName = getDatasetTypeName( datasetTypeCode );
			if ( datasetTypeName == null) {
				throw new SpagoBIServiceException(SERVICE_NAME,	"Impossible to resolve dataset type whose code is equal to [" + datasetTypeCode + "]");
			}
			dataSet = getDataSet(datasetTypeName);
		} catch(Throwable t) {
			if(t instanceof SpagoBIServiceException) throw (SpagoBIServiceException)t;
			throw new SpagoBIServiceException(SERVICE_NAME,	"An unexpected error occured while retriving dataset from request", t);
		}
		return dataSet;
	}

	private IDataSet getDataSet(String datasetTypeName) throws Exception{

		IDataSet dataSet = null;
		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_FILE)){	
			dataSet = new FileDataSet();
			String fileName = getAttributeAsString(DataSetConstants.FILE_NAME);
			((FileDataSet)dataSet).setFileName(fileName);		
		} 

		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_QUERY)){		
			dataSet=new JDBCDataSet();
			String query = getAttributeAsString(DataSetConstants.QUERY);
			String queryScript = getAttributeAsString(DataSetConstants.QUERY_SCRIPT);
			String queryScriptLanguage = getAttributeAsString(DataSetConstants.QUERY_SCRIPT_LANGUAGE);
			String dataSourceLabel = getAttributeAsString(DataSetConstants.DATA_SOURCE);
			((JDBCDataSet)dataSet).setQuery(query);
			((JDBCDataSet)dataSet).setQueryScript(queryScript);
			((JDBCDataSet)dataSet).setQueryScriptLanguage(queryScriptLanguage);
			if(dataSourceLabel!=null && !dataSourceLabel.equals("")){
				IDataSource dataSource;
				try {
					dataSource = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dataSourceLabel);
					if(dataSource!=null){
						((JDBCDataSet)dataSet).setDataSource(dataSource);
					}
				} catch (EMFUserError e) {
					logger.error("Error while retrieving Datasource with label="+dataSourceLabel,e);
					e.printStackTrace();
				}			
			}
		}

		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_WS)){	
			dataSet=new WebServiceDataSet();
			String wsAddress = getAttributeAsString(DataSetConstants.WS_ADDRESS);
			String wsOperation = getAttributeAsString(DataSetConstants.WS_OPERATION);
			((WebServiceDataSet)dataSet).setAddress(wsAddress);
			((WebServiceDataSet)dataSet).setOperation(wsOperation);
		}

		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_SCRIPT)){	
			dataSet=new ScriptDataSet();
			String script = getAttributeAsString(DataSetConstants.SCRIPT);
			String scriptLanguage = getAttributeAsString(DataSetConstants.SCRIPT_LANGUAGE);
			((ScriptDataSet)dataSet).setScript(script);
			((ScriptDataSet)dataSet).setScriptLanguage(scriptLanguage);
		}

		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_JCLASS)){		
			dataSet=new JavaClassDataSet();
			String jclassName = getAttributeAsString(DataSetConstants.JCLASS_NAME);
			((JavaClassDataSet)dataSet).setClassName(jclassName);
		}

		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_CUSTOM)){		
			CustomDataSet customDs=new CustomDataSet();
			String customData = getAttributeAsString(DataSetConstants.CUSTOM_DATA);
			customDs.setCustomData(customData);
			String javaClassName = getAttributeAsString(DataSetConstants.JCLASS_NAME);
			customDs.setJavaClassName(javaClassName);
//			customDs.init();
			
			// if custom type call the referred class extending CustomAbstractDataSet
			try {
				dataSet = customDs.instantiate();			
			} catch (Exception e) {
				logger.error("Cannot instantiate class "+customDs.getJavaClassName()+ ": go on with CustomDatasetClass");
				throw new SpagoBIServiceException("Manage Dataset","Cannot instantiate class "+javaClassName+": check it extends AbstractCustomDataSet");	
			}			
		}


		if(datasetTypeName.equalsIgnoreCase(DataSetConstants.DS_QBE)){

			dataSet = new QbeDataSet();
			QbeDataSet qbeDataSet = (QbeDataSet) dataSet;
			String qbeDatamarts = getAttributeAsString(DataSetConstants.QBE_DATAMARTS);
			String dataSourceLabel = getAttributeAsString(DataSetConstants.QBE_DATA_SOURCE);
			String jsonQuery = getAttributeAsString(DataSetConstants.QBE_JSON_QUERY);

			qbeDataSet.setJsonQuery(jsonQuery);
			qbeDataSet.setDatamarts(qbeDatamarts);
			IDataSource dataSource = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dataSourceLabel);
			qbeDataSet.setDataSource(dataSource);		

		}
		return dataSet;
	}

	private IDataSet setTransformer(IDataSet ds,String trasfTypeCd){
		List<Domain> domainsTrasf = (List<Domain>)getSessionContainer().getAttribute("trasfTypesList");
		HashMap<String, Integer> domainTrasfIds = new HashMap<String, Integer> ();
		if(domainsTrasf != null){
			for(int i=0; i< domainsTrasf.size(); i++){
				domainTrasfIds.put(domainsTrasf.get(i).getValueCd(), domainsTrasf.get(i).getValueId());
			}
		}
		Integer transformerId = domainTrasfIds.get(trasfTypeCd);

		String pivotColName = getAttributeAsString(DataSetConstants.PIVOT_COL_NAME);
		if(pivotColName!=null){
			pivotColName = pivotColName.trim();
		}
		String pivotColValue = getAttributeAsString(DataSetConstants.PIVOT_COL_VALUE);
		if(pivotColValue!=null){
			pivotColValue = pivotColValue.trim();
		}
		String pivotRowName = getAttributeAsString(DataSetConstants.PIVOT_ROW_NAME);
		if(pivotRowName!=null){
			pivotRowName = pivotRowName.trim();
		}
		Boolean pivotIsNumRows = getAttributeAsBoolean(DataSetConstants.PIVOT_IS_NUM_ROWS);

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
		return ds;
	}

	private JSONObject createJSONResponse(JSONArray rows, Integer totalResNumber)
	throws JSONException {
		JSONObject results;

		results = new JSONObject();
		results.put("total", totalResNumber);
		results.put("title", "Datasets");
		results.put("rows", rows);
		return results;
	}

	private String getDataSetParametersAsString() {
		String parametersString = null;
		
		try {
			JSONArray parsListJSON = getAttributeAsJSONArray(DataSetConstants.PARS);
			if(parsListJSON == null) return null;
			
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
			parametersString = sb.toXML(false);
		} catch(Throwable t) {
			if(t instanceof SpagoBIServiceException) throw (SpagoBIServiceException)t;
			throw new SpagoBIServiceException(SERVICE_NAME,	"An unexpected error occured while deserializing dataset parameters", t);
		}
		return parametersString;
	}
	
	private HashMap<String, String> getDataSetParametersAsMap() {
		HashMap<String, String> parametersMap = null;
		
		try {
			parametersMap = new HashMap<String, String>();
			
			JSONArray parsListJSON = getAttributeAsJSONArray(DataSetConstants.PARS);
			if(parsListJSON == null) return parametersMap;
			
			for(int i=0; i< parsListJSON.length(); i++){
				JSONObject obj = (JSONObject)parsListJSON.get(i);
				String name = obj.getString("name");
				String type = null;
				if(obj.has("type")){
					type = obj.getString("type"); 
				}
	
				boolean hasVal  = obj.has("value");
				String tempVal = "";
				if(hasVal){
					tempVal = obj.getString("value");
				}
	
				boolean multivalue = false;
				if(tempVal!=null && tempVal.contains(",")){
					multivalue = true;
				}
	
				String value = "";
				if(multivalue){
					value = getMultiValue(tempVal, type);
				}
				else{
					value = getSingleValue(tempVal, type);
				}
	
				logger.debug("name: " + name + " / value: " + value);
				parametersMap.put(name,value);
			}
		} catch(Throwable t) {
			if(t instanceof SpagoBIServiceException) throw (SpagoBIServiceException)t;
			throw new SpagoBIServiceException(SERVICE_NAME,	"An unexpected error occured while deserializing dataset parameters", t);
		}
		return parametersMap;
	}


	private String getSingleValue(String value, String type){
		String toReturn = "";
		value = value.trim();
		if(type.equalsIgnoreCase(STRING_TYPE)){
			if(!(value.startsWith("'") && value.endsWith("'"))){
				toReturn="'"+value+"'";
			}
		}
		else if(type.equalsIgnoreCase(NUMBER_TYPE)){

			if((value.startsWith("'") && value.endsWith("'"))){
				toReturn = value.substring(1, value.length()-1);
			}
			else{
				toReturn = value;
			}
		}
		else if(type.equalsIgnoreCase(GENERIC_TYPE)){
			toReturn = value;
		}
		else if(type.equalsIgnoreCase(RAW_TYPE)){
			if((value.startsWith("'") && value.endsWith("'"))){
				toReturn = value.substring(1, value.length()-1);
			}
		}

		return toReturn;
	}


	private String getMultiValue(String value, String type){
		String toReturn = "";

		String[] tempArrayValues = value.split(",");
		for(int j=0; j< tempArrayValues.length; j++){
			String tempValue = tempArrayValues[j];
			if(j==0){
				toReturn = getSingleValue(tempValue, type);
			}else{
				toReturn = toReturn+","+getSingleValue(tempValue, type);	
			}
		}

		return toReturn;
	}

	
	

	public JSONArray serializeJSONArrayParsList(String parsList) throws JSONException, SourceBeanException{
		JSONArray toReturn = new JSONArray();
		DataSetParametersList params  = DataSetParametersList.fromXML(parsList);
		toReturn = ObjectUtils.toJSONArray(params.getItems());
		return toReturn;
	}

	public String getDatasetTestMetadata(IDataSet dataSet, HashMap parametersFilled, IEngUserProfile profile) throws Exception {
		logger.debug("IN");
		String dsMetadata = null;

		Integer start = new Integer(0);
		Integer limit = new Integer(10);

		dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( profile ));
		dataSet.setParamsMap(parametersFilled);		
		try {
			dataSet.loadData(start, limit, GeneralUtilities.getDatasetMaxResults());
			IDataStore dataStore = dataSet.getDataStore();
			DatasetMetadataParser dsp = new DatasetMetadataParser();
			dsMetadata = dsp.metadataToXML(dataStore.getMetaData());
		}
		catch (Exception e) {
			logger.error("Error while executing dataset for test purpose",e);
			return null;		
		}

		logger.debug("OUT");
		return dsMetadata;
	}

	public JSONObject getDatasetTestResultList(IDataSet dataSet, HashMap<String, String> parametersFilled, IEngUserProfile profile) {
		
		JSONObject dataSetJSON;

		logger.debug("IN");
		
		dataSetJSON = null;
		try {
			Integer start = -1;
			try{
				start = getAttributeAsInteger( DataSetConstants.START );
			}catch (NullPointerException e){
				logger.info("start option undefined");			
			}
			Integer limit = -1;
			try{
				limit = getAttributeAsInteger( DataSetConstants.LIMIT );
			}catch (NullPointerException e){
				logger.info("limit option undefined");			
			}
			
			dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( profile ));
			dataSet.setParamsMap(parametersFilled);		
			IDataStore dataStore = null;
			try {
				if(dataSet.getTransformerId() != null){
					dataStore = dataSet.test();
				} else{
					dataStore = dataSet.test(start, limit, GeneralUtilities.getDatasetMaxResults());
				}		
				if(dataStore == null) {
					throw new SpagoBIServiceException(SERVICE_NAME,	"Impossible to read resultset");
				}
			} catch (Throwable t) {
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String rootErrorMsg = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				if(dataSet instanceof JDBCDataSet) {
					JDBCDataSet jdbcDataSet = (JDBCDataSet)dataSet;
					if (jdbcDataSet.getQueryScript() != null) {
						QuerableBehaviour querableBehaviour = (QuerableBehaviour)jdbcDataSet.getBehaviour(QuerableBehaviour.class.getName());
						String statement = querableBehaviour.getStatement();
						rootErrorMsg += "\nQuery statement: [" + statement + "]";
					}
				}
				
				throw new SpagoBIServiceException(SERVICE_NAME,	"An unexpected error occured while executing dataset: " + rootErrorMsg, t);	
			}
			
			try {	
				JSONDataWriter dataSetWriter = new JSONDataWriter();
				dataSetJSON = (JSONObject) dataSetWriter.write(dataStore);
				if(dataSetJSON == null) {
					throw new SpagoBIServiceException(SERVICE_NAME,	"Impossible to read serialized resultset");
				}
			} catch (Exception t) {
				throw new SpagoBIServiceException(SERVICE_NAME,	"An unexpected error occured while serializing resultset", t);	
			}
		} catch(Throwable t) {
			if(t instanceof SpagoBIServiceException) throw (SpagoBIServiceException)t;
			throw new SpagoBIServiceException(SERVICE_NAME,	"An unexpected error occured while getting dataset results", t);
		} finally {
			logger.debug("OUT");
		}
		
		return dataSetJSON;
	}

	public JSONObject getJSONDatasetResult(Integer dsId, IEngUserProfile profile) {
		logger.debug("IN");
		JSONObject dataSetJSON = null;		
		//Integer id = obj.getDataSetId();	
		//gets the dataset object informations		
		try {
			IDataSet dataset = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(dsId);
			if (dataset.getParameters() != null){
				HashMap<String, String> parametersMap = new HashMap<String, String>();
				parametersMap = getDataSetParametersAsMap();
				dataSetJSON = getDatasetTestResultList(dataset, parametersMap, profile);
			}
		}
		catch (Exception e) {
			logger.error("Error while executing dataset",e);
			return null;		
		}
		logger.debug("OUT");
		return dataSetJSON;
	}

	private String filterList(JSONObject filtersJSON) throws JSONException {
		logger.debug("IN");				
		String hsql= " from SbiDataSetHistory h where h.active = true ";
		if (filtersJSON != null) {
			String valuefilter = (String) filtersJSON.get(SpagoBIConstants.VALUE_FILTER);
			String typeFilter = (String) filtersJSON.get(SpagoBIConstants.TYPE_FILTER);
			String columnFilter = (String) filtersJSON.get(SpagoBIConstants.COLUMN_FILTER);
			if(typeFilter.equals("=")){
				hsql += " and h."+columnFilter+" = '"+valuefilter+"'";
			}else if(typeFilter.equals("like")){
				hsql += " and h."+columnFilter+" like '%"+valuefilter+"%'";
			}			
		}
		logger.debug("OUT");
		return hsql;
	}


}
