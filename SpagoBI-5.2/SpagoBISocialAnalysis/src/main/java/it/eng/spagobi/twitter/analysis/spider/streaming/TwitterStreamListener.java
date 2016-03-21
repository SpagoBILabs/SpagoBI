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
package it.eng.spagobi.twitter.analysis.spider.streaming;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;

import org.apache.log4j.Logger;

import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.UserMentionEntity;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterStreamListener implements StatusListener {

	private final ITwitterCache cache;
	private static HashtagEntity hashtag[] = null;
	private static UserMentionEntity mentions[] = null;
	private String keyword = "";
	static final Logger logger = Logger.getLogger(TwitterStreamListener.class);
	long searchID;

	public TwitterStreamListener(ITwitterCache cache, long searchID) {
		this.cache = cache;
		this.searchID = searchID;
	}

	// TODO pensare ad una eventuale gestione degli altri eventi (oltre a quello
	// OnStatus)

	/*
	 * (non-Javadoc)
	 * 
	 * @see twitter4j.StreamListener#onException(java.lang.Exception)
	 */
	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see twitter4j.StatusListener#onDeletionNotice(twitter4j.StatusDeletionNotice)
	 */
	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// System.out.println("Got a status deletion notice id:"
		// + statusDeletionNotice.getStatusId());
		logger.debug("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see twitter4j.StatusListener#onScrubGeo(long, long)
	 */
	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		// System.out.println("Got scrub_geo event userId:" + userId
		// + " upToStatusId:" + upToStatusId);
		logger.debug("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see twitter4j.StatusListener#onStallWarning(twitter4j.StallWarning)
	 */
	@Override
	public void onStallWarning(StallWarning warning) {
		// System.out.println("Stall WARNING, Code: " + warning.getCode()
		// + ", message:" + warning.getMessage());
		logger.debug("Stall WARNING, Code: " + warning.getCode() + ", message:" + warning.getMessage());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see twitter4j.StatusListener#onStatus(twitter4j.Status)
	 */
	@Override
	public void onStatus(Status tweet) {

		// System.out.println("@" + tweet.getUser().getScreenName() + " - "
		// + tweet.getText());
		logger.debug("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());

		hashtag = tweet.getHashtagEntities();
		mentions = tweet.getUserMentionEntities();
		try {
			cache.saveTweet(tweet, searchID);
		} catch (Exception e) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// System.out.println("Got track limitation notice:"
		// + numberOfLimitedStatuses);
		logger.debug("Got track limitation notice:" + numberOfLimitedStatuses);

	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
