package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.SelectableFieldsBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.tools.dataset.functionalities.temporarytable.DatasetTempTable;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class AbstractCustomDataSet implements IDataSet{

	private Map paramsMap;
	private Map userProfileAttributes;
	private Map<String, String> properties;
	private Map behaviours;
	private IMetaData metadata;	

	private static transient Logger logger = Logger.getLogger(AbstractCustomDataSet.class);


	public AbstractCustomDataSet() {
		super();
		behaviours = new HashMap();
		addBehaviour( new FilteringBehaviour(this) );
		addBehaviour( new SelectableFieldsBehaviour(this) );

	}

	public Map getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map paramsMap) {
		this.paramsMap = paramsMap;
	}  


	public Map<String, String> getProperties(){
		return properties;
	}

	public void setProperties(Map properties){
		this.properties = properties;
	}

	public Map getUserProfileAttributes(){
		return userProfileAttributes;
	}
	public void setUserProfileAttributes(Map attributes){
		this.userProfileAttributes = attributes;
	}

	public boolean hasBehaviour(String behaviourId) {
		return behaviours.containsKey(behaviourId);
	}

	public Object getBehaviour(String behaviourId) {
		return behaviours.get(behaviourId);
	}

	public void addBehaviour(IDataSetBehaviour behaviour) {
		behaviours.put(behaviour.getId(), behaviour);
	}



	public IMetaData getMetadata(){
		return this.metadata;
	}

	public void setMetadata(IMetaData metadata){
		this.metadata = metadata;
	}



	public IDataSetTableDescriptor createTemporaryTable(String tableName
			, MetaData metadata
			, Connection connection){
		return DatasetTempTable.createTemporaryTable(connection, metadata, tableName);
	}



	// *********** Abstract methods **************

	
	 // no implement
	public abstract IDataStore test();
	public abstract String getSignature();
	public abstract IDataStore getDomainValues(String attributeName, Integer start, Integer limit, IDataStoreFilter filter);
	public abstract Map<String, List<String>> getDomainDescriptions(Map<String, List<String>> codes); 
	public abstract IDataSetTableDescriptor persist(String tableName, Connection connection);

	
	
	// *********** Fake implementation methods **************
	
	public void addBinding(String bindingName, Object bindingValue) {
		// TODO Auto-generated method stub
		
	}

	public String getCategoryCd() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getCategoryId() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataStore getDataStore() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataStoreTransformer getDataStoreTransformer() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDsMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDsType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGroovyFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJsFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPivotColumnName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPivotColumnValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPivotRowName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTransformerCd() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getTransformerId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasDataStoreTransformer() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNumRows() {
		// TODO Auto-generated method stub
		return false;
	}

	public void loadData() {
		// TODO Auto-generated method stub
		
	}

	public void loadData(int offset, int fetchSize, int maxResults) {
		// TODO Auto-generated method stub
		
	}

	public void removeDataStoreTransformer() {
		// TODO Auto-generated method stub
		
	}

	public void setAbortOnOverflow(boolean abortOnOverflow) {
		// TODO Auto-generated method stub
		
	}

	public void setCategoryCd(String categoryCd) {
		// TODO Auto-generated method stub
		
	}

	public void setCategoryId(Integer categoryId) {
		// TODO Auto-generated method stub
		
	}

	public void setDataStoreTransformer(IDataStoreTransformer transformer) {
		// TODO Auto-generated method stub
		
	}

	public void setDescription(String description) {
		// TODO Auto-generated method stub
		
	}

	public void setDsMetadata(String dsMetadata) {
		// TODO Auto-generated method stub
		
	}

	public void setDsType(String dsType) {
		// TODO Auto-generated method stub
		
	}

	public void setGroovyFileName(String groovyFileName) {
		// TODO Auto-generated method stub
		
	}

	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	public void setJsFileName(String jsFileName) {
		// TODO Auto-generated method stub
		
	}

	public void setLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	public void setNumRows(boolean numRows) {
		// TODO Auto-generated method stub
		
	}

	public void setParameters(String parameters) {
		// TODO Auto-generated method stub
		
	}

	public void setPivotColumnName(String pivotColumnName) {
		// TODO Auto-generated method stub
		
	}

	public void setPivotColumnValue(String pivotColumnValue) {
		// TODO Auto-generated method stub
		
	}

	public void setPivotRowName(String pivotRowName) {
		// TODO Auto-generated method stub
		
	}

	public void setQuery(Object query) {
		// TODO Auto-generated method stub
		
	}

	public void setResourcePath(String resPath) {
		// TODO Auto-generated method stub
		
	}

	public void setTransformerCd(String transfomerCd) {
		// TODO Auto-generated method stub
		
	}

	public void setTransformerId(Integer transformerId) {
		// TODO Auto-generated method stub
		
	}

	public SpagoBiDataSet toSpagoBiDataSet() {
		// TODO Auto-generated method stub
		return null;
	}






}
