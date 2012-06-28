/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
