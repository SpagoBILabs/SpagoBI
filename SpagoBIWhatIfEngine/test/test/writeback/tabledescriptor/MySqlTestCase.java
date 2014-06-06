/**
 * 
 */
package test.writeback.tabledescriptor;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.database.temporarytable.TemporaryTableManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import test.AbstractWhatIfTestCase;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class MySqlTestCase extends AbstractWhatIfTestCase {

	public static final String tableName = "sales_fact_1998_virtual";
	
	public void testGetTemporaryTableManager( ) throws Exception{
		WhatIfEngineInstance ei = getWhatifengineiEngineInstance(getCatalogue());
		
		IDataSource dataSource = ei.getDataSource();
		
		IDataSetTableDescriptor tabledescriptor = TemporaryTableManager.getTableDescriptor(null, tableName, dataSource);
		Set<String> columns = tabledescriptor.getColumnNames();
		String t = "ddd";
	}
	
	public String getCatalogue(){
		
        File userDir = new File("").getAbsoluteFile();
        File f  = new File(userDir,  "\\test\\test\\writeback\\resources\\FoodMartMySQL.xml");
		return f.getAbsolutePath();
	}


	

}
