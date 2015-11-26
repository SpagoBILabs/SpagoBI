/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import it.eng.spagobi.twitter.analysis.entities.idclasses.TwitterDataPK;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Entity
@IdClass(TwitterDataPK.class)
@Table(name = "TWITTER_DATA")
public class TwitterData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8908623496529801477L;

	@Id
	@Column(name = "tweet_id")
	@NotNull
	private long tweetID;

	@OneToOne
	@JoinColumn(name = "user_id")
	@NotNull
	private TwitterUser twitterUser;

	@OneToOne
	@JoinColumn(name = "search_id")
	@NotNull
	private TwitterSearch twitterSearch;

	@Column(name = "date_created_at")
	@Temporal(TemporalType.DATE)
	@NotNull
	private java.util.Calendar dateCreatedAt;

	@Column(name = "time_created_at")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar timeCreatedAt;

	@Column(name = "source_client")
	@Length(max = 200)
	private String sourceClient;

	@Column(name = "tweet_text")
	@Length(max = 400)
	@NotNull
	private String tweetText;

	@Column(name = "tweet_text_translated")
	@Length(max = 400)
	private String tweetTextTranslated;

	@Column(name = "geo_latitude")
	private double geoLatitude;

	@Column(name = "geo_longitude")
	private double geoLongitude;

	@Column(name = "hashtags")
	@Length(max = 200)
	private String hashtags;

	@Column(name = "mentions")
	@Length(max = 200)
	private String mentions;

	@Column(name = "retweet_count")
	@NotNull
	private int retweetCount;

	@Column(name = "is_retweet", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isRetweet = false;

	@Column(name = "language_code")
	@Length(max = 5)
	private String languageCode;

	@Column(name = "place_country")
	@Length(max = 200)
	private String placeCountry;

	@Column(name = "place_name")
	@Length(max = 200)
	private String placeName;

	@Column(name = "url_cited")
	@Length(max = 200)
	private String urlCited;

	@Column(name = "is_favorited", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isFavorited = false;

	@Column(name = "favorited_count")
	@NotNull
	private int favoritedCount;

	@Column(name = "reply_to_screen_name")
	@Length(max = 45)
	private String replyToScreenName;

	@Column(name = "reply_to_user_id")
	private Long replyToUserId;

	@Column(name = "reply_to_tweet_id")
	private Long replyToTweetId;

	@Column(name = "original_RT_tweet_id")
	private Long originalRTTweetId;

	@Column(name = "is_sensitive", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isSensitive = false;

	@Column(name = "media_count")
	@NotNull
	private int mediaCount;

	@Column(name = "topics")
	@Length(max = 1000)
	private String topics;

	@Column(name = "is_positive", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isPositive = false;

	@Column(name = "is_neutral", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isNeutral = false;

	@Column(name = "is_negative", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isNegative = false;

	@Transient
	private TwitterUser replyUser;

	@Transient
	private TwitterUser rtUser;

	public TwitterData() {

	}

	public TwitterData(long tweetID, TwitterUser twitterUser, TwitterSearch twitterSearch, Calendar dateCreatedAt, Calendar timeCreatedAt, String sourceClient,
			String tweetText, String tweetTextTranslated, double geoLatitude, double geoLongitude, String hashtags, String mentions, int retweetCount,
			boolean isRetweet, String languageCode, String placeCountry, String placeName, String urlCited, boolean isFavorited, int favoritedCount,
			String replyToScreenName, Long replyToUserId, Long replyToTweetId, Long originalRTTweetId, boolean isSensitive, int mediaCount, String topics,
			boolean isPositive, boolean isNeutral, boolean isNegative) {

		this.tweetID = tweetID;
		this.twitterUser = twitterUser;
		this.twitterSearch = twitterSearch;
		this.dateCreatedAt = dateCreatedAt;
		this.timeCreatedAt = timeCreatedAt;
		this.sourceClient = sourceClient;
		this.tweetText = tweetText;
		this.tweetTextTranslated = tweetTextTranslated;
		this.geoLatitude = geoLatitude;
		this.geoLongitude = geoLongitude;
		this.hashtags = hashtags;
		this.mentions = mentions;
		this.retweetCount = retweetCount;
		this.isRetweet = isRetweet;
		this.languageCode = languageCode;
		this.placeCountry = placeCountry;
		this.placeName = placeName;
		this.urlCited = urlCited;
		this.isFavorited = isFavorited;
		this.favoritedCount = favoritedCount;
		this.replyToScreenName = replyToScreenName;
		this.replyToUserId = replyToUserId;
		this.replyToTweetId = replyToTweetId;
		this.originalRTTweetId = originalRTTweetId;
		this.isSensitive = isSensitive;
		this.mediaCount = mediaCount;
		this.topics = topics;
		this.isPositive = isPositive;
		this.isNeutral = isNeutral;
		this.isNegative = isNegative;
	}

	public TwitterData(boolean isRetweet, Long replyToTweetId) {
		this.isRetweet = isRetweet;
		this.replyToTweetId = replyToTweetId;
	}

	public TwitterData(Calendar timeCreatedAt, boolean isRetweet) {
		this.timeCreatedAt = timeCreatedAt;
		this.isRetweet = isRetweet;
	}

	public TwitterData(TwitterUser twitterUser, Calendar dateCreatedAt, String hashtags, String tweetText, Calendar timeCreatedAt, int retweetCount) {

		this.twitterUser = twitterUser;
		this.dateCreatedAt = dateCreatedAt;
		this.timeCreatedAt = timeCreatedAt;
		this.tweetText = tweetText;
		this.hashtags = hashtags;
		this.retweetCount = retweetCount;
	}

	public TwitterData(boolean isPositive, boolean isNeutral, boolean isNegative) {
		this.isPositive = isPositive;
		this.isNeutral = isNeutral;
		this.isNegative = isNegative;
	}

	public TwitterData(boolean isPositive, boolean isNeutral, boolean isNegative, String topics) {
		this.isPositive = isPositive;
		this.isNeutral = isNeutral;
		this.isNegative = isNegative;
		this.topics = topics;
	}

	public TwitterData(TwitterSearch twitterSearch, TwitterUser twitterUser, Long replyToUserId, Long originalRTTweetId, TwitterUser replyUser,
			TwitterUser rtUser) {
		this.twitterSearch = twitterSearch;
		this.twitterUser = twitterUser;
		this.replyToUserId = replyToUserId;
		this.originalRTTweetId = originalRTTweetId;
		this.replyUser = replyUser;
		this.rtUser = rtUser;
	}

	public long getTweetID() {
		return tweetID;
	}

	public void setTweetID(long tweetID) {
		this.tweetID = tweetID;
	}

	public TwitterUser getTwitterUser() {
		return twitterUser;
	}

	public void setTwitterUser(TwitterUser twitterUser) {
		this.twitterUser = twitterUser;
	}

	public TwitterSearch getTwitterSearch() {
		return twitterSearch;
	}

	public void setTwitterSearch(TwitterSearch twitterSearch) {
		this.twitterSearch = twitterSearch;
	}

	public java.util.Calendar getDateCreatedAt() {
		return dateCreatedAt;
	}

	public void setDateCreatedAt(java.util.Calendar dateCreatedAt) {
		this.dateCreatedAt = dateCreatedAt;
	}

	public java.util.Calendar getTimeCreatedAt() {
		return timeCreatedAt;
	}

	public void setTimeCreatedAt(java.util.Calendar timeCreatedAt) {
		this.timeCreatedAt = timeCreatedAt;
	}

	public String getSourceClient() {
		return sourceClient;
	}

	public void setSourceClient(String sourceClient) {
		this.sourceClient = sourceClient;
	}

	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public String getTweetTextTranslated() {
		return tweetTextTranslated;
	}

	public void setTweetTextTranslated(String tweetTextTranslated) {
		this.tweetTextTranslated = tweetTextTranslated;
	}

	public double getGeoLatitude() {
		return geoLatitude;
	}

	public void setGeoLatitude(double geoLatitude) {
		this.geoLatitude = geoLatitude;
	}

	public double getGeoLongitude() {
		return geoLongitude;
	}

	public void setGeoLongitude(double geoLongitude) {
		this.geoLongitude = geoLongitude;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}

	public String getMentions() {
		return mentions;
	}

	public void setMentions(String mentions) {
		this.mentions = mentions;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public void setRetweet(boolean isRetweet) {
		this.isRetweet = isRetweet;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getPlaceCountry() {
		return placeCountry;
	}

	public void setPlaceCountry(String placeCountry) {
		this.placeCountry = placeCountry;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getUrlCited() {
		return urlCited;
	}

	public void setUrlCited(String urlCited) {
		this.urlCited = urlCited;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}

	public int getFavoritedCount() {
		return favoritedCount;
	}

	public void setFavoritedCount(int favoritedCount) {
		this.favoritedCount = favoritedCount;
	}

	public String getReplyToScreenName() {
		return replyToScreenName;
	}

	public void setReplyToScreenName(String replyToScreenName) {
		this.replyToScreenName = replyToScreenName;
	}

	public Long getReplyToUserId() {
		return replyToUserId;
	}

	public void setReplyToUserId(Long replyToUserId) {
		this.replyToUserId = replyToUserId;
	}

	public Long getReplyToTweetId() {
		return replyToTweetId;
	}

	public void setReplyToTweetId(Long replyToTweetId) {
		this.replyToTweetId = replyToTweetId;
	}

	public Long getOriginalRTTweetId() {
		return originalRTTweetId;
	}

	public void setOriginalRTTweetId(Long originalRTTweetId) {
		this.originalRTTweetId = originalRTTweetId;
	}

	public boolean isSensitive() {
		return isSensitive;
	}

	public void setSensitive(boolean isSensitive) {
		this.isSensitive = isSensitive;
	}

	public int getMediaCount() {
		return mediaCount;
	}

	public void setMediaCount(int mediaCount) {
		this.mediaCount = mediaCount;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	public boolean isNeutral() {
		return isNeutral;
	}

	public void setNeutral(boolean isNeutral) {
		this.isNeutral = isNeutral;
	}

	public boolean isNegative() {
		return isNegative;
	}

	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}

	public TwitterUser getReplyUser() {
		return replyUser;
	}

	public void setReplyUser(TwitterUser replyUser) {
		this.replyUser = replyUser;
	}

	public TwitterUser getRtUser() {
		return rtUser;
	}

	public void setRtUser(TwitterUser rtUser) {
		this.rtUser = rtUser;
	}

	@Override
	public String toString() {
		return "TwitterData [tweetID=" + tweetID + ", twitterUser=" + twitterUser + ", twitterSearch=" + twitterSearch + ", dateCreatedAt=" + dateCreatedAt
				+ ", timeCreatedAt=" + timeCreatedAt + ", sourceClient=" + sourceClient + ", tweetText=" + tweetText + ", tweetTextTranslated="
				+ tweetTextTranslated + ", geoLatitude=" + geoLatitude + ", geoLongitude=" + geoLongitude + ", hashtags=" + hashtags + ", mentions=" + mentions
				+ ", retweetCount=" + retweetCount + ", isRetweet=" + isRetweet + ", languageCode=" + languageCode + ", placeCountry=" + placeCountry
				+ ", placeName=" + placeName + ", urlCited=" + urlCited + ", isFavorited=" + isFavorited + ", favoritedCount=" + favoritedCount
				+ ", replyToScreenName=" + replyToScreenName + ", replyToUserId=" + replyToUserId + ", replyToTweetId=" + replyToTweetId
				+ ", originalRTTweetId=" + originalRTTweetId + ", isSensitive=" + isSensitive + ", mediaCount=" + mediaCount + ", topics=" + topics
				+ ", isPositive=" + isPositive + ", isNeutral=" + isNeutral + ", isNegative=" + isNegative + "]";
	}

}
