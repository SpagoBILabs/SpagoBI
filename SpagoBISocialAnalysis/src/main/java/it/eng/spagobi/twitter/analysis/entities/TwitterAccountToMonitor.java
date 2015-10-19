/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Entity
@Table(name = "TWITTER_ACCOUNTS_TO_MONITOR")
public class TwitterAccountToMonitor {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TWITTER_ACCOUNTS_TO_MONITOR_GEN")
	@TableGenerator(name = "TWITTER_ACCOUNTS_TO_MONITOR_GEN", allocationSize = 1, initialValue = 1, table = "TWITTER_HIBERNATE_SEQUENCES", pkColumnName = "SEQUENCE_NAME", valueColumnName = "NEXT_VAL", pkColumnValue = "TWITTER_ACCOUNTS_TO_MONITOR")
	@Column(name = "id")
	@NotNull
	private long id;

	@OneToOne
	@JoinColumn(name = "search_id")
	@NotNull
	private TwitterSearch twitterSearch;

	@Column(name = "account_name")
	@NotNull
	@Length(max = 45)
	private String accountName;

	@Column(name = "followers_count")
	@NotNull
	private int followersCount;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar timestamp;

	public TwitterAccountToMonitor() {

	}

	public TwitterAccountToMonitor(long id, TwitterSearch twitterSearch, String accountName, int followersCount, Calendar timestamp) {

		this.id = id;
		this.twitterSearch = twitterSearch;
		this.accountName = accountName;
		this.followersCount = followersCount;
		this.timestamp = timestamp;
	}

	public TwitterAccountToMonitor(int followersCount, Calendar timestamp) {

		this.followersCount = followersCount;
		this.timestamp = timestamp;

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TwitterSearch getTwitterSearch() {
		return twitterSearch;
	}

	public void setTwitterSearch(TwitterSearch twitterSearch) {
		this.twitterSearch = twitterSearch;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public java.util.Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.util.Calendar timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TwitterAccountToMonitor [id=" + id + ", twitterSearch=" + twitterSearch + ", accountName=" + accountName + ", followersCount=" + followersCount
				+ ", timestamp=" + timestamp + "]";
	}

}
