/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.DataProcessorCacheImpl;
import it.eng.spagobi.twitter.analysis.cache.IDataProcessorCache;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */

public class TwitterLocationMapDataProcessor {

	private static final Logger logger = Logger.getLogger(TwitterLocationMapDataProcessor.class);

	private final IDataProcessorCache dpCache = new DataProcessorCacheImpl();

	/**
	 * This method creates the JSON object useful for jquery vector map
	 *
	 * @param searchID
	 * @return
	 */
	public JSONObject locationTracker(String searchID) {

		logger.debug("Method locationTracker(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try

		{
			Map<String, Integer> locationMap = new HashMap<String, Integer>();
			JSONObject resultJSON = new JSONObject();

			initializeLocationMap(locationMap);

			List<String> locationCodes = dpCache.getUsersLocationCodes(searchId);

			for (String locationCode : locationCodes) {

				if (locationCode != null && !locationCode.equals("")) {
					Integer tweetCounter = locationMap.get(locationCode);
					if (tweetCounter != null) {
						tweetCounter++;
						locationMap.put(locationCode, tweetCounter);
					}
				}
			}

			for (Map.Entry<String, Integer> entry : locationMap.entrySet()) {
				try {
					resultJSON.append(entry.getKey(), entry.getValue());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			logger.debug("Method locationTracker(): End");
			return resultJSON;

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method locationTracker(): An error occurred for search ID: " + searchID, t);
		}

	}

	/**
	 * Get users localization percentage
	 *
	 * @param searchId
	 * @return
	 */
	public String getRatioInfo(String searchID) {

		logger.debug("Method getRatioInfo(): Start");

		long searchId = AnalysisUtility.isLong(searchID);

		try {

			double result = 0;

			int totalUsers = dpCache.getTotalUsers(searchId);
			int totalUsersWithLocationCode = dpCache.getTotalUsersWithLocationCode(searchId);

			if (totalUsers > 0) {
				result = (100 * totalUsersWithLocationCode) / totalUsers;
			}

			int intResult = (int) result;

			logger.debug("Method getRatioInfo(): End");
			return "Location found for " + intResult + "% of twitter users";

		} catch (Throwable t) {

			throw new SpagoBIRuntimeException("Method getRatioInfo(): An error occurred for search ID: " + searchId, t);
		}

	}

	private void initializeLocationMap(Map<String, Integer> locationMap) {
		locationMap.put("AF", 0);
		locationMap.put("AL", 0);
		locationMap.put("DZ", 0);
		locationMap.put("AO", 0);
		locationMap.put("AG", 0);
		locationMap.put("AR", 0);
		locationMap.put("AM", 0);
		locationMap.put("AU", 0);
		locationMap.put("AT", 0);
		locationMap.put("AZ", 0);
		locationMap.put("BS", 0);
		locationMap.put("BH", 0);
		locationMap.put("BD", 0);
		locationMap.put("BB", 0);
		locationMap.put("BY", 0);
		locationMap.put("BE", 0);
		locationMap.put("BZ", 0);
		locationMap.put("BJ", 0);
		locationMap.put("BT", 0);
		locationMap.put("BO", 0);
		locationMap.put("BA", 0);
		locationMap.put("BW", 0);
		locationMap.put("BR", 0);
		locationMap.put("BN", 0);
		locationMap.put("BG", 0);
		locationMap.put("BF", 0);
		locationMap.put("BI", 0);
		locationMap.put("KH", 0);
		locationMap.put("CM", 0);
		locationMap.put("CA", 0);
		locationMap.put("CV", 0);
		locationMap.put("CF", 0);
		locationMap.put("TD", 0);
		locationMap.put("CL", 0);
		locationMap.put("CN", 0);
		locationMap.put("CO", 0);
		locationMap.put("KM", 0);
		locationMap.put("CD", 0);
		locationMap.put("CG", 0);
		locationMap.put("CR", 0);
		locationMap.put("CI", 0);
		locationMap.put("HR", 0);
		locationMap.put("CY", 0);
		locationMap.put("CZ", 0);
		locationMap.put("DK", 0);
		locationMap.put("DJ", 0);
		locationMap.put("DM", 0);
		locationMap.put("DO", 0);
		locationMap.put("EC", 0);
		locationMap.put("EG", 0);
		locationMap.put("SV", 0);
		locationMap.put("GQ", 0);
		locationMap.put("ER", 0);
		locationMap.put("EE", 0);
		locationMap.put("ET", 0);
		locationMap.put("FJ", 0);
		locationMap.put("FI", 0);
		locationMap.put("FR", 0);
		locationMap.put("GA", 0);
		locationMap.put("GM", 0);
		locationMap.put("GE", 0);
		locationMap.put("DE", 0);
		locationMap.put("GH", 0);
		locationMap.put("GR", 0);
		locationMap.put("GD", 0);
		locationMap.put("GT", 0);
		locationMap.put("GN", 0);
		locationMap.put("GW", 0);
		locationMap.put("GY", 0);
		locationMap.put("HT", 0);
		locationMap.put("HN", 0);
		locationMap.put("HK", 0);
		locationMap.put("HU", 0);
		locationMap.put("IS", 0);
		locationMap.put("IN", 0);
		locationMap.put("ID", 0);
		locationMap.put("IR", 0);
		locationMap.put("IQ", 0);
		locationMap.put("IE", 0);
		locationMap.put("IL", 0);
		locationMap.put("IT", 0);
		locationMap.put("JM", 0);
		locationMap.put("JP", 0);
		locationMap.put("JO", 0);
		locationMap.put("KZ", 0);
		locationMap.put("KE", 0);
		locationMap.put("KI", 0);
		locationMap.put("KR", 0);
		locationMap.put("UNDEFINED", 0);
		locationMap.put("KW", 0);
		locationMap.put("KG", 0);
		locationMap.put("LA", 0);
		locationMap.put("LV", 0);
		locationMap.put("LB", 0);
		locationMap.put("LS", 0);
		locationMap.put("LR", 0);
		locationMap.put("LY", 0);
		locationMap.put("LT", 0);
		locationMap.put("LU", 0);
		locationMap.put("MK", 0);
		locationMap.put("MG", 0);
		locationMap.put("MW", 0);
		locationMap.put("MY", 0);
		locationMap.put("MV", 0);
		locationMap.put("ML", 0);
		locationMap.put("MT", 0);
		locationMap.put("MR", 0);
		locationMap.put("MU", 0);
		locationMap.put("MX", 0);
		locationMap.put("MD", 0);
		locationMap.put("MN", 0);
		locationMap.put("ME", 0);
		locationMap.put("MA", 0);
		locationMap.put("MZ", 0);
		locationMap.put("MM", 0);
		locationMap.put("NA", 0);
		locationMap.put("NP", 0);
		locationMap.put("NL", 0);
		locationMap.put("NZ", 0);
		locationMap.put("NI", 0);
		locationMap.put("NE", 0);
		locationMap.put("NG", 0);
		locationMap.put("NO", 0);
		locationMap.put("OM", 0);
		locationMap.put("PK", 0);
		locationMap.put("PA", 0);
		locationMap.put("PG", 0);
		locationMap.put("PY", 0);
		locationMap.put("PE", 0);
		locationMap.put("PH", 0);
		locationMap.put("PL", 0);
		locationMap.put("PT", 0);
		locationMap.put("QA", 0);
		locationMap.put("RO", 0);
		locationMap.put("RU", 0);
		locationMap.put("RW", 0);
		locationMap.put("WS", 0);
		locationMap.put("ST", 0);
		locationMap.put("SA", 0);
		locationMap.put("SN", 0);
		locationMap.put("RS", 0);
		locationMap.put("SC", 0);
		locationMap.put("SL", 0);
		locationMap.put("SG", 0);
		locationMap.put("SK", 0);
		locationMap.put("SI", 0);
		locationMap.put("SB", 0);
		locationMap.put("ZA", 0);
		locationMap.put("ES", 0);
		locationMap.put("LK", 0);
		locationMap.put("KN", 0);
		locationMap.put("LC", 0);
		locationMap.put("VC", 0);
		locationMap.put("SD", 0);
		locationMap.put("SR", 0);
		locationMap.put("SZ", 0);
		locationMap.put("SE", 0);
		locationMap.put("CH", 0);
		locationMap.put("SY", 0);
		locationMap.put("TW", 0);
		locationMap.put("TJ", 0);
		locationMap.put("TZ", 0);
		locationMap.put("TH", 0);
		locationMap.put("TL", 0);
		locationMap.put("TG", 0);
		locationMap.put("TO", 0);
		locationMap.put("TT", 0);
		locationMap.put("TN", 0);
		locationMap.put("TR", 0);
		locationMap.put("TM", 0);
		locationMap.put("UG", 0);
		locationMap.put("UA", 0);
		locationMap.put("AE", 0);
		locationMap.put("GB", 0);
		locationMap.put("US", 0);
		locationMap.put("UY", 0);
		locationMap.put("UZ", 0);
		locationMap.put("VU", 0);
		locationMap.put("VE", 0);
		locationMap.put("VN", 0);
		locationMap.put("YE", 0);
		locationMap.put("ZM", 0);
		locationMap.put("ZW", 0);
		locationMap.put("SS", 0);
		locationMap.put("_3", 0);
		locationMap.put("SO", 0);
		locationMap.put("GL", 0);
		locationMap.put("CU", 0);
		locationMap.put("_1", 0);
		locationMap.put("_2", 0);
	}

}
