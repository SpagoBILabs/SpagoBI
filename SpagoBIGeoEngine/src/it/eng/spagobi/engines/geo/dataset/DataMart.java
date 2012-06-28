/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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