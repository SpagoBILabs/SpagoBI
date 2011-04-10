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
package it.eng.qbe.datasource.jpa;

import it.eng.qbe.datasource.AbstractDataSourceTestCase;
import it.eng.qbe.datasource.AbstractQbeTestCase;
import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.structure.IModelStructure;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ViewsJPADataSourceTestCase extends AbstractQbeTestCase {
	
	String modelName;
	
	private static final String QBE_FILE = "resources/jpa/foodmartviews/datamart.jar";
	
	@Override
	protected void setUpDataSource() {
		IDataSourceConfiguration configuration;
		
		modelName = "My Model";  
		
		File file = new File(QBE_FILE);
		configuration = new FileDataSourceConfiguration(modelName, file);
		configuration.loadDataSourceProperties().put("connection", connection);
		dataSource = DriverManager.getDataSource(JPADriver.DRIVER_ID, configuration);
	}
	
	public void testSmoke() throws JSONException {
		List views = dataSource.getConfiguration().loadViews();
		assertNotNull(views);
		assertEquals(1, views.size());
		assertNotNull(views.get(0));
		assertTrue("Views conf cannot be an insatnce of [" + views.get(0).getClass().getName()  +"]", views.get(0) instanceof JSONObject);
		
		IModelStructure modelStructure = dataSource.getModelStructure();
		// ...
	}
}
