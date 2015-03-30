/*
CKANClient-J - Data Catalogue Software client in Java
Copyright (C) 2013 Newcastle University
Copyright (C) 2012 Open Knowledge Foundation

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.eng.spagobi.tools.dataset.ckan;

import it.eng.spagobi.tools.dataset.ckan.exception.CKANException;
import it.eng.spagobi.tools.dataset.ckan.resource.impl.Dataset;
import it.eng.spagobi.tools.dataset.ckan.resource.impl.Organization;
import it.eng.spagobi.tools.dataset.ckan.resource.impl.OrganizationSummary;
import it.eng.spagobi.tools.dataset.ckan.resource.impl.Resource;
import it.eng.spagobi.tools.dataset.ckan.resource.impl.User;
import it.eng.spagobi.tools.dataset.ckan.result.CKANResult;
import it.eng.spagobi.tools.dataset.ckan.result.impl.DatasetResult;
import it.eng.spagobi.tools.dataset.ckan.result.impl.DatasetSearchResult;
import it.eng.spagobi.tools.dataset.ckan.result.impl.OrganizationResult;
import it.eng.spagobi.tools.dataset.ckan.result.impl.ResourceResult;
import it.eng.spagobi.tools.dataset.ckan.result.impl.UserResult;
import it.eng.spagobi.tools.dataset.ckan.result.list.impl.DatasetList;
import it.eng.spagobi.tools.dataset.ckan.result.list.impl.OrganizationSummaryList;
import it.eng.spagobi.tools.dataset.ckan.result.list.impl.StringList;
import it.eng.spagobi.tools.dataset.ckan.utils.CKANUtils;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The primary interface to this package the Client class is responsible for managing all interactions with a given connection.
 *
 * @author Alessandro Portosa (alessandro.portosa@eng.it)
 */
public final class CKANClient {

	private static transient Logger logger = Logger.getLogger(CKANClient.class);

	private Connection connection = null;
	private ObjectMapper mapper = null;
	private ApacheHttpClientExecutor httpExecutor = null;
	private final int DEFAULT_SEARCH_FACET_LIMIT = -1;
	/** Unlimited **/
	private final int DEFAULT_SEARCH_FACET_MIN_COUNT = 1;
	private final int DEFAULT_SEARCH_FIRST_ROW = 0;
	private final int DEFAULT_SEARCH_MAX_RETURNED_ROWS = 100;
	private final int DATASETS_LIMIT = 300;

	private CKANClient() {
	}

	/**
	 * Constructs a new Client for making requests to a remote CKAN instance.
	 *
	 * @param c
	 *            A Connection object containing info on the location of the CKAN Instance.
	 * @param apikey
	 *            A user's API Key sent with every request.
	 */
	public CKANClient(Connection c) {
		logger.debug("Initialising CKANClient");
		this.connection = c;
		mapper = new ObjectMapper();
		httpExecutor = new ApacheHttpClientExecutor(getHttpClient());
		logger.debug("CKANClient initialised");
	}

	/**
	 * Initialise a new REST Client for making requests to a remote REST services.
	 */

	public static HttpClient getHttpClient() {

		// Getting proxy properties set as JVM args
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		int proxyPortInt = CKANUtils.portAsInteger(proxyPort);
		String proxyUsername = System.getProperty("http.proxyUsername");
		String proxyPassword = System.getProperty("http.proxyPassword");

		logger.debug("Setting REST client");
		HttpClient httpClient = new HttpClient();
		httpClient.setConnectionTimeout(500);

		if (proxyHost != null && proxyPortInt > 0) {
			if (proxyUsername != null && proxyPassword != null) {
				logger.debug("Setting proxy with authentication");
				httpClient.getHostConfiguration().setProxy(proxyHost, proxyPortInt);
				HttpState state = new HttpState();
				state.setProxyCredentials(null, null, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
				httpClient.setState(state);
				logger.debug("Proxy with authentication set");
			} else {
				// Username and/or password not acceptable. Trying to set proxy without credentials
				logger.debug("Setting proxy without authentication");
				httpClient.getHostConfiguration().setProxy(proxyHost, proxyPortInt);
				logger.debug("Proxy without authentication set");
			}
		} else {
			logger.debug("No proxy configuration found");
		}
		logger.debug("REST client set");

		return httpClient;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Loads a JSON string into a class of the specified type.
	 *
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	protected <T> T getObjectFromJson(Class<T> cls, String data, String action) throws CKANException {
		Object o;
		logger.debug("Converting JSON to Object");
		try {
			o = mapper.readValue(data, cls);
			logger.debug("JSON converted");
		} catch (IOException e) {
			logger.debug("Error during JSON conversion");
			CKANException ckane = new CKANException("Error at: Client." + action + "()");
			ckane.addError(e.getLocalizedMessage());
			throw ckane;
		}
		handleResult((CKANResult) o, data, action);
		return (T) o;
	}

	/**
	 * Loads a JSON string into a class of the specified type.
	 *
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	protected String getJsonFromObject(Object o, String action) throws CKANException {
		String json;
		logger.debug("Converting Object to JSON");
		try {
			json = mapper.writeValueAsString(o);
			logger.debug("Object converted");
		} catch (IOException e) {
			CKANException ckanEx = new CKANException("Error at: Client." + action + "()");
			ckanEx.addError(e.getLocalizedMessage());
			throw ckanEx;
		}
		return json;
	}

	/**
	 * Handles error responses from CKAN
	 *
	 * When given a JSON string it will generate a valid CKANException containing all of the error messages from the JSON.
	 *
	 * @param json
	 *            The JSON response
	 * @param action
	 *            The name of the action calling this for the primary error message.
	 * @throws A
	 *             CKANException containing the error messages contained in the provided JSON.
	 */
	protected void handleResult(CKANResult result, String json, String action) throws CKANException {
		if (!result.success) {
			throw new CKANException("Error at: Client." + action + "()");
		}
	}

	/**
	 * Makes a POST request
	 *
	 * Submits a POST HTTP request to the CKAN instance configured within the constructor, returning the entire contents of the response.
	 *
	 * @param path
	 *            The URL path to make the POST request to
	 * @param jsonParams
	 *            The data to be posted to the URL
	 * @throws Exception
	 * @returns The String contents of the response
	 * @throws A
	 *             CKANException if the request fails
	 */
	protected String postAndReturnTheJSON(String path, String jsonParams) throws CKANException {
		String uri = connection.getHost() + path;
		ClientRequest request = null;
		ClientResponse<String> response = null;
		String jsonResponse = "";
		try {
			new URL(uri);
		} catch (MalformedURLException mue) {
			System.err.println(mue);
			throw new CKANException("Failed: the provided URI is malformed.");
		}

		try {
			request = new ClientRequest(uri, httpExecutor);
			// For FIWARE CKAN instance
			request.header("X-Auth-Token", connection.getApiKey());
			// For ANY CKAN instance
			// request.header("Authorization", connection.getApiKey());
			request.body("application/json", jsonParams);
			request.accept("application/json");
			response = request.post(String.class);
			jsonResponse = response.getEntity();
			LogMF.debug(logger, "Status code is [{0}], server response is \n{1}", new String[] { Integer.toString(response.getStatus()), jsonResponse });
			if (response.getStatus() != 200) {
				logger.error("Failed : HTTP error code : " + response.getStatus() + ". Server returned \n" + jsonResponse);
				throw new CKANException("Failed : HTTP error code : " + response.getStatus());
			}

			if (jsonResponse == null) {
				throw new CKANException("Failed: deserialisation has not been perfomed well. jsonResponse is null");
			}
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while requesting [" + uri + "]", e);
		} finally {
			if (response != null) {
				response.releaseConnection();
			}
		}
		return jsonResponse;
	}

	/**
	 * Makes a HEAD request
	 *
	 * Submits a HEAD HTTP request to the CKAN instance configured within the constructor, returning only the header contents of the response.
	 *
	 * @param path
	 *            The URL path to make the HEAD request to
	 * @throws Exception
	 * @returns The String contents of the header response
	 * @throws A
	 *             CKANException if the request fails
	 */
	private String[] headAndReturnHeaders(String uri) throws CKANException {
		ClientRequest request = null;
		ClientResponse response = null;
		String[] headers = new String[] { "", "" };
		try {
			URL url = new URL(uri);
		} catch (MalformedURLException mue) {
			System.err.println(mue);
			throw new CKANException("Failed: the provided URI is malformed.");
		}

		try {
			request = new ClientRequest(uri, httpExecutor);
			// For FIWARE CKAN instance
			request.header("X-Auth-Token", connection.getApiKey());
			// For ANY CKAN instance
			// request.header("Authorization", connection.getApiKey());
			response = request.head();
			if (response.getStatus() != 200) {
				throw new CKANException("Failed : HTTP error code : " + response.getStatus());
			}
			Object temp = response.getHeaders().getFirst("Content-Type");
			if (temp != null) {
				headers[0] = temp.toString();
			}
			temp = response.getHeaders().getFirst("Content-Length");
			if (temp != null) {
				headers[1] = temp.toString();
			}
		} catch (CKANException ckane) {
			logger.debug("Error " + ckane.getErrorMessages());
		} catch (Exception e) {
			logger.error("Can't connect to REST service " + uri);
			e.printStackTrace();
		} finally {
			if (response != null) {
				response.releaseConnection();
			}
		}
		return headers;
	}

	protected <T> T getObjectResult(Class<T> cls, String uri, String jsonParams, String action) throws CKANException {
		return getObjectFromJson(cls, postAndReturnTheJSON(uri, jsonParams), action);
	}

	protected DatasetList getDatasetList(String uri, String jsonParams, String action) throws CKANException {
		return getObjectResult(DatasetList.class, uri, jsonParams, action);
	}

	public List<Resource> getAllAvailableResources() throws CKANException {
		List<Resource> resources = new ArrayList<Resource>();
		for (Dataset ds : getAllAccessibleDatasetList()) {
			List<Resource> rs = ds.getResources();
			if (rs != null && rs.size() > 0)
				resources.addAll(rs);
		}
		return resources;
	}

	public List<Resource> getAllResourcesCompatibleWithSpagoBI() throws CKANException {
		List<Resource> resources = new ArrayList<Resource>();
		for (Dataset ds : getAllAccessibleDatasetList()) {
			if (ds.getState().equals("active")) {
				List<Resource> rsList = ds.getResources();
				if (rsList != null && rsList.size() > 0) {
					for (Resource rs : rsList) {
						// String[] headers = headAndReturnHeaders(rs.getUrl());
						// rs.setContentType(headers[0]);
						// rs.setContentLength(headers[1]);
						if (rs.getState().equals("active") && CKANUtils.isCompatibleWithSpagoBI(rs)) {
							rs.setPackage_name(ds.getName());
							rs.setPackage_id(ds.getId());
							rs.setPackage_license(ds.getLicense());
							rs.setPackage_isPrivate(ds.isPrivate());
							rs.setPackage_isSearchable(Boolean.parseBoolean(ds.isSearchable()));
							rs.setPackage_url(ds.getUrl());
							// Tag[] tags = (Tag[]) ds.getTags().toArray();
							// String tagList = tags[0].getName();
							// for(int i=1; i<tags.length; i++)
							// {
							// tagList += ", " + tags[i].getName();
							// }
							// rs.setPackage_tags(tagList);
							resources.add(rs);
						}
					}
				}
			}
		}
		return resources;
	}

	public List<Dataset> getAllAccessibleDatasetList() throws CKANException {

		List<Dataset> accessibleDatasets = new ArrayList<Dataset>();

		String userId = connection.getUserId();
		boolean isAuthenticated = connection.getApiKey() != null && userId != null;

		if (isAuthenticated) {
			logger.debug("Getting public and searchable private datasets for the user " + userId);
			int start = 0;
			int numberOfDatasets = searchDatasets("", "", 0).getResult().getCount();
			// TODO: we need to enable datasets paging
			if (numberOfDatasets > DATASETS_LIMIT) {
				numberOfDatasets = DATASETS_LIMIT;
			}
			long elapsedTime = System.currentTimeMillis();

			// MULTIPLE SMALL REST CALL
			// if (numberOfDatasets > 0) {
			// do {
			// datasetList = searchDatasets("", "", DEFAULT_SEARCH_MAX_RETURNED_ROWS, "", start).getResult().getResults();
			// start = start + numberOfDatasets;
			// } while (start < numberOfDatasets);
			// }

			// ONE FAT REST CALL
			accessibleDatasets = searchDatasets("", "", numberOfDatasets, "", start).getResult().getResults();

			elapsedTime = System.currentTimeMillis() - elapsedTime;
			System.out.println(elapsedTime + "ms");
			logger.debug("Public and searchable private datasets for the user " + userId + " obtained");

			logger.debug("Getting acquired datasets for the user " + userId);
			logger.debug("[NOTICE] The action \"getAcquiredDatasetList\" is not provided by official CKAN API");
			try {
				List<Dataset> acquireDatasets = getAcquiredDatasetList().getResult();
				accessibleDatasets.addAll(acquireDatasets);
				logger.debug("Acquired datasets for the user " + userId + "obtained");
			} catch (CKANException ckane) {
				logger.debug("[WARNING] The target host" + connection.getHost() + "does not support \"getAcquiredDatasetList\": skipped");
			}

			logger.debug("Getting not searchable private datasets for the user " + userId);
			for (Dataset ds : getUser(userId).getDatasets()) {
				if (ds.isPrivate() && !(Boolean.parseBoolean(ds.isSearchable()))) {
					accessibleDatasets.add(ds);
				}
			}
			logger.debug("Not searchable private datasets for the user " + userId + "obtained");

			logger.debug("Getting private organization datasets for user " + userId);
			// Organization datasets result is limited to 1000
			for (OrganizationSummary orgSummary : getOrganizationListForUser(userId)) {
				for (Dataset ds : getOrganization(orgSummary.getName()).getPackages()) {
					if (ds.isPrivate()) {
						accessibleDatasets.add(ds);
					}
				}
			}
			logger.debug("Private organization datasets for user " + userId + " obtained");
		} else {
			// No authentication details available
			logger.debug("Getting only public datasets");
			int start = 0;
			int numberOfDatasets = searchDatasets("", "", 0).getResult().getCount();
			if (numberOfDatasets > 0) {
				do {
					accessibleDatasets = searchDatasets("", "", DEFAULT_SEARCH_MAX_RETURNED_ROWS, "", start).getResult().getResults();
					start = start + DEFAULT_SEARCH_MAX_RETURNED_ROWS;
				} while (start <= numberOfDatasets);
			}
			logger.debug("Only public datasets obtained");
		}
		return accessibleDatasets;
	}

	/********************/

	public DatasetList getAcquiredDatasetList() throws CKANException {
		return getDatasetList("/api/action/acquisitions_list", "{}", "getAcquiredDatasetList");
	}

	/********************/

	public List<OrganizationSummary> getOrganizationListForUser(String name) throws CKANException {
		OrganizationSummaryList osl = getObjectFromJson(OrganizationSummaryList.class,
				postAndReturnTheJSON("/api/action/organization_list_for_user", "{\"id\":\"" + name + "\"}"), "getOrganizationListForUser");
		return osl.getResult();
	}

	/********************/

	public DatasetList getCurrentPackageListWithResources(int limit, int page) throws CKANException {
		return getDatasetList("/api/action/current_package_list_with_resources", "{\"limit\":\"" + limit + "\",\"page\":\"" + page + "\"}",
				"getCurrentPackageListWithResources");
	}

	/**
	 * Retrieves a dataset
	 *
	 * Retrieves the dataset with the given name, or ID, from the CKAN connection specified in the Client constructor.
	 *
	 * @param name
	 *            The name or ID of the dataset to fetch
	 * @returns The Dataset for the provided name.
	 * @throws A
	 *             CKANException if the request fails
	 */
	public Organization getOrganization(String name) throws CKANException {
		OrganizationResult or = getObjectFromJson(OrganizationResult.class, postAndReturnTheJSON("/api/action/organization_show", "{\"id\":\"" + name + "\"}"),
				"getDataset");
		return or.getResult();
	}

	/********************/

	public Dataset getDataset(String name) throws CKANException {
		DatasetResult dr = getObjectFromJson(DatasetResult.class, postAndReturnTheJSON("/api/action/package_show", "{\"id\":\"" + name + "\"}"), "getDataset");
		return dr.getResult();
	}

	/********************/

	public StringList getDatasetList() throws CKANException {
		return getObjectFromJson(StringList.class, postAndReturnTheJSON("/api/action/package_list", "{}"), "getDatasetList");
	}

	/********************/

	public Resource getResource(String id) throws CKANException {
		ResourceResult rr = getObjectFromJson(ResourceResult.class, postAndReturnTheJSON("/api/action/resource_show", "{\"id\":\"" + id + "\"}"), "getResource");
		return rr.result;
	}

	/********************/

	public User getUser(String id) throws CKANException {
		UserResult ur = getObjectFromJson(UserResult.class, postAndReturnTheJSON("/api/action/user_show", "{\"id\":\"" + id + "\"}"), "getUser");
		return ur.getResult();
	}

	public User getUser(User user) throws CKANException {
		String uid = user.getId();
		String name = user.getName();
		/*
		 * If uid is not blank use it, failing that use the name, failing that just send a blank string
		 */
		String id = uid != null && !uid.equals("") ? uid : name != null && !name.equals("") ? name : "";
		return getUser(id);
	}

	/********************/

	public DatasetSearchResult searchDatasets(String q) throws CKANException {
		return searchDatasets(q, "", DEFAULT_SEARCH_MAX_RETURNED_ROWS, "", DEFAULT_SEARCH_FIRST_ROW, "", true, DEFAULT_SEARCH_FACET_MIN_COUNT,
				DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters) throws CKANException {
		return searchDatasets(q, filters, DEFAULT_SEARCH_MAX_RETURNED_ROWS, "", DEFAULT_SEARCH_FIRST_ROW, "", true, DEFAULT_SEARCH_FACET_MIN_COUNT,
				DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows) throws CKANException {
		return searchDatasets(q, filters, rows, "", DEFAULT_SEARCH_FIRST_ROW, "", true, DEFAULT_SEARCH_FACET_MIN_COUNT, DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort) throws CKANException {
		return searchDatasets(q, filters, rows, sort, DEFAULT_SEARCH_FIRST_ROW, "", true, DEFAULT_SEARCH_FACET_MIN_COUNT, DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort, int start) throws CKANException {
		return searchDatasets(q, filters, rows, sort, start, "", true, DEFAULT_SEARCH_FACET_MIN_COUNT, DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort, int start, String qf) throws CKANException {
		return searchDatasets(q, filters, rows, sort, start, qf, true, DEFAULT_SEARCH_FACET_MIN_COUNT, DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort, int start, String qf, boolean isFacetedResult)
			throws CKANException {
		return searchDatasets(q, filters, rows, sort, start, qf, isFacetedResult, DEFAULT_SEARCH_FACET_MIN_COUNT, DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort, int start, String qf, boolean isFacetedResult, int facetMinCount)
			throws CKANException {
		return searchDatasets(q, filters, rows, sort, start, qf, isFacetedResult, facetMinCount, DEFAULT_SEARCH_FACET_LIMIT, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort, int start, String qf, boolean isFacetedResult,
			int facetMinCount, int facetLimit) throws CKANException {
		return searchDatasets(q, filters, rows, sort, start, qf, isFacetedResult, facetMinCount, facetLimit, null);
	}

	public DatasetSearchResult searchDatasets(String q, String filters, int rows, String sort, int start, String qf, boolean isFacetedResult,
			int facetMinCount, int facetLimit, List<String> facetField) throws CKANException {
		/*
		 * ,\"qf\":\""+qf+"\" -> removed from JSON
		 *
		 * Dismax query fields not figured out yet
		 */
		if (facetField == null) {
			facetField = new ArrayList<String>();
		}
		return getObjectFromJson(
				DatasetSearchResult.class,
				postAndReturnTheJSON("/api/action/package_search", "{\"q\":\"" + q + "\",\"fq\":\"" + filters + "\",\"rows\":\"" + rows + "\",\"sort\":\""
						+ sort + "\",\"start\":\"" + start + "\",\"facet\":\"" + isFacetedResult + "\",\"facet.mincount\":\"" + facetMinCount
						+ "\",\"facet.limit\":\"" + facetLimit + "\",\"facet.field\":\"" + getJsonFromObject(facetField, "searchPackages") + "\"}"),
				"searchPackages");
	}
}
