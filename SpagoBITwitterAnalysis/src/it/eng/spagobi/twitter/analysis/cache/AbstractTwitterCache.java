/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/

package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkCategoryPojo;
import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkPojo;
import it.eng.spagobi.twitter.analysis.pojos.TwitterAccountToMonitorPojo;
import it.eng.spagobi.twitter.analysis.pojos.TwitterMessageObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.Status;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public abstract class AbstractTwitterCache implements ITwitterCache {

	private String url;
	private String driver;
	private String userName;
	private String password;
	private String tableName;

	public AbstractTwitterCache(String url, String driver, String userName, String password) {
		this.url = url;
		this.driver = driver;
		this.userName = userName;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTableName() {
		if (tableName == null) {
			return "twitter_data";
		} else {
			return tableName;
		}
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public abstract Connection openConnection();

	@Override
	public abstract void closeConnection();

	@Override
	public abstract long insertTwitterSearch(String keywords, String searchType, String searchLabel);

	public abstract void insertTwitterUser(TwitterMessageObject twitterMessage);

	public abstract void insertTweet(TwitterMessageObject twitterMessage, long searchID);

	@Override
	public abstract void insertBitlyAnalysis(BitlyLinkPojo linkPojo, List<BitlyLinkCategoryPojo> linkCategoryPojos, long searchID);

	@Override
	public abstract void insertAccountToMonitor(TwitterAccountToMonitorPojo accountToMonitor);

	public abstract CachedRowSet runQuery(String sqlQuery);

	/**
	 * Method to retrieve data from a tweet
	 *
	 * @param tweet
	 * @param keyword
	 * @throws Exception
	 */
	@Override
	public final void saveTweet(Status tweet, String keyword, long searchID) throws Exception {

		TwitterMessageObject twitterMessage = new TwitterMessageObject(tweet);

		if (twitterMessage.getUserID() > 0) {
			insertTwitterUser(twitterMessage);
		}

		if (twitterMessage.getUserID() > 0 && twitterMessage.getTweetID() > 0) {
			insertTweet(twitterMessage, searchID);
		}

	}

	/**
	 * Method to geocode tweet user location
	 *
	 * @param location
	 * @param userTimeZone
	 * @return
	 * @throws Exception
	 */

	public final String findCountryCodeFromUserLocation(String location, String userTimeZone) throws Exception {

		String countryCode = "";
		String locationEncoded = "";

		if (location != null && !location.equals("")) {
			locationEncoded = URLEncoder.encode(location, "UTF-8");
		} else {
			if (userTimeZone != null && !userTimeZone.equals("")) {
				locationEncoded = URLEncoder.encode(userTimeZone, "UTF-8");
			}
		}

		String urlString = "http://maps.googleapis.com/maps/api/geocode/json?address=" + locationEncoded + "&sensor=false";

		System.setProperty("http.proxyHost", "www-proxy");
		System.setProperty("http.proxyPort", "8080");

		URL url = new URL(urlString);

		// System.out.println(urlString);

		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		String line = null;

		String json = "";
		while ((line = reader.readLine()) != null) {
			json = json + line;
		}

		reader.close();

		JSONObject obj = new JSONObject(json);

		String status = obj.getString("status");

		if (status.equals("OK")) {
			JSONArray obj2 = obj.getJSONArray("results");
			JSONObject obj3 = obj2.getJSONObject(0);
			JSONArray obj4 = obj3.getJSONArray("address_components");

			for (int i = 0; i < obj4.length(); i++) {
				JSONObject tempObj = obj4.getJSONObject(i);
				JSONArray types = tempObj.getJSONArray("types");
				String firstType = types.getString(0);

				if (firstType.equals("country")) {
					countryCode = tempObj.getString("short_name");
				}
			}
		}

		return countryCode;

	}

}
