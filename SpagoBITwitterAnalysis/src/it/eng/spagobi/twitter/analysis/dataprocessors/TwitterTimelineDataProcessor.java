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
import it.eng.spagobi.twitter.analysis.pojos.TwitterTimelinePojo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */

public class TwitterTimelineDataProcessor {

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public List<TwitterTimelinePojo> getTimelineObjsDaily(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		List<TwitterTimelinePojo> timelineChartObjs = new ArrayList<TwitterTimelinePojo>();

		LinkedHashMap<Long, Integer> tweetsMap = new LinkedHashMap<Long, Integer>();
		LinkedHashMap<Long, Integer> rtsMap = new LinkedHashMap<Long, Integer>();

		Calendar timeMax = GregorianCalendar.getInstance();
		Calendar timeMin = GregorianCalendar.getInstance();

		timeMax.add(Calendar.DAY_OF_MONTH, 1);
		timeMax.set(Calendar.HOUR_OF_DAY, 0);
		timeMax.set(Calendar.MINUTE, 0);
		timeMax.set(Calendar.SECOND, 0);
		timeMax.set(Calendar.MILLISECOND, 0);

		timeMin.set(Calendar.HOUR_OF_DAY, 0);
		timeMin.set(Calendar.MINUTE, 0);
		timeMin.set(Calendar.SECOND, 0);
		timeMin.set(Calendar.MILLISECOND, 0);

		long lowerBound = timeMin.getTimeInMillis();
		long upperBound = timeMax.getTimeInMillis();

		String sqlQuery = "SELECT t.time_created_at, t.is_retweet from twitter_data t where search_id = '" + searchID + "' and '" + new java.sql.Date(timeMin.getTimeInMillis())
				+ "' = t.date_created_at order by t.time_created_at asc";

		try {

			while (timeMin.compareTo(timeMax) <= 0) {

				tweetsMap.put(timeMin.getTimeInMillis(), 0);
				rtsMap.put(timeMin.getTimeInMillis(), 0);

				timeMin.add(Calendar.HOUR_OF_DAY, 1);
			}

			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {

					// recupero il time del tweet e lo converto ai mills
					java.sql.Timestamp timeFromDB = rs.getTimestamp("time_created_at");
					Calendar tweetTime = GregorianCalendar.getInstance();
					tweetTime.setTime(timeFromDB);

					tweetTime.set(Calendar.SECOND, 0);
					tweetTime.set(Calendar.MILLISECOND, 0);
					tweetTime.set(Calendar.MINUTE, 0);

					long tweetTimeMills = tweetTime.getTimeInMillis();

					// cerco se il tweet è un RT
					boolean isRetweet = rs.getBoolean("is_retweet");

					if (tweetsMap.containsKey(tweetTimeMills)) {
						int tweets = tweetsMap.get(tweetTimeMills);
						tweets++;
						tweetsMap.put(tweetTimeMills, tweets);

						if (isRetweet) {
							int retweets = rtsMap.get(tweetTimeMills);
							retweets++;
							rtsMap.put(tweetTimeMills, retweets);
						}
					}
				}
			}

			for (Map.Entry<Long, Integer> entry : tweetsMap.entrySet()) {

				long time = entry.getKey();
				int nTweets = entry.getValue();
				int nRTs = rtsMap.get(time);
				TwitterTimelinePojo obj = new TwitterTimelinePojo(time, nTweets, nRTs, lowerBound, upperBound);
				timelineChartObjs.add(obj);
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return timelineChartObjs;

	}

	public List<TwitterTimelinePojo> getTimelineObjsWeekly(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		List<TwitterTimelinePojo> timelineChartObjs = new ArrayList<TwitterTimelinePojo>();

		LinkedHashMap<Long, Integer> tweetsMap = new LinkedHashMap<Long, Integer>();
		LinkedHashMap<Long, Integer> rtsMap = new LinkedHashMap<Long, Integer>();

		Calendar timeMax = GregorianCalendar.getInstance();
		Calendar timeMin = GregorianCalendar.getInstance();

		timeMax.add(Calendar.DAY_OF_MONTH, 1);
		timeMax.set(Calendar.HOUR_OF_DAY, 0);
		timeMax.set(Calendar.MINUTE, 0);
		timeMax.set(Calendar.SECOND, 0);
		timeMax.set(Calendar.MILLISECOND, 0);

		timeMin.add(Calendar.DAY_OF_MONTH, -6);
		timeMin.set(Calendar.HOUR_OF_DAY, 0);
		timeMin.set(Calendar.MINUTE, 0);
		timeMin.set(Calendar.SECOND, 0);
		timeMin.set(Calendar.MILLISECOND, 0);

		long lowerBound = timeMin.getTimeInMillis();
		long upperBound = timeMax.getTimeInMillis();

		String sqlQuery = "SELECT t.time_created_at, t.is_retweet from twitter_data t where search_id = '" + searchID + "' and '" + new java.sql.Date(timeMin.getTimeInMillis())
				+ "' <= t.date_created_at and t.date_created_at <= '" + new java.sql.Date(timeMax.getTimeInMillis()) + "'  order by t.time_created_at asc";

		try {

			while (timeMin.compareTo(timeMax) <= 0) {

				tweetsMap.put(timeMin.getTimeInMillis(), 0);
				rtsMap.put(timeMin.getTimeInMillis(), 0);

				timeMin.add(Calendar.DAY_OF_MONTH, 1);

			}

			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {

					// recupero il time del tweet e lo converto ai mills
					java.sql.Timestamp timeFromDB = rs.getTimestamp("time_created_at");
					Calendar tweetTime = GregorianCalendar.getInstance();
					tweetTime.setTime(timeFromDB);

					tweetTime.set(Calendar.SECOND, 0);
					tweetTime.set(Calendar.MILLISECOND, 0);
					tweetTime.set(Calendar.MINUTE, 0);
					tweetTime.set(Calendar.HOUR_OF_DAY, 0);

					long tweetTimeMills = tweetTime.getTimeInMillis();

					// cerco se il tweet è un RT
					boolean isRetweet = rs.getBoolean("is_retweet");

					if (tweetsMap.containsKey(tweetTimeMills)) {
						int tweets = tweetsMap.get(tweetTimeMills);
						tweets++;
						tweetsMap.put(tweetTimeMills, tweets);

						if (isRetweet) {
							int retweets = rtsMap.get(tweetTimeMills);
							retweets++;
							rtsMap.put(tweetTimeMills, retweets);
						}
					}
				}
			}

			for (Map.Entry<Long, Integer> entry : tweetsMap.entrySet()) {

				long time = entry.getKey();
				int nTweets = entry.getValue();
				int nRTs = rtsMap.get(time);
				TwitterTimelinePojo obj = new TwitterTimelinePojo(time, nTweets, nRTs, lowerBound, upperBound);
				timelineChartObjs.add(obj);
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return timelineChartObjs;

	}

	public List<TwitterTimelinePojo> getTimelineObjsMonthly(String searchIDStr) {

		long searchID = Long.parseLong(searchIDStr);

		List<TwitterTimelinePojo> timelineChartObjs = new ArrayList<TwitterTimelinePojo>();

		LinkedHashMap<Long, Integer> tweetsMap = new LinkedHashMap<Long, Integer>();
		LinkedHashMap<Long, Integer> rtsMap = new LinkedHashMap<Long, Integer>();

		Calendar timeMax = GregorianCalendar.getInstance();
		Calendar timeMin = GregorianCalendar.getInstance();

		timeMax.add(Calendar.DAY_OF_MONTH, 1);
		timeMax.set(Calendar.HOUR_OF_DAY, 0);
		timeMax.set(Calendar.MINUTE, 0);
		timeMax.set(Calendar.SECOND, 0);
		timeMax.set(Calendar.MILLISECOND, 0);

		timeMin.add(Calendar.MONTH, -1);
		timeMin.set(Calendar.HOUR_OF_DAY, 0);
		timeMin.set(Calendar.MINUTE, 0);
		timeMin.set(Calendar.SECOND, 0);
		timeMin.set(Calendar.MILLISECOND, 0);

		long lowerBound = timeMin.getTimeInMillis();
		long upperBound = timeMax.getTimeInMillis();

		String sqlQuery = "SELECT t.time_created_at, t.is_retweet from twitter_data t where search_id = '" + searchID + "' and '" + new java.sql.Date(timeMin.getTimeInMillis())
				+ "' <= t.date_created_at and t.date_created_at <= '" + new java.sql.Date(timeMax.getTimeInMillis()) + "'  order by t.time_created_at asc";

		try {

			while (timeMin.compareTo(timeMax) <= 0) {

				tweetsMap.put(timeMin.getTimeInMillis(), 0);
				rtsMap.put(timeMin.getTimeInMillis(), 0);

				timeMin.add(Calendar.DAY_OF_MONTH, 1);

			}

			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {

					// recupero il time del tweet e lo converto ai mills
					java.sql.Timestamp timeFromDB = rs.getTimestamp("time_created_at");
					Calendar tweetTime = GregorianCalendar.getInstance();
					tweetTime.setTime(timeFromDB);

					tweetTime.set(Calendar.SECOND, 0);
					tweetTime.set(Calendar.MILLISECOND, 0);
					tweetTime.set(Calendar.MINUTE, 0);
					tweetTime.set(Calendar.HOUR_OF_DAY, 0);

					long tweetTimeMills = tweetTime.getTimeInMillis();

					// cerco se il tweet è un RT
					boolean isRetweet = rs.getBoolean("is_retweet");

					if (tweetsMap.containsKey(tweetTimeMills)) {
						int tweets = tweetsMap.get(tweetTimeMills);
						tweets++;
						tweetsMap.put(tweetTimeMills, tweets);

						if (isRetweet) {
							int retweets = rtsMap.get(tweetTimeMills);
							retweets++;
							rtsMap.put(tweetTimeMills, retweets);
						}
					}
				}
			}

			for (Map.Entry<Long, Integer> entry : tweetsMap.entrySet()) {

				long time = entry.getKey();
				int nTweets = entry.getValue();
				int nRTs = rtsMap.get(time);
				TwitterTimelinePojo obj = new TwitterTimelinePojo(time, nTweets, nRTs, lowerBound, upperBound);
				timelineChartObjs.add(obj);
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return timelineChartObjs;

	}

}
