/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.common.datareader;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class AbstractDataReader implements IDataReader {
	int offset;
	int fetchSize;
	int maxResults;
	
	public AbstractDataReader() {
		offset = -1;
		fetchSize = -1;
		maxResults = -1;
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
		return false;
	}
	
	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
}
