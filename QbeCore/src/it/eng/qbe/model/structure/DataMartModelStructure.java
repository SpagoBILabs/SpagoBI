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
 * @author Andrea Gioia
 */
public class DataMartModelStructure {
	
	
	long id = 0;
	
	Map rootEntities;
	
	Map fields; // uniqueName -> field
	
	Map cfields; // entity uniqueName -> fields' list
	
	public Map getCalculatedFields() {
		return cfields;
	}
	
	public List getCalculatedFieldsByEntity(String entityName) {
		List result;
		
		result = new ArrayList();
		if(cfields.containsKey(entityName)) {
			result.addAll( (List)cfields.get(entityName) );
		}
		
		return result;
	}

	public void setCalculatedFields(Map calculatedFields) {
		this.cfields = calculatedFields;
	}
	
	public void addCalculatedField(String entityName, DataMartCalculatedField calculatedFiled) {
		List cfiledsOnTargetEntity;
		if(!cfields.containsKey(entityName)) {
			cfields.put(entityName, new ArrayList());
		}
		cfiledsOnTargetEntity = (List)cfields.get(entityName);	
		List toRemove = new ArrayList();
		for(int i = 0; i < cfiledsOnTargetEntity.size(); i++) {
			DataMartCalculatedField f = (DataMartCalculatedField)cfiledsOnTargetEntity.get(i);
			if(f.getName().equals(calculatedFiled.getName())) {
				toRemove.add(f);
			}
		}
		for(int i = 0; i < toRemove.size(); i++) {
			cfiledsOnTargetEntity.remove(toRemove.get(i));
		}
		cfiledsOnTargetEntity.add(calculatedFiled);
	}
	
	public void removeCalculatedFiield(String entityName, DataMartCalculatedField calculatedFiled) {
		List cfiledsOnTargetEntity;
		
		cfiledsOnTargetEntity = (List)cfields.get(entityName);	
		if(cfiledsOnTargetEntity != null) {
			cfiledsOnTargetEntity.remove(calculatedFiled);
		}
	}
	






	Map entities;
	
	
	
	/**
	 * Instantiates a new data mart model structure.
	 */
	public DataMartModelStructure() {
		rootEntities = new HashMap();
		fields = new HashMap();
		cfields = new  HashMap();
		entities = new HashMap();
	}
	
	/**
	 * Adds the root entity.
	 * 
	 * @param name the name
	 * @param path the path
	 * @param role the role
	 * @param type the type
	 * 
	 * @return the data mart entity
	 */
	public DataMartEntity addRootEntity(String datamartName, String name, String path, String role, String type) {
		DataMartEntity entity = new DataMartEntity(name, path, role, type, this);
		addRootEntity(datamartName, entity);
		return entity;
	}
	
	/**
	 * Adds the root entity.
	 * 
	 * @param entity the entity
	 */
	private void addRootEntity(String datamartName, DataMartEntity entity) {
		HashMap datamartRootEntities = (HashMap) rootEntities.get(datamartName);
		if (datamartRootEntities == null) {
			datamartRootEntities = new HashMap();
			rootEntities.put(datamartName, datamartRootEntities);
		}
		datamartRootEntities.put(entity.getUniqueName(), entity);
		addEntity(entity);
	}
	
	
	/**
	 * Gets the root entity.
	 * 
	 * @param entityName the entity name
	 * 
	 * @return the root entity
	 */
	public DataMartEntity getRootEntity(String datamartName, String entityName) {
		HashMap datamartRootEntities = (HashMap) rootEntities.get(datamartName);
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
		Iterator keysIt = rootEntities.keySet().iterator();
		while (keysIt.hasNext()) {
			String datamartName = (String) keysIt.next();
			DataMartEntity rootEntity = getRootEntity(entity, datamartName);
			if (rootEntity != null) {
				toReturn = rootEntity;
				break;
			}
		}
		return toReturn;
	}
	
	/**
	 * Gets the root entity relevant to the input entity in the datamart specified in input
	 * @param entity
	 * @return the root entity relevant to the input entity in the datamart specified in input
	 */
	public DataMartEntity getRootEntity(DataMartEntity entity, String datamartName) {
		if (entity == null) {
			return null;
		}
		DataMartEntity toReturn = null;
		List rootEntities = getRootEntities(datamartName);
		Iterator rootEntitiesIt = rootEntities.iterator();
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
	 * Gets the root entity iterator.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the root entity iterator
	 */
	public Iterator getRootEntityIterator(String datamartName) {
		return getRootEntities(datamartName).iterator();
	}
	
	/**
	 * Gets the root entities.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return the root entities
	 */
	public List getRootEntities(String datamartName) {
		List list = new ArrayList();
		HashMap datamartRootEntities = (HashMap) rootEntities.get(datamartName);
		if (datamartRootEntities != null) {
			Iterator it = datamartRootEntities.keySet().iterator();
			while(it.hasNext()) {
				String entityName = (String)it.next();
				// TODO replace with this ...
				//list.add( entities.get(entityName).getCopy() );
				list.add( datamartRootEntities.get(entityName) );
			}
		}
		return list;
	}	
		
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
	
	
	
	
	/**
	 * Gets the next id.
	 * 
	 * @return the next id
	 */
	public long getNextId() {
		return ++id;
	}
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/*
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String key = null;
		for(Iterator it = rootEntities.keySet().iterator(); it.hasNext();) {
			key = (String)it.next();
			DataMartEntity o = (DataMartEntity)rootEntities.get(key);
			buffer.append("\n------------------------------------\n");
			if(o == null)
				buffer.append(key + " --> NULL\n");
			else
				buffer.append(o.toString() + "\n");
		}
		return buffer.toString();
	}
	*/ 
}
