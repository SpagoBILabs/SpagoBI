/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import it.eng.spagobi.twitter.analysis.enums.SearchRepeatTypeEnum;

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
import javax.validation.constraints.NotNull;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Entity
@Table(name = "TWITTER_SEARCH_SCHEDULER")
public class TwitterSearchScheduler {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TWITTER_SEARCH_SCHEDULER_GEN")
	@TableGenerator(name = "TWITTER_SEARCH_SCHEDULER_GEN", initialValue = 1, allocationSize = 1, table = "TWITTER_HIBERNATE_SEQUENCES", pkColumnName = "SEQUENCE_NAME", valueColumnName = "NEXT_VAL", pkColumnValue = "TWITTER_SEARCH_SCHEDULER")
	@Column(name = "id")
	@NotNull
	private long id;

	@OneToOne
	@JoinColumn(name = "search_id")
	@NotNull
	private TwitterSearch twitterSearch;

	@Column(name = "starting_time")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar startingTime;

	@Column(name = "repeat_frequency")
	@NotNull
	private int repeatFrequency;

	@Column(name = "repeat_type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private SearchRepeatTypeEnum repeatType;

	@Column(name = "active", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean active = true;

	public TwitterSearchScheduler() {

	}

	public TwitterSearchScheduler(long id, TwitterSearch twitterSearch, Calendar startingTime, int repeatFrequency, SearchRepeatTypeEnum repeatType,
			boolean active) {

		this.id = id;
		this.twitterSearch = twitterSearch;
		this.startingTime = startingTime;
		this.repeatFrequency = repeatFrequency;
		this.repeatType = repeatType;
		this.active = active;
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

	public int getRepeatFrequency() {
		return repeatFrequency;
	}

	public void setRepeatFrequency(int repeatFrequency) {
		this.repeatFrequency = repeatFrequency;
	}

	public SearchRepeatTypeEnum getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(SearchRepeatTypeEnum repeatType) {
		this.repeatType = repeatType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "TwitterSearchScheduler [id=" + id + ", twitterSearch=" + twitterSearch + ", startingTime=" + startingTime + ", repeatFrequency="
				+ repeatFrequency + ", repeatType=" + repeatType + ", active=" + active + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + repeatFrequency;
		result = prime * result + ((repeatType == null) ? 0 : repeatType.hashCode());
		result = prime * result + ((startingTime == null) ? 0 : startingTime.hashCode());
		result = prime * result + ((twitterSearch == null) ? 0 : twitterSearch.hashCode());
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
		TwitterSearchScheduler other = (TwitterSearchScheduler) obj;
		if (active != other.active)
			return false;
		if (id != other.id)
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
		return true;
	}

}
