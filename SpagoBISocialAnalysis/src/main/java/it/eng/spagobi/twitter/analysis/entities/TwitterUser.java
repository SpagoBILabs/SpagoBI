/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import it.eng.spagobi.twitter.analysis.utilities.AnalysisUtility;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "TWITTER_USERS")
public class TwitterUser {

	@Id
	@Column(name = "user_id")
	@NotNull
	private long userID;

	@Column(name = "username")
	@NotNull
	@Length(max = 45)
	private String username;

	@Column(name = "description")
	@Length(max = 200)
	private String description;

	@Column(name = "followers_count")
	@NotNull
	private int followersCount;

	@Column(name = "profile_image_source")
	@Length(max = 200)
	@NotNull
	private String profileImgSrc;

	@Column(name = "location")
	@Length(max = 200)
	private String location;

	@Column(name = "location_code")
	@Length(max = 45)
	private String locationCode;

	@Column(name = "language_code")
	@Length(max = 5)
	private String languageCode;

	@Column(name = "name")
	@Length(max = 45)
	@NotNull
	private String name;

	@Column(name = "time_zone")
	@Length(max = 200)
	private String timeZone;

	@Column(name = "tweets_count")
	@NotNull
	private int tweetsCount;

	@Column(name = "verified", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean verified = false;

	@Column(name = "following_count")
	@NotNull
	private int followingCount;

	@Column(name = "UTC_offset")
	private int utcOffset;

	@Column(name = "is_geo_enabled", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean isGeoEnabled = false;

	@Column(name = "listed_count")
	@NotNull
	private int listedCount;

	@Column(name = "start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Calendar endDate;

	@Transient
	private long nTweets;

	public TwitterUser() {

	}

	public TwitterUser(long userID, String username, String description, int followersCount, String profileImgSrc, String location, String locationCode,
			String languageCode, String name, String timeZone, int tweetsCount, boolean verified, int followingCount, int utcOffset, boolean isGeoEnabled,
			int listedCount, Calendar startDate, Calendar endDate) {

		this.userID = userID;
		this.username = username;
		this.description = AnalysisUtility.customEscapeString(description);
		this.followersCount = followersCount;
		this.profileImgSrc = profileImgSrc;
		this.location = location;
		this.locationCode = locationCode;
		this.languageCode = languageCode;
		this.name = name;
		this.timeZone = timeZone;
		this.tweetsCount = tweetsCount;
		this.verified = verified;
		this.followingCount = followingCount;
		this.utcOffset = utcOffset;
		this.isGeoEnabled = isGeoEnabled;
		this.listedCount = listedCount;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public TwitterUser(String username, String description, String profileImg, int followers) {
		this.username = username;
		this.description = AnalysisUtility.customEscapeString(description);
		this.profileImgSrc = profileImg;
		this.followersCount = followers;
	}

	public TwitterUser(long userID, int followersCount, long nTweets) {
		this.userID = userID;
		this.followersCount = followersCount;
		this.nTweets = nTweets;
	}

	public TwitterUser(long userID, int followersCount) {
		this.userID = userID;
		this.followersCount = followersCount;
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

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
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
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
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

	public java.util.Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Calendar startDate) {
		this.startDate = startDate;
	}

	public java.util.Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Calendar endDate) {
		this.endDate = endDate;
	}

	public long getnTweets() {
		return nTweets;
	}

	public void setnTweets(long nTweets) {
		this.nTweets = nTweets;
	}

	@Override
	public String toString() {
		return "TwitterUser [userID=" + userID + ", username=" + username + ", description=" + description + ", followersCount=" + followersCount
				+ ", profileImgSrc=" + profileImgSrc + ", location=" + location + ", locationCode=" + locationCode + ", languageCode=" + languageCode
				+ ", name=" + name + ", timeZone=" + timeZone + ", tweetsCount=" + tweetsCount + ", verified=" + verified + ", followingCount="
				+ followingCount + ", utcOffset=" + utcOffset + ", isGeoEnabled=" + isGeoEnabled + ", listedCount=" + listedCount + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}

}
