/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.exceptions.DaoServiceException;
import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.entities.TwitterUser;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
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
public class UsersNetworkLinkMapDataProcessor {

	private static final Logger logger = Logger.getLogger(UsersNetworkLinkMapDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private final int NRESULTS = 30;

	private int actualMin = 0;
	private int actualMax = 0;

	private JSONArray links = new JSONArray();
	private JSONArray contriesCodes = new JSONArray();
	private JSONObject weightLinks = new JSONObject();

	private Map<String, Integer> codes = new HashMap<String, Integer>();
	private Map<String, Integer> linksWeight = new HashMap<String, Integer>();

	public void initializeUsersNetworkLinkMap(String searchID) {

		logger.debug("Method initializeUsersNetworkLinkMap(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			int nTweets = dpCache.getTotalTweets(searchId);

			List<TwitterData> tweets = new ArrayList<TwitterData>();

			if (nTweets > NRESULTS) {

				this.actualMax = NRESULTS - 1;

			} else {

				this.actualMax = nTweets;
			}

			tweets = dpCache.getLimitedTweetsFromSearchId(searchId, actualMin, this.actualMax);

			createLinksAndCodesJsonArray(tweets, searchId, nTweets);

			// tweets = dpCache.getLimitedTweetsFromSearchId(searchId, 0, 1000);
			// List<String> codes = dpCache.getDistinctUsersLocationCodes(searchID, 50);

			// createLinksAndCodesJsonArray(tweets);
			// this.contriesCodes = createCountriesList(codes);

			long endMills = System.currentTimeMillis() - initMills;

			logger.debug("Method initializeUsersNetworkLinkMap(): End for search = " + searchId + " in " + endMills + "ms");

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method initializeUsersNetworkLinkMap(): An error occurred for search ID: " + searchID, t);
		}

	}

	private void createLinksAndCodesJsonArray(List<TwitterData> tweets, long searchId, int nTweets) throws JSONException {

		logger.debug("Method createLinksAndCodesJsonArray(): Looking for results from " + this.actualMin + " to " + this.actualMax);

		try {

			createMapLinks(tweets);

			if (nTweets > this.actualMax) {

				logger.debug("Method createLinksAndCodesJsonArray(): Interactions found = " + this.links.length());

				if (this.links.length() < NRESULTS) {

					List<TwitterData> newTweets = new ArrayList<TwitterData>();

					// missing links to complete results
					int gap = NRESULTS - this.links.length();

					this.actualMin = this.actualMin + NRESULTS;
					int newMax = this.actualMin + gap - 1;

					if (nTweets < newMax) {
						this.actualMax = nTweets;

					} else {
						this.actualMax = newMax;
					}

					newTweets = dpCache.getLimitedTweetsFromSearchId(searchId, this.actualMin, this.actualMax);

					createLinksAndCodesJsonArray(newTweets, searchId, nTweets);
				} else {
					if (codes != null && codes.size() > 0) {
						this.contriesCodes = createCountriesList(codes);
					}

					if (linksWeight != null && linksWeight.size() > 0) {
						this.weightLinks = createWeightLinksObject(linksWeight);
					}
				}
			} else {
				if (codes != null && codes.size() > 0) {
					this.contriesCodes = createCountriesList(codes);
				}

				if (linksWeight != null && linksWeight.size() > 0) {
					this.weightLinks = createWeightLinksObject(linksWeight);
				}
			}

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method createLinksAndCodesJsonArray(): An error occurred loading links and countries", t);
		}

	}

	private JSONArray createCountriesList(Map<String, Integer> countries) {

		JSONArray result = new JSONArray();

		if (countries != null && countries.size() > 0) {
			for (Map.Entry<String, Integer> entry : countries.entrySet()) {

				// TODO: add a check for equal time tweet and rt
				String cc = entry.getKey();
				int repetition = entry.getValue();

				JSONArray tempArray = new JSONArray();

				tempArray.put(cc);
				tempArray.put(repetition);

				result.put(tempArray);

			}
		}

		return result;
	}

	private JSONObject createWeightLinksObject(Map<String, Integer> links) throws JSONException {

		JSONObject result = new JSONObject();

		if (links != null && links.size() > 0) {

			for (Map.Entry<String, Integer> entry : links.entrySet()) {

				String label = entry.getKey();
				int repetition = entry.getValue();

				result.put(label, repetition);
			}
		}

		return result;
	}

	private void createMapLinks(List<TwitterData> tweets) throws DaoServiceException {

		if (tweets != null && tweets.size() > 0) {

			for (TwitterData tweet : tweets) {

				if ((tweet.getTwitterUser().getLocationCode() != null && !tweet.getTwitterUser().getLocationCode().trim().equals(""))) {

					if (tweet.getReplyToUserId() != null) {

						// TwitterData tweetToReply = twitterCache.isTwitterDataPresent(tweet.getTwitterSearch().getSearchID(),
						// Long.parseLong(tweet.getReplyToUserId()));

						TwitterUser tweetUserToReply = tweet.getReplyUser();

						if (tweetUserToReply != null && (tweetUserToReply.getLocationCode() != null && !tweetUserToReply.getLocationCode().trim().equals(""))) {

							String sourceCountry = tweet.getTwitterUser().getLocationCode();
							String tagetCountry = tweetUserToReply.getLocationCode();

							if (!sourceCountry.equals(tagetCountry)) {

								JSONArray linkArr = new JSONArray();

								linkArr.put(sourceCountry);
								linkArr.put(tagetCountry);

								this.links.put(linkArr);

								// Weight label for arc map

								String label = sourceCountry + tagetCountry;
								String inverseLabel = tagetCountry + sourceCountry;

								if (linksWeight.containsKey(label)) {
									int counter = linksWeight.get(label);
									counter++;
									linksWeight.put(label, counter);
								} else if (linksWeight.containsKey(inverseLabel)) {
									int counter = linksWeight.get(inverseLabel);
									counter++;
									linksWeight.put(inverseLabel, counter);
								} else {
									linksWeight.put(label, 1);
								}

								// Weight codes for point map radius
								String keyOne = tweet.getTwitterUser().getLocationCode();

								if (codes.containsKey(keyOne)) {

									int counter = codes.get(keyOne);
									counter++;
									codes.put(keyOne, counter);
								} else {

									codes.put(keyOne, 1);
								}

								String keyTwo = tweetUserToReply.getLocationCode();

								if (codes.containsKey(keyTwo)) {

									int counter = codes.get(keyTwo);
									counter++;
									codes.put(keyTwo, counter);

								} else {

									codes.put(keyTwo, 1);
								}
							}

						}

					} else if (tweet.getOriginalRTTweetId() != null) {

						// TwitterData originalTweet = twitterCache.isTwitterDataPresent(tweet.getTwitterSearch().getSearchID(),
						// Long.parseLong(tweet.getOriginalRTTweetId()));

						TwitterUser originalTweetUser = tweet.getRtUser();

						if (originalTweetUser != null
								&& (originalTweetUser.getLocationCode() != null && !originalTweetUser.getLocationCode().trim().equals(""))) {

							String sourceCountry = originalTweetUser.getLocationCode();
							String tagetCountry = tweet.getTwitterUser().getLocationCode();

							if (!sourceCountry.equals(tagetCountry)) {

								JSONArray linkArr = new JSONArray();

								linkArr.put(sourceCountry);
								linkArr.put(tagetCountry);

								this.links.put(linkArr);

								// Weight label for arc map

								String label = sourceCountry + tagetCountry;
								String inverseLabel = tagetCountry + sourceCountry;

								if (linksWeight.containsKey(label)) {
									int counter = linksWeight.get(label);
									counter++;
									linksWeight.put(label, counter);
								} else if (linksWeight.containsKey(inverseLabel)) {
									int counter = linksWeight.get(inverseLabel);
									counter++;
									linksWeight.put(inverseLabel, counter);
								} else {
									linksWeight.put(label, 1);
								}

								String keyOne = originalTweetUser.getLocationCode();

								if (codes.containsKey(keyOne)) {

									int counter = codes.get(keyOne);
									counter++;
									codes.put(keyOne, counter);
								} else {

									codes.put(keyOne, 1);
								}

								String keyTwo = tweet.getTwitterUser().getLocationCode();

								if (codes.containsKey(keyTwo)) {

									int counter = codes.get(keyTwo);
									counter++;
									codes.put(keyTwo, counter);

								} else {

									codes.put(keyTwo, 1);
								}
							}

						}

					}
				}
			}
		}

	}

	// private boolean validCountries(String source, String target) {
	// if (source != null && !source.trim().equals("") && target != null && !target.trim().equals("")) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	public JSONArray getLinks() {
		return links;
	}

	public JSONArray getContriesCodes() {
		return contriesCodes;
	}

	public JSONObject getWeightLinks() {
		return weightLinks;
	}

	public void setWeightLinks(JSONObject weightLinks) {
		this.weightLinks = weightLinks;
	}

}
