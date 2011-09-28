package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.SelectableFieldsBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
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

	//	void loadData();
	//	void loadData(int offset, int fetchSize, int maxResults);


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






}
