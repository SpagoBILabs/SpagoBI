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
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.serializer.DataSetJSONSerializer;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.rest.annotations.ToValidate;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.VersionedDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.dataproxy.FileDataProxy;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;
import it.eng.spagobi.tools.dataset.validation.ErrorField;
import it.eng.spagobi.tools.dataset.validation.GeoDatasetValidatorFactory;
import it.eng.spagobi.tools.dataset.validation.HierarchyLevel;
import it.eng.spagobi.tools.dataset.validation.IDatasetValidator;
import it.eng.spagobi.tools.dataset.validation.IDatasetValidatorFactory;
import it.eng.spagobi.tools.dataset.validation.NumericColumnValidator;
import it.eng.spagobi.tools.dataset.validation.ValidationErrors;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.json.JSONUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
	static private String parsingDSError = "error.mesage.description.data.set.parsing.error";

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
			datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(dataSets, null);
			
			//sets action to modify dataset			
			JSONObject detailAction = new JSONObject();
			detailAction.put("name", "detaildataset");
			detailAction.put("description", "Dataset detail");	
			
			JSONObject deleteAction = new JSONObject();
			deleteAction.put("name", "delete");
			deleteAction.put("description", "Delete dataset");
			
			JSONObject worksheetAction = new JSONObject();
			worksheetAction.put("name", "worksheet");
			worksheetAction.put("description", "Show Worksheet");
			
			JSONObject georeportAction = new JSONObject();
			georeportAction.put("name", "georeport");
			georeportAction.put("description", "Show Map");
			
			
			JSONArray datasetsJSONReturn = new JSONArray();	
			for(int i = 0; i < datasetsJSONArray.length(); i++) {
				JSONArray actions = new JSONArray();
				JSONObject datasetJSON = datasetsJSONArray.getJSONObject(i);
				actions.put(detailAction);		
				actions.put(worksheetAction);
				actions.put(georeportAction); // Annotated view map action to release SpagoBI 4
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
				deleteDatasetFile(ds);
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
	
	public void deleteDatasetFile(IDataSet dataset){
		if (dataset instanceof VersionedDataSet){
			VersionedDataSet versionedDataset = (VersionedDataSet)dataset;
			IDataSet wrappedDataset = versionedDataset.getWrappedDataset();
			
			if (wrappedDataset instanceof FileDataSet){
				FileDataSet fileDataset = (FileDataSet)wrappedDataset;
				String resourcePath = fileDataset.getResourcePath();
				String fileName = fileDataset.getFileName();
				String filePath = resourcePath + File.separatorChar+"dataset"+File.separatorChar+"files"+File.separatorChar;
				File datasetFile = new File(filePath+fileName);
				
				if (datasetFile.exists()){
					boolean isDeleted = datasetFile.delete();
					if (isDeleted){
						logger.debug("Dataset File "+fileName+" has been deleted");
					}
				}
			}
		}
		
	}
	
	@POST
	@Path("/save")
	@ToValidate(typeName= "dataset")
	@Produces(MediaType.APPLICATION_JSON)
	public String saveDataSet(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		try {
			IDataSetDAO dao=DAOFactory.getDataSetDAO();
			dao.setUserProfile(profile);
			String label = (String)req.getParameter("label");			
			String meta = (String)req.getParameter(DataSetConstants.METADATA);			
			
			IDataSet ds  = dao.loadActiveDataSetByLabel(label);
			IDataSet dsNew = recoverDataSetDetails(req, ds, true);
			
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

	@POST
	@Path("/testDataSet")
	@Produces(MediaType.APPLICATION_JSON)
	public String testDataSet(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		try {
			IDataSetDAO dao=DAOFactory.getDataSetDAO();
			dao.setUserProfile(profile);
			String label = (String)req.getParameter("label");			
			String meta = (String)req.getParameter(DataSetConstants.METADATA);			
			
			IDataSet dsToTest = recoverDataSetDetails(req, null, false);
			
			logger.debug("Recalculating dataset's metadata: executing the dataset...");
			String dsMetadata = null;
			dsMetadata = getDatasetTestMetadata(dsToTest, profile, meta);
			JSONArray datasetColumns = getDatasetColumns(dsToTest, profile);
			dsToTest.setDsMetadata(dsMetadata);	
			LogMF.debug(logger, "Dataset executed, metadata are [{0}]", dsMetadata);

			List<IDataSet> dataSets = new ArrayList();
			dataSets.add(dsToTest);

			JSONObject metaJSONobject = DataSetJSONSerializer.serializeGenericMetadata(dsMetadata);
			JSONObject JSONReturn = new JSONObject();
			JSONReturn.put("meta", metaJSONobject);
			JSONReturn.put("datasetColumns", datasetColumns);
			return JSONReturn.toString();
		} catch (SpagoBIRuntimeException ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.TEST", null, "ERR");
			logger.debug(ex.getMessage());
			try {
				return ( ExceptionUtilities.serializeException(ex.getMessage(),null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		} catch (RuntimeException ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.TEST", null, "ERR");
			logger.debug(canNotFillResponseError);	
			try {
				return ( ExceptionUtilities.serializeException(parsingDSError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}catch (Exception ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.TEST", null, "ERR");
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
	@Path("/getDataStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDataStore(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		Integer start = new Integer(0);
		Integer limit = new Integer(10);
		Integer resultNumber = null;
		Integer maxSize = null;


		try {
			IDataSetDAO dao=DAOFactory.getDataSetDAO();
			dao.setUserProfile(profile);
			String datasetMetadata = (String)req.getParameter("datasetMetadata");

			
			IDataSet dataSet = recoverDataSetDetails(req, null, false);
			String dsMetadata = getDatasetTestMetadata(dataSet, profile, datasetMetadata);
			dataSet.setDsMetadata(dsMetadata);	
			
			
			dataSet.loadData(start, limit, GeneralUtilities.getDatasetMaxResults());
			IDataStore dataStore = dataSet.getDataStore(); 

			resultNumber = (Integer)dataStore.getMetaData().getProperty("resultNumber");

			logger.debug("Total records: " + resultNumber);	
			
			boolean overflow = maxSize != null && resultNumber >= maxSize;
			if (overflow) {
				logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
			}
			
			JSONDataWriter dataSetWriter = new JSONDataWriter();
			dataSetWriter.setSetRenderer(true);
			JSONObject gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
			//remove the recNo inside fields that is not managed by DynamicGridPanel
			JSONObject metadata = gridDataFeed.getJSONObject("metaData");
			if (metadata != null){
				JSONArray fieldsArray = metadata.getJSONArray("fields");
				boolean elementFound = false;
				int i = 0;
				for (; i < fieldsArray.length(); i++) {
				    String element = fieldsArray.getString(i);
				   if (element.equals("recNo")){
					   elementFound = true;
					   break;
				   }
				}
				if (elementFound){
					fieldsArray.remove(i);
				}

			}
			
			//Dataset Validation ---------------------------------------------
			if (datasetMetadata != null)	{
				ValidationErrors validationErrors = new ValidationErrors();				
				
				//validation of columns with specified Hierarchies and with numeric Type
				Map<String, HierarchyLevel> hierarchiesColumnsToCheck = getHierarchiesColumnsToCheck(datasetMetadata);
			
				if (!hierarchiesColumnsToCheck.isEmpty()){
					//We get the category of the dataset and with this we search the appropriate validator
					Integer categoryId = dataSet.getCategoryId();
					if (categoryId != null){
						IDomainDAO domainDao = DAOFactory.getDomainDAO();
						Domain domain = domainDao.loadDomainById(categoryId);
						String categoryValueName = domain.getValueName();
						
						//Validate only if there are the proper metadata set
						IDatasetValidatorFactory geoValidatorFactory = new GeoDatasetValidatorFactory();
						
						IDatasetValidator geoValidator = geoValidatorFactory.getValidator(categoryValueName);
						
						if (geoValidator != null){

							//Validate the dataset and return the fields not valid
							ValidationErrors hierarchiesColumnsValidationErrors = geoValidator.validateDataset(dataStore,hierarchiesColumnsToCheck);
							if (!hierarchiesColumnsValidationErrors.isEmpty()){
								validationErrors.addAll(hierarchiesColumnsValidationErrors);
							}
							
						}
	
					}					
					

				}
				if (!validationErrors.isEmpty()){
					//this create an array containing the fields with error for each rows
					JSONArray errorsArray  = validationErrorsToJSONObject(validationErrors);
					gridDataFeed.put("validationErrors", errorsArray);

				}	
				
				
			}		
			//-----------------------------------------------------------------
			
			return gridDataFeed.toString();
			
			
			
			
		} catch (SpagoBIRuntimeException ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.GETDATASTORE", null, "ERR");
			logger.debug(ex.getMessage());
			try {
				return ( ExceptionUtilities.serializeException(ex.getMessage(),null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		} catch (RuntimeException ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.GETDATASTORE", null, "ERR");
			logger.debug(canNotFillResponseError);	
			try {
				return ( ExceptionUtilities.serializeException(parsingDSError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}catch (Exception ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SET.GETDATASTORE", null, "ERR");
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
	

	
	private Map<String, HierarchyLevel> getHierarchiesColumnsToCheck(
			String datasetMetadata) throws JsonMappingException,
			JsonParseException, JSONException, IOException {
		JSONObject metadataObject = null;

		Map<String, HierarchyLevel> hierarchiesColumnsToCheck = new HashMap<String, HierarchyLevel>();
		

		if ((!datasetMetadata.equals("")) && (!datasetMetadata.equals("[]"))) {
			metadataObject = JSONUtils.toJSONObject(datasetMetadata);
			JSONArray columnsMetadataArray = metadataObject
					.getJSONArray("columns");
			// JSONArray datasetMetadataArray =
			// metadataObject.getJSONArray("dataset");

			for (int j = 0; j < columnsMetadataArray.length(); j++) {
				JSONObject columnJsonObject = columnsMetadataArray
						.getJSONObject(j);
				String columnName = columnJsonObject.getString("column");
				String propertyName = columnJsonObject.getString("pname");
				String propertyValue = columnJsonObject.getString("pvalue");

				if (propertyName.equals("hierarchy")) {
					HierarchyLevel hierarchyLevel = hierarchiesColumnsToCheck
							.get(columnName);

					if (hierarchyLevel == null) {
						hierarchyLevel = new HierarchyLevel();
						hierarchyLevel.setHierarchy_name(propertyValue);
						hierarchiesColumnsToCheck.put(columnName,
								hierarchyLevel);
					} else {
						hierarchyLevel.setHierarchy_name(propertyValue);
						hierarchiesColumnsToCheck.put(columnName,
								hierarchyLevel);
					}
				}
				if (propertyName.equals("hierarchy_level")) {
					HierarchyLevel hierarchyLevel = hierarchiesColumnsToCheck
							.get(columnName);

					if (hierarchyLevel == null) {
						hierarchyLevel = new HierarchyLevel();
						hierarchyLevel.setLevel_name(propertyValue);
						hierarchiesColumnsToCheck.put(columnName,
								hierarchyLevel);
					} else {
						hierarchyLevel.setLevel_name(propertyValue);
						hierarchiesColumnsToCheck.put(columnName,
								hierarchyLevel);
					}
				}
				if (propertyName.equalsIgnoreCase("Type")){
					if(( propertyValue.equalsIgnoreCase("Integer")) || (propertyValue.equalsIgnoreCase("Double")) ){
						HierarchyLevel hierarchyLevel = hierarchiesColumnsToCheck.get(columnName);
						if (hierarchyLevel == null) {
							hierarchyLevel = new HierarchyLevel();
							hierarchyLevel.setColumn_type("numeric");
							hierarchiesColumnsToCheck.put(columnName,
									hierarchyLevel);
						} else {
							hierarchyLevel.setColumn_type("numeric");
							hierarchiesColumnsToCheck.put(columnName,
									hierarchyLevel);
						}
					} 
				}

			}

		}
		return hierarchiesColumnsToCheck;
	}
	public JSONArray validationErrorsToJSONObject(ValidationErrors validationErrors) throws JSONException{
		
		JSONArray errorsArray = new JSONArray();		
		Map<Integer, List<ErrorField>> allErrors = validationErrors.getAllErrors();
		
		for (Map.Entry<Integer, List<ErrorField>> entry : allErrors.entrySet())
		{
		   JSONObject rowJSONObject = new JSONObject();
		   rowJSONObject.put("id", String.valueOf(entry.getKey()));
		    
		   List<ErrorField> rowErrors = entry.getValue();
		   for (ErrorField  errorColumn : rowErrors){
			   rowJSONObject.put("column_"+errorColumn.getColumnIndex(),errorColumn.getErrorDescription());
		   }
		   
		   errorsArray.put(rowJSONObject);
		}
		return errorsArray;
		
		
		
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

	private IDataSet recoverDataSetDetails (HttpServletRequest req, IDataSet dataSet, boolean savingDataset) throws EMFUserError, SourceBeanException, IOException  {
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
		Boolean newFileUploaded = false;
		if (req.getParameter("fileUploaded") != null){
			newFileUploaded = Boolean.valueOf(((String)req.getParameter("fileUploaded")));
		}

				
		try{
			String config = "{}";
			if (configuration != null)
				config = JSONUtils.escapeJsonString(configuration);
			
			JSONObject jsonConf  = ObjectUtils.toJSONObject(config);			
			jsonDsConfig.put(DataSetConstants.FILE_TYPE, fileType);
			if (savingDataset){
				//when saving the dataset the file associated will get the dataset label name
				jsonDsConfig.put(DataSetConstants.FILE_NAME, label+"."+fileType.toLowerCase());
			} else {
				jsonDsConfig.put(DataSetConstants.FILE_NAME, fileName);
			}
			jsonDsConfig.put(DataSetConstants.CSV_FILE_DELIMITER_CHARACTER, csvDelimiter);
			jsonDsConfig.put(DataSetConstants.CSV_FILE_QUOTE_CHARACTER, csvQuote);
			jsonDsConfig.put(DataSetConstants.XSL_FILE_SKIP_ROWS, skipRows);
			jsonDsConfig.put(DataSetConstants.XSL_FILE_LIMIT_ROWS, limitRows);
			jsonDsConfig.put(DataSetConstants.XSL_FILE_SHEET_NUMBER, xslSheetNumber);
		

		}catch (Exception e){
			logger.error("Error while defining dataset configuration.  Error: " + e.getMessage());
		}
		type =  getDatasetTypeName(type); 
		FileDataSet toReturn = new FileDataSet();
		if (!insertion){				
			toReturn.setId(dataSet.getId());			
			toReturn.setName(dataSet.getName());
			toReturn.setLabel(dataSet.getLabel());
			toReturn.setDescription(dataSet.getDescription());	

			// set detail dataset ID
			toReturn.setTransformerId((dataSet.getTransformerId() == null)? null:dataSet.getTransformerId());
			toReturn.setPivotColumnName(dataSet.getPivotColumnName());
			toReturn.setPivotRowName(dataSet.getPivotRowName());
			toReturn.setPivotColumnValue(dataSet.getPivotColumnValue());
			toReturn.setNumRows(dataSet.isNumRows());			
			toReturn.setParameters(dataSet.getParameters());		
			toReturn.setDsMetadata(dataSet.getDsMetadata());		
			
			//set persist values
			toReturn.setPersisted(dataSet.isPersisted());
			toReturn.setDataSourcePersistId(dataSet.getDataSourcePersistId());
			toReturn.setFlatDataset(dataSet.isFlatDataset());
			toReturn.setDataSourceFlatId(dataSet.getDataSourceFlatId());
			toReturn.setFlatTableName(dataSet.getFlatTableName());
			

		}
		
		if (id == -1){
			//creating a new dataset, the file uploaded has to be renamed and moved
			((FileDataSet)toReturn).setUseTempFile(true);
			
			if (savingDataset){
				//rename and move the file
				String resourcePath = ((FileDataSet)toReturn).getResourcePath();
				renameAndMoveDatasetFile(fileName,label,resourcePath, fileType);
				((FileDataSet)toReturn).setUseTempFile(false);
			}
		} else {
			//reading or modifying a existing dataset
			
			if (newFileUploaded){
				//modifying an existing dataset with a new file uploaded
				((FileDataSet)toReturn).setUseTempFile(true);
				
				//saving the existing dataset with a new file associated
				if (savingDataset){
					//rename and move the file
					String resourcePath = ((FileDataSet)toReturn).getResourcePath();
					renameAndMoveDatasetFile(fileName,label,resourcePath, fileType);
					((FileDataSet)toReturn).setUseTempFile(false);
				}

			} else {
				//using existing dataset file, file in correct place
				((FileDataSet)toReturn).setUseTempFile(false);
			}

		}
		
		

		
		//next steps are necessary to define a valid dataProxy
		((FileDataSet)toReturn).setConfiguration(jsonDsConfig.toString());
		if (savingDataset){
			//the file used will have the name equals to dataset's label
			((FileDataSet)toReturn).setFileName(label+"."+fileType.toLowerCase());
		} else {
			((FileDataSet)toReturn).setFileName(fileName);
		}
				
		//update general informations
		toReturn.setDsType(type);
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
	
	//This method rename a file and move it from resources\dataset\files\temp to resources\dataset\files
	private void renameAndMoveDatasetFile(String originalFileName, String newFileName, String resourcePath, String fileType){
		String filePath = resourcePath + File.separatorChar+"dataset"+File.separatorChar+"files"+File.separatorChar+"temp"+File.separatorChar;
		String fileNewPath = resourcePath + File.separatorChar+"dataset"+File.separatorChar+"files"+File.separatorChar;
		
		File originalDatasetFile = new File(filePath + originalFileName); 
		File newDatasetFile = new File (fileNewPath + newFileName + "."+fileType.toLowerCase());
		if (originalDatasetFile.exists()){
			/*
			 *  This method copies the contents of the specified source file to the specified destination file.
			 *  The directory holding the destination file is created if it does not exist. 
			 *  If the destination file exists, then this method will overwrite it. 
			 */
			try {
				FileUtils.copyFile(originalDatasetFile, newDatasetFile);
				
				//Then delete temp file
				originalDatasetFile.delete();
			} catch (IOException e) {
				logger.debug("Cannot move dataset File");
				throw new SpagoBIRuntimeException("Cannot move dataset File", e);
			}
		}

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
			if (dataSet instanceof FileDataSet){
				FileDataSet fileDataSet = (FileDataSet)dataSet;
				FileDataProxy fileDataProxy = fileDataSet.getDataProxy();
				fileDataProxy.setUseTempFile(fileDataSet.useTempFile); //inform the DataProxy to use a tempFile or not
			}
			dataSet.loadData(start, limit, GeneralUtilities.getDatasetMaxResults());
			IDataStore dataStore = dataSet.getDataStore();
			DatasetMetadataParser dsp = new DatasetMetadataParser();


			JSONObject metadataObject = new JSONObject();
			JSONArray columnsMetadataArray = new JSONArray();
			JSONArray datasetMetadataArray = new JSONArray();

			if ((!metadata.equals("")) && (!metadata.equals("[]")))	{
				metadataObject = JSONUtils.toJSONObject(metadata);			
				columnsMetadataArray =  metadataObject.getJSONArray("columns");
				datasetMetadataArray =  metadataObject.getJSONArray("dataset");
			}						
			
			IMetaData metaData = dataStore.getMetaData();
			//Setting general custom properties for entire Dataset
			for(int i=0; i<datasetMetadataArray.length(); i++){
				JSONObject datasetJsonObject = datasetMetadataArray.getJSONObject(i);
				String propertyName = datasetJsonObject.getString("pname");
				String propertyValue = datasetJsonObject.getString("pvalue");
				metaData.setProperty(propertyName, propertyValue);
			}
			for(int i=0; i<metaData.getFieldCount(); i++){
				IFieldMetaData ifmd = metaData.getFieldMeta(i);
				
				String gussedType = guessColumnType(dataStore, i);
				
				//Setting mandatory property to defaults, if specified they will be overridden
				if("Double".equalsIgnoreCase(gussedType)) {
					ifmd.setFieldType(IFieldMetaData.FieldType.MEASURE);
					Class type = Class.forName("java.lang.Double");
					ifmd.setType(type);	
				} else {
					ifmd.setFieldType(IFieldMetaData.FieldType.ATTRIBUTE);
					Class type = Class.forName("java.lang.String");
					ifmd.setType(type);	
				}
				

				
				for(int j=0; j<columnsMetadataArray.length(); j++){
					JSONObject columnJsonObject = columnsMetadataArray.getJSONObject(j);
					String columnName = columnJsonObject.getString("column");
					if (ifmd.getName().equals( columnName )){
						
						String propertyName = columnJsonObject.getString("pname");
						String propertyValue = columnJsonObject.getString("pvalue");
						
						//FieldType is a mandatory property
						if (propertyName.equalsIgnoreCase("fieldType")){
							if (propertyValue.equalsIgnoreCase("MEASURE")){
								ifmd.setFieldType(IFieldMetaData.FieldType.MEASURE);
							} 
							else if(propertyValue.equalsIgnoreCase("ATTRIBUTE")) {
								ifmd.setFieldType(IFieldMetaData.FieldType.ATTRIBUTE);
							}
							else {
								if("Double".equalsIgnoreCase(gussedType)) {
									ifmd.setFieldType(IFieldMetaData.FieldType.MEASURE);
								} else {
									ifmd.setFieldType(IFieldMetaData.FieldType.ATTRIBUTE);
								}
							}
						} 
						//Type is a mandatory property
						else if (propertyName.equalsIgnoreCase("Type")){
							if(propertyValue.equalsIgnoreCase("Integer")){
								Class type = Class.forName("java.lang.Integer");
								ifmd.setType(type);								
							} else if(propertyValue.equalsIgnoreCase("Double")){
								Class type = Class.forName("java.lang.Double");
								ifmd.setType(type);	
							} else if(propertyValue.equalsIgnoreCase("String")){
								Class type = Class.forName("java.lang.String");
								ifmd.setType(type);	
							}
							else {
								if("Double".equalsIgnoreCase(gussedType)) {
									Class type = Class.forName("java.lang.Double");
									ifmd.setType(type);	
								} else {
									Class type = Class.forName("java.lang.String");
									ifmd.setType(type);
								}
								
							}
						}
						else {
							//Custom Properties
							ifmd.setProperty(propertyName, propertyValue);

						}
						
					}
				}

			}

			dsMetadata = dsp.metadataToXML(dataStore.getMetaData()); //using new parser
		}
		catch (Exception e) {
			logger.error("Error while executing dataset for test purpose",e);
			return null;		
		}

		logger.debug("OUT");
		return dsMetadata;
	}
	
	/**
	 * @param dataStore
	 * @param i
	 * @return
	 */
	private String guessColumnType(IDataStore dataStore, int columnIndex) {
		boolean isNumeric = true;
		for(int i = 0; i < Math.min(10, dataStore.getRecordsCount()); i++) {
			IRecord record = dataStore.getRecordAt(i);
			IField field = record.getFieldAt(columnIndex);
			Object value = field.getValue();
			try {
				Double.parseDouble(value.toString());
			} catch(Throwable t) {
				isNumeric = false;
				break;
			}
		}

		return isNumeric? "Double": "String";
	}


	public JSONArray getDatasetColumns(IDataSet dataSet,  IEngUserProfile profile) throws Exception{
		logger.debug("IN");

		Integer start = new Integer(0);
		Integer limit = new Integer(10);
		
		JSONArray columnsJSON = new JSONArray();


		try {
			dataSet.loadData(start, limit, GeneralUtilities.getDatasetMaxResults());
			IDataStore dataStore = dataSet.getDataStore();

			IMetaData metaData = dataStore.getMetaData();
			for(int i=0; i<metaData.getFieldCount(); i++){
				IFieldMetaData ifmd = metaData.getFieldMeta(i);
				String name = ifmd.getName();
				JSONObject jsonMeta = new JSONObject();
				jsonMeta.put("columnName", name);
				columnsJSON.put(jsonMeta);
				

			}

		}
		catch (RuntimeException re){
			throw re;
		}
		catch (Exception e) {
			logger.error("Error while getting dataset columns",e);
			return null;		
		}

		logger.debug("OUT");
		return columnsJSON;
		
	}


}
