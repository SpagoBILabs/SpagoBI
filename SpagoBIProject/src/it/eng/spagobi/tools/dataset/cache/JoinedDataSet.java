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
package it.eng.spagobi.tools.dataset.cache;

import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.bo.AbstractDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JoinedDataSet extends AbstractDataSet {

	UserProfile userProfile;
	IDataSetDAO dataSetDao;
	List<IDataSet> joinedDataSets;
	IDataStore joinedDataStore;
	JSONArray associations;

	private static transient Logger logger = Logger.getLogger(JoinedDataSet.class);
	 	
	public JoinedDataSet( SpagoBiDataSet dataSetConfig ) {
	    super(dataSetConfig);
	}
	
	public JoinedDataSet(String label, String name, String description, String configuration) {
		setName(name);
    	setLabel(label);
    	setDescription(description);
		setConfiguration(configuration);
	}
	
	public JoinedDataSet(String label, String name, String description, JSONObject configuration) {
		setName(name);
    	setLabel(label);
    	setDescription(description);
		setConfiguration(configuration);
	}
	
	public JoinedDataSet(String label, String name, String description, List<IDataSet> joinedDataSets, JSONArray associations) {
		setName(name);
    	setLabel(label);
    	setDescription(description);
		setDataSets(joinedDataSets);
		setAssociations(associations);
	}
	
	
	public void setConfiguration(String configuration) {
		 logger.trace("IN");
		 
		 try {
			 JSONObject jsonConf = ObjectUtils.toJSONObject( this.configuration );
			 setConfiguration(jsonConf);
		 } catch(Throwable t) {
			 throw new RuntimeException("An unexpected error occured while setting configuration", t);
		 } finally {
			 logger.trace("OUT");
		 }
	}
	
	public void setConfiguration(JSONObject configuration) {
		logger.trace("IN");
		 
		 try {
			 JSONArray datasets = configuration.getJSONArray("datasets");
			 List<String> dataSetLabels = new ArrayList<String>() ;
			 for(int i = 0; i < datasets.length(); i++) {
				 String dataSetLabel = datasets.getString(i);
				 dataSetLabels.add(dataSetLabel);
			 }
			 setDataSetLabels(dataSetLabels);
			 
			 JSONArray associations = configuration.getJSONArray("associations"); 
			 setAssociations(associations);
			 
			 this.configuration = configuration.toString();
		 } catch(Throwable t) {
			 throw new RuntimeException("An unexpected error occured while setting configuration", t);
		 } finally {
			 logger.trace("OUT");
		 }
	}
	 
	public List<IDataSet> getDataSets() {
		return this.joinedDataSets;
	}
	
	private void setDataSetLabels(List<String> dataSetLabels) {
		List<IDataSet> datsets = new ArrayList<IDataSet>();
		for(String dataSetLabel : dataSetLabels) {
			IDataSet dataSet = this.getDataSetDAO().loadDataSetByLabel(dataSetLabel);
			datsets.add(dataSet);
		}
	}
	
	private void setDataSets(List<IDataSet> joinedDataSets) {
		this.joinedDataSets = joinedDataSets;
	}
	 
	private IDataSetDAO getDataSetDAO() {
		if(dataSetDao == null) {
			try {
				dataSetDao = DAOFactory.getDataSetDAO();
				if(getUserProfile() != null) {
					dataSetDao.setUserProfile(userProfile);
				}
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An unexpected error occured while instatiating the DAO", t);
			} 
		}
		return dataSetDao;
	}
	
	public JSONArray getAssociations() {
		return associations;
	}

	private void setAssociations(JSONArray associations) {
		this.associations = associations;
	}
	 
	 
	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#getUserProfileAttributes()
	 */
	public Map getUserProfileAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#setUserProfileAttributes(java.util.Map)
	 */
	public void setUserProfileAttributes(Map<String, Object> attributes) {
		// TODO Auto-generated method stub
		
	}
	
	public void loadData(int offset, int fetchSize, int maxResults) {
		ICache cache = CacheManager.getCache();
		
		// check if all joinedDataset have been succesfully stored in cache
		List<IDataSet> dataSetNotStoredInCache = new ArrayList();
		for(IDataSet dataSet: joinedDataSets) {
			if(!cache.contains(dataSet)) {
				dataSetNotStoredInCache.add(dataSet);
			}
		}
		
		// qunado tutti i work sono finiti creo la tabella di join
		if(dataSetNotStoredInCache.size() == 0) {
			joinedDataStore = cache.refresh(joinedDataSets, associations);
		} else {
			String dataSetLabels = "";
			for(IDataSet dataSet: dataSetNotStoredInCache) {
				dataSetLabels += dataSet.getLabel() + ";";
			}
			throw new RuntimeException("Impossible to load data for joined store [" + this.getName() + "] " +
					"because the datasets [" + dataSetLabels + "] haven't been properly loaded before");
		}
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#getDataStore()
	 */
	public IDataStore getDataStore() {
		return joinedDataStore;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#setAbortOnOverflow(boolean)
	 */
	public void setAbortOnOverflow(boolean abortOnOverflow) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#addBinding(java.lang.String, java.lang.Object)
	 */
	public void addBinding(String bindingName, Object bindingValue) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#test()
	 */
	public IDataStore test() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#test(int, int, int)
	 */
	public IDataStore test(int offset, int fetchSize, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#getSignature()
	 */
	public String getSignature() {
		String signature = "";
		for(IDataSet dataSet : this.joinedDataSets)	{
			signature += dataSet.getSignature();
		}
		return signature;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#decode(it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	public IDataStore decode(IDataStore datastore) {
		throw new UnsupportedOperationException("Dataset implementation class [" + this.getClass().getName() + "] does not support method [decode]");
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#isCalculateResultNumberOnLoadEnabled()
	 */
	public boolean isCalculateResultNumberOnLoadEnabled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#setCalculateResultNumberOnLoad(boolean)
	 */
	public void setCalculateResultNumberOnLoad(boolean enabled) {
		logger.warn("In [" + this.getClass().getName() + "] calculate result number on loadis always false");
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#setDataSource(it.eng.spagobi.tools.datasource.bo.IDataSource)
	 */
	public void setDataSource(IDataSource dataSource) {
		logger.warn("In [" + this.getClass().getName() + "] datasource is not used");
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.bo.IDataSet#getDataSource()
	 */
	public IDataSource getDataSource() {
		return null;
	}
	
	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
}
