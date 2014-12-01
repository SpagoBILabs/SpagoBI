/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.exceptions.DaoServiceException;
import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterSentimentDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterSentimentDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();
	private final ITwitterCache twitterCache = new TwitterCacheImpl();

	private String positivePercentage = "";
	private String neutralPercentage = "";
	private String negativePercentage = "";

	private String positiveNumber = "";
	private String neutralNumber = "";
	private String negativeNumber = "";

	private JSONArray positiveBC = new JSONArray();
	private JSONArray neutralBC = new JSONArray();
	private JSONArray negativeBC = new JSONArray();

	private JSONArray positiveRadar = new JSONArray();
	private JSONArray neutralRadar = new JSONArray();
	private JSONArray negativeRadar = new JSONArray();

	private boolean rAnalysis = false;

	public TwitterSentimentDataProcessor() throws DaoServiceException {

	}

	public void initializeTwitterSentimentDataProcessor(String searchID) {

		logger.debug("Method initializeTwitterSentimentDataProcessor(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			TwitterSearch twitterSearch = twitterCache.findTwitterSearch(searchId);

			if (twitterSearch != null && twitterSearch.isrAnalysis()) {

				this.rAnalysis = true;

				Map<String, Integer> topicsList = new HashMap<String, Integer>();

				double positivePercentageDouble = 0;
				double neutralPercentageDouble = 0;
				double negativePercentageDouble = 0;

				int positiveNumberInt = 0;
				int neutralNumberInt = 0;
				int negativeNumberInt = 0;

				int positivesTopics = 0;
				int neutralsTopics = 0;
				int negativesTopics = 0;

				Map<String, Integer> positiveMap = new HashMap<String, Integer>();
				Map<String, Integer> neutralMap = new HashMap<String, Integer>();
				Map<String, Integer> negativeMap = new HashMap<String, Integer>();

				List<TwitterData> tweetsSentiment = dpCache.getSentimentSmilesTweets(searchId);

				if (tweetsSentiment != null && tweetsSentiment.size() > 0) {

					for (TwitterData tweet : tweetsSentiment) {
						if (tweet.isPositive()) {

							positiveNumberInt++;

							if (tweet.getTopics() != null && !tweet.getTopics().trim().equals("")) {

								manageSentimentTopics(tweet.getTopics(), positiveMap, topicsList);
								positivesTopics++;

							}

						} else if (tweet.isNeutral()) {

							neutralNumberInt++;

							if (tweet.getTopics() != null && !tweet.getTopics().trim().equals("")) {

								manageSentimentTopics(tweet.getTopics(), neutralMap, topicsList);
								neutralsTopics++;

							}

						} else if (tweet.isNegative()) {

							negativeNumberInt++;

							if (tweet.getTopics() != null && !tweet.getTopics().trim().equals("")) {

								manageSentimentTopics(tweet.getTopics(), negativeMap, topicsList);
								negativesTopics++;
							}
						}
					}
				}

				logger.debug("Method initializeTwitterSentimentDataProcessor(): Calculating results..");

				int totalTweets = positiveNumberInt + neutralNumberInt + negativeNumberInt;

				DecimalFormat df = new DecimalFormat("#.#");

				if (totalTweets > 0) {

					positivePercentageDouble = ((double) positiveNumberInt * 100) / totalTweets;

					neutralPercentageDouble = ((double) neutralNumberInt * 100) / totalTweets;

					negativePercentageDouble = ((double) negativeNumberInt * 100) / totalTweets;

				}

				this.positiveNumber = String.format("%,d", positiveNumberInt);
				this.neutralNumber = String.format("%,d", neutralNumberInt);
				this.negativeNumber = String.format("%,d", negativeNumberInt);

				String formattedPositivePerc = df.format(positivePercentageDouble);
				String formattedNeutralPerc = df.format(neutralPercentageDouble);
				String formattedNegativePerc = df.format(negativePercentageDouble);

				if (positivePercentageDouble % 1 == 0) {
					this.positivePercentage = String.valueOf((int) positivePercentageDouble) + "%";
				} else {

					// corrections %
					double correctionsPerc = 100 - AnalysisUtility.parseDoubleUtil(formattedPositivePerc)
							- AnalysisUtility.parseDoubleUtil(formattedNeutralPerc) - AnalysisUtility.parseDoubleUtil(formattedNegativePerc);

					// logger.debug(AnalysisUtility.parseDoubleUtil(formattedPositivePerc));
					// logger.debug(AnalysisUtility.parseDoubleUtil(formattedNeutralPerc));
					// logger.debug(AnalysisUtility.parseDoubleUtil(formattedNegativePerc));

					positivePercentageDouble = AnalysisUtility.parseDoubleUtil(formattedPositivePerc) + correctionsPerc;

					this.positivePercentage = String.valueOf(df.format(positivePercentageDouble)) + "%";
				}

				if (neutralPercentageDouble % 1 == 0) {
					this.neutralPercentage = String.valueOf((int) neutralPercentageDouble) + "%";
				} else {
					this.neutralPercentage = formattedNeutralPerc + "%";
				}

				if (negativePercentageDouble % 1 == 0) {
					this.negativePercentage = String.valueOf((int) negativePercentageDouble) + "%";
				} else {
					this.negativePercentage = formattedNegativePerc + "%";
				}

				Map<String, Integer> positiveOrdMap = AnalysisUtility.sortByValue(positiveMap);
				Map<String, Integer> neutralOrdMap = AnalysisUtility.sortByValue(neutralMap);
				Map<String, Integer> negativeOrdMap = AnalysisUtility.sortByValue(negativeMap);

				this.positiveBC = this.sentimentMapIntoJSON(positiveOrdMap);
				this.neutralBC = this.sentimentMapIntoJSON(neutralOrdMap);
				this.negativeBC = this.sentimentMapIntoJSON(negativeOrdMap);

				this.positiveRadar = this.sentimentRadarJSON(positiveOrdMap, positivesTopics, topicsList);
				this.neutralRadar = this.sentimentRadarJSON(neutralMap, neutralsTopics, topicsList);
				this.negativeRadar = this.sentimentRadarJSON(negativeOrdMap, negativesTopics, topicsList);

				long endMills = System.currentTimeMillis() - initMills;

				logger.debug("Method initializeTwitterSentimentDataProcessor(): End for search = " + searchId + " in " + endMills + "ms");
			} else {
				this.rAnalysis = false;
				logger.debug("Method initializeTwitterSentimentDataProcessor(): R analysis disabled for search [ " + searchID + " ]");
			}
		} catch (Throwable t) {

			logger.error("Method initializeTwitterSentimentDataProcessor(): Error in sentiment analysis for searchID = " + searchID, t);
			throw new SpagoBIRuntimeException("Method initializeTwitterSentimentDataProcessor(): An error occurred for search ID: " + searchID, t);
		}
	}

	private void manageSentimentTopics(String topicsFromDb, Map<String, Integer> sentimentMap, Map<String, Integer> allTopics) {

		topicsFromDb = topicsFromDb.toLowerCase();
		String[] topicsSplitted = topicsFromDb.split(";");

		for (int i = 0; i < topicsSplitted.length; i++) {
			String word = topicsSplitted[i];
			String topicWords = word.substring(word.lastIndexOf(":") + 1).trim();

			String[] topicWordsArray = topicWords.split(",");

			for (int j = 0; j < topicWordsArray.length; j++) {
				String key = topicWordsArray[j];
				if (sentimentMap.containsKey(key)) {
					int value = sentimentMap.get(key);
					value++;
					sentimentMap.put(key, value);
				} else {
					sentimentMap.put(key, 1);
				}

				if (allTopics.containsKey(key)) {
					int globalValue = allTopics.get(key);
					globalValue++;
					allTopics.put(key, globalValue);
				} else {
					allTopics.put(key, 1);
				}

			}

		}
	}

	private JSONArray sentimentMapIntoJSON(Map<String, Integer> sentimentMap) {
		JSONArray jsonArr = new JSONArray();

		for (Map.Entry<String, Integer> entry : sentimentMap.entrySet()) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("name", entry.getKey());
				obj.put("value", entry.getValue());
				jsonArr.put(obj);

			} catch (JSONException e) {
				logger.error(e);
			}
		}

		return jsonArr;
	}

	private JSONArray sentimentRadarJSON(Map<String, Integer> sentimentMap, int totalTypeTopics, Map<String, Integer> allTopics) throws ParseException {

		JSONArray jsonArr = new JSONArray();

		try {

			if (allTopics != null && allTopics.size() > 0) {

				for (Map.Entry<String, Integer> entry : allTopics.entrySet()) {

					JSONObject obj = new JSONObject();
					obj.put("axis", entry.getKey());

					String key = entry.getKey();

					int globalValueForTopic = entry.getValue();

					if (sentimentMap.containsKey(key)) {
						int relativeValueForTopic = sentimentMap.get(key);
						if (globalValueForTopic > 0) {
							double perc = (double) relativeValueForTopic / (double) globalValueForTopic;
							DecimalFormat df = new DecimalFormat("#.##");
							String formattedPerc = df.format(perc);
							double formattedDouble = AnalysisUtility.parseDoubleUtil(formattedPerc);
							obj.put("value", formattedDouble);
						}
					} else {
						obj.put("value", 0);
					}

					jsonArr.put(obj);
				}
			}
		} catch (JSONException e) {
			logger.error(e);
		}

		return jsonArr;
	}

	public String getPositivePercentage() {
		return positivePercentage;
	}

	public String getNeutralPercentage() {
		return neutralPercentage;
	}

	public String getNegativePercentage() {
		return negativePercentage;
	}

	public String getPositiveNumber() {
		return positiveNumber;
	}

	public String getNeutralNumber() {
		return neutralNumber;
	}

	public String getNegativeNumber() {
		return negativeNumber;
	}

	public JSONArray getPositiveBC() {
		return positiveBC;
	}

	public JSONArray getNeutralBC() {
		return neutralBC;
	}

	public JSONArray getNegativeBC() {
		return negativeBC;
	}

	public JSONArray getPositiveRadar() {
		return positiveRadar;
	}

	public JSONArray getNeutralRadar() {
		return neutralRadar;
	}

	public JSONArray getNegativeRadar() {
		return negativeRadar;
	}

	public boolean isrAnalysis() {
		return rAnalysis;
	}

	public void setrAnalysis(boolean rAnalysis) {
		this.rAnalysis = rAnalysis;
	}

}
