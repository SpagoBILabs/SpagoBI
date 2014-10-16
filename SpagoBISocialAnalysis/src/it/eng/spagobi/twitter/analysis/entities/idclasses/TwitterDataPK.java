/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.entities.idclasses;

import it.eng.spagobi.twitter.analysis.entities.TwitterSearch;

import java.io.Serializable;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici (giorgio.federici@eng.it)
 *
 */
public class TwitterDataPK implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2883833763724918780L;

	protected long tweetID;
	protected TwitterSearch twitterSearch;

	public TwitterDataPK() {
	}

	public TwitterDataPK(long tweetID, TwitterSearch twitterSearch) {
		this.tweetID = tweetID;
		this.twitterSearch = twitterSearch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (tweetID ^ (tweetID >>> 32));
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
		TwitterDataPK other = (TwitterDataPK) obj;
		if (tweetID != other.tweetID)
			return false;
		if (twitterSearch == null) {
			if (other.twitterSearch != null)
				return false;
		} else if (!twitterSearch.equals(other.twitterSearch))
			return false;
		return true;
	}

}