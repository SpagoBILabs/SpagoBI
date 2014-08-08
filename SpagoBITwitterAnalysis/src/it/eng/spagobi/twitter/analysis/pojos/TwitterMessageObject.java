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
package it.eng.spagobi.twitter.analysis.pojos;

import java.util.GregorianCalendar;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterMessageObject {

	// twitter_data fields
	private long tweetID = -1;
	private java.sql.Date dateCreatedAt;
	private java.sql.Timestamp timeCreatedAt;
	private String sourceClient = "";
	private String tweetText = "";
	private String tweetTextTranslated = "";
	private double geoLatitude;
	private double geoLongitude;
	private String hashtags = "";
	private String mentions = "";
	private int retweetCount = 0;;
	private boolean isRetweet = false;
	private String languageCode = "";
	private String placeCountry = "";
	private String placeName = "";
	private String urlCited = "";
	private boolean isFavorited = false;
	private int favoritedCount = 0;
	private String replyToScreenName = "";
	private String replyToUserId = "";
	private String replyToTweetId = "";
	private String originalRTTweetId = "";
	private boolean isSensitive = false;
	private int mediaCount = 0;

	// twitter_user fields
	private long userID = -1;
	private String username = "";
	private String description = "";
	private int followersCount = 0;;
	private String profileImgSrc = "";
	private String location = "";
	private String userLanguageCode = "";
	private String name = "";
	private String timeZone = "";
	private int tweetsCount = 0;
	private boolean isVerified = false;
	private int followingCount = 0;
	private int utcOffset = 0;
	private boolean isGeoEnabled = false;
	private int listedCount = 0;
	private java.sql.Date startDate;
	private java.sql.Date endDate;

	public TwitterMessageObject(Status tweet) {
		// set parameters for twitter_date fields

		tweetID = tweet.getId();

		// UTC time when this Tweet was
		// created.
		java.util.Date tempDate = tweet.getCreatedAt();
		dateCreatedAt = new java.sql.Date(tempDate.getTime());
		timeCreatedAt = new java.sql.Timestamp(tempDate.getTime());

		// Utility used to post the Tweet, as an
		// HTML-formatted string. Tweets from the
		// Twitter website have a source value of
		// web.
		sourceClient = tweet.getSource();

		tweetText = tweet.getText();
		// TODO translation for tweetTextTranslated

		// Represents the geographic location of
		// this Tweet as reported by the user or
		// client application.
		GeoLocation loc = tweet.getGeoLocation();

		if (loc != null) {
			geoLatitude = loc.getLongitude();
			geoLongitude = loc.getLatitude();
		} else {
			geoLatitude = 0.0;
			geoLongitude = 0.0;
		}

		// hashatags included in the tweet
		HashtagEntity[] hashtagsArray = tweet.getHashtagEntities();
		for (int i = 0; i < hashtagsArray.length; i++) {
			hashtags = hashtags + " #" + hashtagsArray[i].getText();
		}
		hashtags = hashtags.trim();

		UserMentionEntity[] mentionsArray = tweet.getUserMentionEntities();
		for (int i = 0; i < mentionsArray.length; i++) {
			mentions = mentions + " @" + mentionsArray[i].getText();
		}
		mentions = mentions.trim();

		// Check if the tweet is a retweet of another tweet
		if (tweet.getRetweetedStatus() != null) {
			isRetweet = true;
			retweetCount = 0;
		} else {
			isRetweet = false;
			// Number of times this Tweet has been retweeted.
			// This field is no longer capped at 99 and will not
			// turn into a String for "100+"
			retweetCount = tweet.getRetweetCount();
		}

		// When present, indicates a BCP 47 language identifier corresponding to
		// the
		// machine-detected language of the Tweet text, or "und" if no language
		// could be detected.
		languageCode = tweet.getLang();

		// When present, indicates that the tweet is associated (but not
		// necessarily originating from) a Place
		if (tweet.getPlace() != null) {
			placeCountry = tweet.getPlace().getCountry();
			placeName = tweet.getPlace().getFullName();
		} else {
			placeCountry = "";
			placeName = "";
		}

		// mentioned urls in tweet
		URLEntity urlEntities[] = tweet.getURLEntities();
		for (int i = 0; i < urlEntities.length; i++) {
			urlCited = urlCited + " " + urlEntities[i].getText();
		}
		urlCited = urlCited.trim();

		isFavorited = tweet.isFavorited();

		// count how many times tweet was favorited
		favoritedCount = tweet.getFavoriteCount();

		// if the tweet is a reply contains the screenName of the original
		// author
		replyToScreenName = tweet.getInReplyToScreenName();

		// If the represented Tweet is a reply, this field will contain the
		// string representation of the original Tweet's ID.
		if (tweet.getInReplyToUserId() == -1) {
			replyToUserId = null;
		} else {
			replyToUserId = String.valueOf(tweet.getInReplyToUserId());
		}
		// If the represented Tweet is a reply, this field will contain the
		// integer representation of the original Tweet's ID.
		if (tweet.getInReplyToStatusId() == -1) {
			replyToTweetId = null;
		} else {
			replyToTweetId = String.valueOf(tweet.getInReplyToStatusId());
		}
		if (tweet.isRetweet()) {
			// The id of the original tweet that was retweeted
			originalRTTweetId = String.valueOf(tweet.getRetweetedStatus().getId());
		} else {
			originalRTTweetId = null;
		}

		// Nullable. This field only surfaces when a tweet contains a link. The
		// meaning of the field doesn't pertain to the tweet content itself, but
		// instead it is an indicator that the URL contained in the tweet may
		// contain content or
		// media identified as sensitive content.
		isSensitive = tweet.isPossiblySensitive();

		// Count media entities uploaded with the tweet
		mediaCount = tweet.getMediaEntities().length;

		// set parameters for twitter_users fields

		if (tweet.getUser() != null) {

			User user = tweet.getUser();
			// Id of The user who posted this Tweet.
			userID = user.getId();

			// ScreenName of The user who posted this Tweet.
			username = user.getScreenName();

			description = user.getDescription();

			// Numbers of followers of the tweet's author
			followersCount = user.getFollowersCount();

			profileImgSrc = user.getProfileImageURL();

			// The user-defined location for this account's profile.
			// Not necessarily a location nor parseable.
			if (user.getLocation() != null) {
				location = user.getLocation();
			} else {
				location = "";
			}

			// The BCP 47 code for the Tweet's Author self-declared user
			// interface language. May or may not have anything to do
			// with the content of their Tweets.
			userLanguageCode = user.getLang();

			name = user.getName();

			// A string describing the Time Zone this Tweet's Author declares
			// themselves within.
			timeZone = user.getTimeZone();

			// The number of tweets (including retweets) issued by the Tweet's
			// Author.
			tweetsCount = user.getStatusesCount();

			// When true, indicates that the Tweet's Author has a verified
			// account
			isVerified = user.isVerified();

			// Numbers of account the tweet's author is following
			followingCount = user.getFriendsCount();

			// Tweet's Author offset from GMT/UTC in seconds
			utcOffset = user.getUtcOffset();

			// When true, indicates that the Tweet's Author has enabled the
			// possibility of geotagging their Tweets.
			isGeoEnabled = user.isGeoEnabled();

			// The number of public lists that this user is a member of.
			listedCount = tweet.getUser().getListedCount();

			// TODO inserire valori corretti per le date sull'utente
			startDate = new java.sql.Date(GregorianCalendar.getInstance().getTimeInMillis());
			endDate = new java.sql.Date(GregorianCalendar.getInstance().getTimeInMillis());
		}
	}

	public long getTweetID() {
		return tweetID;
	}

	public void setTweetID(long tweetID) {
		this.tweetID = tweetID;
	}

	public java.sql.Date getDateCreatedAt() {
		return dateCreatedAt;
	}

	public void setDateCreatedAt(java.sql.Date dateCreatedAt) {
		this.dateCreatedAt = dateCreatedAt;
	}

	public java.sql.Timestamp getTimeCreatedAt() {
		return timeCreatedAt;
	}

	public void setTimeCreatedAt(java.sql.Timestamp timeCreatedAt) {
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

	public String getReplyToUserId() {
		return replyToUserId;
	}

	public void setReplyToUserId(String replyToUserId) {
		this.replyToUserId = replyToUserId;
	}

	public String getReplyToTweetId() {
		return replyToTweetId;
	}

	public void setReplyToTweetId(String replyToTweetId) {
		this.replyToTweetId = replyToTweetId;
	}

	public String getOriginalRTTweetId() {
		return originalRTTweetId;
	}

	public void setOriginalRTTweetId(String originalRTTweetId) {
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

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public String getProfileImgSrc() {
		return profileImgSrc;
	}

	public void setProfileImgSrc(String profileImgSrc) {
		this.profileImgSrc = profileImgSrc;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUserLanguageCode() {
		return userLanguageCode;
	}

	public void setUserLanguageCode(String userLanguageCode) {
		this.userLanguageCode = userLanguageCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public int getTweetsCount() {
		return tweetsCount;
	}

	public void setTweetsCount(int tweetsCount) {
		this.tweetsCount = tweetsCount;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public int getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(int followingCount) {
		this.followingCount = followingCount;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}

	public boolean isGeoEnabled() {
		return isGeoEnabled;
	}

	public void setGeoEnabled(boolean isGeoEnabled) {
		this.isGeoEnabled = isGeoEnabled;
	}

	public int getListedCount() {
		return listedCount;
	}

	public void setListedCount(int listedCount) {
		this.listedCount = listedCount;
	}

	public java.sql.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.sql.Date startDate) {
		this.startDate = startDate;
	}

	public java.sql.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.sql.Date endDate) {
		this.endDate = endDate;
	}

}
