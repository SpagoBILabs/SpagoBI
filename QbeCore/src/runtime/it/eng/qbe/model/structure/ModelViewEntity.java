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

import it.eng.qbe.model.structure.IModelViewEntityDescriptor.IModelViewJoinDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ModelViewEntity extends ModelEntity {
	
	List<IModelEntity> entities;
	List<Join> joins;
	
	public class Join {
		IModelEntity sourceEntity;
		List <IModelField> sourceFields;
		IModelEntity destinationEntity;
		List <IModelField> destinationFields;
		
		public String getFieldUniqueName(IModelEntity parentEntity, String fieldName) {
			if(parentEntity.getParent() == null) {
				return parentEntity.getType() + ":" + fieldName;
			}
			return parentEntity.getUniqueName() + ":" + fieldName;
		}
		
		public Join(IModelViewJoinDescriptor joinDescriptor, String modelName, IModelStructure structure) {
			
			sourceEntity = structure.getRootEntity(modelName, joinDescriptor.getSourceEntityUniqueName());
			destinationEntity = structure.getRootEntity(modelName, joinDescriptor.getDestinationEntityUniqueName());
			
			sourceFields = new ArrayList<IModelField>();
			for(String fieldName : joinDescriptor.getDestinationColumns()) {
				String fieldUniqueName = getFieldUniqueName(sourceEntity, fieldName);
				IModelField f = sourceEntity.getField(fieldUniqueName);
				Assert.assertNotNull("Impossible to find source field [" + fieldUniqueName + "]", f);
				sourceFields.add(f);
			}
			
			destinationFields = new ArrayList<IModelField>();
			for(String fieldName : joinDescriptor.getDestinationColumns()) {
				String fieldUniqueName = getFieldUniqueName(destinationEntity, fieldName);
				IModelField f = destinationEntity.getField(fieldUniqueName);
				Assert.assertNotNull("Impossible to find destination field [" + fieldUniqueName + "]", f);
				destinationFields.add(f);
			}
			
		}
		
		public IModelEntity getSourceEntity() {
			return sourceEntity;
		}
		
		public IModelEntity getDestinationEntity() {
			return destinationEntity;
		}
		
		public List<IModelField> getSourceFileds() {
			return sourceFields;
		}
		
		public List<IModelField> getDestinationFileds() {
			return destinationFields;
		}
	}
	
	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================

	public ModelViewEntity(IModelViewEntityDescriptor view,  String modelName, IModelStructure structure) throws Exception{
		super(null, null, null, null, structure);
	
		setName( view.getName() );
		setType( view.getType() );
		
		entities = new ArrayList<IModelEntity>();
		subEntities = new HashMap<String,IModelEntity>();
		
		Set<String> innerEntityUniqueNames = view.getInnerEntityUniqueNames();
		for(String innerEntityUniqueName : innerEntityUniqueNames) {
			IModelEntity e = structure.getRootEntity(modelName, innerEntityUniqueName);
			entities.add(e);
			List<IModelEntity> innerEntitySubEntities = e.getSubEntities();
			for(IModelEntity innerEntitySubEntity : innerEntitySubEntities) {
				subEntities.put(innerEntitySubEntity.getUniqueName(), innerEntitySubEntity);
			}
			
		}
		
		joins = new ArrayList<Join>();
		List<IModelViewJoinDescriptor> joinDescriptors = view.getJoinDescriptors();
		for(IModelViewJoinDescriptor joinDescriptor : joinDescriptors) {
			joins.add( new Join(joinDescriptor, modelName, structure) );
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

	public List<Join> getJoins() {
		 return joins;		
	}
	
	public List<IModelEntity> getInnerEntities() {
		 return entities;		
	}
}
