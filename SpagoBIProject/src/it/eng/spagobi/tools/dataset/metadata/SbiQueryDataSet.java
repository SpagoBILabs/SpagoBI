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
package it.eng.spagobi.tools.dataset.metadata;

import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class SbiQueryDataSet extends SbiDataSetHistory{
    private String query=null;
    private String queryScript=null;
    private String queryScriptLanguage=null;
    private SbiDataSource dataSource=null;
    
    /**
     * Gets the query.
     * 
     * @return the query
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * Sets the query.
     * 
     * @param query the new query
     */
    public void setQuery(String query) {
        this.query = query;
    }
    
    
    /**
     * Get the script used to modify the query at runtime (optional)
     * 
     * @return the script used to modify the query at runtime (optional)
     */
    public String getQueryScript() {
		return queryScript;
	}

    /**
     * Set the script to use to modify the query at runtime
     * 
     * @param queryScript script to use to modify the query at runtime (optional)
     */
	public void setQueryScript(String queryScript) {
		this.queryScript = queryScript;
	}

	/**
     * Get the language of the script used to modify the query at runtime
     * 
     * @return the language of the script used to modify the query at runtime
     */
	public String getQueryScriptLanguage() {
		return queryScriptLanguage;
	}

	/**
     * Set the language of the script used to modify the query at runtime
     * 
     * @param queryScriptLanguage the language of the script used to modify the query at runtime
     */
	public void setQueryScriptLanguage(String queryScriptLanguage) {
		this.queryScriptLanguage = queryScriptLanguage;
	}

	/**
     * Gets the data source.
     * 
     * @return the data source
     */
    public SbiDataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Sets the data source.
     * 
     * @param dataSource the new data source
     */
    public void setDataSource(SbiDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
