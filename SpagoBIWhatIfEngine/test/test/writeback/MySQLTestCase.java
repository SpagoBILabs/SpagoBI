/**
 * 
 */
package test.writeback;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class MySQLTestCase extends WriteBackTestCase {

	
	public void testMysql(){
		

		it.eng.spagobi.tools.datasource.bo.DataSource ds = new it.eng.spagobi.tools.datasource.bo.DataSource();
		

		
		ds.setUser(TestConstants.MYSQL_USER);
		ds.setPwd(TestConstants.MYSQL_PWD);
		ds.setDriver(TestConstants.MYSQL_DRIVER);
		String connectionUrl = TestConstants.MYSQL_URL;
		ds.setUrlConnection(connectionUrl.replace("jdbc:mondrian:Jdbc=", ""));
		
		Double ration = persistTransformations(ds, "D:/Sviluppo/SpagoBI/progetti/Trunk_40/runtime/resources/Olap/FoodMartMySQLTest.xml");
		

		
		assertTrue(ration<accurancy);
		
	}
	

}
