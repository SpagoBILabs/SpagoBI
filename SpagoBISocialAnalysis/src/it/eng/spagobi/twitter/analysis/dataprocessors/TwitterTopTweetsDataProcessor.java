/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.pojos.TwitterTopTweetsPojo;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterTopTweetsDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterTopTweetsDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	/**
	 * This method creats the objects to show in top retweets box
	 *
	 * @param searchID
	 * @param nProfiles
	 * @return
	 */
	public List<TwitterTopTweetsPojo> getTopTweetsData(String searchID, int nProfiles) {

		logger.debug("Method getTopTweetsData(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {
			List<TwitterTopTweetsPojo> topTweetsData = new ArrayList<TwitterTopTweetsPojo>();

			List<TwitterData> topTweets = dpCache.getTopTweetsRTsOrder(searchId, nProfiles);

			for (TwitterData twitterData : topTweets) {

				String usernameFromDb = twitterData.getTwitterUser().getUsername();
				java.sql.Date tempDate = new java.sql.Date(twitterData.getDateCreatedAt().getTimeInMillis());
				String createDateFromDb = new SimpleDateFormat("dd MMM").format(tempDate);
				String profileImgSrcFromDB = twitterData.getTwitterUser().getProfileImgSrc();
				String hashtagsFromDb = twitterData.getHashtags();
				String tweetTextFromDb = twitterData.getTweetText();
				java.sql.Timestamp tempTime = new java.sql.Timestamp(twitterData.getTimeCreatedAt().getTimeInMillis());
				int counterRTs = twitterData.getRetweetCount();
				int userFollowersCount = twitterData.getTwitterUser().getFollowersCount();

				List<String> hashtags = new ArrayList<String>();
				if (!hashtagsFromDb.isEmpty()) {
					hashtagsFromDb = hashtagsFromDb.toLowerCase();
					// hashtagsFromDb = hashtagsFromDb.replaceAll("#", "");
					String[] hashtagsSplitted = hashtagsFromDb.split(" ");
					hashtags.addAll(Arrays.asList(hashtagsSplitted));
				}

				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(tempTime);

				TwitterTopTweetsPojo tempObj = new TwitterTopTweetsPojo(usernameFromDb, createDateFromDb, profileImgSrcFromDB, hashtagsFromDb, tweetTextFromDb,
						hashtags, calendar, userFollowersCount, counterRTs);

				topTweetsData.add(tempObj);

				nProfiles++;
			}

			logger.debug("Method getTopTweetsData(): End");
			return topTweetsData;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  getTopTweetsData(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method creats the objects to show in top recent box
	 *
	 * @param searchID
	 * @param nProfiles
	 * @return
	 */
	public List<TwitterTopTweetsPojo> getTopRecentTweetsData(String searchID, int nProfiles) {

		logger.debug("Method getTopRecentTweetsData(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			List<TwitterTopTweetsPojo> topTweetsData = new ArrayList<TwitterTopTweetsPojo>();

			List<TwitterData> topTweets = dpCache.getTopTweetsRecentOrder(searchId, nProfiles);

			for (TwitterData twitterData : topTweets) {

				String usernameFromDb = twitterData.getTwitterUser().getUsername();
				java.sql.Date tempDate = new java.sql.Date(twitterData.getDateCreatedAt().getTimeInMillis());
				String createDateFromDb = new SimpleDateFormat("dd MMM").format(tempDate);
				String profileImgSrcFromDB = twitterData.getTwitterUser().getProfileImgSrc();
				String hashtagsFromDb = twitterData.getHashtags();
				String tweetTextFromDb = twitterData.getTweetText();
				java.sql.Timestamp tempTime = new java.sql.Timestamp(twitterData.getTimeCreatedAt().getTimeInMillis());
				int counterRTs = twitterData.getRetweetCount();
				int userFollowersCount = twitterData.getTwitterUser().getFollowersCount();

				List<String> hashtags = new ArrayList<String>();
				if (!hashtagsFromDb.isEmpty()) {
					hashtagsFromDb = hashtagsFromDb.toLowerCase();
					// hashtagsFromDb = hashtagsFromDb.replaceAll("#", "");
					String[] hashtagsSplitted = hashtagsFromDb.split(" ");
					hashtags.addAll(Arrays.asList(hashtagsSplitted));
				}

				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(tempTime);

				TwitterTopTweetsPojo tempObj = new TwitterTopTweetsPojo(usernameFromDb, createDateFromDb, profileImgSrcFromDB, hashtagsFromDb, tweetTextFromDb,
						hashtags, calendar, userFollowersCount, counterRTs);

				topTweetsData.add(tempObj);

				nProfiles++;
			}

			logger.debug("Method getTopRecentTweetsData(): End");
			return topTweetsData;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  getTopRecentTweetsData(): An error occurred for search ID: " + searchID, t);
		}
	}

}
