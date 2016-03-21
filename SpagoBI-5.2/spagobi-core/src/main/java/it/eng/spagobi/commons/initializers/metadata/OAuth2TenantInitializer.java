/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.initializers.metadata;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.security.oauth2.OAuth2Client;
import it.eng.spagobi.security.oauth2.OAuth2Config;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

public class OAuth2TenantInitializer extends SpagoBIInitializer {

	static private Logger logger = Logger.getLogger(OAuth2TenantInitializer.class);

	private static Properties config;

	public OAuth2TenantInitializer() {
		config = OAuth2Config.getInstance().getConfig();
	}

	// It retrieves organizations associated with fi-ware application. If they are not inside the database, it stores them in it
	@Override
	public void init(SourceBean config, Session hibernateSession) {
		logger.debug("IN");
		try {
			String hql = "from SbiTenant";
			Query hqlQuery = hibernateSession.createQuery(hql);
			List<SbiTenant> existingTenants = hqlQuery.list();

			List<String> configuredTenants = getTenants();
			for (String aConfiguredTenant : configuredTenants) {
				if (exists(aConfiguredTenant, existingTenants)) {
					LogMF.debug(logger, "Tenant {0} already exists", aConfiguredTenant);
				} else {
					LogMF.info(logger, "Tenant {0} does not exist. It will be inserted", aConfiguredTenant);
					writeTenant(aConfiguredTenant, hibernateSession);
					LogMF.debug(logger, "Tenant {0} was inserted", aConfiguredTenant);
				}
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw new SpagoBIRuntimeException("An unexpected error occured while initializing Tenants", t);
		} finally {
			logger.debug("OUT");
		}
	}

	private boolean exists(String aConfiguredTenant, List<SbiTenant> existingTenants) {
		for (SbiTenant aTenant : existingTenants) {
			if (aTenant.getName().equals(aConfiguredTenant)) {
				return true;
			}
		}
		return false;
	}

	private void writeTenant(String tenantName, Session hibernateSession) throws Exception {
		logger.debug("IN");
		SbiTenant aTenant = new SbiTenant();
		aTenant.setName(tenantName);
		logger.debug("Inserting tenant with name = [" + tenantName + "]...");
		hibernateSession.save(aTenant);
		logger.debug("OUT");
	}

	private List<String> getTenants() {
		logger.debug("IN");
		List<String> tenants = new ArrayList<String>();

		try {
			OAuth2Client oauth2Client = new OAuth2Client();

			// Retrieve the admin's token for REST services authentication
			String token = oauth2Client.getAdminToken();

			HttpClient httpClient = oauth2Client.getHttpClient();
			// Get organizations authorized for the application (specified in the oauth2.config.properties)
			String url = config.getProperty("REST_BASE_URL") + config.getProperty("ORGANIZATIONS_LIST_PATH") + "?application_id="
					+ config.getProperty("APPLICATION_ID");
			GetMethod httpget = new GetMethod(url);
			httpget.addRequestHeader("X-Auth-Token", token);

			int statusCode = httpClient.executeMethod(httpget);
			byte[] response = httpget.getResponseBody();
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Error while getting organizations from OAuth2 provider: server returned statusCode = " + statusCode);
				LogMF.error(logger, "Server response is:\n{0}", new Object[] { new String(response) });
				throw new SpagoBIRuntimeException("Error while getting organizations from OAuth2 provider: server returned statusCode = " + statusCode);
			}

			String responseStr = new String(response);
			LogMF.debug(logger, "Server response is:\n{0}", responseStr);
			JSONObject jsonObject = new JSONObject(responseStr);
			JSONArray rolesList = jsonObject.getJSONArray("role_assignments");

			for (int i = 0; i < rolesList.length(); i++) {
				String organizationId = rolesList.getJSONObject(i).getString("organization_id");
				String organizationName = getTenantName(organizationId);

				tenants.add(organizationName);
			}

			return tenants;
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while trying to obtain tenants' informations from OAuth2 provider", e);
		} finally {
			logger.debug("OUT");
		}
	}

	private String getTenantName(String organizationId) {
		try {
			OAuth2Client oauth2Client = new OAuth2Client();

			String token = oauth2Client.getAdminToken();

			HttpClient httpClient = oauth2Client.getHttpClient();
			String url = config.getProperty("REST_BASE_URL") + config.getProperty("ORGANIZATION_INFO_PATH") + organizationId;
			GetMethod httpget = new GetMethod(url);
			httpget.addRequestHeader("X-Auth-Token", token);

			int statusCode = httpClient.executeMethod(httpget);
			byte[] response = httpget.getResponseBody();
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Error while getting the name of organization with id [" + organizationId + "]: server returned statusCode = " + statusCode);
				LogMF.error(logger, "Server response is:\n{0}", new Object[] { new String(response) });
				throw new SpagoBIRuntimeException("Error while getting the name of organization with id [" + organizationId
						+ "]: server returned statusCode = " + statusCode);
			}

			String responseStr = new String(response);
			LogMF.debug(logger, "Server response is:\n{0}", responseStr);
			JSONObject jsonObject = new JSONObject(responseStr);

			return jsonObject.getJSONObject("project").getString("name");
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while trying to obtain tenant' name from IdM REST API", e);
		} finally {
			logger.debug("OUT");
		}
	}

}
