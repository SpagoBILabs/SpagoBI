/**
 * 
 */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.QuerableBehaviour;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia
 */
public class ConfigurableDataSet extends  AbstractDataSet {

	IDataReader dataReader;
	IDataProxy dataProxy;
	IDataStore dataStore;
	protected boolean abortOnOverflow;	
	protected Map bindings;

	Object query;	


	Map userProfileParameters;


	private static transient Logger logger = Logger.getLogger(ConfigurableDataSet.class);


	public ConfigurableDataSet(){
		super();
	}

	public ConfigurableDataSet(SpagoBiDataSet dataSetConfig){
		super(dataSetConfig);
	}
    
	public void loadData(int offset, int fetchSize, int maxResults) {

		dataProxy.setParameters(getParamsMap());
		dataProxy.setProfile(getUserProfileAttributes());
		dataProxy.setResPath(resPath);
		dataProxy.setPredefinedGroovyScriptFileName(groovyFileName);
		dataProxy.setPredefinedJsScriptFileName(jsFileName);
		// check if the proxy is able to manage results pagination
		if(dataProxy.isOffsetSupported()) {
			dataProxy.setOffset(offset);
		} else if(dataReader.isOffsetSupported()){
			dataReader.setOffset(offset);
		} else {
			
		}
		
		if(dataProxy.isFetchSizeSupported()) {
			dataProxy.setFetchSize(fetchSize);
		} else if(dataReader.isOffsetSupported()){
			dataReader.setFetchSize(fetchSize);
		} else {
			
		}
		
		// check if the proxy is able to manage results limit
		if(dataProxy.isMaxResultsSupported()) {
			dataProxy.setMaxResults(maxResults);
		} else if(dataReader.isOffsetSupported()){
			dataReader.setMaxResults(maxResults);
		} else {
			
		}


		if( hasBehaviour(QuerableBehaviour.class.getName()) ) { // Querable Behaviour
			QuerableBehaviour querableBehaviour = (QuerableBehaviour)getBehaviour(QuerableBehaviour.class.getName()) ;
			String stm = querableBehaviour.getStatement();
			stm = stm.replaceAll("''", "'");
			dataProxy.setStatement(stm);	
		} 
		
		dataStore = dataProxy.load(dataReader); 
		

		if(hasDataStoreTransformer()) {
			getDataStoreTransformer().transform(dataStore);
		}
	}

	public IDataStore getDataStore() {    	
		return this.dataStore;
	}    

	public Object getQuery() {
		return query;
	}

	public void setQuery(Object query) {
		this.query = query;
	}

	/**
	 * Gets the list of names of the profile attributes required.
	 * 
	 * @return list of profile attribute names
	 * 
	 * @throws Exception the exception
	 */
	public List getProfileAttributeNames() throws Exception {
		List names = new ArrayList();
		String query = (String)getQuery();
		while(query.indexOf("${")!=-1) {
			int startind = query.indexOf("${");
			int endind = query.indexOf("}", startind);
			String attributeDef = query.substring(startind + 2, endind);
			if(attributeDef.indexOf("(")!=-1) {
				int indroundBrack = query.indexOf("(", startind);
				String nameAttr = query.substring(startind+2, indroundBrack);
				names.add(nameAttr);
			} else {
				names.add(attributeDef);
			}
			query = query.substring(endind);
		}
		return names;
	}

	public IDataReader getDataReader() {
		return dataReader;
	}

	public void setDataReader(IDataReader dataReader) {
		this.dataReader = dataReader;
	}

	public IDataProxy getDataProxy() {
		return dataProxy;
	}

	public void setDataProxy(IDataProxy dataProxy) {
		this.dataProxy = dataProxy;
	}

	public Map getUserProfileAttributes() {
		return userProfileParameters;
	}

	public void setUserProfileAttributes(Map parameters) {
		this.userProfileParameters = parameters;
	}
	
	public void setAbortOnOverflow(boolean abortOnOverflow) {
		this.abortOnOverflow = abortOnOverflow;
	}
	
	public void addBinding(String bindingName, Object bindingValue) {
		bindings.put(bindingName, bindingValue);
	}

}
