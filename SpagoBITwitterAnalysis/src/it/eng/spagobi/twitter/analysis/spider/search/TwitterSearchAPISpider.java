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
package it.eng.spagobi.twitter.analysis.spider.search;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class TwitterSearchAPISpider {

	static final Logger logger = Logger.getLogger(TwitterSearchAPISpider.class);

	private Query query;
	private ITwitterCache cache;
	private String languageCode;
	private ResultType resultType;
	private Date untilDate;
	private long searchID;

	public void setCache(ITwitterCache cache) {
		this.cache = cache;
	}

	public void setQuery(String queryText) {
		this.query = new Query(queryText);
	}

	public void setLanguage(String languageCode) {
		this.languageCode = languageCode;
	}

	public void setResultType(ResultType rType) {
		this.resultType = rType;
	}

	public void setUntil(Date date) {
		this.untilDate = date;
	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	/**
	 *
	 * @param queryText
	 * @param languageCode
	 *            Optional, ISO 639-1 language code
	 */
	public void collectTweets() {

		Twitter twitter = new TwitterFactory().getInstance();
		try {

			// Important: The Search API allow to search only the tweets indexed
			// in the last 6-9 days
			// Limit is 100 for page
			query.setCount(100);

			if (languageCode != null) {
				query.lang(languageCode);
			}

			// Search criteria Recent, Mixed or Popular
			query.resultType(this.resultType);

			int searchResultCount;
			long lowestTweetId = Long.MAX_VALUE;

			cache.openConnection();

			do {
				QueryResult queryResult = twitter.search(query);

				searchResultCount = queryResult.getTweets().size();

				for (Status tweet : queryResult.getTweets()) {

					// elaborate the tweet
					// System.out.println("@" + tweet.getUser().getScreenName()
					// + " - " + tweet.getText());
					logger.debug("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());

					if (searchID == -1) {
						throw new SQLException("Creating user failed, no rows affected.");
					}

					cache.saveTweet(tweet, query.getQuery(), searchID);

					if (tweet.getId() < lowestTweetId) {
						lowestTweetId = tweet.getId();
						query.setMaxId(lowestTweetId);
					}
				}

			} while (searchResultCount != 0 && searchResultCount % 100 == 0);
			cache.closeConnection();

		} catch (Exception te) {
			cache.closeConnection();
			;
			te.printStackTrace();
			// System.out.println("Failed to search tweets: " +
			// te.getMessage());
			logger.error("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}
}
