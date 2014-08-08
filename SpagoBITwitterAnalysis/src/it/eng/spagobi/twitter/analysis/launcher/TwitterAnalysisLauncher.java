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
package it.eng.spagobi.twitter.analysis.launcher;

import it.eng.spagobi.bitly.analysis.utilities.BitlyCounterClicksUtility;
import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheFactory;
import it.eng.spagobi.twitter.analysis.spider.search.TwitterSearchAPISpider;
import it.eng.spagobi.twitter.analysis.spider.streaming.TwitterStreamingAPISpider;
import it.eng.spagobi.twitter.analysis.utilities.TwitterUserInfoUtility;
import twitter4j.Query.ResultType;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterAnalysisLauncher {

	// TODO: da questa classe posso decidere qualche tipologia di ricerca
	// lanciare e settare alcuni parametri (es: keyword, lingua ecc)

	String keywords; // dovrebbe essere obbligatorio
	String languageCode;
	String searchType; // StreamingAPI o SearchAPI, dovrebbe essere obbligatorio
	String searchLabel;
	String links;
	String accounts;
	ITwitterCache cache;

	// TODO: esempio di costruttore
	public TwitterAnalysisLauncher(String keyword, String languageCode, String searchType, String searchLabel, String links, String accounts, String dbType) {
		this.keywords = keyword;
		this.languageCode = languageCode;
		this.searchType = searchType;
		this.searchLabel = searchLabel;
		this.links = links;
		this.accounts = accounts;
		this.cache = initCache(dbType);
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords
	 *            the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode
	 *            the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * @return the searchType
	 */
	public String getSearchType() {
		return searchType;
	}

	/**
	 * @param searchType
	 *            the searchType to set
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public ITwitterCache initCache(String dbType) {
		TwitterCacheFactory twitterCacheFactory; // factory di cache con
													// specifiche
													// implementazioni
		twitterCacheFactory = new TwitterCacheFactory();
		return twitterCacheFactory.getCache(dbType);
	}

	public void startSearch() {

		long searchID = cache.insertTwitterSearch(keywords, this.searchType, this.searchLabel);

		if (this.searchType.equalsIgnoreCase("SearchAPI")) {
			TwitterSearchAPISpider searchAPI = new TwitterSearchAPISpider();
			searchAPI.setCache((this.cache));
			searchAPI.setLanguage(this.languageCode);
			searchAPI.setResultType(ResultType.recent);
			searchAPI.setQuery(this.keywords);
			searchAPI.setSearchID(searchID);

			searchAPI.collectTweets();

		} else if (this.searchType.equalsIgnoreCase("StreamingAPI")) {
			TwitterStreamingAPISpider streamingAPI = new TwitterStreamingAPISpider();
			streamingAPI.setCache(this.cache);
			streamingAPI.setSearchID(searchID);

			// TODO pensare a quale dovrebbe essere il carattere per lo split,
			// ricordando la prensenza di ricerche composte

			// TODO gestire la presenza di valori null

			String[] keywordsArr = keywords.split(" ");
			streamingAPI.setTrack(keywordsArr);
			if (languageCode != null && !languageCode.equals("")) {
				String[] languageCodeArr = languageCode.split(" ");
				streamingAPI.setLanguage(languageCodeArr);
			} else {
				streamingAPI.setLanguage(null);
			}
			streamingAPI.collectTweets();
		}

		if (links != null && !links.equals("")) {
			BitlyCounterClicksUtility bitlyUtil = new BitlyCounterClicksUtility(links, searchID);

			bitlyUtil.startBitlyAnalysis();
		}

		if (accounts != null && !accounts.equals("")) {
			TwitterUserInfoUtility userUtil = new TwitterUserInfoUtility(searchID);

			accounts = accounts.trim();
			String[] accountArr = accounts.split(",");

			for (int i = 0; i < accountArr.length; i++) {
				userUtil.saveFollowersCount(accountArr[i]);
			}
		}

	}
}
