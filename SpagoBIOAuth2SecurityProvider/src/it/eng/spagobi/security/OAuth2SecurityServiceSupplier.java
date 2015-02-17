/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.security;

import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

public class OAuth2SecurityServiceSupplier implements ISecurityServiceSupplier {
	static private Logger logger = Logger.getLogger(OAuth2SecurityServiceSupplier.class);

	@Override
	public SpagoBIUserProfile createUserProfile(String userId) {
		SpagoBIUserProfile profile;
		try {
			URL url = new URL("https://account.lab.fiware.org/user?access_token=" + userId);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			JsonReader jsonReader = Json.createReader(connection.getInputStream());
			JsonObject jsonObject = jsonReader.readObject();
			connection.disconnect();
			jsonReader.close();

			profile = new SpagoBIUserProfile();
			profile.setUniqueIdentifier(Integer.toString(jsonObject.getInt("id")));
			profile.setUserId(Integer.toString(jsonObject.getInt("id")));
			profile.setUserName(jsonObject.getString("displayName"));
			// profile.setOrganization("SPAGOBI"); // TODO: mettere o no?
			profile.setIsSuperadmin(false); // TODO: va bene??

			JsonArray jsonRolesArray = jsonObject.getJsonArray("roles");
			List<String> roles = new ArrayList<String>();

			// Read roles
			String name;
			for (int i = 0; i < jsonRolesArray.size(); i++) {
				name = jsonRolesArray.getJsonObject(i).getString("name");
				if (!name.equals("Provider") && !name.equals("Purchaser"))
					roles.add(name);
			}

			// If no roles were found, search for roles in the organizations
			if (roles.size() == 0) {
				JsonArray organizations = jsonObject.getJsonArray("organizations");

				// For each organization
				for (int i = 0; i < organizations.size(); i++) {
					// TODO String organizationName = organizations.getJsonObject(i).getString("displayName");
					jsonRolesArray = organizations.getJsonObject(i).getJsonArray("roles");

					// For each role in the current organization
					for (int k = 0; k < jsonRolesArray.size(); k++) {
						// TODO profile.setOrganization(organizationName);
						name = jsonRolesArray.getJsonObject(k).getString("name");
						if (!name.equals("Provider") && !name.equals("Purchaser"))
							roles.add(name);
					}
				}
			}

			String[] rolesString = new String[roles.size()];
			profile.setRoles(roles.toArray(rolesString));

			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put("displayName", jsonObject.getString("displayName"));
			attributes.put("email", jsonObject.getString("email"));
			profile.setAttributes(attributes);

			return profile;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new SpagoBIRuntimeException("Error while trying to obtain user information from fi-ware", e);
		}
	}

	@Override
	public SpagoBIUserProfile checkAuthentication(String userId, String psw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpagoBIUserProfile checkAuthenticationWithToken(String userId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkAuthorization(String userId, String function) {
		// TODO Auto-generated method stub
		return false;
	}

}
