package it.eng.qbe.model.structure;

import java.util.Iterator;
import java.util.List;

public interface IModelEntity extends IModelNode{
		public String getUniqueName() ;
		public boolean equals(Object o);
		public String getUniqueType();
		public ModelField addNormalField(String fieldName);
		public ModelField addKeyField(String fieldName);
		public ModelField getField(String fieldName);
		public void addCalculatedField(ModelCalculatedField calculatedField);
		public void deleteCalculatedField(String fieldName);
		public List<ModelCalculatedField>  getCalculatedFields() ;
		public List<ModelField> getAllFields();
		public List<ModelField> getKeyFields() ;
		public Iterator<ModelField> getKeyFieldIterator();
		public List<ModelField> getNormalFields();
		public Iterator<ModelField> getNormalFieldIterator();
		public IModelEntity addSubEntity(String subEntityName, String subEntityRole, String subEntityType) ;
		public void addSubEntity(IModelEntity entity) ;
		public IModelEntity getSubEntity(String entityUniqueName);
		public List<IModelEntity> getSubEntities() ;
		public List<IModelEntity> getAllSubEntities() ;
		public List<IModelEntity> getAllSubEntities(String entityName);
		public List<ModelField> getAllFieldOccurencesOnSubEntity(String entityName, String fieldName);
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
