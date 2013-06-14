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

import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;
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

import org.apache.log4j.LogMF;
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
	static private String deleteNullIdDataSetError = "error.mesage.description.data.set.cannot.be.null";
	static private String deleteInUseDSError = "error.mesage.description.data.set.deleting.inuse";
	static private String canNotFillResponseError = "error.mesage.description.generic.can.not.responce";
	static private String saveDuplicatedDSError = "error.mesage.description.data.set.saving.duplicated";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDataSet(@Context HttpServletRequest req) {
		IDataSetDAO dataSetDao = null;
		List<IDataSet> dataSets;
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		JSONObject JSONReturn = new JSONObject();
		JSONArray datasetsJSONArray = new JSONArray();
		try {
			dataSetDao = DAOFactory.getDataSetDAO();
			dataSetDao.setUserProfile(profile);
			dataSets = dataSetDao.loadAllActiveDataSetsByOwnerAndType(profile.getUserUniqueIdentifier().toString(), DataSetConstants.DS_FILE);
			//dataSets = dataSetDao.loadAllActiveDataSetsByOwner(profile.getUserUniqueIdentifier().toString());	
			datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(dataSets, null);
			
			//sets action to modify dataset			
			JSONObject detailAction = new JSONObject();
			detailAction.put("name", "detail");
			detailAction.put("description", "Dataset detail");	
			JSONObject deleteAction = new JSONObject();
			deleteAction.put("name", "delete");
			deleteAction.put("description", "Delete dataset");
			JSONObject worksheetAction = new JSONObject();
			worksheetAction.put("name", "worksheet");
			worksheetAction.put("description", "Show Worksheet");
//			JSONObject geoAction = new JSONObject();
//			geoAction.put("name", "worksheet");
//			geoAction.put("description", "Show Geo");
//			actions.put(geoAction);
			JSONArray datasetsJSONReturn = new JSONArray();
				
			for(int i = 0; i < datasetsJSONArray.length(); i++) {
				JSONArray actions = new JSONArray();
				JSONObject datasetJSON = datasetsJSONArray.getJSONObject(i);
				actions.put(detailAction);		
				actions.put(worksheetAction);
				if (profile.getUserUniqueIdentifier().toString().equals(datasetJSON.get("owner"))){
					//the delete action is able only for private dataset
					actions.put(deleteAction);
				}
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
			try{
				DAOFactory.getDataSetDAO().deleteDataSet(ds.getId());
			}catch(Exception ex){
				if (ex.getMessage().startsWith("[deleteInUseDSError]")){
					updateAudit(req, profile, "DATA_SET.DELETE", logParam, "KO");
					throw new SpagoBIRuntimeException(deleteInUseDSError);
				}else{
					throw ex;
				}
			}
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
			String meta = (String)req.getParameter(DataSetConstants.METADATA);			
			
			IDataSet ds  = dao.loadActiveDataSetByLabel(label);
			IDataSet dsNew = recoverDataSetDetails(req, ds);
			
			logger.debug("Recalculating dataset's metadata: executing the dataset...");
			String dsMetadata = null;
			dsMetadata = getDatasetTestMetadata(dsNew, profile, meta);
			dsNew.setDsMetadata(dsMetadata);	
			LogMF.debug(logger, "Dataset executed, metadata are [{0}]", dsMetadata);
	
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
		JSONObject jsonDsConfig = new JSONObject();	
		boolean insertion = (dataSet == null);
		Integer id=-1;
		String idStr = (String)(String)req.getParameter("id");
		if(idStr!=null && !idStr.equals("")){
			id = new Integer(idStr);
		}
		String type = (String)req.getParameter("type");
		String label = (String)req.getParameter("label");
		String description = (String)req.getParameter("description");	
		String name = (String)req.getParameter("name");
		String catTypeVn = (String)req.getParameter("catTypeVn");		
		String configuration = (String)req.getParameter("configuration");
		String fileName = (String)req.getParameter("fileName");
		String csvDelimiter = (String)req.getParameter("csvDelimiter");
		String csvQuote = (String)req.getParameter("csvQuote");
		String fileType = (String)req.getParameter("fileType");
		String skipRows = (String)req.getParameter("skipRows");
		String limitRows = (String)req.getParameter("limitRows");
		String xslSheetNumber = (String)req.getParameter("xslSheetNumber");
		String meta = (String)req.getParameter(DataSetConstants.METADATA);		
		
		Boolean isPublic = Boolean.valueOf((req.getParameter("isPublic")==null)?"false":(String)req.getParameter("isPublic"));
		
		IDataSet toReturn = dataSet; //for not loose other fields if already esists!	
		try{
			String config = "{}";
			if (configuration != null)
				config = JSONUtils.escapeJsonString(configuration);
			
			JSONObject jsonConf  = ObjectUtils.toJSONObject(config);			
			jsonDsConfig.put(DataSetConstants.FILE_TYPE, fileType);
			jsonDsConfig.put(DataSetConstants.FILE_NAME, fileName);
			jsonDsConfig.put(DataSetConstants.CSV_FILE_DELIMITER_CHARACTER, csvDelimiter);
			jsonDsConfig.put(DataSetConstants.CSV_FILE_QUOTE_CHARACTER, csvQuote);
			jsonDsConfig.put(DataSetConstants.XSL_FILE_SKIP_ROWS, skipRows);
			jsonDsConfig.put(DataSetConstants.XSL_FILE_LIMIT_ROWS, limitRows);
			jsonDsConfig.put(DataSetConstants.XSL_FILE_SHEET_NUMBER, xslSheetNumber);
		

		}catch (Exception e){
			logger.error("Error while defining dataset configuration.  Error: " + e.getMessage());
		}
		type =  getDatasetTypeName(type); 
		if (insertion){
			toReturn = new FileDataSet();
			//next steps are necessary to define a valid dataProxy
			((FileDataSet)toReturn).setConfiguration(jsonDsConfig.toString());
			((FileDataSet)toReturn).setFileName(fileName);
		}
			
		toReturn.setConfiguration(jsonDsConfig.toString());
		toReturn.setDsType(type);
		//update general informations
		toReturn.setDsMetadata(meta);
		toReturn.setId(id.intValue());
		toReturn.setLabel(label);
		toReturn.setName(name);
		toReturn.setDescription(description);		
		
		Integer categoryCode = null;
		try{
			categoryCode = Integer.parseInt(catTypeVn);			
		}catch (Exception e){
			logger.debug("Category must be decodified...");
			categoryCode = getCategoryCode(catTypeVn);
			logger.debug("Category value decodified is : " + categoryCode);
		}
		logger.debug("Category code is :  " + categoryCode);
		toReturn.setCategoryId(categoryCode);
		toReturn.setPublic(isPublic);
				
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
	
	private Integer getCategoryCode(String category) {
		Integer categoryCode = null;
		
		try {
		
			if(category == null) return null;
			List<Domain> categories = null;
			
			try {
				categories = DAOFactory.getDomainDAO().loadListDomainsByType(DataSetConstants.CATEGORY_DOMAIN_TYPE);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An unexpected error occured while loading categories types from database", t);
			}
			
			
			if(categories == null) {
				return null;
			}
			
			
			for(Domain dmCategory : categories){
				if( category.equalsIgnoreCase( dmCategory.getValueCd() ) ){
					categoryCode = dmCategory.getValueId();
					break;
				}
			}
		} catch(Throwable t) {
			if(t instanceof SpagoBIRuntimeException) throw (SpagoBIRuntimeException)t;
			throw new SpagoBIRuntimeException("An unexpected error occured while resolving dataset type name from dataset type code [" + category + "]");
		}
		
		return categoryCode;
	}

	private String getDatasetTestMetadata(IDataSet dataSet,  IEngUserProfile profile, String metadata) throws Exception {
		logger.debug("IN");
		String dsMetadata = null;

		Integer start = new Integer(0);
		Integer limit = new Integer(10);

		dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( profile ));
	
		try {
			dataSet.loadData(start, limit, GeneralUtilities.getDatasetMaxResults());
			IDataStore dataStore = dataSet.getDataStore();
			DatasetMetadataParser dsp = new DatasetMetadataParser();
			
			JSONArray metadataArray = new JSONArray();
			if (!("").equals(metadata)){
				metadataArray = JSONUtils.toJSONArray(metadata);
			}
			
			IMetaData metaData = dataStore.getMetaData();
			for(int i=0; i<metaData.getFieldCount(); i++){
				IFieldMetaData ifmd = metaData.getFieldMeta(i);
				for(int j=0; j<metadataArray.length(); j++){
					if(ifmd.getName().equals((metadataArray.getJSONObject(j)).getString("name"))){
						if("MEASURE".equals((metadataArray.getJSONObject(j)).getString("fieldType"))){
							ifmd.setFieldType(IFieldMetaData.FieldType.MEASURE);
						}else{
							ifmd.setFieldType(IFieldMetaData.FieldType.ATTRIBUTE);
						}
						break;
					}
				}
			}

			dsMetadata = dsp.metadataToXML(dataStore.getMetaData());
		}
		catch (Exception e) {
			logger.error("Error while executing dataset for test purpose",e);
			return null;		
		}

		logger.debug("OUT");
		return dsMetadata;
	}


}
