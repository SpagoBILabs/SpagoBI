package it.eng.qbe.model.structure;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.filter.QbeTreeFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ViewModelEntity implements IModelEntity{

	private QbeTreeFilter qbeTreeFilter;
	private IDataSource dataSource;
	private IModelEntity wrappedModelEntity;
	
	public ViewModelEntity(IModelEntity wrappedModelEntity, IDataSource dataSource, QbeTreeFilter qbeTreeFilter ) {
		this.qbeTreeFilter=qbeTreeFilter;
		this.dataSource=dataSource;
		if(wrappedModelEntity instanceof ViewModelEntity){
			this.wrappedModelEntity = ((ViewModelEntity)wrappedModelEntity).getWrappedModelEntity();
		}else{
			this.wrappedModelEntity = wrappedModelEntity;
		}
		
	}
	
	public List<ModelField> getKeyFields() {
		return qbeTreeFilter.filterFields(dataSource, wrappedModelEntity.getKeyFields());
	}
	
	public List<ModelField> getNormalFields() {
		return qbeTreeFilter.filterFields(dataSource, wrappedModelEntity.getNormalFields());
	}
	
	public List<ModelCalculatedField>  getCalculatedFields() {
		return qbeTreeFilter.filterFields(dataSource, wrappedModelEntity.getCalculatedFields());
	}
	
	public List<IModelEntity> getSubEntities() {
		List<IModelEntity> iModelEntities = qbeTreeFilter.filterEntities(getDataSource(), wrappedModelEntity.getSubEntities());
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

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public long getId() {
		return wrappedModelEntity.getId();
	}

	public String getName() {
		return wrappedModelEntity.getName();
	}

	public void setName(String name) {
		wrappedModelEntity.setName(name);
		
	}

	public Map<String, Object> getProperties() {
		return wrappedModelEntity.getProperties();
	}

	public void setProperties(Map<String, Object> properties) {
		wrappedModelEntity.setProperties(properties);
	}

	public String getUniqueName() {
		return wrappedModelEntity.getUniqueName();
	}

	public String getUniqueType() {
		return wrappedModelEntity.getUniqueType();
	}

	public ModelField addNormalField(String fieldName) {
		return wrappedModelEntity.addNormalField(fieldName);
	}

	public ModelField addKeyField(String fieldName) {
		return wrappedModelEntity.addKeyField(fieldName);
	}

	public ModelField getField(String fieldName) {
		List<ModelField> list = new ArrayList<ModelField>();
		List<ModelField> filteredList;
		ModelField field = wrappedModelEntity.getField(fieldName);
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

	public void addCalculatedField(ModelCalculatedField calculatedField) {
		wrappedModelEntity.addCalculatedField(calculatedField);
		
	}

	public void deleteCalculatedField(String fieldName) {
		wrappedModelEntity.deleteCalculatedField(fieldName);
	}

	public List<ModelField> getAllFields() {
		return qbeTreeFilter.filterFields(dataSource, wrappedModelEntity.getAllFields());
	}

	public Iterator<ModelField> getKeyFieldIterator() {
		return getKeyFields().iterator();
	}

	public Iterator<ModelField> getNormalFieldIterator() {
		return getNormalFields().iterator();
	}

	public IModelEntity addSubEntity(String subEntityName,
			String subEntityRole, String subEntityType) {
		return wrappedModelEntity.addSubEntity(subEntityName, subEntityRole, subEntityType);
	}

	public void addSubEntity(IModelEntity entity) {
		wrappedModelEntity.addSubEntity(entity);
		
	}

	public IModelEntity getSubEntity(String entityUniqueName) {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn =  wrappedModelEntity.getSubEntity(entityUniqueName);
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

	public List<IModelEntity> getAllSubEntities() {
		return qbeTreeFilter.filterEntities(getDataSource(), wrappedModelEntity.getAllSubEntities());
	}

	public List<IModelEntity> getAllSubEntities(String entityName) {
		return qbeTreeFilter.filterEntities(getDataSource(),wrappedModelEntity.getAllSubEntities(entityName));
	}

	public List<ModelField> getAllFieldOccurencesOnSubEntity(String entityName,
			String fieldName) {
		return qbeTreeFilter.filterFields(dataSource, wrappedModelEntity.getAllFieldOccurencesOnSubEntity(entityName, fieldName));
	}

	public String getPath() {
		return wrappedModelEntity.getPath();
	}

	public void setPath(String path) {
		wrappedModelEntity.setPath(path);
		
	}

	public String getRole() {
		return wrappedModelEntity.getRole();
	}

	public void setRole(String role) {
		wrappedModelEntity.setRole(role);
		
	}

	public String getType() {
		return wrappedModelEntity.getType();
	}

	public void setType(String type) {
		wrappedModelEntity.setType(type);
	}

	public IModelEntity getRoot() {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn =  wrappedModelEntity.getRoot();
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

	public IModelStructure getStructure() {
		return wrappedModelEntity.getStructure();
	}

	public IModelEntity getParent() {
		List<IModelEntity> list = new ArrayList<IModelEntity>();
		List<IModelEntity> filteredList;
		IModelEntity entityn =  wrappedModelEntity.getParent();
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

	public void setParent(IModelEntity parent) {
		wrappedModelEntity.setParent(parent);
	}

	public IModelEntity getWrappedModelEntity() {
		return wrappedModelEntity;
	}

	public void setWrappedModelEntity(IModelEntity wrappedModelEntity) {
		this.wrappedModelEntity = wrappedModelEntity;
	}
	
	public Object getProperty(String name) {
		return wrappedModelEntity.getProperty(name);
	}
	
	public String getPropertyAsString(String name) {
		return wrappedModelEntity.getPropertyAsString(name);
	}
	
	public boolean getPropertyAsBoolean(String name) {
		return wrappedModelEntity.getPropertyAsBoolean(name);
	}
	
	public int getPropertyAsInt(String name) {	
		return wrappedModelEntity.getPropertyAsInt(name);
	}
	
}
