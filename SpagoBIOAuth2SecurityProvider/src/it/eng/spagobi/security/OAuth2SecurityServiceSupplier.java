package it.eng.spagobi.security;

import it.eng.spagobi.services.security.bo.SpagoBIUserProfile;
import it.eng.spagobi.services.security.service.ISecurityServiceSupplier;

import java.net.URL;
import java.util.ArrayList;
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
		try {
			URL url2 = new URL("https://account.lab.fiware.org/user?access_token=" + userId);
			HttpsURLConnection con = (HttpsURLConnection) url2.openConnection();
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);

			JsonReader r = Json.createReader(con.getInputStream());
			JsonObject j = r.readObject();
			con.disconnect();
			r.close();

			SpagoBIUserProfile profile = new SpagoBIUserProfile();
			profile.setUniqueIdentifier(Integer.toString(j.getInt("id")));
			profile.setUserId(Integer.toString(j.getInt("id")));
			profile.setUserName(j.getString("displayName"));
			profile.setOrganization("SPAGOBI"); // TODO
			profile.setIsSuperadmin(false);

			JsonArray array = j.getJsonArray("roles");
			List<String> roles = new ArrayList<String>();

			for (int i = 0; i < array.size(); i++) {
				String name = array.getJsonObject(i).getString("name");
				if (!name.equals("Provider") && !name.equals("Purchaser"))
					roles.add(name);
			}
			String[] rolesStr = new String[roles.size()];
			profile.setRoles(roles.toArray(rolesStr));

			return profile;
		} catch (Exception e) {
			// TODO
			return null;
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
