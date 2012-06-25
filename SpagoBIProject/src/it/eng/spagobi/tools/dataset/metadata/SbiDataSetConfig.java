/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.metadata;

import it.eng.spagobi.commons.metadata.SbiHibernateModel;

/**
 * This is the class used by the DAO to map the table 
 * <code>sbi_meta_data</code>. Given the current implementation
 * of the DAO this is the class used by Hibernate to map the table
 * <code>sbi_meta_data</code>. The following snippet of code, for example, shows
 * how the <code>DataSetDAOImpl</code> load a dataset whose id is equal to datasetId...
 * 
 * <code>hibernateSession.load(SbiDataSetConfig.class, datasetId);</code>
 * 
 * @authors
 * 		Angelo Bernabei (angelo.bernabei@eng.it)
 * 		Andrea Gioia (andrea.gioia@eng.it)
 */
public class SbiDataSetConfig extends SbiHibernateModel {
	
	/**
	 * default version UID
	 */
	private static final long serialVersionUID = 1L;
	
	private int dsId;	
	private String name=null;
	private String description=null;
	private String label=null;
    	
	
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
}
