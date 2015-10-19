/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import it.eng.spagobi.twitter.analysis.enums.BooleanOperatorEnum;
import it.eng.spagobi.twitter.analysis.enums.SearchTypeEnum;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Entity
@Table(name = "TWITTER_SEARCH")
public class TwitterSearch implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7513781474878719973L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TWITTER_SEARCH_GEN")
	@TableGenerator(name = "TWITTER_SEARCH_GEN", initialValue = 1, allocationSize = 1, table = "TWITTER_HIBERNATE_SEQUENCES", pkColumnName = "SEQUENCE_NAME", valueColumnName = "NEXT_VAL", pkColumnValue = "TWITTER_SEARCH")
	@Column(name = "search_id")
	@NotNull
	private long searchID;

	@Column(name = "label")
	@NotNull
	@Length(max = 100)
	private String label;

	@Column(name = "keywords")
	@NotNull
	@Length(max = 200)
	private String keywords;

	@Column(name = "creation_date")
	@Temporal(TemporalType.DATE)
	@NotNull
	private java.util.Calendar creationDate;

	@Column(name = "last_activation_time")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private java.util.Calendar lastActivationTime;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@NotNull
	private SearchTypeEnum type;

	@Column(name = "loading", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean loading = true;

	@Column(name = "deleted", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean deleted = false;

	@Column(name = "failed", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean failed = false;

	@Column(name = "fail_message")
	// @Length(max = 1000)
	private String failMessage;

	@Column(name = "boolean_operator")
	@Enumerated(EnumType.STRING)
	@NotNull
	private BooleanOperatorEnum booleanOperator;

	@Column(name = "days_ago")
	private Integer daysAgo;

	@Column(name = "r_analysis", columnDefinition = "boolean", length = 1)
	@NotNull
	private boolean rAnalysis = false;

	@OneToOne(mappedBy = "twitterSearch", cascade = { CascadeType.PERSIST })
	private TwitterSearchScheduler twitterSearchScheduler;

	@OneToOne(mappedBy = "twitterSearch", cascade = { CascadeType.PERSIST })
	private TwitterMonitorScheduler twitterMonitorScheduler;

	// @OneToMany(mappedBy = "twitterSearch")
	// private List<TwitterData> tweets = new ArrayList<TwitterData>();

	public TwitterSearch() {

	}

	public TwitterSearch(long searchID, String label, String keywords, Calendar creationDate, Calendar lastActivationTime, SearchTypeEnum type,
			boolean loading, boolean deleted, boolean failed, String failMessage, BooleanOperatorEnum booleanOperator, Integer daysAgo, boolean rAnalysis,
			TwitterSearchScheduler twitterSearchScheduler, TwitterMonitorScheduler twitterMonitorScheduler) {

		this.searchID = searchID;
		this.label = label;
		this.keywords = keywords;
		this.creationDate = creationDate;
		this.lastActivationTime = lastActivationTime;
		this.type = type;
		this.loading = loading;
		this.deleted = deleted;
		this.failed = failed;
		this.failMessage = failMessage;
		this.booleanOperator = booleanOperator;
		this.daysAgo = daysAgo;
		this.rAnalysis = rAnalysis;
		this.twitterSearchScheduler = twitterSearchScheduler;
		this.twitterMonitorScheduler = twitterMonitorScheduler;
	}

	public long getSearchID() {
		return searchID;
	}

	public void setSearchID(long searchID) {
		this.searchID = searchID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public java.util.Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(java.util.Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public java.util.Calendar getLastActivationTime() {
		return lastActivationTime;
	}

	public void setLastActivationTime(java.util.Calendar lastActivationTime) {
		this.lastActivationTime = lastActivationTime;
	}

	public SearchTypeEnum getType() {
		return type;
	}

	public void setType(SearchTypeEnum type) {
		this.type = type;
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public BooleanOperatorEnum getBooleanOperator() {
		return booleanOperator;
	}

	public void setBooleanOperator(BooleanOperatorEnum booleanOperator) {
		this.booleanOperator = booleanOperator;
	}

	public Integer getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(Integer daysAgo) {
		this.daysAgo = daysAgo;
	}

	public boolean isrAnalysis() {
		return rAnalysis;
	}

	public void setrAnalysis(boolean rAnalysis) {
		this.rAnalysis = rAnalysis;
	}

	public TwitterSearchScheduler getTwitterSearchScheduler() {
		return twitterSearchScheduler;
	}

	public void setTwitterSearchScheduler(TwitterSearchScheduler twitterSearchScheduler) {
		this.twitterSearchScheduler = twitterSearchScheduler;
	}

	public TwitterMonitorScheduler getTwitterMonitorScheduler() {
		return twitterMonitorScheduler;
	}

	public void setTwitterMonitorScheduler(TwitterMonitorScheduler twitterMonitorScheduler) {
		this.twitterMonitorScheduler = twitterMonitorScheduler;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TwitterSearch other = (TwitterSearch) obj;
		if (booleanOperator != other.booleanOperator)
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (daysAgo == null) {
			if (other.daysAgo != null)
				return false;
		} else if (!daysAgo.equals(other.daysAgo))
			return false;
		if (deleted != other.deleted)
			return false;
		if (failMessage == null) {
			if (other.failMessage != null)
				return false;
		} else if (!failMessage.equals(other.failMessage))
			return false;
		if (failed != other.failed)
			return false;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (lastActivationTime == null) {
			if (other.lastActivationTime != null)
				return false;
		} else if (!lastActivationTime.equals(other.lastActivationTime))
			return false;
		if (loading != other.loading)
			return false;
		if (rAnalysis != other.rAnalysis)
			return false;
		if (searchID != other.searchID)
			return false;
		if (twitterMonitorScheduler == null) {
			if (other.twitterMonitorScheduler != null)
				return false;
		} else if (!twitterMonitorScheduler.equals(other.twitterMonitorScheduler))
			return false;
		if (twitterSearchScheduler == null) {
			if (other.twitterSearchScheduler != null)
				return false;
		} else if (!twitterSearchScheduler.equals(other.twitterSearchScheduler))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public JSONObject toJSONObject() throws JSONException {

		JSONObject twitterSearchJSON = new JSONObject();
		twitterSearchJSON.put("searchID", searchID);
		twitterSearchJSON.put("label", label);
		twitterSearchJSON.put("keywords", keywords);

		java.sql.Timestamp lastActivation = new java.sql.Timestamp(lastActivationTime.getTimeInMillis());

		twitterSearchJSON.put("lastActivationTime", lastActivation);
		twitterSearchJSON.put("loading", loading);

		if (twitterMonitorScheduler != null) {
			twitterSearchJSON.put("accounts", twitterMonitorScheduler.getAccounts());
			twitterSearchJSON.put("links", twitterMonitorScheduler.getLinks());
			twitterSearchJSON.put("documents", twitterMonitorScheduler.getDocuments());
			twitterSearchJSON.put("hasMonitorScheduler", true);
		}

		if (twitterSearchScheduler != null && twitterSearchScheduler.isActive()) {
			twitterSearchJSON.put("hasSearchScheduler", true);
		}

		twitterSearchJSON.put("isFailed", failed);

		return twitterSearchJSON;
	}

}
