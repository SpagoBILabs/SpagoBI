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
import it.eng.qbe.model.structure.IModelViewEntityDescriptor.IModelViewRelationshipDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import junit.framework.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ModelViewEntity extends ModelEntity {
	
	List<IModelEntity> entities;
	List<Join> joins;
	List<ViewRelationship> viewRelationships;
	private static transient Logger logger = Logger.getLogger(ModelViewEntity.class);
	
	IModelViewEntityDescriptor viewDescriptor;
	String modelName;
	
	// =========================================================================
	// INNER CLASSES
	// =========================================================================
	
	public class Join {
		IModelEntity sourceEntity;
		List <IModelField> sourceFields;
		IModelEntity destinationEntity;
		List <IModelField> destinationFields;
		
		public String getFieldUniqueName(IModelEntity parentEntity, String fieldName) {
			if (parentEntity == null){
				logger.debug("parentEntity is null, field name is "+fieldName);
			}
			if(parentEntity.getParent() == null) {
				return parentEntity.getType() + ":" + fieldName;
			}
			return parentEntity.getUniqueName() + ":" + fieldName;
		}
		
		public Join(IModelViewJoinDescriptor joinDescriptor, String modelName, IModelStructure structure) {
			
			sourceEntity = structure.getRootEntity(modelName, joinDescriptor.getSourceEntityUniqueName());
			destinationEntity = structure.getRootEntity(modelName, joinDescriptor.getDestinationEntityUniqueName());
			
			sourceFields = new ArrayList<IModelField>();
			for(String fieldName : joinDescriptor.getSourceColumns()) {
				String fieldUniqueName = getFieldUniqueName(sourceEntity, fieldName);
				IModelField f = sourceEntity.getField(fieldUniqueName);
				if(f == null) {
					List<IModelField> fields = sourceEntity.getAllFields();
					String str = "";
					for(IModelField field : fields) {
						str = field.getUniqueName() + ";  ";
					}
					Assert.assertNotNull("Impossible to find source field [" + fieldUniqueName + "]. Valid filed name are [" + str + "]", f);
				}
				
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
	
	public class ViewRelationship {
		IModelEntity sourceEntity;
		List <IModelField> sourceFields;
		IModelEntity destinationEntity;
		List <IModelField> destinationFields;
		boolean isOutbound;
		
		public String getFieldUniqueName(IModelEntity parentEntity, String fieldName) {
			if (parentEntity == null){
				logger.debug("parentEntity is null, field name is "+fieldName);
				return null;
			} else {
				if(parentEntity.getParent() == null) {
					logger.debug("FieldUniqueName is "+parentEntity.getType() + ":" + fieldName);
					return parentEntity.getType() + ":" + fieldName;
				}
			}
			return parentEntity.getUniqueName() + ":" + fieldName;
		}
		
		public ViewRelationship(IModelViewRelationshipDescriptor relationshipDescriptor, String modelName, IModelStructure structure) {
			isOutbound = relationshipDescriptor.isOutbound();
			
			if (!isOutbound){
				sourceEntity = structure.getRootEntity(modelName, relationshipDescriptor.getSourceEntityUniqueName());
				(ModelViewEntity.this).setParent(sourceEntity);
				
				if (relationshipDescriptor.isSourceEntityView()){
					//empty
					sourceFields = new ArrayList<IModelField>();
				} else {
					sourceFields = new ArrayList<IModelField>();
					for(String fieldName : relationshipDescriptor.getSourceColumns()) {
						String fieldUniqueName = getFieldUniqueName(sourceEntity, fieldName);
						IModelField f = sourceEntity.getField(fieldUniqueName);
						if(f == null) {
							List<IModelField> fields = sourceEntity.getAllFields();
							String str = "";
							for(IModelField field : fields) {
								str = field.getUniqueName() + ";  ";
							}
							Assert.assertNotNull("Impossible to find source field [" + fieldUniqueName + "]. Valid filed name are [" + str + "]", f);
						}
						
						sourceFields.add(f);
					}
				}
				destinationFields = new ArrayList<IModelField>();
				if (!relationshipDescriptor.isSourceEntityView()){
					List<String> detinationColumns = relationshipDescriptor.getDestinationColumns();
					if(detinationColumns!=null ){
						for(String fieldName : detinationColumns) {
							
							for(int x=0; x<entities.size(); x++ ){
								List<IModelField> fields = entities.get(x).getAllFields();
								if(fields!=null){
									for(int y=0; y<fields.size(); y++ ){
										if(fields.get(y).getName().equals("compId."+fieldName)){
											destinationFields.add(fields.get(y));
											destinationEntity = entities.get(x);
											break;			
										}
									}
								}
								if(destinationEntity!=null){
									break;
								}
							}
						}
					}
				}
			} else {
				destinationEntity = structure.getRootEntity(modelName, relationshipDescriptor.getDestinationEntityUniqueName());
				destinationEntity.setParent((ModelViewEntity.this));
								
				if (relationshipDescriptor.isDestinationEntityView()){
					//empty
					destinationFields = new ArrayList<IModelField>();
				} else {
					destinationFields = new ArrayList<IModelField>();
					for(String fieldName : relationshipDescriptor.getDestinationColumns()) {
						String fieldUniqueName = getFieldUniqueName(destinationEntity, fieldName);
						IModelField f = destinationEntity.getField(fieldUniqueName);
						if(f == null) {
							List<IModelField> fields = destinationEntity.getAllFields();
							String str = "";
							for(IModelField field : fields) {
								str = field.getUniqueName() + ";  ";
							}
							Assert.assertNotNull("Impossible to find destination field [" + fieldUniqueName + "]. Valid filed name are [" + str + "]", f);
						}
						destinationFields.add(f);
					}
				}
				sourceFields = new ArrayList<IModelField>();
				if (relationshipDescriptor.isDestinationEntityView()){
					List<String> sourceColumns = relationshipDescriptor.getSourceColumns();
					if(sourceColumns!=null ){
						for(String fieldName : sourceColumns) {
							
							for(int x=0; x<entities.size(); x++ ){
								List<IModelField> fields = entities.get(x).getAllFields();
								if(fields!=null){
									for(int y=0; y<fields.size(); y++ ){
										if(fields.get(y).getName().equals("compId."+fieldName)){
											sourceFields.add(fields.get(y));
											sourceEntity = entities.get(x);
											break;			
										}
									}
								}
								if(sourceEntity!=null){
									break;
								}
							}
						}
					}
				}
			}
		}
		
		private void addRelationSide(){
			
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
	
		public boolean isOutbound() {
			return isOutbound;
		}
	}
	
	
	// =========================================================================
	// COSTRUCTORS 
	// =========================================================================

	public ModelViewEntity(IModelViewEntityDescriptor view,  String modelName, IModelStructure structure) throws Exception{
		super(null, null, null, null, structure);
	
		setName( view.getName() );
		setType( view.getType() );
		
		viewDescriptor = view;
		this.modelName = modelName;
		
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
		
		
		viewRelationships = new ArrayList<ViewRelationship>();
		List<IModelViewRelationshipDescriptor> relationshipDescriptors = view.getRelationshipDescriptors();
		for(IModelViewRelationshipDescriptor relationshipDescriptor : relationshipDescriptors) {
			viewRelationships.add( new ViewRelationship(relationshipDescriptor, modelName, structure) );
		}
		
		//only outbound relationship from view are added as subentities
		for(ViewRelationship relationship : viewRelationships){
			if (relationship.isOutbound())
				subEntities.put(relationship.getDestinationEntity().getUniqueName(),relationship.getDestinationEntity());
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
//	
//	public IModelEntity getEntityByField(IModelField field) {
//		for(int x=0; x<entities.size(); x++ ){
//			List<IModelField> fields = entities.get(x).getAllFields();
//			if(fields!=null){
//				for(int y=0; y<fields.size(); y++ ){
//					if(fields.get(y).equals(field)){
//						return entities.get(x);
//					}
//				}
//			}
//		}
//		throw new NoSuchElementException("No field "+field.getName()+ "found in the entity "+this.getName());
//	}
	
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
	
	public List<ViewRelationship> getRelationships() {
		return viewRelationships;
	}
	
	public List<ViewRelationship> getRelationshipsToViews(){
		List<ViewRelationship> relationshipsToViews = new ArrayList<ViewRelationship>();
		List<IModelViewRelationshipDescriptor> relationshipDescriptors = viewDescriptor.getRelationshipToViewsDescriptors();
		for(IModelViewRelationshipDescriptor relationshipDescriptor : relationshipDescriptors) {
			relationshipsToViews.add( new ViewRelationship(relationshipDescriptor, modelName, structure) );
		}
		return relationshipsToViews;
	}
	
	//Only outbound relationship from view to another view are added as subentities
	public void addOutboundRelationshipsToViewEntities() {
		List<ViewRelationship> relationshipsToViews  = getRelationshipsToViews();
		if (!relationshipsToViews.isEmpty()){			
			for(ViewRelationship relationship : relationshipsToViews){
				if (relationship.isOutbound()){
					subEntities.put(relationship.getDestinationEntity().getUniqueName(),relationship.getDestinationEntity());
					logger.debug("["+relationship.getDestinationEntity()+"] was added as subentity of " +
							"["+relationship.getDestinationEntity().getUniqueName()+"]");
				}
			} 
		}
	}
	
}
