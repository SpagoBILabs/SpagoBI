/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.utilities;

import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.enums.UpToTypeEnum;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class AnalysisUtility {

	private static final Logger logger = Logger.getLogger(AnalysisUtility.class);

	public static Calendar setMonitorSchedulerEndingDate(TwitterMonitorScheduler twitterMonitorScheduler) {

		Calendar endingCalendar = GregorianCalendar.getInstance();
		int upToValue = twitterMonitorScheduler.getUpToValue();
		UpToTypeEnum upToType = twitterMonitorScheduler.getUpToType();

		// Calculating the ending date
		if (upToType == UpToTypeEnum.Day) {
			endingCalendar.add(Calendar.DAY_OF_MONTH, upToValue);
		} else if (upToType == UpToTypeEnum.Week) {
			endingCalendar.add(Calendar.DAY_OF_MONTH, (upToValue) * 7);
		} else if (upToType == UpToTypeEnum.Month) {
			endingCalendar.add(Calendar.DAY_OF_MONTH, (upToValue) * 30);
		}

		return endingCalendar;
	}

	/**
	 * Method to geocode tweet user location
	 *
	 * @param location
	 *            : location input inside the user's twitter profile, not always a "location"
	 * @param userTimeZone
	 *            : user time zone inside his twitter profile
	 * @return the short code of user location country
	 * @throws Exception
	 */

	public static final String findCountryCodeFromUserLocation(String location, String userTimeZone) {

		logger.debug("Method findCountryCodeFromUserLocation(): Start");

		String countryCode = null;

		try {
			String locationEncoded = "";

			if (location != null && !location.equals("")) {
				locationEncoded = URLEncoder.encode(location, "UTF-8");
			}

			if (locationEncoded.equals("") && userTimeZone != null && !userTimeZone.equals("")) {
				locationEncoded = URLEncoder.encode(userTimeZone, "UTF-8");
			}

			String urlString = "http://maps.googleapis.com/maps/api/geocode/json?address=" + locationEncoded;

			URL url = new URL(urlString);

			logger.debug("Method findCountryCodeFromUserLocation(): Calling GeoCode API..");

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

				logger.debug("Method findCountryCodeFromUserLocation(): Response OK. Analyzing result..");
				JSONArray resultsArray = obj.getJSONArray("results");

				if (resultsArray != null && resultsArray.length() > 0) {
					JSONObject obj3 = resultsArray.getJSONObject(0);
					JSONArray aComponentsArray = obj3.getJSONArray("address_components");

					if (aComponentsArray != null && aComponentsArray.length() > 0) {
						for (int i = 0; i < aComponentsArray.length(); i++) {

							JSONObject tempObj = aComponentsArray.getJSONObject(i);
							JSONArray types = tempObj.getJSONArray("types");

							if (types != null && types.length() > 0) {
								String firstType = types.getString(0);

								if (firstType.equals("country")) {
									countryCode = tempObj.getString("short_name");
								}
							}
						}
					}
				}
			}
			// TODO: gestire eccezione per evitare blocco a fine limite API
		} catch (java.net.ConnectException e) {
			logger.debug("Method findCountryCodeFromUserLocation(): Error for connection timeout calling GeoCoding" + e.getMessage());
		} catch (IOException e) {
			logger.debug("Method findCountryCodeFromUserLocation(): Error for IO operations GeoCoding" + e.getMessage());
		} catch (JSONException e) {
			logger.debug("Method findCountryCodeFromUserLocation(): Error for JSON parsing after GeoCoding call" + e.getMessage());
		}

		logger.debug("Method findCountryCodeFromUserLocation(): End");
		return countryCode;

	}

	public static String deleteEmoji(String emojiString) {

		Pattern unicodeOutliers = Pattern.compile("[^\\u0000-\\uFFEF]", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);
		Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(emojiString);

		emojiString = unicodeOutlierMatcher.replaceAll(" ");

		return emojiString;

	}

	public static String convertLocationCodeIntoCountry(String locationCode) {

		String country = "";

		Map<String, String> locationMap = new HashMap<String, String>();

		locationMap.put("AF", "Afghanistan");
		locationMap.put("AL", "Albania");
		locationMap.put("DZ", "Algeria");
		locationMap.put("AO", "Angola");
		locationMap.put("AR", "Argentina");
		locationMap.put("AM", "Armenia");
		locationMap.put("AU", "Australia");
		locationMap.put("AT", "Austria");
		locationMap.put("AZ", "Azerbaijan");
		locationMap.put("BS", "The Bahamas");
		locationMap.put("BD", "Bangladesh");
		locationMap.put("BY", "Belarus");
		locationMap.put("BE", "Belgium");
		locationMap.put("BZ", "Belize");
		locationMap.put("BJ", "Benin");
		locationMap.put("BT", "Bhutan");
		locationMap.put("BO", "Bolivia");
		locationMap.put("BA", "Bosnia and Herzegovina");
		locationMap.put("BW", "Botswana");
		locationMap.put("BR", "Brazil");
		locationMap.put("BN", "Brunei");
		locationMap.put("BG", "Bulgaria");
		locationMap.put("BF", "Burkina Faso");
		locationMap.put("BI", "Burundi");
		locationMap.put("KH", "Cambodia");
		locationMap.put("CM", "Cameroon");
		locationMap.put("CA", "Canada");
		locationMap.put("CF", "Central African Republic");
		locationMap.put("TD", "Chad");
		locationMap.put("CL", "Chile");
		locationMap.put("CN", "China");
		locationMap.put("CO", "Colombia");
		locationMap.put("CD", "Democratic Republic of the Congo");
		locationMap.put("CG", "Republic of the Congo");
		locationMap.put("CR", "Costa Rica");
		locationMap.put("CI", "Ivory Coast");
		locationMap.put("HR", "Croatia");
		locationMap.put("CY", "Cyprus");
		locationMap.put("CZ", "Czech Republic");
		locationMap.put("DK", "Denmark");
		locationMap.put("DJ", "Djibouti");
		locationMap.put("DO", "Dominican Republic");
		locationMap.put("EC", "Ecuador");
		locationMap.put("EG", "Egypt");
		locationMap.put("SV", "El Salvador");
		locationMap.put("GQ", "Equatorial Guinea");
		locationMap.put("ER", "Eritrea");
		locationMap.put("EE", "Estonia");
		locationMap.put("ET", "Ethiopia");
		locationMap.put("FJ", "Fiji");
		locationMap.put("FI", "Finland");
		locationMap.put("FR", "France");
		locationMap.put("GA", "Gabon");
		locationMap.put("GM", "Gambia");
		locationMap.put("GE", "Georgia");
		locationMap.put("DE", "Germany");
		locationMap.put("GH", "Ghana");
		locationMap.put("GR", "Greece");
		locationMap.put("GT", "Guatemala");
		locationMap.put("GN", "Guinea");
		locationMap.put("GW", "Guinea Bissau");
		locationMap.put("GY", "Guyana");
		locationMap.put("HT", "Haiti");
		locationMap.put("HN", "Honduras");
		locationMap.put("HU", "Hungary");
		locationMap.put("IS", "Iceland");
		locationMap.put("IN", "India");
		locationMap.put("ID", "Indonesia");
		locationMap.put("IR", "Iran");
		locationMap.put("IQ", "Iraq");
		locationMap.put("IE", "Ireland");
		locationMap.put("IL", "Israel");
		locationMap.put("IT", "Italy");
		locationMap.put("JM", "Jamaica");
		locationMap.put("JP", "Japan");
		locationMap.put("JO", "Jordan");
		locationMap.put("KZ", "Kazakhstan");
		locationMap.put("KE", "Kenya");
		locationMap.put("KR", "South Korea");
		locationMap.put("KW", "Kuwait");
		locationMap.put("KG", "Kyrgyzstan");
		locationMap.put("LA", "Laos");
		locationMap.put("LV", "Latvia");
		locationMap.put("LB", "Lebanon");
		locationMap.put("LS", "Lesotho");
		locationMap.put("LR", "Liberia");
		locationMap.put("LY", "Libya");
		locationMap.put("LT", "Lithuania");
		locationMap.put("LU", "Luxembourg");
		locationMap.put("MK", "Macedonia");
		locationMap.put("MG", "Madagascar");
		locationMap.put("MW", "Malawi");
		locationMap.put("MY", "Malaysia");
		locationMap.put("ML", "Mali");
		locationMap.put("MR", "Mauritania");
		locationMap.put("MX", "Mexico");
		locationMap.put("MD", "Moldova");
		locationMap.put("MN", "Mongolia");
		locationMap.put("ME", "Montenegro");
		locationMap.put("MA", "Morocco");
		locationMap.put("MZ", "Mozambique");
		locationMap.put("MM", "Myanmar");
		locationMap.put("NA", "Namibia");
		locationMap.put("NP", "Nepal");
		locationMap.put("NL", "Netherlands");
		locationMap.put("NZ", "New Zealand");
		locationMap.put("NI", "Nicaragua");
		locationMap.put("NE", "Niger");
		locationMap.put("NG", "Nigeria");
		locationMap.put("NO", "Norway");
		locationMap.put("OM", "Oman");
		locationMap.put("PK", "Pakistan");
		locationMap.put("PA", "Panama");
		locationMap.put("PG", "Papua New Guinea");
		locationMap.put("PY", "Paraguay");
		locationMap.put("PE", "Peru");
		locationMap.put("PH", "Philippines");
		locationMap.put("PL", "Poland");
		locationMap.put("PT", "Portugal");
		locationMap.put("QA", "Qatar");
		locationMap.put("RO", "Romania");
		locationMap.put("RU", "Russia");
		locationMap.put("RW", "Rwanda");
		locationMap.put("SA", "Saudi Arabia");
		locationMap.put("SN", "Senegal");
		locationMap.put("RS", "Republic of Serbia");
		locationMap.put("SL", "Sierra Leone");
		locationMap.put("SK", "Slovakia");
		locationMap.put("SI", "Slovenia");
		locationMap.put("SB", "Solomon Islands");
		locationMap.put("ZA", "South Africa");
		locationMap.put("ES", "Spain");
		locationMap.put("LK", "Sri Lanka");
		locationMap.put("SD", "Sudan");
		locationMap.put("SR", "Suriname");
		locationMap.put("SZ", "Swaziland");
		locationMap.put("SE", "Sweden");
		locationMap.put("CH", "Switzerland");
		locationMap.put("SY", "Syria");
		locationMap.put("TW", "Taiwan");
		locationMap.put("TJ", "Tajikistan");
		locationMap.put("TZ", "Tanzania");
		locationMap.put("TH", "Thailand");
		locationMap.put("TL", "East Timor");
		locationMap.put("TG", "Togo");
		locationMap.put("TT", "Trinidad and Tobago");
		locationMap.put("TN", "Tunisia");
		locationMap.put("TR", "Turkey");
		locationMap.put("TM", "Turkmenistan");
		locationMap.put("UG", "Uganda");
		locationMap.put("UA", "Ukraine");
		locationMap.put("AE", "United Arab Emirates");
		locationMap.put("GB", "United Kingdom");
		locationMap.put("US", "United States of America");
		locationMap.put("UY", "Uruguay");
		locationMap.put("UZ", "Uzbekistan");
		locationMap.put("VU", "Vanuatu");
		locationMap.put("VE", "Venezuela");
		locationMap.put("VN", "Vietnam");
		locationMap.put("YE", "Yemen");
		locationMap.put("ZM", "Zambia");
		locationMap.put("ZW", "Zimbabwe");
		locationMap.put("SS", "South Sudan");
		locationMap.put("_3", "Somaliland");
		locationMap.put("SO", "Somalia");
		locationMap.put("GL", "Greenland");
		locationMap.put("CU", "Cuba");
		locationMap.put("_1", "Kosovo");
		locationMap.put("_2", "Western Sahara");
		locationMap.put("_0", "Northern Cyprus");
		locationMap.put("PR", "Puerto Rico");
		locationMap.put("PS", "West Bank");
		locationMap.put("FK", "Falkland Islands");
		locationMap.put("NC", "New Caledonia");
		locationMap.put("KP", "North Korea");

		if (locationMap.containsKey(locationCode)) {
			country = locationMap.get(locationCode);
		}

		return country;
	}

	public static long isLong(String str) {
		try {
			long l = Long.parseLong(str);
			return l;
		} catch (NumberFormatException nfe) {
			throw new SpagoBIRuntimeException("Invalid format for search ID : " + str, nfe);
		}

	}

	public static String customEscapeString(String s) {

		String result = "";

		if (s != null && !s.equals("")) {
			s = s.replace("\r\n", " ").replace("\n", " ");
			String firstEscapedString = s.replaceAll("'", " ");
			String secondEscapedString = firstEscapedString.replaceAll(System.getProperty("line.separator"), " ");
			String thirdEscapedString = secondEscapedString.replaceAll("\n", " ");
			result = thirdEscapedString;
		}

		return result;
	}

	public static double parseDoubleUtil(String str) throws ParseException {

		Number number = NumberFormat.getNumberInstance(Locale.getDefault()).parse(str);
		return number.doubleValue();

	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
