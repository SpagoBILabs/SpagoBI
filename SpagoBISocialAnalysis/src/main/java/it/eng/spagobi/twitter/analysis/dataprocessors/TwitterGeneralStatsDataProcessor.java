/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.entities.TwitterUser;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterGeneralStatsDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterGeneralStatsDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private String totalUsers = "";
	private String totalTweets = "";
	private String reach = "";
	private String impressions = "";

	private String minDateSearch = "";
	private String maxDateSearch = "";

	// array position fields
	// int userIdPos = 0;
	private final int USERFOLLOWERSPOS = 1;
	private final int USERNTWEETS = 2;

	public TwitterGeneralStatsDataProcessor() {

	}

	/**
	 * Initialize general stats
	 *
	 * @param searchID
	 */
	public void initializeTwitterGeneralStats(String searchID) {

		logger.debug("Method initializeTwitterGeneralStats(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		// check if searchID is a long and convert it
		long searchId = AnalysisUtility.isLong(searchID);

		try {

			int totalUsersInt = 0;
			long totalTweetsLong = 0;
			int reachInt = 0;
			long impressionsLong = 0;

			// count total tweets for searchID search
			// totalTweetsInt = totalTweetsCounter(searchId);

			logger.debug("Method initializeTwitterGeneralStats(): Getting users for searchID = " + searchID);
			List<TwitterUser> users = dpCache.getGeneralStatsForSearchID(searchId);

			if (users != null) {

				totalUsersInt = users.size();

				reachInt = reachInt + totalUsersInt;

				int totFollowers = 0;

				for (TwitterUser user : users) {

					int userFollowers = user.getFollowersCount();

					totFollowers = totFollowers + userFollowers;

					long tweetsNumber = dpCache.countUserTweetsFromSearchId(searchId, user.getUserID());

					totalTweetsLong = totalTweetsLong + tweetsNumber;

					impressionsLong = impressionsLong + (userFollowers * tweetsNumber) + 1;

					// totalTweetsInt = totalTweetsInt + tweetsNumber;

				}

				reachInt = reachInt + totFollowers;

			}

			this.minDateSearch = getMinDateSearch(searchId);
			this.maxDateSearch = getMaxDateSearch(searchId);

			this.totalUsers = String.format("%,d", totalUsersInt);
			this.totalTweets = String.format("%,d", totalTweetsLong);
			this.reach = String.format("%,d", reachInt);
			this.impressions = String.format("%,d", impressionsLong);

			long endMills = System.currentTimeMillis() - initMills;

			logger.debug("Method initializeTwitterGeneralStats(): End for search = " + searchId + " in " + endMills + "ms");
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method initializeTwitterGeneralStats(): An error occurred for search ID: " + searchId, t);
		}
	}

	/**
	 * This method counts total tweets for a search
	 *
	 * @param searchID
	 * @return
	 */
	private int totalTweetsCounter(long searchID) {

		try {
			logger.debug("Method totalTweetsCounter(): Start for search = " + searchID);

			int totalTweets = dpCache.getTotalTweets(searchID);

			logger.debug("Method totalTweetsCounter(): End for search = " + searchID);

			return totalTweets;
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method totalTweetsCounter(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method counts total user linked to a search
	 *
	 * @param searchID
	 * @return
	 */
	public int totalUsersCounter(long searchID) {

		try {
			logger.debug("Method totalUsersCounter(): Start");

			Assert.assertNotNull(searchID, "Impossibile execute totalUsersCounter() without a correct search ID");

			int totalUsers = dpCache.getTotalUsers(searchID);

			logger.debug("Method totalUsersCounter(): End");
			return totalUsers;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method totalUsersCounter(): An error occurred for search ID: " + searchID, t);
		}
	}

	/**
	 * This method finds the min date for a search
	 *
	 * @param searchIDStr
	 *            : search ID
	 * @return formatted minimum date for this search
	 */
	private String getMinDateSearch(long searchID) {

		try {
			logger.debug("Method getMinDataSearch(): Start");

			Assert.assertNotNull(searchID, "Impossibile execute getMinDateSearch() without a correct search ID");

			SimpleDateFormat simpleDataFormatter = new SimpleDateFormat("dd-MM-yyyy");

			Calendar minCalendar = dpCache.getMinTweetDate(searchID);

			if (minCalendar != null) {

				Date minDate = new Date(minCalendar.getTimeInMillis());

				logger.debug("Method getMinDataSearch(): End");
				return simpleDataFormatter.format(minDate);
			}
			{
				logger.debug("Method getMinDataSearch(): End");
				return "N/A";

			}
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getMinDataSearch(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method finds the max date for a search
	 *
	 * @param searchIDStr
	 *            : search ID
	 * @return formatted maximum date for this search
	 */
	private String getMaxDateSearch(long searchID) {

		try {
			logger.debug("Method getMaxDateSearch(): Start");

			Assert.assertNotNull(searchID, "Impossibile execute getMinDateSearch() without a correct search ID");

			SimpleDateFormat simpleDataFormatter = new SimpleDateFormat("dd-MM-yyyy");

			Calendar maxCalendar = dpCache.getMaxTweetDate(searchID);

			if (maxCalendar != null) {

				Date maxDate = new Date(maxCalendar.getTimeInMillis());

				logger.debug("Method getMaxDateSearch(): End");
				return simpleDataFormatter.format(maxDate);
			}
			{
				logger.debug("Method getMaxDateSearch(): End");
				return "N/A";

			}
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getMaxDateSearch(): An error occurred for search ID: " + searchID, t);
		}
	}

	public String getTotalUsers() {
		return totalUsers;
	}

	public String getTotalTweets() {
		return totalTweets;
	}

	public String getReach() {
		return reach;
	}

	public String getImpressions() {
		return impressions;
	}

	public String getMinDateSearch() {
		return minDateSearch;
	}

	public String getMaxDateSearch() {
		return maxDateSearch;
	}

}
