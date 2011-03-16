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
public class ModelStructure extends AbstractModelObject implements IModelStructure {
	
	
	protected long nextId;	
	protected Map<String, Map<String,IModelEntity>> rootEntities;	// modelName->(entityUniqueName->entity)
	protected Map<String, IModelEntity> entities; //entityUniqueName->entity
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
		rootEntities = new HashMap<String, Map<String,IModelEntity>>();
		entities = new HashMap<String, IModelEntity>();
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
	public IModelEntity addRootEntity(String modelName, String name, String path, String role, String type) {
		IModelEntity entity = new ModelEntity(name, path, role, type, this);
		addRootEntity(modelName, entity);
		return entity;
	}
	
	private void addRootEntity(String modelName, IModelEntity entity) {
		Map<String, IModelEntity> modeltRootEntities;
		
		modeltRootEntities = rootEntities.get(modelName);
		if (modeltRootEntities == null) {
			modeltRootEntities = new HashMap<String, IModelEntity>();
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
	public IModelEntity getRootEntity(String modelName, String entityName) {
		Map<String, IModelEntity> modelRootEntities = rootEntities.get(modelName);
		return modelRootEntities == null ? null : (IModelEntity)modelRootEntities.get(entityName);
	}
	
	/**
	 * Gets the root entity relevant to the input entity
	 * 
	 * @param entity
	 * 
	 * @return the root entity relevant to the input entity
	 */
	public IModelEntity getRootEntity(IModelEntity entity) {
		if (entity == null) {
			return null;
		}
		IModelEntity toReturn = null;
		Iterator<String> keysIt = rootEntities.keySet().iterator();
		while (keysIt.hasNext()) {
			String modelName = keysIt.next();
			IModelEntity rootEntity = getRootEntity(entity, modelName);
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
	public IModelEntity getRootEntity(IModelEntity entity, String modelName) {
		if (entity == null) {
			return null;
		}
		IModelEntity toReturn = null;
		List<IModelEntity> rootEntities = getRootEntities(modelName);
		Iterator<IModelEntity> rootEntitiesIt = rootEntities.iterator();
		while (rootEntitiesIt.hasNext()) {
			IModelEntity rootEntity = (IModelEntity) rootEntitiesIt.next();
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
	public Iterator<IModelEntity> getRootEntityIterator(String modelName) {
		return getRootEntities(modelName).iterator();
	}
	
	/**
	 * Gets the root entities.
	 * 
	 * @param modelName the name of the target model
	 * 
	 * @return the root entities
	 */
	public List<IModelEntity> getRootEntities(String modelName) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		Map<String, IModelEntity> modelRootEntities;
		
		list = new ArrayList<IModelEntity>();
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
	public void addEntity(IModelEntity entity) {
		entities.put(entity.getUniqueName(), entity);
	}
	
	/**
	 * Gets the entity.
	 * 
	 * @param entityUniqueName the entity unique name
	 * 
	 * @return the entity
	 */
	public IModelEntity getEntity(String entityUniqueName) {
		IModelEntity entity = (IModelEntity)entities.get(entityUniqueName);
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
	
	public void removeCalculatedField(String entityName, ModelCalculatedField calculatedFiled) {
		List<ModelCalculatedField> calculatedFieldsOnTargetEntity;
		
		calculatedFieldsOnTargetEntity = calculatedFields.get(entityName);	
		if(calculatedFieldsOnTargetEntity != null) {
			calculatedFieldsOnTargetEntity.remove(calculatedFiled);
		}
	}
}
