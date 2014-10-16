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
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class UsersNetworkGraphDataProcessor {

	private static final Logger logger = Logger.getLogger(UsersNetworkGraphDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private final int NRESULTS = 50;

	private int actualMin = 0;
	private int actualMax = 0;

	private List<TwitterUser> users = new ArrayList<TwitterUser>();

	private JSONArray links = new JSONArray();

	private JSONObject profiles = new JSONObject();

	public void initializeUsersNetworkGraph(String searchID) {

		logger.debug("Method initializeUsersNetworkGraph(): Start");

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

			createLinksAndProfilesJsonArray(tweets, searchId, nTweets);

			logger.debug("Method initializeUsersNetworkGraph(): End");

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  initializeUsersNetworkGraph(): An error occurred for search ID: " + searchID, t);
		}

	}

	private void createLinksAndProfilesJsonArray(List<TwitterData> tweets, long searchId, int nTweets) {

		logger.debug("Method createLinksAndProfilesJsonArray(): Looking for results from " + this.actualMin + " to " + this.actualMax);

		try {

			createLinks(tweets);

			if (nTweets > this.actualMax) {

				logger.debug("Method createLinksAndProfilesJsonArray(): Interactions found = " + this.links.length());

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

					createLinksAndProfilesJsonArray(newTweets, searchId, nTweets);
				} else {
					if (users != null && users.size() > 0) {
						this.profiles = createNodesProfiles(users);
					}
				}
			} else {
				if (users != null && users.size() > 0) {
					this.profiles = createNodesProfiles(users);
				}
			}

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method createLinksAndProfilesJsonArray(): An error occurred calculating links and nodes", t);
		}

	}

	private JSONObject createNodesProfiles(List<TwitterUser> users) throws JSONException {

		JSONObject result = new JSONObject();

		if (users != null && users.size() > 0) {
			for (TwitterUser user : users) {

				// userObj.put("name", user.getUsername());
				// userObj.put("fixed", true);
				result.put(user.getUsername(), user.getProfileImgSrc());

			}
		}

		return result;
	}

	private void createLinks(List<TwitterData> tweets) throws JSONException, DaoServiceException {

		if (tweets != null && tweets.size() > 0 && users != null) {

			for (TwitterData tweet : tweets) {

				if (tweet.getReplyToUserId() != null) {

					TwitterData tweetToReply = twitterCache.isTwitterDataPresent(tweet.getTwitterSearch().getSearchID(),
							Long.parseLong(tweet.getReplyToUserId()));

					if (tweetToReply != null) {

						JSONObject linkObj = new JSONObject();

						linkObj.put("source", tweet.getTwitterUser().getUsername());
						linkObj.put("target", tweetToReply.getTwitterUser().getUsername());
						linkObj.put("type", "reply");

						this.links.put(linkObj);

						if (!users.contains(tweet.getTwitterUser())) {
							users.add(tweet.getTwitterUser());
						}

						if (!users.contains(tweetToReply.getTwitterUser())) {
							users.add(tweetToReply.getTwitterUser());
						}
					}

				} else if (tweet.getOriginalRTTweetId() != null) {

					TwitterData originalTweet = twitterCache.isTwitterDataPresent(tweet.getTwitterSearch().getSearchID(),
							Long.parseLong(tweet.getOriginalRTTweetId()));

					if (originalTweet != null) {
						JSONObject linkObj = new JSONObject();

						linkObj.put("source", originalTweet.getTwitterUser().getUsername());
						linkObj.put("target", tweet.getTwitterUser().getUsername());
						linkObj.put("value", "rt");

						this.links.put(linkObj);

						if (!users.contains(originalTweet.getTwitterUser())) {
							users.add(originalTweet.getTwitterUser());
						}

						if (!users.contains(tweet.getTwitterUser())) {
							users.add(tweet.getTwitterUser());
						}
					}

				}
			}
		}
	}

	public JSONArray getLinks() {
		return links;
	}

	public JSONObject getProfiles() {
		return profiles;
	}

}
