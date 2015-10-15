/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.entities.TwitterUser;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterInfluencersDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterInfluencersDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	List<TwitterUser> mostInfluencers = new ArrayList<TwitterUser>();

	public TwitterInfluencersDataProcessor() {

	}

	/**
	 * Initialize top influencers
	 *
	 * @param searchID
	 */
	public void initializeTwitterTopInfluencers(String searchID) {

		logger.debug("Method initializeTwitterTopInfluencers(): Start for searchID = " + searchID);

		long initMills = System.currentTimeMillis();

		// check if searchID is a long and convert it
		long searchId = AnalysisUtility.isLong(searchID);

		this.mostInfluencers = this.createMostInfluencers(searchId);

		long endMills = System.currentTimeMillis() - initMills;

		logger.debug("Method initializeTwitterTopInfluencers(): End for search = " + searchId + " in " + endMills + "ms");
	}

	/**
	 * Finds most influencers
	 *
	 * @param searchID
	 * @return
	 */
	private List<TwitterUser> createMostInfluencers(long searchId) {

		logger.debug("Method createMostInfluencersJSON(): Start");

		try {

			List<TwitterUser> topInfluencers = dpCache.getTopInfluencers(searchId, 32);

			List<TwitterUser> result = new ArrayList<TwitterUser>();

			if (topInfluencers != null && topInfluencers.size() > 0) {

				for (TwitterUser influencer : topInfluencers) {

					String profileImg = influencer.getProfileImgSrc();

					if (profileImg != null && !profileImg.equals("")) {

						profileImg = profileImg.replace("http", "https");

						influencer.setProfileImgSrc(profileImg);

					}

					result.add(influencer);
				}
			}

			logger.debug("Method getMostInfluencers(): End");
			return topInfluencers;
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method createMostInfluencers(): An error occurred for search ID: " + searchId, t);
		}

	}

	public List<TwitterUser> getMostInfluencers() {
		return mostInfluencers;
	}

}
