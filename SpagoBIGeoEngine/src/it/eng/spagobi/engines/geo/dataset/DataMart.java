/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
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