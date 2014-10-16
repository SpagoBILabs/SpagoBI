/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterTagCloudDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterTagCloudDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	/**
	 * This method creates the json array for the hashtag cloud
	 *
	 * @param searchID
	 * @return
	 */
	public JSONArray tagCloudCreate(String searchID) {

		logger.debug("Method tagCloudCreate(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			JSONArray jsonTagCloudArr = new JSONArray();
			List<String> globalHashtags = new ArrayList<String>();

			List<String> hashtagsList = dpCache.getHashtags(searchId);

			for (String hashtagsFromDb : hashtagsList) {

				if (hashtagsFromDb != null) {
					hashtagsFromDb = hashtagsFromDb.toLowerCase();
					String[] hashtagsSplitted = hashtagsFromDb.split(" ");

					for (int i = 0; i < hashtagsSplitted.length; i++) {
						globalHashtags.add(hashtagsSplitted[i]);
					}
				}
			}

			Map<String, Integer> htagWeghtMap = generateHtagWeight(globalHashtags);

			if (htagWeghtMap != null) {
				jsonTagCloudArr = tweetIntoJSON(htagWeghtMap);
			}

			logger.debug("Method tagCloudCreate(): End");
			return jsonTagCloudArr;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  tagCloudCreate(): An error occurred for search ID: " + searchID, t);
		}

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

		return jsonArr;
	}

}
