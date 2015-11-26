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

import it.eng.spagobi.twitter.analysis.entities.TwitterData;
import it.eng.spagobi.twitter.analysis.entities.TwitterUser;
import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;

import java.util.Calendar;
import java.util.GregorianCalendar;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterMessageObject {

	// twitter_data fields
	private long tweetID = -1;
	private final java.sql.Date dateCreatedAt;
	private final java.sql.Timestamp timeCreatedAt;
	private String sourceClient = "";
	private String tweetText = "";
	private final String tweetTextTranslated = "";
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
	private Long replyToUserId;
	private Long replyToTweetId;
	private Long originalRTTweetId;
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

	private final TwitterUser twitterUser;
	private final TwitterData twitterData;

	public TwitterMessageObject(Status tweet) {

		this.twitterUser = new TwitterUser();
		this.twitterData = new TwitterData();

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

		// Check if the tweet is a retweet of another tweet
		if (tweet.getRetweetedStatus() != null) {
			isRetweet = true;
			retweetCount = 0;
			if (tweet.getRetweetedStatus().getUser() != null) {
				tweetText = "RT @" + tweet.getRetweetedStatus().getUser().getScreenName() + ": " + tweet.getRetweetedStatus().getText();
			} else {
				tweetText = "RT: " + tweet.getRetweetedStatus().getText();
			}
		} else {
			isRetweet = false;
			// Number of times this Tweet has been retweeted.
			// This field is no longer capped at 99 and will not
			// turn into a String for "100+"
			retweetCount = tweet.getRetweetCount();
			tweetText = tweet.getText();
		}

		// TODO: check regex emoji
		tweetText = AnalysisUtility.deleteEmoji(tweetText);

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
			replyToUserId = tweet.getInReplyToUserId();
		}
		// If the represented Tweet is a reply, this field will contain the
		// integer representation of the original Tweet's ID.
		if (tweet.getInReplyToStatusId() == -1) {
			replyToTweetId = null;
		} else {
			replyToTweetId = tweet.getInReplyToStatusId();
		}
		if (tweet.isRetweet()) {
			// The id of the original tweet that was retweeted
			originalRTTweetId = tweet.getRetweetedStatus().getId();
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

	public long getUserID() {
		return userID;
	}

	/********** Return objects for persistence *****************/

	public TwitterUser getTwitterUser() {

		this.twitterUser.setUserID(this.userID);
		this.twitterUser.setUsername(this.username);
		this.twitterUser.setDescription(this.description);
		this.twitterUser.setFollowersCount(this.followersCount);
		this.twitterUser.setProfileImgSrc(this.profileImgSrc);
		this.twitterUser.setLocation(this.location);
		this.twitterUser.setLanguageCode(this.userLanguageCode);
		this.twitterUser.setName(this.name);
		this.twitterUser.setTimeZone(this.timeZone);
		this.twitterUser.setTweetsCount(this.tweetsCount);
		this.twitterUser.setVerified(this.isVerified);
		this.twitterUser.setFollowingCount(this.followingCount);
		this.twitterUser.setUtcOffset(this.utcOffset);
		this.twitterUser.setGeoEnabled(this.isGeoEnabled);
		this.twitterUser.setListedCount(this.listedCount);
		this.twitterUser.setStartDate(GregorianCalendar.getInstance());
		this.twitterUser.setEndDate(GregorianCalendar.getInstance());

		return twitterUser;
	}

	public TwitterData getTwitterData() {

		this.twitterData.setTweetID(this.tweetID);
		this.twitterData.setTwitterUser(this.twitterUser);

		Calendar createAtDate = GregorianCalendar.getInstance();
		createAtDate.setTime(dateCreatedAt);

		this.twitterData.setDateCreatedAt(createAtDate);

		Calendar createdAtTime = GregorianCalendar.getInstance();
		createdAtTime.setTime(timeCreatedAt);

		this.twitterData.setTimeCreatedAt(createdAtTime);

		this.twitterData.setSourceClient(this.sourceClient);
		this.twitterData.setTweetText(this.tweetText);
		this.twitterData.setTweetTextTranslated(this.tweetTextTranslated);
		this.twitterData.setGeoLatitude(this.geoLatitude);
		this.twitterData.setGeoLongitude(this.geoLongitude);
		this.twitterData.setHashtags(this.hashtags);
		this.twitterData.setMentions(this.mentions);
		this.twitterData.setRetweetCount(this.retweetCount);
		this.twitterData.setRetweet(this.isRetweet);
		this.twitterData.setLanguageCode(this.languageCode);
		this.twitterData.setPlaceCountry(this.placeCountry);
		this.twitterData.setPlaceName(this.placeName);
		this.twitterData.setUrlCited(this.urlCited);
		this.twitterData.setFavorited(this.isFavorited);
		this.twitterData.setFavoritedCount(this.favoritedCount);
		this.twitterData.setReplyToScreenName(this.replyToScreenName);
		this.twitterData.setReplyToUserId(this.replyToUserId);
		this.twitterData.setReplyToTweetId(this.replyToTweetId);
		this.twitterData.setOriginalRTTweetId(this.originalRTTweetId);
		this.twitterData.setSensitive(this.isSensitive);
		this.twitterData.setMediaCount(this.mediaCount);

		return twitterData;
	}

}
