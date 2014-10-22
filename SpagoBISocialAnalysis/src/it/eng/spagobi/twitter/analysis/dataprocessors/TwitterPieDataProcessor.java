/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.pojos.TwitterPiePojo;
import it.eng.spagobi.twitter.analysis.pojos.TwitterPieSourcePojo;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterPieDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterPieDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private TwitterPiePojo tweetsPieChart;
	private List<TwitterPieSourcePojo> tweetsPieSourceChart;

	public TwitterPieDataProcessor() {

	}

	/**
	 * Initialize pie charts
	 *
	 * @param searchID
	 */
	public void initializeTwitterPieCharts(String searchID) {

		logger.debug("Method initializeTwitterPieCharts(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		// check if searchID is a long and convert it
		long searchId = AnalysisUtility.isLong(searchID);

		this.tweetsPieChart = this.createTweetsPieChart(searchId);
		this.tweetsPieSourceChart = this.createTweetsPieSourceChart(searchId);

		long endMills = System.currentTimeMillis() - initMills;

		logger.debug("Method initializeTwitterPieCharts(): End for search = " + searchId + " in " + endMills + "ms");
	}

	/**
	 * This method creates the tweets type pie chart object for summary.jsp
	 *
	 * @param searchID
	 * @return
	 */
	private TwitterPiePojo createTweetsPieChart(long searchId) {

		logger.debug("Method createTweetsPieChart(): Start");

		try {

			int totalTweets = dpCache.getTotalTweets(searchId);
			int totalReplies = dpCache.getTotalReplies(searchId);
			int totalRTs = dpCache.getTotalRTs(searchId);

			int originalTweets = totalTweets - totalRTs - totalReplies;

			TwitterPiePojo statsObj = new TwitterPiePojo(originalTweets, totalReplies, totalRTs);

			logger.debug("Method createTweetsPieChart(): End");
			return statsObj;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method createTweetsPieChart(): An error occurred for search ID: " + searchId, t);
		}

	}

	/**
	 * This method creates the sources pie chart for summary.jsp
	 *
	 * @param searchID
	 * @return
	 */
	private List<TwitterPieSourcePojo> createTweetsPieSourceChart(long searchId) {

		logger.debug("Method createTweetsPieSourceChart(): Start");

		try {

			Map<String, Integer> sourceMap = new HashMap<String, Integer>();
			List<TwitterPieSourcePojo> sources = new ArrayList<TwitterPieSourcePojo>();

			List<String> tweetsSources = dpCache.getSources(searchId);

			for (String sourceClient : tweetsSources) {
				String formattedSource = tweetSourceFormatter(sourceClient);

				if (sourceMap.containsKey(formattedSource)) {

					int value = sourceMap.get(formattedSource);
					value++;
					sourceMap.put(formattedSource, value);

				} else {

					sourceMap.put(formattedSource, 1);
				}
			}

			for (Map.Entry<String, Integer> entry : sourceMap.entrySet()) {

				String source = entry.getKey();
				int value = entry.getValue();

				TwitterPieSourcePojo obj = new TwitterPieSourcePojo(source, value);
				sources.add(obj);
			}

			logger.debug("Method createTweetsPieSourceChart(): End");
			return sources;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method createTweetsPieSourceChart(): An error occurred for search ID: " + searchId, t);
		}

	}

	/**
	 * This method extracts the source from general html source String
	 *
	 * @param source
	 * @return
	 */
	private String tweetSourceFormatter(String source) {

		String formattedHTMLSource = source.replaceAll("<.*?>", "");
		String formattedTextSourceFirst = formattedHTMLSource.replaceAll("Twitter ", "");
		String formattedTextSourceSecond = formattedTextSourceFirst.replaceAll("for ", "");

		return formattedTextSourceSecond;

	}

	public TwitterPiePojo getTweetsPieChart() {
		return tweetsPieChart;
	}

	public List<TwitterPieSourcePojo> getTweetsPieSourceChart() {
		return tweetsPieSourceChart;
	}

}
