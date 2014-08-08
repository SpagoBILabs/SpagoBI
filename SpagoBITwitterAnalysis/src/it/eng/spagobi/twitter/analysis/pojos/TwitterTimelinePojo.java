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

package it.eng.spagobi.twitter.analysis.pojos;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 * 
 */
public class TwitterTimelinePojo {

	private long postTimeMills;
	private int nTweets;
	private int nRTs;
	private final long lowerBound;
	private final long upperBound;

	public TwitterTimelinePojo(long p, int nT, int nR, long lB, long uB) {
		this.postTimeMills = p;
		this.nTweets = nT;
		this.nRTs = nR;
		this.lowerBound = lB;
		this.upperBound = uB;
	}

	public long getPostTimeMills() {
		return postTimeMills;
	}

	public void setPostTimeMills(long postTimeMills) {
		this.postTimeMills = postTimeMills;
	}

	public int getnTweets() {
		return nTweets;
	}

	public void setnTweets(int nTweets) {
		this.nTweets = nTweets;
	}

	public int getnRTs() {
		return nRTs;
	}

	public void setnRTs(int nRTs) {
		this.nRTs = nRTs;
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public long getUpperBound() {
		return upperBound;
	}

	@Override
	public String toString() {
		return "TimelineChartObject [postTimeMills=" + postTimeMills + ", nTweets=" + nTweets + ", nRTs=" + nRTs + ", lowerBound=" + lowerBound + ", upperBound=" + upperBound
				+ "]";
	}

}
