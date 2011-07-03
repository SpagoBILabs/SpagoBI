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
package it.eng.spagobi.tools.dataset.bo;

import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.tools.dataset.common.datareader.JSONDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JDBCDataSetTest  extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDataSet() throws Exception {
		SpagoBiDataSet dataSetConfig = new SpagoBiDataSet();
		dataSetConfig.setQuery("SELECT fullname as 'Full Name' FROM CUSTOMER LIMIT 10");
		
		SpagoBiDataSource dataSourceConfig = new SpagoBiDataSource();
		dataSourceConfig.setDriver(TestCaseConstants.CONNECTION_DRIVER);
		dataSourceConfig.setHibDialectClass(TestCaseConstants.CONNECTION_DIALECT);
		dataSourceConfig.setHibDialectName(TestCaseConstants.CONNECTION_DIALECT);
		dataSourceConfig.setMultiSchema(false);
		dataSourceConfig.setUser(TestCaseConstants.CONNECTION_USER);
		dataSourceConfig.setPassword(TestCaseConstants.CONNECTION_PWD);
		dataSourceConfig.setUrl(TestCaseConstants.CONNECTION_URL);
		
		dataSetConfig.setDataSource(dataSourceConfig);
		
		IDataSet dataset = new JDBCDataSet(dataSetConfig);
		dataset.loadData();
		IDataStore dataStore1 = dataset.getDataStore();
		JSONDataWriter dataWriter = new JSONDataWriter();
		
		JSONObject o1 = (JSONObject)dataWriter.write(dataStore1);
				
		JSONObject m1 = o1.getJSONObject("metaData");
		System.out.print(m1.toString(3));

		System.out.print("----------------------");
		
		JSONArray d1 = o1.getJSONArray("rows");
		System.out.print(d1.toString(3));
		
		System.out.print("\n\n==========================\n\n");
		
		JSONDataReader dataReader = new JSONDataReader();
		IDataStore dataStore2 = dataReader.read(o1);
		
		
		JSONObject o2 = (JSONObject)dataWriter.write(dataStore2);
		
		JSONObject m2 = o2.getJSONObject("metaData");
		System.out.print(m2.toString(3));

		System.out.print("----------------------");
		
		JSONArray d2 = o2.getJSONArray("rows");
		System.out.print(d2.toString(3));
		
		
		assertEquals(m1.toString(), m2.toString());
		
		assertEquals(d1.toString(), d2.toString());
		
	}

}
