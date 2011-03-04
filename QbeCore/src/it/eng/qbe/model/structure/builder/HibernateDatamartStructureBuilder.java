/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.model.structure.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class HibernateDatamartStructureBuilder implements IDataMartStructureBuilder {
	
	private IHibernateDataSource dataSource;	

	public HibernateDatamartStructureBuilder(IHibernateDataSource dataSource) {
		if(dataSource== null) {
			throw new IllegalArgumentException("DataSource parameter cannot be null");
		}
		setDataSource( dataSource );
	}
	
	public DataMartModelStructure build() {
		
		DataMartModelStructure dataMartStructure;
		List datamartNames;
		String datamartName;
		Map classMetadata;
			
		dataMartStructure = new DataMartModelStructure();	
		
		datamartNames = getDataSource().getDatamartNames();
		for(int i = 0; i < datamartNames.size(); i++) {
			datamartName = (String)datamartNames.get(i);
			Assert.assertNotNull(getDataSource(), "datasource cannot be null");	
			SessionFactory sf = getDataSource().getSessionFactory(datamartName);
			if(sf == null) {
				throw new MissingResourceException("Impossible to find the jar file associated to datamart named: [" + datamartName + "]"
						, SessionFactory.class.getName()
						, datamartName );
			}
			
			
			Map calculatedFields = DAOFactory.getCalculatedFieldsDAO().loadCalculatedFields(datamartName);
			dataMartStructure.setCalculatedFields(calculatedFields);
			
			classMetadata = sf.getAllClassMetadata();
			for(Iterator it = classMetadata.keySet().iterator(); it.hasNext(); ) {
				String entityType = (String)it.next();			
				addEntity(dataMartStructure, datamartName, entityType);		
			}
		}
		
		
		return dataMartStructure;
	}

	private void addEntity (DataMartModelStructure dataMartStructure, String datamartName, String entityType){

		String entityName = getEntityNameFromEntityType(entityType);		
		DataMartEntity dataMartEntity = dataMartStructure.addRootEntity(datamartName, entityName, null, null, entityType);
				
		addKeyFields(dataMartEntity);		
		List subEntities = addNormalFields(dataMartEntity);	
		addCalculatedFields(dataMartEntity);
		addSubEntities(dataMartEntity, subEntities, 0);
	}
	
	private void addCalculatedFields(DataMartEntity dataMartEntity) {
		List calculatedFileds;
		DataMartCalculatedField calculatedField;
		
		calculatedFileds = dataMartEntity.getStructure().getCalculatedFieldsByEntity(dataMartEntity.getUniqueName());
		if(calculatedFileds != null) {
			for(int i = 0; i < calculatedFileds.size(); i++) {
				calculatedField = (DataMartCalculatedField)calculatedFileds.get(i);
				dataMartEntity.addCalculatedField(calculatedField);
			}
		}
	}

	private void addSubEntities(DataMartEntity dataMartEntity, List subEntities, int recursionLevel) {
		
		Iterator it = subEntities.iterator();
		while (it.hasNext()) {
			DataMartEntity subentity = (DataMartEntity)it.next();
			if (subentity.getType().equalsIgnoreCase(dataMartEntity.getType())){
				// ciclo di periodo 0!
			} else if(recursionLevel > 10) {
				// prune recursion tree 
			} else {
				addSubEntity(dataMartEntity, 
						subentity,
						recursionLevel+1);
			}
		}
	}
	
	private void addSubEntity (DataMartEntity parentEntity,
			DataMartEntity subEntity, 			
			int recursionLevel){

		DataMartEntity dataMartEntity;				

		
		//String entityName = getEntityNameFromEntityType(entityType);		
		dataMartEntity = parentEntity.addSubEntity(subEntity.getName(), subEntity.getRole(), subEntity.getType());
		
		
		addKeyFields(dataMartEntity);			
		List subEntities = addNormalFields(dataMartEntity);		
		addCalculatedFields(dataMartEntity);
		addSubEntities(dataMartEntity, subEntities, recursionLevel);
	}
	
	private void addKeyFields(DataMartEntity dataMartEntity) {
		
		PersistentClass classMapping;
		ClassMetadata classMetadata;
		Type identifierType;
		
		classMapping = getDataSource().getConfiguration().getClassMapping(dataMartEntity.getType());
		classMetadata = getDataSource().getSessionFactory().getClassMetadata(dataMartEntity.getType());
		identifierType = classMetadata.getIdentifierType();
		
		
		
		List identifierPropertyNames = new ArrayList();
		String[] propertyClass = null;
		String[] type  = null;
		int[] scale  = null;
		int[] precision = null;
		
		String identifierPropertyName = classMetadata.getIdentifierPropertyName();
		
		if (identifierType.isComponentType()) {
			
				ComponentType componentIdentifierType = (ComponentType)identifierType;	
				String[] subPropertyNames = componentIdentifierType.getPropertyNames();
				Property property = classMapping.getIdentifierProperty();
				
				
				Type[] subPropertyTypes = componentIdentifierType.getSubtypes();
				
				propertyClass = new String[subPropertyNames.length];
				type  = new String[subPropertyNames.length];
				scale  = new int[subPropertyNames.length];
				precision = new int[subPropertyNames.length];
				Class subPropertyClass = null;				
				
				for (int j=0; j < subPropertyNames.length; j++){
					subPropertyClass = subPropertyTypes[j].getClass();
					
					if( subPropertyTypes[j].isComponentType() ) {
						ComponentType cType = (ComponentType)subPropertyTypes[j];	
						String[] sPropertyNames = cType.getPropertyNames();
						Type[] sTypes = cType.getSubtypes();
						for(int z = 0; z < sPropertyNames.length; z++) {
							identifierPropertyNames.add(identifierPropertyName + "." + subPropertyNames[j] + "."+ sPropertyNames[z]);
							propertyClass[j] = subPropertyClass.getName();
							type[j] = subPropertyTypes[j].getName();
						}
					} else {
						identifierPropertyNames.add(identifierPropertyName + "." + subPropertyNames[j]);
						propertyClass[j] = subPropertyClass.getName();
						type[j] = subPropertyTypes[j].getName();
					}
				}	
				
				
			
				
		} else {
			
				propertyClass = new String[1];
				type = new String[1];
				scale = new int[1];
				precision = new int[1];
				
				identifierPropertyNames.add(identifierPropertyName);
				propertyClass[0] = identifierType.getClass().getName();
				type[0] = identifierType.getName();						
		}		
		    	
		
		
		Iterator it = classMapping.getIdentifierProperty().getColumnIterator();
		for (int k = 0; k < scale.length; k++){
			if(!it.hasNext()) continue;
			Column column = (Column)it.next();
			scale[k] = column.getScale();
			precision[k] = column.getPrecision();
		}
		
		
		
		for (int j = 0; j < identifierPropertyNames.size(); j++) {
			String fieldName = (String)identifierPropertyNames.get(j);					
			DataMartField dataMartField = dataMartEntity.addKeyField(fieldName);
			dataMartField.setType(type[j]);
			dataMartField.setPrecision(precision[j]);
			dataMartField.setLength(scale[j]);
		}
	}
	
	public List addNormalFields(DataMartEntity dataMartEntity) {
		
		ClassMetadata classMetadata;
		PersistentClass classMapping;
		String[] propertyNames;
		Property property;
		Type propertyType;	
		
		classMetadata = getDataSource().getSessionFactory().getClassMetadata(dataMartEntity.getType());
		classMapping = getDataSource().getConfiguration().getClassMapping(dataMartEntity.getType());		
		propertyNames = classMetadata.getPropertyNames();		
		
			
		List subEntities = new ArrayList();		
		String propertyName = null;
		
		for(int i=0; i < propertyNames.length; i++) { 
			
			property = classMapping.getProperty(propertyNames[i]);
			
			// TEST if they are the same: if so use the first invocation
		 	propertyType = property.getType();
		 	
		 	Iterator columnIterator = property.getColumnIterator();
		 	Column column;
		 	
		 	if (propertyType instanceof ManyToOneType){ // chiave esterna
		 		
		 		ManyToOneType manyToOnePropertyType = (ManyToOneType)propertyType; 
		 		String entityType = manyToOnePropertyType.getAssociatedEntityName();
		 		
			 	String columnName = null;
			 	if (columnIterator.hasNext()){
			 		column = (Column)columnIterator.next();
			 		columnName = column.getName(); // ????
			 	}
			 	
		 		propertyName = propertyNames[i];	
		 		
		 		
		 		//String entityName = getEntityNameFromEntityType(entityType);
		 		String entityName = propertyName;
		 		DataMartEntity subentity = new DataMartEntity(entityName, null, columnName, entityType, dataMartEntity.getStructure());		
		 		subEntities.add(subentity);	
		 		
		 	} else if (propertyType instanceof CollectionType) { // chiave interna
				
		 		
			} else { // normal field
				propertyName = propertyNames[i];
				
				String type = propertyType.getName();
				int scale = 0;
				int precision = 0;
				
								
			 	if (columnIterator.hasNext()){
			 		column = (Column)columnIterator.next();
			 		scale = column.getScale();
			 		precision = column.getPrecision();
			 	}
		 		
			 
					
				DataMartField datamartField = dataMartEntity.addNormalField(propertyName);
				datamartField.setType(type);
				datamartField.setPrecision(precision);
				datamartField.setLength(scale);
		 		
			}
		 }
		
		return subEntities;
	}
	
	private String getEntityNameFromEntityType(String entityType) {
		String entityName = entityType;
		entityName = (entityName.lastIndexOf('.') > 0 ?
				  entityName.substring(entityName.lastIndexOf('.') + 1 , entityName.length()) :
				  entityName);
				  
		return entityName;
	}

	protected IHibernateDataSource getDataSource() {
		return dataSource;
	}

	protected void setDataSource(IHibernateDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
