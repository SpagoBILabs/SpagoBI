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
package it.eng.qbe.datasource.configuration;

import it.eng.qbe.model.properties.IModelProperties;
import it.eng.qbe.model.properties.SimpleModelProperties;
import it.eng.qbe.model.structure.IModelEntity;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class IntegrationTest extends QbeTestCase {

	public void testSmoke() {
		 assertNotNull("Impossible to build modelStructure", hibernateSimpleDataSource.getModelStructure());
	}
	
	public void testStructure() {
		IModelEntity entity = hibernateSimpleDataSource.getModelStructure().getEntity("it.eng.spagobi.Customer::Customer");
		assertNotNull("Impossible to load entity [it.eng.spagobi.Customer::Customer] from datasource [" + hibernateSimpleDataSource.getName() + "]", entity);
	}
	
	public void testLabelLocalzation() {
		IModelProperties properties;
		String label;
		IModelEntity entity = hibernateSimpleDataSource.getModelStructure().getEntity("it.eng.spagobi.Customer::Customer");
		
		properties = hibernateSimpleDataSource.getModelI18NProperties(Locale.ITALIAN);
		label = properties.getProperty(entity, "label");
		assertTrue("[" + label + "] is not equal to [" + "Customer Italiano" + "]", "Customer Italiano".equals(label));
		
		properties = hibernateSimpleDataSource.getModelI18NProperties(Locale.ENGLISH);
		label = properties.getProperty(entity, "label");
		assertTrue("[" + label + "] is not equal to [" + "Customer Inglese" + "]", "Customer Inglese".equals(label));
		
		properties = hibernateSimpleDataSource.getModelI18NProperties(Locale.JAPANESE);
		label = properties.getProperty(entity, "label");
		assertTrue("[" + label + "] is not equal to [" + "Customer Default" + "]", "Customer Default".equals(label));
	}

	
	public static void main(String[] args) throws Exception {
		IntegrationTest testCase = new IntegrationTest();
		testCase.setUp();
		testCase.testStructure();
	}

}
