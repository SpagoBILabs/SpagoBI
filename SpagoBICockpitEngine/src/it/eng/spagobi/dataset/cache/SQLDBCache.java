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
package it.eng.spagobi.dataset.cache;

import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.List;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCache implements ICache {
	
	
	private IDataSource dataSource;
	
	public SQLDBCache(IDataSource dataSource){
		this.dataSource = dataSource;
	}
	

	/**
	 * @return the dataSource
	 */
	public IDataSource getDataSource() {
		return dataSource;
	}



	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String resultsetSignature) {
		// TODO Auto-generated method stub
		return false;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String)
	 */
	@Override
	public IDataStore get(String resultsetSignature) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#get(java.lang.String, java.util.List, java.util.List, java.util.List)
	 */
	@Override
	public IDataStore get(String resultsetSignature,
			List<GroupCriteria> groups, List<FilterCriteria> filters,
			List<ProjectionCriteria> projections) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#delete(java.lang.String)
	 */
	@Override
	public boolean delete(String resultsetSignature) {
		// TODO Auto-generated method stub
		return false;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#deleteAll()
	 */
	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#getCacheMetadata()
	 */
	@Override
	public ICacheMetadata getCacheMetadata() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#addListener(it.eng.spagobi.dataset.cache.ICacheEvent, it.eng.spagobi.dataset.cache.ICacheListener)
	 */
	@Override
	public void addListener(ICacheEvent event, ICacheListener listener) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.dataset.cache.ICache#scheduleActivity(it.eng.spagobi.dataset.cache.ICacheActivity, it.eng.spagobi.dataset.cache.ICacheTrigger)
	 */
	@Override
	public void scheduleActivity(ICacheActivity activity, ICacheTrigger trigger) {
		// TODO Auto-generated method stub
		
	}

}
