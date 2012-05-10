/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.QuerableBehaviour;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.IDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ConfigurableDataSet extends  AbstractDataSet {

	IDataReader dataReader;
	IDataProxy dataProxy;
	IDataStore dataStore;
	
	protected boolean abortOnOverflow;	
	protected Map bindings;
	private boolean calculateResultNumberOnLoad = true;




	Map<String, Object> userProfileParameters;


	private static transient Logger logger = Logger.getLogger(ConfigurableDataSet.class);


	public ConfigurableDataSet(){
		super();
		userProfileParameters = new HashMap<String, Object>();
	}

	public ConfigurableDataSet(SpagoBiDataSet dataSetConfig){
		super(dataSetConfig);
		userProfileParameters = new HashMap<String, Object>();
	}

	public void loadData(int offset, int fetchSize, int maxResults) {

		dataProxy.setParameters(getParamsMap());
		dataProxy.setProfile(getUserProfileAttributes());
		dataProxy.setResPath(resPath);
		
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


		if( hasBehaviour(QuerableBehaviour.class.getName()) ) { 
			QuerableBehaviour querableBehaviour = (QuerableBehaviour)getBehaviour(QuerableBehaviour.class.getName()) ;
			String stm = querableBehaviour.getStatement();
			stm = stm.replaceAll("''", "'");
			dataProxy.setStatement(stm);
		}
		
		dataProxy.setCalculateResultNumberOnLoad(this.isCalculateResultNumberOnLoadEnabled());

		dataStore = dataProxy.load(dataReader); 


		if(hasDataStoreTransformer()) {
			getDataStoreTransformer().transform(dataStore);
		}
	}

	public IDataStore getDataStore() {    	
		return this.dataStore;
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

	public Map<String, Object> getUserProfileAttributes() {
		return userProfileParameters;
	}

	public void setUserProfileAttributes(Map<String, Object> parameters) {
		this.userProfileParameters = parameters;
	}

	public void setAbortOnOverflow(boolean abortOnOverflow) {
		this.abortOnOverflow = abortOnOverflow;
	}

	public void addBinding(String bindingName, Object bindingValue) {
		bindings.put(bindingName, bindingValue);
	}

	
	public IDataStore test() {
		logger.debug("IN");
		loadData();
		logger.debug("OUT");
		return getDataStore();
	}



	public IDataStore test(int offset, int fetchSize, int maxResults) {
		logger.debug("IN");
		loadData(offset, fetchSize, maxResults);
		logger.debug("OUT");
		return getDataStore();

	}

	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetTableDescriptor persist(String tableName,
			Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataStore getDomainValues(String fieldName, Integer start,
			Integer limit, IDataStoreFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataStore decode(
			IDataStore datastore) {
		return datastore;
	}

	public boolean isCalculateResultNumberOnLoadEnabled() {
		return calculateResultNumberOnLoad;
	}

	public void setCalculateResultNumberOnLoad(boolean enabled) {
		calculateResultNumberOnLoad = enabled;
	}

}
