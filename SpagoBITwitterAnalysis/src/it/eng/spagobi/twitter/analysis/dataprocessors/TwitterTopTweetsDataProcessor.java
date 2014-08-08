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
import it.eng.spagobi.twitter.analysis.pojos.TwitterTopTweetsPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */

public class TwitterTopTweetsDataProcessor {

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public List<TwitterTopTweetsPojo> getTopTweetsData(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		List<TwitterTopTweetsPojo> topTweetsData = new ArrayList<TwitterTopTweetsPojo>();

		String sqlQuery = "SELECT DISTINCT tu.username, td.date_created_at, tu.profile_image_source, td.hashtags, td.tweet_text, td.time_created_at, td.retweet_count, tu.followers_count from twitter_users tu, twitter_data td where tu.user_id = td.user_id and td.search_id = '"
				+ searchID + "'";

		try {
			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {
					String usernameFromDb = rs.getString("username");

					java.sql.Date tempDate = rs.getDate("date_created_at");
					String createDateFromDb = new SimpleDateFormat("dd MMM").format(tempDate);

					String profileImgSrcFromDB = rs.getString("profile_image_source");

					String hashtagsFromDb = rs.getString("hashtags");
					List<String> hashtags = new ArrayList<String>();
					if (!hashtagsFromDb.isEmpty()) {
						hashtagsFromDb = hashtagsFromDb.toLowerCase();
						// hashtagsFromDb = hashtagsFromDb.replaceAll("#", "");
						String[] hashtagsSplitted = hashtagsFromDb.split(" ");
						hashtags.addAll(Arrays.asList(hashtagsSplitted));
					}

					String tweetTextFromDb = rs.getString("tweet_text");

					java.sql.Timestamp tempTime = rs.getTimestamp("time_created_at");
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTime(tempTime);

					int counterRTs = rs.getInt("retweet_count");

					int userFollowersCount = Integer.parseInt(rs.getString("followers_count"));

					TwitterTopTweetsPojo tempObj = new TwitterTopTweetsPojo(usernameFromDb, createDateFromDb, profileImgSrcFromDB, hashtagsFromDb, tweetTextFromDb, hashtags,
							calendar, userFollowersCount, counterRTs);

					topTweetsData.add(tempObj);
				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return topTweetsData;
	}

}
