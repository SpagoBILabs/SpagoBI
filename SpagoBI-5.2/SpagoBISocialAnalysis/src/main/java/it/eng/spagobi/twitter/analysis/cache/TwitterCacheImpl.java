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
import it.eng.spagobi.twitter.analysis.pojos.TwitterMessageObject;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Status;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterCacheImpl implements ITwitterCache {

	private static final Logger logger = Logger.getLogger(TwitterCacheImpl.class);

	private DaoService daoService;

	public TwitterCacheImpl() {

		this.daoService = new DaoService();
	}

	@Override
	public TwitterSearch createTwitterSearch(TwitterSearch twitterSearch) throws DaoServiceException {

		logger.debug("Method createTwitterSearch(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.create(twitterSearch);

		logger.debug("Method createTwitterSearch(): End");

		return twitterSearch;

	}

	@Override
	public void updateTwitterSearch(TwitterSearch twitterSearch) throws DaoServiceException {

		logger.debug("Method updateTwitterSearch(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.update(twitterSearch);

		logger.debug("Method updateTwitterSearch(): End");

	}

	@Override
	public void updateTwitterData(TwitterData twitterData) throws DaoServiceException {

		logger.debug("Method updateTwitterData(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.update(twitterData);

		logger.debug("Method updateTwitterData(): End");

	}

	@Override
	public List<TwitterSearch> getTwitterSearchList(SearchTypeEnum searchType) throws DaoServiceException {

		logger.debug("Method getTwitterSearchList(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterSearch as search where search.type = ? and search.deleted = false";

		List<TwitterSearch> result = daoService.listFromQuery(query, searchType);

		logger.debug("Method getTwitterSearchList(): End");

		return result;

	}

	@Override
	public void deleteSearch(TwitterSearch twitterSearch) throws DaoServiceException {

		logger.debug("Method deleteSearch(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		if (twitterSearch.getTwitterSearchScheduler() != null) {

			TwitterSearchScheduler searchScheduler = (TwitterSearchScheduler) daoService.find(TwitterSearchScheduler.class, twitterSearch
					.getTwitterSearchScheduler().getId());
			daoService.delete(searchScheduler);
			twitterSearch.setTwitterSearchScheduler(null);

		}

		if (twitterSearch.getTwitterMonitorScheduler() != null) {

			TwitterMonitorScheduler twitterMonitorScheduler = (TwitterMonitorScheduler) daoService.find(TwitterMonitorScheduler.class, twitterSearch
					.getTwitterMonitorScheduler().getId());
			daoService.delete(twitterMonitorScheduler);
			twitterSearch.setTwitterMonitorScheduler(null);

		}

		twitterSearch.setLabel(twitterSearch.getLabel() + "_DELETED_" + System.currentTimeMillis());
		twitterSearch.setDeleted(true);
		twitterSearch.setLoading(false);

		daoService.update(twitterSearch);

		logger.debug("Method deleteSearch(): End");

	}

	@Override
	public void saveTweet(Status tweet, long searchID) throws DaoServiceException {

		TwitterMessageObject twitterMessage = new TwitterMessageObject(tweet);

		TwitterUser twitterUser = twitterMessage.getTwitterUser();
		TwitterData twitterData = twitterMessage.getTwitterData();

		TwitterUser twitterUserFromDB = isTwitterUserPresent(twitterMessage.getUserID());

		if (twitterUserFromDB == null) {

			// user not present in DB, create a new twitter user

			String locationCode = AnalysisUtility.findCountryCodeFromUserLocation(twitterUser.getLocation(), twitterUser.getTimeZone());
			twitterUser.setLocationCode(locationCode);
			Calendar nowCalendar = GregorianCalendar.getInstance();
			twitterUser.setStartDate(nowCalendar);
			twitterUser.setEndDate(nowCalendar);

			insertTwitterUser(twitterUser);

		} else {

			// user in DB, update twitter user

			Calendar timeNow = GregorianCalendar.getInstance();

			if (twitterUserFromDB.getEndDate().compareTo(timeNow) < 0) {

				twitterUserFromDB.setUsername(twitterUser.getUsername());
				twitterUserFromDB.setDescription(twitterUser.getDescription());
				twitterUserFromDB.setFollowersCount(twitterUser.getFollowersCount());
				twitterUserFromDB.setProfileImgSrc(twitterUser.getProfileImgSrc());
				twitterUserFromDB.setLanguageCode(twitterUser.getLanguageCode());
				twitterUserFromDB.setName(twitterUser.getName());

				if (twitterUserFromDB.getLocation() != null && !twitterUserFromDB.getLocation().equalsIgnoreCase(twitterUser.getLocation())) {

					logger.debug("Method saveTweet(): Updating Twitter User. Different location found, calling new geocoding");

					String locationCode = AnalysisUtility.findCountryCodeFromUserLocation(twitterUser.getLocation(), twitterUser.getTimeZone());
					twitterUserFromDB.setLocationCode(locationCode);
					twitterUserFromDB.setLocation(twitterUser.getLocation());
				}

				twitterUserFromDB.setTimeZone(twitterUser.getTimeZone());
				twitterUserFromDB.setTweetsCount(twitterUser.getTweetsCount());
				twitterUserFromDB.setVerified(twitterUser.isVerified());
				twitterUserFromDB.setFollowingCount(twitterUser.getFollowingCount());
				twitterUserFromDB.setUtcOffset(twitterUser.getUtcOffset());
				twitterUserFromDB.setGeoEnabled(twitterUser.isGeoEnabled());
				twitterUserFromDB.setListedCount(twitterUser.getListedCount());
				twitterUserFromDB.setEndDate(GregorianCalendar.getInstance());

				updateTwitterUser(twitterUserFromDB);
			}

		}

		TwitterData tweetFromDB = isTwitterDataPresent(searchID, twitterMessage.getTweetID());

		if (tweetFromDB == null) {

			TwitterSearch search = this.findTwitterSearch(searchID);
			search = this.refreshTwitterSearch(search);

			twitterData.setTwitterSearch(search);

			insertTweet(twitterData);

		} else {

			TwitterSearch search = this.findTwitterSearch(searchID);

			tweetFromDB.setTwitterSearch(search);
			tweetFromDB.setTweetText(twitterData.getTweetText());
			tweetFromDB.setTweetTextTranslated(twitterData.getTweetTextTranslated());
			tweetFromDB.setGeoLatitude(twitterData.getGeoLatitude());
			tweetFromDB.setGeoLongitude(twitterData.getGeoLongitude());
			tweetFromDB.setHashtags(twitterData.getHashtags());
			tweetFromDB.setMentions(twitterData.getMentions());
			tweetFromDB.setRetweetCount(twitterData.getRetweetCount());
			tweetFromDB.setRetweet(twitterData.isRetweet());
			tweetFromDB.setLanguageCode(twitterData.getLanguageCode());
			tweetFromDB.setPlaceCountry(twitterData.getPlaceCountry());
			tweetFromDB.setPlaceName(twitterData.getPlaceName());
			tweetFromDB.setUrlCited(twitterData.getUrlCited());
			tweetFromDB.setFavorited(twitterData.isFavorited());
			tweetFromDB.setFavoritedCount(twitterData.getFavoritedCount());
			tweetFromDB.setReplyToScreenName(twitterData.getReplyToScreenName());
			tweetFromDB.setReplyToUserId(twitterData.getReplyToUserId());
			tweetFromDB.setReplyToTweetId(twitterData.getReplyToTweetId());
			tweetFromDB.setOriginalRTTweetId(twitterData.getOriginalRTTweetId());
			tweetFromDB.setSensitive(twitterData.isSensitive());
			tweetFromDB.setMediaCount(twitterData.getMediaCount());

			this.updateTwitterData(tweetFromDB);

		}

	}

	@Override
	public void insertTwitterUser(TwitterUser twitterUser) throws DaoServiceException {

		logger.debug("Method insertTwitterUser(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.create(twitterUser);

	}

	@Override
	public void insertTweet(TwitterData twitterData) throws DaoServiceException {

		logger.debug("Method insertTweet(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		daoService.create(twitterData);

		logger.debug("Method insertTweet(): End");

	}

	@Override
	public TwitterUser isTwitterUserPresent(long userID) throws DaoServiceException {

		logger.debug("Method isTwitterUserPresent(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		TwitterUser twitterUser = (TwitterUser) daoService.find(TwitterUser.class, userID);

		return twitterUser;

	}

	@Override
	public TwitterData isTwitterDataPresent(long searchID, long tweetID) throws DaoServiceException {

		logger.debug("Method isTwitterDataPresent(): Start for tweet_id = " + tweetID);

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = " from TwitterData tweet where tweet.twitterSearch.searchID = ? and tweet.tweetID = ?";

		TwitterData twitterData = daoService.singleResultQuery(query, searchID, tweetID);

		logger.debug("Method isTwitterDataPresent(): End");
		return twitterData;
	}

	@Override
	public TwitterSearch findTwitterSearch(long searchID) throws DaoServiceException {

		logger.debug("Method findTwitterSearch(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		TwitterSearch twitterSearch = (TwitterSearch) daoService.find(TwitterSearch.class, searchID);

		Assert.assertNotNull(twitterSearch, "Method findTwitterSearch(): Impossible to get a Twitter Search with search_id: " + searchID);

		return twitterSearch;

	}

	@Override
	public void insertBitlyAnalysis(TwitterLinkToMonitor twitterLinkToMonitor, List<TwitterLinkToMonitorCategory> twitterLinkToMonitorCategoryList,
			long searchID) throws DaoServiceException {

		logger.debug("Method insertBitlyAnalysis(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		TwitterSearch twitterSearch = new TwitterSearch();
		twitterSearch.setSearchID(searchID);

		twitterLinkToMonitor.setTwitterSearch(twitterSearch);

		twitterLinkToMonitor = (TwitterLinkToMonitor) daoService.create(twitterLinkToMonitor);

		for (TwitterLinkToMonitorCategory linkCategory : twitterLinkToMonitorCategoryList) {
			linkCategory.setTwitterLinkToMonitor(twitterLinkToMonitor);

			daoService.create(linkCategory);
		}

		logger.debug("Method insertBitlyAnalysis(): End");
	}

	@Override
	public void insertAccountToMonitor(TwitterAccountToMonitor twitterAccountToMonitor, long searchID) throws DaoServiceException {

		logger.debug("Method insertAccountToMonitor(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		TwitterSearch twitterSearch = new TwitterSearch();
		twitterSearch.setSearchID(searchID);

		twitterAccountToMonitor.setTwitterSearch(twitterSearch);

		daoService.create(twitterAccountToMonitor);

		logger.debug("Method insertAccountToMonitor(): End");

	}

	@Override
	public TwitterSearch isPresentEnabledStream() throws DaoServiceException {

		logger.debug("Method isPresentEnabledStream(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterSearch search where search.type = ? and search.loading = true";

		TwitterSearch twitterSearch = daoService.singleResultQuery(query, SearchTypeEnum.STREAMINGAPI);

		return twitterSearch;

	}

	@Override
	public void updateTwitterMonitorScheduler(TwitterMonitorScheduler twitterMonitorScheduler) throws DaoServiceException {

		logger.debug("Method updateTwitterMonitorScheduler(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.update(twitterMonitorScheduler);

		logger.debug("Method updateTwitterMonitorScheduler(): End");

	}

	@Override
	public void updateTwitterSearchScheduler(TwitterSearchScheduler twitterSearchScheduler) throws DaoServiceException {

		logger.debug("Method updateTwitterSearchScheduler(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.update(twitterSearchScheduler);

		logger.debug("Method updateTwitterSearchScheduler(): End");
	}

	@Override
	public void stopAllStreams() throws DaoServiceException {

		logger.debug("Method stopAllStreams(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "update TwitterSearch search set search.loading = false where search.type = ?";

		daoService.updateFromQuery(query, SearchTypeEnum.STREAMINGAPI);

		logger.debug("Method stopAllStreams(): End");

	}

	@Override
	public List<TwitterSearchScheduler> getAllActiveSearchSchedulers() throws DaoServiceException {

		logger.debug("Method getAllActiveSearchSchedulers(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterSearchScheduler ts where ts.active = true and ts.twitterSearch.deleted = false";

		List<TwitterSearchScheduler> searchSchedulers = daoService.listFromQuery(query);

		logger.debug("Method getAllActiveSearchSchedulers(): End");
		return searchSchedulers;

	}

	@Override
	public List<TwitterMonitorScheduler> getAllMonitorSchedulers() throws DaoServiceException {

		logger.debug("Method getAllMonitorSchedulers(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterMonitorScheduler tms where tms.twitterSearch.deleted = false and tms.active = true";

		List<TwitterMonitorScheduler> monitorSchedulers = daoService.listFromQuery(query);

		logger.debug("Method getAllMonitorSchedulers(): End");
		return monitorSchedulers;

	}

	@Override
	public TwitterMonitorScheduler getMonitorSchedulerFromSearch(long searchID) throws DaoServiceException {

		logger.debug("Method getMonitorSchedulerFromSearch(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterMonitorScheduler tms where tms.twitterSearch.searchID = ? and tms.twitterSearch.deleted = false";

		TwitterMonitorScheduler monitorScheduler = daoService.singleResultQuery(query, searchID);

		logger.debug("Method getMonitorSchedulerFromSearch(): End");
		return monitorScheduler;

	}

	@Override
	public TwitterSearchScheduler findTwitterSearchScheduler(long id) throws DaoServiceException {

		logger.debug("Method findTwitterSearchScheduler(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		TwitterSearchScheduler twitterSearchScheduler = (TwitterSearchScheduler) daoService.find(TwitterSearchScheduler.class, id);

		// Assert.assertNotNull(twitterSearch, "Method findTwitterSearchScheduler(): Impossible to get a Twitter Search Scheduler with id: " + searchID);

		return twitterSearchScheduler;

	}

	@Override
	public TwitterSearch refreshTwitterSearch(TwitterSearch twitterSearch) throws DaoServiceException {

		logger.debug("Method refreshTwitterSearc(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		logger.debug("Method refreshTwitterSearc(): End");
		return (TwitterSearch) daoService.refresh(twitterSearch);

	}

	@Override
	public void updateTwitterUser(TwitterUser twitterUser) throws DaoServiceException {

		logger.debug("Method updateTwitterUser(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		this.daoService.update(twitterUser);

		logger.debug("Method updateTwitterUser(): End");

	}

	@Override
	public List<TwitterSearch> getHistoricalLoadingSearches() throws DaoServiceException {
		logger.debug("Method getHistoricalLoadingSearches(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterSearch search where search.type = 'SEARCHAPI' and search.loading = true";

		List<TwitterSearch> searches = daoService.listFromQuery(query);

		logger.debug("Method getHistoricalLoadingSearches(): End");
		return searches;

	}

}
