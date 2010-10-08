/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.model.structure.builder;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.log4j.Logger;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPADatamartStructureBuilder implements IDataMartStructureBuilder {
	
	private static transient Logger logger = Logger.getLogger(JPADatamartStructureBuilder.class);

	private JPADataSource dataSource;	
	private EntityManager entityManager;
	private EntityType entityType;
	
	

	/**
	 * Constructor
	 * @param dataSource the JPA DataSource
	 */
	public JPADatamartStructureBuilder(JPADataSource dataSource) {
		if(dataSource== null) {
			throw new IllegalArgumentException("DataSource parameter cannot be null");
		}
		setDataSource( dataSource );
	}
	
	/**
	 * This method builds a JPA datamart structure.
	 * @return DataMartModelStructure
	 */
	public DataMartModelStructure build() {
		DataMartModelStructure dataMartStructure;
		List datamartNames;
		String datamartName;
		Metamodel classMetadata;
			
		dataMartStructure = new DataMartModelStructure();	
		
		datamartNames = getDataSource().getDatamartNames();

		for(int i = 0; i < datamartNames.size(); i++) {
			datamartName = (String)datamartNames.get(i);
			Assert.assertNotNull(getDataSource(), "datasource cannot be null");	
			//EntityManagerFactory emf = getDataSource().getEntityManagerFactory(datamartName);
			setEntityManager(getDataSource().getEntityManager());
			Assert.assertNotNull(getEntityManager(), "Impossible to find the jar file associated to datamart named: [" + datamartName + "]");
			
			Map calculatedFields = DAOFactory.getCalculatedFieldsDAO().loadCalculatedFields(datamartName);
			dataMartStructure.setCalculatedFields(calculatedFields);
			
			classMetadata = getEntityManager().getMetamodel();
						
			for(Iterator it = classMetadata.getEntities().iterator(); it.hasNext(); ) {
				setEntityType((EntityType)it.next());	
				logger.debug("Entity: " + getEntityType());
				String entityTypeName =  getEntityType().getJavaType().getName();
				addEntity(dataMartStructure, datamartName, entityTypeName);		
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
	
	private String getEntityNameFromEntityType(String entityType) {
		String entityName = entityType;
		entityName = (entityName.lastIndexOf('.') > 0 ?
				  entityName.substring(entityName.lastIndexOf('.') + 1 , entityName.length()) :
				  entityName);
				  
		return entityName;
	}
	 
	/**
	 * This method adds the key fields to the datamart entry structure
	 * @param dataMartEntity:  the datamart structure to complete
	 */
	private void addKeyFields(DataMartEntity dataMartEntity) {
		List identifierPropertyNames = new ArrayList();
		String[] propertyClass = null;
		String[] type  = null;
		int[] scale  = null;
		int[] precision = null;
		
		
		ClassDescriptor cd = getEntityManager().unwrap(JpaEntityManager.class).getServerSession().getDescriptor(getEntityType().getJavaType());
		
		int numKeyFields = cd.getPrimaryKeyFields().size();
		type  = new String[numKeyFields];
		scale  = new int[numKeyFields];
		precision = new int[numKeyFields];
		propertyClass = new String[numKeyFields];
		
		if (numKeyFields == 1){
			SingularAttribute keyAttr = getEntityType().getId(getEntityType().getIdType().getJavaType());
			DatabaseMapping dmp = cd.getMappingForAttributeName( keyAttr.getName());
			identifierPropertyNames.add(keyAttr.getName());
			
			propertyClass[0] = dmp.getAttributeClassification().getName();
			type[0] = dmp.getField().getTypeName();		
			scale[0] = dmp.getField().getScale();
			precision[0] = dmp.getField().getPrecision();
			
		}else{
			int i=0;
			for(Iterator it =  getEntityType().getIdClassAttributes().iterator(); it.hasNext(); ) {
				SingularAttribute keyAttr = (SingularAttribute)it.next();
				DatabaseMapping dmp = cd.getMappingForAttributeName( keyAttr.getName());
				identifierPropertyNames.add(keyAttr.getName());
				
				propertyClass[i] = dmp.getField().getName();
				type[i] = dmp.getField().getTypeName();		
				scale[i] = dmp.getField().getScale();
				precision[i] = dmp.getField().getPrecision();
				i++;
			}	
		}
		
		for (int j = 0; j < identifierPropertyNames.size(); j++) {
			String fieldName = (String)identifierPropertyNames.get(j);					
			DataMartField dataMartField = dataMartEntity.addKeyField(fieldName);
			dataMartField.setType(type[j]);
			dataMartField.setPrecision(precision[j]);
			dataMartField.setLength(scale[j]);
		}
		
	}
	
	/**
	 * This method adds the normal fields to the datamart entry structure
	 * @param dataMartEntity:  the datamart structure to complete
	 */
	public List addNormalFields(DataMartEntity dataMartEntity) {		

		String[] propertyNames;
		List subEntities = new ArrayList();			

		ClassDescriptor cd = getEntityManager().unwrap(JpaEntityManager.class).getServerSession().getDescriptor(getEntityType().getJavaType());
		Vector<DatabaseMapping> dbMaps = cd.getMappings();
		
		for (int i=0, l=dbMaps.size(); i<l; i++){
			DatabaseMapping fieldMap = dbMaps.get(i);			
			if (fieldMap instanceof DirectToFieldMapping){ // field normal or table key		
				if (!fieldMap.isPrimaryKeyMapping()){
					DatabaseField field = fieldMap.getField();						
					String type = field.getTypeName();
					int scale = field.getScale();
					int precision = field.getPrecision();

					DataMartField datamartField = dataMartEntity.addNormalField(fieldMap.getAttributeName());
					datamartField.setType(type);
					datamartField.setPrecision(precision);
					datamartField.setLength(scale);
					
				}else {
					logger.debug("It's a key already loaded!");
				}
			}else if (fieldMap instanceof ManyToOneMapping){ // fk	
				String entityType = ((ManyToOneMapping) fieldMap).getReferenceClassName();
				String columnName = fieldMap.getReferenceDescriptor().getCacheKeyType().name();
				String entityName = fieldMap.getReferenceDescriptor().getAlias();
		 		DataMartEntity subentity = new DataMartEntity(entityName, null, columnName, entityType, dataMartEntity.getStructure());		
		 		subEntities.add(subentity);	
			}
		}
	
		return subEntities;
	}
	
	// controllare correttezza per jpa...se va bene generalizzare metodo sia per jpa che hibernate!
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
	
	// controllare correttezza per jpa...se va bene generalizzare metodo sia per jpa che hibernate! 
	private void addSubEntities(DataMartEntity dataMartEntity, List subEntities, int recursionLevel) {
		
		Iterator it = subEntities.iterator();
		while (it.hasNext()) {
			DataMartEntity subentity = (DataMartEntity)it.next();
			if (subentity.getType().equalsIgnoreCase(dataMartEntity.getType())){
				// ciclo di periodo 0!
			} else if(recursionLevel > 10) {
				// prune recursion tree 
			} else {
				addSubEntity(dataMartEntity, subentity, recursionLevel+1);
			}
		}
	}

	// controllare correttezza per jpa...se va bene generalizzare metodo sia per jpa che hibernate!	
	private void addSubEntity (DataMartEntity parentEntity, DataMartEntity subEntity, int recursionLevel){

		DataMartEntity dataMartEntity;				
		
		//String entityName = getEntityNameFromEntityType(entityType);		
		dataMartEntity = parentEntity.addSubEntity(subEntity.getName(), subEntity.getRole(), subEntity.getType());
		
		addKeyFields(dataMartEntity);			
		List subEntities = addNormalFields(dataMartEntity);		
		addCalculatedFields(dataMartEntity);
		addSubEntities(dataMartEntity, subEntities, recursionLevel);
	}
	
	/**
	 * @return the JPADataSource
	 */
	public JPADataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param JPADataSource the datasource to set
	 */
	public void setDataSource(JPADataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the entityType
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


}
