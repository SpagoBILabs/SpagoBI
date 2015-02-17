/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

public class OAuth2SecurityInfoProvider implements ISecurityInfoProvider {
	static private Logger logger = Logger.getLogger(OAuth2SecurityInfoProvider.class);

	private String applicationName;
	private String email;
	private String password;

	@Override
	public List getRoles() {
		loadConfigs();

		JsonObject jsonObject;
		try {
			jsonObject = getJSon();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new SpagoBIRuntimeException("Error while trying to obtain application's information from fi-ware", e);
		}

		List<Role> roles = new ArrayList<Role>();
		JsonArray jsonRolesArray = jsonObject.getJsonArray("roles");

		String name;
		for (JsonValue jsonValue : jsonRolesArray) {
			name = ((JsonObject) jsonValue).getString("name");
			if (!name.equals("Provider") && !name.equals("Purchaser")) {
				roles.add(new Role(name, name));
			}
		}

		return roles;
	}

	@Override
	public List getAllProfileAttributesNames() {
		List<String> attributes = new ArrayList<String>();
		attributes.add("displayName");
		attributes.add("email");

		return attributes;
	}

	// It loads the authentication credentials used for retrieving the application's information
	private void loadConfigs() {
		ResourceBundle resourceBundle = null;
		String configFile = "it.eng.spagobi.security.OAuth2.configs";

		try {
			resourceBundle = ResourceBundle.getBundle(configFile);

			applicationName = resourceBundle.getString("APPLICATION_NAME");
			email = resourceBundle.getString("ADMIN_EMAIL");
			password = resourceBundle.getString("ADMIN_PASSWORD");
		} catch (MissingResourceException e) {
			throw new SpagoBIRuntimeException("Impossible to find configurations file [" + configFile + "]", e);
		}
	}

	// The returned Json contains the application's informations, such its name and its roles
	private JsonObject getJSon() throws IOException {
		URL url;
		url = new URL("https://account.lab.fiware.org/api/v1/tokens.json");

		// HttpsURLConnection
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("POST");

		connection.setDoOutput(true);
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		String body = "email=" + email + "&password=" + password;
		out.write(body);
		out.close();

		JsonReader jsonReader = Json.createReader(connection.getInputStream());
		JsonObject jsonObject = jsonReader.readObject();
		connection.disconnect();

		String token = jsonObject.getString("token");

		url = new URL("https://account.lab.fiware.org/applications/" + applicationName + ".json?auth_token=" + token);
		connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		jsonReader = Json.createReader(connection.getInputStream());
		JsonObject result = jsonReader.readObject();
		connection.disconnect();

		return result;
	}
}
