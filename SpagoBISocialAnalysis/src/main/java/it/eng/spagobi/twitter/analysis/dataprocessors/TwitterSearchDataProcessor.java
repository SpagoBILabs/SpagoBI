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


/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterSearchDataProcessor {

	// static final Logger logger = Logger.getLogger(TwitterSearchDataProcessor.class);
	//
	// private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");
	//
	// public List<TwitterSearchPojo> getTwitterSearchList(String searchType) {
	//
	// List<TwitterSearchPojo> searchList = new ArrayList<TwitterSearchPojo>();
	//
	// String sqlQuery = "SELECT * from twitter_search where type = '" + searchType + "' and isDeleted = 0";
	//
	// try {
	//
	// CachedRowSet rs = twitterCache.runQuery(sqlQuery);
	//
	// if (rs != null) {
	//
	// while (rs.next()) {
	//
	// long searchID = rs.getLong("search_id");
	// String label = rs.getString("label");
	// String keywords = rs.getString("keywords");
	// java.sql.Date creationDate = rs.getDate("creation_date");
	// java.sql.Timestamp lastActivationTime = rs.getTimestamp("last_activation_time");
	// String frequency = rs.getString("frequency");
	// String type = rs.getString("type");
	// boolean loading = rs.getBoolean("loading");
	// boolean isFailed = rs.getBoolean("failed");
	//
	// String links = "";
	// String accounts = "";
	// String documents = "";
	// boolean hasMonitorScheduler = false;
	//
	// CachedRowSet resourcesRs = twitterCache.runQuery("SELECT links, accounts, documents from twitter_monitor_scheduler where search_id = "
	// + searchID);
	// if (resourcesRs.next()) {
	//
	// links = resourcesRs.getString("links");
	// accounts = resourcesRs.getString("accounts");
	// documents = resourcesRs.getString("documents");
	// hasMonitorScheduler = true;
	//
	// }
	//
	// boolean hasSearchScheduler = false;
	//
	// if (searchType.equals("searchAPI")) {
	// CachedRowSet searchSchedulerRs = twitterCache.runQuery("SELECT id from twitter_search_scheduler where search_id = " + searchID
	// + " and active = 1");
	// while (searchSchedulerRs.next()) {
	//
	// hasSearchScheduler = true;
	// }
	// }
	//
	// TwitterSearchPojo searchPojo = new TwitterSearchPojo(searchID, label, keywords, creationDate, lastActivationTime, frequency, type, loading,
	// links, accounts, documents, hasSearchScheduler, hasMonitorScheduler, isFailed);
	//
	// searchList.add(searchPojo);
	// }
	// }
	//
	// } catch (Exception e) {
	// logger.debug("**** connection failed: " + e);
	// }
	//
	// return searchList;
	// }

}
