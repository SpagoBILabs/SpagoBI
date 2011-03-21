package it.eng.qbe.model.structure;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.filter.QbeTreeFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewModelStructure extends AbstractModelObject implements IModelStructure {
	

	private QbeTreeFilter qbeTreeFilter;
	private IDataSource dataSource;
	private IModelStructure wrappedModelStructure;
	
	public ViewModelStructure(IModelStructure wrappedModelStructure, IDataSource dataSource, QbeTreeFilter qbeTreeFilter){
		this.qbeTreeFilter=qbeTreeFilter;
		this.dataSource=dataSource;
		if(wrappedModelStructure instanceof ViewModelStructure){
			this.wrappedModelStructure = ((ViewModelStructure)wrappedModelStructure).getWrappedModelStructure();
		}else{
			this.wrappedModelStructure = wrappedModelStructure;
		}
	}
	
	public List<IModelEntity> getRootEntities(String modelName){
		List<IModelEntity> iModelEntities = qbeTreeFilter.filterEntities(dataSource,wrappedModelStructure.getRootEntities(modelName));
		List<IModelEntity> viewModelEntities = new ArrayList<IModelEntity>();
		for(int i=0; i<iModelEntities.size(); i++){
			ViewModelEntity vme;
			if(iModelEntities.get(i) instanceof ViewModelEntity){
				vme = (ViewModelEntity)iModelEntities.get(i);
				vme.setDataSource(dataSource);
				vme.setQbeTreeFilter(qbeTreeFilter);
			}else{
				vme = new ViewModelEntity(iModelEntities.get(i), dataSource, qbeTreeFilter);
			}
			viewModelEntities.add(vme);
		}
		return viewModelEntities;
	}

	public QbeTreeFilter getQbeTreeFilter() {
		return qbeTreeFilter;
	}

	public void setQbeTreeFilter(QbeTreeFilter qbeTreeFilter) {
		this.qbeTreeFilter = qbeTreeFilter;
	}

	public long getId() {
		return wrappedModelStructure.getId();
	}

	public String getName() {
		return wrappedModelStructure.getName();
	}

	public void setName(String name) {
		wrappedModelStructure.setName(name);		
	}

	public Map<String, Object> getProperties() {
		return wrappedModelStructure.getProperties();
	}

	public void setProperties(Map<String, Object> properties) {
		wrappedModelStructure.setProperties(properties);
		
	}

	public long getNextId() {
		return wrappedModelStructure.getNextId();
	}

	public Set<String> getModelNames() {
		return wrappedModelStructure.getModelNames();
	}

	public IModelEntity addRootEntity(String modelName, String name,
			String path, String role, String type) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn =  wrappedModelStructure.addRootEntity(modelName, name, path, role, type);
		if(entityn==null){
			return null;
		}
		list.add(entityn);
		filteredList = qbeTreeFilter.filterEntities(dataSource,list);
		if(filteredList==null || filteredList.size()==0){
			return null;
		}
		return filteredList.get(0);
	}

	public IModelEntity getRootEntity(String modelName, String entityName) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn = wrappedModelStructure.getRootEntity(modelName, entityName);
		if(entityn==null){
			return null;
		}
		list.add(entityn);
		filteredList = qbeTreeFilter.filterEntities(dataSource,list);
		if(filteredList==null || filteredList.size()==0){
			return null;
		}
		return filteredList.get(0);
	}

	public IModelEntity getRootEntity(IModelEntity entity) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn = wrappedModelStructure.getRootEntity(entity);
		if(entity==null){
			return null;
		}
		list.add(entityn);
		filteredList = qbeTreeFilter.filterEntities(dataSource,list);
		if(filteredList==null || filteredList.size()==0){
			return null;
		}
		return filteredList.get(0);
	}

	public IModelEntity getRootEntity(IModelEntity entity, String modelName) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn = wrappedModelStructure.getRootEntity(entity, modelName);
		if(entity==null){
			return null;
		}
		list.add(entityn);
		filteredList = qbeTreeFilter.filterEntities(dataSource,list);
		if(filteredList==null || filteredList.size()==0){
			return null;
		}
		return filteredList.get(0);
	}

	public Iterator<IModelEntity> getRootEntityIterator(String modelName) {
		return getRootEntities(modelName).iterator();
	}

	public void addEntity(IModelEntity entity) {
		wrappedModelStructure.addEntity(entity);
		
	}

	public IModelEntity getEntity(String entityUniqueName) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entity = wrappedModelStructure.getEntity(entityUniqueName);
		if(entity==null){
			return null;
		}
		list.add(entity);
		filteredList = qbeTreeFilter.filterEntities(dataSource,list);
		if(filteredList==null || filteredList.size()==0){
			return null;
		}
		return filteredList.get(0);
	}

	public void addField(ModelField field) {
		wrappedModelStructure.addField(field);
		
	}

	public ModelField getField(String fieldUniqueName) {
		List<ModelField> list = new ArrayList<ModelField>();
		List<ModelField> filteredList;
		ModelField field = wrappedModelStructure.getField(fieldUniqueName);
		if(field==null){
			return null;
		}
		list.add(field);
		filteredList = qbeTreeFilter.filterEntities(dataSource,list);
		if(filteredList==null || filteredList.size()==0){
			return null;
		}
		return filteredList.get(0);
	}

	public Map<String, List<ModelCalculatedField>> getCalculatedFields() {
		Map<String, List<ModelCalculatedField>> calculatedFields =  wrappedModelStructure.getCalculatedFields();
		Map<String, List<ModelCalculatedField>> filteredCalculatedFields =  new HashMap<String, List<ModelCalculatedField>>();
		Iterator<String> iter = calculatedFields.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			filteredCalculatedFields.put(key, qbeTreeFilter.filterFields(dataSource,calculatedFields.get(key)));
		}
		return filteredCalculatedFields;
	}

	public List<ModelCalculatedField> getCalculatedFieldsByEntity(
			String entityName) {
		return qbeTreeFilter.filterFields(dataSource,wrappedModelStructure.getCalculatedFieldsByEntity(entityName));
	}

	public void setCalculatedFields(
			Map<String, List<ModelCalculatedField>> calculatedFields) {
		wrappedModelStructure.setCalculatedFields(calculatedFields);		
	}

	public void addCalculatedField(String entityName,
			ModelCalculatedField calculatedFiled) {
		wrappedModelStructure.addCalculatedField(entityName, calculatedFiled);
		
	}

	public void removeCalculatedField(String entityName,
			ModelCalculatedField calculatedFiled) {
		wrappedModelStructure.removeCalculatedField(entityName, calculatedFiled);
		
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IModelStructure getWrappedModelStructure() {
		return wrappedModelStructure;
	}

	public void setWrappedModelStructure(IModelStructure wrappedModelStructure) {
		this.wrappedModelStructure = wrappedModelStructure;
	}
	

}
