/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.bitly.analysis.utilities;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterLinkToMonitor;
import it.eng.spagobi.twitter.analysis.entities.TwitterLinkToMonitorCategory;
import it.eng.spagobi.twitter.analysis.launcher.TestTwitterAnalysisLauncher;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class BitlyCounterClicksUtility {

	private static final Logger logger = Logger.getLogger(BitlyCounterClicksUtility.class);

	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private String links;
	private long searchID;

	public BitlyCounterClicksUtility(String l, long id) {
		this.links = l;
		this.searchID = id;
	}

	public BitlyCounterClicksUtility() {
	}

	public long getSearchID() {
		return searchID;
	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	public void startBitlyAnalysis() {

		logger.debug("Method startBitlyAnalysis(): Start");

		// TODO: parametro da rendere configurabile
		// String accessToken = "d32762c9990acba4b3f0bd2649d4cdef296941ae";

		try {

			Properties bitlyProp = new Properties();

			String bitlyFile = "bitly.properties";

			InputStream inputStream = BitlyCounterClicksUtility.class.getClassLoader().getResourceAsStream(bitlyFile);

			bitlyProp.load(inputStream);

			Assert.assertNotNull(bitlyProp, "Impossible to call bitly API without a valid bitly.properties file");

			String accessToken = bitlyProp.getProperty("accessToken");

			String[] linksArr = links.trim().split(",");
			for (int i = 0; i < linksArr.length; i++) {
				String link = linksArr[i].trim();
				TwitterLinkToMonitor twitterLinkToMonitor = counterClicks("https://api-ssl.bitly.com", "/v3/link/clicks", accessToken, link);
				setLongUrlFromBirtlyAPI("https://api-ssl.bitly.com", "/v3/expand", accessToken, link, twitterLinkToMonitor);
				List<TwitterLinkToMonitorCategory> categoryList = counterClicksCountries("https://api-ssl.bitly.com", "/v3/link/countries", accessToken, link);
				categoryList.addAll(counterClicksDomains("https://api-ssl.bitly.com", "/v3/link/referring_domains", accessToken, link));

				twitterCache.insertBitlyAnalysis(twitterLinkToMonitor, categoryList, searchID);

				logger.debug("Method startBitlyAnalysis(): End");

			}
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method createLinksAndCodesJsonArray(): An error occurred for search ID: " + searchID, t);
		}
	}

	public void monitorBitlyLink(String link) {

		logger.debug("Method monitorBitlyLink(): Start");

		try {
			Properties bitlyProp = new Properties();

			String bitlyFile = "bitly.properties";

			InputStream inputStream = TestTwitterAnalysisLauncher.class.getClassLoader().getResourceAsStream(bitlyFile);

			bitlyProp.load(inputStream);

			Assert.assertNotNull(bitlyProp, "Impossible to call bitly API without a valid bitly.properties file");

			String accessToken = bitlyProp.getProperty("accessToken");

			TwitterLinkToMonitor twitterLinkToMonitor = counterClicks("https://api-ssl.bitly.com", "/v3/link/clicks", accessToken, link);
			List<TwitterLinkToMonitorCategory> categoryList = counterClicksCountries("https://api-ssl.bitly.com", "/v3/link/countries", accessToken, link);
			categoryList.addAll(counterClicksDomains("https://api-ssl.bitly.com", "/v3/link/referring_domains", accessToken, link));

			twitterCache.insertBitlyAnalysis(twitterLinkToMonitor, categoryList, searchID);
			logger.debug("Method monitorBitlyLink(): End");

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method monitorBitlyLink(): An error occurred for search ID: " + searchID, t);
		}

	}

	private TwitterLinkToMonitor counterClicks(String addressAPI, String api, String accessToken, String link) {

		logger.debug("Method counterClicks(): Start");

		int linkClicks = 0;

		try {
			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&link=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			URL url = new URL(urlString);

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = null;

			String json = "";
			while ((line = reader.readLine()) != null) {
				json = json + line;
			}

			reader.close();

			JSONObject obj = new JSONObject(json);

			String status = obj.getString("status_code");

			if (status.equals("200")) {

				JSONObject dataObj = obj.getJSONObject("data");
				linkClicks = dataObj.getInt("link_clicks");

			}

			TwitterLinkToMonitor twitterLinkToMonitor = new TwitterLinkToMonitor();
			twitterLinkToMonitor.setLink(link);
			twitterLinkToMonitor.setClicksCount(linkClicks);
			twitterLinkToMonitor.setTimestamp(GregorianCalendar.getInstance());

			logger.debug("Method counterClicks(): End");

			return twitterLinkToMonitor;

		} catch (JSONException ex) {
			throw new SpagoBIRuntimeException("Method counterClicks(): Impossible to parse JSON response from Bitly - " + ex);
		} catch (IOException ex) {
			throw new SpagoBIRuntimeException("Method counterClicks(): Impossible to execute correctly IO operations - " + ex);
		}
	}

	private void setLongUrlFromBirtlyAPI(String addressAPI, String api, String accessToken, String link, TwitterLinkToMonitor twitterLink) {

		logger.debug("Method setLongUrlFromBirtlyAPI(): Start");

		String longUrl = "";

		try {
			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&shortUrl=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			URL url = new URL(urlString);

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = null;

			String json = "";
			while ((line = reader.readLine()) != null) {
				json = json + line;
			}

			reader.close();

			JSONObject obj = new JSONObject(json);

			String status = obj.getString("status_code");

			if (status.equals("200")) {

				JSONObject dataObj = obj.getJSONObject("data");

				if (dataObj != null) {

					JSONArray expand = dataObj.getJSONArray("expand");

					if (expand != null && expand.length() > 0) {

						JSONObject expandObj = expand.getJSONObject(0);

						if (expandObj != null) {

							longUrl = expandObj.getString("long_url");
						}
					}
				}

			}

			twitterLink.setLongUrl(longUrl);

			logger.debug("Method counterClicks(): End");

		} catch (JSONException ex) {
			throw new SpagoBIRuntimeException("Method setLongUrlFromBirtlyAPI(): Impossible to parse JSON response from Bitly - " + ex);
		} catch (IOException ex) {
			throw new SpagoBIRuntimeException("Method setLongUrlFromBirtlyAPI(): Impossible to execute correctly IO operations - " + ex);
		}
	}

	private List<TwitterLinkToMonitorCategory> counterClicksCountries(String addressAPI, String api, String accessToken, String link) {

		logger.debug("Method counterClicksCountries(): Start");

		List<TwitterLinkToMonitorCategory> linksCategory = new ArrayList<TwitterLinkToMonitorCategory>();

		try {

			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&link=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			URL url = new URL(urlString);

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = null;

			String json = "";
			while ((line = reader.readLine()) != null) {
				json = json + line;
			}

			reader.close();

			JSONObject obj = new JSONObject(json);

			String status = obj.getString("status_code");

			if (status.equals("200")) {

				JSONObject dataObj = obj.getJSONObject("data");
				JSONArray countriesArr = dataObj.getJSONArray("countries");

				for (int i = 0; i < countriesArr.length(); i++) {
					JSONObject categoryObj = countriesArr.getJSONObject(i);
					String category = categoryObj.getString("country");
					int clicks_counter = categoryObj.getInt("clicks");

					TwitterLinkToMonitorCategory linkCategory = new TwitterLinkToMonitorCategory();
					linkCategory.setClicksCount(clicks_counter);
					// TODO: Enum Type
					linkCategory.setType("country");
					linkCategory.setCategory(category);

					linksCategory.add(linkCategory);

				}
			}

			logger.debug("Method counterClicksCountries(): End");

			return linksCategory;

		} catch (JSONException ex) {
			throw new SpagoBIRuntimeException("Method counterClicksCountries(): Impossible to parse JSON response from Bitly - " + ex);
		} catch (IOException ex) {
			throw new SpagoBIRuntimeException("Method counterClicksCountries(): Impossible to execute correctly IO operations - " + ex);
		}

	}

	private List<TwitterLinkToMonitorCategory> counterClicksDomains(String addressAPI, String api, String accessToken, String link) {

		logger.debug("Method counterClicksDomains(): Start");

		List<TwitterLinkToMonitorCategory> linksCategory = new ArrayList<TwitterLinkToMonitorCategory>();

		try {

			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&link=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			URL url = new URL(urlString);

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = null;

			String json = "";
			while ((line = reader.readLine()) != null) {
				json = json + line;
			}

			reader.close();

			JSONObject obj = new JSONObject(json);

			String status = obj.getString("status_code");

			if (status.equals("200")) {

				JSONObject dataObj = obj.getJSONObject("data");
				JSONArray countriesArr = dataObj.getJSONArray("referring_domains");

				for (int i = 0; i < countriesArr.length(); i++) {
					JSONObject categoryObj = countriesArr.getJSONObject(i);
					String category = categoryObj.getString("domain");
					int clicks_counter = categoryObj.getInt("clicks");

					TwitterLinkToMonitorCategory linkCategory = new TwitterLinkToMonitorCategory();
					linkCategory.setClicksCount(clicks_counter);
					// TODO: Enum Type
					linkCategory.setType("domain");
					linkCategory.setCategory(category);

					linksCategory.add(linkCategory);

				}
			}

			logger.debug("Method counterClicksDomains(): End");

			return linksCategory;

		} catch (JSONException ex) {
			throw new SpagoBIRuntimeException("Method counterClicksDomains(): Impossible to parse JSON response from Bitly - " + ex);
		} catch (IOException ex) {
			throw new SpagoBIRuntimeException("Method counterClicksDomains(): Impossible to execute correctly IO operations - " + ex);
		}

	}

	// public List<BitlyLinkPojo> getLinkToMonitor(String searchIDStr) {
	//
	// long searchID = Long.parseLong(searchIDStr);
	// List<BitlyLinkPojo> bitlyLinkPojos = new ArrayList<BitlyLinkPojo>();
	//
	// String sqlQuery = "SELECT * from twitter_links_to_monitor where search_id = " + searchID;
	//
	// try {
	// CachedRowSet rs = twitterCache.runQuery(sqlQuery);
	//
	// if (rs != null) {
	//
	// while (rs.next()) {
	// String link = rs.getString("link");
	// int clicksCount = rs.getInt("clicks_count");
	//
	// BitlyLinkPojo bitlyPojo = new BitlyLinkPojo(link, clicksCount);
	// bitlyLinkPojos.add(bitlyPojo);
	//
	// }
	// }
	// } catch (SQLException e) {
	// System.out.println("**** connection failed: " + e);
	// }
	//
	// return bitlyLinkPojos;
	//
	// }
	//
	// public List<BitlyLinkCategoryPojo> getLinkToMonitorCategory(String searchIDStr) {
	//
	// long searchID = Long.parseLong(searchIDStr);
	// List<BitlyLinkCategoryPojo> bitlyLinkCategoryPojos = new ArrayList<BitlyLinkCategoryPojo>();
	//
	// String sqlQuery =
	// "SELECT lc.*, lm.link from twitter_link_to_monitor_category lc, twitter_links_to_monitor lm where lc.link_id = lm.id and lm.search_id = "
	// + searchID;
	//
	// try {
	// CachedRowSet rs = twitterCache.runQuery(sqlQuery);
	//
	// if (rs != null) {
	//
	// while (rs.next()) {
	// String link = rs.getString("link");
	// String type = rs.getString("type");
	// String category = rs.getString("category");
	// int clicksCount = rs.getInt("clicks_count");
	//
	// BitlyLinkCategoryPojo bitlyCategoryPojo = new BitlyLinkCategoryPojo(type, category, clicksCount, link);
	// bitlyLinkCategoryPojos.add(bitlyCategoryPojo);
	//
	// }
	// }
	// } catch (SQLException e) {
	// System.out.println("**** connection failed: " + e);
	// }
	//
	// return bitlyLinkCategoryPojos;
	//
	// }

}
