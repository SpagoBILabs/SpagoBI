/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2016 Engineering Ingegneria Informatica S.p.A.
 *
 * Knowage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Knowage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.eng.spagobi.api;

import static it.eng.spagobi.commons.constants.SpagoBIConstants.DATE_RANGE_OPTIONS_KEY;
import static it.eng.spagobi.commons.constants.SpagoBIConstants.DATE_RANGE_QUANTITY_JSON;
import static it.eng.spagobi.commons.constants.SpagoBIConstants.DATE_RANGE_TYPE_JSON;
import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.RequestContainerAccess;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.AnalyticalModelDocumentManagementAPI;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.handlers.DocumentUrlManager;
import it.eng.spagobi.analiticalmodel.execution.bo.defaultvalues.DefaultValue;
import it.eng.spagobi.analiticalmodel.execution.bo.defaultvalues.DefaultValuesList;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IBinContentDAO;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.commons.utilities.indexing.LuceneIndexer;
import it.eng.spagobi.commons.utilities.messages.IMessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilder;
import it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory;
import it.eng.spagobi.services.rest.annotations.ManageAuthorization;
import it.eng.spagobi.services.rest.annotations.UserConstraint;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.rest.RestUtilities;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Path("/1.0/documentexecution")
@ManageAuthorization
public class DocumentExecutionResource extends AbstractSpagoBIResource {
	
	// GENERAL METADATA NAMES
	public static final String LABEL = "metadata.docLabel";
	public static final String NAME = "metadata.docName";
	public static final String DESCR = "metadata.docDescr";
	public static final String TYPE = "metadata.docType";
	public static final String ENG_NAME = "metadata.docEngine";
	public static final String RATING = "metadata.docRating";
	public static final String SUBOBJ_NAME = "metadata.subobjName";
	public static final String METADATA = "METADATA";
	public static final String NODE_ID_SEPARATOR = "___SEPA__";
	public static final String EDIT_MODE_ON = "true";

	public static String MODE_SIMPLE = "simple";
	// public static String MODE_COMPLETE = "complete";
	// public static String START = "start";
	// public static String LIMIT = "limit";

	public static final String SERVICE_NAME = "DOCUMENT_EXECUTION_RESOURCE";
	private static final String DESCRIPTION_FIELD = "description";

	private static final String VALUE_FIELD = "value";

	private static final String LABEL_FIELD = "label";

	private static IMessageBuilder message = MessageBuilderFactory.getMessageBuilder();

	private static final String[] VISIBLE_COLUMNS = new String[] { VALUE_FIELD, LABEL_FIELD, DESCRIPTION_FIELD };
	private static final String METADATA_DIR = "metadata";

	private class DocumentExecutionException extends Exception {
		private static final long serialVersionUID = -1882998632783944575L;

		DocumentExecutionException(String message) {
			super(message);
		}
	}

	static protected Logger logger = Logger.getLogger(DocumentExecutionResource.class);
	protected AnalyticalModelDocumentManagementAPI documentManager = new AnalyticalModelDocumentManagementAPI(getUserProfile());



	private String BuildEngineUrlString(JSONObject reqVal, BIObject obj, HttpServletRequest req, String isForExport, String cockpitSelections)
			throws JSONException {
		String ret = "";

		if (obj.getBiObjectTypeCode().equals(SpagoBIConstants.OLAP_TYPE_CODE)) {
			JSONObject parameters = reqVal.getJSONObject("parameters");
			if (parameters.length() > 0) {

				String subViewObjectID = parameters.optString("subobj_id");
				String subViewObjectName = parameters.optString("subobj_name");
				String subViewObjectDescription = parameters.optString("subobj_description");
				String subViewObjectVisibility = parameters.optString("subobj_visibility");

				if (!StringUtilities.isEmpty(subViewObjectID)) {
					ret += "&" + AbstractEngineStartAction.SUBOBJ_ID + "=" + subViewObjectID;
				}
				if (!StringUtilities.isEmpty(subViewObjectName)) {
					ret += "&" + AbstractEngineStartAction.SUBOBJ_NAME + "=" + subViewObjectName;
				}
				if (!StringUtilities.isEmpty(subViewObjectDescription)) {
					ret += "&" + AbstractEngineStartAction.SUBOBJ_DESCRIPTION + "=" + subViewObjectDescription;
				}
				if (!StringUtilities.isEmpty(subViewObjectVisibility)) {
					ret += "&" + AbstractEngineStartAction.SUBOBJ_VISIBILITY + "=" + subViewObjectVisibility;
				}
			}
		}

		// REPORT BIRT - JASPER
		// MOBILE
		if (obj.getBiObjectTypeCode().equals(SpagoBIConstants.REPORT_TYPE_CODE)
				&& obj.getEngine() != null
				&& (obj.getEngine().getLabel().equals(SpagoBIConstants.BIRT_ENGINE_LABEL) || obj.getEngine().getLabel()
						.equals(SpagoBIConstants.JASPER_ENGINE_LABEL)) && req.getHeader("User-Agent").indexOf("Mobile") != -1) {
			ret = ret + "&outputType=PDF";
		}
		// COCKPIT
		if (obj.getBiObjectTypeCode().equals(SpagoBIConstants.DOCUMENT_COMPOSITE_TYPE)) {
			if (!("".equalsIgnoreCase(isForExport))) {
				ret += "&IS_FOR_EXPORT=" + isForExport;
				if (!("".equalsIgnoreCase(cockpitSelections))) {
					ret += "&COCKPIT_SELECTIONS=" + cockpitSelections;
				}
			}
		}

		return ret;
	}




//
//	@POST
//	@Path("/parametervalues")
//	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
//	// public Response getParameterValues(@QueryParam("label") String label, @QueryParam("role") String role, @QueryParam("biparameterId") String biparameterId,
//	// @QueryParam("mode") String mode, @QueryParam("treeLovNode") String treeLovNode,
//	// // @QueryParam("treeLovNode") Integer treeLovNodeLevel,
//	// @Context HttpServletRequest req) throws EMFUserError {
//	public Response getParameterValues(@Context HttpServletRequest req) throws EMFUserError, IOException, JSONException {
//
//		RequestContainer aRequestContainer = RequestContainerAccess.getRequestContainer(req);
//		Locale locale = GeneralUtilities.getCurrentLocale(aRequestContainer);
//
//		String role;
//		String label;
//		String biparameterId;
//		String treeLovNode;
//		String mode;
//		// GET PARAMETER
//
//		JSONObject requestVal = RestUtilities.readBodyAsJSONObject(req);
//		role = (String) requestVal.opt("role");
//		label = (String) requestVal.opt("label");
//		biparameterId = (String) requestVal.opt("biparameterId");
//		treeLovNode = (String) requestVal.opt("treeLovNode");
//		mode = (String) requestVal.opt("mode");
//
//		IBIObjectDAO dao = DAOFactory.getBIObjectDAO();
//		BIObject biObject = dao.loadBIObjectForExecutionByLabelAndRole(label, role);
//
//		BIObjectParameter biObjectParameter = null;
//		List<BIObjectParameter> parameters = biObject.getBiObjectParameters();
//		for (int i = 0; i < parameters.size(); i++) {
//			BIObjectParameter p = parameters.get(i);
//			if (biparameterId.equalsIgnoreCase(p.getParameterUrlName())) {
//				biObjectParameter = p;
//				break;
//			}
//		}
//
//		String treeLovNodeValue;
//		Integer treeLovNodeLevel;
//
//		if (treeLovNode.contains("lovroot")) {
//			treeLovNodeValue = "lovroot";
//			treeLovNodeLevel = 0;
//		} else {
//			String[] splittedNode = treeLovNode.split(NODE_ID_SEPARATOR);
//			treeLovNodeValue = splittedNode[0];
//			treeLovNodeLevel = new Integer(splittedNode[1]);
//		}
//
//		// ArrayList<HashMap<String, Object>> result = DocumentExecutionUtils.getLovDefaultValues(
//		// role, biObject, biObjectParameter, requestVal, treeLovNodeLevel, treeLovNodeValue, req);
//		HashMap<String, Object> defaultValuesData = DocumentExecutionUtils.getLovDefaultValues(role, biObject, biObjectParameter, requestVal, treeLovNodeLevel,
//				treeLovNodeValue, req);
//
//		ArrayList<HashMap<String, Object>> result = (ArrayList<HashMap<String, Object>>) defaultValuesData.get(DocumentExecutionUtils.DEFAULT_VALUES);
//
//		HashMap<String, Object> resultAsMap = new HashMap<String, Object>();
//
//		if (result != null && result.size() > 0) {
//			resultAsMap.put("filterValues", result);
//			resultAsMap.put("errors", new ArrayList());
//		} else {
//			resultAsMap.put("filterValues", new ArrayList());
//
//			List errorList = DocumentExecutionUtils.handleNormalExecutionError(this.getUserProfile(), biObject, req,
//					this.getAttributeAsString("SBI_ENVIRONMENT"), role, biObjectParameter.getParameter().getModalityValue().getSelectionType(), null, locale);
//
//			resultAsMap.put("errors", errorList);
//		}
//
//		logger.debug("OUT");
//		return Response.ok(resultAsMap).build();
//	}

	/**
	 * @return the list of values when input parameter (urlName) is correlated to another
	 */
	@GET
	@Path("/filterlist")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public Response getDocumentExecutionFilterList(@QueryParam("label") String label, @QueryParam("role") String role,
			@QueryParam("parameters") String jsonParameters, @QueryParam("urlName") String urlName, @Context HttpServletRequest req) {
		logger.debug("IN");

		String toBeReturned = "{}";

		try {
			role = getExecutionRole(role);

		} catch (DocumentExecutionException e) {
			return Response.ok("{errors: '" + e.getMessage() + "', }").build();
		} catch (Exception e) {
			logger.error("Error while getting the document execution filterlist", e);
			throw new SpagoBIRuntimeException("Error while getting the document execution filterlist", e);
		}

		logger.debug("OUT");
		return Response.ok(toBeReturned).build();
	}

	/**
	 * Produces a json of document metadata grouped by typeCode ("GENERAL_META", "LONG_TEXT", "SHORT_TEXT","FILE")
	 *
	 * @param id
	 *            of document
	 * @param id
	 *            of subObject
	 * @param httpRequest
	 * @return a response with a json
	 * @throws EMFUserError
	 */
	@GET
	@Path("/{id}/documentMetadata")
	public Response documentMetadata(@PathParam("id") Integer objectId, @QueryParam("subobjectId") Integer subObjectId)
			throws EMFUserError {

		try {
			RequestContainer aRequestContainer = RequestContainerAccess.getRequestContainer(this.getServletRequest());
			Locale locale = GeneralUtilities.getCurrentLocale(aRequestContainer);

			Map<String, JSONArray> documentMetadataMap = new HashMap<String, JSONArray>();

			JSONArray generalMetadata = new JSONArray();
			documentMetadataMap.put("GENERAL_META", generalMetadata);

			MessageBuilder msgBuild = new MessageBuilder();

			// START GENERAL METADATA
			if (subObjectId != null) {
				// SubObj Name
				String textSubName = msgBuild.getMessage(SUBOBJ_NAME, locale);
				SubObject subobj = DAOFactory.getSubObjectDAO().getSubObject(subObjectId);
				addMetadata(generalMetadata, textSubName, subobj.getName());
			}

			BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(objectId);

			// Obj Label
			String textLabel = msgBuild.getMessage(LABEL, locale);
			addMetadata(generalMetadata, textLabel, obj.getLabel());

			// Obj Name
			String textName = msgBuild.getMessage(NAME, locale);
			addMetadata(generalMetadata, textName, obj.getName());

			// Obj Type
			String textType = msgBuild.getMessage(TYPE, locale);
			addMetadata(generalMetadata, textType, obj.getBiObjectTypeCode());

			// Obj Description
			String description = msgBuild.getMessage(DESCR, locale);
			addMetadata(generalMetadata, description, obj.getDescription());

			// Obj Engine Name
			String textEngName = msgBuild.getMessage(ENG_NAME, locale);
			addMetadata(generalMetadata, textEngName, obj.getEngine().getName());

			// END GENERAL METADATA

			List metadata = DAOFactory.getObjMetadataDAO().loadAllObjMetadata();
			if (metadata != null && !metadata.isEmpty()) {
				Iterator it = metadata.iterator();
				while (it.hasNext()) {
					ObjMetadata objMetadata = (ObjMetadata) it.next();
					if (!objMetadata.getDataTypeCode().equals("FILE")) {
						ObjMetacontent objMetacontent = DAOFactory.getObjMetacontentDAO().loadObjMetacontent(objMetadata.getObjMetaId(), objectId, subObjectId);
						addTextMetadata(documentMetadataMap, objMetadata.getDataTypeCode(), objMetadata.getName(),
								objMetacontent != null && objMetacontent.getContent() != null ? new String(objMetacontent.getContent()) : "",
								objMetadata.getObjMetaId());
					} else if (objMetadata.getDataTypeCode().equals("FILE")) {
						ObjMetacontent objMetacontent = DAOFactory.getObjMetacontentDAO().loadObjMetacontent(objMetadata.getObjMetaId(), objectId, subObjectId);
						addTextFileMetadata(documentMetadataMap, objMetadata.getDataTypeCode(), objMetadata.getName(),
								objMetacontent != null && objMetacontent.getContent() != null ? new String(objMetacontent.getContent()) : "",
								objMetadata.getObjMetaId(),
								objMetacontent != null && objMetacontent.getAdditionalInfo() != null ? objMetacontent.getAdditionalInfo() : ""); // additionalInfo
																																					// contains
																																					// file
																																					// name! and
																																					// file
																																					// saved
																																					// data
					}
				}
			}

			if (!documentMetadataMap.isEmpty()) {
				return Response.ok(new JSONObject(documentMetadataMap).toString()).build();
			}
		} catch (Exception e) {
			logger.error(this.getServletRequest().getPathInfo(), e);
		}

		return Response.ok().build();
	}

	@PUT
	@Path("/saveDocumentMetadata")
	public Response saveDocumentMetadata(@Context HttpServletRequest httpRequest) throws JSONException {
		try {
			JSONObject params = RestUtilities.readBodyAsJSONObject(httpRequest);
			IObjMetacontentDAO dao = DAOFactory.getObjMetacontentDAO();
			dao.setUserProfile(getUserProfile());
			Integer biobjectId = params.getInt("id");
			//Integer subobjectId = params.has("subobjectId") ? params.getInt("subobjectId") : null;
			Integer subobjectId=null;
			if(params.has("subobjectId")){
				if(params.get("subobjectId") instanceof  Integer){
					subobjectId=params.getInt("subobjectId"); 
				}	
			}
			
			String jsonMeta = params.getString("jsonMeta");
			byte[] bytes = null;

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date date = new Date();
			String saveFileDateString = dateFormat.format(date);

			logger.debug("Object id = " + biobjectId);
			logger.debug("Subobject id = " + subobjectId);

			JSONArray metadata = new JSONArray(jsonMeta);
			for (int i = 0; i < metadata.length(); i++) {
				JSONObject aMetadata = metadata.getJSONObject(i);
				Integer metadataId = aMetadata.getInt("id");
				String text = aMetadata.getString("value");
				boolean isFileMetadata = !aMetadata.isNull("fileToSave");
				boolean newFileUploaded = false;

				String fileName = null;
				if (isFileMetadata) {
					JSONObject fileMetadataObject = aMetadata.getJSONObject("fileToSave");

					newFileUploaded = !fileMetadataObject.isNull("fileName");
					if (newFileUploaded) // file to save is uploaded
					{
						fileName = fileMetadataObject.getString("fileName");
					}

				}

				ObjMetacontent aObjMetacontent = dao.loadObjMetacontent(metadataId, biobjectId, subobjectId);
				String filePath = SpagoBIUtilities.getResourcePath() + "/" + METADATA_DIR + "/" + getUserProfile().getUserName().toString();

				if (aObjMetacontent == null) {
					logger.debug("ObjMetacontent for metadata id = " + metadataId + ", biobject id = " + biobjectId + ", subobject id = " + subobjectId
							+ " was not found, creating a new one...");
					aObjMetacontent = new ObjMetacontent();
					aObjMetacontent.setObjmetaId(metadataId);
					aObjMetacontent.setBiobjId(biobjectId);
					aObjMetacontent.setSubobjId(subobjectId);
					aObjMetacontent.setCreationDate(new Date());
					aObjMetacontent.setLastChangeDate(new Date());

					if (isFileMetadata) {
						if (newFileUploaded) {
							bytes = getFileByteArray(filePath, fileName);
							aObjMetacontent.setContent(bytes);
							JSONObject uploadedFileWithDate = new JSONObject();
							uploadedFileWithDate.put("fileName", fileName);
							uploadedFileWithDate.put("saveDate", saveFileDateString);
							aObjMetacontent.setAdditionalInfo(uploadedFileWithDate.toString());
						} else { // metadata file, but file not uploaded yet
							aObjMetacontent.setContent("".getBytes("UTF-8"));
							JSONObject uploadedFileInfo = new JSONObject();
							if (aObjMetacontent.getAdditionalInfo() != null) {
								uploadedFileInfo = new JSONObject(aObjMetacontent.getAdditionalInfo());
							}
							aObjMetacontent.setAdditionalInfo(uploadedFileInfo.toString());
						}
					} else {
						aObjMetacontent.setContent(text.getBytes("UTF-8"));
					}
					dao.insertObjMetacontent(aObjMetacontent);
				} else { // modify existing metadata
					logger.debug("ObjMetacontent for metadata id = " + metadataId + ", biobject id = " + biobjectId + ", subobject id = " + subobjectId
							+ " was found, it will be modified...");
					if (isFileMetadata) {
						if (newFileUploaded) {
							bytes = getFileByteArray(filePath, fileName);
							aObjMetacontent.setContent(bytes);
							JSONObject uploadedFileWithDate = new JSONObject();
							uploadedFileWithDate.put("fileName", fileName);
							uploadedFileWithDate.put("saveDate", saveFileDateString);
							aObjMetacontent.setAdditionalInfo(uploadedFileWithDate.toString());
						} else { // metadata file, but file not uploaded yet
							aObjMetacontent.setContent("".getBytes("UTF-8"));
							JSONObject uploadedFileInfo = new JSONObject();
							if (aObjMetacontent.getAdditionalInfo() != null) {
								uploadedFileInfo = new JSONObject(aObjMetacontent.getAdditionalInfo());
							}
							aObjMetacontent.setAdditionalInfo(uploadedFileInfo.toString());
						}
					} else {
						aObjMetacontent.setContent(text.getBytes("UTF-8"));
					}
					aObjMetacontent.setLastChangeDate(new Date());
					dao.modifyObjMetacontent(aObjMetacontent);
				}

			}
			/*
			 * indexes biobject by modifying document in index
			 */
			BIObject biObjToIndex = DAOFactory.getBIObjectDAO().loadBIObjectById(biobjectId);
			LuceneIndexer.updateBiobjInIndex(biObjToIndex, false);

		} catch (Exception e) {
			logger.error(request.getPathInfo(), e);
			return Response.ok(new JSONObject("{\"errors\":[{\"message\":\"Exception occurred while saving metadata\"}]}").toString()).build();
		}
		return Response.ok().build();
	}

	/**
	 * Produces a json with a bynary content of a metadata file and its name
	 *
	 * @param id
	 *            of document
	 * @param id
	 *            of subObject
	 * @param id
	 *            of a metaData
	 * @param httpRequest
	 * @return a response with a json
	 * @throws EMFUserError
	 */
	@GET
	@Path("/{id}/{metadataObjectId}/documentfilemetadata")
	public Response documentFileMetadata(@PathParam("id") Integer objectId, @PathParam("metadataObjectId") Integer metaObjId,
			@Context HttpServletRequest httpRequest) throws EMFUserError {
		try {
			Integer subObjectId = null;
			IObjMetacontentDAO metacontentDAO = DAOFactory.getObjMetacontentDAO();

			ObjMetacontent metacontent = metacontentDAO.loadObjMetacontent(metaObjId, objectId, subObjectId);
			JSONObject additionalInfoJSON = new JSONObject(metacontent.getAdditionalInfo());
			String fileName = additionalInfoJSON.getString("fileName");

			int binaryContentId = metacontent.getBinaryContentId();
			IBinContentDAO binaryContentDAO = DAOFactory.getBinContentDAO();
			byte[] fileContent = binaryContentDAO.getBinContent(binaryContentId);

			ResponseBuilder response = Response.ok(fileContent);
			response.header("Content-Disposition", "attachment; filename=" + fileName);

			return response.build();

		} catch (Exception e) {
			logger.error(httpRequest.getPathInfo(), e);
			throw new SpagoBIRuntimeException("Error returning file.", e);
		}

	}

	@GET
	@Path("/{id}/{metadataObjectId}/deletefilemetadata")
	// (delete a metacontent)
	public Response cleanFileMetadata(@PathParam("id") Integer objectId, @PathParam("metadataObjectId") Integer metaObjId,
			@Context HttpServletRequest httpRequest) throws EMFUserError {
		try {
			Integer subObjectId = null;
			IObjMetacontentDAO metacontentDAO = DAOFactory.getObjMetacontentDAO();

			ObjMetacontent metacontent = metacontentDAO.loadObjMetacontent(metaObjId, objectId, subObjectId);
			if(metacontent!=null)
			{	
				JSONObject additionalInfoJSON = new JSONObject(metacontent.getAdditionalInfo());
				String fileName = additionalInfoJSON.getString("fileName");
	
				String filePath = SpagoBIUtilities.getResourcePath() + "/" + METADATA_DIR + "/" + getUserProfile().getUserName().toString() + "/" + fileName;
				metacontentDAO.eraseObjMetadata(metacontent);
	
				File metadataTempFile = new File(filePath);
				if (metadataTempFile.exists()) {
					metadataTempFile.delete();
				}
			}
			ResponseBuilder response = Response.ok();
			return response.build();

		} catch (Exception e) {
			logger.error(httpRequest.getPathInfo(), e);
			throw new SpagoBIRuntimeException("Error returning file.", e);
		}

	}

	/*
	 * File Upload to local temp directory
	 */
	@PUT
	@Path("/upload")
	@UserConstraint(functionalities = { SpagoBIConstants.DOCUMENT_METADATA_MANAGEMENT })
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON })
	public Response uploadFile(@MultipartForm MultipartFormDataInput input) {

		byte[] bytes = null;

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		for (String key : uploadForm.keySet()) {

			List<InputPart> inputParts = uploadForm.get(key);

			for (InputPart inputPart : inputParts) {

				try {

					MultivaluedMap<String, String> header = inputPart.getHeaders();
					if (getFileName(header) != null) {

						// convert the uploaded file to input stream
						InputStream inputStream = inputPart.getBody(InputStream.class, null);

						bytes = IOUtils.toByteArray(inputStream);

						String saveDirectoryPath = SpagoBIUtilities.getResourcePath() + "/" + METADATA_DIR + "/" + getUserProfile().getUserName().toString();
						File saveDirectory = new File(saveDirectoryPath);
						if (!(saveDirectory.exists() && saveDirectory.isDirectory())) {
							saveDirectory.mkdirs();
						}
						
						

						//If file received with full path (e.g. filenames from Internet Explorer)
						String fName=getFileName(header);
						fName=fName.substring(fName.lastIndexOf("\\")+1);						
						//
						
						String tempFile = saveDirectoryPath + "/" + fName;
						File tempFileToSave = new File(tempFile);
						tempFileToSave.createNewFile();
						DataOutputStream os = new DataOutputStream(new FileOutputStream(tempFileToSave));
						os.write(bytes);
						os.close();

					}

				} catch (IOException e) {
					throw new SpagoBIRuntimeException("Error inserting new file metadataContent ", e);
				}

			}

		}

		return Response.status(200).build();

	}

	private String getFileName(MultivaluedMap<String, String> header) {
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return null;
	}

	@SuppressWarnings("resource")
	private byte[] getFileByteArray(String filePath, String fileName) throws IOException {

		filePath = filePath + "/" + fileName;
		File file = new File(filePath);
		FileInputStream fis = null;
		byte[] bFile = null;
		try {
			fis = new FileInputStream(file);
			bFile = new byte[(int) file.length()];

			// convert file into array of bytes
			fis = new FileInputStream(file);
			fis.read(bFile);
			fis.close();
		} catch (IOException e) {
			throw new IOException("Error reading " + filePath + " file.", e);
		}
		return bFile;

	}

	private void addMetadata(JSONArray generalMetadata, String name, String value) throws JsonMappingException, JsonParseException, JSONException, IOException {
		addMetadata(generalMetadata, name, value, null, null);
	}

	private void addMetadata(JSONArray generalMetadata, String name, String value, Integer id, String type) throws JsonMappingException, JsonParseException,
			JSONException, IOException {
		JSONObject data = new JSONObject();
		if (id != null) {
			data.put("id", id);
		}
		data.put("name", name);
		data.put("value", value);
		generalMetadata.put(data);
	}

	private void addTextMetadata(Map<String, JSONArray> metadataMap, String type, String name, String value, Integer id) throws JSONException,
			JsonMappingException, JsonParseException, IOException {
		JSONArray jsonArray = metadataMap.get(type);
		if (jsonArray == null) {
			jsonArray = new JSONArray();
		}
		// if (type.equals("FILE")) { // Avoid to put file content in response message
		// value = "File content is present on server but not sent in this message";
		// }
		addMetadata(jsonArray, name, value, id, type);
		metadataMap.put(type, jsonArray);
	}

	private void addTextFileMetadata(Map<String, JSONArray> metadataMap, String type, String name, String value, Integer id, String metadataFileName)
			throws JSONException, JsonMappingException, JsonParseException, IOException {

		JSONArray jsonArray = metadataMap.get(type);
		if (jsonArray == null) {
			jsonArray = new JSONArray();
		}
		if (type.equals("FILE")) { // Avoid to put file contet in response message
			value = "";
		}

		JSONObject data = new JSONObject();
		if (id != null) {
			data.put("id", id);
		}
		data.put("name", name);
		data.put("value", value);
		data.put("savedFile", metadataFileName); // savedFile instead of uploadedFile!!
		jsonArray.put(data);

		metadataMap.put(type, jsonArray);
	}

	protected String getExecutionRole(String role) throws EMFInternalError, DocumentExecutionException {
		UserProfile userProfile = getUserProfile();
		if (role != null && !role.equals("")) {
			logger.debug("role for document execution: " + role);
		} else {
			if (userProfile.getRoles().size() == 1) {
				role = userProfile.getRoles().iterator().next().toString();
				logger.debug("profile role for document execution: " + role);
			} else {
				logger.debug("missing role for document execution, role:" + role);
				throw new DocumentExecutionException(message.getMessage("SBIDev.docConf.execBIObject.selRoles.Title"));
			}
		}

		return role;
	}



	
	private String convertDate(String dateFrom, String dateTo, String dateStr) {
		String date = dateStr;
		SimpleDateFormat dateFromFormat = new SimpleDateFormat(dateFrom);
		try {
			Date d = dateFromFormat.parse(dateStr);
			Format formatter = new SimpleDateFormat(dateTo);
			date = formatter.format(d);
			// jsonParameters.put(objParameter.getId(), date);
		} catch (ParseException e) {
			logger.error("Error prase date server ", e);

		}
		return date;
	}

	private static void addToZipFile(String filePath, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		File file = new File(filePath + "/" + fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	private static void deleteDirectoryContent(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryContent(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
	}

}
