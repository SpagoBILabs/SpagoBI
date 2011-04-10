/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.model.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ModelViewEntity extends ModelEntity {
	
	List<IModelEntity> entities;
	
	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================

	public ModelViewEntity(IModelViewEntityDescriptor view,  String modelName, IModelStructure structure) throws Exception{
		super(null, null, null, null, structure);
	
		setName( view.getName() );
		setType( view.getType() );
		
		entities = new ArrayList<IModelEntity>();
		Set<String> innerEntityUniqueNames = view.getInnerEntityUniqueNames();
		for(String innerEntityUniqueName : innerEntityUniqueNames) {
			IModelEntity e = structure.getRootEntity(modelName, innerEntityUniqueName);
			entities.add(e);
		}
	}
	
	// =========================================================================
	// ACCESORS 
	// =========================================================================
	
	public IModelField getField(String fieldName) {
		IModelField field = null;
		for(IModelEntity entity : entities) {
			field = entity.getField(fieldName);
			if(field != null) break;
		}
		return field;
	}
	
	public List<IModelField> getAllFields() {
		List<IModelField> fields = new ArrayList<IModelField>();
		for(IModelEntity entity : entities) {
			fields.addAll( entity.getAllFields() );
		}
		return fields;
	}	

	
	public List<IModelField> getKeyFields() {
		List<IModelField> fields = new ArrayList<IModelField>();
		for(IModelEntity entity : entities) {
			fields.addAll( entity.getKeyFields() );
		}
		return fields;
	}
	
	public List<IModelField> getFieldsByType(boolean isKey) {
		List<IModelField> fields = new ArrayList<IModelField>();
		for(IModelEntity entity : entities) {
			fields.addAll( entity.getFieldsByType(isKey) );
		}
		return fields;
	}
	
	public List<IModelField> getNormalFields() {
		List<IModelField> fields = new ArrayList<IModelField>();
		for(IModelEntity entity : entities) {
			fields.addAll( entity.getFieldsByType(false) );
		}
		return fields;
	}
	
	
	public List<IModelField> getAllFieldOccurencesOnSubEntity(String entityName, String fieldName) {
		List<IModelField> fields = new ArrayList<IModelField>();
		for(IModelEntity entity : entities) {
			fields.addAll( entity.getAllFieldOccurencesOnSubEntity(entityName, fieldName) );
		}
		return fields;
	}
}
