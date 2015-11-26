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
@Table(name = "TWITTER_LINKS_TO_MONITOR")
public class TwitterLinkToMonitor {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TWITTER_LINKS_TO_MONITOR_GEN")
	@TableGenerator(name = "TWITTER_LINKS_TO_MONITOR_GEN", allocationSize = 1, initialValue = 1, table = "TWITTER_HIBERNATE_SEQUENCES", pkColumnName = "SEQUENCE_NAME", valueColumnName = "NEXT_VAL", pkColumnValue = "TWITTER_LINKS_TO_MONITOR")
	@Column(name = "id")
	@NotNull
	private long id;

	@OneToOne
	@JoinColumn(name = "search_id")
	@NotNull
	private TwitterSearch twitterSearch;

	@Column(name = "link")
	@NotNull
	@Length(max = 45)
	private String link;

	@Column(name = "long_url")
	@Length(max = 400)
	private String longUrl;

	@Column(name = "clicks_count")
	@NotNull
	private int clicksCount;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar timestamp;

	public TwitterLinkToMonitor() {

	}

	public TwitterLinkToMonitor(long id, TwitterSearch twitterSearch, String link, int clicksCount, Calendar timestamp, String longUrl) {

		this.id = id;
		this.twitterSearch = twitterSearch;
		this.link = link;
		this.clicksCount = clicksCount;
		this.timestamp = timestamp;
		this.longUrl = longUrl;
	}

	public TwitterLinkToMonitor(String longUrl, int clicksCount, Calendar timestamp) {

		this.longUrl = longUrl;
		this.clicksCount = clicksCount;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getClicksCount() {
		return clicksCount;
	}

	public void setClicksCount(int clicksCount) {
		this.clicksCount = clicksCount;
	}

	public java.util.Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.util.Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}

	@Override
	public String toString() {
		return "TwitterLinkToMonitor [id=" + id + ", twitterSearch=" + twitterSearch + ", link=" + link + ", longUrl=" + longUrl + ", clicksCount="
				+ clicksCount + ", timestamp=" + timestamp + "]";
	}

}
