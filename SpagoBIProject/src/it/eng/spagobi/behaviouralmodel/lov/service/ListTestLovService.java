package it.eng.spagobi.behaviouralmodel.lov.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.dbaccess.sql.DataRow;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.lov.bo.FixedListDetail;
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
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.ArrayList;
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
			@QueryParam("page") Integer page, @QueryParam("name") String name, @QueryParam("valueFilter") String valueFilter,
			@QueryParam("columnsFilterValue") String columnsFilter, @QueryParam("typeValueFilter") String typeValueFilter,
			@QueryParam("typeFilter") String typeFilter) {

		logger.debug("IN");

		JSONObject response = new JSONObject();

		try {

			Exception responseFailure = null;
			GridMetadataContainer lovExecutionResult = new GridMetadataContainer();

			// Get from the request (LOV provider XML) the type of LOV (QUERY, SCRIPT, DATASET, ...)
			String typeLov = LovDetailFactory.getLovTypeCode(name);

			// Get the user profile
			UserProfile profile = (UserProfile) servletRequest.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);

			// Based on the LOV type fill the spago list and paginator object
			IDataStore dataStore = null;

			// If the LOV type is QUERY ...
			if (typeLov.equalsIgnoreCase("QUERY")) {

				logger.debug("IN (LOV type is QUERY)");

				// Create QueryDetail object that is based on the LOV provider XML
				QueryDetail qd = QueryDetail.fromXML(name);

				String datasource = qd.getDataSource();
				String statement = qd.getQueryDefinition();

				// Execute query ('executeSelect' method)
				try {

					statement = StringUtilities.substituteProfileAttributesInString(statement, profile);

					dataStore = executeSelect(datasource, statement, page, start, limit, columnsFilter, valueFilter);

					/*
					 * If we want to filter LOV results, "columnsFilter" will not be empty or null.
					 */
					if (columnsFilter != null && columnsFilter != "") {

						int numberOfColumns = dataStore.getMetaData().getFieldCount();
						List<String> columns = new ArrayList<String>();

						for (int i = 0; i < numberOfColumns; i++) {
							columns.add(dataStore.getMetaData().getFieldName(i));
						}

						// CAN I DO SOMETHING LIKE THIS ????
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

			// !!!! SCRIPT filtering is missing !!!!
			else if (typeLov.equalsIgnoreCase("SCRIPT")) {

				logger.debug("IN (LOV type is SCRIPT)");

				ScriptDetail sd = ScriptDetail.fromXML(name);
				// I think this is unnecessary...
				// String script = sd.getScript();

				try {
					// I think this is unnecessary...
					// script = StringUtilities.substituteProfileAttributesInString(script, profile);

					DataStore lovResult = sd.getLovResultAsDataStore(profile, null, null, null);

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

				FixedListDetail fd = FixedListDetail.fromXML(name);
				DataStore result = fd.getLovResultAsDataStore(profile, null, null, null);

				JSONDataWriter dataSetWriter = new JSONDataWriter();
				response = (JSONObject) dataSetWriter.write(result);

				logger.debug("OUT (LOV type is FIX LOV)");
			}

			/*
			 * if (execution was successful) { return datastore as it is -- writeBackToClient(new JSONSuccess(dataStoreJSON)); } else { send to the client an
			 * error message with some information }
			 */

		} catch (Exception e) {
			logger.error("Error testing lov", e);
			throw new SpagoBIServiceException("Error testing lov", e);
		}

		logger.debug("OUT");

		return response.toString();
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