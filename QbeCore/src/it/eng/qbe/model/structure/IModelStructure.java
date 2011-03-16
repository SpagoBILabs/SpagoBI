package it.eng.qbe.model.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IModelStructure extends IModelObject{

		public long getNextId() ;

		public Set<String> getModelNames() ;

		public IModelEntity addRootEntity(String modelName, String name, String path, String role, String type) ;


		public IModelEntity getRootEntity(String modelName, String entityName) ;

		public IModelEntity getRootEntity(IModelEntity entity) ;

		public IModelEntity getRootEntity(IModelEntity entity, String modelName) ;

		public Iterator<IModelEntity> getRootEntityIterator(String modelName) ;

		public List<IModelEntity> getRootEntities(String modelName) ;
		public void addEntity(IModelEntity entity) ;

		public IModelEntity getEntity(String entityUniqueName) ;

		public void addField(ModelField field) ;

		public ModelField getField(String fieldUniqueName) ;

		public Map<String, List<ModelCalculatedField>> getCalculatedFields() ;

		public List<ModelCalculatedField> getCalculatedFieldsByEntity(String entityName) ;

		public void setCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) ;

		public void addCalculatedField(String entityName, ModelCalculatedField calculatedFiled) ;

		public void removeCalculatedField(String entityName, ModelCalculatedField calculatedFiled) ;
}
