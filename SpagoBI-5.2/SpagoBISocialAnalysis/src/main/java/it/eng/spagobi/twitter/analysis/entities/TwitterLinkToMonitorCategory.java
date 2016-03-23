/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Entity
@Table(name = "TWITTER_LINK_TO_MONITOR_CAT")
public class TwitterLinkToMonitorCategory {

	@Id
	@Column(name = "id")
	@NotNull
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TWITTER_LINK_TO_MONITOR_CAT_GEN")
	@TableGenerator(name = "TWITTER_LINK_TO_MONITOR_CAT_GEN", allocationSize = 1, initialValue = 1, table = "TWITTER_HIBERNATE_SEQUENCES", pkColumnName = "SEQUENCE_NAME", valueColumnName = "NEXT_VAL", pkColumnValue = "TWITTER_LINK_TO_MONITOR_CAT")
	private long id;

	@OneToOne
	@JoinColumn(name = "link_id")
	@NotNull
	private TwitterLinkToMonitor twitterLinkToMonitor;

	@Column(name = "type")
	@NotNull
	@Length(max = 45)
	private String type;

	@Column(name = "category")
	@NotNull
	@Length(max = 45)
	private String category;

	@Column(name = "clicks_count")
	@NotNull
	private int clicksCount;

	public TwitterLinkToMonitorCategory() {

	}

	public TwitterLinkToMonitorCategory(long id, TwitterLinkToMonitor twitterLinkToMonitor, String type, String category, int clicksCount) {

		this.id = id;
		this.twitterLinkToMonitor = twitterLinkToMonitor;
		this.type = type;
		this.category = category;
		this.clicksCount = clicksCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TwitterLinkToMonitor getTwitterLinkToMonitor() {
		return twitterLinkToMonitor;
	}

	public void setTwitterLinkToMonitor(TwitterLinkToMonitor twitterLinkToMonitor) {
		this.twitterLinkToMonitor = twitterLinkToMonitor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getClicksCount() {
		return clicksCount;
	}

	public void setClicksCount(int clicksCount) {
		this.clicksCount = clicksCount;
	}

	@Override
	public String toString() {
		return "TwitterLinkToMonitorCategory [id=" + id + ", twitterLinkToMonitor=" + twitterLinkToMonitor + ", type=" + type + ", category=" + category
				+ ", clicksCount=" + clicksCount + "]";
	}

}
