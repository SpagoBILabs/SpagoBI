package it.eng.qbe.model.structure;

import java.util.Iterator;
import java.util.List;

public interface IModelEntity extends IModelNode{
		public String getUniqueName() ;
		public boolean equals(Object o);
		public String getUniqueType();
		public IModelField addNormalField(String fieldName);
		public IModelField addKeyField(String fieldName);
		public IModelField getField(String fieldName);
		public void addCalculatedField(ModelCalculatedField calculatedField);
		public void deleteCalculatedField(String fieldName);
		public List<ModelCalculatedField>  getCalculatedFields() ;
		public List<IModelField> getAllFields();
		public List<IModelField> getKeyFields() ;
		public Iterator<IModelField> getKeyFieldIterator();
		public List<IModelField> getNormalFields();
		public Iterator<IModelField> getNormalFieldIterator();
		public IModelEntity addSubEntity(String subEntityName, String subEntityRole, String subEntityType) ;
		public void addSubEntity(IModelEntity entity) ;
		public IModelEntity getSubEntity(String entityUniqueName);
		public List<IModelEntity> getSubEntities() ;
		public List<IModelEntity> getAllSubEntities() ;
		public List<IModelEntity> getAllSubEntities(String entityName);
		public List<IModelField> getAllFieldOccurencesOnSubEntity(String entityName, String fieldName);
		public String toString();
		public String getPath();
		public void setPath(String path);
		public String getRole();
		public void setRole(String role);
		public String getType();
		public void setType(String type);
		public IModelEntity getRoot();
		public IModelStructure getStructure();
		public IModelEntity getParent();
		public void setParent(IModelEntity parent);
		
}
