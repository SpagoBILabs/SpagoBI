/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.utilities;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.entities.TwitterAccountToMonitor;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterUserInfoUtility {

	private static final Logger logger = Logger.getLogger(TwitterUserInfoUtility.class);

	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private long searchID;

	public TwitterUserInfoUtility(long s) {

		this.searchID = s;
	}

	public TwitterUserInfoUtility() {

	}

	public void saveFollowersCount(String username) {

		logger.debug("Method saveFollowersCount(): Start");

		int followers = 0;

		try {

			Twitter twitter = new TwitterFactory().getInstance();

			User twitterUser = twitter.showUser(username);

			if (twitterUser != null) {
				followers = twitterUser.getFollowersCount();
			}

			TwitterAccountToMonitor twitterAccountToMonitor = new TwitterAccountToMonitor();
			twitterAccountToMonitor.setAccountName("@" + username);
			twitterAccountToMonitor.setFollowersCount(followers);
			twitterAccountToMonitor.setTimestamp(GregorianCalendar.getInstance());

			twitterCache.insertAccountToMonitor(twitterAccountToMonitor, searchID);

			logger.debug("Method saveFollowersCount(): End");

		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Method saveFollowersCount(): Impossible to execute correctly Twitter4J call [ShowUser] - " + t);
		}
	}

	// public void saveFollowersCount(long userID) {
	//
	// int followers = 0;
	// String username = "";
	//
	// try {
	//
	// Twitter twitter = new TwitterFactory().getInstance();
	//
	// User twitterUser = twitter.showUser(userID);
	//
	// if (twitterUser != null) {
	// followers = twitterUser.getFollowersCount();
	// username = twitterUser.getScreenName();
	//
	// }
	//
	// twitterCache.insertAccountToMonitor(new TwitterAccountToMonitorPojo(searchID, username, followers, new java.sql.Timestamp(GregorianCalendar
	// .getInstance().getTimeInMillis())));
	//
	// } catch (Exception e) {
	// System.out.println("**** connection failed: " + e);
	// }
	// }

	// public List<TwitterAccountToMonitorPojo> getFollowersAccountsToMonitor(String searchIDStr) {
	//
	// long searchID = Long.parseLong(searchIDStr);
	//
	// List<TwitterAccountToMonitorPojo> accountsToMonitor = new ArrayList<TwitterAccountToMonitorPojo>();
	//
	// String sqlQuery = "SELECT search_id, account_name, followers_count, timestamp from twitter_accounts_to_monitor where search_id = " + searchID;
	//
	// try {
	// CachedRowSet rs = twitterCache.runQuery(sqlQuery);
	//
	// if (rs != null) {
	//
	// while (rs.next()) {
	// String account = rs.getString("account_name");
	// int followersCount = rs.getInt("followers_count");
	// java.sql.Timestamp timestamp = rs.getTimestamp("timestamp");
	//
	// TwitterAccountToMonitorPojo accountPojo = new TwitterAccountToMonitorPojo(searchID, account, followersCount, timestamp);
	// accountsToMonitor.add(accountPojo);
	//
	// }
	// }
	//
	// } catch (Exception e) {
	// System.out.println("**** connection failed: " + e);
	// }
	//
	// return accountsToMonitor;
	// }

	// public long getUserIDByUsername(String username) {
	//
	// long userID = -1;
	//
	// String sqlQuery = "SELECT user_id from twitter_users where username = '" + username;
	//
	// try {
	// CachedRowSet rs = twitterCache.runQuery(sqlQuery);
	//
	// if (rs != null) {
	//
	// while (rs.next()) {
	// userID = rs.getLong("user_id");
	// }
	// } else {
	// Twitter twitter = new TwitterFactory().getInstance();
	//
	// User twitterUser = twitter.showUser(username);
	// userID = twitterUser.getId();
	// }
	//
	// } catch (Exception e) {
	// System.out.println("**** connection failed: " + e);
	// }
	//
	// return userID;
	// }

	// public void getUserIDsByUsernames(String[] usernames) {
	//
	// try {
	// Twitter twitter = new TwitterFactory().getInstance();
	// ResponseList<User> users = twitter.lookupUsers(usernames);
	//
	// for (User user : users) {
	//
	// }
	//
	// } catch (Exception e) {
	// System.out.println("**** connection failed: " + e);
	// }
	//
	// }
}
