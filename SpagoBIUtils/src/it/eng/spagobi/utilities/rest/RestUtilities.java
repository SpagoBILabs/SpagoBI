/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.utilities.rest;

import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.json.JSONUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestUtilities {

	

	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * Reads the body of a request and return it as a string
	 *
	 * @param request
	 *            the HttpServletRequest request
	 * @return the body
	 * @throws IOException
	 */
	public static String readBody(HttpServletRequest request) throws IOException {

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
		return stringBuilder.toString();
	}

	/**
	 *
	 * Reads the body of a request and return it as a JSONObject
	 *
	 * @param request
	 *            the HttpServletRequest request
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject readBodyAsJSONObject(HttpServletRequest request) throws IOException, JSONException {
		String requestBody = RestUtilities.readBody(request);
		if (requestBody == null || requestBody.equals("")) {
			return new JSONObject();
		}
		return new JSONObject(requestBody);
	}

	/**
	 *
	 * Reads the body of a request and return it as a JSONOArray
	 *
	 * @param request
	 *            the HttpServletRequest request
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONArray readBodyAsJSONArray(HttpServletRequest request) throws IOException, JSONException {
		String requestBody = RestUtilities.readBody(request);
		return JSONUtils.toJSONArray(requestBody);
	}

	public static enum HttpMethod {
		Get, Post, Put, Delete
	}

	private static HttpMethodBase getMethod(HttpMethod method, String address) {
		String addr = address;
		if (method.equals(HttpMethod.Delete)) {
			return new DeleteMethod(addr);
		}
		if (method.equals(HttpMethod.Post)) {
			return new PostMethod(addr);
		}
		if (method.equals(HttpMethod.Get)) {
			return new GetMethod(addr);
		}
		if (method.equals(HttpMethod.Put)) {
			return new PutMethod(addr);
		}
		Assert.assertUnreachable("method doesn't exist");
		return null;
	}

	public static class Response {
		private String responseBody;
		private int statusCode;

		public Response(String responseBody, int statusCode) {
			this.responseBody = responseBody;
			this.statusCode = statusCode;
		}

		public String getResponseBody() {
			return responseBody;
		}

		public int getStatusCode() {
			return statusCode;
		}

	}

	public static Response makeRequest(HttpMethod httpMethod, String address, Map<String, String> requestHeaders, String requestBody,List<NameValuePair> query) throws HttpException,
			IOException {
		HttpMethodBase method = getMethod(httpMethod, address);
		for (Entry<String, String> entry : requestHeaders.entrySet()) {
			method.addRequestHeader(entry.getKey(), entry.getValue());
		}
		if (query!=null) {
			//add uri pairs to provided query
			List<NameValuePair> addressPairs=getAddressPairs(address);
			List<NameValuePair> totalPairs=new ArrayList<NameValuePair>(addressPairs);
			totalPairs.addAll(query);
			method.setQueryString(totalPairs.toArray(new NameValuePair[query.size()]));
		}
		if (method instanceof EntityEnclosingMethod) {
			EntityEnclosingMethod eem = (EntityEnclosingMethod) method;
			// charset of request currently not used
			eem.setRequestBody(requestBody);
		}

		try {
			HttpClient client = new HttpClient();
			int statusCode = client.executeMethod(method);
			String res = method.getResponseBodyAsString();
			return new Response(res, statusCode);
		} finally {
			method.releaseConnection();
		}
	}

	@SuppressWarnings("unchecked")
	protected static List<NameValuePair> getAddressPairs(String address) {
		try {
			String query = URIUtil.getQuery(address);
			Map<String,String> params = new ParameterParser().parse(query, '&');
			List<NameValuePair> res=new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				res.add(new NameValuePair(URIUtil.decode(key,DEFAULT_CHARSET),URIUtil.decode( params.get(key),DEFAULT_CHARSET)));
			}
			return res;
		} catch (URIException e) {
			throw new SpagoBIRuntimeException(e);
		}
	}

}
