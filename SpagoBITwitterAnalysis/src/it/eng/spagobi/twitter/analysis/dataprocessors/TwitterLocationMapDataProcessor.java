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

package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheFactory;

import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */

public class TwitterLocationMapDataProcessor {

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public JSONObject locationTracker(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		Map<String, Integer> locationMap = new HashMap<String, Integer>();
		JSONObject resultJSON = new JSONObject();

		initializeLocationMap(locationMap);

		String sqlQuery = "SELECT tu.location_code from twitter_users tu, twitter_data td where tu.user_id = td.user_id and td.search_id = " + searchID;

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					String locationCode = rs.getString("location_code");

					if (locationCode != null && !locationCode.equals("")) {

						Integer tweetCounter = locationMap.get(locationCode);
						tweetCounter++;
						locationMap.put(locationCode, tweetCounter);
					}
				}

				for (Map.Entry<String, Integer> entry : locationMap.entrySet()) {
					try {
						resultJSON.append(entry.getKey(), entry.getValue());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return resultJSON;

	}

	private void initializeLocationMap(Map<String, Integer> locationMap) {
		locationMap.put("AF", 0);
		locationMap.put("AL", 0);
		locationMap.put("DZ", 0);
		locationMap.put("AO", 0);
		locationMap.put("AG", 0);
		locationMap.put("AR", 0);
		locationMap.put("AM", 0);
		locationMap.put("AU", 0);
		locationMap.put("AT", 0);
		locationMap.put("AZ", 0);
		locationMap.put("BS", 0);
		locationMap.put("BH", 0);
		locationMap.put("BD", 0);
		locationMap.put("BB", 0);
		locationMap.put("BY", 0);
		locationMap.put("BE", 0);
		locationMap.put("BZ", 0);
		locationMap.put("BJ", 0);
		locationMap.put("BT", 0);
		locationMap.put("BO", 0);
		locationMap.put("BA", 0);
		locationMap.put("BW", 0);
		locationMap.put("BR", 0);
		locationMap.put("BN", 0);
		locationMap.put("BG", 0);
		locationMap.put("BF", 0);
		locationMap.put("BI", 0);
		locationMap.put("KH", 0);
		locationMap.put("CM", 0);
		locationMap.put("CA", 0);
		locationMap.put("CV", 0);
		locationMap.put("CF", 0);
		locationMap.put("TD", 0);
		locationMap.put("CL", 0);
		locationMap.put("CN", 0);
		locationMap.put("CO", 0);
		locationMap.put("KM", 0);
		locationMap.put("CD", 0);
		locationMap.put("CG", 0);
		locationMap.put("CR", 0);
		locationMap.put("CI", 0);
		locationMap.put("HR", 0);
		locationMap.put("CY", 0);
		locationMap.put("CZ", 0);
		locationMap.put("DK", 0);
		locationMap.put("DJ", 0);
		locationMap.put("DM", 0);
		locationMap.put("DO", 0);
		locationMap.put("EC", 0);
		locationMap.put("EG", 0);
		locationMap.put("SV", 0);
		locationMap.put("GQ", 0);
		locationMap.put("ER", 0);
		locationMap.put("EE", 0);
		locationMap.put("ET", 0);
		locationMap.put("FJ", 0);
		locationMap.put("FI", 0);
		locationMap.put("FR", 0);
		locationMap.put("GA", 0);
		locationMap.put("GM", 0);
		locationMap.put("GE", 0);
		locationMap.put("DE", 0);
		locationMap.put("GH", 0);
		locationMap.put("GR", 0);
		locationMap.put("GD", 0);
		locationMap.put("GT", 0);
		locationMap.put("GN", 0);
		locationMap.put("GW", 0);
		locationMap.put("GY", 0);
		locationMap.put("HT", 0);
		locationMap.put("HN", 0);
		locationMap.put("HK", 0);
		locationMap.put("HU", 0);
		locationMap.put("IS", 0);
		locationMap.put("IN", 0);
		locationMap.put("ID", 0);
		locationMap.put("IR", 0);
		locationMap.put("IQ", 0);
		locationMap.put("IE", 0);
		locationMap.put("IL", 0);
		locationMap.put("IT", 0);
		locationMap.put("JM", 0);
		locationMap.put("JP", 0);
		locationMap.put("JO", 0);
		locationMap.put("KZ", 0);
		locationMap.put("KE", 0);
		locationMap.put("KI", 0);
		locationMap.put("KR", 0);
		locationMap.put("UNDEFINED", 0);
		locationMap.put("KW", 0);
		locationMap.put("KG", 0);
		locationMap.put("LA", 0);
		locationMap.put("LV", 0);
		locationMap.put("LB", 0);
		locationMap.put("LS", 0);
		locationMap.put("LR", 0);
		locationMap.put("LY", 0);
		locationMap.put("LT", 0);
		locationMap.put("LU", 0);
		locationMap.put("MK", 0);
		locationMap.put("MG", 0);
		locationMap.put("MW", 0);
		locationMap.put("MY", 0);
		locationMap.put("MV", 0);
		locationMap.put("ML", 0);
		locationMap.put("MT", 0);
		locationMap.put("MR", 0);
		locationMap.put("MU", 0);
		locationMap.put("MX", 0);
		locationMap.put("MD", 0);
		locationMap.put("MN", 0);
		locationMap.put("ME", 0);
		locationMap.put("MA", 0);
		locationMap.put("MZ", 0);
		locationMap.put("MM", 0);
		locationMap.put("NA", 0);
		locationMap.put("NP", 0);
		locationMap.put("NL", 0);
		locationMap.put("NZ", 0);
		locationMap.put("NI", 0);
		locationMap.put("NE", 0);
		locationMap.put("NG", 0);
		locationMap.put("NO", 0);
		locationMap.put("OM", 0);
		locationMap.put("PK", 0);
		locationMap.put("PA", 0);
		locationMap.put("PG", 0);
		locationMap.put("PY", 0);
		locationMap.put("PE", 0);
		locationMap.put("PH", 0);
		locationMap.put("PL", 0);
		locationMap.put("PT", 0);
		locationMap.put("QA", 0);
		locationMap.put("RO", 0);
		locationMap.put("RU", 0);
		locationMap.put("RW", 0);
		locationMap.put("WS", 0);
		locationMap.put("ST", 0);
		locationMap.put("SA", 0);
		locationMap.put("SN", 0);
		locationMap.put("RS", 0);
		locationMap.put("SC", 0);
		locationMap.put("SL", 0);
		locationMap.put("SG", 0);
		locationMap.put("SK", 0);
		locationMap.put("SI", 0);
		locationMap.put("SB", 0);
		locationMap.put("ZA", 0);
		locationMap.put("ES", 0);
		locationMap.put("LK", 0);
		locationMap.put("KN", 0);
		locationMap.put("LC", 0);
		locationMap.put("VC", 0);
		locationMap.put("SD", 0);
		locationMap.put("SR", 0);
		locationMap.put("SZ", 0);
		locationMap.put("SE", 0);
		locationMap.put("CH", 0);
		locationMap.put("SY", 0);
		locationMap.put("TW", 0);
		locationMap.put("TJ", 0);
		locationMap.put("TZ", 0);
		locationMap.put("TH", 0);
		locationMap.put("TL", 0);
		locationMap.put("TG", 0);
		locationMap.put("TO", 0);
		locationMap.put("TT", 0);
		locationMap.put("TN", 0);
		locationMap.put("TR", 0);
		locationMap.put("TM", 0);
		locationMap.put("UG", 0);
		locationMap.put("UA", 0);
		locationMap.put("AE", 0);
		locationMap.put("GB", 0);
		locationMap.put("US", 0);
		locationMap.put("UY", 0);
		locationMap.put("UZ", 0);
		locationMap.put("VU", 0);
		locationMap.put("VE", 0);
		locationMap.put("VN", 0);
		locationMap.put("YE", 0);
		locationMap.put("ZM", 0);
		locationMap.put("ZW", 0);
		locationMap.put("SS", 0);
		locationMap.put("_3", 0);
		locationMap.put("SO", 0);
		locationMap.put("GL", 0);
		locationMap.put("CU", 0);
		locationMap.put("_1", 0);
		locationMap.put("_2", 0);
	}

}
