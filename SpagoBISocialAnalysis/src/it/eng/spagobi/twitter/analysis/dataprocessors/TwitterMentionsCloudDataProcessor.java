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
public class TwitterMentionsCloudDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterMentionsCloudDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private JSONArray mentions = new JSONArray();

	public TwitterMentionsCloudDataProcessor() {

	}

	/**
	 * Initialize mentions cloud
	 *
	 * @param searchID
	 */
	public void initializeTwitterMentionsCloud(String searchID) {

		logger.debug("Method initializeTwitterMentionsCloud(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		// check if searchID is a long and convert it
		long searchId = AnalysisUtility.isLong(searchID);

		this.mentions = this.mentionsCloudCreate(searchId);

		long endMills = System.currentTimeMillis() - initMills;

		logger.debug("Method initializeTwitterMentionsCloud(): End for search = " + searchId + " in " + endMills + "ms");
	}

	/**
	 * This method creates the json array for the mentions cloud
	 *
	 * @param searchID
	 * @return
	 */
	private JSONArray mentionsCloudCreate(long searchId) {

		logger.debug("Method mentionsCloudCreate(): Start");

		try {

			JSONArray jsonMentionsCloudArr = new JSONArray();
			List<String> globalMentions = new ArrayList<String>();

			List<String> mentionsList = dpCache.getMentions(searchId);

			for (String mentions : mentionsList) {

				if (mentions != null && !mentions.trim().equals("")) {

					mentions = mentions.toLowerCase();

					String[] mentionsSplitted = mentions.split(" ");

					for (int i = 0; i < mentionsSplitted.length; i++) {
						globalMentions.add(mentionsSplitted[i]);
					}
				}
			}

			Map<String, Integer> mentionsMap = generateMentionsMap(globalMentions);

			if (mentionsMap != null) {
				jsonMentionsCloudArr = tweetIntoJSON(mentionsMap);
			}

			logger.debug("Method mentionsCloudCreate(): End");
			return jsonMentionsCloudArr;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method mentionsCloudCreate(): An error occurred for search ID: " + searchId, t);
		}

	}

	private Map<String, Integer> generateMentionsMap(List<String> mentions) {

		Map<String, Integer> mentionsMap = new HashMap<String, Integer>();

		if (!mentions.isEmpty()) {
			for (String mention : mentions) {
				if (!mentionsMap.containsKey(mentions)) {
					Integer occurences = new Integer(Collections.frequency(mentions, mention));

					if (occurences != null) {
						if (occurences.intValue() > 50) {
							occurences = new Integer(50);
						}
					}

					mentionsMap.put(mention, occurences);
				}
			}
		}

		return mentionsMap;
	}

	private JSONArray tweetIntoJSON(Map<String, Integer> mentionsMap) {
		JSONArray jsonArr = new JSONArray();

		for (Map.Entry<String, Integer> entry : mentionsMap.entrySet()) {
			try {
				JSONObject obj = new JSONObject();
				obj.append("text", entry.getKey());
				obj.append("weight", entry.getValue());
				jsonArr.put(obj);
			} catch (JSONException e) {
				throw new SpagoBIRuntimeException("Method tweetIntoJSON(): Impossible to parse a mentions map int a JSON Array - " + e.getMessage());
			}
		}

		return jsonArr;
	}

	public JSONArray getMentions() {
		return mentions;
	}

}
