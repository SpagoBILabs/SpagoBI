/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.twitter.analysis.cache.exceptions.DaoServiceException;
import it.eng.spagobi.twitter.analysis.entities.TwitterAccountToMonitor;
import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.entities.TwitterLinkToMonitor;
import it.eng.spagobi.twitter.analysis.entities.TwitterLinkToMonitorCategory;
import it.eng.spagobi.twitter.analysis.entities.TwitterMonitorScheduler;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;
import it.eng.spagobi.twitter.analysis.entities.TwitterSearchScheduler;
import it.eng.spagobi.twitter.analysis.entities.TwitterUser;
import it.eng.spagobi.twitter.analysis.enums.SearchTypeEnum;

import java.util.List;

import twitter4j.Status;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public interface ITwitterCache {

	public TwitterSearch createTwitterSearch(TwitterSearch twitterSearch) throws DaoServiceException;

	public void updateTwitterSearch(TwitterSearch twitterSearch) throws DaoServiceException;

	public void updateTwitterData(TwitterData twitterData) throws DaoServiceException;

	public List<TwitterSearch> getTwitterSearchList(SearchTypeEnum searchType) throws DaoServiceException;

	public void deleteSearch(TwitterSearch twitterSearch) throws DaoServiceException;

	public void saveTweet(Status tweet, long searchID) throws DaoServiceException;

	public void insertTwitterUser(TwitterUser twitterUser) throws DaoServiceException;

	public void insertTweet(TwitterData twitterData) throws DaoServiceException;

	public TwitterUser isTwitterUserPresent(long userID) throws DaoServiceException;

	public TwitterData isTwitterDataPresent(long searchID, long tweetID) throws DaoServiceException;

	public TwitterSearch findTwitterSearch(long searchID) throws DaoServiceException;

	public void insertBitlyAnalysis(TwitterLinkToMonitor twitterLinkToMonitor, List<TwitterLinkToMonitorCategory> twitterLinkToMonitorCategoryList,
			long searchID) throws DaoServiceException;

	public void insertAccountToMonitor(TwitterAccountToMonitor twitterAccountToMonitor, long searchID) throws DaoServiceException;

	public TwitterSearch isPresentEnabledStream() throws DaoServiceException;

	public void updateTwitterMonitorScheduler(TwitterMonitorScheduler twitterMonitorScheduler) throws DaoServiceException;

	public void updateTwitterSearchScheduler(TwitterSearchScheduler twitterSearchScheduler) throws DaoServiceException;

	public void stopAllStreams() throws DaoServiceException;

	public List<TwitterSearchScheduler> getAllActiveSearchSchedulers() throws DaoServiceException;

	public List<TwitterMonitorScheduler> getAllMonitorSchedulers() throws DaoServiceException;

	public TwitterMonitorScheduler getMonitorSchedulerFromSearch(long searchID) throws DaoServiceException;

	public TwitterSearchScheduler findTwitterSearchScheduler(long id) throws DaoServiceException;

	public TwitterSearch refreshTwitterSearch(TwitterSearch twitterSearch) throws DaoServiceException;

	public void updateTwitterUser(TwitterUser twitterUser) throws DaoServiceException;

	public List<TwitterSearch> getHistoricalLoadingSearches() throws DaoServiceException;

}
