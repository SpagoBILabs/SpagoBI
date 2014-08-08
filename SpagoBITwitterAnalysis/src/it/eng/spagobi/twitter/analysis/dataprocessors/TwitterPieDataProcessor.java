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
import it.eng.spagobi.twitter.analysis.pojos.TwitterPiePojo;

import javax.sql.rowset.CachedRowSet;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterPieDataProcessor {

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public TwitterPiePojo getTweetsPieChart(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		int totalTweets = 0;
		int totalReplies = 0;
		int totalRTs = 0;

		String sqlQuery = "SELECT is_retweet, reply_to_tweet_id from twitter_data where search_id = '" + searchID + "'";

		try {

			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {

					totalTweets++;

					boolean isRetweet = rs.getBoolean("is_retweet");

					if (isRetweet) {
						totalRTs++;
					} else {
						String replyToTweetId = rs.getString("reply_to_tweet_id");

						if (replyToTweetId != null) {
							totalReplies++;
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		int originalTweets = totalTweets - totalRTs - totalReplies;

		TwitterPiePojo statsObj = new TwitterPiePojo(originalTweets, totalReplies, totalRTs);

		return statsObj;
	}

}
