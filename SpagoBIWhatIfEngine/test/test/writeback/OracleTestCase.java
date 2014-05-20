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
public class OracleTestCase extends WriteBackTestCase {

	
	public void testOracle(){
		

		it.eng.spagobi.tools.datasource.bo.DataSource ds = new it.eng.spagobi.tools.datasource.bo.DataSource();
		

		
		ds.setUser(TestConstants.ORACLE_USER);
		ds.setPwd(TestConstants.ORACLE_PWD);
		ds.setDriver(TestConstants.ORACLE_DRIVER);
		String connectionUrl = TestConstants.ORACLE_URL;
		ds.setUrlConnection(connectionUrl.replace("jdbc:mondrian:Jdbc=", ""));
		
		Double ration = persistTransformations(ds, "D:/Sviluppo/SpagoBI/progetti/Trunk_40/runtime/resources/Olap/FoodMartOracleSQLTest.xml");
		

		
		assertTrue(ration<accurancy);
		
	}
	

}
