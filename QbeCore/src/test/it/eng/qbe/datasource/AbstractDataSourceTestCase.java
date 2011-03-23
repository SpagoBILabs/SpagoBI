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
package it.eng.qbe.datasource;

import java.util.Locale;

import it.eng.qbe.AbstractQbeTestCase;
import it.eng.qbe.model.properties.IModelProperties;
import it.eng.qbe.model.structure.IModelEntity;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractDataSourceTestCase extends AbstractQbeTestCase {
	
	protected String testEntityUniqueName;
	
	public void testSmoke() {
		 assertNotNull("Impossible to build modelStructure", dataSource.getModelStructure());
	}
	
	public void testStructure() {
		IModelEntity entity = dataSource.getModelStructure().getEntity(testEntityUniqueName);
		assertNotNull("Impossible to load entity [" + testEntityUniqueName + "] from datasource [" + dataSource.getName() + "]", entity);
	}
	
	public void testLabelLocalization() {
		IModelProperties properties;
		String label;
		IModelEntity entity = dataSource.getModelStructure().getEntity(testEntityUniqueName);
		
		properties = dataSource.getModelI18NProperties(Locale.ITALIAN);
		label = properties.getProperty(entity, "label");
		assertTrue("[" + label + "] is not equal to [" + "Customer Italiano" + "]", "Customer Italiano".equals(label));
		
		properties = dataSource.getModelI18NProperties(Locale.ENGLISH);
		label = properties.getProperty(entity, "label");
		assertTrue("[" + label + "] is not equal to [" + "Customer Inglese" + "]", "Customer Inglese".equals(label));
		
		properties = dataSource.getModelI18NProperties(Locale.JAPANESE);
		label = properties.getProperty(entity, "label");
		assertTrue("[" + label + "] is not equal to [" + "Customer Default" + "]", "Customer Default".equals(label));
	}
}
