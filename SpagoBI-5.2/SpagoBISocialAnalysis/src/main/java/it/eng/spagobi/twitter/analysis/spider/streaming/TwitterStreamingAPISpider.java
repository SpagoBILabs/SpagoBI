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
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class TwitterStreamingAPISpider {

	FilterQuery filterQuery;
	ITwitterCache cache;
	String[] keywords;
	String[] languageCodes;
	long searchID;

	public void setCache(ITwitterCache cache) {
		this.cache = cache;
	}

	public void setTrack(String[] keywords) {
		this.keywords = keywords;
	}

	public void setLanguage(String[] languageCodes) {
		this.languageCodes = languageCodes;
	}

	public void setFollow(Long[] userIds) {
		// TODO: setta il filtro sugli utenti da seguire
	}

	public void setLocation(Double[][] geoCoordinates) {
		// TODO: setta il filtro sui luoghi
	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	public void collectTweets() {
		// Listen to the TwitterStream via StreamingAPI
		// TwitterStream twitterStream = new
		// TwitterStreamFactory().getInstance();
		TwitterStream twitterStream = TwitterStreamFactory.getSingleton();

		// Initialize my listener
		TwitterStreamListener twitterStreamListener = new TwitterStreamListener(this.cache, searchID);
		// twitterStreamListener.setKeyword(joinStrings(keywords));
		twitterStream.addListener(twitterStreamListener);

		this.filterQuery = new FilterQuery();
		// keywords or hashtags to search inside tweets
		this.filterQuery.track(keywords);
		if (languageCodes != null) {
			filterQuery.language(languageCodes);
		}
		twitterStream.filter(filterQuery);

	}

	// public void closeTwitterStream() {
	//
	// TwitterStream twitterStream = TwitterStreamFactory.getSingleton();
	//
	// // twitterStream.clearListeners();
	// twitterStream.cleanUp();
	// // twitterStream.shutdown();
	//
	// this.cache.stopStreamingSearch();
	// }

	private String joinStrings(String[] arrayString) {
		StringBuilder builder = new StringBuilder();

		for (String string : arrayString) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(string);
		}

		return builder.toString();
	}
}
