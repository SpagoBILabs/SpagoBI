package it.eng.spagobi.behaviouralmodel.lov.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.dbaccess.sql.DataRow;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.lov.bo.DatasetDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.FixedListDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.IJavaClassLov;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.JavaClassDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.ScriptDetail;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.DelegatedBasicListService;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.bo.ConfigurableDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDatasetFactory;
import it.eng.spagobi.tools.dataset.bo.MongoDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/LOV/Test")
public class ListTestLovService {

	private static Logger logger = Logger.getLogger(ListTestLovAction.class);

	@GET
	public String get(@Context HttpServletRequest servletRequest, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit,
			@QueryParam("page") Integer page, @QueryParam("lovProviderXML") String lovProviderXML, @QueryParam("valueFilter") String valueFilter,
			@QueryParam("columnsFilterValue") String columnsFilter, @QueryParam("typeValueFilter") String typeValueFilter,
			@QueryParam("typeFilter") String typeFilter, @QueryParam("addProfileAttributes") String addProfileAttributes) {

		logger.debug("IN");

		JSONObject response = new JSONObject();

		try {

			Exception responseFailure = null;
			GridMetadataContainer lovExecutionResult = new GridMetadataContainer();

			// Get from the request (LOV provider XML) the type of LOV (QUERY, SCRIPT, DATASET, ...)
			String typeLov = LovDetailFactory.getLovTypeCode(lovProviderXML);

			// Get the user profile
			UserProfile profile = (UserProfile) servletRequest.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);

			// Based on the LOV type fill the spago list and paginator object
			IDataStore dataStore = null;

			/*
			 * Is this service (for testing the LOV) called for the first time (no information about potentially missing profile attributes)? If we do not have
			 * 'addProfileAttributes' input query parameter this method is called for the first time - first request for testing.
			 */
			boolean methodsFirstCall = addProfileAttributes == null || addProfileAttributes.equals("");

			/*
			 * The 'lovDetails' object contain data about the LOV (LOV provider) in form of the object (unlike 'lovProviderXML' object that is in form of the
			 * String). This interface is common (i.e. implemented by) for QueryDetail, JavaClassDetail, FixedListDetail, ScriptDetail, DatasetDetail.
			 */
			ILovDetail lovDetails = LovDetailFactory.getLovFromXML(lovProviderXML);

			/* We will get data (names) about the missing profile attributes */
			List profAttrToFill = getProfileAttributesToFill(lovDetails, profile);

			/*
			 * We will define this object when we get the completed UserProfile object through 'clone' object. The purpose of this object is to use it as
			 * profile object that can be used in queries (e.g. in Java class method getValues()).
			 */
			/*
			 * If in this service call we have necessary data about (potentially) missing profile attribute(s) - 'addProfileAttributes' query parameter.
			 */
			/*
			 * Is this method called for the first time, before testing (result page) - without potentially missing attribute(s)?
			 */
			UserProfile cloneToProfile = (UserProfile) profile.clone();

			if (methodsFirstCall == false) {
				cloneToProfile = fillCloneWithData(lovProviderXML, addProfileAttributes, profAttrToFill, profile);
			}

			/*
			 * If we have missing profile attributes AND we call this method for the first time.
			 */
			if (!profAttrToFill.isEmpty() && methodsFirstCall == true) {
				return createListOfMissingProfileAttributes(profAttrToFill);
			}

			if (typeLov.equalsIgnoreCase("QUERY")) {

				logger.debug("IN (LOV type is QUERY)");

				// Create QueryDetail object that is based on the LOV provider XML
				QueryDetail queryDetail = QueryDetail.fromXML(lovProviderXML);

				String datasource = queryDetail.getDataSource();
				String statement = queryDetail.getQueryDefinition();

				// Execute query ('executeSelect' method)
				try {

					// statement = StringUtilities.substituteProfileAttributesInString(statement, profile);
					statement = StringUtilities.substituteProfileAttributesInString(statement, cloneToProfile);
					dataStore = executeSelect(datasource, statement, page, start, limit, columnsFilter, valueFilter);

					/*
					 * If we want to filter LOV results, "columnsFilter" will not be empty or null.
					 */
					/* TODO: Implement for other columns too. */
					if (columnsFilter != null && columnsFilter != "") {

						int numberOfColumns = dataStore.getMetaData().getFieldCount();
						List<String> columns = new ArrayList<String>();

						for (int i = 0; i < numberOfColumns; i++) {
							columns.add(dataStore.getMetaData().getFieldName(i));
						}

						// TODO: CAN I DO SOMETHING LIKE THIS ????
						SourceBean sourceBeanFiltered = DelegatedBasicListService.filterList(dataStore.toSourceBean(), valueFilter, typeValueFilter,
								columnsFilter, typeFilter, new EMFErrorHandler());

						lovExecutionResult.setValues(toList(sourceBeanFiltered, start, limit));
						lovExecutionResult.setFields(GridMetadataContainer.buildHeaderMapForGrid(columns));

						List rows = sourceBeanFiltered.getAttributeAsList(DataRow.ROW_TAG);
						lovExecutionResult.setResults(rows.size());

						response = lovExecutionResult.toJSON();

					} else {
						JSONDataWriter dataSetWriter = new JSONDataWriter();
						response = (JSONObject) dataSetWriter.write(dataStore);
					}

				} catch (Exception e) {

					logger.error("Exception occurred executing query lov: ", e);
					String stacktrace = e.toString();
					response.put("stacktrace", stacktrace);
					int startIndex = stacktrace.indexOf("java.sql.");
					int endIndex = stacktrace.indexOf("\n\tat ", startIndex);
					if (endIndex == -1)
						endIndex = stacktrace.indexOf(" at ", startIndex);
					if (startIndex != -1 && endIndex != -1)
						response.put("errorMessage", stacktrace.substring(startIndex, endIndex));
					responseFailure = e;
					response.put("testExecuted", "false");

				}

				logger.debug("OUT (LOV type is QUERY)");
			}

			// TODO: SCRIPT filtering is missing !!!!
			else if (typeLov.equalsIgnoreCase("SCRIPT")) {

				logger.debug("IN (LOV type is SCRIPT)");

				ScriptDetail scriptDetail = ScriptDetail.fromXML(lovProviderXML);

				try {
					DataStore lovResult = scriptDetail.getLovResultAsDataStore(cloneToProfile, null, null, null);

					JSONDataWriter dataSetWriter = new JSONDataWriter();
					response = (JSONObject) dataSetWriter.write(lovResult);

				} catch (Exception e) {

					logger.error("Exception occurred executing query lov: ", e);
					String stacktrace = e.toString();
					response.put("stacktrace", stacktrace);
					int startIndex = stacktrace.indexOf("java.sql.");
					int endIndex = stacktrace.indexOf("\n\tat ", startIndex);
					if (endIndex == -1)
						endIndex = stacktrace.indexOf(" at ", startIndex);
					if (startIndex != -1 && endIndex != -1)
						response.put("errorMessage", stacktrace.substring(startIndex, endIndex));
					responseFailure = e;
					response.put("testExecuted", "false");

				}

				logger.debug("OUT (LOV type is SCRIPT)");
			}

			else if (typeLov.equalsIgnoreCase("FIXED_LIST")) {

				logger.debug("IN (LOV type is FIX LOV)");

				FixedListDetail fd = FixedListDetail.fromXML(lovProviderXML);
				DataStore result = fd.getLovResultAsDataStore(cloneToProfile, null, null, null);

				JSONDataWriter dataSetWriter = new JSONDataWriter();
				response = (JSONObject) dataSetWriter.write(result);

				logger.debug("OUT (LOV type is FIX LOV)");
			}

			else if (typeLov.equalsIgnoreCase("JAVA_CLASS")) {

				logger.debug("IN (LOV type is JAVA CLASS)");

				/*
				 * We need 'javaClassDetail' for getting the information about the Java class name of the provided LOV.
				 */
				JavaClassDetail javaClassDetail = JavaClassDetail.fromXML(lovProviderXML);

				try {

					/*
					 * If we have complete data about profile attributes that user needs we can proceed with actual testing part - test the provided LOV and
					 * show the results.
					 */

					/*
					 * Take name of the Java class (with its path in the project) that current LOV takes as data source.
					 */
					String javaClassName = javaClassDetail.getJavaClassName();
					IJavaClassLov javaClassLov = (IJavaClassLov) Class.forName(javaClassName).newInstance();
					String result = javaClassLov.getValues(cloneToProfile);

					DataStore ds = javaClassDetail.getLovResultAsDataStore(result);

					JSONDataWriter dataSetWriter = new JSONDataWriter();
					response = (JSONObject) dataSetWriter.write(ds);

				} catch (Exception e) {
					throw new SpagoBIRuntimeException("Error while creating data store for LOV of Java class type.", e);
				}

				logger.debug("OUT (LOV type is JAVA CLASS)");
			}

			else if (typeLov.equalsIgnoreCase("DATASET")) {

				logger.debug("IN (LOV type is DATASET)");

				DatasetDetail datasetDetail = DatasetDetail.fromXML(lovProviderXML);
				DataStore ds = datasetDetail.getLovResultAsDataStore(cloneToProfile, null, null, null);

				JSONDataWriter dataSetWriter = new JSONDataWriter();
				response = (JSONObject) dataSetWriter.write(ds);

				logger.debug("OUT (LOV type is DATASET)");
			}

		} catch (Exception e) {
			logger.error("Error testing lov", e);
			throw new SpagoBIServiceException("Error testing lov", e);
		}

		logger.debug("OUT");

		return response.toString();
	}

	private String createListOfMissingProfileAttributes(List profAttrToFill) {

		logger.debug("IN");

		int numberOfAttrToFill = profAttrToFill.size();

		String attrToFillString = "\"";

		/*
		 * Form the String object with the data about missing profile attributes that user should define on the client-side.
		 */
		for (int i = 0; i < numberOfAttrToFill; i++) {
			if (i == 0) {
				attrToFillString = "\"" + profAttrToFill.get(i).toString() + "\"";
			} else {
				attrToFillString = attrToFillString + ", " + "\"" + profAttrToFill.get(i).toString() + "\"";
			}
		}

		/*
		 * We will return to the client (user) to provide him data about profile attributes that are necessary for executing the LOV. This JSON-formatted String
		 * will contain data about missing attributes, 'attrToFillString'. String is dynamically populated.
		 */

		logger.debug("OUT");

		/*
		 * The idea: if (execution was successful) { return datastore as it is -- writeBackToClient(new JSONSuccess(dataStoreJSON)); } else { send to the client
		 * an error message with some information }
		 */
		return "{result: 'ko', error: {code : 125, description : 'missing profile attributes', missingProfileAttributes : [" + attrToFillString + "]}}";
	}

	private UserProfile fillCloneWithData(String lovProviderXML, String addProfileAttributes, List profAttrToFill, UserProfile profile) {

		logger.debug("IN");

		/*
		 * Format input String query parameter (that is in the form of the JSON) as JSON object. This way we can take the data that LOV Java class needs (for
		 * profile attributes) and that are missing from the current user profile.
		 */
		JSONObject jsonObj = null;

		try {
			jsonObj = new JSONObject(addProfileAttributes);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/* Cloning the current profile for testing purposes. */
		UserProfile clone = (UserProfile) profile.clone();

		// copy attributes and add the missing ones
		Map attributes = new HashMap();
		Collection origAttrNames = profile.getUserAttributeNames();
		Iterator origAttrNamesIter = origAttrNames.iterator();

		while (origAttrNamesIter.hasNext()) {

			String profileAttrName = (String) origAttrNamesIter.next();
			String profileAttrValue;

			try {
				profileAttrValue = profile.getUserAttribute(profileAttrName).toString();
				attributes.put(profileAttrName, profileAttrValue);
			} catch (EMFInternalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/* Set attributes that original profile already has. */
		clone.setAttributes(attributes);

		int numberOfAttrToFill = profAttrToFill.size();

		/* Add attributes that original profile is missing. */
		for (int i = 0; i < numberOfAttrToFill; i++) {
			clone.addAttributes(profAttrToFill.get(i).toString(), jsonObj.opt(profAttrToFill.get(i).toString()));
		}

		logger.debug("OUT");

		return clone;
	}

	private List getProfileAttributesToFill(ILovDetail lovDet, UserProfile profile) {

		logger.debug("IN");

		List attrsToFill = new ArrayList();

		try {

			Collection userAttrNames = profile.getUserAttributeNames();
			List attrsRequired = lovDet.getProfileAttributeNames();
			Iterator attrsReqIter = attrsRequired.iterator();

			while (attrsReqIter.hasNext()) {

				String attrName = (String) attrsReqIter.next();

				if (!userAttrNames.contains(attrName)) {
					attrsToFill.add(attrName);
				}

			}

		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while checking the profile attributes required for test", e);
		}

		logger.debug("OUT");

		return attrsToFill;
	}

	private List<Map<String, String>> toList(SourceBean rowsSourceBean, Integer start, Integer limit) throws JSONException {

		logger.debug("IN");

		Map<String, String> map;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int startIter = 0;
		int endIter;

		if (start != null) {
			startIter = start;
		}

		if (rowsSourceBean != null) {
			List<SourceBean> rows = rowsSourceBean.getAttributeAsList(DataRow.ROW_TAG);
			if (rows != null && rows.size() > 0) {
				if (limit != null && limit > 0) {
					endIter = startIter + limit;
					if (endIter > rows.size()) {
						endIter = rows.size();
					}
				} else {
					endIter = rows.size();
				}

				for (int i = startIter; i < endIter; i++) {
					JSONObject rowJson = new JSONObject();
					List<SourceBeanAttribute> rowAttrs = (rows.get(i)).getContainedAttributes();
					Iterator<SourceBeanAttribute> rowAttrsIter = rowAttrs.iterator();
					map = new HashMap<String, String>();
					while (rowAttrsIter.hasNext()) {
						SourceBeanAttribute rowAttr = rowAttrsIter.next();
						map.put(rowAttr.getKey(), (rowAttr.getValue()).toString());
					}
					list.add(map);
				}
			}
		}

		logger.debug("OUT");

		return list;
	}

	/**
	 * Executes a select statement.
	 *
	 * @param statement
	 *            The statement definition string
	 *
	 * @param datasource
	 *            the datasource
	 *
	 * @param columnsNames
	 *            the columns names
	 *
	 * @return
	 *
	 * @throws
	 *
	 */
	public static IDataStore executeSelect(String dataSourceLabel, String statement, Integer page, Integer start, Integer limit, String columnsFilter,
			String valueFilter) throws EMFInternalError {

		logger.debug("IN");

		try {

			IDataSource datasource = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dataSourceLabel);
			IDataSet dataSet = null;

			if (datasource.getHibDialectClass().toLowerCase().contains("mongo")) {
				dataSet = new MongoDataSet();
			} else {
				dataSet = JDBCDatasetFactory.getJDBCDataSet(datasource);
			}

			((ConfigurableDataSet) dataSet).setDataSource(datasource);
			((ConfigurableDataSet) dataSet).setQuery(statement);

			if (columnsFilter != null && valueFilter != null) {
				dataSet.test();
			} else {
				dataSet.test(start, limit, start + limit);
			}

			logger.debug("OUT (success)");

			return dataSet.getDataStore();

		} catch (Exception e) {

			logger.debug("OUT (error)");

			return null;
		}

	}

}