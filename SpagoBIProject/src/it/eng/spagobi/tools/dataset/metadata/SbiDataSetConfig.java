/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.tools.dataset.metadata;

import it.eng.spagobi.commons.metadata.SbiDomains;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class SbiDataSetConfig {
	private int dsId;	
	private SbiDomains transformer = null;
	private String name=null;
	private String description=null;
	private String label=null;
	private String parameters=null;
	private String dsMetadata=null;
	private String pivotColumnName=null;
	private String pivotRowName=null;
	private String pivotColumnValue=null;
	private boolean numRows = false;
    	
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
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
	    return name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
	    this.name = name;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
	    return description;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
	    this.description = description;
	}
	
	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
	    return label;
	}
	
	/**
	 * Sets the label.
	 * 
	 * @param label the new label
	 */
	public void setLabel(String label) {
	    this.label = label;
	}
	
	/**
	 * Gets the ds id.
	 * 
	 * @return the ds id
	 */
	public int getDsId() {
		return dsId;
	}

	/**
	 * Sets the ds id.
	 * 
	 * @param dsId the new ds id
	 */
	public void setDsId(int dsId) {
		this.dsId = dsId;
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
    	
    
    
}
