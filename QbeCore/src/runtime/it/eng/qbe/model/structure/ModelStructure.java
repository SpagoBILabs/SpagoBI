/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
	protected Map<String, IModelField> fields; // uniqueName -> field	
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
		fields = new HashMap<String, IModelField>();
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

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addRootEntity(String modelName, String name, String path, String role, String type)
	 */
	public IModelEntity addRootEntity(String modelName, String name, String path, String role, String type) {
		IModelEntity entity = new ModelEntity(name, path, role, type, this);
		addRootEntity(modelName, entity);
		return entity;
	}
	
	public void addRootEntity(String modelName, IModelEntity entity) {
		Map<String, IModelEntity> modeltRootEntities;
		
		modeltRootEntities = rootEntities.get(modelName);
		if (modeltRootEntities == null) {
			modeltRootEntities = new HashMap<String, IModelEntity>();
			rootEntities.put(modelName, modeltRootEntities);
		}
		modeltRootEntities.put(entity.getUniqueName(), entity);
		addEntity(entity);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntity(String modelName, String entityName)
	 */
	public IModelEntity getRootEntity(String modelName, String entityName) {
		Map<String, IModelEntity> modelRootEntities = rootEntities.get(modelName);
		//Set test = modelRootEntities.keySet();
		return modelRootEntities == null ? null : (IModelEntity)modelRootEntities.get(entityName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntity(IModelEntity entity)
	 */
	public IModelEntity getRootEntity(IModelEntity entity) {
		if (entity == null) {
			return null;
		}
		IModelEntity toReturn = null;
		Iterator<String> keysIt = getModelNames().iterator();
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
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntity(IModelEntity entity, String modelName) 
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
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntityIterator(String modelName)
	 */
	public Iterator<IModelEntity> getRootEntityIterator(String modelName) {
		return getRootEntities(modelName).iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getRootEntities(String modelName)
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

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#ddEntity(IModelEntity entity) 
	 */
	public void addEntity(IModelEntity entity) {
		entities.put(entity.getUniqueName(), entity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getEntity(String entityUniqueName)
	 */
	public IModelEntity getEntity(String entityUniqueName) {
		IModelEntity entity = (IModelEntity)entities.get(entityUniqueName);
		return entity;
	}
	
	// Fields -----------------------------------------------------------
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addField(IModelField field)
	 */
	public void addField(IModelField field) {
		fields.put(field.getUniqueName(), field);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure# getField(String fieldUniqueName)
	 */
	public IModelField getField(String fieldUniqueName) {
		IModelField field = (IModelField)fields.get(fieldUniqueName);
		return field;
	}
	
	// Calculated Fields ----------------------------------------------------
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getCalculatedFields()
	 */
	public Map<String, List<ModelCalculatedField>> getCalculatedFields() {
		return calculatedFields;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#getCalculatedFieldsByEntity(String entityName)
	 */
	public List<ModelCalculatedField> getCalculatedFieldsByEntity(String entityName) {
		List<ModelCalculatedField> result;
		
		result = new ArrayList<ModelCalculatedField>();
		if(calculatedFields.containsKey(entityName)) {
			result.addAll( calculatedFields.get(entityName) );
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#setCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields)
	 */
	public void setCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) {
		this.calculatedFields = calculatedFields;
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#addCalculatedField(String entityName, ModelCalculatedField calculatedFiled)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.qbe.model.structure.IModelStructure#removeCalculatedField(String entityName, ModelCalculatedField calculatedFiled)
	 */
	public void removeCalculatedField(String entityName, ModelCalculatedField calculatedFiled) {
		List<ModelCalculatedField> calculatedFieldsOnTargetEntity;
		
		calculatedFieldsOnTargetEntity = calculatedFields.get(entityName);	
		if(calculatedFieldsOnTargetEntity != null) {
			calculatedFieldsOnTargetEntity.remove(calculatedFiled);
		}
	}
}
