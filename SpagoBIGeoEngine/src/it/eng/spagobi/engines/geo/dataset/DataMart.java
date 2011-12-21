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
package it.eng.spagobi.engines.geo.dataset;



import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;

import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class DataSet.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DataMart {
    
	IDataStore dataStore;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** The meta data. */
	private DataSetMetaData metaData;
	
	/**
	 * Gets the meta data.
	 * 
	 * @return the meta data
	 */
	public DataSetMetaData getMetaData() {
		return metaData;
	}
	
	
		
	
	
	/** The target feature name. */
	private String targetFeatureName;
	
	
	/**
	 * Constructor.
	 */
    public DataMart() {
        super();
    }

       
    
    
	/**
	 * Gets the target feature name.
	 * 
	 * @return the target feature name
	 */
	public String getTargetFeatureName() {
		return targetFeatureName;
	}

	/**
	 * Sets the target feature name.
	 * 
	 * @param targetFeatureName the new target feature name
	 */
	public void setTargetFeatureName(String targetFeatureName) {
		this.targetFeatureName = targetFeatureName;
	}

	
	
	public IDataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(IDataStore dataStore) {
		this.dataStore = dataStore;
	}

	
    
}