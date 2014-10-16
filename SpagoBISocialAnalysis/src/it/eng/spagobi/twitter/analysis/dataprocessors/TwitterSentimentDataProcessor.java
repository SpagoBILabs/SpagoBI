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

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterSentimentDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterSentimentDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	private String positivePercentage = "";
	private String neutralPercentage = "";
	private String negativePercentage = "";

	private String positiveNumber = "";
	private String neutralNumber = "";
	private String negativeNumber = "";

	public TwitterSentimentDataProcessor() {

	}

	public void initializeTwitterSentimentDataProcessor(String searchID) {

		logger.debug("Method initializeTwitterSentimentDataProcessor(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			double positivePercentageDouble = 0;
			double neutralPercentageDouble = 0;
			double negativePercentageDouble = 0;

			int positiveNumberInt = 0;
			int neutralNumberInt = 0;
			int negativeNumberInt = 0;

			List<TwitterData> tweetsSentiment = dpCache.getSentimentSmilesTweets(searchId);

			if (tweetsSentiment != null && tweetsSentiment.size() > 0) {

				for (TwitterData tweet : tweetsSentiment) {
					if (tweet.isPositive()) {
						positiveNumberInt++;
					} else if (tweet.isNeutral()) {
						neutralNumberInt++;
					} else if (tweet.isNegative()) {
						negativeNumberInt++;
					}
				}
			}

			int totalTweets = positiveNumberInt + neutralNumberInt + negativeNumberInt;

			if (totalTweets > 0) {

				positivePercentageDouble = (positiveNumberInt * 100) / totalTweets;

				positivePercentageDouble = (positiveNumberInt * 100) / totalTweets;

				positivePercentageDouble = (positiveNumberInt * 100) / totalTweets;
			}

			this.positiveNumber = String.format("%,d", positiveNumberInt);
			this.neutralNumber = String.format("%,d", neutralNumberInt);
			this.negativeNumber = String.format("%,d", negativeNumberInt);

			if ((positivePercentageDouble % 1) == 0) {
				this.positivePercentage = String.valueOf((int) positivePercentageDouble) + "%";
			} else {
				this.positivePercentage = String.valueOf(positivePercentageDouble) + "%";
			}

			if ((neutralPercentageDouble % 1) == 0) {
				this.neutralPercentage = String.valueOf((int) neutralPercentageDouble) + "%";
			} else {
				this.neutralPercentage = String.valueOf(neutralPercentageDouble) + "%";
			}

			if ((negativePercentageDouble % 1) == 0) {
				this.negativePercentage = String.valueOf((int) negativePercentageDouble) + "%";
			} else {
				this.negativePercentage = String.valueOf(negativePercentageDouble) + "%";
			}

			logger.debug("Method initializeTwitterSentimentDataProcessor(): End");
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method  initializeTwitterSentimentDataProcessor(): An error occurred for search ID: " + searchID, t);
		}
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

}
