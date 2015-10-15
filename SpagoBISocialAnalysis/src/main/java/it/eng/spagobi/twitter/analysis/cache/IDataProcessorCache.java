/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.twitter.analysis.cache.exceptions.DaoServiceException;
import it.eng.spagobi.twitter.analysis.entities.TwitterAccountToMonitor;
import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.entities.TwitterLinkToMonitor;
import it.eng.spagobi.twitter.analysis.entities.TwitterUser;
import it.eng.spagobi.twitter.analysis.enums.MonitorRepeatTypeEnum;

import java.util.Calendar;
import java.util.List;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public interface IDataProcessorCache {

	public int getTotalTweets(long searchID) throws DaoServiceException;

	public int getTotalUsers(long searchID) throws DaoServiceException;

	public Calendar getMinTweetDate(long searchID) throws DaoServiceException;

	public Calendar getMaxTweetDate(long searchID) throws DaoServiceException;

	public Calendar getMinTweetTime(long searchID) throws DaoServiceException;

	public Calendar getMaxTweetTime(long searchID) throws DaoServiceException;

	public Object[] getDocuments(long searchID) throws DaoServiceException;

	public List<TwitterUser> getTopInfluencers(long searchID, int maxResults) throws DaoServiceException;

	public List<String> getUsersLocationCodes(long searchID) throws DaoServiceException;

	public int getTotalRTs(long searchID) throws DaoServiceException;

	public int getTotalReplies(long searchID) throws DaoServiceException;

	public List<String> getSources(long searchID) throws DaoServiceException;

	public List<String> getAccounts(long searchID) throws DaoServiceException;

	public List<TwitterAccountToMonitor> getAccountsToMonitorInfo(long searchID, String accountName) throws DaoServiceException;

	public List<String> getLinks(long searchID) throws DaoServiceException;

	public List<TwitterLinkToMonitor> getLinksToMonitorInfo(long searchID, String link) throws DaoServiceException;

	public MonitorRepeatTypeEnum getMonitorRepeationType(long searchID) throws DaoServiceException;

	public List<String> getHashtags(long searchID) throws DaoServiceException;

	public List<String> getTopics(long searchID) throws DaoServiceException;

	public List<TwitterData> getTimelineTweets(long searchID) throws DaoServiceException;

	public List<TwitterData> getTopTweetsRTsOrder(long searchID, int nProfiles) throws DaoServiceException;

	public List<TwitterData> getTopTweetsRecentOrder(long searchID, int nProfiles) throws DaoServiceException;

	public Calendar getMinLinksTime(long searchID) throws DaoServiceException;

	public Calendar getMaxLinksTime(long searchID) throws DaoServiceException;

	public Calendar getMinAccountsTime(long searchID) throws DaoServiceException;

	public Calendar getMaxAccountsTime(long searchID) throws DaoServiceException;

	public int getTotalUsersWithLocationCode(long searchID) throws DaoServiceException;

	public List<TwitterUser> getGeneralStatsForSearchID(long searchID) throws DaoServiceException;

	public List<String> getMentions(long searchID) throws DaoServiceException;

	public List<TwitterData> getTweetsFromSearchId(long searchID) throws DaoServiceException;

	public List<TwitterData> getLimitedTweetsFromSearchId(long searchID, int start, int end) throws DaoServiceException;

	public List<String> getDistinctUsersLocationCodes(long searchID, int limit) throws DaoServiceException;

	public int getSearchTweetsNumberForUsersID(long searchID, long userID) throws DaoServiceException;

	public List<TwitterData> getSentimentSmilesTweets(long searchID) throws DaoServiceException;

	public List<TwitterUser> getLimitedUsersForSearchID(long searchID, int limit) throws DaoServiceException;

	public TwitterUser getUserFromTweet(long searchID, long tweetID) throws DaoServiceException;

	public long countUserTweetsFromSearchId(long searchID, long userId) throws DaoServiceException;

}
