/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.service.rest;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.tools.dataset.bo.CustomDataSet;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.DataSourceDAOHibImpl;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.json.JSONUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Antonella Giachino (antonella.giachino@eng.it)
 * 
 */
@Path("/selfservicedataset")
public class SelfServiceDataSetCRUD {

	static private Logger logger = Logger.getLogger(SelfServiceDataSetCRUD.class);
	static private String deleteNullIdDataSetError = "error.message.description.data.set.cannot.be.null";
	static private String deleteInUseDSError = "error.message.description.data.set.deleting.inuse";
	static private String canNotFillResponseError = "error.message.description.generic.can.not.responce";
	static private String saveDuplicatedDSError = "error.message.description.data.set.saving.duplicated";
	static private final String SELFSERVICE_DS_TYPE = "SelfService";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDataSet(@Context HttpServletRequest req) {
		IDataSetDAO dataSetDao = null;
		IDomainDAO domaindao = null;
		List<IDataSet> dataSets;
		List<Domain> categories = null;
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		JSONObject JSONReturn = new JSONObject();
		JSONArray datasetsJSONArray = new JSONArray();
		try {
			dataSetDao = DAOFactory.getDataSetDAO();
			dataSetDao.setUserProfile(profile);
			dataSets = dataSetDao.loadAllActiveDataSets();		
			datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(dataSets, null);
			
			//sets action to modify dataset
			JSONArray actions = new JSONArray();
			JSONObject detailAction = new JSONObject();
			detailAction.put("name", "detail");
			detailAction.put("description", "Dataset detail");
			actions.put(detailAction);
			JSONObject deleteAction = new JSONObject();
			deleteAction.put("name", "delete");
			deleteAction.put("description", "Delete dataset");
			actions.put(deleteAction);
//			JSONObject worksheetAction = new JSONObject();
//			worksheetAction.put("name", "worksheet");
//			worksheetAction.put("description", "Show Worksheet");
//			actions.put(worksheetAction);
//			JSONObject geoAction = new JSONObject();
//			geoAction.put("name", "worksheet");
//			geoAction.put("description", "Show Geo");
//			actions.put(geoAction);
			JSONArray datasetsJSONReturn = new JSONArray();
	
			
			for(int i = 0; i < datasetsJSONArray.length(); i++) {
				JSONObject datasetJSON = datasetsJSONArray.getJSONObject(i);				
				datasetJSON.put("actions", actions);
				datasetsJSONReturn.put(datasetJSON);
			}

			JSONReturn.put("root", datasetsJSONReturn);


		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while instatiating the dao", t);
		}
		return JSONReturn.toString();

	}


	@POST
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteDataSet(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		HashMap<String, String> logParam = new HashMap();

		try {						
			String id = (String) req.getParameter("id");
			Assert.assertNotNull(id,deleteNullIdDataSetError );
			IDataSet ds = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(new Integer(id));
			DAOFactory.getDataSetDAO().deleteDataSet(ds.getId());		
			logParam.put("LABEL", ds.getLabel());
			updateAudit(req, profile, "DATA_SET.DELETE", null, "OK");
			return ("{resp:'ok'}");
		} catch (Exception ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.DELETE", null, "ERR");
			logger.debug(canNotFillResponseError);
			try {
				return ( ExceptionUtilities.serializeException(canNotFillResponseError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}
	}
	
	@POST
	@Path("/save")
	@Produces(MediaType.APPLICATION_JSON)
	public String saveDataSet(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		try {
			IDataSetDAO dao=DAOFactory.getDataSetDAO();
			dao.setUserProfile(profile);
			
			String label = (String)req.getParameter("label");
			IDataSet ds  = dao.loadActiveDataSetByLabel(label);
			IDataSet dsNew = recoverDataSetDetails(req, ds);
			
			HashMap<String, String> logParam = new HashMap();
			logParam.put("LABEL",dsNew.getLabel());

			Integer newId = -1;
			if (dsNew.getId()==-1) {
				//if a ds with the same label not exists on db ok else error
				if (DAOFactory.getDataSetDAO().loadActiveDataSetByLabel(dsNew.getLabel()) != null){
					updateAudit(req, profile, "DATA_SET.ADD", logParam, "KO");
					throw new SpagoBIRuntimeException(saveDuplicatedDSError);
				}	 		
				newId = dao.insertDataSet(dsNew);
				updateAudit(req, profile, "DATA_SET.ADD", logParam, "OK");
			} else {				
				//update ds
				dao.modifyDataSet(dsNew);
				updateAudit(req, profile, "DATA_SET.MODIFY", logParam, "OK");
			}  
					
			return ("{id:"+newId+" }");
		} catch (SpagoBIRuntimeException ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.SAVE", null, "ERR");
			logger.debug(ex.getMessage());
			try {
				return ( ExceptionUtilities.serializeException(ex.getMessage(),null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		} catch (Exception ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.SAVE", null, "ERR");
			logger.debug(canNotFillResponseError);
			try {
				return ( ExceptionUtilities.serializeException(canNotFillResponseError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}
	}


	private static void updateAudit(HttpServletRequest request,
			IEngUserProfile profile, String action_code,
			HashMap<String, String> parameters, String esito) {
		try {
			AuditLogUtilities.updateAudit(request, profile, action_code,
					parameters, esito);
		} catch (Exception e) {
			logger.debug("Error writnig audit", e);
		}
	}
	
	private JSONObject serializeDatasets(List<IDataSet> dataSets) throws SerializationException, JSONException {
		JSONObject dataSetsJSON = new JSONObject();
		JSONArray dataSetsJSONArray = new JSONArray();
		if (dataSets != null) {
			dataSetsJSONArray = (JSONArray) SerializerFactory.getSerializer(
					"application/json").serialize(dataSets, null);
			dataSetsJSON.put("root", dataSetsJSONArray);
		}
		return dataSetsJSON;
	}

	private IDataSet recoverDataSetDetails (HttpServletRequest req, IDataSet dataSet) throws EMFUserError, SourceBeanException, IOException  {
		Integer id=-1;
		String idStr = (String)(String)req.getParameter("id");
		if(idStr!=null && !idStr.equals("")){
			id = new Integer(idStr);
		}
		String label = (String)req.getParameter("label");
		String versionNum = (String)req.getParameter("version_num");
		String active = (String)req.getParameter("active");
		String description = (String)req.getParameter("description");	
		String name = (String)req.getParameter("name");
		String catTypeVn = (String)req.getParameter("catTypeVn");
		String type = (String)req.getParameter("type");
		String configuration = (String)req.getParameter("configuration");
		
		IDataSet toReturn = dataSet; //for not loose other fields if already esists!	
		if (toReturn == null && configuration != null){
			String config = JSONUtils.escapeJsonString(configuration);
			JSONObject jsonConf  = ObjectUtils.toJSONObject(config);
			try{
				if(type.equalsIgnoreCase(DataSetConstants.DS_FILE) || type.equalsIgnoreCase(SELFSERVICE_DS_TYPE)){
					toReturn = new FileDataSet();			
					((FileDataSet)toReturn).setFileName(jsonConf.getString(DataSetConstants.FILE_NAME));		
				}
		
				if(type.equalsIgnoreCase(DataSetConstants.DS_QUERY)) { 
					toReturn=new JDBCDataSet();
					((JDBCDataSet)toReturn).setQuery(jsonConf.getString(DataSetConstants.QUERY));
					((JDBCDataSet)toReturn).setQueryScript(jsonConf.getString(DataSetConstants.QUERY_SCRIPT));
					((JDBCDataSet)toReturn).setQueryScriptLanguage(jsonConf.getString(DataSetConstants.QUERY_SCRIPT_LANGUAGE));				
					DataSourceDAOHibImpl dataSourceDao=new DataSourceDAOHibImpl();
					IDataSource dataSource= dataSourceDao.loadDataSourceByLabel(jsonConf.getString(DataSetConstants.DATA_SOURCE));				
					((JDBCDataSet)toReturn).setDataSource(dataSource);				
				}
		
				if(type.equalsIgnoreCase(DataSetConstants.DS_WS)) { 			
					toReturn=new WebServiceDataSet();
					((WebServiceDataSet)toReturn).setAddress(jsonConf.getString(DataSetConstants.WS_ADDRESS));
					((WebServiceDataSet)toReturn).setOperation(jsonConf.getString(DataSetConstants.WS_OPERATION));
				}
		
				if(type.equalsIgnoreCase(DataSetConstants.DS_SCRIPT)) {	
					toReturn=new ScriptDataSet();
					((ScriptDataSet)toReturn).setScript(jsonConf.getString(DataSetConstants.SCRIPT));
					((ScriptDataSet)toReturn).setScriptLanguage(jsonConf.getString(DataSetConstants.SCRIPT_LANGUAGE));
				}
		
				if(type.equalsIgnoreCase(DataSetConstants.DS_JCLASS)) { 			
					toReturn=new JavaClassDataSet();
					((JavaClassDataSet)toReturn).setClassName(jsonConf.getString(DataSetConstants.JCLASS_NAME));
				}
				
				if(type.equalsIgnoreCase(DataSetConstants.DS_CUSTOM)) { 			
					toReturn=new CustomDataSet();
					((CustomDataSet)toReturn).setCustomData(jsonConf.getString(DataSetConstants.CUSTOM_DATA));
					((CustomDataSet)toReturn).setJavaClassName(jsonConf.getString(DataSetConstants.JCLASS_NAME));
				}
				
				if(type.equalsIgnoreCase(DataSetConstants.DS_QBE) ) { 		
					toReturn = new QbeDataSet();				
					((QbeDataSet)toReturn).setJsonQuery(jsonConf.getString(DataSetConstants.QBE_JSON_QUERY));
					((QbeDataSet)toReturn).setDatamarts( jsonConf.getString(DataSetConstants.QBE_DATAMARTS));
					DataSourceDAOHibImpl dataSourceDao=new DataSourceDAOHibImpl();
					IDataSource dataSource= dataSourceDao.loadDataSourceByLabel(jsonConf.getString(DataSetConstants.QBE_DATA_SOURCE));									
					if (dataSource!=null){				
						((QbeDataSet)toReturn).setDataSource(dataSource);				
					}			
					
				}			
				toReturn.setDsType(type);
			}catch (Exception e){
				logger.error("Error while defining dataset configuration.  Error: " + e.getMessage());
			}
		}
		//temporary for self service datasets
		if (configuration == null && type.equalsIgnoreCase(SELFSERVICE_DS_TYPE)){
			toReturn = new FileDataSet();			
			toReturn.setDsType(DataSetConstants.DS_FILE);
			JSONObject confFile = new JSONObject();
			try{
				confFile.put("fileName", "prova.xml");
			}catch (Exception e){
				logger.error("Error while defining self service dataset configuration.  Error: " + e.getMessage());
			}
			toReturn.setConfiguration(confFile.toString());
		}else{
			toReturn.setDsType(getDatasetTypeName(type)); 
		}

		toReturn.setId(id.intValue());
		toReturn.setLabel(label);
		toReturn.setName(name);
		toReturn.setDescription(description);				
		toReturn.setCategoryId((catTypeVn.equals(""))?null:Integer.valueOf(catTypeVn));		
				
		return toReturn;
	}
	

	
	private String serializeException(Exception e) throws JSONException{
		return ExceptionUtilities.serializeException(e.getMessage(),null);
	}

	private String getDatasetTypeName(String datasetTypeCode) {
		String datasetTypeName = null;
		
		try {
		
			if(datasetTypeCode == null) return null;
			List<Domain> datasetTypes = null;
			
			try {
				datasetTypes = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.DATA_SET_TYPE);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An unexpected error occured while loading dataset types from database", t);
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

}
