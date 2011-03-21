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

import it.eng.spagobi.commons.utilities.StringUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Gioia
 */
public class ModelEntity extends AbstractModelNode implements IModelEntity{
	
	protected IModelEntity root;	
	
	protected String path;		
	protected String role;	
	protected String type;	
	
	protected Map<String,ModelField> fields;	
	protected Map<String, ModelCalculatedField> calculatedFields;	
	protected Map<String,IModelEntity> subEntities;
	

	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================
	
	
	public ModelEntity(String name, String path, String role, String type,	IModelStructure structure) {
		
		setStructure( structure );
		
		setId ( structure.getNextId() );
		setName( name );		
		setPath( path == null? "" : path );
		setRole( role );
		setType( type );
		
		setParent(null);
		this.fields = new HashMap<String,ModelField>();
		this.calculatedFields = new HashMap<String, ModelCalculatedField>();
		this.subEntities = new HashMap<String,IModelEntity>();
		
		initProperties();
	}
	
	// =========================================================================
	// ACCESORS 
	// =========================================================================
	
	
	public String getUniqueName() {
		String uniqueName = "";
			
		uniqueName += getRoot().getType() + ":";
		uniqueName += getPath() + ":";
		uniqueName += getName();
		if(getRole() != null) uniqueName +=  "(" + getRole() + ")";
		
		return uniqueName;
	}
	
	public boolean equals(Object o){
		if ( this == o ) return true;
		if ( !(o instanceof ModelEntity) ) return false;
		ModelEntity de = (ModelEntity)o;
		return this.getUniqueName().equals( de.getUniqueName() );
	}

	
	public String getUniqueType() {
		String entityType = getType();
		if ( !StringUtilities.isEmpty( getRole() ) ) {
			entityType += "(" + getRole() + ")";
		}
		return entityType;
	}
	
	
	private void addField(ModelField field) {
		fields.put(field.getUniqueName(), field);
		getStructure().addField(field);
	}
	
	
	private ModelField addField(String fieldName, boolean isKey) {
		
		ModelField field = new ModelField(fieldName, this);
		field.setKey(isKey);
		addField(field);
		return field;
	}
	
	
	public ModelField addNormalField(String fieldName) {
		return addField(fieldName, false);
	}
	
	
	public ModelField addKeyField(String fieldName) {		
		return addField(fieldName, true);
	}
	
	
	
	public ModelField getField(String fieldName) {
		return (ModelField)fields.get(fieldName);
	}
	
	public void addCalculatedField(ModelCalculatedField calculatedField) {
		// bound field to structure
		calculatedField.setId(getStructure().getNextId());
		calculatedField.setStructure(getStructure());
		calculatedField.setParent(this);
		
		// append field to entity
		calculatedFields.put(calculatedField.getUniqueName(), calculatedField);
		
		// append field to structure level facade
		getStructure().addCalculatedField(getUniqueName(), calculatedField);
	}	
	
	public void deleteCalculatedField(String fieldName) {
		ModelCalculatedField calculatedField;
		
		calculatedField = (ModelCalculatedField)calculatedFields.remove(fieldName);
		if(calculatedField != null) {
			getStructure().removeCalculatedField(calculatedField.getParent().getUniqueName(), calculatedField);
		}
		
	}
	
	public List<ModelCalculatedField>  getCalculatedFields() {
		List<ModelCalculatedField> list;
		
		list = new ArrayList<ModelCalculatedField>();
		String key = null;
		for(Iterator<String> it = calculatedFields.keySet().iterator(); it.hasNext(); ) {
			key = it.next();
			list.add(calculatedFields.get(key));			
		}
		
		return list;
	}	
	
	public List<ModelField> getAllFields() {
		List<ModelField> list;
		
		list = new ArrayList<ModelField>();
		String key = null;
		for(Iterator<String> it = fields.keySet().iterator(); it.hasNext(); ) {
			key = it.next();
			list.add(fields.get(key));			
		}
		
		return list;
	}	
	
	
	private List<ModelField> getFieldsByType(boolean isKey) {
		List<ModelField> list = new ArrayList<ModelField>();
		String key = null;
		for(Iterator<String> it = fields.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			ModelField field = (ModelField)fields.get(key);
			if(field.isKey() == isKey) {
				list.add(field);		
			}
		}
		return list;
	}
	
	
	public List<ModelField> getKeyFields() {
		return getFieldsByType(true);
	}
	
	
	public Iterator<ModelField> getKeyFieldIterator() {
		return getKeyFields().iterator();
	}
	
	
	public List<ModelField> getNormalFields() {
		return getFieldsByType(false);
	}
	
	
	public Iterator<ModelField> getNormalFieldIterator() {
		return getNormalFields().iterator();
	}	
	
	public IModelEntity addSubEntity(String subEntityName, String subEntityRole, String subEntityType) {
				
		String subEntityPath = "";
		if(getParent() != null) {
			subEntityPath = getName() +  "(" + getRole() + ")";
			if(!getPath().equalsIgnoreCase("")) {
				subEntityPath = getPath() + "." + subEntityPath;
			}
		}
		
		IModelEntity subEntity = new ModelEntity(subEntityName, subEntityPath, subEntityRole, subEntityType, getStructure());
		subEntity.setParent(this);
		
		addSubEntity(subEntity);
		return subEntity;
	}
	
	
	public void addSubEntity(IModelEntity entity) {
		subEntities.put(entity.getUniqueName(), entity);
		getStructure().addEntity(entity);
	}
	
	
	public IModelEntity getSubEntity(String entityUniqueName) {
		return (IModelEntity)subEntities.get(entityUniqueName);
	}
	
	
	public List<IModelEntity> getSubEntities() {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		String key = null;
		for(Iterator<String> it = subEntities.keySet().iterator(); it.hasNext(); ) {
			key = it.next();
			list.add(subEntities.get(key));			
		}
		return list;
	}
	
	public List<IModelEntity> getAllSubEntities() {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		String key = null;
		for(Iterator<String> it = subEntities.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			IModelEntity entity = (IModelEntity)subEntities.get(key);
			list.add(entity);
			list.addAll(entity.getAllSubEntities());
		}
		return list;
	}
	
	
	public List<IModelEntity> getAllSubEntities(String entityName) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		String key = null;
		for(Iterator<String> it = subEntities.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			IModelEntity entity = (IModelEntity)subEntities.get(key);
			if(entity.getName().equalsIgnoreCase(entityName)) {
				list.add(entity);
			}
			
			list.addAll(entity.getAllSubEntities(entityName));
		}
		return list;
	}
	
	public List<ModelField> getAllFieldOccurencesOnSubEntity(String entityName, String fieldName) {
		List<ModelField> list = new ArrayList<ModelField>();
		List<IModelEntity> entities = getAllSubEntities(entityName);
		for(int i = 0; i < entities.size(); i++) {
			IModelEntity entity = entities.get(i);
			List<ModelField> fields = entity.getAllFields();
			for(int j = 0; j < fields.size(); j++) {
				ModelField field = fields.get(j);
				if(field.getName().endsWith("." + fieldName)) {
					list.add(field);
				}
			}
		}
		
		return list;
	}

	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		String line = getName().toUpperCase() + "(id="+getId()
			+";path="+path
			+";parent:" + (getParent()==null?"NULL": getParent().getName())
			+";role="+ role;
		
		
		buffer.append(line + "\n");
		String key = null;
		for(Iterator<String> it = fields.keySet().iterator(); it.hasNext(); ) {
			key = it.next();
			Object o = fields.get(key);
			buffer.append(" - " + (o==null? "NULL": o.toString()) + "\n");
		}
		
		for(Iterator<String> it = subEntities.keySet().iterator(); it.hasNext();) {
			key = it.next();
			Object o = subEntities.get(key);
			buffer.append(" + " + (o==null? "NULL": o.toString()));
		}
		return buffer.toString();
	}

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRole() {
		return role!= null? role.toLowerCase(): null;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public IModelEntity getRoot() {
		if(root == null) {
			root = this;
			while(root.getParent() != null) {
				root = root.getParent();
			}
		}		
		
		return root;
	}

	public void setRoot(IModelEntity root) {
		this.root = root;
	}

	
}
