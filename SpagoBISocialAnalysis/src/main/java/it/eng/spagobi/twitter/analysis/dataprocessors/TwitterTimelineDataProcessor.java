/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterTimelineDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterTimelineDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private final long CONST_TIMEZONE = 60 * 60 * 2000;

	private java.sql.Timestamp lowerBound = new java.sql.Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
	private java.sql.Timestamp upperBound = new java.sql.Timestamp(GregorianCalendar.getInstance().getTimeInMillis());

	String hourData = "";
	String dayData = "";
	String weekData = "";
	String monthData = "";

	String hourDataOverview = "";
	String dayDataOverview = "";
	String weekDataOverview = "";
	String monthDataOverview = "";

	String weekTicks = "";

	public TwitterTimelineDataProcessor() {

	}

	/**
	 * This method initializes the structures for a timeline
	 *
	 * @param searchIDStr
	 * @return
	 */
	public void initializeTwitterTimelineDataProcessor(String searchID) {

		logger.debug("Method initializeTwitterTimelineDataProcessor(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			LinkedHashMap<Long, Integer> tweetsHourMap = new LinkedHashMap<Long, Integer>();
			LinkedHashMap<Long, Integer> rtsHourMap = new LinkedHashMap<Long, Integer>();

			LinkedHashMap<Long, Integer> tweetsDayMap = new LinkedHashMap<Long, Integer>();
			LinkedHashMap<Long, Integer> rtsDayMap = new LinkedHashMap<Long, Integer>();

			LinkedHashMap<Long, Integer> tweetsWeekMap = new LinkedHashMap<Long, Integer>();
			LinkedHashMap<Long, Integer> rtsWeekMap = new LinkedHashMap<Long, Integer>();

			LinkedHashMap<Long, Integer> tweetsMonthMap = new LinkedHashMap<Long, Integer>();
			LinkedHashMap<Long, Integer> rtsMonthMap = new LinkedHashMap<Long, Integer>();

			// set lower and upper bound useful for initialize structures
			this.setLowerBound(searchId);
			this.setUpperBound(searchId);

			this.initializeTimelines(tweetsHourMap, rtsHourMap, "hours");
			this.initializeTimelines(tweetsDayMap, rtsDayMap, "days");
			this.initializeTimelines(tweetsWeekMap, rtsWeekMap, "weeks");
			this.initializeTimelines(tweetsMonthMap, rtsMonthMap, "months");

			List<TwitterData> twitterDatas = dpCache.getTimelineTweets(searchId);

			for (TwitterData twitterData : twitterDatas) {

				boolean isRetweet = twitterData.isRetweet();
				java.sql.Timestamp timeFromDB = new java.sql.Timestamp(twitterData.getTimeCreatedAt().getTimeInMillis());

				long hourRoundedTime = roundTime("hours", timeFromDB);
				long dayRoundedTime = roundTime("days", timeFromDB);
				long weekRoundedTime = roundTime("weeks", timeFromDB);
				long monthRoundedTime = roundTime("months", timeFromDB);

				this.updateTimelineCounters(tweetsHourMap, rtsHourMap, hourRoundedTime, isRetweet);
				this.updateTimelineCounters(tweetsDayMap, rtsDayMap, dayRoundedTime, isRetweet);
				this.updateTimelineCounters(tweetsWeekMap, rtsWeekMap, weekRoundedTime, isRetweet);
				this.updateTimelineCounters(tweetsMonthMap, rtsMonthMap, monthRoundedTime, isRetweet);
			}

			JSONArray hourJSONArray = this.convertLinkedHashMapsIntoString(tweetsHourMap, rtsHourMap);
			JSONArray dayJSONArray = this.convertLinkedHashMapsIntoString(tweetsDayMap, rtsDayMap);
			JSONArray weekJSONArray = this.convertLinkedHashMapsIntoString(tweetsWeekMap, rtsWeekMap);
			JSONArray monthJSONArray = this.convertLinkedHashMapsIntoString(tweetsMonthMap, rtsMonthMap);

			this.hourData = hourJSONArray.toString();
			this.dayData = dayJSONArray.toString();
			this.weekData = weekJSONArray.toString();
			this.monthData = monthJSONArray.toString();

			this.hourDataOverview = this.getDataOverviewFromFullData(hourJSONArray).toString();
			this.dayDataOverview = this.getDataOverviewFromFullData(dayJSONArray).toString();
			this.weekDataOverview = this.getDataOverviewFromFullData(weekJSONArray).toString();
			this.monthDataOverview = this.getDataOverviewFromFullData(monthJSONArray).toString();

			this.weekTicks = this.createWeekTicks(weekJSONArray).toString();

			long endMills = System.currentTimeMillis() - initMills;

			logger.debug("Method initializeTwitterTimelineDataProcessor(): End for search = " + searchId + " in " + endMills + "ms");

		}

		catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  initializeTwitterTimelineDataProcessor(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method get min tweet time for a search
	 *
	 * @param searchID
	 */
	private void setLowerBound(long searchID) {

		try {
			logger.debug("Method  setLowerBound(): Start");

			Calendar minCalendar = dpCache.getMinTweetTime(searchID);

			if (minCalendar != null) {

				Timestamp timeFromDB = new Timestamp(minCalendar.getTimeInMillis());
				this.lowerBound = timeFromDB;

			}

			logger.debug("Method  setLowerBound(): End");
		}

		catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  setLowerBound(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method get max tweet time for a search
	 *
	 * @param searchID
	 */
	private void setUpperBound(long searchID) {

		try {

			logger.debug("Method  setUpperBound(): Start");

			Calendar maxCalendar = dpCache.getMaxTweetTime(searchID);

			if (maxCalendar != null) {

				Timestamp timeFromDB = new Timestamp(maxCalendar.getTimeInMillis());
				this.upperBound = timeFromDB;

			}

			logger.debug("Method  setUpperBound(): End");
		}

		catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  setUpperBound(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method completes the timelines adding 0 values
	 *
	 * @param tweetTimelineMap
	 * @param rtTimelineMap
	 * @param filter
	 */
	private void initializeTimelines(LinkedHashMap<Long, Integer> tweetTimelineMap, LinkedHashMap<Long, Integer> rtTimelineMap, String filter) {

		Calendar lowerCalendar = GregorianCalendar.getInstance();
		Calendar upperCalendar = GregorianCalendar.getInstance();

		long roundedUpperBound;

		long roundedLowerBound = roundTime(filter, this.lowerBound);
		if (!filter.equalsIgnoreCase("months") && !filter.equalsIgnoreCase("weeks")) {
			roundedUpperBound = roundTime(filter, this.upperBound);
		} else {
			roundedUpperBound = this.upperBound.getTime();
		}

		lowerCalendar.setTimeInMillis(roundedLowerBound);
		upperCalendar.setTimeInMillis(roundedUpperBound);

		if (filter.equalsIgnoreCase("hours")) {

			lowerCalendar.add(Calendar.HOUR_OF_DAY, -1);
			upperCalendar.add(Calendar.HOUR_OF_DAY, 1);

		} else if (filter.equalsIgnoreCase("days")) {

			lowerCalendar.add(Calendar.DAY_OF_MONTH, -1);
			upperCalendar.add(Calendar.DAY_OF_MONTH, 1);

		} else if (filter.equalsIgnoreCase("weeks")) {

			lowerCalendar.add(Calendar.WEEK_OF_YEAR, -1);
			upperCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		}

		else if (filter.equalsIgnoreCase("months")) {

			lowerCalendar.add(Calendar.MONTH, -1);
			upperCalendar.add(Calendar.MONTH, 1);
		}

		while (lowerCalendar.compareTo(upperCalendar) <= 0) {

			long tempMinMills = lowerCalendar.getTimeInMillis();

			Calendar utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
			utcCalendar.setTimeInMillis(tempMinMills);

			// TODO: check the behaviour for others time zone != GMT
			// manage daylight auto add/sub 1 hour
			if (!filter.equalsIgnoreCase("hours")) {

				if (utcCalendar.get(Calendar.HOUR_OF_DAY) == 23) {
					utcCalendar.add(Calendar.HOUR_OF_DAY, 1);
				} else if (utcCalendar.get(Calendar.HOUR_OF_DAY) == 1) {
					utcCalendar.add(Calendar.HOUR_OF_DAY, -1);
				}
			}

			tempMinMills = utcCalendar.getTimeInMillis();

			tweetTimelineMap.put(tempMinMills, 0);
			rtTimelineMap.put(tempMinMills, 0);

			if (filter.equalsIgnoreCase("hours")) {

				lowerCalendar.add(Calendar.HOUR_OF_DAY, 1);

			} else if (filter.equalsIgnoreCase("days")) {

				lowerCalendar.add(Calendar.DAY_OF_MONTH, 1);

			} else if (filter.equalsIgnoreCase("weeks")) {

				lowerCalendar.add(Calendar.WEEK_OF_YEAR, 1);

			}

			else if (filter.equalsIgnoreCase("months")) {

				lowerCalendar.add(Calendar.MONTH, 1);

			}

		}

	}

	/**
	 * This method rounds a timestamp looking at the time filter
	 *
	 * @param filter
	 * @param tempTime
	 * @return
	 */
	private long roundTime(String filter, java.sql.Timestamp sqlTimestamp) {

		Calendar tempTime = GregorianCalendar.getInstance();
		tempTime.setTime(sqlTimestamp);

		if (filter.equalsIgnoreCase("hours")) {

			// round for hours
			tempTime.set(Calendar.MINUTE, 0);
			tempTime.set(Calendar.SECOND, 0);
			tempTime.set(Calendar.MILLISECOND, 0);

		} else if (filter.equalsIgnoreCase("days")) {

			// round for days
			tempTime.set(Calendar.HOUR_OF_DAY, 0);
			tempTime.set(Calendar.MINUTE, 0);
			tempTime.set(Calendar.SECOND, 0);
			tempTime.set(Calendar.MILLISECOND, 0);

			if (TimeZone.getDefault().getOffset(tempTime.getTimeInMillis()) != TimeZone.getDefault().getOffset(sqlTimestamp.getTime())) {
				tempTime.add(Calendar.HOUR_OF_DAY, 1);
			}

		} else if (filter.equalsIgnoreCase("weeks")) {

			// round for weeks
			tempTime.set(Calendar.HOUR_OF_DAY, 0);
			tempTime.set(Calendar.MINUTE, 0);
			tempTime.set(Calendar.SECOND, 0);
			tempTime.set(Calendar.MILLISECOND, 0);
			tempTime.set(Calendar.DAY_OF_WEEK, tempTime.getFirstDayOfWeek());

			if (TimeZone.getDefault().getOffset(tempTime.getTimeInMillis()) != TimeZone.getDefault().getOffset(sqlTimestamp.getTime())) {
				tempTime.add(Calendar.HOUR_OF_DAY, 1);
			}

		}

		else if (filter.equalsIgnoreCase("months")) {

			// round for months
			tempTime.set(Calendar.HOUR_OF_DAY, 0);
			tempTime.set(Calendar.MINUTE, 0);
			tempTime.set(Calendar.SECOND, 0);
			tempTime.set(Calendar.MILLISECOND, 0);
			tempTime.set(Calendar.DAY_OF_MONTH, 1);

			if (TimeZone.getDefault().getOffset(tempTime.getTimeInMillis()) != TimeZone.getDefault().getOffset(sqlTimestamp.getTime())) {
				tempTime.add(Calendar.HOUR_OF_DAY, 1);
			}

		}

		TimeZone timeZone = TimeZone.getDefault();
		int offset = timeZone.getRawOffset();

		if (timeZone.inDaylightTime(sqlTimestamp)) {
			offset = offset + timeZone.getDSTSavings();
		}

		long roundedTime = tempTime.getTimeInMillis() + offset;
		return roundedTime;

		// return tempTime.getTimeInMillis() + (CONST_TIMEZONE);
	}

	/**
	 * This method manages differents tweet and rt maps
	 *
	 * @param tweetTimelineMap
	 * @param rtTimelineMap
	 * @param roundedMills
	 * @param isRetweet
	 */
	private void updateTimelineCounters(LinkedHashMap<Long, Integer> tweetTimelineMap, LinkedHashMap<Long, Integer> rtTimelineMap, long roundedMills,
			boolean isRetweet) {

		if (tweetTimelineMap.containsKey(roundedMills)) {
			int tweets = tweetTimelineMap.get(roundedMills);
			tweets++;
			tweetTimelineMap.put(roundedMills, tweets);

			if (isRetweet) {
				int retweets = rtTimelineMap.get(roundedMills);
				retweets++;
				rtTimelineMap.put(roundedMills, retweets);
			}
		}
	}

	/**
	 * This method prepares data for JSP and creates JSONArray from LinkedHashMaps
	 *
	 * @param tweetTimelineMap
	 * @param rtTimelineMap
	 * @return
	 */
	private JSONArray convertLinkedHashMapsIntoString(LinkedHashMap<Long, Integer> tweetTimelineMap, LinkedHashMap<Long, Integer> rtTimelineMap) {

		// We have to create this structure: [ {data: [ [] [] ], label: "# of tweets"]}, {{data: [ [] [] ], label: "# of RTs"]} ]

		try {
			JSONArray result = new JSONArray();

			JSONArray tweetData = new JSONArray();
			JSONArray RTweetData = new JSONArray();

			for (Map.Entry<Long, Integer> entry : tweetTimelineMap.entrySet()) {

				// TODO: add a check for equal time tweet and rt
				long time = entry.getKey();
				int nTweets = entry.getValue();
				int nRTs = rtTimelineMap.get(time);

				JSONArray tempTElement = new JSONArray();
				JSONArray tempRTElement = new JSONArray();

				tempTElement.put(time);
				tempTElement.put(nTweets);

				tweetData.put(tempTElement);

				tempRTElement.put(time);
				tempRTElement.put(nRTs);

				RTweetData.put(tempRTElement);
			}

			JSONObject tweetJson = new JSONObject();

			tweetJson.put("data", tweetData);
			tweetJson.put("label", "# of tweets");

			JSONObject rtJson = new JSONObject();

			rtJson.put("data", RTweetData);
			rtJson.put("label", "# of RTs");

			result.put(tweetJson);
			result.put(rtJson);

			// logger.debug("Method convertLinkedHashMapsIntoJsonArray(): Resulting JSON array is " + result.toString());

			return result;

		} catch (JSONException e) {
			throw new SpagoBIRuntimeException("Method convertLinkedHashMapsIntoJsonArray(): Impossible to create a correct timeline parsing JSON data ", e);
		}
	}

	private JSONArray getDataOverviewFromFullData(JSONArray fullData) {

		// We have to create this structure: [ {data: [ [] [] ] }, {data: [ [] [] ] } ]

		try {

			JSONArray result = new JSONArray();

			for (int i = 0; i < fullData.length(); i++) {

				JSONObject tempObj = fullData.getJSONObject(i);
				JSONArray tempArr = tempObj.getJSONArray("data");

				result.put(new JSONObject().put("data", tempArr));
			}

			return result;

		} catch (JSONException e) {
			throw new SpagoBIRuntimeException("Method getDataOverviewFromFullData(): Impossible to create a correct overview data parsing JSON data ", e);
		}
	}

	/**
	 * This method helps to create week label
	 *
	 * @return
	 */
	private JSONArray createWeekTicks(JSONArray overviewData) {

		try {

			List<Long> ticks = new ArrayList<Long>();

			if (overviewData.length() > 0) {
				JSONObject data = overviewData.getJSONObject(0);
				JSONArray jArray = data.getJSONArray("data");

				for (int i = 0; i < jArray.length(); i++) {

					JSONArray tempArr = jArray.getJSONArray(i);
					ticks.add(tempArr.getLong(0));
				}
			}

			JSONArray result = new JSONArray();

			for (long mills : ticks) {
				result.put(mills);
			}

			return result;

		} catch (JSONException e) {
			throw new SpagoBIRuntimeException("Method createWeekTicks(): Impossible to create corret ticks parsing JSON data ", e);
		}

	}

	public String getHourData() {
		return hourData;
	}

	public String getDayData() {
		return dayData;
	}

	public String getWeekData() {
		return weekData;
	}

	public String getMonthData() {
		return monthData;
	}

	public String getHourDataOverview() {
		return hourDataOverview;
	}

	public String getDayDataOverview() {
		return dayDataOverview;
	}

	public String getWeekDataOverview() {
		return weekDataOverview;
	}

	public String getMonthDataOverview() {
		return monthDataOverview;
	}

	public String getWeekTicks() {
		return weekTicks;
	}

}
