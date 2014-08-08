package it.eng.spagobi.bitly.analysis.utilities;

import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkCategoryPojo;
import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkPojo;
import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import twitter4j.JSONArray;
import twitter4j.JSONObject;

public class BitlyCounterClicksUtility {

	private String links;
	private long searchID;

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public BitlyCounterClicksUtility(String l, long id) {
		this.links = l;
		this.searchID = id;
	}

	public BitlyCounterClicksUtility() {
	}

	public void startBitlyAnalysis() {

		String accessToken = "d32762c9990acba4b3f0bd2649d4cdef296941ae";

		String[] linksArr = links.trim().split(",");
		for (int i = 0; i < linksArr.length; i++) {
			String link = linksArr[i];
			BitlyLinkPojo linkPojo = counterClicks("https://api-ssl.bitly.com", "/v3/link/clicks", accessToken, link);
			List<BitlyLinkCategoryPojo> linkCategory = counterClicksCountries("https://api-ssl.bitly.com", "/v3/link/countries", accessToken, link);
			linkCategory.addAll(counterClicksDomains("https://api-ssl.bitly.com", "/v3/link/referring_domains", accessToken, link));

			try {

				twitterCache.insertBitlyAnalysis(linkPojo, linkCategory, searchID);

			} catch (Exception e) {
				System.out.println("**** connection failed: " + e);
			}
		}

	}

	private BitlyLinkPojo counterClicks(String addressAPI, String api, String accessToken, String link) {

		int linkClicks = 0;

		try {

			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&link=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			System.setProperty("https.proxyHost", "www-proxy");
			System.setProperty("https.proxyPort", "8080");

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

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return new BitlyLinkPojo(link, linkClicks);
	}

	private List<BitlyLinkCategoryPojo> counterClicksCountries(String addressAPI, String api, String accessToken, String link) {

		List<BitlyLinkCategoryPojo> linkCategoryPojos = new ArrayList<BitlyLinkCategoryPojo>();

		try {

			// Statement statement = conn.createStatement();

			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&link=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			System.setProperty("https.proxyHost", "www-proxy");
			System.setProperty("https.proxyPort", "8080");

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

					linkCategoryPojos.add(new BitlyLinkCategoryPojo(-1, "country", category, clicks_counter));

				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return linkCategoryPojos;
	}

	private List<BitlyLinkCategoryPojo> counterClicksDomains(String addressAPI, String api, String accessToken, String link) {

		List<BitlyLinkCategoryPojo> linkCategoryPojos = new ArrayList<BitlyLinkCategoryPojo>();

		try {

			// Statement statement = conn.createStatement();

			String linkEncoded = URLEncoder.encode(link, "UTF-8");
			String parameterLink = "&link=" + linkEncoded;

			String urlString = addressAPI + api + "?access_token=" + accessToken + parameterLink;

			System.setProperty("https.proxyHost", "www-proxy");
			System.setProperty("https.proxyPort", "8080");

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

					linkCategoryPojos.add(new BitlyLinkCategoryPojo(-1, "domain", category, clicks_counter));

				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return linkCategoryPojos;
	}

	public List<BitlyLinkPojo> getLinkToMonitor(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);
		List<BitlyLinkPojo> bitlyLinkPojos = new ArrayList<BitlyLinkPojo>();

		String sqlQuery = "SELECT * from twitter_links_to_monitor where search_id = " + searchID;

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					String link = rs.getString("link");
					int clicksCount = rs.getInt("clicks_count");

					BitlyLinkPojo bitlyPojo = new BitlyLinkPojo(link, clicksCount);
					bitlyLinkPojos.add(bitlyPojo);

					System.out.println(bitlyPojo);

				}
			}
		} catch (SQLException e) {
			System.out.println("**** connection failed: " + e);
		}

		return bitlyLinkPojos;

	}

	public List<BitlyLinkCategoryPojo> getLinkToMonitorCategory(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);
		List<BitlyLinkCategoryPojo> bitlyLinkCategoryPojos = new ArrayList<BitlyLinkCategoryPojo>();

		String sqlQuery = "SELECT lc.*, lm.link from twitter_link_to_monitor_category lc, twitter_links_to_monitor lm where lc.link_id = lm.id and lm.search_id = " + searchID;

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					String link = rs.getString("link");
					String type = rs.getString("type");
					String category = rs.getString("category");
					int clicksCount = rs.getInt("clicks_count");

					BitlyLinkCategoryPojo bitlyCategoryPojo = new BitlyLinkCategoryPojo(type, category, clicksCount, link);
					bitlyLinkCategoryPojos.add(bitlyCategoryPojo);

					System.out.println(bitlyCategoryPojo);

				}
			}
		} catch (SQLException e) {
			System.out.println("**** connection failed: " + e);
		}

		return bitlyLinkCategoryPojos;

	}
}
