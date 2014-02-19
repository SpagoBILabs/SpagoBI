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



import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.FileCreatorConfiguration;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;

import it.eng.spagobi.commons.dao.DAOFactory;

import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.dataset.cache.CacheFactory;
import it.eng.spagobi.dataset.cache.ICache;

import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tenant.TenantManager;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.dao.DataSetFactory;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import junit.framework.TestCase;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class FirstTest extends TestCase {

	private static ICache cache = null;
	
	private JDBCDataSet sqlDataset;
	private QbeDataSet qbeDataset;
	private FileDataSet fileDataset;
	private DataSource dataSourceFoodmart;
	
	private static String RESOURCE_PATH = "C:/Users/cortella/workspaceJEE/SpagoBICockpitEngine/test/it/eng/spagobi/dataset/cache/impl/sqldbcache/test/";

	static private Logger logger = Logger.getLogger(FirstTest.class);

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("AF_CONFIG_FILE", "/WEB-INF/conf/master.xml");
    	ConfigSingleton.setConfigurationCreation( new FileCreatorConfiguration( "C:/Users/cortella/workspaceJEE/SpagoBICockpitEngine/WebContent" ) );

		//System.getProperty("AF_ROOT_PATH", "C:/Users/cortella/workspaceJEE/SpagoBICockpitEngine/WebContent/WEB-INF/conf/");
		//UserUtilities userUtilities = new UserUtilities();
    	//IEngUserProfile user = userUtilities.getUserProfile("biadmin");
    	TenantManager.setTenant(new Tenant("SPAGOBI"));
		CacheFactory cacheFactory = new CacheFactory();
		IDataSourceDAO dataSourceDAO= DAOFactory.getDataSourceDAO();
		IDataSource dataSource = dataSourceDAO.loadDataSourceWriteDefault();
		cache = cacheFactory.initCache(dataSource);
		
		//creazione datasource per persistenza e dataset
		this.createDataSources();
		this.createDatasets();


	}
	
	public void testCacheInit(){
		//assertTrue("Cache correctly initialized", cache != null);
		assertNotNull("Cache correctly initialized", cache );
	}
	
	public void testCachePut(){
		IDataStore resultset;
		//Test JDBCDataset
		/*
		sqlDataset.loadData();
		IDataStore resultset = sqlDataset.getDataStore();
		cache.put(sqlDataset, sqlDataset.getSignature(), resultset);
		logger.debug("JDBCDataset inserted inside cache");
		*/
		//Test FileDataset
		fileDataset.loadData();
		resultset =	fileDataset.getDataStore();
		cache.put(fileDataset, fileDataset.getSignature(), resultset);
		logger.debug("FileDataSet inserted inside cache");

		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	//initialization methods
	public void createDataSources(){
		dataSourceFoodmart = new DataSource();
		
		dataSourceFoodmart.setDsId(999999);
		dataSourceFoodmart.setLabel("datasetTest_foodmart");
		dataSourceFoodmart.setDescr("datasetTest_foodmart");
		dataSourceFoodmart.setJndi("");
		dataSourceFoodmart.setUrlConnection("jdbc:mysql://localhost:3306/foodmart");
		dataSourceFoodmart.setUser("root");
		dataSourceFoodmart.setPwd("root");
		dataSourceFoodmart.setDriver("com.mysql.jdbc.Driver");
		//dataSourceFoodmart.setDialectId(hibDataSource.getDialect().getValueId());
		//dataSourceFoodmart.setEngines(hibDataSource.getSbiEngineses());
		//dataSourceFoodmart.setObjects(hibDataSource.getSbiObjectses());
		dataSourceFoodmart.setSchemaAttribute("");
		dataSourceFoodmart.setMultiSchema(false);
		dataSourceFoodmart.setHibDialectClass("org.hibernate.dialect.MySQLInnoDBDialect");
		dataSourceFoodmart.setHibDialectName("sbidomains.nm.mysql");
		dataSourceFoodmart.setReadOnly(false);
		dataSourceFoodmart.setWriteDefault(false);

	}
	
	public void createDatasets() throws JSONException{
		/*
		sqlDataset = new JDBCDataSet();
		sqlDataset.setQuery("select * from customer");
		sqlDataset.setQueryScript("");
		sqlDataset.setQueryScriptLanguage("");
		sqlDataset.setDataSource(dataSourceFoodmart);
		*/
		fileDataset = new FileDataSet();
		fileDataset.setFileType("CSV");
		JSONObject jsonConf = new JSONObject();
		jsonConf.put("fileType", "CSV");
		jsonConf.put("fileName", "customers.csv");
		jsonConf.put("csvDelimiter", ",");
		jsonConf.put("csvDelimiter", ",");
		jsonConf.put("csvQuote", "\"");
		jsonConf.put("csvEncoding", "UTF-8");
		jsonConf.put("DS_SCOPE", "USER");
		
		//TODO: completare dataset metadata
		fileDataset.setDsMetadata("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><META version=\"1\"><COLUMNLIST></COLUMNLIST><DATASET><PROPERTY name=\"resultNumber\" value=\"10281\"/> </DATASET></META>");
				
		fileDataset.setConfiguration(jsonConf.toString());
		fileDataset.setResourcePath(RESOURCE_PATH);
		fileDataset.setFileName("customers.csv");
		
		
	}
	
	


}
