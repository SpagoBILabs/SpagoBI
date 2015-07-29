/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.dataproxy;

import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.Helper;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.rest.RestUtilities;
import it.eng.spagobi.utilities.rest.RestUtilities.HttpMethod;
import it.eng.spagobi.utilities.rest.RestUtilities.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;

/**
 * This data proxy makes REST calls. Providing all attributes (address, type of method, etc..) it make a http call and read the data store from the response
 * using the provided reader.
 * 
 * @author fabrizio
 *
 */
public class RESTDataProxy extends AbstractDataProxy {

	private static final int OFFSET_NOT_DEFINED = -1;
	private static final int FETCH_SIZE_NOT_DEFINED = -1;
	
	private final String requestBody;
	private final String address;
	private final Map<String, String> requestHeaders;
	private final HttpMethod method;

	private final String offsetParam;
	private final String fetchSizeParam;
	private final String maxResultsParam;

	public RESTDataProxy(String address, HttpMethod method, String requestBody, Map<String, String> requestHeaders, String offsetParam, String fetchSizeParam,
			String maxResultsParam) {
		Helper.checkNotNull(address, "address");
		Helper.checkNotEmpty(address, "address");
		Helper.checkNotNull(method, "method");
		// cab be empty
		Helper.checkNotNull(requestHeaders, "requestHeaders");
		// can be null, can't empty
		if (requestBody != null) {
			Helper.checkNotEmpty(requestBody, "requestBody");
		}

		// offset and fetch size must exist together
		if (offsetParam != null) {
			Helper.checkNotEmpty(offsetParam, "offsetParam");
			Helper.checkNotNull(fetchSizeParam, "fetchSizeParam");
		}
		if (fetchSizeParam != null) {
			Helper.checkNotEmpty(fetchSizeParam, "fetchSizeParam");
			Helper.checkNotNull(offsetParam, "offsetParam");
		}
		if (maxResultsParam != null) {
			Helper.checkNotEmpty(maxResultsParam, "maxResultsParam");
		}

		this.address = address;
		this.method = method;
		this.requestBody = requestBody;
		this.requestHeaders = Collections.unmodifiableMap(requestHeaders);
		this.offsetParam = offsetParam;
		this.fetchSizeParam = fetchSizeParam;
		this.maxResultsParam = maxResultsParam;
	}

	public IDataStore load(IDataReader dataReader) {
		try {
			Helper.checkNotNull(dataReader, "dataReader");

			Response response = RestUtilities.makeRequest(this.method, this.address, this.requestHeaders, this.requestBody, getQuery());
			String responseBody = response.getResponseBody();
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				throw new RESTDataProxyException(String.format("The response status is not ok: status=%d, response=%s", response.getStatusCode(), responseBody));
			}

			Assert.assertNotNull(responseBody, "responseBody is null");
			IDataStore res = dataReader.read(responseBody);
			Assert.assertNotNull(res, "datastore is null");
			return res;
		} catch (RESTDataProxyException e) {
			throw e;
		} catch (Exception e) {
			throw new RESTDataProxyException(e);
		}
	}

	private List<NameValuePair> getQuery() {
		List<NameValuePair> res = new ArrayList<NameValuePair>(3);
		if (offsetParam != null) {
			if (offset != OFFSET_NOT_DEFINED) {
				res.add(new NameValuePair(offsetParam, Integer.toString(offset)));
			}
			Assert.assertTrue(fetchSizeParam != null, "fetchSizeParam!=null");
			if (fetchSize != FETCH_SIZE_NOT_DEFINED) {
				res.add(new NameValuePair(fetchSizeParam, Integer.toString(fetchSize)));
			}
		}
		if (maxResultsParam != null) {
			res.add(new NameValuePair(maxResultsParam, Integer.toString(maxResults)));
		}
		return res;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public String getAddress() {
		return address;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public HttpMethod getRequestMethod() {
		return method;
	}

	@Override
	public boolean isOffsetSupported() {
		return offsetParam != null;
	}

	@Override
	public boolean isFetchSizeSupported() {
		return fetchSizeParam != null;
	}

	@Override
	public boolean isMaxResultsSupported() {
		return maxResultsParam != null;
	}

	@Override
	public boolean isPaginationSupported() {
		boolean res = offsetParam != null;
		Assert.assertTrue(res == (fetchSizeParam != null), "res==(fetchSizeParam!=null)");
		return res;
	}

	public String getOffsetParam() {
		return offsetParam;
	}

	public String getFetchSizeParam() {
		return fetchSizeParam;
	}

	public String getMaxResultsParam() {
		return maxResultsParam;
	}

}
