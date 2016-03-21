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

import java.util.Calendar;
import java.util.List;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 * 
 */
public class TwitterTopTweetsPojo {
	private String usernameFromDb;
	private String createDateFromDb;
	private String profileImgSrcFromDB;
	private String hashtagsFromDb;
	private String tweetText;
	private List<String> hashtags;
	private Calendar calendar;
	private int userFollowersCount;
	private String location;
	private int counterRTs;

	public TwitterTopTweetsPojo() {

	}

	public TwitterTopTweetsPojo(String user, String cdate, String profileImg, String hashtags, String text, List<String> hash, Calendar c, int ufc, int counterRTs) {
		this.usernameFromDb = user;
		this.createDateFromDb = cdate;
		this.profileImgSrcFromDB = profileImg;
		this.hashtagsFromDb = hashtags;
		this.tweetText = text;
		this.hashtags = hash;
		this.calendar = c;
		this.userFollowersCount = ufc;
		this.counterRTs = counterRTs;
	}

	public TwitterTopTweetsPojo(String user, String profileImg, int ufc) {
		this.usernameFromDb = user;
		this.profileImgSrcFromDB = profileImg;
		this.userFollowersCount = ufc;
	}

	public TwitterTopTweetsPojo(String location) {
		this.location = location;
	}

	public String getUsernameFromDb() {
		return usernameFromDb;
	}

	public void setUsernameFromDb(String usernameFromDb) {
		this.usernameFromDb = usernameFromDb;
	}

	public String getCreateDateFromDb() {
		return createDateFromDb;
	}

	public void setCreateDateFromDb(String createDateFromDb) {
		this.createDateFromDb = createDateFromDb;
	}

	public String getProfileImgSrcFromDB() {
		return profileImgSrcFromDB;
	}

	public void setProfileImgSrcFromDB(String profileImgSrcFromDB) {
		this.profileImgSrcFromDB = profileImgSrcFromDB;
	}

	public String getHashtagsFromDb() {
		return hashtagsFromDb;
	}

	public void setHashtagsFromDb(String hashtagsFromDb) {
		this.hashtagsFromDb = hashtagsFromDb;
	}

	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public int getUserFollowersCount() {
		return userFollowersCount;
	}

	public void setUserFollowersCount(int userFollowersCount) {
		this.userFollowersCount = userFollowersCount;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getCounterRTs() {
		return counterRTs;
	}

	public void setCounterRTs(int counterRTs) {
		this.counterRTs = counterRTs;
	}

}
