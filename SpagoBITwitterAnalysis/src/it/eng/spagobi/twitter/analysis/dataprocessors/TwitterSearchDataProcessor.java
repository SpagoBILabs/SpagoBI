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
package it.eng.spagobi.twitter.analysis.dataprocessors;

import it.eng.spagobi.twitter.analysis.cache.ITwitterCache;
import it.eng.spagobi.twitter.analysis.cache.TwitterCacheFactory;
import it.eng.spagobi.twitter.analysis.pojos.TwitterSearchPojo;

import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterSearchDataProcessor {

	private final ITwitterCache twitterCache = new TwitterCacheFactory().getCache("mysql");

	public List<TwitterSearchPojo> getTwitterSearchList() {

		List<TwitterSearchPojo> searchList = new ArrayList<TwitterSearchPojo>();

		String sqlQuery = "SELECT * from twitter_search";

		try {

			CachedRowSet rs = twitterCache.runQuery(sqlQuery);

			if (rs != null) {

				while (rs.next()) {

					long searchID = rs.getLong("search_id");
					String label = rs.getString("label");
					String keywords = rs.getString("keywords");
					java.sql.Date creationDate = rs.getDate("creation_date");
					java.sql.Timestamp lastActivationTime = rs.getTimestamp("last_activation_time");
					String frequency = rs.getString("frequency");
					String type = rs.getString("type");

					TwitterSearchPojo searchPojo = new TwitterSearchPojo(searchID, label, keywords, creationDate, lastActivationTime, frequency, type);
					System.out.println(searchPojo);
					searchList.add(searchPojo);
				}
			}

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return searchList;
	}

}
