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

import it.eng.qbe.datasource.ConnectionDescriptor;
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
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
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
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;

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
	public static Logger logger = Logger.getLogger(ManageDatasets.class);

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
		String serviceType = this.getAttributeAsString(DataSetConstants.MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		
		if (serviceType != null && serviceType.equalsIgnoreCase(DataSetConstants.DATASETS_FOR_KPI_LIST)) {			
			try {	
				Integer totalItemsNum = dsDao.countDatasets();
				List<SbiDataSetConfig> items = getListOfGenericDatasetsForKpi(dsDao);
				logger.debug("Loaded items list");
				JSONArray itemsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(items, locale);
				JSONObject responseJSON = createJSONResponse(itemsJSON, totalItemsNum);
				writeBackToClient(new JSONSuccess(responseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving items", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving items", e);
			}
		} else if(serviceType != null && serviceType.equalsIgnoreCase(DataSetConstants.DATASETS_LIST)) {			
			try {		
				Integer totalItemsNum = dsDao.countDatasets();
				List<GuiGenericDataSet> items = getListOfGenericDatasets(dsDao);
				logger.debug("Loaded items list");
				JSONArray itemsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(items, locale);
				JSONObject responseJSON = createJSONResponse(itemsJSON, totalItemsNum);
				writeBackToClient(new JSONSuccess(responseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving items", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving items", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_INSERT)) {			
			GuiGenericDataSet ds = getGuiGenericDatasetToInsert();		
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
					throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while saving new resource", e);
				}
			}else{
				logger.error("DataSet name, label or type are missing");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please fill DataSet name, label and type");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_TEST)) {			
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
				throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while Testing Dataset", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_DELETE)) {
			Integer dsID = getAttributeAsInteger(DataSetConstants.ID);
			try {
				dsDao.deleteDataSet(dsID);
				logger.debug("Dataset deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while retrieving dataset to delete", e);
			}
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_VERSION_DELETE)) {
			Integer dsVersionID = getAttributeAsInteger(DataSetConstants.VERSION_ID);
			try {
				boolean deleted = dsDao.deleteInactiveDataSetVersion(dsVersionID);
				logger.debug("Dataset Version deleted");
				if(deleted){
					writeBackToClient( new JSONAcknowledge("Operation succeded") );
				}else{
					throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while retrieving dataset Version to delete");
				}
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while retrieving dataset to delete", e);
			}
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_ALL_VERSIONS_DELETE)) {
			Integer dsID = getAttributeAsInteger(DataSetConstants.DS_ID);
			try {
				dsDao.deleteAllInactiveDataSetVersions(dsID);
				logger.debug("All Older Dataset versions deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while retrieving dataset to delete", e);
			}
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(DataSetConstants.DATASET_VERSION_RESTORE)) {
			Integer dsID = getAttributeAsInteger(DataSetConstants.DS_ID);
			Integer dsVersionNum = getAttributeAsInteger(DataSetConstants.VERSION_NUM);
			try {
				dsDao.restoreOlderDataSetVersion(dsID, dsVersionNum);
				logger.debug("Dataset Version correctly Restored");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving dataset to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,"Exception occurred while retrieving dataset to delete", e);
			}
		}else if(serviceType == null){
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
			} catch (EMFUserError e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,"Exception retrieving dataset types", e);
			}
		}
		logger.debug("OUT");
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
		String dsTypeCd = getAttributeAsString(DataSetConstants.DS_TYPE_CD);

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
		
	    if (name != null && label != null && dsType!=null && !dsType.equals("")) {
			
	    	ds = new GuiGenericDataSet();		
			if(ds!=null){
				ds.setLabel(label);
				ds.setName(name);
				
				if(description != null && !description.equals("")){
					ds.setDescription(description);
				}
				GuiDataSetDetail dsActiveDetail = constructDataSetDetail(dsType);
				ds.setActiveDetail(dsActiveDetail);	
			}else{
				logger.error("DataSet type is not existent");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please change DataSet Type");
			}
		}    
		return ds;
	}
	
	private GuiDataSetDetail constructDataSetDetail(String dsType){
		GuiDataSetDetail dsActiveDetail = instantiateCorrectDsDetail(dsType);
		
		if(dsActiveDetail!=null){
			dsActiveDetail.setDsType(dsType);
			
			String catTypeCd = getAttributeAsString(DataSetConstants.CATEGORY_TYPE_CD);			
			JSONArray parsJSON = getAttributeAsJSONArray(DataSetConstants.PARS);
			String meta = getAttributeAsString(DataSetConstants.METADATA);					
			String trasfTypeCd = getAttributeAsString(DataSetConstants.TRASFORMER_TYPE_CD);
			
			List<Domain> domainsCat = (List<Domain>)getSessionContainer().getAttribute("catTypesList");			
			HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
		    if(domainsCat != null){
			    for(int i=0; i< domainsCat.size(); i++){
			    	domainIds.put(domainsCat.get(i).getValueCd(), domainsCat.get(i).getValueId());
			    }
		    }
		    Integer catTypeID = domainIds.get(catTypeCd);
		    if(catTypeID!=null){
				dsActiveDetail.setCategoryCd(catTypeCd);
				dsActiveDetail.setCategoryId(catTypeID);
			}

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

			if(trasfTypeCd!=null && !trasfTypeCd.equals("")){
				dsActiveDetail = setTransformer(dsActiveDetail, trasfTypeCd);
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
			String query = getAttributeAsString(DataSetConstants.QUERY);
			String dataSourceLabel = getAttributeAsString(DataSetConstants.DATA_SOURCE);
			if(query!=null && !query.equals("")){
				((QueryDataSetDetail)dsActiveDetail).setQuery(query);
			}
			if(dataSourceLabel!=null && !dataSourceLabel.equals("")){
				((QueryDataSetDetail)dsActiveDetail).setDataSourceLabel(dataSourceLabel);
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
	
	private JSONObject datasetTest() throws Exception{
		
		JSONObject dataSetJSON = null;		
		String id = getAttributeAsString(DataSetConstants.ID);		
		String dsTypeCd = getAttributeAsString(DataSetConstants.DS_TYPE_CD);				
		JSONArray parsJSON = getAttributeAsJSONArray(DataSetConstants.PARS);
		String trasfTypeCd = getAttributeAsString(DataSetConstants.TRASFORMER_TYPE_CD);
		
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
			ds = instantiateCorrectIDataSetType(dsType);		
			if(ds!=null){									
				if(trasfTypeCd!=null && !trasfTypeCd.equals("")){
					ds = setTransformer(ds, trasfTypeCd);
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
	
	private IDataSet instantiateCorrectIDataSetType(String dsType) throws Exception{

		IDataSet ds = null;
		if(dsType.equalsIgnoreCase(DataSetConstants.DS_FILE)){	
			ds = new FileDataSet();
			String fileName = getAttributeAsString(DataSetConstants.FILE_NAME);
			((FileDataSet)ds).setFileName(fileName);		
		}

		if(dsType.equalsIgnoreCase(DataSetConstants.DS_QUERY)){		
			ds=new JDBCDataSet();
			String query = getAttributeAsString(DataSetConstants.QUERY);
			String dataSourceLabel = getAttributeAsString(DataSetConstants.DATA_SOURCE);
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

		if(dsType.equalsIgnoreCase(DataSetConstants.DS_WS)){	
			ds=new WebServiceDataSet();
			String wsAddress = getAttributeAsString(DataSetConstants.WS_ADDRESS);
			String wsOperation = getAttributeAsString(DataSetConstants.WS_OPERATION);
			((WebServiceDataSet)ds).setAddress(wsAddress);
			((WebServiceDataSet)ds).setOperation(wsOperation);
		}

		if(dsType.equalsIgnoreCase(DataSetConstants.DS_SCRIPT)){	
			ds=new ScriptDataSet();
			String script = getAttributeAsString(DataSetConstants.SCRIPT);
			String scriptLanguage = getAttributeAsString(DataSetConstants.SCRIPT_LANGUAGE);
			((ScriptDataSet)ds).setScript(script);
			((ScriptDataSet)ds).setLanguageScript(scriptLanguage);
		}

		if(dsType.equalsIgnoreCase(DataSetConstants.DS_JCLASS)){		
			ds=new JavaClassDataSet();
			String jclassName = getAttributeAsString(DataSetConstants.JCLASS_NAME);
			((JavaClassDataSet)ds).setClassName(jclassName);
		}
		
		if(dsType.equalsIgnoreCase(DataSetConstants.DS_QBE)){
			
			String qbeDatamarts = getAttributeAsString(DataSetConstants.QBE_DATAMARTS);
			String dataSourceLabel = getAttributeAsString(DataSetConstants.QBE_DATA_SOURCE);
			String jsonQuery = getAttributeAsString(DataSetConstants.QBE_JSON_QUERY);
			
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
		return ds;
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
		String pivotColValue = getAttributeAsString(DataSetConstants.PIVOT_COL_VALUE);
		String pivotRowName = getAttributeAsString(DataSetConstants.PIVOT_ROW_NAME);
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
		
		Integer start = getAttributeAsInteger( DataSetConstants.START );
		Integer limit = getAttributeAsInteger( DataSetConstants.LIMIT );
		
		dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( profile ));
		dataSet.setParamsMap(parametersFilled);		
		try {
			dataSet.loadData(start, limit, GeneralUtilities.getDatasetMaxResults());
			IDataStore dataStore = dataSet.getDataStore();
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
   
        ConnectionDescriptor connection = new ConnectionDescriptor();
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
