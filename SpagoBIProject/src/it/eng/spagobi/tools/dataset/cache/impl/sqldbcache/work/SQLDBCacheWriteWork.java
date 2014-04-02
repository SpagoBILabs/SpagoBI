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
package it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.work;

import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import commonj.work.Work;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class SQLDBCacheWriteWork implements Work {

	ICache cache;
	IDataStore dataStore;
	IDataSet dataSet;

	/**
	 * @param cache
	 * @param dataStore
	 * @param signature
	 * @param dataSet
	 */
	public SQLDBCacheWriteWork(ICache cache, IDataStore dataStore, IDataSet dataSet) {
		super();
		this.cache = cache;
		this.dataStore = dataStore;
		this.dataSet = dataSet;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		cache.put(dataSet, dataStore);
	}

	/* (non-Javadoc)
	 * @see commonj.work.Work#isDaemon()
	 */
	public boolean isDaemon() {
		return false;
	}

	/* (non-Javadoc)
	 * @see commonj.work.Work#release()
	 */
	public void release() {
		// TODO Auto-generated method stub

	}

}
