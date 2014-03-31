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
package it.eng.spagobi.tools.dataset.cache;

import java.math.BigDecimal;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class CacheConfiguration {

	private String tableNamePrefix;
	private BigDecimal cacheSpaceAvailable;
	private Integer cachePercentageToClean;
	
	/**
	 * @return the tableNamePrefixConfig
	 */
	public String getTableNamePrefix() {
		return tableNamePrefix;
	}
	/**
	 * @param tableNamePrefix the tableNamePrefixConfig to set
	 */
	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}
	/**
	 * @return the cacheSpaceAvailable
	 */
	public BigDecimal getCacheSpaceAvailable() {
		return cacheSpaceAvailable;
	}
	/**
	 * @param cacheSpaceAvailable the cacheSpaceAvailable to set
	 */
	public void setCacheSpaceAvailable(BigDecimal cacheSpaceAvailable) {
		this.cacheSpaceAvailable = cacheSpaceAvailable;
	}
	/**
	 * @return the cachePercentageToClean
	 */
	public Integer getCachePercentageToClean() {
		return cachePercentageToClean;
	}
	/**
	 * @param cachePercentageToClean the cachePercentageToClean to set
	 */
	public void setCachePercentageToClean(Integer cachePercentageToClean) {
		this.cachePercentageToClean = cachePercentageToClean;
	}
	
	
}
