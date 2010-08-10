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

import it.eng.spagobi.commons.utilities.StringUtilities;

/**
 * @author Andrea Gioia
 */
public class DataMartEntity extends AbstractDataMartItem {
	
	private DataMartEntity root;	
	
	private String path;		
	private String role;	
	private String type;	
	
	private Map fields;	
	private Map calculatedFields;	
	private Map subEntities;
	
		
	public DataMartEntity(String name, String path, String role, String type,
			DataMartModelStructure structure) {
		
		setStructure( structure );
		
		setId ( structure.getNextId() );
		setName( name );		
		setPath( path == null? "" : path );
		setRole( role );
		setType( type );
		
		setParent(null);
		this.fields = new HashMap();
		this.calculatedFields = new HashMap();
		this.subEntities = new HashMap();
	}
	
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
		if ( !(o instanceof DataMartEntity) ) return false;
		DataMartEntity de = (DataMartEntity)o;
		return this.getUniqueName().equals( de.getUniqueName() );
	}

	
	public String getUniqueType() {
		String entityType = getType();
		if ( !StringUtilities.isEmpty( getRole() ) ) {
			entityType += "(" + getRole() + ")";
		}
		return entityType;
	}
	
	
	private void addField(DataMartField field) {
		fields.put(field.getUniqueName(), field);
		getStructure().addField(field);
	}
	
	
	private DataMartField addField(String fieldName, boolean isKey) {
		
		DataMartField field = new DataMartField(fieldName, this);
		field.setKey(isKey);
		addField(field);
		return field;
	}
	
	
	public DataMartField addNormalField(String fieldName) {
		return addField(fieldName, false);
	}
	
	
	public DataMartField addKeyField(String fieldName) {		
		return addField(fieldName, true);
	}
	
	
	
	public DataMartField getField(String fieldName) {
		return (DataMartField)fields.get(fieldName);
	}
	
	public void addCalculatedField(DataMartCalculatedField calculatedField) {
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
		DataMartCalculatedField calculatedField;
		
		calculatedField = (DataMartCalculatedField)calculatedFields.remove(fieldName);
		if(calculatedField != null) {
			getStructure().removeCalculatedFiield(calculatedField.getParent().getUniqueName(), calculatedField);
		}
		
	}
	
	public List getCalculatedFields() {
		List list = new ArrayList();
		String key = null;
		for(Iterator it = calculatedFields.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			list.add(calculatedFields.get(key));			
		}
		return list;
	}	
	
	public List getAllFields() {
		List list = new ArrayList();
		String key = null;
		for(Iterator it = fields.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			list.add(fields.get(key));			
		}
		return list;
	}	
	
	
	private List getFieldsByType(boolean isKey) {
		List list = new ArrayList();
		String key = null;
		for(Iterator it = fields.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			DataMartField field = (DataMartField)fields.get(key);
			if(field.isKey() == isKey) {
				list.add(field);		
			}
		}
		return list;
	}
	
	
	public List getKeyFields() {
		return getFieldsByType(true);
	}
	
	
	public Iterator getKeyFieldIterator() {
		return getKeyFields().iterator();
	}
	
	
	public List getNormalFields() {
		return getFieldsByType(false);
	}
	
	
	public Iterator getNormalFieldIterator() {
		return getNormalFields().iterator();
	}	
	
	public DataMartEntity addSubEntity(String subEntityName, String subEntityRole, String subEntityType) {
				
		String subEntityPath = "";
		if(getParent() != null) {
			subEntityPath = getName() +  "(" + getRole() + ")";
			if(!getPath().equalsIgnoreCase("")) {
				subEntityPath = getPath() + "." + subEntityPath;
			}
		}
		
		DataMartEntity subEntity = new DataMartEntity(subEntityName, subEntityPath, subEntityRole, subEntityType, getStructure());
		subEntity.setParent(this);
		
		addSubEntity(subEntity);
		return subEntity;
	}
	
	
	private void addSubEntity(DataMartEntity entity) {
		subEntities.put(entity.getUniqueName(), entity);
		getStructure().addEntity(entity);
	}
	
	
	public DataMartEntity getSubEntity(String entityUniqueName) {
		return (DataMartEntity)subEntities.get(entityUniqueName);
	}
	
	
	public List getSubEntities() {
		List list = new ArrayList();
		String key = null;
		for(Iterator it = subEntities.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			list.add(subEntities.get(key));			
		}
		return list;
	}
	
	public List getAllSubEntities() {
		List list = new ArrayList();
		String key = null;
		for(Iterator it = subEntities.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			DataMartEntity entity = (DataMartEntity)subEntities.get(key);
			list.add(entity);
			list.addAll(entity.getAllSubEntities());
		}
		return list;
	}
	
	
	public List getAllSubEntities(String entityName) {
		List list = new ArrayList();
		String key = null;
		for(Iterator it = subEntities.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			DataMartEntity entity = (DataMartEntity)subEntities.get(key);
			if(entity.getName().equalsIgnoreCase(entityName)) {
				list.add(entity);
			}
			
			list.addAll(entity.getAllSubEntities(entityName));
		}
		return list;
	}
	
	public List getAllFieldOccurencesOnSubEntity(String entityName, String fieldName) {
		List list = new ArrayList();
		List entities = getAllSubEntities(entityName);
		for(int i = 0; i < entities.size(); i++) {
			DataMartEntity entity = (DataMartEntity)entities.get(i);
			List fields = entity.getAllFields();
			for(int j = 0; j < fields.size(); j++) {
				DataMartField field = (DataMartField)fields.get(j);
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
		for(Iterator it = fields.keySet().iterator(); it.hasNext(); ) {
			key = (String)it.next();
			Object o = fields.get(key);
			buffer.append(" - " + (o==null? "NULL": o.toString()) + "\n");
		}
		
		for(Iterator it = subEntities.keySet().iterator(); it.hasNext();) {
			key = (String)it.next();
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

	public DataMartEntity getRoot() {
		if(root == null) {
			root = this;
			while(root.getParent() != null) {
				root = root.getParent();
			}
		}		
		
		return root;
	}

	public void setRoot(DataMartEntity root) {
		this.root = root;
	}

	
}
