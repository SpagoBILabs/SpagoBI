/**
 * 
 */
package test.writeback;

import java.io.File;

import it.eng.spagobi.tools.datasource.bo.DataSource;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class MySQLTestCase extends AbstractWriteBackTestCase {

	
	public DataSource getDataSource(){
		DataSource ds = new it.eng.spagobi.tools.datasource.bo.DataSource();
		ds.setUser(TestConstants.MYSQL_USER);
		ds.setPwd(TestConstants.MYSQL_PWD);
		ds.setDriver(TestConstants.MYSQL_DRIVER);
		String connectionUrl = TestConstants.MYSQL_URL;
		ds.setUrlConnection(connectionUrl.replace("jdbc:mondrian:Jdbc=", ""));
		return ds;
	}
	
	public String getCatalogue(){
		
        File userDir = new File("").getAbsoluteFile();
        File f  = new File(userDir,  "\\test\\test\\writeback\\resources\\FoodMartMySQL.xml");
		return f.getAbsolutePath();
	}
	
	

}
