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
package it.eng.qbe.model.structure.builder.jpa;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.model.structure.builder.IDataMartStructureBuilder;
import it.eng.spagobi.utilities.assertion.Assert;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.log4j.Logger;







/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPADatamartStructureBuilder implements IDataMartStructureBuilder {
	
	private static transient Logger logger = Logger.getLogger(JPADatamartStructureBuilder.class);

	private JPADataSource dataSource;	
	private EntityManager entityManager;


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
		String datamartName;
		Metamodel classMetadata;
		
		logger.debug("Building the data mart structure..");
		dataMartStructure = new DataMartModelStructure();	
		
		datamartName = getDataSource().getConfiguration().getModelName();
		Assert.assertNotNull(getDataSource(), "datasource cannot be null");	
		setEntityManager(getDataSource().getEntityManager());
		Assert.assertNotNull(getEntityManager(), "Impossible to find the jar file associated to datamart named: [" + datamartName + "]");
			
		Map calculatedFields = getDataSource().getConfiguration().getCalculatedFields();
		dataMartStructure.setCalculatedFields(calculatedFields);
			
		classMetadata = getEntityManager().getMetamodel();
							
			
		logger.debug("Loading "+classMetadata.getEntities().size()+" Entities..");
		for(Iterator it = classMetadata.getEntities().iterator(); it.hasNext(); ) {
			EntityType et = (EntityType)it.next();	
			logger.debug("Entity: " + et);
			String entityTypeName =  et.getJavaType().getName();
			addEntity(dataMartStructure, datamartName, entityTypeName);		
		}			
		logger.debug("Data mart structure built..");
		return dataMartStructure;
	}
	
	private void addEntity (DataMartModelStructure dataMartStructure, String datamartName, String entityType){

		String entityName = getEntityNameFromEntityType(entityType);		
		DataMartEntity dataMartEntity = dataMartStructure.addRootEntity(datamartName, entityName, null, null, entityType);
		
		
		//addKeyFields(dataMartEntity);		
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
	 * This method adds the normal fields to the datamart entry structure
	 * @param dataMartEntity:  the datamart structure to complete
	 */
	public List addNormalFields(DataMartEntity dataMartEntity) {		
		logger.debug("Adding the field "+dataMartEntity.getName());
		String[] propertyNames;
		List subEntities = new ArrayList();			
		EntityType thisEntityType = null;
		
		Metamodel classMetadata = getEntityManager().getMetamodel();
		
		for(Iterator it = classMetadata.getEntities().iterator(); it.hasNext(); ) {
			EntityType et = (EntityType)it.next();
			if(et.getJavaType().getName().equals(dataMartEntity.getType())){
				thisEntityType = et;
				break;
			}
		}	
		
		if(thisEntityType==null){
			return new ArrayList();
		}
		
		Set<Attribute> attributes = thisEntityType.getAttributes();
		Iterator<Attribute> attributesIt = attributes.iterator();
		
		
		while(attributesIt.hasNext()){
			Attribute a = attributesIt.next();
			String n = a.getName();
			Member m = a.getJavaMember();
			Class c = a.getJavaType();

			if(a.getPersistentAttributeType().equals(PersistentAttributeType.BASIC)){		
				String type = c.getName();
				
				// TODO: SCALE E PREC
				int scale = 0;
				int precision = 0;

				DataMartField datamartField = dataMartEntity.addNormalField( a.getName());
				datamartField.setType(type);
				datamartField.setPrecision(precision);
				datamartField.setLength(scale);
			}else {
				if(a.getPersistentAttributeType().equals(PersistentAttributeType.MANY_TO_ONE)){
					String entityType = c.getName();
					String columnName = a.getName();
					String entityName =  a.getName();
			 		DataMartEntity subentity = new DataMartEntity(entityName, null, columnName, entityType, dataMartEntity.getStructure());		
			 		subEntities.add(subentity);		
				}
			}
		}
		
		logger.debug("Field "+dataMartEntity.getName()+" added");
		return subEntities;
	}
	
	// TODO: controllare correttezza per jpa...se va bene generalizzare metodo sia per jpa che hibernate!
	private void addCalculatedFields(DataMartEntity dataMartEntity) {
		logger.debug("Adding the calculated field "+dataMartEntity.getName());
		List calculatedFileds;
		DataMartCalculatedField calculatedField;
		
		calculatedFileds = dataMartEntity.getStructure().getCalculatedFieldsByEntity(dataMartEntity.getUniqueName()); 
		if(calculatedFileds != null) {
			for(int i = 0; i < calculatedFileds.size(); i++) {
				calculatedField = (DataMartCalculatedField)calculatedFileds.get(i);
				dataMartEntity.addCalculatedField(calculatedField);
			}
		}
		logger.debug("Added the calculated field "+dataMartEntity.getName());
	}
	
	// TODO: controllare correttezza per jpa...se va bene generalizzare metodo sia per jpa che hibernate! 
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

	// TODO: controllare correttezza per jpa...se va bene generalizzare metodo sia per jpa che hibernate!	
	private void addSubEntity (DataMartEntity parentEntity, DataMartEntity subEntity, int recursionLevel){
		logger.debug("Adding the sub entity field "+subEntity.getName()+" child of "+parentEntity.getName());
		DataMartEntity dataMartEntity;				
		
		//String entityName = getEntityNameFromEntityType(entityType);		
		dataMartEntity = parentEntity.addSubEntity(subEntity.getName(), subEntity.getRole(), subEntity.getType());
		
		//addKeyFields(dataMartEntity);			
		List subEntities = addNormalFields(dataMartEntity);		
		addCalculatedFields(dataMartEntity);
		addSubEntities(dataMartEntity, subEntities, recursionLevel);
		logger.debug("Added the sub entity field "+subEntity.getName()+" child of "+parentEntity.getName());
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
