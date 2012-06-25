/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

/**
 * @author Chiara Chiarelli
 */
public class QueryDataSetDetail extends GuiDataSetDetail{
	
    private String query=null;
    private String queryScript=null;
    private String queryScriptLanguage=null;
    private String dataSourceLabel=null;
    
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

	public String getQueryScript() {
		return queryScript;
	}

	public void setQueryScript(String queryScript) {
		this.queryScript = queryScript;
	}

	

	public String getQueryScriptLanguage() {
		return queryScriptLanguage;
	}

	public void setQueryScriptLanguage(String queryScriptLanguage) {
		this.queryScriptLanguage = queryScriptLanguage;
	}

	public String getDataSourceLabel() {
		return dataSourceLabel;
	}

	public void setDataSourceLabel(String dataSourceLabel) {
		this.dataSourceLabel = dataSourceLabel;
	}
    
  
}
