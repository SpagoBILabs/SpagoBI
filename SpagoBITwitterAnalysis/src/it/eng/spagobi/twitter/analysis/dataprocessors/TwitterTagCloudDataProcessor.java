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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterTagCloudDataProcessor {

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public JSONArray tagCloudCreate(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		JSONArray jsonTagCloudArr = new JSONArray();
		List<String> globalHashtags = new ArrayList<String>();

		String sqlQuery = "SELECT hashtags from twitter_data where search_id = '" + searchID + "'";

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					String hashtagsFromDb = rs.getString("hashtags");

					if (hashtagsFromDb != null) {
						hashtagsFromDb = hashtagsFromDb.toLowerCase();
						String[] hashtagsSplitted = hashtagsFromDb.split(" ");

						for (int i = 0; i < hashtagsSplitted.length; i++) {
							globalHashtags.add(hashtagsSplitted[i]);
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		Map<String, Integer> htagWeghtMap = generateHtagWeight(globalHashtags);

		if (htagWeghtMap != null) {
			jsonTagCloudArr = tweetIntoJSON(htagWeghtMap);
		}

		return jsonTagCloudArr;

	}

	private Map<String, Integer> generateHtagWeight(List<String> htags) {
		Map<String, Integer> weightsMap = new HashMap<String, Integer>();

		if (!htags.isEmpty()) {
			for (String htag : htags) {
				if (!weightsMap.containsKey(htag)) {
					Integer occurences = new Integer(Collections.frequency(htags, htag));

					if (occurences != null) {
						if (occurences.intValue() > 50) {
							occurences = new Integer(50);
						}
					}

					weightsMap.put(htag, occurences);
				}
			}
		}

		return weightsMap;
	}

	private JSONArray tweetIntoJSON(Map<String, Integer> htagsMap) {
		JSONArray jsonArr = new JSONArray();

		for (Map.Entry<String, Integer> entry : htagsMap.entrySet()) {
			try {
				JSONObject obj = new JSONObject();
				obj.append("text", entry.getKey());
				obj.append("weight", entry.getValue());
				jsonArr.put(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		System.out.println(jsonArr.toString());

		return jsonArr;
	}

}
