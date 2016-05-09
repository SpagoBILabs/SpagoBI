/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import it.eng.spagobi.twitter.analysis.enums.MonitorRepeatTypeEnum;
import it.eng.spagobi.twitter.analysis.enums.UpToTypeEnum;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Entity
@Table(name = "TWITTER_MONITOR_SCHEDULER")
public class TwitterMonitorScheduler {

	@Id
	@Column(name = "id")
	@NotNull
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TWITTER_MONITOR_SCHEDULER_GEN")
	@TableGenerator(name = "TWITTER_MONITOR_SCHEDULER_GEN", allocationSize = 1, initialValue = 1, table = "TWITTER_HIBERNATE_SEQUENCES", pkColumnName = "SEQUENCE_NAME", valueColumnName = "NEXT_VAL", pkColumnValue = "TWITTER_MONITOR_SCHEDULER")
	private long id;

	@OneToOne
	@JoinColumn(name = "search_id")
	@NotNull
	private TwitterSearch twitterSearch;

	@Column(name = "starting_time")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar startingTime;

	@Column(name = "last_activation_time")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar lastActivationTime;

	@Column(name = "ending_time")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar endingTime;

	@Column(name = "repeat_frequency")
	@NotNull
	private int repeatFrequency;

	@Column(name = "repeat_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private MonitorRepeatTypeEnum repeatType;

	@Column(name = "active_search", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean activeSearch = true;

	@Column(name = "up_to_value")
	@NotNull
	private int upToValue;

	@Column(name = "up_to_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private UpToTypeEnum upToType;

	@Column(name = "links")
	@Length(max = 500)
	private String links;

	@Column(name = "accounts")
	@Length(max = 500)
	private String accounts;

	@Column(name = "documents")
	@Length(max = 500)
	private String documents;

	@Column(name = "active", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean active = true;

	@Transient
	private Calendar firstSearchTime;

	public TwitterMonitorScheduler() {

	}

	public TwitterMonitorScheduler(long id, TwitterSearch twitterSearch, Calendar startingTime, Calendar lastActivationTime, Calendar endingTime,
			int repeatFrequency, MonitorRepeatTypeEnum repeatType, boolean activeSearch, int upToValue, UpToTypeEnum upToType, String links, String accounts,
			String documents, boolean active) {

		this.id = id;
		this.twitterSearch = twitterSearch;
		this.startingTime = startingTime;
		this.lastActivationTime = lastActivationTime;
		this.endingTime = endingTime;
		this.repeatFrequency = repeatFrequency;
		this.repeatType = repeatType;
		this.activeSearch = activeSearch;
		this.upToValue = upToValue;
		this.upToType = upToType;
		this.links = links;
		this.accounts = accounts;
		this.documents = documents;
		this.active = active;
	}

	// Entity mini -> TwitterDocumentsDataProcessor
	public TwitterMonitorScheduler(String documents, Calendar starting_time, Calendar last_activation_time) {
		this.documents = documents;
		this.startingTime = startingTime;
		this.lastActivationTime = last_activation_time;
	}

	public TwitterMonitorScheduler(long id, String links, String accounts) {
		this.id = id;
		this.links = links;
		this.accounts = accounts;
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

	public java.util.Calendar getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(java.util.Calendar startingTime) {
		this.startingTime = startingTime;
	}

	public java.util.Calendar getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(java.util.Calendar endingTime) {
		this.endingTime = endingTime;
	}

	public java.util.Calendar getLastActivationTime() {
		return lastActivationTime;
	}

	public void setLastActivationTime(java.util.Calendar lastActivationTime) {
		this.lastActivationTime = lastActivationTime;
	}

	public int getRepeatFrequency() {
		return repeatFrequency;
	}

	public void setRepeatFrequency(int repeatFrequency) {
		this.repeatFrequency = repeatFrequency;
	}

	public MonitorRepeatTypeEnum getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(MonitorRepeatTypeEnum repeatType) {
		this.repeatType = repeatType;
	}

	public boolean isActiveSearch() {
		return activeSearch;
	}

	public void setActiveSearch(boolean activeSearch) {
		this.activeSearch = activeSearch;
	}

	public int getUpToValue() {
		return upToValue;
	}

	public void setUpToValue(int upToValue) {
		this.upToValue = upToValue;
	}

	public UpToTypeEnum getUpToType() {
		return upToType;
	}

	public void setUpToType(UpToTypeEnum upToType) {
		this.upToType = upToType;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	public String getDocuments() {
		return documents;
	}

	public void setDocuments(String documents) {
		this.documents = documents;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Calendar getFirstSearchTime() {
		return firstSearchTime;
	}

	public void setFirstSearchTime(Calendar firstSearchTime) {
		this.firstSearchTime = firstSearchTime;
	}

	@Override
	public String toString() {
		return "TwitterMonitorScheduler [id=" + id + ", twitterSearch=" + twitterSearch + ", startingTime=" + startingTime + ", lastActivationTime="
				+ lastActivationTime + ", endingTime=" + endingTime + ", repeatFrequency=" + repeatFrequency + ", repeatType=" + repeatType + ", activeSearch="
				+ activeSearch + ", upToValue=" + upToValue + ", upToType=" + upToType + ", links=" + links + ", accounts=" + accounts + ", documents="
				+ documents + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result + (activeSearch ? 1231 : 1237);
		result = prime * result + ((documents == null) ? 0 : documents.hashCode());
		result = prime * result + ((endingTime == null) ? 0 : endingTime.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lastActivationTime == null) ? 0 : lastActivationTime.hashCode());
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + repeatFrequency;
		result = prime * result + ((repeatType == null) ? 0 : repeatType.hashCode());
		result = prime * result + ((startingTime == null) ? 0 : startingTime.hashCode());
		result = prime * result + ((twitterSearch == null) ? 0 : twitterSearch.hashCode());
		result = prime * result + ((upToType == null) ? 0 : upToType.hashCode());
		result = prime * result + upToValue;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TwitterMonitorScheduler other = (TwitterMonitorScheduler) obj;
		if (accounts == null) {
			if (other.accounts != null)
				return false;
		} else if (!accounts.equals(other.accounts))
			return false;
		if (activeSearch != other.activeSearch)
			return false;
		if (documents == null) {
			if (other.documents != null)
				return false;
		} else if (!documents.equals(other.documents))
			return false;
		if (endingTime == null) {
			if (other.endingTime != null)
				return false;
		} else if (!endingTime.equals(other.endingTime))
			return false;
		if (id != other.id)
			return false;
		if (lastActivationTime == null) {
			if (other.lastActivationTime != null)
				return false;
		} else if (!lastActivationTime.equals(other.lastActivationTime))
			return false;
		if (links == null) {
			if (other.links != null)
				return false;
		} else if (!links.equals(other.links))
			return false;
		if (repeatFrequency != other.repeatFrequency)
			return false;
		if (repeatType != other.repeatType)
			return false;
		if (startingTime == null) {
			if (other.startingTime != null)
				return false;
		} else if (!startingTime.equals(other.startingTime))
			return false;
		if (twitterSearch == null) {
			if (other.twitterSearch != null)
				return false;
		} else if (!twitterSearch.equals(other.twitterSearch))
			return false;
		if (upToType != other.upToType)
			return false;
		if (upToValue != other.upToValue)
			return false;
		return true;
	}

}
