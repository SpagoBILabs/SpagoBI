/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.metadata;

import java.util.Date;

import it.eng.spagobi.commons.metadata.SbiDomains;

/**
 * This is the class used by the DAO to map the table 
 * <code>sbi_meta_data_history</code>. Given the current implementation
 * of the DAO this is the class used by Hibernate to map the table
 * <code>sbi_meta_data_history</code>. The following snippet of code, for example, shows
 * how the <code>DataSetDAOImpl</code> load a dataset version whose id is equal to datasetVersionId...
 * 
 * <code>session.load(SbiDataSetHistory.class, datasetVersionId);</code>
 * 
 * @authors
 * 		Chiara Chiarelli
 * 		Andrea Gioia (andrea.gioia@eng.it)
 */
public class SbiDataSetHistory {
	private int dsHId;	
	private boolean active = true;
	private SbiDataSetConfig sbiDsConfig =null;
	private int versionNum;

	private SbiDomains category  = null;
	private String parameters=null;
	private String dsMetadata=null;
	
	private SbiDomains transformer = null;
	private String pivotColumnName=null;
	private String pivotRowName=null;
	private String pivotColumnValue=null;
	private boolean numRows = false;
	
	private String userIn=null;
	private String sbiVersionIn=null;
	private String metaVersion=null;
	private Date timeIn = null;
	private String organization=null;

	public int getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getDsHId() {
		return dsHId;
	}

	public void setDsHId(int dsHId) {
		this.dsHId = dsHId;
	}

	public SbiDataSetConfig getSbiDsConfig() {
		return sbiDsConfig;
	}

	public void setSbiDsConfig(SbiDataSetConfig sbiDsConfig) {
		this.sbiDsConfig = sbiDsConfig;
	}

	public String getUserIn() {
		return userIn;
	}

	public void setUserIn(String userIn) {
		this.userIn = userIn;
	}

	public String getSbiVersionIn() {
		return sbiVersionIn;
	}

	public void setSbiVersionIn(String sbiVersionIn) {
		this.sbiVersionIn = sbiVersionIn;
	}

	public String getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(String metaVersion) {
		this.metaVersion = metaVersion;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}

	/**
	 * @return the numRows
	 */
	public boolean isNumRows() {
		return numRows;
	}

	/**
	 * @param numRows the numRows to set
	 */
	public void setNumRows(boolean numRows) {
		this.numRows = numRows;
	}

	/**
	 * Gets the parameters.
	 * 
	 * @return the parameters
	 */
	public String getParameters() {
	    return parameters;
	}
	
	/**
	 * Sets the parameters.
	 * 
	 * @param parameters the new parameters
	 */
	public void setParameters(String parameters) {
	    this.parameters = parameters;
	}
	
	/**
	 * Gets the pivot column name.
	 * 
	 * @return the pivot column name
	 */
	public String getPivotColumnName() {
		return pivotColumnName;
	}
	
	/**
	 * Sets the pivot column name
	 * 
	 * @param pivotColumnName the new pivot column name
	 */
	public void setPivotColumnName(String pivotColumnName) {
		this.pivotColumnName = pivotColumnName;
	}

	/**
	 * Gets the pivot column value.
	 * 
	 * @return the pivot column value
	 */
	public String getPivotColumnValue() {
		return pivotColumnValue;
	}

	/**
	 * Sets the pivot column value
	 * 
	 * @param pivotColumnValue the new pivot column value
	 */
	public void setPivotColumnValue(String pivotColumnValue) {
		this.pivotColumnValue = pivotColumnValue;
	}

	public String getPivotRowName() {
		return pivotRowName;
	}

	public void setPivotRowName(String pivotRowName) {
		this.pivotRowName = pivotRowName;
	}

	public SbiDomains getCategory() {
		return category;
	}

	public void setCategory(SbiDomains category) {
		this.category = category;
	}

	/**
	 * Gets the transformer.
	 * 
	 * @return the transformer
	 */
	public SbiDomains getTransformer() {
        return this.transformer;
    }
    
    /**
     * Sets the transformer.
     * 
     * @param transformer the new transformer
     */
    public void setTransformer(SbiDomains transformer) {
        this.transformer = transformer;
    }

    /**
     * Gets the metadata.
     * 
     * @return metadata
     */
	public String getDsMetadata() {
		return dsMetadata;
	}

    /**
     *  the metadata.
     * 
     * @param transformer the new metadata
     */
	public void setDsMetadata(String dsMetadata) {
		this.dsMetadata = dsMetadata;
	}
    	
	
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}   
    
}
