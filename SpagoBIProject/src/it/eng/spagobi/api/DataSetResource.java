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
package it.eng.spagobi.api;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.execution.service.ExecuteAdHocUtility;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.tools.dataset.DatasetManagementAPI;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commonj.work.Work;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 */
@Path("/1.0/datasets")
public class DataSetResource extends AbstractSpagoBIResource{

	static private Logger logger = Logger.getLogger(DataSetResource.class);
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getDataSets(@QueryParam("typeDoc") String typeDoc) {
		try {
			List<IDataSet> dataSets = getDatasetManagementAPI().getDataSets();
			return serializeDataSets(dataSets, typeDoc);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/{label}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getDataSet(@PathParam("label") String label) {
		try {
			IDataSet dataSet = getDatasetManagementAPI().getDataSet(label);
			return serializeDataSet(dataSet, null);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/{label}/fields")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDataSetFields(@Context HttpServletRequest req, @PathParam("label") String label) {
		try {
			List<IFieldMetaData> fieldsMetaData = getDatasetManagementAPI().getDataSetFieldsMetadata(label);
			JSONArray fieldsJSON = writeFields(fieldsMetaData);
			JSONObject resultsJSON = new JSONObject();
			resultsJSON.put("results", fieldsJSON);
		
			return resultsJSON.toString();	
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/{label}/data")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDataStore(@Context HttpServletRequest req
			, @PathParam("label") String label
			, @QueryParam("offset") @DefaultValue("-1") int offset
			, @QueryParam("fetchSize") @DefaultValue("-1") int fetchSize
			, @QueryParam("maxResults") @DefaultValue("-1") int maxResults) {
		try {
		
			IDataStore dataStore = getDatasetManagementAPI().getDataStore(label, offset, fetchSize, maxResults);
			Map<String, Object> properties = new HashMap<String, Object>();
			JSONArray fieldOptions = new JSONArray("[{id: 1, options: {measureScaleFactor: 0.5}}]");
			properties.put(JSONDataWriter.PROPERTY_FIELD_OPTION, fieldOptions);
			JSONDataWriter dataSetWriter = new JSONDataWriter(properties);
			JSONObject gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
			
			return gridDataFeed.toString();	
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/enterprise")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getEnterpriseDataSet(@QueryParam("typeDoc") String typeDoc) {
		try {
			List<IDataSet> dataSets = getDatasetManagementAPI().getEnterpriseDataSet();
			return serializeDataSets(dataSets, typeDoc);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/owned")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getOwnedDataSet(@QueryParam("typeDoc") String typeDoc) {
		try {
			List<IDataSet> dataSets = getDatasetManagementAPI().getOwnedDataSet();
			return serializeDataSets(dataSets, typeDoc);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/shared")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getSharedDataSet(@QueryParam("typeDoc") String typeDoc) {
		try {
			List<IDataSet> dataSets = getDatasetManagementAPI().getSharedDataSet();
			return serializeDataSets(dataSets, typeDoc);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}
	}
	
	@GET
	@Path("/uncertified")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getUncertifiedDataSet(@QueryParam("typeDoc") String typeDoc) {
		try {
			List<IDataSet> dataSets = getDatasetManagementAPI().getUncertifiedDataSet();
			return serializeDataSets(dataSets, typeDoc);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@GET
	@Path("/mydata")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getMyDataDataSet(@QueryParam("typeDoc") String typeDoc) {
		try {
			List<IDataSet> dataSets = getDatasetManagementAPI().getMyDataDataSet();
			return serializeDataSets(dataSets, typeDoc);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}
	}
		

	


//	@GET
//	@Path("/getflatdataset")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getFlatDataSet(@Context HttpServletRequest req) {
//		IDataSetDAO dataSetDao = null;
//		List<IDataSet> dataSets;
//		IEngUserProfile profile = (IEngUserProfile) req.getSession()
//				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
//		JSONObject JSONReturn = new JSONObject();
//		JSONArray datasetsJSONArray = new JSONArray();
//		try {
//			dataSetDao = DAOFactory.getDataSetDAO();
//			dataSetDao.setUserProfile(profile);
//			dataSets = dataSetDao.loadFlatDatasets(profile.getUserUniqueIdentifier().toString());
//
//			datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer(
//					"application/json").serialize(dataSets, null);
//			
//			JSONArray datasetsJSONReturn = putActions(profile, datasetsJSONArray, null);
//
//			JSONReturn.put("root", datasetsJSONReturn);
//
//		} catch (Throwable t) {
//			throw new SpagoBIServiceException(
//					"An unexpected error occured while instatiating the dao", t);
//		}
//		return JSONReturn.toString();
//
//	}
	
	
	// ===================================================================
	// UTILITY METHODS
	// ===================================================================
	
	public String getUserId() {
		return getUserProfile().getUserUniqueIdentifier().toString();
	}
	
	private UserProfile getUserProfile() {
		UserProfile profile = this.getIOManager().getUserProfile();
		return profile;
	}
//	private IDataSetDAO getDataSetDAO() {
//		IDataSetDAO dataSetDao = null;
//		try {
//			dataSetDao = DAOFactory.getDataSetDAO();
//		} catch (Throwable t) {
//			throw new SpagoBIRuntimeException("An unexpected error occured while instatiating the DAO", t);
//		} 
//		
//	
//		dataSetDao.setUserProfile(getUserProfile());
//		return dataSetDao;
//	}
	
	private DatasetManagementAPI getDatasetManagementAPI() {
		DatasetManagementAPI managementAPI = new DatasetManagementAPI(getUserProfile());
		return managementAPI;
	}
	
	// ==========================================================================================
	// Serialization methods
	// ==========================================================================================
	
	// PROPERTIES TO LOOK FOR INTO THE FIELDS
	public static final String PROPERTY_VISIBLE = "visible";
	public static final String PROPERTY_CALCULATED_EXPERT = "calculatedExpert";
	public static final String PROPERTY_IS_SEGMENT_ATTRIBUTE = "isSegmentAttribute";
	public static final String PROPERTY_IS_MANDATORY_MEASURE = "isMandatoryMeasure";
	public static final String PROPERTY_AGGREGATION_FUNCTION = "aggregationFunction";
	
	public JSONArray writeFields(List<IFieldMetaData> fieldsMetaData) throws Exception {

		// field's meta
		JSONArray fieldsMetaDataJSON = new JSONArray();
		
		List<JSONObject> attributesList = new ArrayList<JSONObject>();
		List<JSONObject> measuresList = new ArrayList<JSONObject>();

		int fieldCount = fieldsMetaData.size();
		logger.debug("Number of fields = " + fieldCount);
		Assert.assertTrue(fieldCount > 0, "Dataset has no fields!!!");

		for (IFieldMetaData fieldMetaData: fieldsMetaData) {
			
			logger.debug("Evaluating field with name [" + fieldMetaData.getName() + "], alias [" + fieldMetaData.getAlias() + "] ...");

			Boolean isCalculatedExpert= (Boolean) fieldMetaData.getProperty(PROPERTY_CALCULATED_EXPERT);
				
			if(isCalculatedExpert!=null && isCalculatedExpert){
				logger.debug("The field is a expert calculated field so we skip it");
				//continue;
			}
				
			Object propertyRawValue = fieldMetaData.getProperty(PROPERTY_VISIBLE);
			logger.debug("Read property " + PROPERTY_VISIBLE + ": its value is [" + propertyRawValue + "]");
				
			if (propertyRawValue != null && !propertyRawValue.toString().equals("")
					&& (Boolean.parseBoolean(propertyRawValue.toString()) == false)) {
				logger.debug("The field is not visible");
				continue;
			} else {
				logger.debug("The field is visible");
			}
			String fieldName = getFieldName(fieldMetaData);
			String fieldHeader = getFieldAlias(fieldMetaData);
			String fieldColumnType = getFieldColumnType(fieldMetaData);
			JSONObject fieldMetaDataJSON = new JSONObject();
			fieldMetaDataJSON.put("id", fieldName);						
			fieldMetaDataJSON.put("alias", fieldHeader);
			fieldMetaDataJSON.put("colType", fieldColumnType);
			FieldType type = fieldMetaData.getFieldType();
			logger.debug("The field type is " + type.name());
			switch (type) {
				case ATTRIBUTE:
					Object isSegmentAttributeObj = fieldMetaData.getProperty(PROPERTY_IS_SEGMENT_ATTRIBUTE);
					logger.debug("Read property " + PROPERTY_IS_SEGMENT_ATTRIBUTE + ": its value is [" + propertyRawValue + "]");
					String attributeNature = (isSegmentAttributeObj != null
							&& (Boolean.parseBoolean(isSegmentAttributeObj.toString())==true)) ? "segment_attribute" : "attribute";
					
					logger.debug("The nature of the attribute is recognized as " + attributeNature);
					fieldMetaDataJSON.put("nature", attributeNature);
					fieldMetaDataJSON.put("funct", AggregationFunctions.NONE);
					fieldMetaDataJSON.put("iconCls", attributeNature);
					break;
				case MEASURE:
					Object isMandatoryMeasureObj = fieldMetaData.getProperty(PROPERTY_IS_MANDATORY_MEASURE);
					logger.debug("Read property " + PROPERTY_IS_MANDATORY_MEASURE + ": its value is [" + isMandatoryMeasureObj + "]");
					String measureNature = (isMandatoryMeasureObj != null && (Boolean.parseBoolean(isMandatoryMeasureObj.toString())==true)) ? "mandatory_measure" : "measure";
					logger.debug("The nature of the measure is recognized as " + measureNature);
					fieldMetaDataJSON.put("nature", measureNature);
					String aggregationFunction = (String) fieldMetaData.getProperty(PROPERTY_AGGREGATION_FUNCTION);
					logger.debug("Read property " + PROPERTY_AGGREGATION_FUNCTION + ": its value is [" + aggregationFunction + "]");
					fieldMetaDataJSON.put("funct", AggregationFunctions.get(aggregationFunction).getName());
					fieldMetaDataJSON.put("iconCls", measureNature);
					String decimalPrecision= (String) fieldMetaData.getProperty(IFieldMetaData.DECIMALPRECISION);
					if(decimalPrecision!=null){
					fieldMetaDataJSON.put("precision", decimalPrecision);
				}else{
					fieldMetaDataJSON.put("precision", "2");
				}
				break;
			}
				
			if(type.equals(it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType.MEASURE)){
				measuresList.add(fieldMetaDataJSON);
			}
			else{
				attributesList.add(fieldMetaDataJSON);
			}
		}

			
		//  put first measures and only after attributes
			
		for (Iterator iterator = measuresList.iterator(); iterator.hasNext();) {
			JSONObject jsonObject = (JSONObject) iterator.next();
			fieldsMetaDataJSON.put(jsonObject);
		}	

		for (Iterator iterator = attributesList.iterator(); iterator.hasNext();) {
			JSONObject jsonObject = (JSONObject) iterator.next();
			fieldsMetaDataJSON.put(jsonObject);
		}	

		return fieldsMetaDataJSON;
	}

	protected String getFieldAlias(IFieldMetaData fieldMetaData) {
		String fieldAlias = fieldMetaData.getAlias() != null ? fieldMetaData.getAlias() : fieldMetaData.getName();
		return fieldAlias;
	}

	protected String getFieldName(IFieldMetaData fieldMetaData) {
		String fieldName = fieldMetaData.getName();
		return fieldName;
	}

	protected String getFieldColumnType(IFieldMetaData fieldMetaData) {
		String fieldColumnType = fieldMetaData.getType().toString();
		fieldColumnType = fieldColumnType.substring(fieldColumnType.lastIndexOf(".")+1); //clean the class type name
		return fieldColumnType;
	}

	private String serializeDataSet(IDataSet dataSet, String typeDocWizard) throws JSONException {
		try {
			JSONObject datasetsJSONObject = (JSONObject) SerializerFactory.getSerializer("application/json").serialize(dataSet, null);
			JSONArray datasetsJSONArray = new JSONArray();
			datasetsJSONArray.put(datasetsJSONObject);
			JSONArray datasetsJSONReturn = putActions(getUserProfile(), datasetsJSONArray, typeDocWizard);
			return datasetsJSONReturn.toString();
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while serializing results",  t);
		}
	}
	
	private String serializeDataSets(List<IDataSet> dataSets, String typeDocWizard)  {
		try {
			JSONArray datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(dataSets, null);
			JSONArray datasetsJSONReturn = putActions(getUserProfile(), datasetsJSONArray, typeDocWizard);
			JSONObject resultJSON = new JSONObject();
			resultJSON.put("root", datasetsJSONReturn);
			return resultJSON.toString();
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while serializing results",  t);
		}	
	}
	
	/**
	 * 
	 * @param profile
	 * @param datasetsJSONArray
	 * @param typeDocWizard Usato dalla my analysis per visualizzare solo i dataset su cui è possi bile costruire 
	 * un certo tipo di analisi selfservice. Al momento filtra la lista dei dataset solo nel caso del GEO in cui
	 * vengono eliminati tutti i dataset che non contengono un riferimento alla dimensione spaziale. Ovviamente il
	 * fatto che un metodo che si chiama putActions filtri in modo silente la lista dei dataset è una follia che andrebbe 
	 * rifattorizzata al più presto.
	 * 
	 * @return
	 * @throws JSONException
	 * @throws EMFInternalError
	 */
	private JSONArray putActions(IEngUserProfile profile, JSONArray datasetsJSONArray, String typeDocWizard)
			throws JSONException, EMFInternalError {
		
		Engine wsEngine = null;
		try{
			wsEngine = ExecuteAdHocUtility.getWorksheetEngine() ;
		}catch(SpagoBIRuntimeException r){
			//the ws engine is not found
			logger.info("Engine not found. ", r);
		}
		
		Engine qbeEngine = null;
		try{
			qbeEngine = ExecuteAdHocUtility.getQbeEngine() ;
		}catch(SpagoBIRuntimeException r){
			//the qbe engine is not found
			logger.info("Engine not found. ", r);
		}
		
		Engine geoEngine = null;
		try{
			geoEngine = ExecuteAdHocUtility.getGeoreportEngine() ;
		}catch(SpagoBIRuntimeException r){
			//the geo engine is not found
			logger.info("Engine not found. ", r);
		}
		
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
		
		JSONObject qbeAction = new JSONObject();
		qbeAction.put("name", "qbe");
		qbeAction.put("description", "Show Qbe");
		
		JSONArray datasetsJSONReturn = new JSONArray();	
		for(int i = 0; i < datasetsJSONArray.length(); i++) {
			JSONObject datasetJSON = datasetsJSONArray.getJSONObject(i);
			JSONArray actions = new JSONArray();
			
			if (typeDocWizard == null){
				actions.put(detailAction);						
				if (profile.getUserUniqueIdentifier().toString().equals(datasetJSON.get("owner"))){
					//the delete action is able only for private dataset
					actions.put(deleteAction);
				}
			}
			
			boolean isGeoDataset = false;
			try{
				String meta = datasetJSON.getString("meta");
				isGeoDataset = ExecuteAdHocUtility.hasGeoHierarchy(meta);				
			} catch(Exception e) {
				logger.error("Error during check of Geo spatial column", e);
			}
			
			if (isGeoDataset && geoEngine != null){
				actions.put(georeportAction); 
			}
						
			
			if (wsEngine != null && (typeDocWizard == null || typeDocWizard.equalsIgnoreCase("REPORT"))){
				actions.put(worksheetAction);			
				if (qbeEngine != null && profile.getFunctionalities().contains(SpagoBIConstants.BUILD_QBE_QUERIES_FUNCTIONALITY)){
					actions.put(qbeAction);
				}
			}
			
			datasetJSON.put("actions", actions);
			
			if ("GEO".equalsIgnoreCase(typeDocWizard)) {
				//if is caming from myAnalysis - create Geo Document - must shows only ds geospatial --> isGeoDataset == true
				if (geoEngine != null  && isGeoDataset) {
					datasetsJSONReturn.put(datasetJSON);
				}	
			} else {
				datasetsJSONReturn.put(datasetJSON);
			}
				
		}
		return datasetsJSONReturn;
	}

}
