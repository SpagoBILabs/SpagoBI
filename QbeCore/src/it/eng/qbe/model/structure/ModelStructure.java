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
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ModelStructure extends AbstractModelObject {
	
	
	protected long nextId;	
	protected Map<String, Map<String,ModelEntity>> rootEntities;	// modelName->(entityUniqueName->entity)
	protected Map<String, ModelEntity> entities; //entityUniqueName->entity
	protected Map<String, ModelField> fields; // uniqueName -> field	
	protected Map<String, List<ModelCalculatedField>> calculatedFields; // entity uniqueName -> fields' list
	
	
	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================
	
	/**
	 * Instantiate a new empty ModelStructure object
	 */
	public ModelStructure() {
		nextId = 0;
		id = getNextId();
		name = "Generic Model";
		rootEntities = new HashMap<String, Map<String,ModelEntity>>();
		entities = new HashMap<String, ModelEntity>();
		fields = new HashMap<String, ModelField>();
		calculatedFields = new  HashMap<String, List<ModelCalculatedField>>();
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
	
	public Set<String> getModelNames() {
		return rootEntities.keySet();
	}
	
	// Root Entities -----------------------------------------------------------
	
	/**
	 * Create a new entity and add it to the root entities of the specified model
	 * 
	 * @param name the name of the  to which the new entity will be added
	 * @param path the path of the new entity
	 * @param role the role of the new entity
	 * @param type the type of the new entity
	 * 
	 * @return the new entity added to the model
	 */
	public ModelEntity addRootEntity(String modelName, String name, String path, String role, String type) {
		ModelEntity entity = new ModelEntity(name, path, role, type, this);
		addRootEntity(modelName, entity);
		return entity;
	}
	
	private void addRootEntity(String modelName, ModelEntity entity) {
		Map<String, ModelEntity> modeltRootEntities;
		
		modeltRootEntities = rootEntities.get(modelName);
		if (modeltRootEntities == null) {
			modeltRootEntities = new HashMap<String, ModelEntity>();
			rootEntities.put(modelName, modeltRootEntities);
		}
		modeltRootEntities.put(entity.getUniqueName(), entity);
		addEntity(entity);
	}
	
	
	/**
	 * Gets a root entity by name from the specified model.
	 * 
	 * @param modelName the name of the target model 
	 * @param entityName the name of the entity to look for
	 * 
	 * @return the searched root entity
	 */
	public ModelEntity getRootEntity(String modelName, String entityName) {
		Map<String, ModelEntity> modelRootEntities = rootEntities.get(modelName);
		return modelRootEntities == null ? null : (ModelEntity)modelRootEntities.get(entityName);
	}
	
	/**
	 * Gets the root entity relevant to the input entity
	 * 
	 * @param entity
	 * 
	 * @return the root entity relevant to the input entity
	 */
	public ModelEntity getRootEntity(ModelEntity entity) {
		if (entity == null) {
			return null;
		}
		ModelEntity toReturn = null;
		Iterator<String> keysIt = rootEntities.keySet().iterator();
		while (keysIt.hasNext()) {
			String modelName = keysIt.next();
			ModelEntity rootEntity = getRootEntity(entity, modelName);
			if (rootEntity != null) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/**
	 * Gets the root entity relevant to the input entity in the 	 * specified in input
	 * 
	 * @param the target entity 
	 * @param the name of the target model
	 * 
	 * @return the root entity relevant to the input entity in the model 
	 * specified in input
	 */
	public ModelEntity getRootEntity(ModelEntity entity, String modelName) {
		if (entity == null) {
			return null;
		}
		ModelEntity toReturn = null;
		List<ModelEntity> rootEntities = getRootEntities(modelName);
		Iterator<ModelEntity> rootEntitiesIt = rootEntities.iterator();
		while (rootEntitiesIt.hasNext()) {
			ModelEntity rootEntity = (ModelEntity) rootEntitiesIt.next();
			if (entity.getType().equals(rootEntity.getType())) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/**
	 * Gets the root entity iterator for the target model.
	 * 
	 * @param modelName the name of the target model
	 * 
	 * @return the root entities iterator
	 */
	public Iterator<ModelEntity> getRootEntityIterator(String modelName) {
		return getRootEntities(modelName).iterator();
	}
	
	/**
	 * Gets the root entities.
	 * 
	 * @param modelName the name of the target model
	 * 
	 * @return the root entities
	 */
	public List<ModelEntity> getRootEntities(String modelName) {
		List<ModelEntity> list = new ArrayList<ModelEntity>();
		Map<String, ModelEntity> modelRootEntities;
		
		list = new ArrayList<ModelEntity>();
		modelRootEntities = rootEntities.get(modelName);
		
		if (modelRootEntities != null) {
			Iterator<String> it = modelRootEntities.keySet().iterator();
			while(it.hasNext()) {
				String entityName = it.next();
				// TODO replace with this ...
				//list.add( entities.get(entityName).getCopy() );
				list.add( modelRootEntities.get(entityName) );
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
	public void addEntity(ModelEntity entity) {
		entities.put(entity.getUniqueName(), entity);
	}
	
	/**
	 * Gets the entity.
	 * 
	 * @param entityUniqueName the entity unique name
	 * 
	 * @return the entity
	 */
	public ModelEntity getEntity(String entityUniqueName) {
		ModelEntity entity = (ModelEntity)entities.get(entityUniqueName);
		return entity;
	}
	
	// Fields -----------------------------------------------------------
	
	/**
	 * Adds the field.
	 * 
	 * @param field the field
	 */
	public void addField(ModelField field) {
		fields.put(field.getUniqueName(), field);
	}
	
	/**
	 * Gets the field.
	 * 
	 * @param fieldUniqueName the field unique name
	 * 
	 * @return the field
	 */
	public ModelField getField(String fieldUniqueName) {
		ModelField field = (ModelField)fields.get(fieldUniqueName);
		return field;
	}
	
	// Calculated Fields ----------------------------------------------------
	
	public Map<String, List<ModelCalculatedField>> getCalculatedFields() {
		return calculatedFields;
	}
	
	public List<ModelCalculatedField> getCalculatedFieldsByEntity(String entityName) {
		List<ModelCalculatedField> result;
		
		result = new ArrayList<ModelCalculatedField>();
		if(calculatedFields.containsKey(entityName)) {
			result.addAll( calculatedFields.get(entityName) );
		}
		
		return result;
	}

	public void setCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) {
		this.calculatedFields = calculatedFields;
	}
	
	public void addCalculatedField(String entityName, ModelCalculatedField calculatedFiled) {
		List<ModelCalculatedField> calculatedFiledsOnTargetEntity;
		if(!calculatedFields.containsKey(entityName)) {
			calculatedFields.put(entityName, new ArrayList<ModelCalculatedField>());
		}
		calculatedFiledsOnTargetEntity = calculatedFields.get(entityName);	
		List<ModelCalculatedField> toRemove = new ArrayList<ModelCalculatedField>();
		for(int i = 0; i < calculatedFiledsOnTargetEntity.size(); i++) {
			ModelCalculatedField f = (ModelCalculatedField)calculatedFiledsOnTargetEntity.get(i);
			if(f.getName().equals(calculatedFiled.getName())) {
				toRemove.add(f);
			}
		}
		for(int i = 0; i < toRemove.size(); i++) {
			calculatedFiledsOnTargetEntity.remove(toRemove.get(i));
		}
		calculatedFiledsOnTargetEntity.add(calculatedFiled);
	}
	
	public void removeCalculatedFiield(String entityName, ModelCalculatedField calculatedFiled) {
		List<ModelCalculatedField> calculatedFieldsOnTargetEntity;
		
		calculatedFieldsOnTargetEntity = calculatedFields.get(entityName);	
		if(calculatedFieldsOnTargetEntity != null) {
			calculatedFieldsOnTargetEntity.remove(calculatedFiled);
		}
	}
}
