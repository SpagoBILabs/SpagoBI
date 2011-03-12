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
package it.eng.qbe.model.properties.initializer;

import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.model.properties.ModelPropertiesMeta;
import it.eng.qbe.model.properties.ModelProperty;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.model.structure.IDataMartNode;
import it.eng.qbe.model.structure.IDataMartObject;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SimpleDataMartStructurePropertiesInitializer implements IDataMartStructurePropertiesInitializer {
		
	IHibernateDataSource dataSource;
	Map properties;
	
	public SimpleDataMartStructurePropertiesInitializer(IHibernateDataSource dataSource) {
		this.dataSource =  dataSource;
		this.properties = new HashMap();
		properties.putAll( dataSource.getConfiguration().loadModelProperties() );
	}
	

	public void addProperties(IDataMartObject item) {
		if(item instanceof DataMartEntity) {
			addDataMartEntityProperties( (DataMartEntity)item );
		} else if (item instanceof DataMartField) {
			addDataMartFieldProperties( (DataMartField)item );
		} else if (item instanceof DataMartModelStructure) {
			addDataMartModelProperties( (DataMartModelStructure)item );
		}
	}
	
	private void addDataMartModelProperties(DataMartModelStructure item) {
		ModelProperty property;
		String propertyValue;
		
		for (int i = 0; i < ModelPropertiesMeta.globalProperties.length; i++) {
			property = ModelPropertiesMeta.globalProperties[i];
			propertyValue = getProperty(item, property.getName());
			
			// property not set
			if(propertyValue == null) {
				if(property.isOptional() == false) {
					throw new SpagoBIRuntimeException("Impossible to initialize property [" + property.getName() + "] of structure [" + item.getName() + "]");
				}
				propertyValue = property.getDefaultValue();
			} 
			
			item.getProperties().put(property.getName(), propertyValue);
		}
	}

	protected void addDataMartEntityProperties(DataMartEntity item) {
		ModelProperty property;
		String propertyValue;
		
		for (int i = 0; i < ModelPropertiesMeta.entityProperties.length; i++) {
			property = ModelPropertiesMeta.entityProperties[i];
			propertyValue = getProperty(item, property.getName());
			
			// property not set
			if(propertyValue == null) {
				if(property.isOptional() == false) {
					throw new SpagoBIRuntimeException("Impossible to initialize property [" + property.getName() + "] of entity [" + item.getUniqueName() + "]");
				}
				propertyValue = property.getDefaultValue();
			} 
			
			// property not set + property default value not set
			if(propertyValue == null && property.isInherited()) {
				propertyValue = getInheritedProperty(item, property.getName());
			}
			
			item.getProperties().put(property.getName(), propertyValue);
		}
	}
	
	protected void addDataMartFieldProperties(DataMartField item) {
		ModelProperty property;
		String propertyValue;
		
		for (int i = 0; i < ModelPropertiesMeta.fieldProperties.length; i++) {
			property = ModelPropertiesMeta.fieldProperties[i];
			propertyValue = getProperty(item, property.getName());
			
			// property not set
			if(propertyValue == null) {
				if(property.isOptional() == false) {
					throw new SpagoBIRuntimeException("Impossible to initialize property [" + property.getName() + "] of field [" + item.getUniqueName() + "]");
				}
				propertyValue = property.getDefaultValue();
			}
			
			// property not set + property default value not set
			if(propertyValue == null && property.isInherited()) {
				propertyValue = getInheritedProperty(item, property.getName());
			}
			
			item.getProperties().put(property.getName(), propertyValue);
		}
	}
	
	// TODO create method getRootItem in IDataMartItem interface and move some code there
	protected String getInheritedProperty(DataMartEntity item, String propertyName) {
		Assert.assertUnreachable("Property [" + propertyName + "] of entity [" + item.getName()+ "] cannot be inehritated");
		String propertyValue;
		DataMartEntity rootEntity = item.getStructure().getRootEntity(item);
		Assert.assertNotNull(rootEntity, "Impossible to find root entity of entity [" + item.getName() + "]");
		propertyValue = getProperty(rootEntity, propertyName);
		
		return propertyValue;
	}
	
	// TODO create method getRootItem in IDataMartItem interface and move some code there
	protected String getInheritedProperty(DataMartField item, String propertyName) {
		String propertyValue;
		DataMartField rootField = null;
		DataMartEntity rootEntity = item.getStructure().getRootEntity(item.getParent());
		if(rootEntity == null) {
			rootEntity = item.getStructure().getRootEntity(item.getParent());
			Assert.assertUnreachable("rootEntity for field [" + item.getName() + "] cannot be null");
		}
		
		List fields = null;
		if(item instanceof DataMartCalculatedField) {
			fields = rootEntity.getCalculatedFields();
		} else {
			fields = rootEntity.getAllFields();
		}
		Iterator<DataMartField> it = fields.iterator();
		while (it.hasNext()) {
			DataMartField field = it.next();
			if (field.getName().equals(item.getName())) {
				rootField = field;
				break;
			}
		}
		Assert.assertNotNull(rootField, "Impossible to find root field of field [" + item.getName() + "]");
		propertyValue = getProperty(rootField, propertyName);
		
		return propertyValue;
	}
	
	protected String getProperty(IDataMartObject item, String propertyName) {
		String propertyQualifiedName;
		
		propertyQualifiedName = null;
		if(item instanceof IDataMartNode) {
			propertyQualifiedName = getPropertyQualifiedName( (IDataMartNode)item, propertyName);
		} else {
			propertyQualifiedName = propertyName;
		}
		
		String propertyValue = (String)properties.get( propertyQualifiedName );
		propertyValue = StringUtilities.isNull( propertyValue )? null: propertyValue.trim();
		return propertyValue;
	}
	
	protected String getPropertyQualifiedName(IDataMartNode item, String propertyName) {
		return getItemQulifier( item ) + "." + propertyName.trim();
	}
	
	protected String getItemQulifier( IDataMartNode item ) {
		Assert.assertNotNull(item, "Parameter [item] cannot be null");
		Assert.assertNotNull(item.getUniqueName(), "Item [uniqueName] cannot be null [" + item.getName() + "]");
		return item.getUniqueName().replaceAll(":", "/");
	}
	
	
	
	

}
