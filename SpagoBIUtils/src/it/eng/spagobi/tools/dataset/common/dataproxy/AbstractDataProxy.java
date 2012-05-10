/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.common.dataproxy;

import java.util.Map;

public abstract class AbstractDataProxy implements IDataProxy {

	Map parameters;
	Map profile;
	int offset;
	int fetchSize;
	int maxResults;
	boolean calculateResultNumberOnLoad;
	String statement;
	String resPath;

	public String getResPath() {
		return resPath;
	}

	public void setResPath(String resPath) {
		this.resPath = resPath;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public Map getProfile() {
		return profile;
	}

	public void setProfile(Map profile) {
		this.profile = profile;
	}

	public boolean isPaginationSupported() {
		return isOffsetSupported() && isMaxResultsSupported();
	}
	
	public boolean isOffsetSupported() {
		return false;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean isFetchSizeSupported() {
		return false;
	}
	
	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public boolean isMaxResultsSupported() {
		return true;
	}
	
	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	
	public boolean isCalculateResultNumberOnLoadEnabled() {
		return calculateResultNumberOnLoad;
	}
	
	public void setCalculateResultNumberOnLoad(boolean enabled) {
		calculateResultNumberOnLoad = enabled;
	}
	
	public long getResultNumber() {
		return -1;
	}
	
	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}
	
}
