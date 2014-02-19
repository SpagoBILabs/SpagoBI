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
package it.eng.spagobi.dataset.cache.impl.sqldbcache.test;



import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.FileCreatorConfiguration;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;

import it.eng.spagobi.commons.dao.DAOFactory;

import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.dataset.cache.CacheFactory;
import it.eng.spagobi.dataset.cache.ICache;

import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import junit.framework.TestCase;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class FirstTest extends TestCase {

	private static ICache cache = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("AF_CONFIG_FILE", "/WEB-INF/conf/master.xml");
    	ConfigSingleton.setConfigurationCreation( new FileCreatorConfiguration( "C:/Users/cortella/workspaceJEE/SpagoBICockpitEngine/WebContent" ) );

		//System.getProperty("AF_ROOT_PATH", "C:/Users/cortella/workspaceJEE/SpagoBICockpitEngine/WebContent/WEB-INF/conf/");
		UserUtilities userUtilities = new UserUtilities();
    	IEngUserProfile user = userUtilities.getUserProfile("biadmin");
		
		CacheFactory cacheFactory = new CacheFactory();
		IDataSourceDAO dataSourceDAO= DAOFactory.getDataSourceDAO();
		IDataSource dataSource = dataSourceDAO.loadDataSourceWriteDefault();
		cache = cacheFactory.initCache(dataSource);
		


	}
	
	public void testCacheInit(){
		//assertTrue("Cache correctly initialized", cache != null);
		assertNotNull("Cache correctly initialized", cache );
	}
	
	public void testCachePut(){
		IDataSetDAO datasetDAO;
		try {
			datasetDAO = DAOFactory.getDataSetDAO();
			IDataSet dataset = datasetDAO.loadDataSetByLabel("ds__3840363");
			dataset.loadData(-1, -1, -1);
			IDataStore resultset = dataset.getDataStore();
			cache.put(dataset, dataset.getSignature(), resultset);
		} catch (EMFUserError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	


}
