package it.eng.spagobi.behaviouralmodel.lov.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.dbaccess.sql.DataRow;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.DelegatedBasicListService;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.bo.ConfigurableDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDatasetFactory;
import it.eng.spagobi.tools.dataset.bo.MongoDataSet;
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

		System.out.println("###############2");
		System.out.println(valueFilter);
		System.out.println(columnsFilter);
		System.out.println(typeValueFilter);
		System.out.println(typeFilter);

		System.out.println("###############3");
		System.out.println(name);

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

				// Create QueryDetail object that is based on the LOV provider XML
				QueryDetail qd = QueryDetail.fromXML(name);

				String datasource = qd.getDataSource();
				String statement = qd.getQueryDefinition();

				// Execute query ('executeSelect' method)
				try {

					statement = StringUtilities.substituteProfileAttributesInString(statement, profile);

					// I have deleted the List<String> colNames object from list of input parameters for this method
					System.out.println("11111111111111");
					System.out.println(page);
					System.out.println(start);
					System.out.println(limit);
					System.out.println(columnsFilter);
					System.out.println(valueFilter);

					dataStore = executeSelect(datasource, statement, page, start, limit, columnsFilter, valueFilter);

					if (columnsFilter != null && columnsFilter != "") {

						int numberOfColumns = dataStore.getMetaData().getFieldCount();
						List<String> columns = new ArrayList<String>();

						System.out.println("#############");

						for (int i = 0; i < numberOfColumns; i++) {
							columns.add(dataStore.getMetaData().getFieldName(i));
						}

						System.out.println(columns);

						// CAN I DO SOMETHING LIKE THIS ????
						SourceBean sourceBeanFiltered = DelegatedBasicListService.filterList(dataStore.toSourceBean(), valueFilter, typeValueFilter,
								columnsFilter, typeFilter, new EMFErrorHandler());

						lovExecutionResult.setValues(toList(sourceBeanFiltered, start, limit));
						lovExecutionResult.setFields(GridMetadataContainer.buildHeaderMapForGrid(columns));

						System.out.println("444444444444");
						System.out.println(lovExecutionResult);

						List rows = sourceBeanFiltered.getAttributeAsList(DataRow.ROW_TAG);
						lovExecutionResult.setResults(rows.size());

						System.out.println(rows);
						// lovExecutionResult.setResults(rows.size());

						//
						// // FilterQueryTransformer aaa = new FilterQueryTransformer();
						// // Object vvv = aaa.execTransformation(statement);
						//
						// System.out.println();

						// JSONDataWriter dataSetWriter = new JSONDataWriter();

						// response = (JSONObject) dataSetWriter.write(dataStore);

						response = lovExecutionResult.toJSON();
					} else {
						System.out.println("444444444444");
						System.out.println(dataStore);
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
			}

			/*
			 * if (executoin was successful) { return datasotre as it is -- writeBackToClient(new JSONSuccess(dataStoreJSON)); } else { send to the client an
			 * error message with some information }
			 */

		} catch (Exception e) {
			logger.error("Error testing lov", e);
			throw new SpagoBIServiceException("Error testing lov", e);
		}

		return response.toString();
	}

	// public static Object executeSelect(String datasource, String statement,
	// List columnsNames) throws EMFInternalError {
	// // ResponseContainer responseContainer, String pool, String statement, List columnsNames) throws EMFInternalError {
	// Object result = null;
	// // DataConnectionManager dataConnectionManager = null;
	// DataConnection dataConnection = null;
	// SQLCommand sqlCommand = null;
	// DataResult dataResult = null;
	// try {
	// /*
	// * dataConnectionManager = DataConnectionManager.getInstance(); dataConnection = dataConnectionManager.getConnection(pool);
	// */
	// // gets connection
	// DataSourceUtilities dsUtil = new DataSourceUtilities();
	// Connection conn = dsUtil.getConnection(requestContainer, datasource);
	// dataConnection = dsUtil.getDataConnection(conn);
	//
	// sqlCommand = dataConnection.createSelectCommand(statement, false);
	// dataResult = sqlCommand.execute();
	// ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
	// List temp = Arrays.asList(scrollableDataResult.getColumnNames());
	// columnsNames.addAll(temp);
	// result = scrollableDataResult.getSourceBean();
	// } finally {
	// Utils.releaseResources(dataConnection, sqlCommand, dataResult);
	// }
	// return result;
	// }

	private List<Map<String, String>> toList(SourceBean rowsSourceBean, Integer start, Integer limit) throws JSONException {
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

		try {

			IDataSource datasource = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dataSourceLabel);
			IDataSet dataSet = null;

			System.out.println("0000000000000000 - 1");

			if (datasource.getHibDialectClass().toLowerCase().contains("mongo")) {
				dataSet = new MongoDataSet();
			} else {
				dataSet = JDBCDatasetFactory.getJDBCDataSet(datasource);
			}
			System.out.println("0000000000000000 - 2");
			System.out.println(datasource);
			System.out.println(statement);

			((ConfigurableDataSet) dataSet).setDataSource(datasource);

			// if ((columnsFilter != "" && columnsFilter != null && valueFilter != "" && valueFilter != null)) {
			// System.out.println("PROBLEEEEEEEM 1");
			// System.out.println(columnsFilter);
			// System.out.println(valueFilter);
			// FilterQueryTransformer aaa = new FilterQueryTransformer();
			// System.out.println("PROBLEEEEEEEM 2");
			// aaa.addFilter(columnsFilter, valueFilter);
			// System.out.println("PROBLEEEEEEEM 3");
			// Object vvv = aaa.execTransformation(datasource);
			//
			// ((ConfigurableDataSet) dataSet).setQuery(vvv);
			//
			// System.out.println("PROBLEEEEEEEM 4");
			// System.out.println(vvv);
			// } else {
			System.out.println("0000000000000000 - 3");
			((ConfigurableDataSet) dataSet).setQuery(statement);
			// }

			if (columnsFilter != null && valueFilter != null) {
				dataSet.test();
			} else {
				dataSet.test(start, limit, start + limit);
			}

			// dataSet.test(start, limit, start + limit);

			// dataSet.getDataStore().

			return dataSet.getDataStore();

		} catch (Exception e) {
			return null;
		}

	}

}