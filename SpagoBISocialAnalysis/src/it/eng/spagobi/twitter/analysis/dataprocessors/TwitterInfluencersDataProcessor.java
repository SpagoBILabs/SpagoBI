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

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterInfluencersDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterInfluencersDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	/**
	 * Finds most influencers
	 * 
	 * @param searchID
	 * @return
	 */
	public List<TwitterUser> getMostInfluencers(String searchID) {

		logger.debug("Method getMostInfluencersJSON(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			List<TwitterUser> topInfluencers = dpCache.getTopInfluencers(searchId, 32);

			logger.debug("Method getMostInfluencers(): End");
			return topInfluencers;
		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getMostInfluencers(): An error occurred for search ID: " + searchID, t);
		}

	}
}
