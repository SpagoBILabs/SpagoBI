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

import org.apache.log4j.Logger;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class DataProcessorCacheImpl implements IDataProcessorCache {

	private static final Logger logger = Logger.getLogger(DataProcessorCacheImpl.class);

	private DaoService daoService;

	public DataProcessorCacheImpl() {

		this.daoService = new DaoService();
	}

	@Override
	public int getTotalTweets(long searchID) throws DaoServiceException {

		logger.debug("Method getTotalTweets(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		// String queryHQL = "select tweet.tweetID from TwitterData tweet where tweet.twitterSearch.searchID = ?";
		String queryHQL = "select tweet.tweetID from TwitterData tweet where tweet.twitterSearch.searchID = ?";

		int result = daoService.countQuery(queryHQL, searchID);

		logger.debug("Method getTotalTweets(): End");

		return result;

	}

	@Override
	public int getTotalUsers(long searchID) throws DaoServiceException {

		logger.debug("Method getTotalUsers(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String queryHQL = "select user.userID from TwitterUser user, TwitterData tweet where tweet.twitterSearch.searchID = ? and tweet.twitterUser.userID = user.userID group by (user.userID)";

		int result = daoService.countQuery(queryHQL, searchID);

		logger.debug("Method getTotalUsers(): End");

		return result;

	}

	@Override
	public Calendar getMinTweetDate(long searchID) throws DaoServiceException {

		logger.debug("Method getMinTweetDate(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select tweet.dateCreatedAt from TwitterData tweet where tweet.twitterSearch.searchID = ? order by tweet.dateCreatedAt ASC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMinTweetDate(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMinTweetDate(): End");
			return null;
		}
	}

	@Override
	public Calendar getMaxTweetDate(long searchID) throws DaoServiceException {

		logger.debug("Method getMaxTweetDate(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select tweet.dateCreatedAt from TwitterData tweet where tweet.twitterSearch.searchID = ? order by tweet.dateCreatedAt DESC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMaxTweetDate(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMaxTweetDate(): End");
			return null;
		}

	}

	@Override
	public Calendar getMinTweetTime(long searchID) throws DaoServiceException {

		logger.debug("Method getMinTweetTime(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select tweet.timeCreatedAt from TwitterData tweet where tweet.twitterSearch.searchID = ? order by tweet.timeCreatedAt ASC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMinTweetTime(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMinTweetTime(): End");
			return null;
		}

	}

	@Override
	public Calendar getMaxTweetTime(long searchID) throws DaoServiceException {

		logger.debug("Method getMaxTweetTime(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select tweet.timeCreatedAt from TwitterData tweet where tweet.twitterSearch.searchID = ? order by tweet.timeCreatedAt DESC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMaxTweetTime(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMaxTweetTime(): End");
			return null;
		}

	}

	@Override
	public Object[] getDocuments(long searchID) throws DaoServiceException {

		logger.debug("Method getDocuments(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select ts.creationDate, tms.lastActivationTime, tms.documents from TwitterMonitorScheduler tms, TwitterSearch ts where ts.searchID = ? and tms.twitterSearch.searchID = ts.searchID";

		Object[] result = daoService.singleResultQuery(query, searchID);

		logger.debug("Method getDocuments(): End");

		return result;
	}

	@Override
	public List<TwitterUser> getTopInfluencers(long searchID, int maxResults) throws DaoServiceException {

		logger.debug("Method getTopInfluencers(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT DISTINCT new TwitterUser(tu.username, tu.description, tu.profileImgSrc, tu.followersCount) from TwitterUser tu, TwitterData td where tu.userID = td.twitterUser.userID and td.twitterSearch.searchID = ? order by tu.followersCount desc";

		List<TwitterUser> topInfluencers = daoService.listFromLimitedQuery(query, maxResults, searchID);

		logger.debug("Method getTopInfluencers(): End");

		return topInfluencers;

	}

	@Override
	public List<String> getUsersLocationCodes(long searchID) throws DaoServiceException {
		logger.debug("Method getUsersLocationCodes(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT tu.locationCode from TwitterUser tu, TwitterData td where tu.userID = td.twitterUser.userID and td.twitterSearch.searchID = ?";

		List<String> locationCodes = daoService.listFromQuery(query, searchID);

		logger.debug("Method getUsersLocationCodes(): End");

		return locationCodes;

	}

	@Override
	public int getTotalRTs(long searchID) throws DaoServiceException {

		logger.debug("Method getTotalRTs(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String queryHQL = "select tweet.tweetID from TwitterData tweet where tweet.isRetweet = true and tweet.twitterSearch.searchID = ?";

		int result = daoService.countQuery(queryHQL, searchID);

		logger.debug("Method getTotalRTs(): End");

		return result;

	}

	@Override
	public int getTotalReplies(long searchID) throws DaoServiceException {

		logger.debug("Method getTotalReplies(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String queryHQL = "select tweet.tweetID from TwitterData tweet where tweet.replyToTweetId != null and tweet.twitterSearch.searchID = ?";

		int result = daoService.countQuery(queryHQL, searchID);

		logger.debug("Method getTotalReplies(): End");

		return result;

	}

	@Override
	public List<String> getSources(long searchID) throws DaoServiceException {

		logger.debug("Method getSources(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT td.sourceClient from TwitterData td where td.twitterSearch.searchID = ?";

		List<String> sources = daoService.listFromQuery(query, searchID);

		logger.debug("Method getSources(): End");

		return sources;

	}

	@Override
	public List<String> getAccounts(long searchID) throws DaoServiceException {

		logger.debug("Method getAccounts(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT ta.accountName from TwitterAccountToMonitor ta where ta.twitterSearch.searchID = ? GROUP BY ta.accountName";

		List<String> accounts = daoService.listFromQuery(query, searchID);

		logger.debug("Method getAccounts(): End");

		return accounts;

	}

	@Override
	public List<TwitterAccountToMonitor> getAccountsToMonitorInfo(long searchID, String accountName) throws DaoServiceException {

		logger.debug("Method getAccountsToMonitorInfo(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT new TwitterAccountToMonitor(ta.followersCount, ta.timestamp) from TwitterAccountToMonitor ta where ta.twitterSearch.searchID = ? and ta.accountName = ? order by ta.timestamp asc";

		List<TwitterAccountToMonitor> accountInfo = daoService.listFromQuery(query, searchID, accountName);

		logger.debug("Method getAccountsToMonitorInfo(): End");
		return accountInfo;

	}

	@Override
	public List<String> getLinks(long searchID) throws DaoServiceException {

		logger.debug("Method getLinks(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT tl.link from TwitterLinkToMonitor tl where tl.twitterSearch.searchID = ? GROUP BY tl.link";

		List<String> links = daoService.listFromQuery(query, searchID);

		logger.debug("Method getLinks(): End");

		return links;

	}

	@Override
	public List<TwitterLinkToMonitor> getLinksToMonitorInfo(long searchID, String link) throws DaoServiceException {

		logger.debug("Method getLinksToMonitorInfo(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT new TwitterLinkToMonitor(tl.longUrl, tl.clicksCount, tl.timestamp) from TwitterLinkToMonitor tl where tl.twitterSearch.searchID = ? and tl.link = ? order by tl.timestamp asc";

		List<TwitterLinkToMonitor> linkInfo = daoService.listFromQuery(query, searchID, link);

		logger.debug("Method getLinksToMonitorInfo(): End");
		return linkInfo;

	}

	@Override
	public MonitorRepeatTypeEnum getMonitorRepeationType(long searchID) throws DaoServiceException {

		logger.debug("Method getMonitorRepeationType(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select tms.repeatType from TwitterMonitorScheduler tms WHERE tms.twitterSearch.searchID = ?";

		MonitorRepeatTypeEnum repeatType = daoService.singleResultQuery(query, searchID);

		logger.debug("Method getMonitorRepeationType(): End");

		return repeatType;

	}

	@Override
	public List<String> getHashtags(long searchID) throws DaoServiceException {

		logger.debug("Method getHashtags(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT td.hashtags from TwitterData td where td.twitterSearch.searchID = ?";

		List<String> hashtags = daoService.listFromQuery(query, searchID);

		logger.debug("Method getHashtags(): End");

		return hashtags;

	}

	@Override
	public List<String> getTopics(long searchID) throws DaoServiceException {

		logger.debug("Method getTopics(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT td.topics from TwitterData td where td.twitterSearch.searchID = ?";

		List<String> topics = daoService.listFromQuery(query, searchID);

		logger.debug("Method getTopics(): End");

		return topics;

	}

	@Override
	public List<TwitterData> getTimelineTweets(long searchID) throws DaoServiceException {

		logger.debug("Method getTimelineTweets(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT new TwitterData(td.timeCreatedAt, td.isRetweet) from TwitterData td where td.twitterSearch.searchID = ? order by td.timeCreatedAt asc";

		List<TwitterData> twitterDatas = daoService.listFromQuery(query, searchID);

		logger.debug("Method getTimelineTweets(): End");
		return twitterDatas;

	}

	@Override
	public List<TwitterData> getTopTweetsRTsOrder(long searchID, int nProfiles) throws DaoServiceException {

		logger.debug("Method getTopTweetsRTsOrder(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT new TwitterData(td.twitterUser, td.dateCreatedAt, td.hashtags, td.tweetText, td.timeCreatedAt, td.retweetCount) from TwitterData td where td.twitterSearch.searchID = ? order by td.retweetCount DESC";

		List<TwitterData> twitterDatas = daoService.listFromLimitedQuery(query, nProfiles, searchID);

		logger.debug("Method getTopTweetsRTsOrder(): End");
		return twitterDatas;
	}

	@Override
	public List<TwitterData> getTopTweetsRecentOrder(long searchID, int nProfiles) throws DaoServiceException {

		logger.debug("Method getTopTweetsRecentOrder(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT new TwitterData(td.twitterUser, td.dateCreatedAt, td.hashtags, td.tweetText, td.timeCreatedAt, td.retweetCount) from TwitterData td where td.twitterSearch.searchID = ? order by td.timeCreatedAt DESC";

		List<TwitterData> twitterDatas = daoService.listFromLimitedQuery(query, nProfiles, searchID);

		logger.debug("Method getTopTweetsRecentOrder(): End");
		return twitterDatas;

	}

	@Override
	public Calendar getMinLinksTime(long searchID) throws DaoServiceException {

		logger.debug("Method getMinLinksTime(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select links.timestamp from TwitterLinkToMonitor links where links.twitterSearch.searchID = ? order by links.timestamp ASC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMinLinksTime(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMinLinksTime(): End");
			return null;
		}

	}

	@Override
	public Calendar getMaxLinksTime(long searchID) throws DaoServiceException {

		logger.debug("Method getMaxLinksTime(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select links.timestamp from TwitterLinkToMonitor links where links.twitterSearch.searchID = ? order by links.timestamp DESC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMaxLinksTime(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMaxLinksTime(): End");
			return null;
		}

	}

	@Override
	public Calendar getMinAccountsTime(long searchID) throws DaoServiceException {

		logger.debug("Method getMinAccountsTime(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select accounts.timestamp from TwitterAccountToMonitor accounts where accounts.twitterSearch.searchID = ? order by accounts.timestamp ASC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMinAccountsTime(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMinAccountsTime(): End");
			return null;
		}

	}

	@Override
	public Calendar getMaxAccountsTime(long searchID) throws DaoServiceException {

		logger.debug("Method getMaxAccountsTime(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select accounts.timestamp from TwitterAccountToMonitor accounts where accounts.twitterSearch.searchID = ? order by accounts.timestamp DESC";

		List<Calendar> dates = daoService.listFromQuery(query, searchID);

		if (dates != null && dates.size() > 0) {

			logger.debug("Method getMaxAccountsTime(): End");
			return dates.get(0);

		} else {

			logger.debug("Method getMaxAccountsTime(): End");
			return null;
		}

	}

	@Override
	public int getTotalUsersWithLocationCode(long searchID) throws DaoServiceException {

		logger.debug("Method getTotalUsersWithLocationCode(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String queryHQL = "select distinct user from TwitterUser user, TwitterData tweet where tweet.twitterSearch.searchID = ? and tweet.twitterUser.userID = user.userID and user.locationCode is not null ";

		int result = daoService.countQuery(queryHQL, searchID);

		logger.debug("Method getTotalUsersWithLocationCode(): End");

		return result;

	}

	@Override
	public List<TwitterUser> getGeneralStatsForSearchID(long searchID) throws DaoServiceException {

		logger.debug("Method getUsersForSearchID(): Start");

		long initMills = System.currentTimeMillis();

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		// String queryHQL =
		// "select tu.userID as user, tu.followersCount, count(td.tweetID) from TwitterUser tu ,TwitterData td where tu.userID = td.twitterUser.userID and td.twitterSearch.searchID = ? GROUP by (td.twitterUser.userID)";

		String queryHQL = "select distinct new TwitterUser(tu.userID, tu.followersCount) from TwitterUser tu ,TwitterData td where tu.userID = td.twitterUser.userID and td.twitterSearch.searchID = ? ";

		// List<Object[]> result = daoService.listFromQuery(queryHQL, searchID);
		// List<Map<String, Object>> result = null;
		List<TwitterUser> users = daoService.listFromQuery(queryHQL, searchID);

		// List<TwitterUser> result = daoService.listFromQuery(queryHQL, searchID);
		// Annotation[] test = TwitterUser.class.getAnnotations();

		long endMills = System.currentTimeMillis() - initMills;

		logger.debug("Method getUsersForSearchID(): End in " + endMills + "ms");

		return users;

	}

	@Override
	public List<String> getMentions(long searchID) throws DaoServiceException {

		logger.debug("Method getMentions(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT td.mentions from TwitterData td where td.twitterSearch.searchID = ?";

		List<String> mentions = daoService.listFromQuery(query, searchID);

		logger.debug("Method getMentions(): End");

		return mentions;

	}

	@Override
	public List<TwitterData> getTweetsFromSearchId(long searchID) throws DaoServiceException {
		logger.debug("Method getTweetsFromSearchId(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "from TwitterData td where td.twitterSearch.searchID = ?";

		List<TwitterData> tweets = daoService.listFromQuery(query, searchID);

		logger.debug("Method getTweetsFromSearchId(): End");
		return tweets;
	}

	@Override
	public List<TwitterData> getLimitedTweetsFromSearchId(long searchID, int start, int end) throws DaoServiceException {

		logger.debug("Method getLimitedTweetsFromSearchId(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select new TwitterData(td.twitterSearch, td.twitterUser, td.replyToUserId, td.originalRTTweetId, (select tru from TwitterUser tru where tru.userID = td.replyToUserId), (select rtuser from TwitterData data, TwitterUser rtuser where data.twitterSearch.searchID = td.twitterSearch.searchID and data.tweetID = td.originalRTTweetId and rtuser.userID = data.twitterUser.userID)) from TwitterData td where td.twitterSearch.searchID = ? and (td.replyToUserId IS NOT NULL or td.originalRTTweetId IS NOT NULL) order by td.twitterUser.followersCount DESC";

		List<TwitterData> tweets = daoService.listFromBetweenLimitedQuery(query, start, end, searchID);

		logger.debug("Method getLimitedTweetsFromSearchId(): End");
		return tweets;
	}

	@Override
	public List<String> getDistinctUsersLocationCodes(long searchID, int limit) throws DaoServiceException {

		logger.debug("Method getDistinctUsersLocationCodes(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT distinct tu.locationCode from TwitterUser tu, TwitterData td where tu.userID = td.twitterUser.userID and td.twitterSearch.searchID = ? order by td.twitterUser.followersCount DESC";

		List<String> locationCodes = daoService.listFromLimitedQuery(query, limit, searchID);

		logger.debug("Method getDistinctUsersLocationCodes(): End");

		return locationCodes;

	}

	@Override
	public int getSearchTweetsNumberForUsersID(long searchID, long userID) throws DaoServiceException {

		logger.debug("Method getSearchTweetsNumberForUsersID(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT td.tweetID from TwitterData td where td.twitterSearch.searchID = ? and td.twitterUser.userID = ? ";

		int result = daoService.countQuery(query, searchID, userID);

		logger.debug("Method getSearchTweetsNumberForUsersID(): End");

		return result;
	}

	@Override
	public List<TwitterData> getSentimentSmilesTweets(long searchID) throws DaoServiceException {

		logger.debug("Method getSentimentSmilesTweets(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "SELECT new TwitterData(td.isPositive, td.isNeutral, td.isNegative, td.topics) from TwitterData td where td.twitterSearch.searchID = ?";

		List<TwitterData> result = daoService.listFromQuery(query, searchID);

		logger.debug("Method getSentimentSmilesTweets(): End");
		return result;
	}

	@Override
	public List<TwitterUser> getLimitedUsersForSearchID(long searchID, int limit) throws DaoServiceException {

		logger.debug("Method getUsersForSearchID(): Start");

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String queryHQL = "select distinct user from TwitterUser user, TwitterData tweet where tweet.twitterSearch.searchID = ? and tweet.twitterUser.userID = user.userID group by (user.userID)";

		List<TwitterUser> result = daoService.listFromLimitedQuery(queryHQL, limit, searchID);

		logger.debug("Method getUsersForSearchID(): End");

		return result;

	}

	@Override
	public TwitterUser getUserFromTweet(long searchID, long tweetID) throws DaoServiceException {

		logger.debug("Method getUserFromTweet(): Start looking for user of tweet_id = " + tweetID);

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String query = "select tweet.twitterUser from TwitterData tweet where tweet.twitterSearch.searchID = ? and tweet.tweetID = ?";

		TwitterUser twitterUser = daoService.singleResultQuery(query, searchID, tweetID);

		logger.debug("Method getUserFromTweet(): End");
		return twitterUser;
	}

	@Override
	public long countUserTweetsFromSearchId(long searchID, long userId) throws DaoServiceException {

		logger.debug("Method countUserTweetsFromSearchId(): Start");

		long initMills = System.currentTimeMillis();

		if (this.daoService == null) {
			this.daoService = new DaoService();
		}

		String queryHQL = "select count(td.tweetID) from TwitterData td where td.twitterSearch.searchID = ? and td.twitterUser.userID = ? ";

		long tweets = daoService.singleResultQuery(queryHQL, searchID, userId);

		// List<TwitterUser> result = daoService.listFromQuery(queryHQL, searchID);
		// Annotation[] test = TwitterUser.class.getAnnotations();

		long endMills = System.currentTimeMillis() - initMills;

		logger.debug("Method countUserTweetsFromSearchId(): End in " + endMills + "ms");

		return tweets;

	}

}
