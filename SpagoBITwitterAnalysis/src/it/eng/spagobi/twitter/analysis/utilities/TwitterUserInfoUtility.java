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
package it.eng.spagobi.twitter.analysis.utilities;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheFactory;
import it.eng.spagobi.twitter.analysis.pojos.TwitterAccountToMonitorPojo;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class TwitterUserInfoUtility {

	// private final String accounts;
	private long searchID;

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public TwitterUserInfoUtility(long s) {
		// this.accounts = a;
		this.searchID = s;
	}

	public TwitterUserInfoUtility() {

	}

	// Chiamate che restituiscono un valore puntuale su un determinato attributo
	// dell'utente

	// modificare con ricerca user_id

	public void saveFollowersCount(String username) {

		int followers = 0;

		try {

			Twitter twitter = new TwitterFactory().getInstance();

			User twitterUser = twitter.showUser(username);

			if (twitterUser != null) {
				followers = twitterUser.getFollowersCount();
			}

			twitterCache.insertAccountToMonitor(new TwitterAccountToMonitorPojo(searchID, username, followers, new java.sql.Timestamp(GregorianCalendar.getInstance()
					.getTimeInMillis())));

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}
	}

	public void saveFollowersCount(long userID) {

		int followers = 0;
		String username = "";

		try {

			Twitter twitter = new TwitterFactory().getInstance();

			User twitterUser = twitter.showUser(userID);

			if (twitterUser != null) {
				followers = twitterUser.getFollowersCount();
				username = twitterUser.getScreenName();

			}

			twitterCache.insertAccountToMonitor(new TwitterAccountToMonitorPojo(searchID, username, followers, new java.sql.Timestamp(GregorianCalendar.getInstance()
					.getTimeInMillis())));

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}
	}

	public List<TwitterAccountToMonitorPojo> getFollowersAccountsToMonitor(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		List<TwitterAccountToMonitorPojo> accountsToMonitor = new ArrayList<TwitterAccountToMonitorPojo>();

		String sqlQuery = "SELECT search_id, account_name, followers_count, timestamp from twitter_accounts_to_monitor where search_id = " + searchID;

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					String account = rs.getString("account_name");
					int followersCount = rs.getInt("followers_count");
					java.sql.Timestamp timestamp = rs.getTimestamp("timestamp");

					TwitterAccountToMonitorPojo accountPojo = new TwitterAccountToMonitorPojo(searchID, account, followersCount, timestamp);
					accountsToMonitor.add(accountPojo);

					System.out.println(accountPojo);
				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return accountsToMonitor;
	}

	public long getUserIDByUsername(String username) {

		long userID = -1;

		String sqlQuery = "SELECT user_id from twitter_users where username = '" + username;

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					userID = rs.getLong("user_id");
				}
			} else {
				Twitter twitter = new TwitterFactory().getInstance();

				User twitterUser = twitter.showUser(username);
				userID = twitterUser.getId();
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return userID;
	}
}
