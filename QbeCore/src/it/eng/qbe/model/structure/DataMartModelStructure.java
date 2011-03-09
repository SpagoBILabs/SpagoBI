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
package it.eng.qbe.model.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DataMartModelStructure extends AbstractDataMartObject {
	
	
	protected long nextId;	
	protected Map<String, Map<String,DataMartEntity>> rootEntities;	// datamartName->(entityUniqueName->entity)
	protected Map<String, DataMartEntity> entities; //entityUniqueName->entity
	protected Map<String, DataMartField> fields; // uniqueName -> field	
	protected Map<String, List<DataMartCalculatedField>> calculatedFields; // entity uniqueName -> fields' list
	
	
	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================
	
	/**
	 * Instantiate a new empty DataMartModelStructure object
	 */
	public DataMartModelStructure() {
		nextId = 0;
		id = getNextId();
		name = "Generic Datamart";
		rootEntities = new HashMap<String, Map<String,DataMartEntity>>();
		entities = new HashMap<String, DataMartEntity>();
		fields = new HashMap<String, DataMartField>();
		calculatedFields = new  HashMap<String, List<DataMartCalculatedField>>();
		initProperties();
		
	}
	
	
	// =========================================================================
	// ACCESORS 
	// =========================================================================
	
	/**
	 * Gets the next id.
	 * 
	 * @return the next id
	 */
	public long getNextId() {
		return nextId++;
	}
	
	// Root Entities -----------------------------------------------------------
	
	/**
	 * Create a new entity and add it to the root entities of the specified datamart
	 * 
	 * @param name the name of the datamart to which the new entity will be added
	 * @param path the path of the new entity
	 * @param role the role of the new entity
	 * @param type the type of the new entity
	 * 
	 * @return the new entity added to the datamart
	 */
	public DataMartEntity addRootEntity(String datamartName, String name, String path, String role, String type) {
		DataMartEntity entity = new DataMartEntity(name, path, role, type, this);
		addRootEntity(datamartName, entity);
		return entity;
	}
	
	private void addRootEntity(String datamartName, DataMartEntity entity) {
		Map<String, DataMartEntity> datamartRootEntities;
		
		datamartRootEntities = rootEntities.get(datamartName);
		if (datamartRootEntities == null) {
			datamartRootEntities = new HashMap<String, DataMartEntity>();
			rootEntities.put(datamartName, datamartRootEntities);
		}
		datamartRootEntities.put(entity.getUniqueName(), entity);
		addEntity(entity);
	}
	
	
	/**
	 * Gets a root entity by name from the specified datamart.
	 * 
	 * @param datamartName the name of the target datamart 
	 * @param entityName the name of the entity to look for
	 * 
	 * @return the searched root entity
	 */
	public DataMartEntity getRootEntity(String datamartName, String entityName) {
		Map<String, DataMartEntity> datamartRootEntities = rootEntities.get(datamartName);
		return datamartRootEntities == null ? null : (DataMartEntity)datamartRootEntities.get(entityName);
	}
	
	/**
	 * Gets the root entity relevant to the input entity
	 * @param entity
	 * @return the root entity relevant to the input entity
	 */
	public DataMartEntity getRootEntity(DataMartEntity entity) {
		if (entity == null) {
			return null;
		}
		DataMartEntity toReturn = null;
		Iterator<String> keysIt = rootEntities.keySet().iterator();
		while (keysIt.hasNext()) {
			String datamartName = keysIt.next();
			DataMartEntity rootEntity = getRootEntity(entity, datamartName);
			if (rootEntity != null) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/**
	 * Gets the root entity relevant to the input entity in the datamart 
	 * specified in input
	 * 
	 * @param the target entity 
	 * @param the name of the target datamart
	 * 
	 * @return the root entity relevant to the input entity in the datamart 
	 * specified in input
	 */
	public DataMartEntity getRootEntity(DataMartEntity entity, String datamartName) {
		if (entity == null) {
			return null;
		}
		DataMartEntity toReturn = null;
		List<DataMartEntity> rootEntities = getRootEntities(datamartName);
		Iterator<DataMartEntity> rootEntitiesIt = rootEntities.iterator();
		while (rootEntitiesIt.hasNext()) {
			DataMartEntity rootEntity = (DataMartEntity) rootEntitiesIt.next();
			if (entity.getType().equals(rootEntity.getType())) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/**
	 * Gets the root entity iterator for the target datamart.
	 * 
	 * @param datamartName the name of the target datamart
	 * 
	 * @return the root entities iterator
	 */
	public Iterator<DataMartEntity> getRootEntityIterator(String datamartName) {
		return getRootEntities(datamartName).iterator();
	}
	
	/**
	 * Gets the root entities.
	 * 
	 * @param datamartName datamartName the name of the target datamart
	 * 
	 * @return the root entities
	 */
	public List<DataMartEntity> getRootEntities(String datamartName) {
		List<DataMartEntity> list = new ArrayList<DataMartEntity>();
		Map<String, DataMartEntity> datamartRootEntities;
		
		list = new ArrayList<DataMartEntity>();
		datamartRootEntities = rootEntities.get(datamartName);
		
		if (datamartRootEntities != null) {
			Iterator<String> it = datamartRootEntities.keySet().iterator();
			while(it.hasNext()) {
				String entityName = it.next();
				// TODO replace with this ...
				//list.add( entities.get(entityName).getCopy() );
				list.add( datamartRootEntities.get(entityName) );
			}
		}
		return list;
	}	
	

	// Entities -----------------------------------------------------------

	/**
	 * Adds the entity.
	 * 
	 * @param entity the entity
	 */
	public void addEntity(DataMartEntity entity) {
		entities.put(entity.getUniqueName(), entity);
	}
	
	/**
	 * Gets the entity.
	 * 
	 * @param entityUniqueName the entity unique name
	 * 
	 * @return the entity
	 */
	public DataMartEntity getEntity(String entityUniqueName) {
		DataMartEntity entity = (DataMartEntity)entities.get(entityUniqueName);
		return entity;
	}
	
	// Fields -----------------------------------------------------------
	
	/**
	 * Adds the field.
	 * 
	 * @param field the field
	 */
	public void addField(DataMartField field) {
		fields.put(field.getUniqueName(), field);
	}
	
	/**
	 * Gets the field.
	 * 
	 * @param fieldUniqueName the field unique name
	 * 
	 * @return the field
	 */
	public DataMartField getField(String fieldUniqueName) {
		DataMartField field = (DataMartField)fields.get(fieldUniqueName);
		return field;
	}
	
	// Calculated Fields ----------------------------------------------------
	
	public Map<String, List<DataMartCalculatedField>> getCalculatedFields() {
		return calculatedFields;
	}
	
	public List<DataMartCalculatedField> getCalculatedFieldsByEntity(String entityName) {
		List<DataMartCalculatedField> result;
		
		result = new ArrayList<DataMartCalculatedField>();
		if(calculatedFields.containsKey(entityName)) {
			result.addAll( calculatedFields.get(entityName) );
		}
		
		return result;
	}

	public void setCalculatedFields(Map<String, List<DataMartCalculatedField>> calculatedFields) {
		this.calculatedFields = calculatedFields;
	}
	
	public void addCalculatedField(String entityName, DataMartCalculatedField calculatedFiled) {
		List<DataMartCalculatedField> calculatedFiledsOnTargetEntity;
		if(!calculatedFields.containsKey(entityName)) {
			calculatedFields.put(entityName, new ArrayList<DataMartCalculatedField>());
		}
		calculatedFiledsOnTargetEntity = calculatedFields.get(entityName);	
		List<DataMartCalculatedField> toRemove = new ArrayList<DataMartCalculatedField>();
		for(int i = 0; i < calculatedFiledsOnTargetEntity.size(); i++) {
			DataMartCalculatedField f = (DataMartCalculatedField)calculatedFiledsOnTargetEntity.get(i);
			if(f.getName().equals(calculatedFiled.getName())) {
				toRemove.add(f);
			}
		}
		for(int i = 0; i < toRemove.size(); i++) {
			calculatedFiledsOnTargetEntity.remove(toRemove.get(i));
		}
		calculatedFiledsOnTargetEntity.add(calculatedFiled);
	}
	
	public void removeCalculatedFiield(String entityName, DataMartCalculatedField calculatedFiled) {
		List<DataMartCalculatedField> calculatedFieldsOnTargetEntity;
		
		calculatedFieldsOnTargetEntity = calculatedFields.get(entityName);	
		if(calculatedFieldsOnTargetEntity != null) {
			calculatedFieldsOnTargetEntity.remove(calculatedFiled);
		}
	}
}
