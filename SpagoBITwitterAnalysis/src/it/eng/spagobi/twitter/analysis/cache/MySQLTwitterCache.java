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
package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkCategoryPojo;
import it.eng.spagobi.analysis.bitly.pojos.BitlyLinkPojo;
import it.eng.spagobi.twitter.analysis.pojos.TwitterAccountToMonitorPojo;
import it.eng.spagobi.twitter.analysis.pojos.TwitterMessageObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import org.apache.log4j.Logger;

import com.sun.rowset.CachedRowSetImpl;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class MySQLTwitterCache extends AbstractTwitterCache {

	private static final Logger logger = Logger.getLogger(MySQLTwitterCache.class);
	private Connection conn = null;

	public MySQLTwitterCache(String url, String driver, String userName, String password) {
		super(url, driver, userName, password);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.eng.spagobi.twitter.analysis.cache.ITwitterCache#openConnection()
	 */
	@Override
	public Connection openConnection() {
		try {
			Class.forName(getDriver()).newInstance();
			conn = DriverManager.getConnection(getUrl(), getUserName(), getPassword());
			logger.debug("**** Connected to the database");
			// System.out.println("**** Connected to the database");
		} catch (InstantiationException e) {
			// System.out.println("**** ERROR Connecting to the database: "+e);
			logger.debug("**** ERROR Connecting to the database: " + e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// System.out.println("**** ERROR Connecting to the database: "+e);
			logger.debug("**** ERROR Connecting to the database: " + e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// System.out.println("**** ERROR Connecting to the database: "+e);
			logger.debug("**** ERROR Connecting to the database: " + e);
			e.printStackTrace();
		} catch (SQLException e) {
			// System.out.println("**** ERROR Connecting to the database: "+e);
			logger.debug("**** ERROR Connecting to the database: " + e);
			e.printStackTrace();
		}
		return conn;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.eng.spagobi.twitter.analysis.cache.ITwitterCache#closeConnection()
	 */
	@Override
	public void closeConnection() {
		try {
			if ((conn != null) && (!conn.isClosed())) {
				conn.close();
			}
		} catch (SQLException e) {
			// System.out.println("**** ERROR Disconnecting to the database: "+e);
			logger.debug("**** ERROR Disconnecting to the database: " + e);
			e.printStackTrace();
		}
		// System.out.println("**** Disconnected from database");
		logger.debug("**** Disconnected from database");
	}

	@Override
	public long insertTwitterSearch(String keywords, String searchType, String searchLabel) {

		PreparedStatement statement = null;

		ResultSet generatedKeys = null;

		long searchID = -1;

		try {
			// TODO ragionare sulle possibili ottimizzazioni della
			// openConnection nella fase di inserimento ricerca, tweets, utenti,
			// etc.
			conn = openConnection();

			String insertSearchSQL = "INSERT INTO `twitterdb`.`twitter_search`" + " (`label`,`keywords`, `creation_date`,`frequency`,`type`)" + " VALUES (?,?,?,?,?)";

			statement = conn.prepareStatement(insertSearchSQL, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, searchLabel);
			statement.setString(2, keywords);
			statement.setDate(3, new java.sql.Date(GregorianCalendar.getInstance().getTimeInMillis()));
			statement.setString(4, "day");
			statement.setString(5, searchType);

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {

				throw new SQLException("Creating user failed, no rows affected.");
			}

			generatedKeys = statement.getGeneratedKeys();

			if (generatedKeys.next()) {

				searchID = generatedKeys.getLong(1);
			}

			closeConnection();

		} catch (Exception e) {
			logger.debug("**** connection failed: " + e);
			// System.out.println("**** connection failed: " +e);
		}

		return searchID;
	}

	@Override
	public void insertTweet(TwitterMessageObject twitterMessage, long searchID) {

		java.sql.PreparedStatement st;
		Statement stmt;

		try {

			stmt = conn.createStatement();
			ResultSet res1 = stmt.executeQuery("SELECT tweet_id from `twitter_data` where tweet_id = '" + twitterMessage.getTweetID() + "' and search_id = '" + searchID + "'");

			if (!res1.next()) {
				st = conn
						.prepareStatement("INSERT INTO `twitterdb`.`twitter_data` "
								+ "(`tweet_id`,`user_id`,`search_id`,`date_created_at`,`time_created_at`,`source_client`,`tweet_text`,`tweet_text_translated`,`geo_latitude`,`geo_longitude`,`hashtags`,`mentions`,`retweet_count`,`is_retweet`,`language_code`,`place_country`,`place_name`,`url_cited`,`is_favorited`,`favorited_count`,`reply_to_screen_name`,`reply_to_user_id`,`reply_to_tweet_id`,`original_RT_tweet_id`,`is_sensitive`,`media_count`)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				st.setLong(1, twitterMessage.getTweetID());
				st.setLong(2, twitterMessage.getUserID());
				st.setLong(3, searchID);
				st.setDate(4, twitterMessage.getDateCreatedAt());
				st.setTimestamp(5, twitterMessage.getTimeCreatedAt());
				st.setString(6, twitterMessage.getSourceClient());
				st.setString(7, twitterMessage.getTweetText());
				st.setString(8, twitterMessage.getTweetTextTranslated());
				st.setDouble(9, twitterMessage.getGeoLatitude());
				st.setDouble(10, twitterMessage.getGeoLongitude());
				st.setString(11, twitterMessage.getHashtags());
				st.setString(12, twitterMessage.getMentions());
				st.setInt(13, twitterMessage.getRetweetCount());
				st.setBoolean(14, twitterMessage.isRetweet());
				st.setString(15, twitterMessage.getLanguageCode());
				st.setString(16, twitterMessage.getPlaceCountry());
				st.setString(17, twitterMessage.getPlaceName());
				st.setString(18, twitterMessage.getUrlCited());
				st.setBoolean(19, twitterMessage.isFavorited());
				st.setLong(20, twitterMessage.getFavoritedCount());
				st.setString(21, twitterMessage.getReplyToScreenName());
				st.setString(22, twitterMessage.getReplyToUserId());
				st.setString(23, twitterMessage.getReplyToTweetId());
				st.setString(24, twitterMessage.getOriginalRTTweetId());
				st.setBoolean(25, twitterMessage.isSensitive());
				st.setInt(26, twitterMessage.getMediaCount());

				st.executeUpdate();
			}

			// close();

		} catch (Exception e) {
			logger.debug("**** connection failed: " + e);
			// System.out.println("**** connection failed: " +e);
		}

	}

	@Override
	public void insertBitlyAnalysis(BitlyLinkPojo linkPojo, List<BitlyLinkCategoryPojo> linkCategoryPojos, long searchID) {

		PreparedStatement statement = null;
		PreparedStatement statementLinkCategory = null;

		ResultSet generatedKeys = null;

		conn = openConnection();

		try {
			// String link_insert =
			// "INSERT INTO `twitterdb`.`twitter_links_to_monitor`" +
			// "(`search_id`,`link`,`clicks_count`)" + " VALUES (?,?,?)";
			String link_insert = "INSERT INTO `twitterdb`.`twitter_links_to_monitor`" + "(`search_id`,`link`,`clicks_count`)" + " VALUES (?,?,?)";

			statement = conn.prepareStatement(link_insert, Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, searchID);
			statement.setString(2, linkPojo.getLink());
			statement.setInt(3, linkPojo.getCounter_clicks());

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating user failed, no rows affected.");
			}

			generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {

				long link_id = generatedKeys.getLong(1);

				for (BitlyLinkCategoryPojo linkCategoryPojo : linkCategoryPojos) {

					String linkCategory_insert = "INSERT INTO `twitterdb`.`twitter_link_to_monitor_category`" + "(`link_id`,`type`,`category`,`clicks_count`)"
							+ " VALUES (?,?,?,?)";

					statementLinkCategory = conn.prepareStatement(linkCategory_insert);
					statementLinkCategory.setLong(1, link_id);
					statementLinkCategory.setString(2, linkCategoryPojo.getType());
					statementLinkCategory.setString(3, linkCategoryPojo.getCategory());
					statementLinkCategory.setInt(4, linkCategoryPojo.getClicks_count());

					statementLinkCategory.executeUpdate();
				}

			} else {
				throw new SQLException("Creating user failed, no generated key obtained.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (generatedKeys != null)
				try {
					generatedKeys.close();
				} catch (SQLException logOrIgnore) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException logOrIgnore) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException logOrIgnore) {
				}
		}
	}

	@Override
	public void insertTwitterUser(TwitterMessageObject twitterMessage) {

		java.sql.PreparedStatement st;
		Statement stmt;

		try {

			stmt = conn.createStatement();
			ResultSet res1 = stmt.executeQuery("SELECT user_id from `twitter_users` where user_id = '" + twitterMessage.getUserID() + "'");

			if (!res1.next()) {
				st = conn
						.prepareStatement("INSERT INTO `twitterdb`.`twitter_users`"
								+ " (`user_id`,`username`, `description`,`followers_count`,`profile_image_source`,`location`,`location_code`,`language_code`,`name`,`time_zone`,`tweets_count`,`verified`,`following_count`,`UTC_offset`,`is_geo_enabled`,`listed_count`,`start_date`,`end_date`)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				st.setLong(1, twitterMessage.getUserID());
				st.setString(2, twitterMessage.getUsername());
				st.setString(3, twitterMessage.getDescription());
				st.setInt(4, twitterMessage.getFollowersCount());
				st.setString(5, twitterMessage.getProfileImgSrc());
				st.setString(6, twitterMessage.getLocation());

				String locationCode = findCountryCodeFromUserLocation(twitterMessage.getLocation(), twitterMessage.getTimeZone());
				st.setString(7, locationCode);

				st.setString(8, twitterMessage.getUserLanguageCode());
				st.setString(9, twitterMessage.getName());
				st.setString(10, twitterMessage.getTimeZone());
				st.setInt(11, twitterMessage.getTweetsCount());
				st.setBoolean(12, twitterMessage.isVerified());
				st.setInt(13, twitterMessage.getFollowingCount());
				st.setInt(14, twitterMessage.getUtcOffset());
				st.setBoolean(15, twitterMessage.isGeoEnabled());
				st.setInt(16, twitterMessage.getListedCount());
				st.setDate(17, twitterMessage.getStartDate());
				st.setDate(18, twitterMessage.getEndDate());

				st.executeUpdate();
			}

			// close();

		} catch (Exception e) {
			logger.debug("**** connection failed: " + e);
			// System.out.println("**** connection failed: " +e);
		}

	}

	@Override
	public void insertAccountToMonitor(TwitterAccountToMonitorPojo accountToMonitor) {

		try {

			conn = openConnection();

			java.sql.PreparedStatement st = conn.prepareStatement("INSERT INTO `twitterdb`.`twitter_accounts_to_monitor`"
					+ " (`search_id`,`account_name`,`followers_count`,`timestamp`)" + " VALUES (?,?,?,?)");
			st.setLong(1, accountToMonitor.getSearchID());
			st.setString(2, accountToMonitor.getUsername());
			st.setInt(3, accountToMonitor.getFollowers());
			st.setTimestamp(4, accountToMonitor.getTimestamp());

			st.executeUpdate();

			closeConnection();
		} catch (Exception e) {
			logger.debug("**** connection failed: " + e);
			// System.out.println("**** connection failed: " +e);
		}
	}

	@Override
	public CachedRowSet runQuery(String sqlQuery) {

		CachedRowSet rowset = null;

		try {

			conn = openConnection();

			rowset = new CachedRowSetImpl();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sqlQuery);
			rowset.populate(rs);

			closeConnection();

		} catch (Exception e) {
			System.out.println("**** connection failed: " + e);
		}

		return rowset;

	}
}
