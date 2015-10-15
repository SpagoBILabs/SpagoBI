/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.entities.TwitterLinkToMonitor;
import it.eng.spagobi.twitter.analysis.enums.MonitorRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

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

public class TwitterResourcesTimelineDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterResourcesTimelineDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private final long CONST_TIMEZONE = 60 * 60 * 2000;

	// private java.sql.Timestamp lowerBound = new java.sql.Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
	// private java.sql.Timestamp upperBound = new java.sql.Timestamp(GregorianCalendar.getInstance().getTimeInMillis());

	String hourData = "";
	String dayData = "";
	String weekData = "";
	String monthData = "";

	String hourDataOverview = "";
	String dayDataOverview = "";
	String weekDataOverview = "";
	String monthDataOverview = "";

	String weekTicks = "";

	public TwitterResourcesTimelineDataProcessor() {

	}

	/**
	 * initialize link timeline
	 *
	 * @param searchID
	 */
	public void initializeTwitterResourcesTimelineDataProcessor(String searchID) {

		logger.debug("Method initializeTwitterResourcesTimelineDataProcessor(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			JSONArray hourJSONArray = this.getClicks(searchId, "hours", false);
			JSONArray dayJSONArray = this.getClicks(searchId, "days", false);
			JSONArray weekJSONArray = this.getClicks(searchId, "weeks", false);
			JSONArray monthJSONArray = this.getClicks(searchId, "months", false);

			JSONArray hourJSONArrayOverview = this.getClicks(searchId, "hours", true);
			JSONArray dayJSONArrayOverview = this.getClicks(searchId, "days", true);
			JSONArray weekJSONArrayOverview = this.getClicks(searchId, "weeks", true);
			JSONArray monthJSONArrayOverview = this.getClicks(searchId, "months", true);

			this.hourData = hourJSONArray.toString();
			this.dayData = dayJSONArray.toString();
			this.weekData = weekJSONArray.toString();
			this.monthData = monthJSONArray.toString();

			this.hourDataOverview = hourJSONArrayOverview.toString();
			this.dayDataOverview = dayJSONArrayOverview.toString();
			this.weekDataOverview = weekJSONArrayOverview.toString();
			this.monthDataOverview = monthJSONArrayOverview.toString();

			this.weekTicks = this.createWeekTicks(weekJSONArray).toString();

			logger.debug("Method initializeTwitterResourcesTimelineDataProcessor(): End");
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method initializeTwitterResourcesTimelineDataProcessor(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method retrieves max time for resources
	 *
	 * @param searchID
	 * @param tableName
	 * @return
	 */
	private Calendar getMaxTimeResourceToMonitor(long searchID, String tableName) {

		logger.debug("Method getMaxTimeResourceToMonitor(): Start");

		try {
			Calendar maxTime = GregorianCalendar.getInstance();

			Calendar maxTimeFromDB = dpCache.getMaxLinksTime(searchID);

			if (maxTimeFromDB != null) {

				maxTime.setTime(maxTimeFromDB.getTime());

			}

			// maxTime.set(Calendar.HOUR_OF_DAY, 0);
			maxTime.set(Calendar.MINUTE, 0);
			maxTime.set(Calendar.SECOND, 0);
			maxTime.set(Calendar.MILLISECOND, 0);

			logger.debug("Method getMaxTimeResourceToMonitor(): End");
			return maxTime;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getMaxTimeResourceToMonitor(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * This method retrieves min time for resources
	 *
	 * @param searchID
	 * @param tableName
	 * @return
	 */
	private Calendar getMinTimeResourceToMonitor(long searchID, String tableName) {

		try {
			logger.debug("Method getMinTimeResourceToMonitor(): Start");

			Calendar minTime = GregorianCalendar.getInstance();

			Calendar minTimeFromDB = dpCache.getMinLinksTime(searchID);

			if (minTimeFromDB != null) {

				minTime.setTime(minTimeFromDB.getTime());

			}

			// minTime.set(Calendar.HOUR_OF_DAY, 0);
			minTime.set(Calendar.MINUTE, 0);
			minTime.set(Calendar.SECOND, 0);
			minTime.set(Calendar.MILLISECOND, 0);

			logger.debug("Method getMinTimeResourceToMonitor(): End");
			return minTime;

		}

		catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getMinTimeResourceToMonitor(): An error occurred for search ID: " + searchID, t);
		}
	}

	private JSONArray getClicks(long searchID, String timeFilter, boolean isOverview) {

		try {
			logger.debug("Method getClicks(): Start");

			Calendar minTime = getMinTimeResourceToMonitor(searchID, "twitter_links_to_monitor");
			Calendar maxTime = getMaxTimeResourceToMonitor(searchID, "twitter_links_to_monitor");

			minTime = roundTime(timeFilter, minTime);
			maxTime = roundTime(timeFilter, maxTime);

			JSONArray results = new JSONArray();

			List<String> links = dpCache.getLinks(searchID);

			for (String link : links) {

				JSONObject element = new JSONObject();

				Calendar min = GregorianCalendar.getInstance();
				Calendar max = GregorianCalendar.getInstance();

				min.setTimeInMillis(minTime.getTimeInMillis());
				max.setTimeInMillis(maxTime.getTimeInMillis());

				LinkedHashMap<Long, Integer> linkClicks = initializeTimeline(min, max, timeFilter);

				List<TwitterLinkToMonitor> linksInfo = dpCache.getLinksToMonitorInfo(searchID, link);

				if (!isOverview) {
					if (linksInfo != null && linksInfo.size() > 0) {

						if (linksInfo.get(0).getLongUrl() != null && !linksInfo.get(0).getLongUrl().equals("")) {

							element.put("label", linksInfo.get(0).getLongUrl());

						} else {

							element.put("label", link);
						}
					}
				}

				for (TwitterLinkToMonitor lInfo : linksInfo) {

					JSONArray data = new JSONArray();

					int clicksCount = lInfo.getClicksCount();

					Calendar timestampCalendar = lInfo.getTimestamp();

					Calendar roundedTime = roundTime(timeFilter, timestampCalendar);

					long time = roundedTime.getTimeInMillis();

					if (linkClicks.containsKey(time)) {

						int value = linkClicks.get(time);

						if (value == -1) {
							// defaultvalue, first add to this key
							value = clicksCount;
						} else {
							// value already modified with real values
							// value = value + clicksCount;
							value = clicksCount;
						}

						linkClicks.put(time, value);
					}

					if (linkClicks.size() == 1) {

						// manage the situation of only one result
						// useless graph

						Calendar newMinTime = GregorianCalendar.getInstance();
						newMinTime.setTimeInMillis(minTime.getTimeInMillis());

						// actual min followers value
						int lowerClicks = linkClicks.get(newMinTime.getTimeInMillis());

						if (timeFilter.equalsIgnoreCase("hours")) {

							newMinTime.add(Calendar.HOUR_OF_DAY, -1);

						} else if (timeFilter.equalsIgnoreCase("days")) {

							newMinTime.add(Calendar.DAY_OF_MONTH, -1);

						} else if (timeFilter.equalsIgnoreCase("weeks")) {

							newMinTime.add(Calendar.WEEK_OF_YEAR, -1);
						}

						else if (timeFilter.equalsIgnoreCase("months")) {

							newMinTime.add(Calendar.MONTH, -1);
						}

						long newLowerMills = newMinTime.getTimeInMillis();
						linkClicks.put(newLowerMills, lowerClicks);

					}

					int coverValue = 0;

					for (Map.Entry<Long, Integer> entry : linkClicks.entrySet()) {

						JSONArray dataElement = new JSONArray();

						long timeMills = entry.getKey();
						int clicks = entry.getValue();

						if (clicks == -1) {
							// no values for this time, put cover values
							// until next monitored value
							clicks = coverValue;
						}

						// mantain the real value to cover next deault
						// values
						coverValue = clicks;

						dataElement.put(timeMills);
						dataElement.put(clicks);

						data.put(dataElement);
					}

					element.put("data", data);
				}

				results.put(element);
			}

			logger.debug("JSON CLICKS: _-> " + results);

			logger.debug("Method getClicks(): End");
			return results;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getClicks(): An error occurred for search ID: " + searchID, t);
		}

	}

	public String getVisualizationType(long searchID) {

		try {

			logger.debug("Method getVisualizationType(): Start");

			MonitorRepeatTypeEnum repeatType = dpCache.getMonitorRepeationType(searchID);

			// Assert.assertNotNull(repeatType, "Repetition type not present. It should be Hour or Day");

			if (repeatType != null) {
				String type = repeatType.toString().toLowerCase();

				logger.debug("Method getVisualizationType(): End");
				return type;

			} else {

				logger.debug("Method getVisualizationType(): End");
				return "";
			}
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  getVisualizationType(): An error occurred for search ID: " + searchID, t);
		}

	}

	private LinkedHashMap<Long, Integer> initializeTimeline(Calendar minTime, Calendar maxTime, String filter) {

		LinkedHashMap<Long, Integer> baseMap = new LinkedHashMap<Long, Integer>();

		while (minTime.compareTo(maxTime) <= 0) {

			Calendar utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
			utcCalendar.setTime(minTime.getTime());

			// TODO: check the behaviour for others time zone != GMT
			// manage daylight auto add/sub 1 hour
			if (!filter.equalsIgnoreCase("hours")) {

				if (utcCalendar.get(Calendar.HOUR_OF_DAY) == 23) {
					utcCalendar.add(Calendar.HOUR_OF_DAY, 1);
				} else if (utcCalendar.get(Calendar.HOUR_OF_DAY) == 1) {
					utcCalendar.add(Calendar.HOUR_OF_DAY, -1);
				}
			}

			baseMap.put(utcCalendar.getTimeInMillis(), -1);

			if (filter.equalsIgnoreCase("hours")) {

				minTime.add(Calendar.HOUR_OF_DAY, 1);

			} else if (filter.equalsIgnoreCase("days")) {

				minTime.add(Calendar.DAY_OF_MONTH, 1);

			} else if (filter.equalsIgnoreCase("weeks")) {

				minTime.add(Calendar.WEEK_OF_YEAR, 1);
			}

			else if (filter.equalsIgnoreCase("months")) {

				minTime.add(Calendar.MONTH, 1);
			}

		}

		return baseMap;
	}

	private Calendar roundTime(String filter, Calendar tempTime) {
		if (tempTime != null) {
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

			} else if (filter.equalsIgnoreCase("weeks")) {

				// round for weeks
				tempTime.set(Calendar.HOUR_OF_DAY, 0);
				tempTime.set(Calendar.MINUTE, 0);
				tempTime.set(Calendar.SECOND, 0);
				tempTime.set(Calendar.MILLISECOND, 0);
				tempTime.set(Calendar.DAY_OF_WEEK, tempTime.getFirstDayOfWeek());

			}

			else if (filter.equalsIgnoreCase("months")) {

				// round for weeks
				tempTime.set(Calendar.HOUR_OF_DAY, 0);
				tempTime.set(Calendar.MINUTE, 0);
				tempTime.set(Calendar.SECOND, 0);
				tempTime.set(Calendar.MILLISECOND, 0);
				tempTime.set(Calendar.DAY_OF_MONTH, 1);

			}

			TimeZone timeZone = TimeZone.getDefault();
			int offset = timeZone.getRawOffset();

			if (timeZone.inDaylightTime(tempTime.getTime())) {
				offset = offset + timeZone.getDSTSavings();
			}

			long roundTimeWithOffset = tempTime.getTimeInMillis() + offset;
			tempTime.setTimeInMillis(roundTimeWithOffset);

			return tempTime;
		} else {
			return GregorianCalendar.getInstance();
		}
	}

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

	public String hideHours(String searchID) {

		long searchId = AnalysisUtility.isLong(searchID);

		String monitoringType = new TwitterResourcesTimelineDataProcessor().getVisualizationType(searchId);

		if (monitoringType.equalsIgnoreCase("Hour")) {
			return "";
		} else {
			return "display:none;";
		}

	}

	public String getHourData() {
		return hourData;
	}

	public void setHourData(String hourData) {
		this.hourData = hourData;
	}

	public String getDayData() {
		return dayData;
	}

	public void setDayData(String dayData) {
		this.dayData = dayData;
	}

	public String getWeekData() {
		return weekData;
	}

	public void setWeekData(String weekData) {
		this.weekData = weekData;
	}

	public String getMonthData() {
		return monthData;
	}

	public void setMonthData(String monthData) {
		this.monthData = monthData;
	}

	public String getHourDataOverview() {
		return hourDataOverview;
	}

	public void setHourDataOverview(String hourDataOverview) {
		this.hourDataOverview = hourDataOverview;
	}

	public String getDayDataOverview() {
		return dayDataOverview;
	}

	public void setDayDataOverview(String dayDataOverview) {
		this.dayDataOverview = dayDataOverview;
	}

	public String getWeekDataOverview() {
		return weekDataOverview;
	}

	public void setWeekDataOverview(String weekDataOverview) {
		this.weekDataOverview = weekDataOverview;
	}

	public String getMonthDataOverview() {
		return monthDataOverview;
	}

	public void setMonthDataOverview(String monthDataOverview) {
		this.monthDataOverview = monthDataOverview;
	}

	public String getWeekTicks() {
		return weekTicks;
	}

	public void setWeekTicks(String weekTicks) {
		this.weekTicks = weekTicks;
	}

}
