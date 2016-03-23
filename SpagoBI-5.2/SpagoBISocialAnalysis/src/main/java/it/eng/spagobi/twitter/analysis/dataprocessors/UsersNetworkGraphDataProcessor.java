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

	private final int NRESULTS = 30;

	private int actualMin = 0;
	private int actualMax = 0;

	private List<TwitterUser> users = new ArrayList<TwitterUser>();

	private JSONArray links = new JSONArray();

	private JSONObject profiles = new JSONObject();

	public void initializeUsersNetworkGraph(String searchID) {

		logger.debug("Method initializeUsersNetworkGraph(): Start for searchID = " + searchID);

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

			createLinksAndProfilesJsonArray(tweets, searchId, nTweets);

			long endMills = System.currentTimeMillis() - initMills;

			logger.debug("Method initializeUsersNetworkGraph(): End for search = " + searchId + " in " + endMills + "ms");

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

				if (user.getProfileImgSrc() != null && !user.getProfileImgSrc().equals("")) {
					String modifiedProfileImgSrc = user.getProfileImgSrc().replace("http", "https");
					user.setProfileImgSrc(modifiedProfileImgSrc);
				}
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

					// TwitterUser tweetUserToReply = dpCache.getUserFromTweet(tweet.getTwitterSearch().getSearchID(),
					// Long.parseLong(tweet.getReplyToUserId()));

					TwitterUser tweetUserToReply = tweet.getReplyUser();

					if (tweetUserToReply != null) {

						JSONObject linkObj = new JSONObject();

						linkObj.put("source", tweet.getTwitterUser().getUsername());
						linkObj.put("target", tweetUserToReply.getUsername());
						linkObj.put("type", "reply");

						this.links.put(linkObj);

						if (!users.contains(tweet.getTwitterUser())) {
							users.add(tweet.getTwitterUser());
						}

						if (!users.contains(tweetUserToReply)) {
							users.add(tweetUserToReply);
						}
					}

				} else if (tweet.getOriginalRTTweetId() != null) {

					// TwitterUser originalTweetUser = dpCache.getUserFromTweet(tweet.getTwitterSearch().getSearchID(),
					// Long.parseLong(tweet.getOriginalRTTweetId()));

					TwitterUser originalTweetUser = tweet.getRtUser();

					if (originalTweetUser != null) {
						JSONObject linkObj = new JSONObject();

						linkObj.put("source", originalTweetUser.getUsername());
						linkObj.put("target", tweet.getTwitterUser().getUsername());
						linkObj.put("value", "rt");

						this.links.put(linkObj);

						if (!users.contains(originalTweetUser)) {
							users.add(originalTweetUser);
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
